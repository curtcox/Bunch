package bunch.api;

final class RunModePrCalc {

  private EngineArgs bunchArgs = new EngineArgs();
  private EngineResults results = new EngineResults();
  private Double precision;
  private Double recall;

  private void runPRCalc() {
    String clusterF = bunchArgs.PR_CLUSTER_FILE;
    String expertF = bunchArgs.PR_EXPERT_FILE;

    bunch.util.PrecisionRecallCalculator calc =
      new bunch.util.PrecisionRecallCalculator(expertF,clusterF);

    precision = calc.get_precision();
    recall = calc.get_recall();
  }

  private EngineResults getPRResultsHT() {
    results = new EngineResults();
    if ((precision == null) || (recall == null))
      return null;

    results.prPrecisionValue = precision;
    results.prRecallValue = recall;
    return results;
  }

}
