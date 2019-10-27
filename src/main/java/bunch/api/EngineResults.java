package bunch.api;

import java.util.Map;

final class EngineResults {
  public String prPrecisionValue;
  public String mqCalcResultValue;
  public String prRecallValue;
  public long RUNTIME;
  public long MQEVALUATIONS;
  public int TOTAL_CLUSTER_LEVELS;
  public long SA_NEIGHBORS_TAKEN;
  public int MEDIAN_LEVEL_GRAPH;
  public Map ERROR_HASHTABLE;
  public Map WARNING_HASHTABLE;
  public Map[] RESULT_CLUSTER_OBJS;
}
