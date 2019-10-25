package bunch.clustering;

import bunch.model.GenericFactory;

/**
 * A factory for different kinds of objects that calculate the
 * optimum clustering for a graph
 *
 * @author Brian Mitchell
 *
 * @see ClusteringMethod
 * @see GenericFactory
 */
public final class ClusteringMethodFactory extends GenericFactory {

final String defaultMethod = "Hill Climbing";

/**
 * Class constructor, defines the objects that the factory will be able
 * to create
 */
public ClusteringMethodFactory() {
  setFactoryType("ClusteringMethod");
  addItem("Hill Climbing", bunch.clustering.GeneralHillClimbingClusteringMethod.class.getName());
  addItem("NAHC", bunch.clustering.NextAscentHillClimbingClusteringMethod.class.getName());
  addItem("SAHC", bunch.clustering.SteepestAscentHillClimbingClusteringMethod.class.getName());
  addItem("GA", bunch.clustering.GAClusteringMethod.class.getName());
  addItem("Exhaustive", bunch.clustering.OptimalClusteringMethod.class.getName());
}

/**
 * This method returns the default clustering method.  It is used in the GUI and
 * API when the clustering algorithm is not explicitly specified.
 */
public String getDefaultMethod() {
  return defaultMethod;
}

/**
 * This method returns a list of items in the factory.
 *
 * @return A string array containing the keys in the factory.
 */
public String[] getItemList() {
  String[] masterList = super.getItemList();
  String[] resList    = new String[masterList.length-2];

  int resPos = 0;
  for(int i = 0; i < masterList.length; i++) {
    String item = masterList[i];
    if ((item.equals("SAHC")) || (item.equals("NAHC")))
      continue;
    else
      resList[resPos++] = item;
  }

  return resList;
}

/**
 * Obtains the clustering method corresponding to name passed as parameter.
 * Utility method that uses the #getItemInstance(java.lang.String) method
 * from GenericFactory and casts the object to a ClusteringMethod object.
 *
 * @param the name for the desired method
 * @return the clustering method corresponding to the name
 */
public ClusteringMethod getMethod(String name) {
  return (ClusteringMethod)getItemInstance(name);
}
}
