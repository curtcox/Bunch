package bunch.clustering;

/**
 * A factory for different kinds of objects that calculate the
 * optimum clustering for a graph
 *
 * @author Brian Mitchell
 *
 * @see BaseClusteringMethod
 */
public final class ClusteringMethodFactory {

private final ClusteringMethod defaultMethod = new NextAscentHillClimbingClusteringMethod();


/**
 * This method returns the default clustering method.  It is used in the GUI and
 * API when the clustering algorithm is not explicitly specified.
 */
public ClusteringMethod getDefaultMethod() {
  return defaultMethod;
}

}
