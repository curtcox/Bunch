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
    meclValue = 0;
  }

  public long run() {
    edgeA.clear();
    var Ca = determineSubClusters();

    constructEdgeSet();

    meclValue = collectSubClusters(Ca);

    return meclValue;
  }

  public double getQualityMetric() {
    int edgeCount = A.getEdges().size();
    double pct = ((double)meclValue / (double)edgeCount);
    return (1.0 - pct);
  }

  private long collectSubClusters(HashMap<String,HashMap<String,List<BunchNode>>> Ca) {
    long tally=0;
    HashMap<String,List<BunchNode>> Ccollected = new HashMap<>();

    for (var item : Ca.values()) {
      for (var o : item.keySet()) {
        var value = item.get(o);
        tally += mergeSubCluster(Ccollected, o, value);
      }
    }
    return tally;
  }

  private long mergeSubCluster(HashMap<String,List<BunchNode>> Ccollected, String key, List<BunchNode> value) {
    long tally = 0;

    var currentSubCluster = Ccollected.get(key);
    if(currentSubCluster == null) {
      Ccollected.put(key,value);
      return 0;
    }

    for (var item : currentSubCluster) {
      for (var o : value) {
        if (!o.isAMemberOfCluster(item.getMemberCluster().getName())) {
          tally += this.getConnectedWeight(item.getName(), o.getName());
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
          List<BunchNode> members = subClustersA.computeIfAbsent(bnInBClusterName, k -> new ArrayList<>());
          members.add(bnInA);
        //Now find the appropriate
      }

      Ca.put(bunchCluster.getName(), subClustersA);
    }
    return Ca;
  }
}
