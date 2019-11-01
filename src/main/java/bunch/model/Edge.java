package bunch.model;

final class Edge {

    private final Node from_d;
    private final Node to_d;

    public Edge(Node from, Node to) {
        from_d = from;
        to_d = to;
    }

    private Node getFrom()
    {
        return from_d;
    }

    private Node getTo()
    {
        return to_d;
    }

    public boolean equalByCluster(Edge e) {
        return from_d.cluster == e.getFrom().cluster &&
                to_d.cluster == e.getTo().cluster;
    }

}
