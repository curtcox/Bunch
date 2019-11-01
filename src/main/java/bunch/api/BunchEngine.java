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

  private EngineArgs bunchArgs = new EngineArgs();
  private EngineResults results = new EngineResults();
  private ClusteringMethod clusteringMethod_d;
  private GraphOutput graphOutput_d;
  private Graph initialGraph_d = new Graph(0);
  private BunchPreferences preferences_d;
  private Configuration configuration_d;
  private long totalTime=0;
  private Cluster baseCluster;
  private List<Cluster> clusterList;
  private int reflexiveEdgeCount = 0;

  private Double precision;
  private Double recall;
  private Double MQCalcValue;

  private final StatsManager stats = bunch.stats.StatsManager.getInstance();

  BunchEngine() {}

  private String getFileDelims() {
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
private void arrangeLibrariesClientsAndSuppliers(Graph g, Map special) {
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
  for (Node item : originalList) {
    if (suppliers != null) {
      for (Object supplier : suppliers) {
        String name = item.getName();
        if (name.equals(supplier)) {
          item.setType(Node.SUPPLIER);
          break;
        }
      }
    }
    if (clients != null) {
      for (Object client : clients) {
        String name = item.getName();
        if (name.equals(client)) {
          item.setType(Node.CLIENT);
          break;
        }
      }
    }
    if (centrals != null) {
      for (Object central : centrals) {
        String name = item.getName();
        if (name.equals(central)) {
          item.setType(Node.CENTRAL);
          break;
        }
      }
    }

    if (libraries != null) {
      for (Object library : libraries) {
        String name = item.getName();
        if (name.equals(library)) {
          item.setType(Node.LIBRARY);
          break;
        }
      }
    }
  }

  int deadNodes = 0;
  //now consolidate nodes that only point to omnipresent, libs, and suppliers
  for (Node value : originalList) {
    if (value.getType() == Node.NORMAL) {
      boolean noNormalDeps = true;
      int[] tmpDeps = value.getDependencies();
      int[] tmpBeDeps = value.getBackEdges();
      int client = 0;
      int supplier = 0;
      int central = 0;
      int library = 0;
      for (int tmpDep : tmpDeps) {
        if ((originalList[tmpDep].getType() == Node.NORMAL) ||
                (originalList[tmpDep].getType() >= Node.DEAD)) {
          noNormalDeps = false;
          break;
        } else {
          switch (originalList[tmpDep].getType()) {
            case Node.CLIENT:
              client++;
              break;
            case Node.SUPPLIER:
              supplier++;
              break;
            case Node.CENTRAL:
              central++;
              break;
            case Node.LIBRARY:
              library++;
              break;
          }
        }
      }
      for (int tmpBeDep : tmpBeDeps) {
        if ((originalList[tmpBeDep].getType() == Node.NORMAL) ||
                (originalList[tmpBeDep].getType() >= Node.DEAD)) {
          noNormalDeps = false;
          break;
        } else {
          switch (originalList[tmpBeDep].getType()) {
            case Node.CLIENT:
              client++;
              break;
            case Node.SUPPLIER:
              supplier++;
              break;
            case Node.CENTRAL:
              central++;
              break;
            case Node.LIBRARY:
              library++;
              break;
          }
        }
      }
      if (noNormalDeps) {
        deadNodes++;
        int n1 = Math.max(client, supplier);
        int n2 = Math.max(central, library);
        int max = Math.max(n1, n2);
        int type = Node.CLIENT;

        if (max == client) type = Node.CLIENT;
        if (max == supplier) type = Node.SUPPLIER;
        if (max == central) type = Node.CENTRAL;
        if (max == library) type = Node.LIBRARY;
        value.setType(Node.DEAD + max);
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
  for (Node node : originalList) {
    if (node.getType() == Node.NORMAL) {
      normal.put(node.getId(), j);
      nodeList[j++] = node.cloneNode();
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
      tmpAssoc = (Integer)normal.get(deps[z]);
      if (tmpAssoc == null) {
        deps[z] = -1;
        depsRemoveCount++;
      } else {
        deps[z] = tmpAssoc;
      }
    }

    for(int z = 0; z < beDeps.length; z++) {
      tmpAssoc = (Integer)normal.get(beDeps[z]);
      if (tmpAssoc == null) {
        beDeps[z] = -1;
        beDeptsRemoveCount++;
      } else {
        beDeps[z] = tmpAssoc;
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

  private Map getDefaultSpecialNodes(String graphName, double threshold) {
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
      for (Node node1 : nodeList) {
        String nname = node1.getName();
        if ((node1.getDependencies() == null || node1.getDependencies().length == 0)
                && !clients.containsKey(node1.getName())
                && !suppliers.containsKey(node1.getName())
                && !centrals.containsKey(node1.getName())) {
          libraries.put(node1.getName(), node1.getName());
        }
      }

      //find clients
      double avg = 0.0, sum = 0.0;
      for (Node element : nodeList) {
        if (element.getDependencies() != null) {
          sum += element.getDependencies().length;
        }
      }
      avg = sum/nodeList.length;
      avg = avg * threshold;
      for (Node item : nodeList) {
        if (item.getDependencies() != null
                && item.getDependencies().length > avg
                && !libraries.containsKey(item.getName())) {
          clients.put(item.getName(), item.getName());
        }
      }

      //find suppliers
      avg = 0.0; sum = 0.0;
      int[] inNum = new int[nodeList.length];

      for (int j=0; j<nodeList.length; ++j) {
        int currval = 0;
        for (Node node : nodeList) {
          int[] deps = node.getDependencies();
          if (deps != null) {
            for (int dep : deps) {
              if (dep == j) {
                currval++;
              }
            }
          }
        }
        inNum[j] = currval;
      }
      for (int value : inNum) {
        sum += value;
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

      for (Object o : clientsAL) {
        String client = (String) o;
        if (suppliers.containsKey(client))
          centrals.put(client, client);
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

  private Hashtable getSpecialModulesFromProperties() {
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

    if(containsSpecial)
      return h;
    else
      return null;
  }

  private void initClustering() throws IOException, ClassNotFoundException {
    clusterList = new ArrayList();
    loadPreferences();
    constructGraph();
    handleUserDirectedClustering();
    seeIfThereAreSpecialModules();
    loadClusteringMethodHandler();
    setIsClusterTree();
    setUpCalculator();
    setupClusteringMethod();
    StatsManager.getInstance();
    initCallback();
    initTimer();
    setGraphOutputDriver();
  }

  private void initTimer() {
    //see if there is a timeout requested
    Integer toTime = bunchArgs.TIMEOUT_TIME;
  }

  private void initCallback() {
    //see if a callback class is setup, if so save a reference to the class
    Integer iTmp = bunchArgs.callbackObjectFrequency;
    int callbackFrequency;
    if(iTmp != null)
      callbackFrequency = iTmp;
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
      c.setPopulationSize(popSz);

    if(HCPct != null) {
      c.setMinPctToConsider(HCPct);

      if(rndPct != null)
        c.setRandomizePct(rndPct);
      else {
        int pctTmp = 100- HCPct;
        c.setRandomizePct(pctTmp);
      }
    }

    String SAClass = bunchArgs.algNahcSaClass;
    if (SAClass != null) {
      SATechnique saHandler = (SATechnique) Beans.instantiate(null,SAClass);
      if (saHandler != null) {
        Map saHandlerArgs = bunchArgs.algNahcSaConfig;
        if(saHandlerArgs != null) {
          saHandler.setConfig();
        }
        c.setSATechnique(saHandler);
      }
    }
  }

  private void loadHillClimbingConfig() {
    NAHCConfiguration c = (NAHCConfiguration)configuration_d;
    if(bunchArgs.algHcRndPct != null) {
      Integer randomize = bunchArgs.algHcRndPct;
      c.setRandomizePct(randomize);
    }

    if(bunchArgs.algHcHcPct != null) {
      Integer hcThreshold = bunchArgs.algHcHcPct;
      c.setMinPctToConsider(hcThreshold);
    }
  }

  private void loadSahcConfig() {
    Integer popSz = bunchArgs.ALG_SAHC_POPULATION_SZ;

    if(popSz != null)
      configuration_d.setPopulationSize(popSz);
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

      if (outputMode != null) {
        String outFileName = bunchArgs.OUTPUT_FILE;
        if (outFileName == null)
          outFileName = bunchArgs.MDG_INPUT_FILE_NAME;

        graphOutput_d = preferences_d.getGraphOutputFactory().getOutput(outputMode);

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
      if(lock)
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

  private void runClustering() throws IOException, ClassNotFoundException {
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

  }

  private void runMQCalc() {
    String MQCalcMdgFileName = bunchArgs.MQCALC_MDG_FILE;
    String MQCalcSilFileName = bunchArgs.MQCALC_SIL_FILE;
    var MQCalcClass = bunchArgs.mqCalculatorClass;

    MQCalcValue = bunch.util.MQCalculator.CalcMQ(MQCalcMdgFileName, MQCalcSilFileName,MQCalcClass);
  }

  private void runPRCalc() {
    String clusterF = bunchArgs.PR_CLUSTER_FILE;
    String expertF = bunchArgs.PR_EXPERT_FILE;

    bunch.util.PrecisionRecallCalculator calc =
      new bunch.util.PrecisionRecallCalculator(expertF,clusterF);

    precision = calc.get_precision();
    recall = calc.get_recall();

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

  private EngineResults getMQCalcResultsHT() {
    results = new EngineResults();
    if (MQCalcValue == null)
      return null;

    results.mqCalcResultValue = MQCalcValue;
    return results;
  }

  private EngineResults getPRResultsHT() {
    results = new EngineResults();
    if ((precision == null) || (recall == null))
      return null;

    results.prPrecisionValue = precision;
    results.prRecallValue = recall;
    return results;
  }

  public List<Cluster> getClusterList()
  {
    return this.clusterList;
  }

  private EngineResults getClusteringResultsHT() {
      if (clusteringMethod_d == null) return null;
      if (baseCluster == null)        return null;

      results = new EngineResults();

      results.RUNTIME = totalTime;
      results.MQEVALUATIONS = stats.getMQCalculations();
      results.TOTAL_CLUSTER_LEVELS = clusterList.size();
      results.SA_NEIGHBORS_TAKEN = stats.getSAOverrides();
      results.MEDIAN_LEVEL_GRAPH = getMedianLevelNumber();

      //now handle errors & warnings
    results.ERROR_HASHTABLE = new Hashtable();

      Map warningHT = new Hashtable();
      if (reflexiveEdgeCount > 0) {
        warningHT.put(REFLEXIVE_EDGE_COUNT,reflexiveEdgeCount);
      }
      results.WARNING_HASHTABLE = warningHT;

      Map []resultClusters = new Hashtable[clusterList.size()];

      for(int i = 0; i < clusterList.size(); i++) {
        Hashtable lvlHT = new Hashtable();
        lvlHT.clear();

        Cluster c = clusterList.get(i);
        lvlHT.put(CLUSTER_LEVEL,i);
        lvlHT.put(MQVALUE,c.getObjFnValue());
        lvlHT.put(CLUSTER_DEPTH,c.getDepth());
        lvlHT.put(NUMBER_CLUSTERS,c.getClusterNames().length);
        resultClusters[i] = lvlHT;
      }

      results.RESULT_CLUSTER_OBJS = resultClusters;
      StatsManager.cleanup();

      Configuration cTmp = clusteringMethod_d.getConfiguration();
      if(cTmp instanceof NAHCConfiguration) {
        NAHCConfiguration nahcConf = (NAHCConfiguration)cTmp;
        if (nahcConf.getSATechnique() != null)
          nahcConf.getSATechnique().reset();
      }

      return results;
  }

  public void run(EngineArgs args) throws IOException, ClassNotFoundException {
    bunchArgs = args;

    var runMode = bunchArgs.runMode;
    if (runMode == null) return;

    if (runMode == CLUSTER) {
      runClustering();
      return;
    }
    if (runMode == PR_CALC) {
      runPRCalc();
      return;
    }
    if (runMode == MQ_CALC) {
      runMQCalc();
      return;
    }

    throw new IllegalArgumentException();
  }

  private void executeClusteringEngine() {
    long startTime = System.currentTimeMillis();
    clusteringMethod_d.run();
    long endTime = System.currentTimeMillis();
    totalTime += (endTime - startTime);
  }

}
