package bunch.precisionrecall;

public final class PrecisionRecallEngine {

  public static class Results {
    public final double precision;
    public final double recall;

    Results(double precision, double recall) {
      this.precision = precision;
      this.recall = recall;
    }
  }

  public Results run(String clusterFile,String expertFile) {
    var calc = new PrecisionRecallCalculator(expertFile,clusterFile);
    return new Results(calc.get_precision(),calc.get_recall());
  }

}
