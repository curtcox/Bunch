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
    edgeA = new HashMap();
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
    HashMap Ccollected = new HashMap();
    Ccollected.clear();

    for (Object item : Ca.values()) {
      HashMap Ci = (HashMap) item;
      for (Object o : Ci.keySet()) {
        String key = (String) o;
        ArrayList value = (ArrayList) Ci.get(key);
        tally += mergeSubCluster(Ccollected, key, value);
      }
    }
    return tally;
  }

  private long mergeSubCluster(HashMap Ccollected, String key, ArrayList value) {
    long tally = 0;

    ArrayList currentSubCluster = (ArrayList)Ccollected.get(key);
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
    HashMap Ca = new HashMap();

    for (BunchCluster bunchCluster : A.getClusters()) {
      HashMap subClustersA = new HashMap();
      BunchCluster Ai = bunchCluster;
      for (Object o : Ai.getClusterNodes()) {
        BunchNode bnInA = (BunchNode) o;
        String nodeName = bnInA.getName();

        //Now find this node in the B graph
        BunchNode bnInB = B.findNode(nodeName);
        String bnInBClusterName = bnInB.getMemberCluster().getName();
        //---------------------------

        //Now add the current node to the sub cluster
        //hash map for the current cluster in a
        List members = (ArrayList) subClustersA.get(bnInBClusterName);
        if (members == null) {
          members = new ArrayList();
          subClustersA.put(bnInBClusterName, members);
        }
        members.add(bnInA);
        //Now find the appropriate
      }

      Ca.put(Ai.getName(), subClustersA);
    }
    return Ca;
  }
}
