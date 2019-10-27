package bunch.api;

import java.util.Map;

final class EngineResults {
  public String prPrecisionValue;
  public String mqCalcResultValue;
  public String prRecallValue;
  public Long RUNTIME;
  public Long MQEVALUATIONS;
  public Integer TOTAL_CLUSTER_LEVELS;
  public Long SA_NEIGHBORS_TAKEN;
  public Integer MEDIAN_LEVEL_GRAPH;
  public Map ERROR_HASHTABLE;
  public Map WARNING_HASHTABLE;
  public Map[] RESULT_CLUSTER_OBJS;
}
