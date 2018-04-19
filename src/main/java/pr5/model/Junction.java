package pr5.model;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import pr5.ini.IniSection;

/**Defines one of the main types of Simulated Object.
 * 
 * @author Inmapg
 * @author Arturacu
 * @version 3.0
 * @see SimulatedObject
 */
public class Junction extends SimulatedObject {
    /**Tag name for report*/
    private static final String SECTION_TAG_NAME = "junction_report";
    
    /**Contains information about an incoming road to the junction*/
    protected class IncomingRoad {
        /**Green light on/off*/
        private boolean greenLight;
        /**Queue of vehicles
         * @see Vehicle
         */
        private ArrayDeque<Vehicle> waiting = new ArrayDeque<>();
        /**Incoming road*/
        protected final Road road;
        
        /**Class consctructor specifying road.
         * greenLight is false-initialized.
         * @param road Incoming road
         */ 
        public IncomingRoad(Road road){ 
            this.road = road; 
            greenLight = false; 
        }
        
        /**Returns the state of the trafficlight.
         * 
         * @return green/red
         */
        protected String lightToString(){
            return (greenLight) ? "green" : "red";
        }
        
        /**Moves first vehile from queue to next road*/
        protected void advanceFirstVehicle(){
            Vehicle movingVehicle = waiting.pollFirst(); // returns null when empty
            if(movingVehicle == null){
               // empty queue
            }
            else{
                movingVehicle.moveToNextRoad();
            }
        }
        
        /**Returns the size of the queue
         * 
         * @return Size of queue
         */
        protected int sizeOfQueue(){
            return waiting.size();
        }
        
        /**Turns green the trafficlight*/
        protected void onGreenLight(){
            greenLight = true;
        }
        
        /**Turns red the trafficlight*/
        protected void offGreenLight(){
            greenLight = false; 
        }
        
        /**Returns if trafficlight is green/red
         * 
         * @return red - false / green - true
         */
        protected boolean isGreenLight(){
            return greenLight;
        }
        
        /**Prints the current state of the queue
         * @return State of the queue
         */
        protected String printQueue(){
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            if(!waiting.isEmpty()){
                waiting.forEach((v) -> {
                    sb.append(v.getId()).append(',');
                });
            }
            if(sb.length() > 1){
                return sb.substring(0, sb.length()-1) + ']';
            }
            else{
                return sb.append(']').toString();
            }
            
        }
    } // End of the internal class IncomingRoad
    
    /**Next road to turn it trafficlight green
     * @see Road
     */
    protected Iterator<Road> nextRoad;
    
    /**Current road
     * @see Road
     */
    protected IncomingRoad currentRoad;
    
    /**Road with green light
     * @see Road
     */
    protected IncomingRoad lastGreenLightRoad;
    
    /**Associates roads with their respective incoming roads
     * @see Road
     * @see IncomingRoad
     */
    protected Map<Road, IncomingRoad> incomingRoadMap = new LinkedHashMap<>(); // LinkedHashMap para preservar orden de inserción
    
    /**Associates junctions with their respective outgoing roads
     * @see Road
     */
    private Map<Junction, Road> outgoingRoadMap = new HashMap<>();
    
    /**Class constructor specifying id.
     * The rest of attributes are null-initialized.
     * 
     * @param id Identification
     */
    public Junction(String id) {
        super(id);
        lastGreenLightRoad = null;
        currentRoad = null;
        nextRoad = null;
        
    }
    
    @Override
    protected String getReportSectionTag() {
        return SECTION_TAG_NAME;
    }
    
    /**Puts a vehicle in the junction.
     * 
     * @param newVehicle Entering vehicle
     */
    public void enter(Vehicle newVehicle){
        incomingRoadMap.get(newVehicle.getRoad()).waiting.offer(newVehicle);
    } 
    
    /**Adds an incoming road to the junction.
     * 
     * @param newRoad Incoming road 
     */
    public void addIncomingRoad(Road newRoad) {
        incomingRoadMap.put(newRoad, createIncomingRoadQueue(newRoad));
    }
    
    /**Adds an outgoing road from the junction.
     * 
     * @param newRoad Outgoing road
     * @param newJunction Junction
     */
    public void addOutGoingRoad(Road newRoad, Junction newJunction) {
        outgoingRoadMap.put(newJunction, newRoad);
    }
    
    /**Creates an incoming road queue.
     * 
     * @param r Road
     * @return Incoming road queue
     */
    protected IncomingRoad createIncomingRoadQueue(Road r) {
        return new IncomingRoad(r);
    }
    
    /**Returns the road that goes to the destination junction.
     * 
     * @param destinationJunction Destination junction
     * @return Road if found, null if not
     */
    public Road roadTo(Junction destinationJunction) {
        return outgoingRoadMap.get(destinationJunction); // returns null if there is no value with the key 
    }
    
    @Override
    public void advance() {
        if(!incomingRoadMap.isEmpty()){
            if(currentRoad != null){
                currentRoad.advanceFirstVehicle();
            }
            switchLights();
        }
        
    }
    
    /**
     * Returns the next road on the incoming road map.
     * @return next road
     */
    protected IncomingRoad getNextRoad(){
        if (nextRoad == null || !nextRoad.hasNext()) {
            nextRoad = incomingRoadMap.keySet().iterator();
        }
        return incomingRoadMap.get(nextRoad.next());
    }
    
    /**
     * Changes the trafficlights of the roads.
     */
    protected void switchLights() {
        currentRoad = getNextRoad();
        if(lastGreenLightRoad != null){
            lastGreenLightRoad.offGreenLight();
        }
        currentRoad.onGreenLight();
        lastGreenLightRoad = currentRoad; 
    }
    
    @Override
    protected void fillReportDetails(IniSection sec) {
        StringBuilder sb = new StringBuilder();
        if(!incomingRoadMap.isEmpty()){
             incomingRoadMap.values().forEach((ir) -> {
            sb.append('(').append(ir.road.getId()).append(',').append(ir.lightToString()).append(',').append(ir.printQueue()).append("),");
            });
            sec.setValue("queues", sb.substring(0, sb.length() - 1));
        }
        else{
            sec.setValue("queues", "");
        }
    }
    
}
