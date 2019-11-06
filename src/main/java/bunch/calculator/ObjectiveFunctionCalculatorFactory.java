package bunch.calculator;

/**
 * A factory for different kinds of objective function calculator objects
 *
 * @author Diego Doval
 * @version 1.0
 * @see ObjectiveFunctionCalculator
 */
public final class ObjectiveFunctionCalculatorFactory {
  private final ObjectiveFunctionCalculator defaultMethod = new TurboMQIncrW();

/**
 * This method returns the default clustering method.  It is used in the GUI and
 * API when the clustering algorithm is not explicitly specified.
 */
public ObjectiveFunctionCalculator getDefaultMethod() {
  return defaultMethod;
}

}