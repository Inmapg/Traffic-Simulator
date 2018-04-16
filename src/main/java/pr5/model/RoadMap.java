package pr5.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import pr5.exception.SimulatorError;

/**Contains every simulated object of the program.
 * 
 * @author Inmapg
 * @author Arturacu
 * @version 2.0
 */
public class RoadMap {
    /**List of simulated vehicles
     * @see Vehicle
     */
    private Map<String, Vehicle> simulatedVehicles; // id - vehicle
    /**List of simulated roads
     * @see Road
     */
    private Map<String, Road> simulatedRoads; // id- road
    /**List of simulated junctions
     * @see Junction
     */
    private Map<String, Junction> simulatedJunctions; // id - junction
    
    /**Class constructor*/
    public RoadMap(){
        simulatedVehicles = new LinkedHashMap<>();
        simulatedRoads = new LinkedHashMap<>();
        simulatedJunctions = new LinkedHashMap<>();
    }
    
    /**Checks if a simulated object exists.
     * 
     * @param obj Simulated object
     */
    public void checkIfIdExists(SimulatedObject obj){
        if ( simulatedVehicles.get(obj.getId()) != null
             || simulatedRoads.get(obj.getId()) != null
             || simulatedJunctions.get(obj.getId()) != null){
            throw new IllegalArgumentException("The id " + obj.getId() + " has already been used to name other object,"
                    + " you cannot create the object " + obj.getClass() + " with this id");
        }
    }
    
    /**Returns a vehicle from list.
     * 
     * @param vehicleId Identification
     * @return Vehicle associated to vehicleId
     * @see Vehicle
     */
    public Vehicle getVehicle(String vehicleId){
        Vehicle rslt = null;
        try{
            rslt = simulatedVehicles.get(vehicleId);
            if(rslt == null) throw new NullPointerException("Vehicle with " + vehicleId + " id not found in roadmap");
        }
        catch(NullPointerException e){
            System.err.println(e.getMessage());
        }
        return rslt; // null when not found
    }
    
    /**Returns a road from list.
     * 
     * @param roadId Identification
     * @return Road associated to roadId
     * @see Road
     */
    public Road getRoad(String roadId){
        Road rslt = null;
        try{
            rslt = simulatedRoads.get(roadId);
            if(rslt == null) throw new NullPointerException("Road with " + roadId + " id not found in roadmap");
        }
        catch(NullPointerException e){
            System.err.println(e.getMessage());
        }
        return rslt; // null when not found
    }
    
    /**Returns a junction from list.
     * 
     * @param junctionId Identification
     * @return Junction associated to junctionId
     * @see Junction
     */
    public Junction getJunction(String junctionId){
        Junction rslt = null;
        try{
            rslt = simulatedJunctions.get(junctionId);
            if(rslt == null){
                throw new NullPointerException("Junction with " + junctionId + " id not found in roadmap");
            }
        }
        catch(NullPointerException e){
            System.err.println(e.getMessage());
            throw (e);
        }
        return rslt; // null when not found    
    }
    
    /**Adds a new junction to list.
     * 
     * @param newJunction New junction
     */
    public void addJunction(Junction newJunction){
        checkIfIdExists(newJunction);
        simulatedJunctions.put(newJunction.getId(), newJunction);
    }
    
    /**Adds a new road to list.
     * 
     * @param newRoad New road
     */
    public void addRoad(Road newRoad){
        checkIfIdExists(newRoad);
        simulatedRoads.put(newRoad.getId(), newRoad);
        newRoad.getDestination().addIncomingRoad(newRoad);
        newRoad.getSource().addOutGoingRoad(newRoad, newRoad.getDestination());
    }
    
    /**Adds a new vehicle to list.
     * 
     * @param newVehicle New vehicle
     */
    public void addVehicle(Vehicle newVehicle){
        checkIfIdExists(newVehicle);
        simulatedVehicles.put(newVehicle.getId(), newVehicle);        
    }
    
    /**Returns the list of roads.
     * 
     * @return List of roads
     * @see Road
     */
    public List<Road> getRoads(){
        return (new ArrayList<>(simulatedRoads.values()));
    }
    
    /**Returns the list of vehicles.
     * 
     * @return List of vehicles
     * @see Vehicle
     */
    public List<Vehicle> getVehicles(){
        return (new ArrayList<>(simulatedVehicles.values()));
    }
    
    /**Returns the list of junctions.
     * 
     * @return List of junctions
     * @see Junction
     */
    public List<Junction> getJunctions(){
        return (new ArrayList<>(simulatedJunctions.values()));
    }
    
    /**Reset the lists.
     * 
     */
    public void clear(){
        simulatedVehicles = new LinkedHashMap<>();
        simulatedRoads = new LinkedHashMap<>();
        simulatedJunctions = new LinkedHashMap<>();   
    }
    
    /**Returns a list of junctios from a itinerary.
     * 
     * @param itinerary Itinerary
     * @return List of junctions
     * @see Junction
     */
    public List<Junction> getItinerary(String [] itinerary){
        List<Junction> path = new ArrayList<>();
        Junction previousJ = null;
        for(String junctionId : itinerary){
            try{
                Junction j = getJunction(junctionId);                    
                if(previousJ != null && previousJ.roadTo(j) == null){
                    throw new SimulatorError("No road connects " + previousJ.getId() + " junction with " + j.getId());
                }
                previousJ = j;
                path.add(j);
            } 
            catch (NullPointerException e){
                System.err.println("Itinerary cannot be created, junction id " + junctionId + " does not exist");
            }
            catch(SimulatorError e){
                System.err.println(e.getMessage());
            }
        }
        return path;
    }
    
}
