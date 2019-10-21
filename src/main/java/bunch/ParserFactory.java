package bunch;

/**
 * A factory for parsers of different kinds
 *
 * @author Diego Doval
 * @version 1.0
 * @see bunch.Parser
 * @see bunch.GenericFactory
 */
public class ParserFactory
  extends GenericFactory
{

public
ParserFactory()
{
  super();
  setFactoryType("Parser");
  addItem("dependency", "bunch.DependencyFileParser");
  addItem("gxl", "bunch.gxl.parser.GXLGraphParser");
  addItem("cluster", "bunch.ClusterFileParser");
}

public
Parser
getParser(String name)
{
  return (Parser)getItemInstance(name);
}
}
