package bunch.api;

import java.util.*;

public final class BunchCluster {

  int clusterID = -1;
  String clusterName = "";
  ArrayList clusterNodes = null;
  ArrayList overlapNodes = null;
  Hashtable nodeHT = null;

  public BunchCluster(int id, String name, ArrayList nodes) {
    clusterID = id;
    clusterName = name;
    clusterNodes = nodes;
    nodeHT = null;
    //inform member nodes that they are a primary member of this cluster
    for(int i = 0; i < nodes.size(); i++)
    {
      BunchNode bn = (BunchNode)nodes.get(i);
      bn.setMemberCluster(this);
    }
  }

  public int getSize()
  {
    if (clusterNodes == null) return 0;
    return clusterNodes.size();
  }

  public Collection getClusterNodes()
  { return clusterNodes;  }

  public Collection getOverlapNodes()
  { return overlapNodes;  }

  public int getOverlapNodeCount()
  {
    if(overlapNodes != null)
      return overlapNodes.size();
    else
      return 0;
  }

  public void addOverlapNode(BunchNode bn)
  {
    if (overlapNodes == null)
      overlapNodes = new ArrayList();

    overlapNodes.add(bn);
    nodeHT = null;
  }

  public void addNode(BunchNode bn)
  {
    bn.setMemberCluster(this);
    clusterNodes.add(bn);
  }

  public void removeNode(BunchNode bn)
  {
    bn.setMemberCluster(null);
    clusterNodes.remove(bn);
  }

  public int getID()
  { return clusterID; }

  public String getName()
  { return clusterName; }

  public boolean containsNode(BunchNode bn)
  {
    return containsNode(bn.getName());
  }

  public boolean containsNode(String nodeName)
  {
    if(nodeHT == null)
      nodeHT = constructNodeHT();

    return nodeHT.containsKey(nodeName);
  }

  private Hashtable constructNodeHT()
  {
    Hashtable h = new Hashtable();
    h.clear();

    if(clusterNodes != null)
    {
      for(int i = 0; i < clusterNodes.size(); i++)
      {
        BunchNode bn = (BunchNode)clusterNodes.get(i);
        String key = bn.getName();
        h.put(key,bn);
      }
    }
    if(overlapNodes != null)
    {
      for(int i = 0; i < overlapNodes.size(); i++)
      {
        BunchNode bn = (BunchNode)overlapNodes.get(i);
        String key = bn.getName();
        h.put(key,bn);
      }
    }
    return h;
  }
}