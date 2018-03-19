// Not finished. Not working
package pr4.model;

/**Defines a congested junction.
 * 
 * @author Inmapg
 * @author Arturacu
 * @version 2.0
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
        private int timeSpent;       
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
        
        @Override
        protected void onGreenLight(){
           super.onGreenLight();
           completelyUsed = true;
           timeSpent = 0;
        }
        
        /**Returns if time is greater than the interval of time.
         * 
         * @return If time is spent
         */
        public final boolean timeIsOver(){
            return timeSpent >= intervalTime;
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
         * @param intervalTime Interval time 
         */
        public void setIntervalTime(int intervalTime){
            this.intervalTime = intervalTime;
        }
        
    } // End of the internal class TimeSliceIncomingRoad
    
    /**Class constructor specifying the id. 
     * 
     * @param id Identification
     */
    public TimeSliceJunction(String id) {
        super(id);
    }
    
    @Override
    protected IncomingRoad createIncomingRoadQueue(Road r) {
        return new TimeSliceIncomingRoad(r);
    }
}
