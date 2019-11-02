package bunch.api;

import bunch.model.Cluster;
import java.util.List;

final class EngineResults {
  public Double prPrecisionValue;
  public Double mqCalcResultValue;
  public Double prRecallValue;
  public long RUNTIME;
  public long MQEVALUATIONS;
  public int TOTAL_CLUSTER_LEVELS;
  public long SA_NEIGHBORS_TAKEN;
  public int MEDIAN_LEVEL_GRAPH;
  public int reflexiveEdgeCount;
  public List<Cluster> RESULT_CLUSTER_OBJS;
}
