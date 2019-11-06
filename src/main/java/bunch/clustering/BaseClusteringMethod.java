package bunch.clustering;

import bunch.event.IterationEvent;
import bunch.event.IterationListener;
import bunch.clustering.ga.GAClusteringMethod;
import bunch.model.Cluster;
import bunch.model.Graph;

/**
 * The superclass for all clustering methods. A clustering method is (usually) an
 * optimization algorithm that takes a graph as input and produces a partitioned
 * graph as output.
 *
 * @author Brian Mitchell
 *
 * @see NextAscentHillClimbingClusteringMethod
 * @see SteepestAscentHillClimbingClusteringMethod
 * @see GAClusteringMethod
 */
abstract class BaseClusteringMethod implements ClusteringMethod {

private IterationListener listener_d;
private Graph graph_d;
private Graph bestGraph_d;
private double elapsedTime_d=0.0;
private final Configuration configuration_d;

BaseClusteringMethod(Configuration configuration) {
    this.configuration_d = configuration;
}

public void initialize()
{
  setBestGraph(null);
}

public void setGraph(Graph g)
{
  graph_d = g;
}

public Graph getGraph() {
  return graph_d;
}

void setBestGraph(Graph g)
{
  bestGraph_d = g;
}
public Graph getBestGraph()
{
  return bestGraph_d;
}
public double getBestObjectiveFunctionValue()
{
  return bestGraph_d.getObjectiveFunctionValue();
}
public double getElapsedTime()
{
  return elapsedTime_d;
}
void setElapsedTime(double l)
{
  elapsedTime_d = l;
}

public void setIterationListener(IterationListener il)
{
  listener_d = il;
}
public IterationListener getIterationListener()
{
  return listener_d;
}

void fireIterationEvent(IterationEvent e) {
   if (listener_d != null) {
      listener_d.newIteration(e);
   }
}

public abstract int getMaxIterations();

public abstract Cluster getBestCluster();

public Configuration getConfiguration() {
  return configuration_d;
}

}


