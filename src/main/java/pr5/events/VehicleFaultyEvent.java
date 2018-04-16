package pr5.events;

import pr5.ini.IniSection;
import pr5.model.RoadMap;

/**Breaks down an existing vehicle.
 * @author Inmapg
 * @author Arturacu
 * @version 2.0
 */
public class VehicleFaultyEvent extends Event{
    /**Duration*/
    private final int duration;
    /**List of vehicles*/
    private final String[] vehicles;
    
    /**Class constructor specifying time, list of vehicles and duration 
     * 
     * @param time Time
     * @param vehicles List of id from vehicles
     * @param duration Duration
     */
    public VehicleFaultyEvent(int time, String[] vehicles, int duration){
        super(time);
        this.duration = duration;
        this.vehicles = vehicles;
    }

    @Override
    public void execute(RoadMap roadmap) {
        for(String vehicleId : vehicles){
            roadmap.getVehicle(vehicleId).makeFaulty(duration);
        }
    }
    
     /**Builds the vehicle faulty event.
     * @see Event.Builder
     */
    public static class Builder implements Event.Builder {

        @Override
        public Event parse(IniSection sec) {
            if ( ! "make_vehicle_faulty".equals(sec.getTag())){
                return null;
            }
            return new VehicleFaultyEvent(
                    parseInt(sec, "time", 0),
                    parseStringList(sec, "vehicles"),
                    parseInt(sec, "duration", 1) 
            );
        }
    }

}
