package bunch;

import java.util.Vector;
import java.util.Enumeration;

public class Population {

  Vector   pop = new Vector();
  static   Graph g = null;
  Cluster  bestCluster = null;

  public Population(Graph graph) {
      g = graph.cloneGraph();
  }

  public void shuffle()
  {
      for(int i = 0; i < pop.size(); i++)
      {
         Cluster c = (Cluster)pop.elementAt(i);
         g.setClusters(c.getClusterVector());
         g.shuffleClusters();
         c.setClusterVector(g.getClusters());
         c.setConverged(false);
      }

  }

  public void genPopulation(int howMany)
  {
      pop.removeAllElements();
      for(int i = 0; i < howMany; i++)
      {
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

  public Cluster getCluster(int whichOne)
  {
      if ((whichOne >= 0) && (whichOne < size()))
         return (Cluster)pop.elementAt(whichOne);
      else
         return null;
  }

  public Enumeration elements()
  {
      return pop.elements();
  }
}