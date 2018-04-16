package pr5.events;

import pr5.ini.IniSection;
import pr5.model.Bike;
import pr5.model.RoadMap;

/**Creates a new bike.
 * @author Inmapg
 * @author Arturacu
 * @version 2.0
 */
public class BikeEvent extends VehicleEvent{
    
    /**Class constructor specifying time, id, itinerary and maximum speed.
     * 
     * @param time When the event occurs
     * @param id Identification
     * @param itinerary Itinerary followed
     * @param maxSpeed Maximum speed
     */
    public BikeEvent(int time, String id, String[] itinerary, int maxSpeed){
        super(time, id, maxSpeed, itinerary);
    }
    
    @Override
    public void execute(RoadMap roadmap) {
        roadmap.addVehicle(new Bike(id,  maxSpeed, roadmap.getItinerary(itinerary)));
    }
    
    /**Builds the bike event.
     * @see Event.Builder
     */
    public static class Builder implements Event.Builder {

       @Override
        public Event parse(IniSection sec) {
            if (!"new_vehicle".equals(sec.getTag()) || !"bike".equals(sec.getValue("type"))){
                return null;
            }
            return new BikeEvent(
                parseInt(sec, "time", 0),
                parseString(sec, "id"),
                parseStringList(sec, "itinerary"),
                parseInt(sec, "max_speed", 1) 
            );
        }
  
    }
}
