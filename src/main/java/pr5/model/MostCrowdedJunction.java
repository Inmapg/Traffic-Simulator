package pr5.model;

import pr5.ini.IniSection;

/**
 * Creates a new most crowded junction.
 */
public class MostCrowdedJunction extends TimeSliceJunction{
    private static final String TYPE = "mc";
    protected TimeSliceIncomingRoad currentRoad;
    
    /**
     * Class constructor specifying id.
     * 
     * @param id
     */
    public MostCrowdedJunction(String id) {
        super(id);
        
    }
    
    /**
     * Selects the following most crowded road.
     */
    private void updateCurrentRoad(){
        if(incomingRoadMap.values().size() > 1){
             if (nextRoad == null || !nextRoad.hasNext()) {
                    nextRoad = incomingRoadMap.keySet().iterator();
             }
            TimeSliceIncomingRoad mostCrowdedRoad = (TimeSliceIncomingRoad) incomingRoadMap.get(nextRoad.next());
            for(IncomingRoad ir : incomingRoadMap.values()){
                if(ir.sizeOfQueue() > mostCrowdedRoad.sizeOfQueue()){
                 mostCrowdedRoad = (TimeSliceIncomingRoad) ir;
                }
            }
            currentRoad = mostCrowdedRoad;
        }
        
    }
    
    @Override
    protected void switchLights() { 
        if(currentRoad == null){
            nextRoad = incomingRoadMap.keySet().iterator();
            currentRoad = (TimeSliceIncomingRoad) incomingRoadMap.get(nextRoad.next());
            currentRoad.onGreenLight();
            currentRoad.setIntervalTime(Math.max(currentRoad.sizeOfQueue()/2, 1));
        }
        else if(currentRoad.timeIsOver()){ // if the interval time is used up
               currentRoad.offGreenLight();
               updateCurrentRoad();
               currentRoad.onGreenLight();
               currentRoad.setIntervalTime(Math.max(currentRoad.sizeOfQueue()/2, 1));
               currentRoad.reset();
        }
         else{
            currentRoad.advanceFirstVehicle();
         }
           
     }

    
    @Override
     protected void fillReportDetails(IniSection sec) {
        super.fillReportDetails(sec);
        sec.setValue("type", TYPE);
    }
}
