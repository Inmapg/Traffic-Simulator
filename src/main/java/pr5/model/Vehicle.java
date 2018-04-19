package pr5.model;
// Revisar el final void
import java.util.ArrayList;
import java.util.List;
import pr5.ini.IniSection;

/**Defines one of the main types of Simulated Object.
 * 
 * @author Inmapg
 * @author Arturacu
 * @version 2.0
 * @see SimulatedObject
 */
public class Vehicle extends SimulatedObject {
    /**Tag name for report*/
    private static final String SECTION_TAG_NAME = "vehicle_report";
    /**Maximum speed*/
    protected final int maxSpeed;
    /**Itinerary. Set of junctions*/
    protected final ArrayList<Junction> itinerary;
    /**Current junction from itinerary*/
    protected int currentJunction;
    /**Current speed*/
    protected int currentSpeed;
    /**Kilometrage*/
    protected int kilometrage;
    /**Location*/
    protected int location;
    /**Faulty time*/
    protected int faulty;
    /**Shows if the vehicle has arrived to its destination*/
    protected boolean hasArrived;
    /**Current road*/
    protected Road currentRoad;

    /**Class constructor.
     * The rest of attributes are zero-initialized.
     * 
     * @param id Identification
     * @param maxSpeed Maximum speed
     * @param itinerary Itinerary
     */
    public Vehicle(String id, int maxSpeed, List<Junction> itinerary){
        super(id);
        this.itinerary = new ArrayList(itinerary);
        this.maxSpeed = maxSpeed;
        currentJunction = 0;
        currentSpeed = 0;
        kilometrage = 0;
        moveToNextRoad();
    }
    
    /**Moves itself to next road.
     * 
     */
    public final void moveToNextRoad(){
        if(currentJunction > 0){
            currentRoad.exit(this);
        }
        location = 0;
        if(currentJunction < itinerary.size()-1){
            currentRoad = itinerary.get(currentJunction).roadTo(itinerary.get(++currentJunction));
            currentRoad.enter(this);
        }
        else{
            hasArrived = true;
        }
        
    }
    
    /**Breaks down the vehicle
     * 
     * @param counter Duration
     */
    public void makeFaulty(int counter){
        faulty += counter;
        setSpeed(0);
    }    
    
    /**Sets the speed of the vehicle.
     * 
     * @param newSpeed New speed
     */
    public void setSpeed(int newSpeed){
        currentSpeed = (getFaultyTime() == 0 && currentRoad.getLength() != location) ? Math.min(maxSpeed, newSpeed) : 0;
    }
    
    /**Returns its breakdown duration.
     * 
     * @return Faulty time
     */
    public int getFaultyTime(){
        return faulty;
    }
    
    /**Returns its location.
     * 
     * @return Location
     */
    public int getLocation(){
        return location;
    }
    
    /**Returns the road where it is located.
     * 
     * @return Road where the vehicle is
     * @see Road
     */
    public Road getRoad(){
        return currentRoad;
    }
    
    @Override
    protected void advance(){
        if(faulty > 0){
            makeFaulty(-1);
        }
        else if(location != currentRoad.getLength()){
            kilometrage -= location;
            location = Math.min(currentRoad.getLength(), location + currentSpeed);
            kilometrage += location;
            if(location == currentRoad.getLength()){
                currentSpeed = 0;
                itinerary.get(currentJunction).enter(this);
            }
        }
    }
    
    @Override
    protected String getReportSectionTag() {
         return SECTION_TAG_NAME; 
    }

    @Override
    protected void fillReportDetails(IniSection sec) {
        sec.setValue("speed", ""+currentSpeed);
        sec.setValue("kilometrage", ""+kilometrage);
        sec.setValue("faulty", ""+faulty);
        if(!hasArrived){
            StringBuilder sb = new StringBuilder();
            sb.append('(').append(currentRoad.getId()).append(',').append(location).append(')');
            sec.setValue("location", sb.toString());
        }
        else{
            sec.setValue("location", "arrived");
        }
    }
    
}