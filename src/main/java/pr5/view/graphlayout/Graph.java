package pr5.view.graphlayout;

import java.util.ArrayList;
import java.util.List;

public class Graph {

    private final List<Edge> _edges = new ArrayList<>();
    private final List<Node> _nodes = new ArrayList<>();

    public Graph() {
    }

    public void addEdge(Edge e) {
        _edges.add(e);
    }

    public void addNode(Node n) {
        _nodes.add(n);
    }

    public List<Edge> getEdges() {
        return _edges;
    }

    public List<Node> getNodes() {
        return _nodes;
    }
}
