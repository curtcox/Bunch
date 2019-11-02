package bunch.api;

import java.util.*;

public final class BunchCluster {

  private int clusterID = -1;
  private String clusterName = "";
  private final List<BunchNode> clusterNodes;
  private List<BunchNode> overlapNodes;
  private Map<String,BunchNode> nodeHT;

  public BunchCluster(int id, String name, List<BunchNode> nodes) {
    clusterID = id;
    clusterName = name;
    clusterNodes = nodes;
    nodeHT = null;
    //inform member nodes that they are a primary member of this cluster
    for (BunchNode node : nodes) {
        node.setMemberCluster(this);
    }
  }

  public int getSize() {
    if (clusterNodes == null) return 0;
    return clusterNodes.size();
  }

  public Collection<BunchNode> getClusterNodes()
  { return clusterNodes;  }

  public int getOverlapNodeCount() {
    if(overlapNodes != null)
      return overlapNodes.size();
    else
      return 0;
  }

  public void addOverlapNode(BunchNode bn) {
    if (overlapNodes == null)
      overlapNodes = new ArrayList<>();

    overlapNodes.add(bn);
    nodeHT = null;
  }

  public int getID()
  { return clusterID; }

  public String getName()
  { return clusterName; }

  public boolean containsNode(String nodeName) {
    if(nodeHT == null)
      nodeHT = constructNodeHT();

    return nodeHT.containsKey(nodeName);
  }

  private Map<String,BunchNode> constructNodeHT() {
    Map<String,BunchNode> h = new Hashtable<>();

      if(clusterNodes != null) {
      for (BunchNode bn : clusterNodes) {
        String key = bn.getName();
        h.put(key, bn);
      }
    }
    if(overlapNodes != null) {
      for (BunchNode bn : overlapNodes) {
        String key = bn.getName();
        h.put(key, bn);
      }
    }
    return h;
  }
}