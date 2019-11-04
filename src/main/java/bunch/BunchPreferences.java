package bunch;

import bunch.calculator.ObjectiveFunctionCalculatorFactory;
import bunch.clustering.ClusteringMethodFactory;
import bunch.model.GraphOutputFactory;
import bunch.parser.ParserFactory;

/**
 * This class contains the settings used by Bunchv2. (Currently most of
 * the options are wired at compile time. However, the structure exists to
 * make this configuration loadable simply by providing a dialog box
 * that lets the user configure the options and then store them in a
 * java Serialized object)
 *
 * @author Brian Mitchell
 */
public class BunchPreferences
{

/**
 * The main entities for managing the clustering process are the clustering
 * method, objective function, parser and output factories.  This class keeps
 * track as a global singleton of the factory instances.
 */
private final ClusteringMethodFactory methodFactory_d;

/**
 * class constructor.  Create the factory objects.
 */
public BunchPreferences() {
  methodFactory_d =  new ClusteringMethodFactory();
}

}
