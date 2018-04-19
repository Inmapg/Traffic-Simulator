// Not finished. Not working
package pr5.model;

import pr5.ini.IniSection;
import pr5.model.Junction.IncomingRoad;

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
    /**Chosen road*/
    private TimeSliceIncomingRoad currentRoad;
    /**Last chosen road*/
    private TimeSliceIncomingRoad lastGreenLightRoad;
    
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
        currentRoad = null;
    }
    
    @Override
    protected IncomingRoad createIncomingRoadQueue(Road r) {
        TimeSliceIncomingRoad newIncomingRoad = new TimeSliceIncomingRoad(r, maxTimeSlice);

        return newIncomingRoad;
    }
    
    @Override
    public void addIncomingRoad(Road newRoad) {
        incomingRoadMap.put(newRoad, createIncomingRoadQueue(newRoad));
    }

    @Override
    protected void switchLights() { 
       if(currentRoad == null){
            nextRoad = incomingRoadMap.keySet().iterator();
            currentRoad = (TimeSliceIncomingRoad) incomingRoadMap.get(nextRoad.next());
            currentRoad.onGreenLight();
        }
       else if(currentRoad.timeIsOver()){ // if the interval time is used up
                currentRoad.offGreenLight(); // set the lights off
                 if(!currentRoad.used()){ // there was no reduction in time
                    currentRoad.setIntervalTime(Math.max(currentRoad.getIntervalTime()-1, minTimeSlice));
                }
                else if(currentRoad.completelyUsed()){ // time completely used
                    currentRoad.setIntervalTime(Math.min(currentRoad.getIntervalTime()+1, maxTimeSlice));
                }
               
                currentRoad.reset(); // set spent-time to zero
                
                if (nextRoad == null || !nextRoad.hasNext()) {
                    nextRoad = incomingRoadMap.keySet().iterator();
                }
                currentRoad = (TimeSliceIncomingRoad) incomingRoadMap.get(nextRoad.next());
                currentRoad.onGreenLight();
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
