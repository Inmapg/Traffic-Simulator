package pr5.view.graphlayout;

import java.util.ArrayList;
import java.util.List;

public class Edge {

    private final String _id;
    private final Node _source;
    private final Node _target;
    private final int _length;
    private final List<Dot> _dots = new ArrayList<>();
    private final boolean _green;
    
    public Edge(String id, Node source, Node target, int length, boolean green){
        _source = source;
        _target = target;
        _id = id;
        _length = length;
        _green = green;
    }

    public void addDot(Dot e) {
        _dots.add(e);
    }

    public String getId() {
        return _id;
    }

    public Node getSource() {
        return _source;
    }

    public Node getTarget() {
        return _target;
    }

    public int getLength() {
        return _length;
    }

    public List<Dot> getDots() {
        return _dots;
    }
    public boolean isGreen(){
        return _green;
    }
}
