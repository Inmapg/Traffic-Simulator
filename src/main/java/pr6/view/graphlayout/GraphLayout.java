package pr6.view.graphlayout;

import javax.swing.*;

import java.util.HashMap;
import java.util.Map;
import pr6.model.Junction;
import pr6.model.Road;
import pr6.model.RoadMap;

@SuppressWarnings("serial")
/**
 * Represents the layout of the graph.
 */
public class GraphLayout extends JPanel {

    private GraphComponent graphComponent = new GraphComponent();
    private RoadMap roadmap;

    /**
     * Class constructor specifying the roadmap.
     *
     * @param roadmap
     */
    public GraphLayout(RoadMap roadmap) {
        this.roadmap = roadmap;
        initGUI();
    }

    /**
     * Adds the graph.
     */
    private void initGUI() {
        add(graphComponent);
        generateGraph();
    }

    /**
     * Creates the graph.
     */
    private void generateGraph() {

        Graph graph = new Graph();
        Map<Junction, Node> junctionsMap = new HashMap<>();
        Map<Road, Edge> roadsMap = new HashMap<>();

        roadmap.getJunctions().forEach((j) -> {
            Node node = new Node(j.getId());
            junctionsMap.put(j, node);
            graph.addNode(node);
        });

        roadmap.getRoads().forEach((r) -> {
            Edge e = new Edge(r.getId(), junctionsMap.get(r.getSource()),
                    junctionsMap.get(r.getDestination()), r.getLength(),
                    r.getDestination().isTrafficLightOn(r));
            roadsMap.put(r, e);
            r.getVehicleList().forEach((v) -> {
                roadsMap.get(v.getRoad()).addDot(new Dot(v.getId(), v.getLocation(), v.getFaultyTime()));
            }); // Se hace dentro, hay que pintar los coches que estén en las carreteras,
            // no todos los coches. Así se arregla el problema de location zero
            graph.addEdge(e);
        });

        graphComponent.setGraph(graph);

    }

    /**
     * Updates the graph and its roadmap.
     *
     * @param roadmap
     */
    public void update(RoadMap roadmap) {
        this.roadmap = roadmap;
        generateGraph();
    }
}
