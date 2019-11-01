package bunch.model;

import java.io.*;

/**
 * A generic configuration class for clustering methods. This basic class
 * includes parameters for a population size, the number of iterations for
 * the algorithm and three sets of "Features" for pre/post and processing
 * during the algorithm. Features are intended to extend an algorithm in
 * generic form. This class is also Serializable so it can easily be saved
 * or transmitted over the network if necessary.
 *
 * @author Brian Mitchell
 *
 * @see Feature
 */
public class Configuration implements java.io.Serializable {

private Feature[] preFeatures_d;
private Feature[] features_d;
private Feature[] postFeatures_d;
private int numIterations_d;
private int popSize_d;
public final int expNumber_d = 0;
public final boolean runBatch_d = false;
public final BufferedWriter writer_d = null;

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

/**
 * Obtains the array of pre-condition features for this configuration.
 *
 * @return the array of precondition features
 */
public Feature[] getPreConditionFeatures()
{
  return preFeatures_d;
}

/**
 * Obtains the array of features that are executed along with the
 * clustering algorithm for this configuration.
 *
 * @return the array of features
 */
public Feature[] getFeatures()
{
  return features_d;
}

/**
 * Obtains the array of post-condition features for this configuration.
 *
 * @return the array of postcondition features
 */
public Feature[] getPostConditionFeatures()
{
  return postFeatures_d;
}

}
