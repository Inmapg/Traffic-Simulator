package pr5.events;

import pr5.ini.IniSection;
import pr5.model.RoadMap;
import pr5.model.RoundRobinJunction;

/**Creates a new round robin junction.
 * @author Inmapg
 * @author Arturacu
 * @version 3.0
 */
public class RoundRobinJunctionEvent extends JunctionEvent{
    /**Minimum time slice*/
    private int minTimeSlice;
    /**Maximum time slice*/
    private int maxTimeSlice;
    /**
     * Class constructor specifying time and id
     * @param time Internal time
     * @param id Identifier
     * @param minTimeSlice Minimum time slice
     * @param maxTimeSlice Maximum time slice
     */
    public RoundRobinJunctionEvent(int time, String id, int minTimeSlice, int maxTimeSlice) {
        super(time, id);
        this.minTimeSlice = minTimeSlice;
        this.maxTimeSlice = maxTimeSlice;
    }
    
    @Override
    public void execute(RoadMap roadmap) {
        roadmap.addJunction(new RoundRobinJunction(id, minTimeSlice, maxTimeSlice));
    }
    
    /**Builds the round robin junction event.
     * @see Event.Builder
     */
    public static class Builder implements Event.Builder {
    
    @Override
    public Event parse(IniSection sec) {
        if ( !"new_junction".equals(sec.getTag()) || !"rr".equals(sec.getValue("type"))) return null;
        return new RoundRobinJunctionEvent(
                parseInt(sec, "time", 0),
                parseString(sec, "id"),
                parseInt(sec, "min_time_slice", 1),
                parseInt(sec, "max_time_slice", 1));
      }       
    }
    
}
