package bunch.BunchServer;

import bunch.*;

public class ServerClusteringEngine {

  ServerProperties sProps;
  
  public ServerClusteringEngine(ServerProperties sp) {
      sProps = sp;
  }

  public boolean run()
  {
      sProps.theGraph.setRandom(new java.util.Random(10));
      //ClusteringMethod2 cm = (ClusteringMethod2)new SteepestAscentHillClimbingClusteringMethod2();
      ClusteringMethod2 cm = (ClusteringMethod2)new ServerSteepestAscentClusteringMethod();

      HillClimbingConfiguration hcc = (HillClimbingConfiguration)cm.getConfiguration();
      hcc.setNumOfIterations(1);
      hcc.setThreshold(1.0);
      ((GenericDistribHillClimbingClusteringMethod)cm).setConfiguration(hcc);
      
      cm.initialize();
      cm.setGraph(sProps.theGraph.cloneGraph());
      cm.run();
      Cluster c = cm.getBestCluster();
      Graph g = cm.getBestGraph();

      sProps.theGraph = g.cloneGraph();

      return true;
  }

  
} 