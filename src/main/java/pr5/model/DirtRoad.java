package pr5.model;

import pr5.ini.IniSection;

/**Defines a dirt road.
 * 
 * @author Inmapg
 * @author Arturacu
 * @version 2.0
 * @see Road
 */
public class DirtRoad extends Road {
    /**Type of road*/
    private static final String TYPE = "dirt";
    
    /**Class constructor specifying id, length, maximum speed, source junction and 
     * destination junction.
     * 
     * @param id Identification
     * @param length Length
     * @param maxSpeed Maximum speed
     * @param sourceJunction Source junction
     * @param destinationJunction Destination junction
     */
    public DirtRoad(String id, int length, int maxSpeed, Junction sourceJunction, Junction destinationJunction) {
        super(id, length, maxSpeed, sourceJunction, destinationJunction);
    }
    
    @Override
    protected int reduceSpeedFactor(int brokenVehicles){
        return 1+brokenVehicles;
    }
    
    @Override
    protected int calculateBaseSpeed(){
        return maxSpeed;
    }
    
    @Override
    protected void fillReportDetails(IniSection sec) {
        sec.setValue("type", TYPE);
        super.fillReportDetails(sec);
    }
}
