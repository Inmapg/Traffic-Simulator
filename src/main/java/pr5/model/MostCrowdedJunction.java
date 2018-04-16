package pr5.model;

/**Creates a new most crowded junction.
 * @author Inmapg
 * @author Arturacu
 * @version 2.0
 */
public class MostCrowdedJunction extends TimeSliceJunction{
    
    public MostCrowdedJunction(String id) {
        super(id);
        
    }
 @Override
    protected void switchLights() {
        if (nextRoad == null || !nextRoad.hasNext()) {
            nextRoad = incomingRoadMap.keySet().iterator();
        }
        if(currentRoad == null){
            currentRoad = nextRoad.next();
            incomingRoadMap.get(currentRoad).onGreenLight();
            incomingRoadMap.get(currentRoad).advanceFirstVehicle();
           // lastGreenLightRoad = currentRoad; not needed here, the switching lights control has been modified in this type of junction
        }
        else{
            
        }
        
    }
}
