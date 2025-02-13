package bunch.clustering.ga;

import bunch.clustering.ClusteringMethodConfiguration;
import bunch.model.Graph;

/**
 * A subclass of Configuration with specific parameters used by the Genetic
 * Algorithm clustering method.
 *
 * @author Brian Mitchell
 *
 * @see GAClusteringMethod
 */
public final class GAConfiguration
  extends ClusteringMethodConfiguration
{
private GAMethod method_d;
private double mutationThreshold_d;
private double crossoverThreshold_d;
private GAMethodFactory methodFactory_d;

/**
 * Parameterless class constructor.
 */
public GAConfiguration()
{
  methodFactory_d = new GAMethodFactory();
}

/**
 * Utility class constructor that receives a graph, and calls #init(bunch.model.Graph).
 *
 * @param g the graph used to set the default values
 * @see #init(Graph)
 */
public GAConfiguration(Graph g)
{
  init(g);
}

/**
 * Initializes this GAConfiguration object with values appropriate
 * to the characteristics of the graph passed as parameter.
 *
 * @param g the graph that will be used to create the default values for the
 * configuration object
 */
public void init(Graph g) {
  int nodes = g.getNumberOfNodes();
  setNumOfIterations(nodes * 100);
  setPopulationSize(nodes * 10);
  setCrossoverThreshold(0.6+0.2 * (getPopulationSize()/1000));
  int bitsize=0;
  for (bitsize=0; bitsize<nodes; ++bitsize) {
    double d = Math.pow(2, bitsize);
    if (d > nodes) {
      break;
    }
  }
  setMutationThreshold(0.005 * bitsize);
  setMethod(new GATournamentMethod());
}

/**
 * Obtains the GAMethod factory being used by this GAConfiguration object.
 * The factory is used to know what type of GAMethods are available and to return
 * an instance of one of them when necessary.
 *
 * @return the ga method factory
 */
public GAMethodFactory getMethodFactory()
{
  return methodFactory_d;
}

/**
 * Obtains the GAMethod currently selected for this GAConfiguration
 *
 * @return the ga method selected
 * @see #setMethod(GAMethod)
 */
public GAMethod getMethod()
{
  return method_d;
}

/**
 * Sets the GAMethod currently selected for this GAConfiguration
 *
 * @param m the ga method to set to this configuration instance
 * @see #getMethod()
 */
private void setMethod(GAMethod m) {
  method_d = m;
}

/**
 * Utility method to set the GAMethod currently selected for this GAConfiguration passing
 * the name of the class. The name of the method will be used to obtain
 * a corresponding GAMethod instance by calling GAMethodFactory.
 *
 * @param m the ga method to set to this configuration instance
 * @see #getMethod()
 * @see #setMethod(GAMethod)
 */
//public void setMethod(GaSelection m) {
//  setMethod(methodFactory_d.getMethod(m));
//}

/**
 * Sets the mutation threshold for this configuration object, expressed in chance of
 * a mutation ocurring. (e.g., 0.004 is a chance of 4 in a thousand)
 *
 * @param m the mutation threshold
 * @see #getMutationThreshold()
 */
public void setMutationThreshold(double m)
{
  mutationThreshold_d = m;
}

/**
 * Obtains the mutation threshold for this method
 *
 * @return the mutation threshold
 * @see #setMutationThreshold(double)
 */
public double getMutationThreshold()
{
  return mutationThreshold_d;
}

/**
 * Sets the crossover threshold for this method, expressed in chance of
 * a crossover ocurring. (e.g., 0.6 is a 60% chance of a crossover happening)
 *
 * @param c the crossover threshold
 * @see #getCrossoverThreshold()
 */
public void setCrossoverThreshold(double c)
{
  crossoverThreshold_d = c;
}

/**
 * Obtains the crossover threshold for this method
 *
 * @return the crossover threshold
 * @see #setCrossoverThreshold(double)
 */
public double getCrossoverThreshold()
{
  return crossoverThreshold_d;
}
}

