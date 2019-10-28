package bunch.api;

import bunch.*;

import static bunch.api.Algorithm.*;
import static bunch.api.Key.*;
import static bunch.api.OutputFormat.*;
import static bunch.api.RunMode.*;

import bunch.calculator.ObjectiveFunctionCalculator;
import bunch.clustering.ClusteringMethod;
import bunch.clustering.NAHCConfiguration;
import bunch.ga.GAConfiguration;
import bunch.model.*;
import bunch.parser.Parser;
import bunch.simple.SATechnique;
import bunch.stats.*;

import java.io.IOException;
import java.util.*;
import java.beans.*;

final class BunchEngine {

  EngineArgs bunchArgs = new EngineArgs();
  EngineResults results = new EngineResults();
  ClusteringMethod clusteringMethod_d;
  GraphOutput graphOutput_d;
  Graph initialGraph_d;
  BunchPreferences preferences_d;
  StatsManager stats = bunch.stats.StatsManager.getInstance();
  Configuration configuration_d;
  ProgressCallbackInterface cbInterfaceObj;
  int callbackFrequency;
  long startTime;
  long endTime;
  long totalTime=0;
  Cluster baseCluster;
  ArrayList clusterList;
  javax.swing.Timer timeoutTimer;
  int reflexiveEdgeCount = 0;

  String precision;
  String recall;
  String MQCalcMdgFileName;
  String MQCalcSilFileName;
  String MQCalcValue;

  BunchEngine() {}

  String getFileDelims() {
    String delims = "";
    String def_delims = bunchArgs.MDG_PARSER_DELIMS;
    if(def_delims != null)
      delims += def_delims;

    if (bunchArgs.MDG_PARSER_USE_SPACES)
      delims = " " + delims;  //includes the space character
    if (bunchArgs.MDG_PARSER_USE_TABS)
      delims = "\t" + delims; //includes the tab character

    return delims;
  }

/**
 * This method sets the libraries, clients and suppliers defined in their
 * respective panes to the graph, just previous to processing.
 */
public void arrangeLibrariesClientsAndSuppliers(Graph g, Map special) {
  Object []suppliers = null; //new Object[0]; //null;
  Object []clients = null; //new Object[0]; //null;
  Object []centrals = null; //new Object[0]; //null;
  Object []libraries = null; //new Object[0]; //null;

  if(special.get(OMNIPRESENT_CENTRAL)!= null)
    centrals = ((Collection)special.get(OMNIPRESENT_CENTRAL)).toArray();
  if(special.get(OMNIPRESENT_CLIENT)!=null)
    clients = ((Collection)special.get(OMNIPRESENT_CLIENT)).toArray();
  if(special.get(OMNIPRESENT_SUPPLIER)!=null)
    suppliers = ((Collection)special.get(OMNIPRESENT_SUPPLIER)).toArray();
  if(special.get(LIBRARY_MODULE)!=null)
    libraries = ((Collection)special.get(LIBRARY_MODULE)).toArray();

  Node[] nodeList = g.getNodes();
  Node[] originalList = nodeList;

  //tag the nodes with their type (matching them by name from the lists)
  for (int j=0; j<originalList.length; ++j) {
    if(suppliers != null) {
      for (int i=0; i<suppliers.length; ++i) {
        String name = originalList[j].getName();
        if (name.equals((String)suppliers[i])) {
          originalList[j].setType(Node.SUPPLIER);
          break;
        }
      }
    }
    if(clients != null) {
      for (int i=0; i<clients.length; ++i) {
        String name = originalList[j].getName();
        if (name.equals((String)clients[i])) {
          originalList[j].setType(Node.CLIENT);
          break;
        }
      }
    }
    if(centrals != null) {
      for (int i=0; i<centrals.length; ++i) {
        String name = originalList[j].getName();
        if (name.equals((String)centrals[i])) {
          originalList[j].setType(Node.CENTRAL);
          break;
        }
      }
    }

    if(libraries != null) {
      for (int i=0; i<libraries.length; ++i) {
        String name = originalList[j].getName();
        if (name.equals((String)libraries[i])) {
          originalList[j].setType(Node.LIBRARY);
          break;
        }
      }
    }
  }

  int deadNodes = 0;
  //now consolidate nodes that only point to omnipresent, libs, and suppliers
  for (int i=0; i<originalList.length; ++i) {
    if (originalList[i].getType() == Node.NORMAL) {
      boolean noNormalDeps = true;
      int []tmpDeps = originalList[i].getDependencies();
      int []tmpBeDeps = originalList[i].getBackEdges();
      int client = 0;
      int supplier = 0;
      int central = 0;
      int library = 0;
      for(int j = 0; j < tmpDeps.length; j++) {
        if ((originalList[tmpDeps[j]].getType() == Node.NORMAL) ||
            (originalList[tmpDeps[j]].getType() >= Node.DEAD)) {
          noNormalDeps = false;
          break;
        } else {
          switch(originalList[tmpDeps[j]].getType()) {
            case Node.CLIENT:
              client++; break;
            case Node.SUPPLIER:
              supplier++; break;
            case Node.CENTRAL:
              central++; break;
            case Node.LIBRARY:
              library++;  break;
          }
        }
      }
      for(int j = 0; j < tmpBeDeps.length; j++) {
        if ((originalList[tmpBeDeps[j]].getType() == Node.NORMAL) ||
            (originalList[tmpBeDeps[j]].getType() >= Node.DEAD))
        {
          noNormalDeps = false;
          break;
        } else {
          switch(originalList[tmpBeDeps[j]].getType()) {
            case Node.CLIENT:
              client++; break;
            case Node.SUPPLIER:
              supplier++; break;
            case Node.CENTRAL:
              central++; break;
            case Node.LIBRARY:
              library++;  break;
          }
        }
      }
      if (noNormalDeps == true) {
        deadNodes++;
        int n1 = Math.max(client,supplier);
        int n2 = Math.max(central,library);
        int max = Math.max(n1,n2);
        int type = Node.CLIENT;

        if(max == client)   type = Node.CLIENT;
        if(max == supplier) type = Node.SUPPLIER;
        if(max == central)  type = Node.CENTRAL;
        if(max == library)  type = Node.LIBRARY;
        originalList[i].setType(Node.DEAD+max);
      }
    }
  }

  //now we have all the special modules tagged
  nodeList = new Node[originalList.length -
                (clients.length+suppliers.length+ deadNodes+
                +centrals.length+libraries.length)];
  int j=0;

  Hashtable normal = new Hashtable();
  //build new node list without omnipresent modules
  for (int i=0; i<originalList.length; ++i) {
    if (originalList[i].getType() == Node.NORMAL) {
      normal.put(new Integer(originalList[i].getId()),new Integer(j));
      nodeList[j++] = originalList[i].cloneNode();
    }
  }

  for (int i = 0; i < nodeList.length; ++i) {
    nodeList[i].nodeID = i;
    int[] deps = nodeList[i].getDependencies();
    int[] beDeps = nodeList[i].getBackEdges();
    int[] weight = nodeList[i].getWeights();
    int[] beWeight = nodeList[i].getBeWeights();
    int depsRemoveCount = 0;
    int beDeptsRemoveCount = 0;

    Integer tmpAssoc;
    for(int z = 0; z < deps.length; z++) {
      tmpAssoc = (Integer)normal.get(new Integer(deps[z]));
      if (tmpAssoc == null) {
        deps[z] = -1;
        depsRemoveCount++;
      } else {
        deps[z] = tmpAssoc.intValue();
      }
    }

    for(int z = 0; z < beDeps.length; z++) {
      tmpAssoc = (Integer)normal.get(new Integer(beDeps[z]));
      if (tmpAssoc == null) {
        beDeps[z] = -1;
        beDeptsRemoveCount++;
      } else {
        beDeps[z] = tmpAssoc.intValue();
      }
    }

    if(depsRemoveCount  > 0) {
      int []newDeps = new int[deps.length-depsRemoveCount];
      int []newWeight = new int[deps.length-depsRemoveCount];

      int pos = 0;
      for (int z = 0; z < deps.length; z++)
        if(deps[z] != -1) {
          newDeps[pos] = deps[z];
          newWeight[pos] = weight[z];
          pos++;
        }
        deps = newDeps;
        weight = newWeight;
    }

    if(beDeptsRemoveCount  > 0) {
      int []newBeDeps = new int[beDeps.length-beDeptsRemoveCount];
      int []newBeWeight = new int[beDeps.length-beDeptsRemoveCount];

      int pos = 0;
      for (int z = 0; z < beDeps.length; z++)
        if(beDeps[z] != -1) {
          newBeDeps[pos] = beDeps[z];
          newBeWeight[pos] = beWeight[z];
          pos++;
        }
        beDeps = newBeDeps;
        beWeight = newBeWeight;
    }

    nodeList[i].setDependencies(deps);
    nodeList[i].setWeights(weight);
    nodeList[i].setBackEdges(beDeps);
    nodeList[i].setBeWeights(beWeight);
  }

  //reinitialize the graph with the new nodes
  g.initGraph(nodeList.length);
  g.clear();
  g.setNodes(nodeList);
  g.setOriginalNodes(originalList);
}

  public Map getDefaultSpecialNodes(String graphName)
  { return getDefaultSpecialNodes(graphName, 3.0);  }

  public Map getDefaultSpecialNodes(String graphName, double threshold) {
    try {
      Hashtable h = new Hashtable();
      Hashtable centrals = new Hashtable();
      Hashtable clients = new Hashtable();
      Hashtable suppliers = new Hashtable();
      Hashtable libraries = new Hashtable();

      //=====================================================
      //Construct the graph
      //=====================================================
      BunchPreferences prefs =
        (BunchPreferences)(Beans.instantiate(null, "bunch.BunchPreferences"));

      String parserClass = "dependency";
      if(graphName.endsWith(".gxl") || graphName.endsWith(".GXL"))
        parserClass = "gxl";

      Parser p = preferences_d.getParserFactory().getParser(parserClass);
      p.setInput(graphName);
      Graph g = (Graph)p.parse();

      Node[] nodeList = g.getNodes();

      //find libraries
      for (int i=0; i<nodeList.length; ++i) {
        String nname = nodeList[i].getName();
        if ((nodeList[i].getDependencies() == null|| nodeList[i].getDependencies().length==0)
              && !clients.containsKey(nodeList[i].getName())
              && !suppliers.containsKey(nodeList[i].getName())
              && !centrals.containsKey(nodeList[i].getName())) {
          libraries.put(nodeList[i].getName(), nodeList[i].getName());
        }
      }

      //find clients
      double avg = 0.0, sum = 0.0;
      for (int i=0; i<nodeList.length; ++i) {
        if (nodeList[i].getDependencies() != null) {
          sum += nodeList[i].getDependencies().length;
        }
      }
      avg = sum/nodeList.length;
      avg = avg * threshold;
      for (int i=0; i<nodeList.length; ++i) {
        if (nodeList[i].getDependencies() != null
            && nodeList[i].getDependencies().length > avg
            && !libraries.containsKey(nodeList[i].getName())) {
          clients.put(nodeList[i].getName(),nodeList[i].getName());
        }
      }

      //find suppliers
      avg = 0.0; sum = 0.0;
      int[] inNum = new int[nodeList.length];

      for (int j=0; j<nodeList.length; ++j) {
        int currval = 0;
        for (int i=0; i<nodeList.length; ++i) {
          int[] deps = nodeList[i].getDependencies();
          if (deps != null) {
            for (int n=0; n<deps.length; ++n) {
              if (deps[n] == j) {
                currval++;
              }
            }
          }
        }
        inNum[j] = currval;
      }
      for (int i=0; i<inNum.length; ++i) {
        sum += inNum[i];
      }
      avg = sum/nodeList.length;
      avg = avg * threshold;
      for (int i=0; i<nodeList.length; ++i) {
        if (inNum[i] > avg
            && !libraries.containsKey(nodeList[i].getName())) {
          suppliers.put(nodeList[i].getName(),nodeList[i].getName());
        }
      }

      //looking for central nodes (nodes that are clients and suppliers
      ArrayList clientsAL = new ArrayList(clients.values());

      for (int i=0; i<clientsAL.size(); ++i) {
        String client = (String) clientsAL.get(i);
        if(suppliers.containsKey(client))
          centrals.put(client,client);
      }

      Enumeration e = centrals.elements();
      while(e.hasMoreElements()) {
        String elem = (String)e.nextElement();
        clients.remove(elem);
        suppliers.remove(elem);
      }

      //=====================================================
      //return the hashtable
      //=====================================================
      h.put(OMNIPRESENT_CENTRAL,centrals.values());
      h.put(OMNIPRESENT_CLIENT,clients.values());
      h.put(OMNIPRESENT_SUPPLIER,suppliers.values());
      h.put(LIBRARY_MODULE,libraries.values());
      return h;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  Hashtable getSpecialModulesFromProperties() {
    Hashtable h = new Hashtable();
    ArrayList emptyList = new ArrayList();
    boolean   containsSpecial = false;

    emptyList.clear();

    if(bunchArgs.omnipresentBoth != null) {
      containsSpecial = true;
      h.put(OMNIPRESENT_CENTRAL,bunchArgs.omnipresentBoth);
    } else
      h.put(OMNIPRESENT_CENTRAL,emptyList);

    if(bunchArgs.omnipresentClients != null) {
      containsSpecial = true;
      h.put(OMNIPRESENT_CLIENT,bunchArgs.omnipresentClients);
    } else
      h.put(OMNIPRESENT_CLIENT,emptyList);

    if(bunchArgs.omnipresentSuppliers != null) {
      containsSpecial = true;
      h.put(OMNIPRESENT_SUPPLIER,bunchArgs.omnipresentSuppliers);
    }
    else
      h.put(OMNIPRESENT_SUPPLIER,emptyList);

    if(bunchArgs.libraryList != null) {
      containsSpecial = true;
      h.put(LIBRARY_MODULE,bunchArgs.libraryList);
    } else
      h.put(LIBRARY_MODULE,emptyList);

    if(containsSpecial == true)
      return h;
    else
      return null;
  }

  void initClustering() throws IOException, ClassNotFoundException {
    clusterList = new ArrayList();
    loadPreferences();
    constructGraph();
    handleUserDirectedClustering();
    seeIfThereAreSpecialModules();
    loadClusteringMethodHandler();
    setIsClusterTree();
    setUpCalculator();
    setupClusteringMethod();
    stats.getInstance();
    initCallback();
    initTimer();
    setGraphOutputDriver();
  }

  private void initTimer() {
    //see if there is a timeout requested
    Integer toTime = bunchArgs.TIMEOUT_TIME;
    if(toTime != null)
      timeoutTimer = new javax.swing.Timer(toTime.intValue(),new TimeoutTimer());
  }

  private void initCallback() {
    //see if a callback class is setup, if so save a reference to the class
    cbInterfaceObj = bunchArgs.CALLBACK_OBJECT_REF;
    Integer iTmp = bunchArgs.callbackObjectFrequency;
    if(iTmp != null)
      callbackFrequency = iTmp.intValue();
  }

  private void setIsClusterTree() {
    //now set if we are clustering trees or one level
    initialGraph_d.setIsClusterTree(bunchArgs.AGGLOMERATIVE);
  }

  private void loadClusteringMethodHandler() throws IOException, ClassNotFoundException {
    //Load Clusteirng Method Handler
    var clustAlg = bunchArgs.CLUSTERING_ALG;
    if(clustAlg==null) throw new IllegalArgumentException();
    clusteringMethod_d = preferences_d.getClusteringMethodFactory().getMethod(clustAlg);
    if(clusteringMethod_d == null) throw new IllegalArgumentException();

    configuration_d = clusteringMethod_d.getConfiguration();
    if (initialGraph_d!=null&&configuration_d!=null)
      configuration_d.init(initialGraph_d);

    if (clustAlg == GA)            { loadGaConfig(); }
    if (clustAlg == SAHC)          { loadSahcConfig(); }
    if (clustAlg == HILL_CLIMBING) { loadHillClimbingConfig(); }
    if (clustAlg == NAHC)          { loadNahcConfig(); }
  }

  private void loadNahcConfig() throws IOException, ClassNotFoundException {
    Integer HCPct = bunchArgs.algNahcHcPct;
    Integer rndPct = bunchArgs.algNahcRndPct;
    Integer popSz = bunchArgs.algNahcPopulationSz;

    NAHCConfiguration c = (NAHCConfiguration)configuration_d;

    if(popSz != null)
      c.setPopulationSize(popSz.intValue());

    if(HCPct != null) {
      c.setMinPctToConsider(HCPct.intValue());

      if(rndPct != null)
        c.setRandomizePct(rndPct.intValue());
      else {
        int pctTmp = 100-HCPct.intValue();
        c.setRandomizePct(pctTmp);
      }
    }

    String SAClass = bunchArgs.algNahcSaClass;
    if (SAClass != null) {
      SATechnique saHandler = (SATechnique) Beans.instantiate(null,SAClass);
      if (saHandler != null) {
        Map saHandlerArgs = bunchArgs.algNahcSaConfig;
        if(saHandlerArgs != null) {
          saHandler.setConfig(saHandlerArgs);
        }
        c.setSATechnique(saHandler);
      }
    }
  }

  private void loadHillClimbingConfig() {
    NAHCConfiguration c = (NAHCConfiguration)configuration_d;
    if(bunchArgs.algHcRndPct != null) {
      Integer randomize = bunchArgs.algHcRndPct;
      c.setRandomizePct(randomize.intValue());
    }

    if(bunchArgs.algHcHcPct != null) {
      Integer hcThreshold = bunchArgs.algHcHcPct;
      c.setMinPctToConsider(hcThreshold.intValue());
    }
  }

  private void loadSahcConfig() {
    Integer popSz = bunchArgs.ALG_SAHC_POPULATION_SZ;

    if(popSz != null)
      configuration_d.setPopulationSize(popSz.intValue());
  }

  private void loadGaConfig() {
    GAConfiguration gaConfig = (GAConfiguration)configuration_d;
    gaConfig.setMethod(bunchArgs.ALG_GA_SELECTION_METHOD);
    gaConfig.setNumOfIterations(bunchArgs.ALG_GA_NUM_GENERATIONS);
    gaConfig.setCrossoverThreshold(bunchArgs.ALG_GA_CROSSOVER_PROB);
    gaConfig.setMutationThreshold(bunchArgs.ALG_GA_MUTATION_PROB);
    gaConfig.setPopulationSize(bunchArgs.ALG_GA_POPULATION_SZ);
  }

  private void seeIfThereAreSpecialModules() {
    //See if there are special modules
    if(bunchArgs.SPECIAL_MODULE_HASHTABLE != null) {
      var special = bunchArgs.SPECIAL_MODULE_HASHTABLE;
      arrangeLibrariesClientsAndSuppliers(initialGraph_d,special);
    }

    Hashtable specialFromInput = getSpecialModulesFromProperties();
    if (specialFromInput != null)
      arrangeLibrariesClientsAndSuppliers(initialGraph_d,specialFromInput);
  }

  private void setGraphOutputDriver() {
    //now set the graph output driver
    graphOutput_d = null;
    var outputMode = bunchArgs.OUTPUT_FORMAT;
    if (outputMode != null || !(outputMode==NULL)) {
      var driver = outputMode;

      if (driver != null) {
        String outFileName = bunchArgs.OUTPUT_FILE;
        if (outFileName == null)
          outFileName = bunchArgs.MDG_INPUT_FILE_NAME;

        graphOutput_d = preferences_d.getGraphOutputFactory().getOutput(driver);

        if (bunchArgs.OUTPUT_TREE) {
            graphOutput_d.setNestedLevels(true);
        }

        graphOutput_d.setBaseName(outFileName); //(String)bunchArgs.get(BunchProperties.MDG_INPUT_FILE_NAME));
        graphOutput_d.setBasicName(outFileName); //(String)bunchArgs.get(BunchProperties.MDG_INPUT_FILE_NAME));
        String outputFileName = graphOutput_d.getBaseName();
        String outputPath = bunchArgs.OUTPUT_DIRECTORY;
        if(outputPath != null) {
          java.io.File f = new java.io.File(graphOutput_d.getBaseName());
          String filename = f.getName();
          outputFileName = outputPath+filename;
        }
        graphOutput_d.setCurrentName(outputFileName);
        //System.out.println("Current name is " + outputFileName);
      }
    }
  }

  private void setupClusteringMethod() {
    //now setup the clustering method object
    clusteringMethod_d.initialize();
    clusteringMethod_d.setGraph(initialGraph_d.cloneGraph());
  }

  private void setUpCalculator() {
    //now setup the calculator
    ObjectiveFunctionCalculator objFnCalc = bunchArgs.mqCalculatorClass;
    (preferences_d.getObjectiveFunctionCalculatorFactory()).setCurrentCalculator(objFnCalc);
    Graph.setObjectiveFunctionCalculatorFactory(preferences_d.getObjectiveFunctionCalculatorFactory());
    initialGraph_d.setObjectiveFunctionCalculator(objFnCalc);
  }

  private void handleUserDirectedClustering() {
    //NOW HANDLE USER DIRECTED CLUSTERING, IF SET AND THE LOCKS
    String userSILFile = bunchArgs.userDirectedClusterSil;
    if(userSILFile != null) {
      boolean lock = bunchArgs.lockUserSetClusters;

      Parser cp = preferences_d.getParserFactory().getParser("cluster");
      cp.setInput(userSILFile);
      cp.setObject(initialGraph_d);
      cp.parse();
      if(lock==true)
        initialGraph_d.setDoubleLocks(true);

      //=================================
      //Now lock the clusters
      //=================================
      int[] clust = initialGraph_d.getClusters();
      boolean[] locks = initialGraph_d.getLocks();
      for (int i=0; i<clust.length; ++i) {
        if (clust[i] != -1) {
          locks[i] = true;
        }
      }
    }
  }

  private void constructGraph() {
    //Construct Graph
    if(bunchArgs.mdgInputFileName != null) {
      Parser p = preferences_d.getParserFactory().getParser("dependency");
      p.setInput(bunchArgs.mdgInputFileName);
      p.setDelims(getFileDelims());
      initialGraph_d = (Graph)p.parse();
      reflexiveEdgeCount = p.getReflexiveEdges();
    }

    if (bunchArgs.mdgGraphObject != null) {
      BunchMDG mdgObj = bunchArgs.mdgGraphObject;

      initialGraph_d = bunch.util.BunchUtilities.toInternalGraph(mdgObj);
      reflexiveEdgeCount = 0;
    }
  }

  private void loadPreferences() throws IOException, ClassNotFoundException {
    //Load Preferences
    preferences_d = (BunchPreferences)(Beans.instantiate(null, "bunch.BunchPreferences"));
  }

  public Graph getBestGraph() {
    if (clusteringMethod_d == null)
      return null;

    return clusteringMethod_d.getBestGraph().cloneGraph();
  }

  boolean runClustering() throws IOException, ClassNotFoundException {
    initClustering();

    executeClusteringEngine();//clusteringMethod_d,bunchArgs);

    clusteringMethod_d.getBestCluster();
    baseCluster = clusteringMethod_d.getBestCluster().cloneCluster();
    clusterList.add(clusteringMethod_d.getBestCluster().cloneCluster());

    if(bunchArgs.AGGLOMERATIVE) {
      Graph g = clusteringMethod_d.getBestGraph().cloneGraph();

      int []cNames = g.getClusterNames();  //c.getClusterNames();
      while(cNames.length>1) {
        //level++;
        NextLevelGraph nextL = new NextLevelGraph();
        Graph newG=nextL.genNextLevelGraph(g);

        newG.setPreviousLevelGraph(g);
        newG.setGraphLevel(g.getGraphLevel()+1);

        clusteringMethod_d.setGraph(newG);
        clusteringMethod_d.initialize();

        executeClusteringEngine();//clusteringMethod_d,bunchArgs);

        clusteringMethod_d.getBestCluster();
        clusterList.add(clusteringMethod_d.getBestCluster().cloneCluster());

        g = clusteringMethod_d.getBestGraph().cloneGraph();
        cNames = g.getClusterNames();  //c.getClusterNames();
      }
    }
    if(graphOutput_d != null) {
      graphOutput_d.setGraph(clusteringMethod_d.getBestGraph());
      graphOutput_d.write();
    }

    return true;
  }

  boolean runMQCalc() {
    MQCalcMdgFileName = bunchArgs.MQCALC_MDG_FILE;
    MQCalcSilFileName = bunchArgs.MQCALC_SIL_FILE;
    var MQCalcClass = bunchArgs.mqCalculatorClass;

    double mqResult = bunch.util.MQCalculator.CalcMQ(MQCalcMdgFileName,MQCalcSilFileName,MQCalcClass);
    Double Dmq = new Double(mqResult);
    MQCalcValue =  Dmq.toString();
    return true;
  }

  boolean runPRCalc() {
    String clusterF = bunchArgs.PR_CLUSTER_FILE;
    String expertF = bunchArgs.PR_EXPERT_FILE;

    bunch.util.PrecisionRecallCalculator calc =
      new bunch.util.PrecisionRecallCalculator(expertF,clusterF);

    precision = calc.get_precision();
    recall = calc.get_recall();

    return true;
  }

  private int getMedianLevelNumber() {
      if (clusteringMethod_d == null)
        return -1;

      Graph g = clusteringMethod_d.getBestGraph();
      Graph medianG = g.getMedianTree();
      return medianG.getGraphLevel();
  }

  public EngineResults getResultsHT() {
    var runMode = bunchArgs.runMode;
    if (runMode == CLUSTER) return getClusteringResultsHT();
    if (runMode == PR_CALC) return getPRResultsHT();
    if (runMode == MQ_CALC) return getMQCalcResultsHT();

    throw new UnsupportedOperationException();
  }

  public EngineResults getMQCalcResultsHT() {
    results = new EngineResults();
    if (MQCalcValue == null)
      return null;

    results.mqCalcResultValue = MQCalcValue;
    return results;
  }

  public EngineResults getPRResultsHT() {
    results = new EngineResults();
    if ((precision == null) || (recall == null))
      return null;

    results.prPrecisionValue = precision;
    results.prRecallValue = recall;
    return results;
  }

  public ArrayList getClusterList()
  {
    return this.clusterList;
  }

  public EngineResults getClusteringResultsHT() {
      if(clusteringMethod_d == null) return null;
      if(baseCluster == null) return null;

      results = new EngineResults();

      Long rt = new Long(totalTime);
      Long mqEvals = new Long(stats.getMQCalculations());
      Integer totalClusterLevels = new Integer(clusterList.size());
      Long saMovesTaken = new Long(stats.getSAOverrides());
      Integer medianLvl = new Integer(getMedianLevelNumber());

      results.RUNTIME = rt;
      results.MQEVALUATIONS = mqEvals;
      results.TOTAL_CLUSTER_LEVELS = totalClusterLevels;
      results.SA_NEIGHBORS_TAKEN = saMovesTaken;
      results.MEDIAN_LEVEL_GRAPH = medianLvl;

      //now handle errors & warnings
      Map errorHT = new Hashtable();
      results.ERROR_HASHTABLE = errorHT;

      Map warningHT = new Hashtable();
      if (reflexiveEdgeCount > 0) {
        Integer re = new Integer(reflexiveEdgeCount);
        warningHT.put(REFLEXIVE_EDGE_COUNT,re.toString());
      }
      results.WARNING_HASHTABLE = warningHT;

      Map []resultClusters = new Hashtable[clusterList.size()];

      for(int i = 0; i < clusterList.size(); i++) {
        Integer level = new Integer(i);
        Hashtable lvlHT = new Hashtable();
        lvlHT.clear();

        Cluster c = (Cluster)clusterList.get(i);
        Double bestMQ = new Double(c.getObjFnValue());
        Long clusterDepth = new Long(c.getDepth());
        Integer numClusters = new Integer(c.getClusterNames().length);

        lvlHT.put(CLUSTER_LEVEL,level.toString());
        lvlHT.put(MQVALUE,bestMQ.toString());
        lvlHT.put(CLUSTER_DEPTH,clusterDepth.toString());
        lvlHT.put(NUMBER_CLUSTERS,numClusters.toString());

        resultClusters[i] = lvlHT;
      }

      results.RESULT_CLUSTER_OBJS = resultClusters;
      stats.cleanup();

      Configuration cTmp = clusteringMethod_d.getConfiguration();
      if(cTmp instanceof NAHCConfiguration) {
        NAHCConfiguration nahcConf = (NAHCConfiguration)cTmp;
        if (nahcConf.getSATechnique() != null)
          nahcConf.getSATechnique().reset();
      }

      return results;
  }

  public boolean run(EngineArgs args) throws IOException, ClassNotFoundException {
    bunchArgs = args;

    var runMode = bunchArgs.runMode;
    if (runMode == null) return false;

    if (runMode == CLUSTER) return runClustering();
    if (runMode == PR_CALC) return runPRCalc();
    if (runMode == MQ_CALC) return runMQCalc();

    throw new IllegalArgumentException();
  }

    private void executeClusteringEngine() {
        startTime = System.currentTimeMillis();

        if(timeoutTimer != null)
          timeoutTimer.start();

        clusteringMethod_d.run();
        endTime = System.currentTimeMillis();
        totalTime += (endTime-startTime);

        if(timeoutTimer != null)
          timeoutTimer.stop();

      }

//********************
// For handling timeouts
//
static class TimeoutTimer implements java.awt.event.ActionListener {
    public void actionPerformed(java.awt.event.ActionEvent e) { }
}

}
