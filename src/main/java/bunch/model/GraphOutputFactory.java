package bunch.model;

import bunch.api.OutputFormat;
import static bunch.api.OutputFormat.*;

/**
 * A factory for graph output generators
 */
public final class GraphOutputFactory {

private final GraphOutput defaultOption = new DotGraphOutput();

/**
 * Obtains the graph output generator corresponding to name passed as parameter.
 * Utility method that uses the #getItemInstance(java.lang.String) method
 * from GenericFactory and casts the object to a GraphOutput object.
 *
 * @param name for the desired output generator
 * @return the graph output generator corresponding to the name
 */
public GraphOutput getOutput(OutputFormat name) {
  if (name == DOT) { return new DotGraphOutput(); }
  if (name == TEXT) { return new TXTGraphOutput(); }
  return defaultOption;
}

}
