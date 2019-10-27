package bunch.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

final class MeCl {
  BunchGraph A;
  BunchGraph B;
  HashMap edgeA;
  long  meclValue;

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

    Iterator i = Ca.values().iterator();
    while(i.hasNext()) {
      HashMap Ci = (HashMap)i.next();
      Iterator j = Ci.keySet().iterator();
      while(j.hasNext()) {
        String key = (String)j.next();
        ArrayList value = (ArrayList)Ci.get(key);
        tally+=mergeSubCluster(Ccollected,key,value);
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

    for(int i = 0; i < currentSubCluster.size(); i++) {
      BunchNode bn1 = (BunchNode)currentSubCluster.get(i);
      for(int j = 0; j < value.size(); j++) {
        BunchNode bn2 = (BunchNode)value.get(j);
        if (!bn2.isAMemberOfCluster(bn1.getMemberCluster().getName())) {
          tally += this.getConnectedWeight(bn1.getName(),bn2.getName());
        }
      }
    }
    currentSubCluster.addAll(value);
    return tally;
  }

  private void constructEdgeSet() {
    Iterator i = A.getEdges().iterator();

    while(i.hasNext()) {
      BunchEdge be = (BunchEdge)i.next();
      String key = be.getSrcNode().getName() + be.getDestNode().getName();
      Integer weight = new Integer(be.getWeight());
      edgeA.put(key,weight);
    }
  }

  public int getConnectedWeight(String n1, String n2) {
    String key1 = n1+n2;
    String key2 = n2+n1;
    int    total = 0;

    Integer forward = (Integer)edgeA.get(key1);
    if(forward != null){
      total += forward.intValue();
    }

    Integer reverse = (Integer)edgeA.get(key2);
    if(reverse != null) {
      total += reverse.intValue();
    }

    return total;
  }

  public HashMap determineSubClusters() {
    HashMap Ca = new HashMap();

    Iterator i = A.getClusters().iterator();
    while(i.hasNext()) {
      HashMap subClustersA = new HashMap();
      BunchCluster Ai = (BunchCluster)i.next();
      Iterator j = Ai.getClusterNodes().iterator();
      while(j.hasNext()) {
        BunchNode bnInA = (BunchNode)j.next();
        String nodeName = bnInA.getName();

        //Now find this node in the B graph
        BunchNode bnInB = B.findNode(nodeName);
        String    bnInBClusterName = bnInB.getMemberCluster().getName();
        //---------------------------

        //Now add the current node to the sub cluster
        //hash map for the current cluster in a
        ArrayList members = (ArrayList)subClustersA.get(bnInBClusterName);
        if(members == null)
        {
          members = new ArrayList();
          subClustersA.put(bnInBClusterName,members);
        }
        members.add(bnInA);
        //Now find the appropriate
      }

      Ca.put(Ai.getName(),subClustersA);
    }
    return Ca;
  }
}
