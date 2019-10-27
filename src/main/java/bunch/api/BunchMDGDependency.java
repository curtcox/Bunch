package bunch.api;

public final class BunchMDGDependency {

  String srcNode;
  String destNode;
  int    edgeW;

  public BunchMDGDependency(String s, String d, int w) {
    srcNode = s;
    destNode = d;
    edgeW = w;
  }

  public String getSrcNode()
  { return srcNode; }

  public String getDestNode()
  { return destNode; }

  public int getEdgeW()
  { return edgeW; }
}