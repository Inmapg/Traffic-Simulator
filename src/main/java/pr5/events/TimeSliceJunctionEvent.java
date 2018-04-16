package pr5.events;

import pr5.ini.IniSection;
import pr5.model.RoadMap;
import pr5.model.TimeSliceJunction;

/**Creates a new time slice junction.
 * @author Inmapg
 * @author Arturacu
 * @version 2.0
 */
public class TimeSliceJunctionEvent extends JunctionEvent{
    
    
    public TimeSliceJunctionEvent(int time, String id) {
        super(time, id);
    }
    
    @Override
    public void execute(RoadMap roadmap) {
        roadmap.addJunction(new TimeSliceJunction(id));
    }
    
    /**Builds the round robin junction event.
     * @see Event.Builder
     */
    public static class Builder implements Event.Builder {
    
    @Override
    public Event parse(IniSection sec) {
        if ( !"new_junction".equals(sec.getTag()) || !"mc".equals(sec.getValue("type"))) return null;
        return new TimeSliceJunctionEvent(
                parseInt(sec, "time", 0),
                parseString(sec, "id"));
      }       
    }
}
