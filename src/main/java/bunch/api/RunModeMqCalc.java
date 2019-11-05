package bunch.api;

import bunch.model.ClusterList;
import bunch.model.Graph;

public final class RunModeMqCalc
  implements RunMode
{
  private Double MQCalcValue;

  public EngineResults run(EngineArgs bunchArgs) {
    String MQCalcMdgFileName = bunchArgs.MQCALC_MDG_FILE;
    String MQCalcSilFileName = bunchArgs.MQCALC_SIL_FILE;
    var MQCalcClass = bunchArgs.mqCalculatorClass;

    MQCalcValue = bunch.util.MQCalculator.CalcMQ(MQCalcMdgFileName, MQCalcSilFileName,MQCalcClass);
    return getResults();
  }

  private EngineResults getResults() {
    if (MQCalcValue == null) {
      throw new IllegalArgumentException();
    }

    var results = new EngineResults();
    results.mqCalcResultValue = MQCalcValue;
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
