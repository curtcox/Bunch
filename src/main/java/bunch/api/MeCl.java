package bunch.api;

import java.util.*;

final class MeCl {
  private final BunchGraph A;
  private final BunchGraph B;
  private final Map<String,Integer> edgeA;
  private long  meclValue;

  public MeCl(BunchGraph g1, BunchGraph g2) {
    A = g1;
    B = g2;
    edgeA = new HashMap<>();
    edgeA.clear();
    meclValue = 0;
  }

  public long run() {
    edgeA.clear();
    HashMap Ca = determineSubClusters();

    constructEdgeSet();

    meclValue = collectSubClusters(Ca);

    return meclValue;
  }

  public double getQualityMetric() {
    int edgeCount = A.getEdges().size();
    double pct = ((double)meclValue / (double)edgeCount);
    return (1.0 - pct);
  }

  private long collectSubClusters(HashMap Ca) {
    long tally=0;
    HashMap<String,List> Ccollected = new HashMap<>();
    Ccollected.clear();

    for (var item : Ca.values()) {
      HashMap<String,List> Ci = (HashMap) item;
      for (var o : Ci.keySet()) {
        String key = o;
        var value = Ci.get(key);
        tally += mergeSubCluster(Ccollected, key, value);
      }
    }
    return tally;
  }

  private long mergeSubCluster(HashMap<String,List> Ccollected, String key, List<String> value) {
    long tally = 0;

    List currentSubCluster = Ccollected.get(key);
    if(currentSubCluster == null) {
      Ccollected.put(key,value);
      return 0;
    }

    for (Object item : currentSubCluster) {
      BunchNode bn1 = (BunchNode) item;
      for (Object o : value) {
        BunchNode bn2 = (BunchNode) o;
        if (!bn2.isAMemberOfCluster(bn1.getMemberCluster().getName())) {
          tally += this.getConnectedWeight(bn1.getName(), bn2.getName());
        }
      }
    }
    currentSubCluster.addAll(value);
    return tally;
  }

  private void constructEdgeSet() {

    for (BunchEdge be : A.getEdges()) {
      String key = be.getSrcNode().getName() + be.getDestNode().getName();
      edgeA.put(key, be.getWeight());
    }
  }

  private int getConnectedWeight(String n1, String n2) {
    String key1 = n1+n2;
    String key2 = n2+n1;
    int    total = 0;

    Integer forward = edgeA.get(key1);
    if(forward != null){
      total += forward;
    }

    Integer reverse = edgeA.get(key2);
    if(reverse != null) {
      total += reverse;
    }

    return total;
  }

  private HashMap determineSubClusters() {
    HashMap<String,Map> Ca = new HashMap<>();

    for (BunchCluster bunchCluster : A.getClusters()) {
      HashMap<String,List> subClustersA = new HashMap<>();
        for (Object o : bunchCluster.getClusterNodes()) {
        BunchNode bnInA = (BunchNode) o;
        String nodeName = bnInA.getName();

        //Now find this node in the B graph
        BunchNode bnInB = B.findNode(nodeName);
        String bnInBClusterName = bnInB.getMemberCluster().getName();
        //---------------------------

        //Now add the current node to the sub cluster
        //hash map for the current cluster in a
          List members = subClustersA.computeIfAbsent(bnInBClusterName, k -> new ArrayList());
          members.add(bnInA);
        //Now find the appropriate
      }

      Ca.put(bunchCluster.getName(), subClustersA);
    }
    return Ca;
  }
}
