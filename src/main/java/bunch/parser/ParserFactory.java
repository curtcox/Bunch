package bunch.parser;

import bunch.GenericFactory;
import bunch.parser.Parser;

/**
 * A factory for parsers of different kinds
 */
public class ParserFactory
  extends GenericFactory
{

public
ParserFactory()
{
  super();
  setFactoryType("Parser");
  addItem("dependency", "bunch.parser.DependencyFileParser");
  addItem("gxl", "bunch.gxl.parser.GXLGraphParser");
  addItem("cluster", "bunch.parser.ClusterFileParser");
}

public Parser
getParser(String name)
{
  return (Parser)getItemInstance(name);
}
}
