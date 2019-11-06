package bunch.output;

import bunch.output.DotGraphOutput;
import bunch.output.GraphOutput;

/**
 * A factory for graph output generators
 */
public final class GraphOutputFactory {

public final GraphOutput defaultOption = new DotGraphOutput();

}
