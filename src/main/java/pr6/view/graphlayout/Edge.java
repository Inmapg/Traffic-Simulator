package pr6.view.graphlayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an edge (road) on the graph.
 */
public class Edge {

    private final String id;
    private final Node source;
    private final Node target;
    private final int length;
    private final List<Dot> dots = new ArrayList<>();
    private final boolean green;

    /**
     * Class constructor specifying edge id, source node, target node, edge
     * length and a boolean representing the status of the traffic light.
     *
     * @param id
     * @param source
     * @param target
     * @param length
     * @param green Traffic light status
     */
    public Edge(String id, Node source, Node target, int length, boolean green) {
        this.source = source;
        this.target = target;
        this.id = id;
        this.length = length;
        this.green = green;
    }

    /**
     * Adds a dot to the edge
     *
     * @param dot
     */
    public void addDot(Dot dot) {
        dots.add(dot);
    }

    /**
     * @return the edge id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the source node
     */
    public Node getSource() {
        return source;
    }

    /**
     * @return the target node
     */
    public Node getTarget() {
        return target;
    }

    /**
     * @return the length of the edge
     */
    public int getLength() {
        return length;
    }

    /**
     * @return the existing dots of the edge
     */
    public List<Dot> getDots() {
        return dots;
    }

    /**
     * @return if the traffic light is on
     */
    public boolean isGreen() {
        return green;
    }
}
