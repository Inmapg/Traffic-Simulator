package pr5.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import pr5.exception.SimulatorError;

/**
 * Contains every simulated object of the program.
 */
public class RoadMap {
    /**
     * Map of simulated vehicles.
     * id - vehicle
     * @see Vehicle
     */
    private Map<String, Vehicle> simulatedVehicles; 
    /**
     * Map of simulated roads.
     * id- road
     * @see Road
     */
    private Map<String, Road> simulatedRoads; 
    /**
     * Map of simulated junctions.
     * id - junction
     * @see Junction
     */
    private Map<String, Junction> simulatedJunctions;
    
    /**
     * Class constructor.
     */
    public RoadMap(){
        simulatedVehicles = new LinkedHashMap<>();
        simulatedRoads = new LinkedHashMap<>();
        simulatedJunctions = new LinkedHashMap<>();
    }
    
    /**
     * Checks if a simulated object exists.
     * 
     * @param simObject 
     */
    public void complainIfIdExists(SimulatedObject simObject){
        if ( simulatedVehicles.get(simObject.getId()) != null
             || simulatedRoads.get(simObject.getId()) != null
             || simulatedJunctions.get(simObject.getId()) != null){
            throw new IllegalArgumentException("The id " + simObject.getId() 
                    + " has already been used to name other object,"
                    + " you cannot create the object " + simObject.getClass() 
                    + " with this id");
        }
    }
    
    /**
     * @param vehicleId 
     * @return Vehicle associated to vehicleId, null if not found
     * @see Vehicle
     */
    public Vehicle getVehicle(String vehicleId){
        Vehicle rslt = simulatedVehicles.get(vehicleId);
        if(rslt == null){
            throw new NullPointerException("Vehicle with " + vehicleId
                    + " id not found in roadmap");
        }

        return rslt; 
    }
    
    /**
     * @param roadId
     * @return Road associated to roadId, null if not found
     * @see Road
     */
    public Road getRoad(String roadId){
        Road rslt = simulatedRoads.get(roadId);
        if(rslt == null){
            throw new NullPointerException("Road with " + roadId
                + " id not found in roadmap");
        }
        
        return rslt;
    }
    
    /**
     * @param junctionId 
     * @return Junction associated to junctionId, null if not found
     * @see Junction
     */
    public Junction getJunction(String junctionId){
        Junction rslt = simulatedJunctions.get(junctionId);
        if(rslt == null){
            throw new NullPointerException("Junction with " + junctionId
                    + " id not found in roadmap");
        }

        return rslt; 
    }
    
    /**
     * Adds a new junction to list.
     * 
     * @param newJunction
     */
    public void addJunction(Junction newJunction){
        try{
            complainIfIdExists(newJunction);
        }
        catch(IllegalArgumentException e){
            throw e;
        }
        simulatedJunctions.put(newJunction.getId(), newJunction);
    }
    
    /**
     * Adds a new road to list.
     * 
     * @param newRoad
     */
    public void addRoad(Road newRoad){
        try{
            complainIfIdExists(newRoad);
        }
        catch(IllegalArgumentException e){
            throw e;
        }
        simulatedRoads.put(newRoad.getId(), newRoad);
        newRoad.getDestination().addIncomingRoad(newRoad);
        newRoad.getSource().addOutGoingRoad(newRoad, newRoad.getDestination());
    }
    
    /**
     * Adds a new vehicle to list.
     * 
     * @param newVehicle 
     */
    public void addVehicle(Vehicle newVehicle){
        try{
            complainIfIdExists(newVehicle);
        }
        catch(IllegalArgumentException e){
            throw e;
        }
        simulatedVehicles.put(newVehicle.getId(), newVehicle);        
    }
    
    /**
     * @return List of roads
     * @see Road
     */
    public List<Road> getRoads(){
        return (new ArrayList<>(simulatedRoads.values()));
    }
    
    /**
     * @return List of vehicles
     * @see Vehicle
     */
    public List<Vehicle> getVehicles(){
        return (new ArrayList<>(simulatedVehicles.values()));
    }
    
    /**
     * @return List of junctions
     * @see Junction
     */
    public List<Junction> getJunctions(){
        return (new ArrayList<>(simulatedJunctions.values()));
    }
    
    /**
     * Resets the lists.
     */
    public void clear(){
        simulatedVehicles = new LinkedHashMap<>();
        simulatedRoads = new LinkedHashMap<>();
        simulatedJunctions = new LinkedHashMap<>();   
    }
    
    /**
     * @param itinerary 
     * @return List of junctions
     * @see Junction
     */
    public List<Junction> getItinerary(String [] itinerary){
        List<Junction> path = new ArrayList<>();
        Junction previousJ = null;
        for(String junctionId : itinerary){
            Junction j = getJunction(junctionId);                    
            if(previousJ != null && previousJ.roadTo(j) == null){
                throw new SimulatorError("No road connects " 
                        + previousJ.getId() + " junction with " + j.getId()
                        + "\nItinerary cannot be created, junction id " 
                        + j.getId() + " does not exist");
            }
            previousJ = j;
            path.add(j);
        }
        return path;
    }
    
}
