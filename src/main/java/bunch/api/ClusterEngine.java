package bunch.api;

import bunch.calculator.ObjectiveFunctionCalculator;
import bunch.clustering.ClusteringMethod;
import bunch.clustering.ClusteringMethodConfiguration;
import bunch.clustering.NAHCConfiguration;
import bunch.model.*;
import bunch.output.GraphOutput;
import bunch.stats.StatsManager;
import java.io.IOException;

public final class ClusterEngine {

  private ClusterArgs bunchArgs;
  private ClusteringMethod clusteringMethod;
  private GraphOutput graphOutput;
  private Graph initialGraph = new Graph(0);
  private long totalTime=0;
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
  }

  private void setIsClusterTree() {
    //now set if we are clustering trees or one level
    initialGraph.setIsClusterTree(bunchArgs.AGGLOMERATIVE);
  }

  private void loadClusteringMethodHandler() {
    clusteringMethod = bunchArgs.clusteringMethod;
    if (clusteringMethod == null) throw new IllegalArgumentException();

    var configuration = clusteringMethod.getConfiguration();
    if (initialGraph !=null && configuration !=null)
      configuration.init(initialGraph);
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
    if (bunchArgs.mdgGraphObject != null) {
      BunchMDG mdgObj = bunchArgs.mdgGraphObject;

      initialGraph = bunch.util.BunchUtilities.toInternalGraph(mdgObj);
      reflexiveEdgeCount = 0;
    }
  }

  public ClusterResults run(ClusterArgs args) throws IOException, ClassNotFoundException {
    this.bunchArgs = args;
    initClustering();

    executeClusteringEngine();//clusteringMethod_d,bunchArgs);

    clusteringMethod.getBestCluster();
    Cluster baseCluster = clusteringMethod.getBestCluster().cloneCluster();
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

    return getResults();
  }

  private int getMedianLevelNumber() {
      if (clusteringMethod == null)
        return -1;

      Graph g = clusteringMethod.getBestGraph();
      Graph medianG = g.getMedianTree();
      return medianG.getGraphLevel();
  }

  ClusterResults getResults() {
      var results = new ClusterResults();

      results.RUNTIME = totalTime;
      results.MQEVALUATIONS = stats.getMQCalculations();
      results.TOTAL_CLUSTER_LEVELS = clusterList.size();
      results.SA_NEIGHBORS_TAKEN = stats.getSAOverrides();
      results.MEDIAN_LEVEL_GRAPH = getMedianLevelNumber();

      results.reflexiveEdgeCount = reflexiveEdgeCount;
      results.RESULT_CLUSTER_OBJS = clusterList;
      StatsManager.cleanup();

      ClusteringMethodConfiguration cTmp = clusteringMethod.getConfiguration();
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

  public ClusterList getClusterList() {
    return this.clusterList;
  }

  public Graph getBestGraph() {
    return clusteringMethod.getBestGraph().cloneGraph();
  }

}
