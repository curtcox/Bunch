package bunch.model;

/**
 * A factory for graph output generators
 */
public class GraphOutputFactory extends GenericFactory {

public String defaultOption = "Dotty";
/**
 * Class constructor, defines the objects that the factory will be able
 * to create
 */
public GraphOutputFactory() {
  super();
  setFactoryType("GraphOutput");
  addItem("Dotty", "bunch.model.DotGraphOutput");
  addItem("Text", "bunch.model.TXTGraphOutput");
  addItem("GXL","bunch.gxl.io.GXLGraphOutput");
  //addItem("Tom Sawyer", "bunch.model.TSGraphOutput");
  //addItem("Text Tree","bunch.model.TXTTreeGraphOutput");
}

/**
 * Obtains the graph output generator corresponding to name passed as parameter.
 * Utility method that uses the #getItemInstance(java.lang.String) method
 * from GenericFactory and casts the object to a GraphOutput object.
 *
 * @param the name for the desired output generator
 * @return the graph output generator corresponding to the name
 */
public GraphOutput getOutput(String name)
{
  return (GraphOutput)getItemInstance(name);
}

public String getDefaultMethod()
{
  return this.defaultOption;
}
}
