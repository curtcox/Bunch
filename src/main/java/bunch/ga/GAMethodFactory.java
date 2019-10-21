package bunch.ga;

import bunch.model.GenericFactory;

/**
 * A factory for different kinds of methods for the GA
 *
 * @author Diego Doval / Brian Mitchell
 * @version 1.0
 * @see GAMethod
 * @see GenericFactory
 */
public class GAMethodFactory
  extends GenericFactory
{

/**
 * Class constructor, defines the objects that the factory will be able
 * to create
 */
public GAMethodFactory()
{
  super();
  setFactoryType("GAMethod");
  addItem("tournament", "bunch.ga.GATournamentMethod");
  addItem("roulette wheel", "bunch.ga.GARouletteWheelMethod");
}

/**
 * Obtains the GA method corresponding to name passed as parameter.
 * Utility method that uses the #getItemInstance(java.lang.String) method
 * from GenericFactory and casts the object to a GAMethod object.
 *
 * @param the name for the desired method
 * @return the GA method corresponding to the name
 */
public
GAMethod
getMethod(String name)
{
  return (GAMethod)getItemInstance(name);
}
}
