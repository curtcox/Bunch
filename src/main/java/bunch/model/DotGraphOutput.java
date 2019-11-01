package bunch.model;

import bunch.model.Graph;
import bunch.model.GraphOutput;
import bunch.model.NextLevelGraph;
import bunch.model.Node;

import java.io.*;
import java.util.*;

/**
 * A class to output a partitioned graph in "Dot" format.
 *
 * @see GraphOutput
 *
 * @author Brian Mitchell
 */
public final class DotGraphOutput
  extends GraphOutput
{

int clusterIDTmp = 0;

public DotGraphOutput() { }


/**
 * This method writes the header necessary to create the dot output file format
 */
private void writeHeader() throws IOException {
    writer_d.write("/* ------------------------------------------------------------ */\n");
    writer_d.write("/* created with bunch v3 */\n");
    writer_d.write("/* Objective Function value = "+graph_d.getObjectiveFunctionValue()+"*/\n");
    writer_d.write("/* ------------------------------------------------------------ */\n\n");
    writer_d.write("digraph G {\n");
    writer_d.write("size= \"10,10\";\n");
    writer_d.write("rotate = 90;\n");
}

/**
 * This method handles dumping the special modules - omnipresent & libraries
 */
private void writeSpecialModules(Node[] originalNodes) throws IOException {
  ArrayList deadList = new ArrayList();
  deadList.clear();

  if (originalNodes != null) {
    boolean hasSuppliers = false;
    boolean hasClients = false;
    boolean hasCentrals = false;
    boolean hasLibraries = false;

    /**
     * Do first scan to see if there are special modules.  Mark the flags
     * for each special module type
     */
    for (Node node : originalNodes) {
      if (!hasSuppliers && node.getType() == Node.SUPPLIER) {
        hasSuppliers = true;
      }
      if (!hasClients && node.getType() == Node.CLIENT) {
        hasClients = true;
      }
      if (!hasCentrals && node.getType() == Node.CENTRAL) {
        hasCentrals = true;
      }
      if (!hasLibraries && node.getType() == Node.LIBRARY) {
        hasLibraries = true;
      }
    }

    if (hasLibraries) {
      //create libraries cluster
      writer_d.write("subgraph cluster_libraries {\n");
      writer_d.write("label = \"libraries\";\n");
      writer_d.write("color = black;\n");
      writer_d.write("style = bold;\n\n");
      for (Node originalNode : originalNodes) {
        if (originalNode.getType() == Node.LIBRARY) {
          writer_d.write("\"" + originalNode.getName() + "\"[shape=diamond,color=lightgray,fontcolor=black,style=filled];\n");
        }
        if (originalNode.getType() >= Node.DEAD)
          if ((originalNode.getType() - Node.DEAD) == Node.LIBRARY) {
            writer_d.write("\"" + originalNode.getName() + "\"[label=\"" + originalNode.getName() + "\",shape=box,color=lightblue,fontcolor=black,style=filled];\n");
            deadList.add(originalNode);
          }
      }
      writer_d.write("}\n");
    }

    if (hasSuppliers) {
      //create suppliers cluster
      writer_d.write("subgraph cluster_omnipresent_suppliers {\n");
      writer_d.write("label = \"omnipresent suppliers\";\n");
      writer_d.write("color = black;\n");
      writer_d.write("style = bold;\n\n");
      for (Node originalNode : originalNodes) {
        if (originalNode.getType() == Node.SUPPLIER) {
          writer_d.write("\"" + originalNode.getName() + "\"[shape=diamond,color=lightgray,fontcolor=black,style=filled];\n");
        }
        if (originalNode.getType() >= Node.DEAD)
          if ((originalNode.getType() - Node.DEAD) == Node.SUPPLIER) {
            writer_d.write("\"" + originalNode.getName() + "\"[label=\"" + originalNode.getName() + "\",shape=box,color=lightblue,fontcolor=black,style=filled];\n");
            deadList.add(originalNode);
          }
      }
      writer_d.write("}\n");
    }

    if (hasClients) {
      //create suppliers cluster
      writer_d.write("subgraph cluster_omnipresent_clients {\n");
      writer_d.write("label = \"omnipresent clients\";\n");
      writer_d.write("color = black;\n");
      writer_d.write("style = bold;\n\n");
      for (Node originalNode : originalNodes) {
        if (originalNode.getType() == Node.CLIENT) {
          writer_d.write("\"" + originalNode.getName() + "\"[shape=diamond,color=lightgray,fontcolor=black,style=filled];\n");
        }
        if (originalNode.getType() >= Node.DEAD)
          if ((originalNode.getType() - Node.DEAD) == Node.CLIENT) {
            writer_d.write("\"" + originalNode.getName() + "\"[label=\"" + originalNode.getName() + "\",shape=box,color=lightblue,fontcolor=black,style=filled];\n");
            deadList.add(originalNode);
          }
      }
      writer_d.write("}\n");
    }

    if (hasCentrals) {
      //create suppliers cluster
      writer_d.write("subgraph cluster_omnipresent_centrals {\n");
      writer_d.write("label = \"omnipresent clients/suppliers\";\n");
      writer_d.write("color = black;\n");
      writer_d.write("style = bold;\n\n");
      for (Node originalNode : originalNodes) {
        if (originalNode.getType() == Node.CENTRAL) {
          writer_d.write("\"" + originalNode.getName() + "\"[shape=diamond,color=lightgray,fontcolor=black,style=filled];\n");
        }
        if (originalNode.getType() >= Node.DEAD)
          if ((originalNode.getType() - Node.DEAD) == Node.CENTRAL) {
            writer_d.write("\"" + originalNode.getName() + "\"[label=\"" + originalNode.getName() + "\",shape=box,color=lightblue,fontcolor=black,style=filled];\n");
            deadList.add(originalNode);
          }
      }
      writer_d.write("}\n");
    }
  }

  if(deadList.size()>0) {
    writer_d.write("\n");
    for (Object o : deadList) {
      Node tmpN = (Node) o;
      writeEdges(tmpN, originalNodes);
    }
    writer_d.write("\n");
  }
}


/**
 * Dump the edges of the dotty file. Make sure that each node name
 * is enclosed in quotes or there will be problems if the names contain
 * special characters
 */
private void writeEdges(Node n, Node[] origList) throws IOException {
  String srcName = n.getName();
  int[] deps = n.getDependencies();

  for (int dep : deps) {
    String tgtName = origList[dep].getName();
    writer_d.write("\"" + srcName + "\" -> \"" + tgtName + "\" [color=blue,font=6];\n");
  }
}


/**
 * We need a closing bracket.  Its overkill to use a method, but it fits into
 * the overall framework.
 */
private void writeClosing() throws IOException {
    writer_d.write("}\n");
    //writer_d.close();
}

/**
 * Given a node object (which is a cluster), and an ID, generate the
 * cluster associated with the node.
 */
private void genCluster(Node n, long baseID) throws IOException {
  Stack st = new Stack();
  Hashtable ht = new Hashtable();

  /**
   * We need a stack to manage the depth and embedded clusters
   */
  st.push(n);

  /**
   * Continue processing the stack until the stack is empty.  This approach works well
   * for recursing down the levels of the subsystem hierarchy.
   */
  while(!st.empty()) {
    Node tmp = (Node)st.peek();
    if(tmp.isCluster()) {
      String strongestNode = findStrongestNode(tmp);
      String hkey = "SS_"+tmp.name_d;

      /**
       * This condition indicates that the subsystem is ending
       */
      if(ht.containsKey(tmp.getUniqueID())) {
        writer_d.write("}\n\n");
        ht.remove(tmp.getUniqueID());
        st.pop();
      }
      /**
       * We are beginning a new subsystem
       */
      else
      {
        ht.put(tmp.getUniqueID(),tmp.getUniqueID());

        /**
         * Generate the subsystem header
         */
        String cName = "(SS-L"+tmp.nodeLevel+"):"+strongestNode;
        long clustID = tmp.nodeID+(baseID++);

        writer_d.write("subgraph cluster"+clustID+" {\n");
        writer_d.write("label = \""+cName+"\";\n");
        writer_d.write("color = black;\n");
        writer_d.write("style = bold;\n\n");

        /**
         * Push the children for processing later
         */
        for(int j = 0; j < tmp.children.length;j++)
          st.push(tmp.children[j]);
      }
    }
    /**
     * This is not a subsystem, it contains the leafs from the tree, so generate
     * the nodes
     */
    else
    {
      writer_d.write("\""+tmp.getName()+"\"[label=\""+tmp.getName()+"\",shape=ellipse,color=lightblue,fontcolor=black,style=filled];\n");
      st.pop();
    }
  }
}


/**
 * Given a graph, this method generates all of the clusters
 */
private void generateClusters(Graph cLvlG) throws IOException {
  Graph nextLvlG = null;

  /**
   * If we are not at the root of the subsystem hierarchy, get the previous
   * level graph.  We will use this graph to recreate the clusters at this
   * level
   */
  if((cLvlG.getClusterNames().length <= 1)&&(cLvlG.getPreviousLevelGraph()!=null))
    cLvlG = cLvlG.getPreviousLevelGraph();

  /**
   * Now, generate the next level graph, which is the same as the level that
   * we started with.  The genNextLevelGraph() method has several things that
   * help us with the nesting
   */
  NextLevelGraph nextLvl = new NextLevelGraph();
  nextLvlG = nextLvl.genNextLevelGraph(cLvlG);


  Node[]         nodeList = nextLvlG.getNodes();
  int            Lvl      = cLvlG.getGraphLevel();

  long base=1000;

  /**
   * Each node in this graph is a cluster, so process them individually
   */
  /**
   * First we check for illegial invariants, that is since we are expecting that
   * these are clusters, they better have children at this point, or else...
   */
  /**
   * Given the node, mark the strongest child in the cluster.  This will be used to
   * name the cluster
   */
  /**
   * Now generate the cluster, and update the id so that each cluster has a
   * unique id
   */
  for (Node tmp : nodeList) {
    /**
     * First we check for illegial invariants, that is since we are expecting that
     * these are clusters, they better have children at this point, or else...
     */
    if (tmp.children == null)
      continue;

    if (tmp.children.length == 0)
      continue;

    /**
     * Given the node, mark the strongest child in the cluster.  This will be used to
     * name the cluster
     */
    findStrongestNode(tmp);

    /**
     * Now generate the cluster, and update the id so that each cluster has a
     * unique id
     */
    genCluster(tmp, base);
    base += 1000;
  }
}

/**
 * This method is used to generate a vector of vectors, with each high level
 * vector indicating a cluster, and its associated vector indicating the nodes
 * in the cluster
 */
private void genChildrenFromOneLevel(Graph cLvlG) throws IOException {
  Graph nextLvlG = null;

  /**
   * If we are not at the root of the subsystem hierarchy, get the previous
   * level graph.  We will use this graph to recreate the clusters at this
   * level
   */
  if((cLvlG.getClusterNames().length <= 1)&&(cLvlG.getPreviousLevelGraph()!=null)) {
    cLvlG = cLvlG.getPreviousLevelGraph();
  }

  /**
   * Now, generate the next level graph, which is the same as the level that
   * we started with.  The genNextLevelGraph() method has several things that
   * help us with the nesting
   */
  NextLevelGraph nextLvl = new NextLevelGraph();
  nextLvlG = nextLvl.genNextLevelGraph(cLvlG);

  Node[]         nodeList = nextLvlG.getNodes();
  Vector         cVect    = new Vector();
  int            Lvl      = cLvlG.getGraphLevel();

  cVect.removeAllElements();

  /**
   * Now process the nodes at the current level, each is a cluster
   */
  /**
   * These are panic statements.  It should never be the case that the nodes
   * at this point do not contain children
   */
  for (Node tmp : nodeList) {
    /**
     * These are panic statements.  It should never be the case that the nodes
     * at this point do not contain children
     */
    if (tmp.children == null)
      continue;

    if (tmp.children.length == 0)
      continue;

    //The strongest node is used to name the cluster
    findStrongestNode(tmp);

    //Create a vector for the new cluster
    Vector newCluster = new Vector();
    newCluster.removeAllElements();
    cVect.addElement(newCluster);

    //add the children to this cluster
    echoNestedChildren(tmp, newCluster);
  }

  /**
   * Now that the datastructures are setup, write the clusters to the
   * output file
   */
  WriteOutputClusters(cVect,Lvl);
}

/**
 * Given a particular node, this method will find the strongest member
 * and return the node name.
 *
 * @return The name of the strongest member of the provided cluster
 */
private String findStrongestNode(Node n) {
  /**
   * Make sure that we have a cluster
   */
  if (!n.isCluster())
    return "";

  int lvl = n.nodeLevel;
  boolean lvlIncr = false;

  Vector nodeV = new Vector();

  /**
   * Place all of the members of this cluster into a linked list.  These entries
   * may be clusters themselves, so we may need to do some recursing...
   */
  LinkedList l = new LinkedList();
  l.clear();
  nodeV.clear();

  //Seed the linked list
  l.addLast(n);

  /**
   * For each member in the provided linked list, traverse down the hierarchy until
   * we find the real nodes and not nodes that are clusters
   */
  while (!l.isEmpty()) {
    Node curr = (Node)l.removeFirst();

    /**
     * If the current node is a cluster, put its children on the linked list, we will
     * need to look at these later to see if they are also clusters or if they are
     * real nodes
     */
    if (curr.isCluster()) {
      Node[] children = curr.children;
      if((children != null) && (children.length>0))
        for (Node child : children) l.addLast(child);
    }
    /**
     * OK, its a real node, add it to the vector for examination.
     */
    else
    {
      nodeV.add(curr);
    }
  }

  /**
   * Now that we have a vector of nodes for the provided cluster, we can
   * find out which node is the strongest.
   */
  String ssName = findStrongestNode(nodeV);

  /**
   * Return the strongest node
   */
  n.setName(ssName);
  return ssName;
}

/**
 * This method helps out its companion method by taking a list of nodes and
 * determining the central node.
 *
 * @returns The strongest node for a list of nodes provided on the interface
 */
private String findStrongestNode(Vector v) {
  int maxEdgeWeight = 0;
  int maxEdgeCount = 0;
  Node domEdgeNode = null;
  Node domWeightNode = null;

  /**
   * This should not happen, but we will check anyway.
   */
  if (v == null) return "EmptyCluster";

  /**
   * Now process the nodes...
   */
  for(int i = 0; i < v.size();i++) {
    Node n = (Node)v.elementAt(i);
    String name = n.getName();
    int edgeWeights=0;
    int depCount = 0;
    int beCount = 0;

    /**
     * For the provided node, determine the total edge edges incident
     * to each node
     */
    if(n.dependencies!=null)
      depCount = n.dependencies.length;

    if(n.backEdges != null)
      beCount = n.backEdges.length;

    int edgeCount = depCount + beCount;

    /**
     * Is this the strongest node seen so far, if so save it.  When we
     * are done processing all of the nodes, the strongest node will be
     * known.
     */
    if(edgeCount >= maxEdgeCount)
    {
      maxEdgeCount = edgeCount;
      domEdgeNode = n;
    }

    if(n.weights!=null)
      for(int j = 0; j < n.weights.length;j++)
        edgeWeights+=n.weights[j];

    if(n.beWeights!=null)
      for(int j = 0; j < n.beWeights.length;j++)
        edgeWeights+=n.beWeights[j];

    if(edgeWeights >= maxEdgeWeight)
    {
      maxEdgeWeight = edgeWeights;
      domWeightNode = n;
    }
  }

  /**
   * Now that all of the nodes have been processed, return the strongest
   * one investigated...
   */
  return domEdgeNode.getName();
}

/**
 * Given a particular node, and a vector (by reference), populate the vector
 * with the children of the node.  They may be nested so we will need to recurse
 */
private void echoNestedChildren(Node n, Vector v) {
  /**
   * Create a stack to manage the recursion
   */
  Stack s = new Stack();
  boolean firstNode = true;

  /**
   * Push the first node on the stack as the basis for "drilling down"
   */
  s.push(n);
  while(!s.isEmpty())
  {
    Node tmpNode = (Node)s.pop();

    /**
     * If the node does not have children, continue.  This situation should
     * not happen
     */
    if(tmpNode.children==null)
      continue;

    /**
     * For each of the children... if they are clusters themselves push them,
     * if they are actual nodes add it to the returned vector.
     */
    for(int i = 0; i < tmpNode.children.length; i++) {
      Node childNode = tmpNode.children[i];
      if(childNode.isCluster())
        s.push(childNode);
      else
      {
        v.addElement(childNode); //writer_d.write(childNode.getName());
      }
    }
  }
}

/**
 * This method outputs the cluster passed via cVect, and the specified level
 * to the output stream
 */
private void WriteOutputClusters(Vector cVect, int lvl) throws IOException {
  if(cVect==null) return;

  /**
   * Process each cluster individually
   */
  for(int i = 0; i < cVect.size(); i++) {
    /**
     * Get the particular cluster, and assign it a name
     */
    Vector cluster = (Vector)cVect.elementAt(i);
    String cName = findStrongestNode(cluster);
    cName = "(SS-L"+lvl+"):"+cName;

    /**
     * Write the dotty specific header for the cluster
     */
    long clustID = baseID++;
    writer_d.write("subgraph cluster"+clustID+" {\n");
    writer_d.write("label = \""+cName+"\";\n");
    writer_d.write("color = black;\n");
    writer_d.write("style = bold;\n\n");

    /**
     * Now generate the nodes embedded in the cluster
     */
    for(int j = 0; j < cluster.size(); j++)
    {
      Node n = (Node)cluster.elementAt(j);
      writer_d.write("\""+n.getName()+"\"[label=\""+n.getName()+"\",shape=ellipse,color=lightblue,fontcolor=black,style=filled];\n");
    }

    //close the cluster
    writer_d.write("}\n");
  }
}

/**
 * The write method is called to actually execute the creation of the
 * dot file
 */
public void write() {
  Graph gWriteGraph = graph_d;

  /**
   * Get the config parameters describing how the output will be generated and the
   * output file name
   */
  int technique = this.getOutputTechnique();
  String fileName = this.getCurrentName();

  /**
   * Handle the different cases for the different output options.
   */
  switch(technique) {
    //The goal is to generate for all levels
    case GraphOutput.OUTPUT_ALL_LEVELS: {
      Graph gLvl = graph_d;

      /**
       * Drill down the graph hierarchy to find the first level
       */
      while(gLvl.getGraphLevel() > 0) {
        if(gLvl.getClusterNames().length <= 1) {
          gLvl = gLvl.getPreviousLevelGraph();
          continue;
        }
        String fName = fileName+"L"+gLvl.getGraphLevel()+".dot";
        writeGraph(fName,gLvl);
        gLvl = gLvl.getPreviousLevelGraph();
      }

      fileName += ".dot";

      /**
       * Write the graph for the specified level
       */
      writeGraph(fileName,graph_d.getMedianTree());

      break;
    }

    //We only want to the median level
    case GraphOutput.OUTPUT_MEDIAN_ONLY: {
      fileName += ".dot";

      Graph g = graph_d;
      if (graph_d.isClusterTree())
        g = graph_d.getMedianTree();

      /**
       * Get the median level and write it...
       */
      writeGraph(fileName,g);
      break;
    }

    case GraphOutput.OUTPUT_TOP_ONLY: {
      fileName += ".dot";
      writeGraph(fileName,graph_d);
      break;
    }
    case GraphOutput.OUTPUT_DETAILED_LEVEL_ONLY: {
      fileName += ".dot";
      Graph tmpG = graph_d;
      while(tmpG.getGraphLevel() > 0)
        tmpG=tmpG.getPreviousLevelGraph();

      writeGraph(fileName,tmpG);
      break;
    }
  }
}

/**
 * This method writes the specified graph to the output stream
 */
private void writeGraph(String fileName, Graph g) {
  /**
   * Open the output stream and use the generateOutput() method to
   * actually generate the output
   */
  try {
    writer_d = new BufferedWriter(new FileWriter(fileName));
    generateOutput(g);
    writer_d.close();
  } catch (IOException e) {
    throw new RuntimeException(e);
  }
}

/**
 * This is the method that we use to controll the graph generation
 */
private void generateOutput(Graph g) throws IOException {
  Graph gBase = g;

  while (gBase.getGraphLevel() != 0)
    gBase = gBase.getPreviousLevelGraph();

  int[] clusters = gBase.getClusters();
  Node[] nodeList = gBase.getNodes();
  int nodes = nodeList.length;
  int[][] clustMatrix = new int[nodes][nodes+1];

  /**
   * Initialize a matrix to be used to help with the output
   */
  for (int i=0; i<nodes; ++i) {
        clustMatrix[i][0] = 0;
        nodeList[i].cluster = -1;
  }

  int pos=0;
  for (int i=0; i<nodes; ++i) {
    int numCluster = clusters[i];
    clustMatrix[numCluster][(++clustMatrix[numCluster][0])] = i;
    nodeList[i].cluster = numCluster;
  }

  /**
   * Generate the header to start the output processing
   */
  writeHeader();
  Node [] on = gBase.getOriginalNodes();

  /**
   * If there are special modules then write them out now...
   */
  if((on != null) &&(on.length != nodeList.length))
    writeSpecialModules(gBase.getOriginalNodes());

  /**
   * Now write the nested clusters, or the single level, depending on the
   * user selected option.
   */
  if(!getWriteNestedLevels())
    genChildrenFromOneLevel(g);
  else
    generateClusters(g);

  /**
   * Now generate the edges, which in dot format can come at the end
   */
  genEdges(g);

  /**
   * Everything has been generated, now write the closing.
   */
  writeClosing();
}

/**
 * This helper method generates the edges for the given graph.
 */
private void genEdges(Graph g) throws IOException {
  Graph gBase = g;

  /**
   * We need the first level graph to to get the edges from the MDG.
   */
  while (gBase.getGraphLevel() != 0)
    gBase = gBase.getPreviousLevelGraph();

  /**
   * Do some initialization
   */
  int[] clusters = gBase.getClusters();
  Node[] nodeList = gBase.getNodes();
  int nodes = nodeList.length;
  int[][] clustMatrix = new int[nodes][nodes+1];

  /**
   * Initialize a matrix to indicate what nodes have connection to other
   * nodes.
   */
  for (int i=0; i<nodes; ++i) {
        clustMatrix[i][0] = 0;
        nodeList[i].cluster = -1;
  }

  int pos=0;
  for (int i=0; i<nodes; ++i) {
    int numCluster = clusters[i];
    clustMatrix[numCluster][(++clustMatrix[numCluster][0])] = i;
    nodeList[i].cluster = numCluster;
  }

  /**
   * Now travers the matrix to determine if an edge goes between a
   * pair of modules.  If so genenerate the dotty statement to
   * place this edge in the output file.
   */
  for (Node node : nodeList) {
    int[] l = node.dependencies;
    if (l != null) {
      for (int value : l) {
        writer_d.write("\"" + node.getName() + "\" -> \"" + nodeList[value].getName() + "\" [color=blue,font=6];\n");
      }
    }
  }
}
}
