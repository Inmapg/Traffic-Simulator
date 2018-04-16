package pr5.model;

import java.util.List;
import pr5.ini.IniSection;

/**Defines bike: a type of vehicle.
 * 
 * @author Inmapg
 * @author Arturacu
 * @version 2.0
 * @see Vehicle
 */
public class Bike extends Vehicle {
    /**Type of vehicle*/
    private static final String TYPE = "bike";
    
    /**Class constructor specifying id, maximum speed and itinerary
     * 
     * @param id Identification
     * @param maxSpeed Maximum speed
     * @param itinerary Itinerary
     */
    public Bike(String id, int maxSpeed, List<Junction> itinerary) {
        super(id, maxSpeed, itinerary);
    }
    
    @Override
    public void makeFaulty(int counter){
        if(super.faulty > 0 || super.currentSpeed > super.maxSpeed/2 ){
            super.makeFaulty(counter);
        }
    }
    
    @Override
    public void fillReportDetails(IniSection sec) {
        sec.setValue("type", TYPE);
        super.fillReportDetails(sec);
    }    
}
