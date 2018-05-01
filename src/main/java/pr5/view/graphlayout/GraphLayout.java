package pr5.view.graphlayout;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import pr5.model.Junction;
import pr5.model.Road;
import pr5.model.RoadMap;
import pr5.model.Vehicle;

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

        for (Junction j : roadmap.getJunctions()) {
            Node node = new Node(j.getId());
            junctions.put(j, node);
            graph.addNode(node);
        }
        for (Road r : roadmap.getRoads()) {
            Edge e = new Edge(r.getId(), junctions.get(r.getSource()), junctions.get(r.getDestination()), r.getLength());
            roads.put(r, e);
            graph.addEdge(e);
        }
        for (Vehicle v : roadmap.getVehicles()) {
            roads.get(v.getRoad()).addDot(new Dot(v.getId(), v.getLocation()));
        }
        _graphComp.setGraph(graph);

    }

    public void update(RoadMap roadmap) {
        this.roadmap = roadmap;
        generateGraph();
    }
}
