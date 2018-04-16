package pr5.model;

import pr5.ini.IniSection;
import pr5.util.MultiTreeMap;

/**Defines one of the main types of Simulated Object.
 * 
 * @author Inmapg
 * @author Arturacu
 * @version 2.0
 * @see SimulatedObject
 */
public class Road extends SimulatedObject{
    /**Tag name for report*/
    private static final String SECTION_TAG_NAME = "road_report";
    /**Source junction
     * @see Junction
     */
    private Junction sourceJunction;
    /**Destination junction
     * @see Junction
     */
    private Junction destinationJunction;
    /**Initial position*/
    private static final int INITIAL_POS = 0;
    /**List of vehicles ordered by insertion
     * @see Vehicle
     */
    protected MultiTreeMap<Integer, Vehicle> vehiclesList = new MultiTreeMap<> ( (a, b) -> b-a );
    /**Length*/
    protected final int length;
    /**Maximum speed*/
    protected final int maxSpeed;
    
    /**Class constructor specifying id, length, maximum speed, source junction and
     * destination junction.
     * 
     * @param id Identification
     * @param length Length
     * @param maxSpeed Maximum speed
     * @param sourceJunction Source junction
     * @param destinationJunction Destination junction
     */
    public Road(String id, int length, int maxSpeed, Junction sourceJunction, Junction destinationJunction) {
       super(id);
       this.length = length;
       this.maxSpeed = maxSpeed;
       this.sourceJunction = sourceJunction;
       this.destinationJunction = destinationJunction;
    }
    
    /**Returns the source junction.
     * 
     * @return Source junction
     */
    public Junction getSource(){
        return sourceJunction;
    }
    
    /**Returns the destination junction.
     * 
     * @return Destionation junction
     */
    public Junction getDestination(){
        return destinationJunction;
    }
    
    /**Puts a new vehicle on road.
     * 
     * @param newVehicle Incoming vehicle
     */
    public void enter(Vehicle newVehicle){
        vehiclesList.putValue(INITIAL_POS, newVehicle);
    }
    
    /**Takes a vehicle off the road.
     * 
     * @param exitVehicle Outgoing vehicle 
     */
    public void exit(Vehicle exitVehicle){
        vehiclesList.removeValue(exitVehicle.getLocation(), exitVehicle);
    }
    
    /**Returns the base speed of the road.
     * 
     * @return Base speed
     */
    protected int calculateBaseSpeed(){
        return (int) Math.min(maxSpeed, maxSpeed/(Math.max(1, vehiclesList.sizeOfValues())) + 1);
    }
    
    /**Returns the speed factor of the road.
     * 
     * @param brokenVehicles Number of faulty vehicles
     * @return 2 if there are faulty vehicles, 1 if not
     */
    protected int reduceSpeedFactor(int brokenVehicles){
        return (brokenVehicles > 0) ? 2 : 1;
    }
    
    @Override
    public void advance() {
        MultiTreeMap<Integer, Vehicle> updated = new MultiTreeMap<> ( (a, b) -> b-a );
        int brokenVehicles = 0;
        for(Vehicle v : vehiclesList.innerValues()){
            if(v.getFaultyTime() > 0){
                brokenVehicles++;
            }
            v.setSpeed(calculateBaseSpeed()/reduceSpeedFactor(brokenVehicles));
            v.advance();
            updated.putValue(v.getLocation(), v);
        }
        vehiclesList.clear();
        vehiclesList = updated;
    }
    
    /**Returns the length of the road
     * 
     * @return Length
     */
    public int getLength(){
        return length;
    }
    
    @Override
    protected String getReportSectionTag() {
        return SECTION_TAG_NAME;
    }
    
    @Override
    protected void fillReportDetails(IniSection sec) {
        StringBuilder sb = new StringBuilder();
        if(!vehiclesList.isEmpty()){
            vehiclesList.innerValues().forEach( (Vehicle v) -> sb.append("(").append(v.getId()).append(",").append(v.getLocation()).append("),"));
            sec.setValue("state", sb.substring(0, sb.length() - 1)); 
        }
        else{
            sec.setValue("state", "");
        }
    }
    
    
    
    
}