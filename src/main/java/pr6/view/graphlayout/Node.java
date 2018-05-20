package pr6.view.graphlayout;

/**
 * Represents a node (junction) on the graph.
 */
public class Node {

    private String id;

    /**
     * Class constructor specifying node id.
     *
     * @param id
     */
    public Node(String id) {
        this.id = id;
    }

    /**
     * @return the node id
     */
    public String getId() {
        return id;
    }
}
