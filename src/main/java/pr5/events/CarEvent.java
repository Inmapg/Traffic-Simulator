package pr5.events;

import pr5.ini.IniSection;
import pr5.model.Car;
import pr5.model.RoadMap;

/**Creates a new car.
 * @author Inmapg
 * @author Arturacu
 * @version 2.0
 */
public class CarEvent extends VehicleEvent {
    /**Resistance*/
    private final int resistance;
    /**Probability of breaking*/
    private final double faultProbability;
    /**Maximum duration if broken*/
    private final int maxFaultDuration;
    /**Seed for random number generator*/
    private final long seed;
    
    /**Class constructor specifying time, id, itinerary, maximum speed, resistance, probability
     * of breaking, maximum duration if broken and seed.
     * 
     * @param time When the event occurs
     * @param id Identification
     * @param itinerary Itinerary followed
     * @param maxSpeed Maximum speed
     * @param resistance Resistance
     * @param faultProbability Probability of breaking
     * @param maxFaultDuration Maximum duration if broken
     * @param seed  Seed for random number generator
     */
    public CarEvent(int time, String id, String[] itinerary, int maxSpeed, int resistance, 
            double faultProbability, int maxFaultDuration, long seed){
        super(time, id, maxSpeed, itinerary);
        this.maxFaultDuration = maxFaultDuration;
        this.resistance = resistance;
        this.faultProbability = faultProbability;
        this.seed = seed;
    }
    
    @Override
    public void execute(RoadMap roadmap) {
        roadmap.addVehicle(new Car(id, maxSpeed, roadmap.getItinerary(itinerary), resistance, faultProbability, maxFaultDuration, 
        seed));
    }

    /**Builds the car event.
     * @see Event.Builder
     */
    public static class Builder implements Event.Builder {

        @Override
        public Event parse(IniSection sec) {
            if (!"new_vehicle".equals(sec.getTag()) || !"car".equals(sec.getValue("type"))){
                return null;
            }
            return new CarEvent(
                parseInt(sec, "time", 0),
                parseString(sec, "id"),
                parseStringList(sec, "itinerary"),
                parseInt(sec, "max_speed", 1),
                parseInt(sec, "resistance", 1),
                parseDouble(sec, "fault_probability", 0, 1),
                parseInt(sec, "max_fault_duration", 0),
                parseLongOrMills(sec, "seed")
            );
        }
  
    }
    
}
