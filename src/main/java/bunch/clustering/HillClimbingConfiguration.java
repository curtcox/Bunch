package bunch.clustering;

import bunch.model.Graph;

/**
 * A subclass of Configuration with specific parameters used by the Hill Climbing
 * Algorithms.
 *
 * @author Brian Mitchell
 */
public class HillClimbingConfiguration extends Configuration {
private double threshold_d=0.1;

/**
 * Parameterless class constructor.
 */
public HillClimbingConfiguration() { }

/**
 * Utility class constructor that receives a graph, and calls #init(bunch.model.Graph).
 *
 * @param g the graph used to set the default values
 * @see #init(Graph)
 */
public HillClimbingConfiguration(Graph g)
{
  init(g);
}

/**
 * Initializes this HillClimbingConfiguration object with values appropriate
 * to the characteristics of the graph passed as parameter.
 *
 * @param g the graph that will be used to create the default values for the
 * configuration object
 */
public void init(Graph g) {
  int nodes = g.getNumberOfNodes();
  super.init(g);
}

/**
 * Define the threshold that determines when no further improvement can be
 * expected. This threshold is a percentage of the total number of
 * generations the algorithm will run. If that percentage of generations
 * has elapsed without change in the best graph found, the algorithm is
 * considered finished.
 *
 * @param t the threshold percentage expressed as a real value
 * @see #getThreshold()
 */
public void setThreshold(double t)
{
    threshold_d = t;
}

/**
 * Obtain the threshold that determines when no further improvement can be
 * expected by running the hill climbing algorithm
 *
 * @return the threshold percentage expressed as a real (double) value
 * @see #setThreshold(double)
 */
public double getThreshold()
{
    return threshold_d;
}
}

