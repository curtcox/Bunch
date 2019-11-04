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
  private ClusteringMethod clusteringMethod;
  private GraphOutput graphOutput;
  private Graph initialGraph = new Graph(0);
  private BunchPreferences preferences;
  private Configuration configuration;
  private long totalTime=0;
  private Cluster baseCluster;
  private ClusterList clusterList;
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

  public Map getDefaultSpecialNodes(String graphName)
  { return getDefaultSpecialNodes(graphName, 3.0);  }

  private Map getDefaultSpecialNodes(String graphName, double threshold) {
    try {
      Hashtable<Key,Collection<String>> h = new Hashtable<>();
      Hashtable<String,String> centrals = new Hashtable<>();
      Hashtable<String,String> clients = new Hashtable<>();
      Hashtable<String,String> suppliers = new Hashtable<>();
      Hashtable<String,String> libraries = new Hashtable<>();

      String parserClass = "dependency";
      if(graphName.endsWith(".gxl") || graphName.endsWith(".GXL"))
        parserClass = "gxl";

      Parser p = bunchArgs.parserFactory.getParser(parserClass);
      p.setInput(graphName);
      Graph g = (Graph)p.parse();

      Node[] nodeList = g.getNodes();

      //find libraries
      for (Node node1 : nodeList) {
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
      List<String> clientsAL = new ArrayList<>(clients.values());

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

  private Hashtable<Key,Collection<String>> getSpecialModulesFromProperties() {
    Hashtable<Key,Collection<String>> h = new Hashtable<>();
    List<String> emptyList = new ArrayList<>();
    boolean   containsSpecial = false;

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
    clusterList = new ClusterList();
    loadPreferences();
    constructGraph();
    handleUserDirectedClustering();
    loadClusteringMethodHandler();
    setIsClusterTree();
    setUpCalculator();
    setupClusteringMethod();
    StatsManager.getInstance();
    setGraphOutputDriver();
  }

  private void setIsClusterTree() {
    //now set if we are clustering trees or one level
    initialGraph.setIsClusterTree(bunchArgs.AGGLOMERATIVE);
  }

  private void loadClusteringMethodHandler() throws IOException, ClassNotFoundException {
    //Load Clusteirng Method Handler
    var clustAlg = bunchArgs.CLUSTERING_ALG;
    if(clustAlg==null) throw new IllegalArgumentException();
    clusteringMethod = preferences.getClusteringMethodFactory().getMethod(clustAlg);
    if(clusteringMethod == null) throw new IllegalArgumentException();

    configuration = clusteringMethod.getConfiguration();
    if (initialGraph !=null&& configuration !=null)
      configuration.init(initialGraph);

    if (clustAlg == GA)            { loadGaConfig(); }
    if (clustAlg == SAHC)          { loadSahcConfig(); }
    if (clustAlg == HILL_CLIMBING) { loadHillClimbingConfig(); }
    if (clustAlg == NAHC)          { loadNahcConfig(); }
  }

  private void loadNahcConfig() throws IOException, ClassNotFoundException {
    Integer HCPct = bunchArgs.algNahcHcPct;
    Integer rndPct = bunchArgs.algNahcRndPct;
    Integer popSz = bunchArgs.algNahcPopulationSz;

    NAHCConfiguration c = (NAHCConfiguration) configuration;

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
    NAHCConfiguration c = (NAHCConfiguration) configuration;
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
      configuration.setPopulationSize(popSz);
  }

  private void loadGaConfig() {
    GAConfiguration gaConfig = (GAConfiguration) configuration;
    gaConfig.setMethod(bunchArgs.ALG_GA_SELECTION_METHOD);
    gaConfig.setNumOfIterations(bunchArgs.ALG_GA_NUM_GENERATIONS);
    gaConfig.setCrossoverThreshold(bunchArgs.ALG_GA_CROSSOVER_PROB);
    gaConfig.setMutationThreshold(bunchArgs.ALG_GA_MUTATION_PROB);
    gaConfig.setPopulationSize(bunchArgs.ALG_GA_POPULATION_SZ);
  }

  private void setGraphOutputDriver() {
    //now set the graph output driver
    graphOutput = bunchArgs.graphOutput;
    var outputMode = bunchArgs.OUTPUT_FORMAT;
    if (outputMode != null || !(outputMode==NULL)) {

      if (outputMode != null) {
        String outFileName = bunchArgs.OUTPUT_FILE;
        if (outFileName == null)
          outFileName = bunchArgs.MDG_INPUT_FILE_NAME;

        if (bunchArgs.OUTPUT_TREE) {
            graphOutput.setNestedLevels(true);
        }

        graphOutput.setBaseName(outFileName);
        graphOutput.setBasicName(outFileName);
        String outputFileName = graphOutput.getBaseName();
        String outputPath = bunchArgs.OUTPUT_DIRECTORY;
        if(outputPath != null) {
          java.io.File f = new java.io.File(graphOutput.getBaseName());
          String filename = f.getName();
          outputFileName = outputPath+filename;
        }
        graphOutput.setCurrentName(outputFileName);
      }
    }
  }

  private void setupClusteringMethod() {
    //now setup the clustering method object
    clusteringMethod.initialize();
    clusteringMethod.setGraph(initialGraph.cloneGraph());
  }

  private void setUpCalculator() {
    //now setup the calculator
    ObjectiveFunctionCalculator objFnCalc = bunchArgs.mqCalculatorClass;
    (preferences.getObjectiveFunctionCalculatorFactory()).setCurrentCalculator(objFnCalc);
    initialGraph.setObjectiveFunctionCalculator(objFnCalc);
    Global.calculator = objFnCalc;
  }

  private void handleUserDirectedClustering() {
    //NOW HANDLE USER DIRECTED CLUSTERING, IF SET AND THE LOCKS
    String userSILFile = bunchArgs.userDirectedClusterSil;
    if(userSILFile != null) {
      boolean lock = bunchArgs.lockUserSetClusters;

      Parser cp = bunchArgs.parserFactory.getParser("cluster");
      cp.setInput(userSILFile);
      cp.setObject(initialGraph);
      cp.parse();
      if(lock)
        initialGraph.setDoubleLocks(true);

      //=================================
      //Now lock the clusters
      //=================================
      int[] clust = initialGraph.getClusters();
      boolean[] locks = initialGraph.getLocks();
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
      Parser p = bunchArgs.parserFactory.getParser("dependency");
      p.setInput(bunchArgs.mdgInputFileName);
      p.setDelims(getFileDelims());
      initialGraph = (Graph)p.parse();
      reflexiveEdgeCount = p.getReflexiveEdges();
    }

    if (bunchArgs.mdgGraphObject != null) {
      BunchMDG mdgObj = bunchArgs.mdgGraphObject;

      initialGraph = bunch.util.BunchUtilities.toInternalGraph(mdgObj);
      reflexiveEdgeCount = 0;
    }
  }

  private void loadPreferences() throws IOException, ClassNotFoundException {
    //Load Preferences
    preferences = (BunchPreferences)(Beans.instantiate(null, "bunch.BunchPreferences"));
  }

  public Graph getBestGraph() {
    if (clusteringMethod == null)
      return null;

    return clusteringMethod.getBestGraph().cloneGraph();
  }

  private void runClustering() throws IOException, ClassNotFoundException {
    initClustering();

    executeClusteringEngine();//clusteringMethod_d,bunchArgs);

    clusteringMethod.getBestCluster();
    baseCluster = clusteringMethod.getBestCluster().cloneCluster();
    clusterList.add(clusteringMethod.getBestCluster().cloneCluster());

    if(bunchArgs.AGGLOMERATIVE) {
      Graph g = clusteringMethod.getBestGraph().cloneGraph();

      int []cNames = g.getClusterNames();  //c.getClusterNames();
      while(cNames.length>1) {
        //level++;
        NextLevelGraph nextL = new NextLevelGraph();
        Graph newG=nextL.genNextLevelGraph(g);

        newG.setPreviousLevelGraph(g);
        newG.setGraphLevel(g.getGraphLevel()+1);

        clusteringMethod.setGraph(newG);
        clusteringMethod.initialize();

        executeClusteringEngine();//clusteringMethod_d,bunchArgs);

        clusteringMethod.getBestCluster();
        clusterList.add(clusteringMethod.getBestCluster().cloneCluster());

        g = clusteringMethod.getBestGraph().cloneGraph();
        cNames = g.getClusterNames();  //c.getClusterNames();
      }
    }
    if(graphOutput != null) {
      graphOutput.setGraph(clusteringMethod.getBestGraph());
      graphOutput.write();
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
      if (clusteringMethod == null)
        return -1;

      Graph g = clusteringMethod.getBestGraph();
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

  public ClusterList getClusterList()
  {
    return this.clusterList;
  }

  private EngineResults getClusteringResultsHT() {
      if (clusteringMethod == null) return null;
      if (baseCluster == null)        return null;

      results = new EngineResults();

      results.RUNTIME = totalTime;
      results.MQEVALUATIONS = stats.getMQCalculations();
      results.TOTAL_CLUSTER_LEVELS = clusterList.size();
      results.SA_NEIGHBORS_TAKEN = stats.getSAOverrides();
      results.MEDIAN_LEVEL_GRAPH = getMedianLevelNumber();

      results.reflexiveEdgeCount = reflexiveEdgeCount;
      results.RESULT_CLUSTER_OBJS = clusterList;
      StatsManager.cleanup();

      Configuration cTmp = clusteringMethod.getConfiguration();
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
    clusteringMethod.run();
    long endTime = System.currentTimeMillis();
    totalTime += (endTime - startTime);
  }

}
