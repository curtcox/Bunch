package bunch.clustering;

import bunch.event.IterationEvent;
import bunch.event.IterationListener;
import bunch.model.Cluster;
import bunch.model.Configuration;
import bunch.model.Graph;

/**
 * The superclass for all updated clustering methods. A clustering method is (usually) an
 * optimization algorithm that takes a graph as input and produces a partitioned
 * graph as output.
 *
 * NOTE:  This abstract class extends the clustering method class and adds support for
 * working with objects of type cluster as well as type graph.  When all clustering
 * methods can use the newer type Cluster, this class can replace the base Clustering
 * Method class.
 *
 * @author Brian Mitchell
 *
 * @see NextAscentHillClimbingClusteringMethod
 * @see SteepestAscentHillClimbingClusteringMethod
 * @see GAClusteringMethod
 */

public abstract class BaseHierClusteringMethod extends BaseClusteringMethod
        implements HierClusteringMethod
{

    private IterationListener listener_d;
private Graph graph_d;
private Cluster bestCluster_d;
Configuration configuration_d;
private double elapsedTime_d=0.0;

/**
 * the class constructor
 */
BaseHierClusteringMethod(Configuration configuration) { super(configuration); }

/**
 * Initializes the clustering method.
 */
public void initialize()
{
  setBestCluster(null);
}

/**
 * Sets the graph to be partitioned by this clustering method
 *
 * @param g the graph to partition
 * @see #getGraph()
 */
public void setGraph(Graph g)
{
  graph_d = g;
}

/**
 * Obtains the graph to be partitioned by this clustering method
 *
 * @return the graph
 */
Graph getGraph()
{
  return graph_d;
}

/**
 * Sets the resultant of this clustering method (the best partitioned graph
 * found)
 *
 * @see #getBestGraph()
 */
void setBestCluster(Cluster c)
{
  bestCluster_d = c;
}

/**
 * Obtains the resultant of this clustering method (the best partitioned graph
 * found).  The returned object is a cluster
 *
 * @return the (best) result graph
 */
public Cluster getBestCluster()
{
  return bestCluster_d;
}

/**
 * Obtains the resultant of this clustering method (the best partitioned graph
 * found).  The returned object is a Graph.
 *
 * @return the (best) result graph
 */
public Graph getBestGraph() {
   Cluster best = getBestCluster();
   graph_d.setClusters(best.getClusterVector());
   graph_d.setObjectiveFunctionValue(best.getObjFnValue());
   return graph_d;
}

/**
 * Utility method that returns the objective function value of the best graph
 *
 * @return the objective function value of the best partitioned graph found
 */
public double getBestObjectiveFunctionValue()
{
  return bestCluster_d.getObjFnValue();
}

/**
 * Obtains the elapsed time so far for the current clustering process
 *
 * @return the elapsed time
 * @see #setElapsedTime(double)
 */
public double getElapsedTime()
{
  return elapsedTime_d;
}

/**
 * Sets the elapsed time so far for the current clustering process (in general,
 * used internally by the clustering algorithm)
 *
 * @param l the elapsed time
 * @see #getElapsedTime()
 */
void setElapsedTime(double l)
{
  elapsedTime_d = l;
}

/**
 * Sets an iteration listener for this clustering method. The listener's
 * "newIteration" method  will be called at every iteration of the
 * partitioning process. This can be used to update a progress bar, for example.
 *
 * @param il the IterationListener for this class
 * @see IterationListener
 * @see #getIterationListener()
 */
public void setIterationListener(IterationListener il)
{
  listener_d = il;
}

/**
 * Obtains the iteration listener set for this clustering method object
 *
 * @return the IterationListener for this object
 * @see IterationListener
 * @see #setIterationListener(IterationListener)
 */
public IterationListener getIterationListener()
{
  return listener_d;
}

/**
 * Fires an Iteration event to this clustering method's iteration listener
 */
void fireIterationEvent(IterationEvent e) {
   if (listener_d != null) {
      listener_d.newIteration(e);
   }
}

    /**
 * Obtains the maximum number of iterations this algorithm will perform. Useful
 * to set the parameters for a progress bar, for example
 */
public abstract int getMaxIterations();

/**
 * Obtains the configuration object for this clustering method.
 *
 * @return the configuration
 */
public Configuration getConfiguration()
{
  return configuration_d;
}

}
