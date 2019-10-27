package bunch.api;

import bunch.model.Graph;
import bunch.model.NextLevelGraph;
import bunch.model.Node;

import  java.util.*;
import  java.io.*;

public final class BunchGraph {

  ArrayList nodeList = null;
  ArrayList edgeList = null;
  ArrayList clusterList = null;
  Hashtable nodeHT = null;
  boolean includesIsomorphicUpdates = false;

  double mqValue = 0;
  int    numClusters = 0;

  //default constructor
  public BunchGraph() { }

  public Collection getNodes()
  {
    return nodeList;
  }

  public int getTotalOverlapNodes() {
    int total = 0;
    if(clusterList != null) {
      for(int i = 0; i < clusterList.size(); i++) {
        BunchCluster bc = (BunchCluster)clusterList.get(i);
        total += bc.getOverlapNodeCount();
      }
    }
    return total;
  }

  public Collection getEdges()
  {
    return edgeList;
  }

  public Collection getClusters()
  { return clusterList; }

  public double getMQValue()
  { return mqValue; }

  public int getNumClusters()
  {
    return numClusters;
  }

  public void writeSILFile(String fname) throws java.io.IOException {
    writeSILFile(fname,false);
  }

  public BunchNode findNode(String nodeName) {
    if(nodeHT == null)
      nodeHT = constructNodeHT();

    return (BunchNode)nodeHT.get(nodeName);
  }

  private Hashtable constructNodeHT() {
    Hashtable h = new Hashtable();
    h.clear();
    for(int i = 0; i < nodeList.size(); i++) {
      BunchNode theNode = (BunchNode)nodeList.get(i);
      String key = theNode.getName();
      h.put(key,theNode);
    }
    return h;
  }

  public void writeSILFile(String fname, boolean includeOverlapNodes)
    throws java.io.IOException {
    FileWriter outF = new FileWriter(fname);
    java.io.BufferedWriter out = new BufferedWriter(outF);

    for(int i = 0; i < clusterList.size(); i++) {
      BunchCluster bc = (BunchCluster)clusterList.get(i);
      ArrayList clusterNodes = new ArrayList(bc.getClusterNodes());
      if(clusterNodes.size()==0)
        continue;
      out.write("SS("+bc.getName()+")=");
      for(int j = 0; j < clusterNodes.size(); j++) {
        BunchNode bn = (BunchNode)clusterNodes.get(j);
        out.write(bn.getName());
        if(j < (clusterNodes.size()-1))
          out.write(", ");
      }
      if((includeOverlapNodes == true)&&(bc.getOverlapNodes()!=null)) {
        ArrayList overlapNodes = new ArrayList(bc.getOverlapNodes());
        if(overlapNodes.size()>0)
          out.write(", ");

        for(int j = 0; j < overlapNodes.size(); j++) {
          BunchNode bn = (BunchNode)overlapNodes.get(j);
          out.write(bn.getName());
          if(j < (overlapNodes.size()-1))
            out.write(", ");
        }
      }
      out.write("\r\n");
    }
    out.close();
  }

  private ArrayList getChildrenList(Node n) {
    ArrayList a = new ArrayList();
    if (n.isCluster() == false) {
      a.add(n);
      return a;
    }
    Stack s = new Stack();
    s.push(n);
    while(s.isEmpty() == false) {
      Node c = (Node)s.pop();
      Node []childrenList = c.children;
      for (int i = 0; i < childrenList.length; i++) {
        Node aChild = childrenList[i];
        if (aChild.isCluster() == true)
          s.push(aChild);
        else
          a.add(aChild);
      }
    }
    return a;
  }

  public void determineIsomorphic() {
    if (includesIsomorphicUpdates == true)
      return;

    includesIsomorphicUpdates = true;
    Iterator nodeI = getNodes().iterator();
    ArrayList theClusters = new ArrayList(getClusters());
    int adjustCount = 0;
    int nodeAdjustCount = 0;
    int totalCount = getNodes().size();
    boolean nodeIsomorphic = false;

    while(nodeI.hasNext()) {
      BunchNode bn = (BunchNode)nodeI.next();
      nodeIsomorphic = false;
      int[] cv = howConnected(bn);

      int currClust = bn.getCluster();
      int currStrength = cv[currClust];
      BunchCluster homeCluster = (BunchCluster)theClusters.get(currClust);
      for(int i = 0; i < cv.length; i++)
      {
        if(i == currClust) continue;
        int connectStrength = cv[i];
        if(connectStrength == currStrength)
        {
          BunchCluster bc = (BunchCluster)theClusters.get(i);
          bc.addOverlapNode(bn);
          bn.subscribeToCluster(bc);
          adjustCount++;
          nodeIsomorphic = true;

        }
      }
      if(nodeIsomorphic == true) nodeAdjustCount++;
    }
  }

  private int[] howConnected(BunchNode bn) {
    int howManyClusters = getClusters().size();
    int [] connectVector = new int[howManyClusters];
    Iterator fdeps = null;
    Iterator bdeps = null;

    for(int i=0; i<connectVector.length;i++)
      connectVector[i] = 0;

    if (bn.getDeps() != null) {
      fdeps = bn.getDeps().iterator();
      while(fdeps.hasNext()) {
        BunchEdge be = (BunchEdge)fdeps.next();
        BunchNode target = be.getDestNode();
        int targetCluster = target.getCluster();
        connectVector[targetCluster]++;
      }
    }


    if (bn.getBackDeps() != null) {
      bdeps = bn.getBackDeps().iterator();
      while(bdeps.hasNext()) {
        BunchEdge be = (BunchEdge)bdeps.next();
        BunchNode target = be.getSrcNode();
        int targetCluster = target.getCluster();
        connectVector[targetCluster]++;
      }
    }

    return connectVector;
  }

  public boolean construct(Graph gBase) {
    Graph g = gBase.getDetailedGraph();

    nodeList = new ArrayList();
    edgeList = new ArrayList();
    clusterList = new ArrayList();

    Node [] graphNodes = g.getNodes();
    int  [] clustVector = g.getClusters();

    if (graphNodes.length != clustVector.length)
      return false;

    //Build the node list to create all of the BunchNode objects
    for(int i = 0; i < graphNodes.length; i++) {
      Node n = graphNodes[i];
      BunchNode bn = new BunchNode(n.getName(),i,
          clustVector[i], n.isCluster());
      nodeList.add(i,bn);
    }

    //Now build the bunchEdge objects and attach them to the nodes
    for(int i = 0; i < graphNodes.length; i++) {
      Node n = graphNodes[i];
      int [] deps = n.getDependencies();
      int [] weights = n.getWeights();
      int [] backDeps = n.getBackEdges();
      int [] backWeights = n.getBeWeights();
      if ((deps != null) && (deps.length != weights.length))
        return false;
      if ((backDeps != null) && (backDeps.length != backWeights.length))
        return false;

      ArrayList forwardList = null;
      ArrayList backList = null;

      if (deps != null)
        forwardList = new ArrayList();

      for(int j = 0; j < deps.length; j++) {
        int edgeWeight = weights[j];
        int srcIdx = i;
        int destIdx = deps[j];
        BunchEdge be = new BunchEdge(edgeWeight,
          (BunchNode)nodeList.get(srcIdx),
          (BunchNode)nodeList.get(destIdx));
        edgeList.add(be);
        forwardList.add(be);
      }
      if (backDeps != null)
        backList = new ArrayList();

      for(int j = 0; j < backDeps.length; j++) {
        int edgeWeight = backWeights[j];
        int srcIdx = backDeps[j];
        int destIdx = i;
        BunchEdge be = new BunchEdge(edgeWeight,
          (BunchNode)nodeList.get(srcIdx),
          (BunchNode)nodeList.get(destIdx));

        backList.add(be);
      }

      BunchNode bn = (BunchNode)nodeList.get(i);
      bn.setDeps(forwardList,backList);
    }

    mqValue = g.getObjectiveFunctionValue(); //c.getObjFnValue();
    int [] cnames = g.getClusterNames();
    numClusters = cnames.length; //  c.getClusterNames().length;

    //Now construct the cluster objects
    Graph nextLvlG = null;
    Graph cLvlG = gBase.cloneGraph();

    NextLevelGraph nextLvl = new NextLevelGraph();
    nextLvlG = nextLvl.genNextLevelGraph(cLvlG);
    Node[]         nodeArray = nextLvlG.getNodes();

    for(int i = 0; i < nodeArray.length; i++) {
      String cname = nodeArray[i].getName();
      if (nodeArray[i].isCluster == false) continue;
      Node [] members = nodeArray[i].children;
      ArrayList memberList = new ArrayList();

      for(int j = 0; j < members.length; j++) {
        Node aMember = members[j];
        ArrayList childrenList = getChildrenList(aMember);
        for(int k = 0; k < childrenList.size(); k++) {
          Node leafMember = (Node)childrenList.get(k);
          String memberName = leafMember.getName();
          for(int l = 0; l < nodeList.size(); l++) {
            BunchNode bn = (BunchNode)nodeList.get(l);
            String nodeName = bn.getName();
            if (memberName.equals(nodeName)) {
              if(bn.getCluster() != BunchNode.NOT_A_MEMBER_OF_A_CLUSTER) {
                bn.resetCluster(i);
                memberList.add(bn);
              }
            }
          }
        }
      }
      if (memberList.size()>0) {
        BunchCluster bc = new BunchCluster(i,cname,memberList);
        clusterList.add(bc);
      }
    }

    numClusters = clusterList.size();
    return true;
  }

  public void printGraph() {
    System.out.println("PRINTING GRAPH\n");
    System.out.println("Node Count:         " + nodeList.size());
    System.out.println("Edge Count:         " + edgeList.size());
    System.out.println("MQ Value:           " + mqValue);
    System.out.println("Number of Clusters: " + numClusters);
    System.out.println();

    for(int i = 0; i < nodeList.size(); i++) {
      BunchNode bn = (BunchNode)nodeList.get(i);
      ArrayList fdeps = null;
      ArrayList bdeps = null;

      System.out.println("NODE:   " + bn.getName());
      System.out.println("Cluster ID:   " + bn.getCluster());

      if (bn.getDeps() != null) {
        fdeps = new ArrayList(bn.getDeps());
        for(int j = 0; j < fdeps.size(); j++) {
          BunchEdge be = (BunchEdge)fdeps.get(j);
          String depName = be.getDestNode().getName();
          int weight = be.getWeight();
          System.out.println("   ===> " + depName+" ("+weight+")");
        }
      }

      if (bn.getBackDeps() != null) {
        bdeps = new ArrayList(bn.getBackDeps());
        for(int j = 0; j < bdeps.size(); j++)
        {
          BunchEdge be = (BunchEdge)bdeps.get(j);
          String depName = be.getSrcNode().getName();
          int weight = be.getWeight();
          System.out.println("   <=== " + depName+" ("+weight+")");
        }
      }
      System.out.println();
    }

    //Now view as clusters
    System.out.println("Cluster Breakdown\n");
    ArrayList clusts = new ArrayList(this.getClusters());
    for(int i = 0; i < clusts.size(); i++) {
      BunchCluster bc = (BunchCluster)clusts.get(i);
      System.out.println("Cluster id:   " + bc.getID());
      System.out.println("Custer name:  " + bc.getName());
      System.out.println("Cluster size: " +bc.getSize());

      ArrayList members = new ArrayList(bc.getClusterNodes());
      for (int j = 0; j < members.size(); j++)
      {
        BunchNode bn = (BunchNode)members.get(j);
        System.out.println("   --> " + bn.getName() + "   ("+bn.getCluster()+")");
      }
      System.out.println();
    }
  }
}