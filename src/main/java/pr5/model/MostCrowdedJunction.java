package pr5.model;

import java.util.Iterator;
import pr5.ini.IniSection;

/**Creates a new most crowded junction.
 * @author Inmapg
 * @author Arturacu
 * @version 2.0
 */
public class MostCrowdedJunction extends TimeSliceJunction{
    
    private static final String TYPE = "mc";
    protected TimeSliceIncomingRoad currentRoad;
    
    public MostCrowdedJunction(String id) {
        super(id);
        
    }
    
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

    
     protected void fillReportDetails(IniSection sec) {
        super.fillReportDetails(sec);
        sec.setValue("type", TYPE);
    }
}
