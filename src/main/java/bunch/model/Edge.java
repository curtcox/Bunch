package bunch.model;

class Edge {

    public Node from_d, to_d;

    public Edge(Node from, Node to) {
        from_d = from;
        to_d = to;
    }

    public Node getFrom()
    {
        return from_d;
    }

    public Node getTo()
    {
        return to_d;
    }

    public boolean equalByCluster(Edge e) {
        if (from_d.cluster == e.getFrom().cluster &&
                to_d.cluster == e.getTo().cluster)
            return true;
        else
            return false;
    }

}
