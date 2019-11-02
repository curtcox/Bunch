package bunch.api;

import bunch.model.Graph;
import bunch.model.NextLevelGraph;
import bunch.model.Node;

import  java.util.*;
import  java.io.*;

public final class BunchGraph {

  private List<BunchNode> nodeList;
  private List<BunchEdge> edgeList;
  private List<BunchCluster> clusterList;
  private Map<String,BunchNode> nodeHT;
  private boolean includesIsomorphicUpdates = false;

  private double mqValue = 0;
  private int    numClusters = 0;

  //default constructor
  public BunchGraph() { }

  public Collection<BunchNode> getNodes() {
    return nodeList;
  }

  public int getTotalOverlapNodes() {
    int total = 0;
    if(clusterList != null) {
      for (BunchCluster bc : clusterList) {
        total += bc.getOverlapNodeCount();
      }
    }
    return total;
  }

  public Collection<BunchEdge> getEdges()
  {
    return edgeList;
  }

  public Collection<BunchCluster> getClusters()
  { return clusterList; }

  public double getMQValue()
  { return mqValue; }

  public int getNumClusters()
  {
    return numClusters;
  }

  public BunchNode findNode(String nodeName) {
    if(nodeHT == null)
      nodeHT = constructNodeHT();

    return nodeHT.get(nodeName);
  }

  private Map<String,BunchNode> constructNodeHT() {
    Map<String,BunchNode> h = new HashMap<>();
    for (BunchNode theNode : nodeList) {
      String key = theNode.getName();
      h.put(key, theNode);
    }
    return h;
  }

  private List<Node> getChildrenList(Node n) {
    List<Node> a = new ArrayList<>();
    if (!n.isCluster()) {
      a.add(n);
      return a;
    }
    Stack<Node> s = new Stack<>();
    s.push(n);
    while(!s.isEmpty()) {
      Node c = s.pop();
      Node []childrenList = c.children;
      for (Node aChild : childrenList) {
        if (aChild.isCluster())
          s.push(aChild);
        else
          a.add(aChild);
      }
    }
    return a;
  }

  public void determineIsomorphic() {
    if (includesIsomorphicUpdates)
      return;

    includesIsomorphicUpdates = true;
    Iterator<BunchNode> nodeI = getNodes().iterator();
    List<BunchCluster> theClusters = new ArrayList<>(getClusters());
    boolean nodeIsomorphic;

    while(nodeI.hasNext()) {
      BunchNode bn = nodeI.next();
      nodeIsomorphic = false;
      int[] cv = howConnected(bn);

      int currClust = bn.getCluster();
      int currStrength = cv[currClust];
      for(int i = 0; i < cv.length; i++) {
        if(i == currClust) continue;
        int connectStrength = cv[i];
        if(connectStrength == currStrength) {
          BunchCluster bc = theClusters.get(i);
          bc.addOverlapNode(bn);
          bn.subscribeToCluster(bc);
          nodeIsomorphic = true;

        }
      }
      if(nodeIsomorphic) ;
    }
  }

  private int[] howConnected(BunchNode bn) {
    int howManyClusters = getClusters().size();
    int [] connectVector = new int[howManyClusters];
    Iterator<BunchEdge> fdeps;
    Iterator<BunchEdge> bdeps;

    for(int i=0; i<connectVector.length;i++)
      connectVector[i] = 0;

    if (bn.getDeps() != null) {
      fdeps = bn.getDeps().iterator();
      while(fdeps.hasNext()) {
        BunchEdge be = fdeps.next();
        BunchNode target = be.getDestNode();
        int targetCluster = target.getCluster();
        connectVector[targetCluster]++;
      }
    }


    if (bn.getBackDeps() != null) {
      bdeps = bn.getBackDeps().iterator();
      while(bdeps.hasNext()) {
        BunchEdge be = bdeps.next();
        BunchNode target = be.getSrcNode();
        int targetCluster = target.getCluster();
        connectVector[targetCluster]++;
      }
    }

    return connectVector;
  }

  public boolean construct(Graph gBase) {
    Graph g = gBase.getDetailedGraph();

    nodeList = new ArrayList<>();
    edgeList = new ArrayList<>();
    clusterList = new ArrayList<>();

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

      List<BunchEdge> forwardList = null;
      List<BunchEdge> backList = null;

      if (deps != null)
        forwardList = new ArrayList<>();

      for(int j = 0; j < deps.length; j++) {
        int edgeWeight = weights[j];
        int destIdx = deps[j];
        BunchEdge be = new BunchEdge(edgeWeight,
                nodeList.get(i),
                nodeList.get(destIdx));
        edgeList.add(be);
        forwardList.add(be);
      }
      if (backDeps != null)
        backList = new ArrayList<>();

      for(int j = 0; j < backDeps.length; j++) {
        int edgeWeight = backWeights[j];
        int srcIdx = backDeps[j];
        BunchEdge be = new BunchEdge(edgeWeight,
                nodeList.get(srcIdx),
                nodeList.get(i));

        backList.add(be);
      }

      BunchNode bn = nodeList.get(i);
      bn.setDeps(forwardList,backList);
    }

    mqValue = g.getObjectiveFunctionValue(); //c.getObjFnValue();
    int [] cnames = g.getClusterNames();
    numClusters = cnames.length; //  c.getClusterNames().length;

    //Now construct the cluster objects
    Graph nextLvlG;
    Graph cLvlG = gBase.cloneGraph();

    NextLevelGraph nextLvl = new NextLevelGraph();
    nextLvlG = nextLvl.genNextLevelGraph(cLvlG);
    Node[]         nodeArray = nextLvlG.getNodes();

    for(int i = 0; i < nodeArray.length; i++) {
      String cname = nodeArray[i].getName();
      if (!nodeArray[i].isCluster) continue;
      Node [] members = nodeArray[i].children;
      List<BunchNode> memberList = new ArrayList<>();

      for (Node aMember : members) {
        List<Node> childrenList = getChildrenList(aMember);
        for (Node leafMember : childrenList) {
          String memberName = leafMember.getName();
          for (BunchNode bn : nodeList) {
            String nodeName = bn.getName();
            if (memberName.equals(nodeName)) {
              if (bn.getCluster() != BunchNode.NOT_A_MEMBER_OF_A_CLUSTER) {
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
    println("PRINTING GRAPH\n");
    println("Node Count:         " + nodeList.size());
    println("Edge Count:         " + edgeList.size());
    println("MQ Value:           " + mqValue);
    println("Number of Clusters: " + numClusters);
    println();

    for (BunchNode bn : nodeList) {
      List<BunchEdge> fdeps;
      List<BunchEdge> bdeps;

      println("NODE:   " + bn.getName());
      println("Cluster ID:   " + bn.getCluster());

      if (bn.getDeps() != null) {
        fdeps = new ArrayList<>(bn.getDeps());
        for (BunchEdge be : fdeps) {
          String depName = be.getDestNode().getName();
          int weight = be.getWeight();
          println("   ===> " + depName + " (" + weight + ")");
        }
      }

      if (bn.getBackDeps() != null) {
        bdeps = new ArrayList<>(bn.getBackDeps());
        for (BunchEdge be : bdeps) {
          String depName = be.getSrcNode().getName();
          int weight = be.getWeight();
          println("   <=== " + depName + " (" + weight + ")");
        }
      }
      println();
    }

    //Now view as clusters
    println("Cluster Breakdown\n");
    List<BunchCluster> clusts = new ArrayList<>(this.getClusters());
    for (BunchCluster bc : clusts) {
      println("Cluster id:   " + bc.getID());
      println("Custer name:  " + bc.getName());
      println("Cluster size: " + bc.getSize());

      List<BunchNode> members = new ArrayList<>(bc.getClusterNodes());
      for (BunchNode bn : members) {
        println("   --> " + bn.getName() + "   (" + bn.getCluster() + ")");
      }
      println();
    }
  }

  private static void println(String message) {
    System.out.println(message);
  }

  private static void println() {
    System.out.println();
  }

}