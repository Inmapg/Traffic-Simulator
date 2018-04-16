// Not finished. Not working
package pr5.model;

/**Defines a circular junction.
 * 
 * @author Inmapg
 * @author Arturacu
 * @version 2.0
 * @see Junction
 */
public class RoundRobinJunction extends TimeSliceJunction {
    /**Type of junction*/
    private static final String TYPE = "rr";
    /**Minimum time slice*/
    private final int minTimeSlice;
    /**Maximum time slice*/
    private final int maxTimeSlice;
    
    /**Class constructor specifying id, minimum time slice and maximum time slice.
     * 
     * @param id Identification
     * @param minTimeSlice Minimum time slice
     * @param maxTimeSlice Maximum time slice
     */
    public RoundRobinJunction(String id, int minTimeSlice, int maxTimeSlice) {
        super(id);
        this.minTimeSlice = minTimeSlice;
        this.maxTimeSlice = maxTimeSlice;
    }
    
    @Override
    protected IncomingRoad createIncomingRoadQueue(Road r) {
        TimeSliceIncomingRoad newIncomingRoad = new TimeSliceIncomingRoad(r);
        newIncomingRoad.setIntervalTime(maxTimeSlice);
        return newIncomingRoad;
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
            lastGreenLightRoad = currentRoad;
        }
        else{
            TimeSliceIncomingRoad currentSliceIncomingRoad = (TimeSliceIncomingRoad) incomingRoadMap.get(currentRoad);
            if(currentSliceIncomingRoad.timeIsOver()){
                currentSliceIncomingRoad.offGreenLight();
                if(currentSliceIncomingRoad.completelyUsed()){
                  //  currentSliceIncomingRoad.setIntervalTime(Math.min());
                }
            }  
        }
    }
    
}
