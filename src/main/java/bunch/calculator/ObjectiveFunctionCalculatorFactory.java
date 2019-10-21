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
public class ObjectiveFunctionCalculatorFactory extends GenericFactory {
String currObjFnMethod = "Incremental MQ Weighted";
String defaultMethod = "Incremental MQ Weighted";

/**
 * Class constructor, defines the objects that the factory will be able
 * to create
 */
public
ObjectiveFunctionCalculatorFactory()
{
  super();
  setFactoryType("ObjectiveFunctionCalculator");
  addItem("Basic MQ Function", "bunch.calculator.BasicMQ");
  addItem("Turbo MQ Function", "bunch.calculator.TurboMQ");
  //addItem("Incremental MQ", "bunch.calculator.TurboMQIncr");
  addItem("Incremental MQ Weighted", "bunch.calculator.TurboMQIncrW");

  addItem("bunch.calculator.BasicMQ", "bunch.calculator.BasicMQ");
  addItem("bunch.calculator.TurboMQ", "bunch.calculator.TurboMQ");
  addItem("bunch.ITurboMQ", "bunch.calculator.TurboMQIncrW");
  addItem("bunch.calculator.TurboMQIncrW", "bunch.calculator.TurboMQIncrW");

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
 * @param the name for the desired method
 * @return the OF Calculator corresponding to the name
 */
public
ObjectiveFunctionCalculator
getCalculator(String name)
{
  return (ObjectiveFunctionCalculator)getItemInstance(name);
}

public
ObjectiveFunctionCalculator
getSelectedCalculator()
{
  return (ObjectiveFunctionCalculator)getItemInstance(currObjFnMethod);
}

/**
 * This method returns the default clustering method.  It is used in the GUI and
 * API when the clustering algorithm is not explicitly specified.
 */
public String getDefaultMethod()
{
  return defaultMethod;
}

public
String
getCurrentCalculator()
{
   return currObjFnMethod;
}

public
void
setCurrentCalculator(String sCalc)
{
   currObjFnMethod = sCalc;
}

}