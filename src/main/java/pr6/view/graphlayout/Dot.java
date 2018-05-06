package pr6.view.graphlayout;

/**
 * Represents a do (vehicle) on the graph.
 */
public class Dot {

    private final String id;
    private final int location;
    private final boolean faulty;

    /**
     * Class constructor specifying id, location and faulty time.
     *
     * @param id
     * @param location
     * @param faulty Breakdown counter
     */
    public Dot(String id, int location, int faulty) {
        this.id = id;
        this.location = location;
        this.faulty = (faulty > 0);
    }

    /**
     *
     * @return the dot id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @return the location of the dot
     */
    public int getLocation() {
        return location;
    }

    /**
     *
     * @return if the dot (vehicle) is faulty
     */
    public boolean isFaulty() {
        return faulty;
    }

}
