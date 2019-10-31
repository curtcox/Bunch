package bunch.api;

public final class BunchEdge {

  final int weight;
  final BunchNode srcNode;
  final BunchNode destNode;

  public BunchEdge(int w, BunchNode src, BunchNode dest) {
    weight = w;
    srcNode = src;
    destNode = dest;
  }

  public int getWeight()
  { return weight;  }

  public BunchNode getSrcNode()
  { return srcNode; }

  public BunchNode getDestNode()
  { return destNode;  }
}