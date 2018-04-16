package pr5.events;

import pr5.ini.IniSection;
import pr5.model.LaneRoad;
import pr5.model.RoadMap;

/**Creates a new lane road.
 * @author Inmapg
 * @author Arturacu
 * @version 2.0
 */
public class LaneRoadEvent extends RoadEvent {
    /**Number of lanes of the road*/
    private final int numberOfLanes;
    
    /**Class constructor specifying time, id, source junction, destination junction, maximum speed,
     * length and number of lanes.
     * 
     * @param time When the event occurs
     * @param id Identification
     * @param src Source junction ID
     * @param dest Destination junction ID
     * @param maxSpeed Maximum speed
     * @param length Length
     * @param numberOfLanes Number of lanes
     */
    public LaneRoadEvent(int time, String id, String src, String dest, int maxSpeed, int length, int numberOfLanes) {
        super(time, id, src, dest, maxSpeed, length);
        this.numberOfLanes = numberOfLanes;
    }
    
    @Override
    public void execute(RoadMap roadmap) {
        roadmap.addRoad(new LaneRoad(id, length, maxSpeed, roadmap.getJunction(src), roadmap.getJunction(dest), numberOfLanes));
    }
    
    /**Builds the lane road event.
     * @see Event.Builder
     */
    public static class Builder implements Event.Builder {
    
    @Override
    public Event parse(IniSection sec) {
         if ( !"new_road".equals(sec.getTag()) || !"lanes".equals(sec.getValue("type"))) return null;
        return new LaneRoadEvent(
                parseInt(sec, "time", 0),
                parseString(sec, "id"),
                parseString(sec, "src"),
                parseString(sec, "dest"),
                parseInt(sec, "max_speed", 1),
                parseInt(sec, "length", 1),
                parseInt(sec, "lanes", 1));
      }       
    }
}
