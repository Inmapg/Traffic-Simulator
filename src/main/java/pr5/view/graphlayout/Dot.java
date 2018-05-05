package pr5.view.graphlayout;

public class Dot {

    private final String _id;
    private final int _location;
    private final boolean _faulty;
    public Dot(String id, int location, int faulty) {
        _id = id;
        _location = location;
        _faulty = (faulty > 0);
    }
    
    public String getId() {
        return _id;
    }

    public int getLocation() {
        return _location;
    }

    public boolean isFaulty(){
        return _faulty;
    }
    
}
