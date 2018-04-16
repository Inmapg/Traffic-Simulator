package pr5.events;

import pr5.ini.IniSection;
import pr5.model.DirtRoad;
import pr5.model.RoadMap;

/**Creates a new dirt road.
 * @author Inmapg
 * @author Arturacu
 * @version 2.0
 */
public class DirtRoadEvent extends RoadEvent {
    
    /**Class constructor specifying time, id, source junction, destination junction, maximum speed
     * and length.
     * 
     * @param time When the event occurs
     * @param id Identification
     * @param src Source junction ID
     * @param dest Destination junction ID
     * @param maxSpeed Maximum speed
     * @param length Length
     */
    public DirtRoadEvent(int time, String id, String src, String dest, int maxSpeed, int length) {
        super(time, id, src, dest, maxSpeed, length);
    }
    
    @Override
    public void execute(RoadMap roadmap) {
        roadmap.addRoad(new DirtRoad(id, length, maxSpeed, roadmap.getJunction(src), roadmap.getJunction(dest)));
    }
    
    /**Builds the dirt road event.
     * @see Event.Builder
     */
    public static class Builder implements Event.Builder {

        @Override
        public Event parse(IniSection sec) {
            if ( !"new_road".equals(sec.getTag()) || !"dirt".equals(sec.getValue("type"))){
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
