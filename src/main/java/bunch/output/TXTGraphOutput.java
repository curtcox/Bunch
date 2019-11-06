package bunch.output;

import bunch.model.Graph;
import bunch.model.NextLevelGraph;
import bunch.model.Node;
import bunch.output.GraphOutput;

import java.util.*;
import java.io.*;

public final class TXTGraphOutput extends GraphOutput {

private boolean hasSuppliers = false;
private boolean hasClients = false;
private boolean hasCentrals = false;
private boolean hasLibraries = false;

public TXTGraphOutput() { }

private void checkForSpecialModules(Node[] originalNodes) {
    if (originalNodes != null) {
      hasSuppliers = false;
      hasClients = false;
      hasCentrals = false;
      hasLibraries = false;

      for (Node originalNode : originalNodes) {
        if (!hasSuppliers && originalNode.getType() == Node.SUPPLIER) {
          hasSuppliers = true;
        }
        if (!hasClients && originalNode.getType() == Node.CLIENT) {
          hasClients = true;
        }
        if (!hasCentrals && originalNode.getType() == Node.CENTRAL) {
          hasCentrals = true;
        }
        if (!hasLibraries && originalNode.getType() == Node.LIBRARY) {
          hasLibraries = true;
        }
      }
    }
}

private void writeSpecialModules(Node[] originalNodes) throws IOException {
    List<Node> deadList = new ArrayList<>();

  if (originalNodes != null) {
      hasSuppliers = false;
      hasClients = false;
      hasCentrals = false;
      hasLibraries = false;

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

      int count = 1;
      if (hasLibraries) {
        //create libraries cluster
        writer_d.write("SS(libraries) = ");
        for (Node originalNode : originalNodes) {
          if (originalNode.getType() == Node.LIBRARY) {
            if (count > 1) writer_d.write(", ");
            writer_d.write(originalNode.getName());
            count++;
          }
          if (originalNode.getType() >= Node.DEAD)
            if ((originalNode.getType() - Node.DEAD) == Node.LIBRARY) {
              if (count > 1) writer_d.write(", ");
              writer_d.write(originalNode.getName());
              deadList.add(originalNode);
              count++;
            }
        }
        writer_d.write("\n");
      }

      count = 1;
      if (hasSuppliers) {
        //create suppliers cluster
        writer_d.write("SS(omnipresent_suppliers) = ");
        for (Node originalNode : originalNodes) {
          if (originalNode.getType() == Node.SUPPLIER) {
            if (count > 1) writer_d.write(", ");
            writer_d.write(originalNode.getName());
            count++;
          }
          if (originalNode.getType() >= Node.DEAD)
            if ((originalNode.getType() - Node.DEAD) == Node.SUPPLIER) {
              if (count > 1) writer_d.write(", ");
              writer_d.write(originalNode.getName());
              deadList.add(originalNode);
              count++;
            }
        }
        writer_d.write("\n");
      }

      count = 1;
      if (hasClients) {
        //create suppliers cluster
        writer_d.write("SS(omnipresent_clients) = ");
        for (Node originalNode : originalNodes) {
          if (originalNode.getType() == Node.CLIENT) {
            if (count > 1) writer_d.write(", ");
            writer_d.write(originalNode.getName());
            count++;
          }
          if (originalNode.getType() >= Node.DEAD)
            if ((originalNode.getType() - Node.DEAD) == Node.CLIENT) {
              if (count > 1) writer_d.write(", ");
              writer_d.write(originalNode.getName());
              deadList.add(originalNode);
              count++;
            }
        }
        writer_d.write("\n");
      }

      count = 1;
      if (hasCentrals) {
        //create suppliers cluster
        writer_d.write("SS(omnipresent_centrals) = ");
        for (Node originalNode : originalNodes) {
          if (originalNode.getType() == Node.CENTRAL) {
            if (count > 1) writer_d.write(", ");
            writer_d.write(originalNode.getName());
            count++;
          }
          if (originalNode.getType() >= Node.DEAD)
            if ((originalNode.getType() - Node.DEAD) == Node.CENTRAL) {
              if (count > 1) writer_d.write(", ");
              writer_d.write(originalNode.getName());
              deadList.add(originalNode);
              count++;
            }
        }
        writer_d.write("\n");
      }
    }
}


private void writeClosing() {
    //writer_d.close();
}

public void echoNestedChildrenOLD(Node n) throws IOException {
  Stack<Node> s = new Stack<>();
  boolean firstNode = true;
  s.push(n);
  while(!s.isEmpty()) {
    Node tmpNode = s.pop();
    if(tmpNode.children==null)
      continue;
    for(int i = 0; i < tmpNode.children.length; i++) {
      Node childNode = tmpNode.children[i];
      if(childNode.isCluster())
        s.push(childNode);
      else {
        if(!firstNode)
          writer_d.write(", ");
        else
          firstNode = false;    //dont write the comma on the first node
        writer_d.write(childNode.getName());
      }
    }
  }
}

private void generateClusters(Graph cLvlG) throws IOException {
  Graph nextLvlG;

  if((cLvlG.getClusterNames().length <= 1)&&(cLvlG.getPreviousLevelGraph()!=null))
    cLvlG = cLvlG.getPreviousLevelGraph();


  NextLevelGraph nextLvl = new NextLevelGraph();
  nextLvlG = nextLvl.genNextLevelGraph(cLvlG);


  Node[]         nodeList = nextLvlG.getNodes();
  Vector<Node>         cVect = new Vector<>();

  writer_d.write("SS(ROOT) = ");
  int count = 1;
  for (Node tmp : nodeList) {
    if (tmp.children == null)
      continue;

    if (tmp.children.length == 0)
      continue;

    findStrongestNode(tmp);
    cVect.addElement(tmp);
  }


  for(int i = 0; i < cVect.size(); i++) {
    if(count > 1) writer_d.write(", ");
    count++;
    Node n = (Node)cVect.elementAt(i);
    //String ssName = "(SS-L"+n.nodeLevel+"):"+n.getName();
    String ssName = n.getName()+".ssL"+n.nodeLevel;
    writer_d.write(ssName);
  }

  if(hasSuppliers) {
    if(count > 1) writer_d.write(", ");
    count++;
    writer_d.write("omnipresent_suppliers");
  }
  if(hasClients)
  {
    if(count > 1) writer_d.write(", ");
    count++;
    writer_d.write("omnipresent_clients");
  }
  if(hasCentrals)
  {
    if(count > 1) writer_d.write(", ");
    count++;
    writer_d.write("omnipresent_centrals");
  }
  if(hasLibraries)
  {
    if(count > 1) writer_d.write(", ");
    count++;
    writer_d.write("libraries");
  }
  writer_d.write("\n");

  echoNestedTree(cVect);
}

private void echoNestedTree(Vector<Node> v) throws IOException {
  LinkedList<Node> l = new LinkedList<>();

  for(int i = 0; i < v.size(); i++)
    l.addLast(v.elementAt(i));

  while(l.size() > 0) {
    Node n = l.removeFirst();
    if((n.children != null)&(n.children.length>0)) {
      findStrongestNode(n);
      //writer_d.write("SS("+ssName+") = ");
      //String ssName = "(SS-L"+n.nodeLevel+"):"+n.getName();
      String ssName = n.getName()+".ssL"+n.nodeLevel;
      writer_d.write("SS("+ssName+") = ");
      for(int i = 0; i < n.children.length; i++) {
        Node c = n.children[i];
        if(c.isCluster()) {
          l.addLast(c);
          findStrongestNode(c);
          //ssName = "(SS-L"+c.nodeLevel+"):"+c.getName();
          ssName = c.getName()+".ssL"+c.nodeLevel;
          writer_d.write(ssName);
        }
        else
          writer_d.write(c.getName());
        if(i < (n.children.length-1))
          writer_d.write(", ");
        else
          writer_d.write("\n");
      }
    }
  }
}

private void echoNestedChildren(Node n, List<Node> v) {
  Stack<Node> s = new Stack<>();
  s.push(n);
  while(!s.isEmpty()) {
    Node tmpNode = s.pop();
    if(tmpNode.children==null)
      continue;
    for(int i = 0; i < tmpNode.children.length; i++) {
      Node childNode = tmpNode.children[i];
      if(childNode.isCluster())
        s.push(childNode);
      else {
        v.add(childNode);
      }
    }
  }
}

private void genChildrenFromOneLevel(Graph cLvlG) throws IOException {
  Graph nextLvlG;

  if((cLvlG.getClusterNames().length <= 1)&&(cLvlG.getPreviousLevelGraph()!=null)) {
    cLvlG = cLvlG.getPreviousLevelGraph();
    fixupNodeList(cLvlG);
  }

    NextLevelGraph nextLvl = new NextLevelGraph();
    nextLvlG = nextLvl.genNextLevelGraph(cLvlG);

  Node[]         nodeList = nextLvlG.getNodes();
  Vector<List>         cVect    = new Vector<>();

  cVect.removeAllElements();
  for (Node tmp : nodeList) {
    if (tmp.children == null)
      continue;

    if (tmp.children.length == 0)
      continue;

    findStrongestNode(tmp);

    Vector<Node> newCluster = new Vector<>();
    newCluster.removeAllElements();
    cVect.addElement(newCluster);
    echoNestedChildren(tmp, newCluster);
  }
  WriteOutputClusters(cVect);
}

private void findStrongestNode(Node n) {
  if (!n.isCluster())
    return;

  List<Node> nodeV = new Vector<>();

  LinkedList<Node> l = new LinkedList<>();

  //Seed the linked list

  l.addLast(n);
  while (!l.isEmpty()) {
    Node curr = l.removeFirst();
    if (curr.isCluster()) {
      Node[] children = curr.children;
      if((children != null) && (children.length>0))
        for (Node child : children) l.addLast(child);
    } else {
      nodeV.add(curr);
    }
  }

  //String ssName = "ss"+(findStrongestNode(nodeV))+"L"+n.nodeLevel;
  String ssName = findStrongestNode(nodeV);
  n.setName(ssName);
  //ssName = "(SS-L"+n.nodeLevel+"):"+ssName;
}

private String findStrongestNode(List<Node> v) {
  int maxEdgeWeight = 0;
  int maxEdgeCount = 0;
  Node domEdgeNode = null;

  if (v == null) return "EmptyCluster";
  //if(v.size()==0)
  //  System.out.println("size is 0");
  for(int i = 0; i < v.size();i++) {
    Node n = v.get(i);
    int  edgeWeights=0;
    int depCount = 0;
    int beCount = 0;

    if(n.dependencies!=null)
      depCount = n.dependencies.length;

    if(n.backEdges != null)
      beCount = n.backEdges.length;

    int edgeCount = depCount + beCount;

    if(edgeCount >= maxEdgeCount) {
      maxEdgeCount = edgeCount;
      domEdgeNode = n;
    }

    if(n.weights!=null)
      for(int j = 0; j < n.weights.length;j++)
        edgeWeights+=n.weights[j];

    if(n.beWeights!=null)
      for(int j = 0; j < n.beWeights.length;j++)
        edgeWeights+=n.beWeights[j];

    if(edgeWeights >= maxEdgeWeight) {
      maxEdgeWeight = edgeWeights;
    }
  }
  return domEdgeNode.getName();
}

private void WriteOutputClusters(Vector<List> cVect) throws IOException {
  if(cVect==null) return;

  for(int i = 0; i < cVect.size(); i++) {
    List<Node> cluster = cVect.elementAt(i);
    String cName = findStrongestNode(cluster);

    writer_d.write("SS("+cName+".ss) = ");
    for(int j = 0; j < cluster.size(); j++) {
      Node n = (Node)cluster.get(j);
      writer_d.write(n.getName());
      if(j<(cluster.size()-1))
        writer_d.write(", ");
      else
        writer_d.write("\n");
    }
  }
}

public void write() {
  int technique = this.getOutputTechnique();
  String fileName = this.getCurrentName();

  switch(technique) {
    case GraphOutput.OUTPUT_ALL_LEVELS: {
      Graph gLvl = graph_d;

      while(gLvl.getGraphLevel() > 0) {
        fixupNodeList(gLvl);
        if(gLvl.getClusterNames().length <= 1) {
          gLvl = gLvl.getPreviousLevelGraph();
          continue;
        }
        String fName = fileName+"L"+gLvl.getGraphLevel()+".bunch";
        writeGraph(fName,gLvl);
        gLvl = gLvl.getPreviousLevelGraph();
      }

      fileName += ".bunch";
      writeGraph(fileName,graph_d.getMedianTree());

      break;
    }
    case GraphOutput.OUTPUT_MEDIAN_ONLY: {
      fileName += ".bunch";

      Graph g = graph_d;
      if (graph_d.isClusterTree())
        g = graph_d.getMedianTree();
      writeGraph(fileName,g);
      break;
    }
    case GraphOutput.OUTPUT_TOP_ONLY: {
      fileName += ".bunch";
      writeGraph(fileName,graph_d);
      break;
    }
    case GraphOutput.OUTPUT_DETAILED_LEVEL_ONLY: {
      fileName += ".bunch";
      Graph tmpG = graph_d;
      while(tmpG.getGraphLevel() > 0)
        tmpG=tmpG.getPreviousLevelGraph();

      writeGraph(fileName,tmpG);
      break;
    }
  }
}

private void writeGraph(String fileName, Graph g) {
  try {
    writer_d = new BufferedWriter(new FileWriter(fileName));
    generateOutput(g);
    writer_d.close();
  } catch (IOException e) {
    throw new RuntimeException(e);
  }
}

private void fixupNodeList(Graph g) {
  //this is basically a hack, and an update to the Graph class should
  //be made to ensure that this happens there
  Node [] nodeList = g.getNodes();
  int  [] clusters = g.getClusters();
  for(int i = 0; i < nodeList.length; i++)
    nodeList[i].cluster = clusters[i];
}


private void generateOutput(Graph g) throws IOException {

  Graph gBase = g;

  while (gBase.getGraphLevel() != 0)
    gBase = gBase.getPreviousLevelGraph();

  int[] clusters = gBase.getClusters();
  Node[] nodeList = gBase.getNodes();
  int nodes = nodeList.length;
  int[][] clustMatrix = new int[nodes][nodes+1];

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


   Node [] on = gBase.getOriginalNodes();
   if((on != null) &&(on.length != nodeList.length))
      checkForSpecialModules(gBase.getOriginalNodes());

   if(getWriteNestedLevels())
      genChildrenFromOneLevel(g);
   else
     generateClusters(g);

   on = gBase.getOriginalNodes();
   if((on != null) &&(on.length != nodeList.length))
      writeSpecialModules(gBase.getOriginalNodes());


   writeClosing();

}
}


