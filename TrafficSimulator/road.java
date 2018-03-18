package pr4.model;

import pr4.ini.IniSection;
import pr4.util.MultiTreeMap;

/**<i>Class that implements the simulated object: <b>Road</b></i>
 * @author Inmapg
 * @author Arturacu
 */
public class Road extends SimulatedObject{
    /**Section tag*/
    private static final String SECTION_TAG_NAME = "road_report";
    /**Length*/
    private final int length;
    /**Maximum speed allowed*/
    private final int maxSpeed;
    /**Source junction*/
    Junction sourceJunction;
    /**Destination junction*/
    Junction destinationJunction;
    /**Initial entry position*/
    private static final int INITIAL_POS = 0;
    /**Map of vehicle lists*/
    private MultiTreeMap<Integer, Vehicle> vehiclesList;
    
    /**Constructor
     * 
     * @param id Identification value
     * @param length Length
     * @param maxSpeed Maximum speed allowed
     * @param sourceJunction Source junction
     * @param destinationJunction End junction
     */
    public Road(String id, int length, int maxSpeed, Junction sourceJunction, Junction destinationJunction) {
       super(id);
       this.length = length;
       this.maxSpeed = maxSpeed;
       this.sourceJunction = sourceJunction;
       this.destinationJunction = destinationJunction;
       vehiclesList = new MultiTreeMap<> ( (a, b) -> b-a );
    }
    
    /**Getter for source junction
     * 
     * @return source junction
     */
    public Junction getSource(){
        return sourceJunction;
    }
    
    /**Getter for destination junction
     * 
     * @return destination junction
     */
    public Junction getDestination(){
        return destinationJunction;
    }
    
    /**Getter for length
     * 
     * @return length
     */
    public int getLength(){
        return length;
    }
    
    /**Getter for maximum speed allowed
     * 
     * @return maximum speed allowed
     */
    public int getMaxSpeed(){
        return maxSpeed;
    }
    
    
    /**Method that executes the Simulated object: Road*/
    void advance(){ 
        MultiTreeMap<Integer, Vehicle> updated = new MultiTreeMap<> ( (a, b) -> b-a );
        int brokenVehicles = 0;
        for(Vehicle v : vehiclesList.innerValues()){
         //   vehiclesList.removeValue(v.getLocation(), v); no es necesario
            if(v.getFaultyTime() > 0){
                brokenVehicles++;
            }
            v.setSpeed(calculateBaseSpeed()/reduceSpeedFactor(brokenVehicles));
            v.advance();
            if(length == v.getLocation()){
                v.setSpeed(0);
                destinationJunction.enter(v);
            }
            updated.putValue(v.getLocation(), v);
        }
        vehiclesList.clear();
        vehiclesList = updated;
    }
    
    /**Method that calculates the base speed of the road
     * 
     * @return base speed
     */
    protected int calculateBaseSpeed(){
        return (int) Math.min(maxSpeed, maxSpeed/(Math.max(1, vehiclesList.sizeOfValues())) + 1);
    }
    
    /**Method that sets a new vehicle in the list
     * 
     * @param newVehicle Vehicle
     */
    void enter(Vehicle newVehicle){
        vehiclesList.putValue(INITIAL_POS, newVehicle);
        newVehicle.setLocation(0);
    }
    
    /**Method that removes a vehicle from the list
     * 
     * @param exitVehicle Vehicle
     */
    public void exit(Vehicle exitVehicle){
        vehiclesList.removeValue(exitVehicle.getLocation(), exitVehicle);
        
    }

    @Override
    /**Getter for section tag
     * 
     * @return section tag
     */
    protected String getReportSectionTag(){
         return SECTION_TAG_NAME;
    }

    @Override
    /**Method that fills in the report
     * @param ini Section
     */
    protected void fillReportDetails(IniSection ini) {
        StringBuilder sb = new StringBuilder();
        if(!vehiclesList.isEmpty()){
            vehiclesList.innerValues().forEach( (Vehicle v) -> sb.append("(").append(v.getId()).append(",").append(v.getLocation()).append("),"));
            ini.setValue("state", sb.substring(0, sb.length() - 1)); 
        }
        else{
            ini.setValue("state", "");
        }
    }    
    
    /**Method that reduces the speed factor depending on the number of broken vehicles
     * 
     * @param brokenVehicles Number of broken vehicles
     * @return speed factor
     */
    protected int reduceSpeedFactor(int brokenVehicles){
        return (brokenVehicles > 0) ? 2 : 1;
    }
    
}
