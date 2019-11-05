package bunch.api;

import bunch.model.ClusterList;
import bunch.model.Graph;
import bunch.util.PrecisionRecallCalculator;

public final class RunModePrCalc
  implements RunMode
{

  private Double precision;
  private Double recall;

  public EngineResults run(EngineArgs bunchArgs) {
    String clusterF = bunchArgs.PR_CLUSTER_FILE;
    String expertF = bunchArgs.PR_EXPERT_FILE;

    var calc = new PrecisionRecallCalculator(expertF,clusterF);
    precision = calc.get_precision();
    recall = calc.get_recall();
    return getResults();
  }

  private EngineResults getResults() {
    var results = new EngineResults();
    if ((precision == null) || (recall == null)) {
      throw new IllegalArgumentException();
    }

    results.prPrecisionValue = precision;
    results.prRecallValue = recall;
    return results;
  }

  @Override
  public Graph getBestGraph() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ClusterList getClusterList() {
    throw new UnsupportedOperationException();
  }

}
