package bunch.util;

import java.util.*;

import bunch.api.BunchMDG;
import bunch.model.Graph;
import bunch.model.Node;

public final class BunchUtilities {

private final static double defaultPrecision = 0.0001;

public static boolean compareGreater(double a, double b) {
  int ia = (int)(a/defaultPrecision);
  int ib = (int)(b/defaultPrecision);

  return (ia > ib);
}

public static boolean compareGreaterEqual(double a, double b) {
  int ia = (int)(a/defaultPrecision);
  int ib = (int)(b/defaultPrecision);

  return (ia >= ib);
}

public static Graph toInternalGraph(BunchMDG bunchMDG) {
  var al = new ArrayList<>(bunchMDG.getMDGEdges());
  Hashtable<String,ParserNode> nodes = new Hashtable<>();

    for (Object o : al) {
        bunch.api.BunchMDGDependency bmd = (bunch.api.BunchMDGDependency) o;

        ParserNode currentNode;
        ParserNode targetNode;

        if (bmd.getSrcNode().equals(bmd.getDestNode()))
            continue;

        currentNode = nodes.get(bmd.getSrcNode());
        //Node is not known yet, add it to the list
        if (currentNode == null) {
            currentNode = new ParserNode(bmd.getSrcNode());
            nodes.put(bmd.getSrcNode(), currentNode);
        }

        targetNode = nodes.get(bmd.getDestNode());
        //Node is not known yet, add it to the list
        if (targetNode == null) {
            targetNode = new ParserNode(bmd.getDestNode());
            nodes.put(bmd.getDestNode(), targetNode);
        }

        String src = bmd.getSrcNode();
        String dep = bmd.getDestNode();
        Integer w = bmd.getEdgeW();  //The edge weight

        //Add source to target, and target to source if they don't already
        //exist as forward and backward dependencies
        if (!currentNode.dependencies.containsKey(dep)) {
            currentNode.dependencies.put(dep, dep);
            currentNode.dWeights.put(dep, w);
            //System.out.println("Adding weight " + w);
        } else {
            Integer wExisting = currentNode.dWeights.get(dep);
            Integer wtemp = w + wExisting;
            currentNode.dWeights.put(dep, wtemp);
        }

        if (!targetNode.backEdges.containsKey(src)) {
            targetNode.backEdges.put(src, src);
            targetNode.beWeights.put(src, w);
        } else {
            Integer wExisting = targetNode.beWeights.get(src);
            Integer wtemp = w + wExisting;
            targetNode.beWeights.put(src, wtemp);
        }

        //----------------
        //DataStructure updated for edge now
        //----------------
    }

  //now deal with Bunch Format -- Generate bunch graph object
  int sz = nodes.size();
  Hashtable<String,Integer> nameTable = new Hashtable<>();

  //build temporary name to ID mapping table
  Object [] oa = nodes.keySet().toArray();
  for (int i = 0; i < oa.length; i++) {
    String n = (String)oa[i];
    nameTable.put(n, i);
  }

  //now build the graph
  Graph retGraph = new Graph(nodes.size());
  retGraph.clear();
  Node[] nodeList = retGraph.getNodes();

  //now setup the datastructure
  Object [] nl = nodes.values().toArray();
  for(int i = 0; i < nl.length; i++) {
    Node n = new Node();
    nodeList[i]  = n;
    ParserNode p = (ParserNode)nl[i];
    n.setName(p.name);
      n.nodeID = nameTable.get(p.name);
    n.dependencies = ht2ArrayFromKey(nameTable,p.dependencies);
    n.weights = ht2ArrayValFromKey(p.dWeights);
    n.backEdges = ht2ArrayFromKey(nameTable,p.backEdges);
    n.beWeights = ht2ArrayValFromKey(p.beWeights);
  }

  return retGraph;
}

/**
 * Helper routine that given a hashtable of values and a key returns an
 * object array of values
 */
private static int[] ht2ArrayFromKey(Hashtable key, Hashtable values) {
    int [] retArray = new int[values.size()];

    try {
      Object [] oa = values.keySet().toArray();
      for(int i = 0; i < oa.length; i++) {
        String s = (String)oa[i];
        Integer val = (Integer)key.get(s);
        retArray[i] = val;
      }
      return retArray;
    } catch(Exception e) {
        throw new RuntimeException(e);
    }
}

/**
 * Since this is a hashtable of hashtables we want to return
 * the contents of the inner hashtable in an integer array format.
 */
private static int[] ht2ArrayValFromKey(Hashtable values) {
    int [] retArray = new int[values.size()];

    try{
      Object [] oa = values.keySet().toArray();
      for(int i = 0; i < oa.length; i++)
      {
        String s = (String)oa[i];
        Integer value = (Integer)values.get(s);
        retArray[i] = value;
      }
      return retArray;
    } catch(Exception e) {
        throw new RuntimeException(e);
    }
}

}

