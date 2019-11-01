package bunch.model;

import java.util.*;
import java.beans.Beans;

/**
 * A generic factory class. This class should be used as superclass
 * for specific (i.e., typed) factory classes that have to abstract
 * the creation of types that are generalized through an interface.<P>
 * Because factory classes are useful also as configuration objects
 * GenericFactory implements the serializable interface. Therefore,
 * classes that subclass it must be careful to specify as transient any
 * member that they do not want to be persistent.
 *
 * @author Brian Mitchell
 */
public class GenericFactory implements java.io.Serializable {

private final Hashtable<String,String> methodTable_d;
public static final long serialVersionUID = 100L;

/**
 * The Factory Type is the name of the abstract class that will be
 * used by the objects stored in the factory. It is used by the
 * #getItemInstance(String) method to obtain the FQN for
 * the default class for the particular factory in question
 */
private String factoryType_d;

protected GenericFactory() {
  methodTable_d = new Hashtable<>();
}

/**
 * Sets the type of factory of this class, normally the name of the class
 * of objects that will be stored in this factory
 *
 * @param name of the class
 */
protected void setFactoryType(String name)
{
  factoryType_d = name;
}


/**
 * Add a new object to the factory with its corresponding key name.
 *
 * @param name the object's key name
 * @param className the name of the class that will be instanced to answer for
 * this object
 */
protected void addItem(String name, String className)
{
  methodTable_d.put(name, className);
}

/**
 * Obtains an instance of an item in the factory.
 *
 * @param name the name of the object to be retrieved
 * @return an instance of the name that corresponds to the key passed
 * as parameter
 */
protected Object getItemInstance(String name) {
  String cls;
  if (name.toLowerCase().equals("default")) {
    cls = "bunch.Default"+factoryType_d;
  } else {
    cls = methodTable_d.get(name);
  }

  try {
    return (Beans.instantiate(null, cls));
  } catch (Exception e) {
    return getItemInstanceFromClass(name);
  }
}

/**
 * Get object instance using a class name as a key
 */
private Object getItemInstanceFromClass(String cls) {
  try {
    return (Beans.instantiate(null, cls));
  } catch (Exception e) {
    throw new RuntimeException(e);
  }
}

/**
 * This method returns the default factory entry.  If one is not
 * overriden in a concreate class, then a null string will be
 * returned
 */
public Object getDefaultMethod()
{
  return null;
}

}
