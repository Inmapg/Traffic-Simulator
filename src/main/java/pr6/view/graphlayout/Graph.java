package pr6.view.graphlayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a graph that contais a list of edges and another of nodes.
 */
public class Graph {

    private final List<Edge> edges = new ArrayList<>();
    private final List<Node> nodes = new ArrayList<>();

    /**
     * Class constructor.
     */
    public Graph() {
    }

    /**
     * Adds a new edge to the graph.
     *
     * @param edge
     */
    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    /**
     * Adds a new node to the graph.
     *
     * @param node
     */
    public void addNode(Node node) {
        nodes.add(node);
    }

    /**
     * @return the list of edges
     */
    public List<Edge> getEdges() {
        return edges;
    }

    /**
     * @return the list of nodes
     */
    public List<Node> getNodes() {
        return nodes;
    }
}
