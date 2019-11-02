package bunch.model;

import java.util.*;

public final class NextLevelGraph {

static final class NodeInfo {

  final String name;
  int    id;
  final Hashtable<Integer,Integer> dependencies = new Hashtable<>();
  final Hashtable<Integer,Integer> backEdges = new Hashtable<>();
  final Hashtable<Integer,Integer> dWeights = new Hashtable<>();
  final Hashtable<Integer,Integer> beWeights = new Hashtable<>();
  final Hashtable<Integer,Node> childNodes = new Hashtable<>();

  NodeInfo(String n) {
    name = n;
    id = -1;
  }
}

  public NextLevelGraph() {}

  public Graph genNextLevelGraph(Graph g) {
    int    lvl          = g.getGraphLevel()+1;
    Node[] nodeList     = g.getNodes();
    Hashtable<Integer,NodeInfo> cnameht   = new Hashtable<>();
    Hashtable<Integer,Integer> clusterMap = new Hashtable<>();
    int       nodeLevel = g.getGraphLevel();

    int [] clusters = g.getClusters();
    for(int i = 0; i < nodeList.length; i++) {
      nodeList[i].cluster = clusters[i];
    }
    int[]  cnames = g.getClusterNames();
    for(int i = 0; i < cnames.length; i++) {
      String name = "clusterLvl"+lvl+"Num"+i;
      NodeInfo ni = new NodeInfo(name);
      ni.id = i;
      cnameht.put(cnames[i],ni);
    }


    for (Node srcNode : nodeList) {
      int[] edges = srcNode.getDependencies();
      int[] weights = srcNode.getWeights();

      clusterMap.put(srcNode.nodeID, srcNode.cluster);

      NodeInfo niTmp = cnameht.get(srcNode.cluster);
      niTmp.childNodes.put(srcNode.nodeID, srcNode);

      for (int j = 0; j < edges.length; j++) {
        Node destNode = nodeList[edges[j]];
        int srcCluster = srcNode.cluster;
        int destCluster = destNode.cluster;

        if (srcCluster == destCluster) continue;

        int weight = weights[j];

        NodeInfo niSrc = cnameht.get(srcCluster);
        NodeInfo niDest = cnameht.get(destCluster);

        Integer src = niSrc.id;
        Integer dest = niDest.id;
        Integer w = weight;

        if (niSrc.dependencies.get(dest) == null) {
          niSrc.dependencies.put(dest, dest);
          niSrc.dWeights.put(dest, w);
        } else {
          Integer edgeW = niSrc.dWeights.get(dest);
          w = w + edgeW;
          niSrc.dWeights.put(dest, w);
        }

        if (niDest.backEdges.get(src) == null) {
          niDest.backEdges.put(src, src);
          niDest.beWeights.put(src, w);
        } else {
          Integer edgeW = niDest.beWeights.get(src);
          w = w + edgeW;
          niDest.beWeights.put(src, w);
        }
      }
    }

    //Now build the new Bunch Structure
    Graph retGraph = new Graph(cnameht.size());
    retGraph.clear();
    Node[] newNL = retGraph.getNodes();

    NodeInfo [] nl = cnameht.values().toArray(new NodeInfo[0]);
    for (NodeInfo nodeInfo : nl) {
      Node n = new Node();
        newNL[nodeInfo.id] = n;
      n.setName(nodeInfo.name);
      n.nodeID = nodeInfo.id;
      n.setIsCluster(true);
      n.nodeLevel = nodeLevel;
      n.children = new Node[nodeInfo.childNodes.size()];
      int numForwardDep = nodeInfo.dependencies.size();
      int numBackDep = nodeInfo.backEdges.size();
      n.dependencies = new int[numForwardDep];
      n.weights = new int[numForwardDep];
      n.backEdges = new int[numBackDep];
      n.beWeights = new int[numBackDep];

      int j = 0;
      for (Enumeration e = nodeInfo.childNodes.elements(); e.hasMoreElements(); )
        n.children[j++] = (Node) e.nextElement();

      updateEdgeArrays(nodeInfo.dependencies, n.dependencies, nodeInfo.dWeights, n.weights);
      updateEdgeArrays(nodeInfo.backEdges, n.backEdges, nodeInfo.beWeights, n.beWeights);

      //Uncomment the following line for debug
      //dumpNode(n);
    }

    retGraph.setPreviousLevelGraph(g);
    retGraph.setGraphLevel(g.getGraphLevel()+1);
    retGraph.setIsClusterTree(g.isClusterTree());

    return retGraph;
  }

  private void dumpNode(Node n) {
    System.out.println("Node: " + n.name_d);
    System.out.println("Node ID:  " + n.nodeID);
    System.out.print("EDGES(Weight): ");
    for(int i = 0; i < n.dependencies.length; i++)
      System.out.print(n.dependencies[i]+"("+n.weights[i]+")");
    System.out.println();
    System.out.print("BACK EDGES(Weight): ");
    for(int i = 0; i < n.backEdges.length; i++)
      System.out.print(n.backEdges[i]+"("+n.beWeights[i]+")");
    System.out.println();
    System.out.print("Children: ");
    for(int i = 0; i < n.children.length; i++)
      System.out.print("["+n.children[i].name_d+"] ");
    System.out.println();
    System.out.println("======================================");
  }

  private void updateEdgeArrays(Hashtable<Integer,Integer> edgeH, int[]edge, Hashtable<Integer,Integer> weightH, int[]weight) {

      Integer [] eo = edgeH.keySet().toArray(new Integer[0]);
    for(int i = 0; i < eo.length; i++) {
      Integer Ikey = eo[i];
      Integer Iweight = weightH.get(Ikey);
      int edgeTo = Ikey;
      int edgeWeight = Iweight;
      edge[i]=edgeTo;
      weight[i]=edgeWeight;
    }
  }
}

