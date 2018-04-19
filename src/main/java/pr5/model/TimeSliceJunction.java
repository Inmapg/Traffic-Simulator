package pr5.model;

import pr5.ini.IniSection;

/**Defines a time slice junction.
 * 
 * @author Inmapg
 * @author Arturacu
 * @version 3.0
 * @see Junction
 */
public class TimeSliceJunction extends Junction {
    /**Contains information about an incoming time slice road to the junction.
     * @see IncomingRoad
     */
    protected class TimeSliceIncomingRoad extends IncomingRoad{
        /**Interval of time*/
        private int intervalTime;
        /**Time spent*/
        protected int timeSpent;       
        /**Time completely used*/
        private boolean completelyUsed;
        /**Time used*/
        private boolean used;
        
        /**Class constructor specifying the road.
         * The rest of attributes are zero-initialized.
         * @param r Road
         */
        public TimeSliceIncomingRoad(Road r) {
            super(r);
            timeSpent = 0;
            intervalTime = 0;
            used = false;
            completelyUsed = false;
        }
        
        /**
         * Class constructor specifying road and interval time.
         * @param road Road
         * @param intervalTime Interval time
         */
        public TimeSliceIncomingRoad(Road road, int intervalTime) {
            super(road);
            timeSpent = 0;
            this.intervalTime = intervalTime;
            used = false;
            completelyUsed = false;
        }
        
        @Override
        protected void onGreenLight(){
           super.onGreenLight();
           completelyUsed = true;
           used = false;
           timeSpent = 0;
        }
        
        /**Returns if time is greater than the interval of time.
         * 
         * @return If time is spent
         */
        public final boolean timeIsOver(){
            return timeSpent >= intervalTime-1;
        }
        
        @Override
        protected void advanceFirstVehicle(){
           timeSpent++;
           int queueBefore = sizeOfQueue();
           super.advanceFirstVehicle();
           completelyUsed = queueBefore > sizeOfQueue() && completelyUsed;
           used = used || completelyUsed;
        } 
        
        /**Returns if the time is completely used.
         * 
         * @return If time completely used.
         */
        public final boolean completelyUsed(){
            return completelyUsed;
        }
        
        /**Returns if the time is used.
         * 
         * @return If time is used.
         */
        public final boolean used(){
            return used;
        }
        
        /**Sets the interval time.
         * 
         * @param intervalTime interval time 
         */
        public void setIntervalTime(int intervalTime){
            this.intervalTime = intervalTime;
        }
        
        /**
         * Returns the interval time.
         * @return interval time
         */
        public int getIntervalTime(){
            return intervalTime;
        }
        
        /**
         * Resets time spent to zero.
         */
        public void reset(){
            timeSpent = 0;
        }
    } // End of the internal class TimeSliceIncomingRoad
    
    /**Class constructor specifying the id. 
     * 
     * @param id Identifier
     */
    public TimeSliceJunction(String id) {
        super(id);
    }
    
    @Override
    protected IncomingRoad createIncomingRoadQueue(Road r) {
        return new TimeSliceIncomingRoad(r);
    }
    
    @Override
    protected void fillReportDetails(IniSection sec) {
            StringBuilder sb = new StringBuilder();
            if(!incomingRoadMap.isEmpty()){
             incomingRoadMap.values().forEach((ir) -> {
             if(ir.isGreenLight()){
             sb.append('(').append(ir.road.getId()).append(',').append(ir.lightToString()).append(":").append(
                    ((TimeSliceIncomingRoad)ir).getIntervalTime() - ((TimeSliceIncomingRoad)ir).timeSpent).append(',').append(ir.printQueue()).append("),");
             }
             else{
                sb.append('(').append(ir.road.getId()).append(',').append(ir.lightToString()).append(',').append(ir.printQueue()).append("),"); 
                }
             });
            sec.setValue("queues", sb.substring(0, sb.length() - 1));
        }
        else{
            sec.setValue("queues", "");
        }
     }

}
