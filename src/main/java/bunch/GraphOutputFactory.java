package bunch;

/**
 * A factory for graph output generators
 *
 * @author Brian Mitchell
 * @version 1.0
 * @see bunch.GraphOutput
 * @see bunch.GenericFactory
 */
public class GraphOutputFactory extends GenericFactory
{

public String defaultOption = "Dotty";
/**
 * Class constructor, defines the objects that the factory will be able
 * to create
 */
public GraphOutputFactory() {
  super();
  setFactoryType("GraphOutput");
  addItem("Dotty", "bunch.DotGraphOutput");
  addItem("Text", "bunch.TXTGraphOutput");
  addItem("GXL","bunch.gxl.io.GXLGraphOutput");
  //addItem("Tom Sawyer", "bunch.TSGraphOutput");
  //addItem("Text Tree","bunch.TXTTreeGraphOutput");
}

/**
 * Obtains the graph output generator corresponding to name passed as parameter.
 * Utility method that uses the #getItemInstance(java.lang.String) method
 * from GenericFactory and casts the object to a GraphOutput object.
 *
 * @param the name for the desired output generator
 * @return the graph output generator corresponding to the name
 */
public
GraphOutput
getOutput(String name)
{
  return (GraphOutput)getItemInstance(name);
}

public String getDefaultMethod()
{
  return this.defaultOption;
}
}
