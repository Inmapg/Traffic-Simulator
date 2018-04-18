// Not finished. Not working
package pr5.model;

import pr5.ini.IniSection;

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
    
    protected TimeSliceIncomingRoad currentRoad;
    
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
        TimeSliceIncomingRoad newIncomingRoad = new TimeSliceIncomingRoad(r);
        newIncomingRoad.setIntervalTime(maxTimeSlice);
        return newIncomingRoad;
    }
    
    protected IncomingRoad getNextRoad(){
        if (nextRoad == null || !nextRoad.hasNext()) {
            nextRoad = incomingRoadMap.keySet().iterator();
        }
        return incomingRoadMap.get(nextRoad.next());
    }
    @Override
    protected void switchLights() {
        if(currentRoad == null){
            super.switchLights(); // the first road will be set on
        }
        else if(currentRoad.timeIsOver()){ // if the interval time is used up
                currentRoad.offGreenLight(); // set the lights off
                if(currentRoad.completelyUsed()){ // time completely used
                    currentRoad.setIntervalTime(Math.min(currentRoad.getIntervalTime()+1, maxTimeSlice));
                }
                else if(!currentRoad.used()){ // there was no reduction in time
                    currentRoad.setIntervalTime(Math.max(currentRoad.getIntervalTime()-1, minTimeSlice));
                }
                currentRoad.resetSpentTime(); // set spent-time to zero
                super.switchLights();
            }
         else{
            currentRoad.advanceFirstVehicle();
         }
           
     }

    
    protected void fillReportDetails(IniSection sec) {
        sec.setValue("type", TYPE);
        sec.setValue("max_time_slice", maxTimeSlice);
        sec.setValue("min_time_slice", minTimeSlice);
        super.fillReportDetails(sec);
    }
}
