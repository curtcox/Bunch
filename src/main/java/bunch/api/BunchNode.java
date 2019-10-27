package bunch.api;

import java.util.*;

public final class BunchNode {

  static public final int NOT_A_MEMBER_OF_A_CLUSTER = -1;

  String nodeName;
  int    nodeIndex;
  int    nodeCluster;
  BunchCluster memberCluster = null;
  boolean isNodeCluster;
  ArrayList   deps = null;
  ArrayList   backDeps = null;
  HashMap     clusterMemberships;

  public BunchNode(String name, int index, int cluster,  boolean isCluster) {
    nodeName = name;
    nodeIndex = index;
    nodeCluster = cluster;
    isNodeCluster = isCluster;
    clusterMemberships = new HashMap();
  }

  public void subscribeToCluster(BunchCluster bc) {
    if(bc != null)
      clusterMemberships.put(bc.getName(),bc);
  }

  public boolean isAMemberOfCluster(String name)
  { return clusterMemberships.containsKey(name); }

  public boolean isAMemberOfCluster(BunchCluster bc)
  { return isAMemberOfCluster(bc.getName()); }

  public int memberOfHowManyClusters()
  { return clusterMemberships.size(); }
  public void setDeps(ArrayList deps, ArrayList backDeps)
  {
    this.deps = deps;
    this.backDeps = backDeps;
  }

  public String getName()
  { return nodeName;  }

  public int  getCluster()
  { return nodeCluster; }

  public void resetCluster(int newClustNumber)
  { nodeCluster = newClustNumber; }

  public Collection getDeps()
  { return deps;  }

  public Collection getBackDeps()
  { return backDeps;  }

  public boolean isCluster()
  { return isNodeCluster; }

  public BunchCluster getMemberCluster()
  { return memberCluster; }

  public void setMemberCluster(BunchCluster bc) {
    memberCluster = bc;
    subscribeToCluster(bc);
  }
}