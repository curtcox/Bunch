package bunch.clustering;

import bunch.model.Graph;

import java.io.*;

/**
 * A generic configuration class for clustering methods. This basic class
 * includes parameters for a population size and the number of iterations for
 * the algorithm.
 *
 * @author Brian Mitchell
 *
 */
public class Configuration {

private int numIterations_d;
private int popSize_d;

/**
 * Class constructor.
 */
protected Configuration() { }

/**
 * Initializes this configuration object based on the characteristics of
 * the graph passed as parameter. Intended for redefinition by subclasses.
 *
 * @param g the graph used to generate a default configuration
 */
public void init(Graph g) { }

/**
 * Sets the maximum number of iterations to be performed by the clustering method
 *
 * @param n the number of iterations
 * @see #getNumOfIterations()
 */
public void setNumOfIterations(int n)
{
  numIterations_d = n;
}

/**
 * Obtains the maximum number of iterations to be performed by the clustering method
 *
 * @return the number of iterations
 * @see #setNumOfIterations(int)
 */
public int getNumOfIterations()
{
  return numIterations_d;
}


/**
 * Sets the population size to be used by the clustering method
 *
 * @param p the population's size
 * @see #getPopulationSize()
 */
public void setPopulationSize(int p)
{
  popSize_d = p;
}

/**
 * Obtains the population size to be used by the clustering method
 *
 * @return population size
 * @see #setPopulationSize(int)
 */
public int getPopulationSize()
{
  return popSize_d;
}

}
