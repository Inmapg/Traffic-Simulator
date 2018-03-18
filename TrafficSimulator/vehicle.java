package pr4.model;
// Revisar el final void
import java.util.ArrayList;
import java.util.List;
import pr4.ini.IniSection;

/**<i>Class that implements the simulated object: <b>Vehicle</b></i>
 * @author Inmapg
 * @author Arturacu
 */
public class Vehicle extends SimulatedObject {
    /**Section tag*/
    private static final String SECTION_TAG_NAME = "vehicle_report";
    /**Maximum speed*/
    private final int maxSpeed;
    /**Current speed*/
    private int currentSpeed;
    /**Current road*/
    private Road currentRoad;
    /**Current position*/
    private int location;
    /**Current junction*/
    private int pathIndex;
    /**Itinerary*/
    private final ArrayList<Junction> path;
    /**Faulty*/
    private int faulty;
    /**Checker of arrival to destination*/
    private boolean hasArrived;
    /**Kilometrage*/
    private int kilometrage;

    /**Constructor
     * 
     * @param id Identification value
     * @param maxSpeed Maximum speed
     * @param path Itinerary
     */
    public Vehicle(String id, int maxSpeed, List<Junction> path){
        super(id);
        this.path = new ArrayList(path);
        pathIndex = 0;
        this.maxSpeed = maxSpeed;
        kilometrage = 0;
        moveToNextRoad();
    }
    
    /**Getter of the current road where the vehicle is
     * 
     * @return current road
     */
    public Road getRoad(){
       return currentRoad;
        // return path.get(pathIndex).getEntryRoad(); 
    }
    
    /**Getter of the maximum speed
     * 
     * @return maximum speed
     */
    public int getMaxSpeed(){
        return maxSpeed;
    }
    
    /**Getter of the speed
     * 
     * @return speed
     */
    public int getSpeed(){
        return currentSpeed;
    }
    
    /**Getter of location
     * 
     * @return location
     */
    public int getLocation(){
        return location;
    }
    
    /**Getter for kilometrage
     * 
     * @return kilometrage
     */
    public int getKilometrage(){
        return kilometrage;
    }

    /**Getter of breakdown counter
     * 
     * @return faulty time
     */
    public int getFaultyTime(){
        return faulty;
    }
    
    /**Method that check if the vehicle has arrived to its destination
     * 
     * @return arrived
     */
    public boolean atDestionation(){
        return hasArrived;
    }
    
    /**Getter of itinerary
     * 
     * @return itinerary
     */
    public List<Junction> getItinerary(){
        return (List) path;
    }
    
    /**Method that increases the breakdown counter of a vehicle
     * 
     * @param counter Counter
     */
    public void makeFaulty(int counter){
        faulty += counter;
    }
    
    /**Setter of speed
     * 
     * @param newSpeed New speed of the vehicle
     */
    void setSpeed(int newSpeed){
        currentSpeed = (getFaultyTime() == 0) ? Math.min(maxSpeed, newSpeed) : 0;
    }
    
    @Override
    /**Method that executes a type of simulated object*/
    void advance(){
        if(getFaultyTime() > 0){
            makeFaulty(-1);
            
        }
        else{
            kilometrage -= location;
            location = Math.min(currentRoad.getLength(), location + currentSpeed);
            kilometrage += location;
        }
    }
    
    /**Method that moves the vehicle to the next road of its itinerary if possible*/
    final void moveToNextRoad(){
        setSpeed(0);
        if(currentRoad != null){
           currentRoad.exit(this);
        }
        if(pathIndex <  path.size()-1){
            currentRoad = path.get(pathIndex).roadTo(path.get(++pathIndex));
            currentRoad.enter(this);
        }
        else{
            hasArrived = true;
            currentRoad = null;
        }
       
    }
    
    public void setLocation(int newLocation){
        location = newLocation;
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
    protected void fillReportDetails(IniSection ini){
        ini.setValue("speed", ""+currentSpeed);
        ini.setValue("kilometrage", ""+kilometrage);
        ini.setValue("faulty", ""+faulty);
        if(!hasArrived){
            StringBuilder sb = new StringBuilder();
            sb.append('(').append(currentRoad.getId()).append(',').append(location).append(')');
            ini.setValue("location", sb.toString());
        }else{
            ini.setValue("location", "arrived");
        }
        

    }


}
