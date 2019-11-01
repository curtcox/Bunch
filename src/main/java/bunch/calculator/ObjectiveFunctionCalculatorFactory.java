package bunch.calculator;

import bunch.model.GenericFactory;

/**
 * A factory for different kinds of objective function calculator objects
 *
 * @author Diego Doval
 * @version 1.0
 * @see ObjectiveFunctionCalculator
 * @see GenericFactory
 */
public final class ObjectiveFunctionCalculatorFactory extends GenericFactory {
  private ObjectiveFunctionCalculator currObjFnMethod = new TurboMQIncrW();
  private final ObjectiveFunctionCalculator defaultMethod = new TurboMQIncrW();

/**
 * Class constructor, defines the objects that the factory will be able
 * to create
 */
public ObjectiveFunctionCalculatorFactory() {
  super();
  setFactoryType("ObjectiveFunctionCalculator");
  addItem("Basic MQ Function", BasicMQ.class.getName());
  addItem("Turbo MQ Function", TurboMQ.class.getName());
  //addItem("Incremental MQ", "bunch.calculator.TurboMQIncr");
  addItem("Incremental MQ Weighted", TurboMQIncrW.class.getName());

  addItem("bunch.calculator.BasicMQ", BasicMQ.class.getName());
  addItem("bunch.calculator.TurboMQ", TurboMQ.class.getName());
  addItem("bunch.ITurboMQ", TurboMQIncrW.class.getName());
  addItem("bunch.calculator.TurboMQIncrW", TurboMQIncrW.class.getName());

  //addItem("Turbo MQ Squared", "bunch.calculator.TurboMQ2");
  //addItem("Experimental Weighted 2", "bunch.calculator.WeightedObjectiveFunctionCalculator2");
  //addItem("Experimental Weighted 3", "bunch.calculator.WeightedObjectiveFunctionCalculator3");
  //addItem("Spiros MQ", "bunch.calculator.SpirosMQ");
}

/**
 * Obtains the OF Calculator corresponding to name passed as parameter.
 * Utility method that uses the #getItemInstance(java.lang.String) method
 * from GenericFactory and casts the object to a ObjectiveFunctionCalculator object.
 *
 * @param name for the desired method
 * @return the OF Calculator corresponding to the name
 */
public ObjectiveFunctionCalculator getCalculator(String name) {
  return (ObjectiveFunctionCalculator)getItemInstance(name);
}

public ObjectiveFunctionCalculator getSelectedCalculator() {
  return currObjFnMethod;
}

/**
 * This method returns the default clustering method.  It is used in the GUI and
 * API when the clustering algorithm is not explicitly specified.
 */
public ObjectiveFunctionCalculator getDefaultMethod() {
  return defaultMethod;
}

public ObjectiveFunctionCalculator getCurrentCalculator() {
   return currObjFnMethod;
}

public void setCurrentCalculator(ObjectiveFunctionCalculator sCalc)
{
   currObjFnMethod = sCalc;
}

}