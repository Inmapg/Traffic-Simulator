package pr5.view.graphlayout;

import javax.swing.*;

import java.util.HashMap;
import java.util.Map;
import pr5.model.Junction;
import pr5.model.Road;
import pr5.model.RoadMap;

@SuppressWarnings("serial")
public class GraphLayout extends JPanel {

    private GraphComponent _graphComp;
    private RoadMap roadmap;

    public GraphLayout(RoadMap roadmap) {
        // default BorderLayout 
        this.roadmap = roadmap;
        initGUI();
    }

    private void initGUI() {
        _graphComp = new GraphComponent();
        add(_graphComp);
        generateGraph();
    }

    private void generateGraph() {

        Graph graph = new Graph();
        Map<Junction, Node> junctions = new HashMap<>();
        Map<Road, Edge> roads = new HashMap<>();

        roadmap.getJunctions().forEach((j) -> {
            Node node = new Node(j.getId());
            junctions.put(j, node);
            graph.addNode(node);
        });

        roadmap.getRoads().forEach((r) -> {
            Edge e = new Edge(r.getId(), junctions.get(r.getSource()), junctions.get(r.getDestination()), r.getLength());
            roads.put(r, e);
            r.getVehicleList().forEach((v) -> {
                roads.get(v.getRoad()).addDot(new Dot(v.getId(), v.getLocation()));
            }); // Se hace dentro, hay que pintar los coches que estén en las carreteras,
            // no todos los coches. Así se arregla el problema de location zero
            graph.addEdge(e);
        });

        _graphComp.setGraph(graph);

    }

    public void update(RoadMap roadmap) {
        this.roadmap = roadmap;
        generateGraph();
    }
}
