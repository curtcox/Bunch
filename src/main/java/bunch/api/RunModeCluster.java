package bunch.api;

import bunch.calculator.ObjectiveFunctionCalculator;
import bunch.clustering.ClusteringMethod;
import bunch.clustering.NAHCConfiguration;
import bunch.ga.GAConfiguration;
import bunch.model.*;
import bunch.parser.Parser;
import bunch.simple.SATechnique;
import bunch.stats.StatsManager;

import java.beans.Beans;
import java.io.IOException;
import java.util.Map;

import static bunch.api.Algorithm.*;
import static bunch.api.OutputFormat.NULL;

final class RunModeCluster {

  private EngineArgs bunchArgs = new EngineArgs();
  private EngineResults results = new EngineResults();
  private ClusteringMethod clusteringMethod;
  private GraphOutput graphOutput;
  private Graph initialGraph = new Graph(0);
  private Configuration configuration;
  private long totalTime=0;
  private Cluster baseCluster;
  private ClusterList clusterList;
  private int reflexiveEdgeCount = 0;
  private final StatsManager stats = StatsManager.getInstance();

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

  private void initClustering() throws IOException, ClassNotFoundException {
    clusterList = new ClusterList();
    constructGraph();
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
    clusteringMethod = bunchArgs.clusteringMethod;
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
    initialGraph.setObjectiveFunctionCalculator(objFnCalc);
    Global.calculator = objFnCalc;
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

  void runClustering() throws IOException, ClassNotFoundException {
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

  private int getMedianLevelNumber() {
      if (clusteringMethod == null)
        return -1;

      Graph g = clusteringMethod.getBestGraph();
      Graph medianG = g.getMedianTree();
      return medianG.getGraphLevel();
  }

  EngineResults getClusteringResultsHT() {
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

  private void executeClusteringEngine() {
    long startTime = System.currentTimeMillis();
    clusteringMethod.run();
    long endTime = System.currentTimeMillis();
    totalTime += (endTime - startTime);
  }

}
