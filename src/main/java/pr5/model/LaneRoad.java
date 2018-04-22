package pr5.model;

import pr5.ini.IniSection;

/**
 * Defines a road with several lanes.
 * 
 * @see Road
 */
public class LaneRoad extends Road {
    private static final String TYPE = "lanes";
    private final int numberOfLanes;
    
    /**
     * Class constructor specifying id, length, maximum speed, source junction, 
     * destination junction and the number of lanes.
     * 
     * @param id 
     * @param length 
     * @param maxSpeed
     * @param sourceJunction 
     * @param destinationJunction 
     * @param numberOfLanes 
     */
    public LaneRoad(String id, int length, int maxSpeed, Junction sourceJunction,
            Junction destinationJunction, int numberOfLanes) {
        super(id, length, maxSpeed, sourceJunction, destinationJunction);
        this.numberOfLanes = numberOfLanes;
    }
    
    @Override
    protected int reduceSpeedFactor(int brokenVehicles){
        return (numberOfLanes > brokenVehicles) ? 1 : 2;
    }
    
    @Override
    protected int calculateBaseSpeed(){
        return (int) Math.min(maxSpeed, 
                (maxSpeed*numberOfLanes)/(Math.max(1, vehiclesList.sizeOfValues())) + 1);
    }
    
    @Override
    protected void fillReportDetails(IniSection sec) {
        sec.setValue("type", TYPE);
        super.fillReportDetails(sec);
    }
}
