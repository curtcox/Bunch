package bunch.clustering;

import bunch.api.Algorithm;
import bunch.model.GenericFactory;

import static bunch.api.Algorithm.*;

/**
 * A factory for different kinds of objects that calculate the
 * optimum clustering for a graph
 *
 * @author Brian Mitchell
 *
 * @see BaseClusteringMethod
 * @see GenericFactory
 */
public final class ClusteringMethodFactory {

private final BaseClusteringMethod defaultMethod = new NextAscentHillClimbingClusteringMethod();


/**
 * This method returns the default clustering method.  It is used in the GUI and
 * API when the clustering algorithm is not explicitly specified.
 */
public BaseClusteringMethod getDefaultMethod() {
  return defaultMethod;
}

public String[] getItemList() {
  throw new UnsupportedOperationException();
}

/**
 * Obtains the clustering method corresponding to name passed as parameter.
 * Utility method that uses the #getItemInstance(java.lang.String) method
 * from GenericFactory and casts the object to a ClusteringMethod object.
 *
 * @param name for the desired method
 * @return the clustering method corresponding to the name
 */
public BaseClusteringMethod getMethod(Algorithm name) {
  if (name == HILL_CLIMBING) return new NextAscentHillClimbingClusteringMethod();
  if (name == NAHC) return new NextAscentHillClimbingClusteringMethod();
  if (name == SAHC) return new SteepestAscentHillClimbingClusteringMethod();
  if (name == GA) return new GAClusteringMethod();
  return defaultMethod;
}

}
