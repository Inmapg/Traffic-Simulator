package pr5.events;

import pr5.ini.IniSection;
import pr5.model.RoadMap;
import pr5.model.MostCrowdedJunction;

/**Creates a new most crowded junction.
 * @author Inmapg
 * @author Arturacu
 * @version 2.0
 */
public class MostCrowdedJunctionEvent extends JunctionEvent{
    /**
     * Class constructor specifying time and id
     * @param time Internal time
     * @param id Identifier
     */
    public MostCrowdedJunctionEvent(int time, String id) {
        super(time, id);
    }
    
    @Override
    public void execute(RoadMap roadmap) {
        roadmap.addJunction(new MostCrowdedJunction(id));
    }
    
    /**Builds the most crowded junction event.
     * @see Event.Builder
     */
    public static class Builder implements Event.Builder {
    
    @Override
    public Event parse(IniSection sec) {
        if ( !"new_junction".equals(sec.getTag()) || !"mc".equals(sec.getValue("type"))) return null;
        return new MostCrowdedJunctionEvent(
                parseInt(sec, "time", 0),
                parseString(sec, "id"));
      }       
    }
}
