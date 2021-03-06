package pr6.events;

import pr6.ini.IniSection;
import pr6.model.RoadMap;
import pr6.model.RoundRobinJunction;

/**
 * Creates a new round robin junction.
 */
public class RoundRobinJunctionEvent extends JunctionEvent {

    private int minTimeSlice;
    private int maxTimeSlice;

    /**
     * Class constructor specifying time and id
     *
     * @param time
     * @param id
     * @param minTimeSlice
     * @param maxTimeSlice
     */
    public RoundRobinJunctionEvent(int time, String id, int minTimeSlice,
            int maxTimeSlice) {
        super(time, id);
        this.minTimeSlice = minTimeSlice;
        this.maxTimeSlice = maxTimeSlice;
    }

    @Override
    public void execute(RoadMap roadmap) {
        roadmap.addJunction(new RoundRobinJunction(id, minTimeSlice,
                maxTimeSlice));
    }

    /**
     * Builds the round robin junction event.
     *
     * @see Event.Builder
     */
    public static class Builder implements Event.Builder {

        @Override
        public Event parse(IniSection sec) {
            if (!"new_junction".equals(sec.getTag())
                    || !"rr".equals(sec.getValue("type"))) {
                return null;
            }
            return new RoundRobinJunctionEvent(
                    parseInt(sec, "time", 0), parseString(sec, "id"),
                    parseInt(sec, "min_time_slice", 1),
                    parseInt(sec, "max_time_slice", 1));
        }
    }
}
