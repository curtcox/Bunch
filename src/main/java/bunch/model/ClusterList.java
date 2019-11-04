package bunch.model;

import java.util.*;

public final class ClusterList implements Iterable<Cluster> {

    private final List<Cluster> clusters = new ArrayList<>();

    public void add(Cluster cluster) {
        clusters.add(cluster);
    }

    public int size() {
        return clusters.size();
    }

    public Cluster get(Integer index) {
        return clusters.get(index);
    }

    @Override
    public Iterator<Cluster> iterator() {
        return clusters.iterator();
    }

    public void clear() {
        clusters.clear();
    }
}
