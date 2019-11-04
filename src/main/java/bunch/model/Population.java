package bunch.model;


public final class Population {

  private final ClusterList pop = new ClusterList();
  private static Graph graph;

  public Population(Graph graph) {
      Population.graph = graph.cloneGraph();
  }

  public void shuffle() {
      for(int i = 0; i < pop.size(); i++) {
         Cluster c = pop.get(i);
         graph.setClusters(c.getClusterVector());
         graph.shuffleClusters();
         c.setClusterVector(graph.getClusters());
         c.setConverged(false);
      }

  }

  public void genPopulation(int howMany) {
      pop.clear();
      for(int i = 0; i < howMany; i++) {
         //UNCOMMENT THE BELOW LINE FOR ORIGIONAL FUNCTION
         //int [] clusterV = graph.getRandomCluster();

         //COMMENT THE BELOW LINE TO REMOVE THE EXPIREMENTAL FUNCTION
         int [] clusterV = graph.genRandomClusterSize();
         Cluster c = new Cluster(graph,clusterV);
         pop.add(c);
      }
  }

  public int size()
  {
      return pop.size();
  }

  public Cluster getCluster(int whichOne) {
      if ((whichOne >= 0) && (whichOne < size()))
         return pop.get(whichOne);
      else
         return null;
  }

}