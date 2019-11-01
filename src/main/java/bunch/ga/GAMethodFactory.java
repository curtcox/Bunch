package bunch.ga;

import bunch.api.GaSelection;
import bunch.model.GenericFactory;

import static bunch.api.GaSelection.*;

/**
 * A factory for different kinds of methods for the GA
 *
 * @author Diego Doval / Brian Mitchell
 * @version 1.0
 * @see GAMethod
 * @see GenericFactory
 */
public final class GAMethodFactory {

  public final GAMethod defaultMethod = new GATournamentMethod();

  /**
 * Obtains the GA method corresponding to name passed as parameter.
 * Utility method that uses the #getItemInstance(java.lang.String) method
 * from GenericFactory and casts the object to a GAMethod object.
 *
 * @param name for the desired method
 * @return the GA method corresponding to the name
 */
public GAMethod getMethod(GaSelection name) {
  if (name == ROULETTE)   return new GARouletteWheelMethod();
  if (name == TOURNAMENT) return new GATournamentMethod();
  return defaultMethod;
}

}
