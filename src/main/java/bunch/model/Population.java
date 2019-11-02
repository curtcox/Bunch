package bunch.model;

import java.util.Vector;

public final class Population {

  private final Vector<Cluster>   pop = new Vector<>();
  private static   Graph g = null;

  public Population(Graph graph) {
      g = graph.cloneGraph();
  }

  public void shuffle() {
      for(int i = 0; i < pop.size(); i++) {
         Cluster c = (Cluster)pop.elementAt(i);
         g.setClusters(c.getClusterVector());
         g.shuffleClusters();
         c.setClusterVector(g.getClusters());
         c.setConverged(false);
      }

  }

  public void genPopulation(int howMany) {
      pop.removeAllElements();
      for(int i = 0; i < howMany; i++) {
         //UNCOMMENT THE BELOW LINE FOR ORIGIONAL FUNCTION
         //int [] clusterV = g.getRandomCluster();

         //COMMENT THE BELOW LINE TO REMOVE THE EXPIREMENTAL FUNCTION
         int [] clusterV = g.genRandomClusterSize();
         Cluster c = new Cluster(g,clusterV);
         pop.addElement(c);
      }
  }

  public int size()
  {
      return pop.size();
  }

  public Cluster getCluster(int whichOne) {
      if ((whichOne >= 0) && (whichOne < size()))
         return (Cluster)pop.elementAt(whichOne);
      else
         return null;
  }

}