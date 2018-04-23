package pr5.events;

import pr5.ini.IniSection;
import pr5.model.Road;
import pr5.model.RoadMap;

/**
 * Creates a new road.
 */
public class RoadEvent extends Event {

    protected final String id;
    /**
     * Source junction
     */
    protected final String src;
    /**
     * Destination junction
     */
    protected final String dest;
    protected final int maxSpeed;
    protected final int length;

    /**
     * Class constructor specifying time, id, source junction, destination
     * junction, maximum speed and length.
     *
     * @param time
     * @param id
     * @param src
     * @param dest
     * @param maxSpeed
     * @param length
     */
    public RoadEvent(int time, String id, String src, String dest, int maxSpeed,
            int length) {
        super(time);
        this.id = id;
        this.src = src;
        this.maxSpeed = maxSpeed;
        this.dest = dest;
        this.length = length;
    }

    @Override
    public void execute(RoadMap roadmap) {
        try {
            roadmap.addRoad(new Road(id, length, maxSpeed, roadmap.getJunction(src),
                    roadmap.getJunction(dest)));
        } catch (NullPointerException e) {
            throw e;
        }
    }

    public static class Builder implements Event.Builder {

        @Override
        public Event parse(IniSection sec) {
            if (!"new_road".equals(sec.getTag())) {
                return null;
            }
            return new RoadEvent(
                    parseInt(sec, "time", 0),
                    parseString(sec, "id"),
                    parseString(sec, "src"),
                    parseString(sec, "dest"),
                    parseInt(sec, "max_speed", 1),
                    parseInt(sec, "length", 1)
            );
        }

    }

}
