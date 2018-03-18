package pr4.model;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import pr4.ini.IniSection;


public class Junction extends SimulatedObject {

    /**<i>Internal class that has the queue of incoming roads to the junction</i>*/
    private static class IncomingRoad {
        /**Green light*/
        private boolean greenLight;
        /**Queue of vehicles*/
        private ArrayDeque<Vehicle> waiting = new ArrayDeque<>();
        // ??
        private Road road;
        /**Constructor
         * 
         * @param r Road
         */
        public IncomingRoad(Road r){ road = r;}
        
        /**Method that advances the first vehicle of the queue*/
        protected void advanceFirstVehicle(){
           Vehicle movingVehicle = waiting.pollFirst(); // returns null when empty
           if(movingVehicle == null){
               // empty queue
           }
           else{
               movingVehicle.moveToNextRoad();
           }
        }
        
        /**Method that prints the current state of the queue*/
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
        
        
    }
    /**Roads iterator*/
    private Iterator<Road> nextRoad;
    /** Current road */
    private Road currentRoad;
    /**Map of incoming roads*/
    private Map<Road, IncomingRoad> incomingRoads = new LinkedHashMap<>(); // LinkedHashMap para preservar orden de inserción
    /**Map of outgoing roads*/
    private Map<Junction, Road> mapOutGoingRoad = new HashMap<>();
    /**Section tag*/
    private static final String SECTION_TAG_NAME = "junction_report";
    /**Current incoming road*/
    private IncomingRoad currentIncomingRoad = null;

    /**Constructor
     * 
     * @param id Identification value
     */
    public Junction(String id) {
        super(id);
    }

    /**Method that returns the road which is joined to a destination junction
     * 
     * @param destinationJunction Destination junction
     * @return road
     */
    public Road roadTo(Junction destinationJunction) {
        Road road = mapOutGoingRoad.get(destinationJunction); // O(1)
        // it returns null if there is no value with the key 
        return road;
    }


    /**Method that adds a new road to the incoming road queue
     * 
     * @param newRoad New road
     */
    void addIncomingRoad(Road newRoad) {
        // Deberíamos de capturar un posible ArrayList que devolviera put
        // indicándonos que la carretera ya había sido añadida
        incomingRoads.put(newRoad, createIncomingRoadQueue(newRoad));
    }

    /**Method that adds a new road to the outgoing road queue
     * 
     * @param newRoad New road
     * @param newJunction New junction
     */
    void addOutGoingRoad(Road newRoad, Junction newJunction) {
        // Deberíamos de capturar un posible ArrayList que devolviera put
        // indicándonos que la carretera ya había sido añadida
        mapOutGoingRoad.put(newJunction, newRoad);
    }

    /**Method that sets a vehicle in the incoming road queue
     * 
     * @param newVehicle New vehicle
     */
    void enter(Vehicle newVehicle) {
        // va a lanzar nullpointerexception cuando get devuelva null
            incomingRoads.get(newVehicle.getRoad()).waiting.offer(newVehicle);
    }

    @Override
    /**Method that executes the Simulated object: Junction*/
    void advance() {
        if(!incomingRoads.isEmpty()){
            if (nextRoad == null || !nextRoad.hasNext()) {
                nextRoad = incomingRoads.keySet().iterator();
            }
            currentRoad = nextRoad.next();
            switchLights();  
        }
        
    }

    /**Method that decides which is the following green light*/
    protected void switchLights() {
        if(currentRoad != null){
            IncomingRoad newIncoming =  incomingRoads.get(currentRoad);
            if(currentIncomingRoad != null)
                currentIncomingRoad.greenLight = false;
            newIncoming.greenLight = true;
            newIncoming.advanceFirstVehicle();
            currentIncomingRoad = newIncoming;
        }
     
       
    }

    /**Method that creates an incoming road queue
     * 
     * @param r Road
     * @return incoming road
     */
    protected IncomingRoad createIncomingRoadQueue(Road r) {
        return new IncomingRoad(r);
    }

    @Override
    /**Getter for section tag
     * 
     * @return section tag
     */
    public String getReportSectionTag() {
        return SECTION_TAG_NAME;
    }

    @Override
    /**Method that fills in the report
     * @param ini Section
     */
    protected void fillReportDetails(IniSection ini) {
        StringBuilder sb = new StringBuilder();
        if(!incomingRoads.isEmpty()){
             incomingRoads.values().forEach((ir) -> {
            sb.append('(').append(ir.road.getId()).append(',').append(ir.greenLight ? "green" : "red").append(',').append(ir.printQueue()).append("),");
            });
             ini.setValue("queues", sb.substring(0, sb.length() - 1));
        }
        else{
            ini.setValue("queues", "");
        }
       
         
    }

}
