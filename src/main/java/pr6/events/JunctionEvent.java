package pr6.events;

import java.util.Map;
import pr6.ini.IniSection;
import pr6.model.Junction;
import pr6.model.RoadMap;

/**
 * Creates a new junction.
 */
public class JunctionEvent extends Event {

    protected final String id;

    /**
     * Class constructor specifying time and id.
     *
     * @param time When the event occurs
     * @param id
     */
    public JunctionEvent(int time, String id) {
        super(time);
        this.id = id;
    }

    @Override
    public void execute(RoadMap roadmap) {
        roadmap.addJunction(new Junction(id));
    }

    @Override
    public void describe(Map<String, String> out) {
        super.describe(out);
        out.put("Type", "New junction " + id);
    }

    /**
     * Builds the junction event.
     *
     * @see Event.Builder
     */
    public static class Builder implements Event.Builder {

        @Override
        public Event parse(IniSection sec) {
            if (!"new_junction".equals(sec.getTag())) {
                return null;
            }
            return new JunctionEvent(parseInt(sec, "time", 0), parseString(sec, "id"));
        }
    }
}
