package bunch.api;

final class RunModeMqCalc {

  private EngineArgs bunchArgs = new EngineArgs();
  private EngineResults results = new EngineResults();
  private Double MQCalcValue;

  private void runMQCalc() {
    String MQCalcMdgFileName = bunchArgs.MQCALC_MDG_FILE;
    String MQCalcSilFileName = bunchArgs.MQCALC_SIL_FILE;
    var MQCalcClass = bunchArgs.mqCalculatorClass;

    MQCalcValue = bunch.util.MQCalculator.CalcMQ(MQCalcMdgFileName, MQCalcSilFileName,MQCalcClass);
  }

  private EngineResults getMQCalcResultsHT() {
    results = new EngineResults();
    if (MQCalcValue == null)
      return null;

    results.mqCalcResultValue = MQCalcValue;
    return results;
  }

}
