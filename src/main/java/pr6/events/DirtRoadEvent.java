package pr6.events;

import pr6.ini.IniSection;
import pr6.model.DirtRoad;
import pr6.model.RoadMap;

/**
 * Creates a new dirt road.
 */
public class DirtRoadEvent extends RoadEvent {

    /**
     * Class constructor specifying time, id, source junction, destination
     * junction, maximum speed and length.
     *
     * @param time When the event occurs
     * @param id
     * @param src Source junction ID
     * @param dest Destination junction ID
     * @param maxSpeed
     * @param length
     */
    public DirtRoadEvent(int time, String id, String src, String dest,
            int maxSpeed, int length) {
        super(time, id, src, dest, maxSpeed, length);
    }

    @Override
    public void execute(RoadMap roadmap) {
        try {
            roadmap.addRoad(new DirtRoad(id, length, maxSpeed,
                    roadmap.getJunction(src), roadmap.getJunction(dest)));
        } catch (NullPointerException e) {
            throw e;
        }
    }

    /**
     * Builds the dirt road event.
     *
     * @see Event.Builder
     */
    public static class Builder implements Event.Builder {

        @Override
        public Event parse(IniSection sec) {
            if (!"new_road".equals(sec.getTag())
                    || !"dirt".equals(sec.getValue("type"))) {
                return null;
            }
            return new DirtRoadEvent(
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
