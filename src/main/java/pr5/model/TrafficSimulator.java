package pr5.model;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import pr5.events.Event;
import pr5.exception.SimulatorError;
import pr5.util.MultiTreeMap;

/**Simulates a system of vehicles driving through some roads and around specified junctions.
 * @author Inmapg
 * @author Arturacu
 * @version 2.0
 */
public class TrafficSimulator {
    
    /**Output stream to process state values*/
    private OutputStream output;
    /**Map of events to be executed ordered by the time when they will be executed*/
    private MultiTreeMap<Integer, Event> mapOfEvents = new MultiTreeMap<>( (a,b) -> a-b );
    /**Internal counter*/
    private int ticks;
    /**Road map storing all the objects in the simulatation*/
    private RoadMap roadMap;
    
    /**Class Constructor specifying output stream.
     * 
     * @param output Output stream for exit values
     */
    public TrafficSimulator(OutputStream output){
        this.output = output;
        roadMap = new RoadMap();
        ticks = 0;
    }
    
    /**Given a certain SimulatedObject it generates its report.
     * 
     * @param obj Simulated object
     * @throws SimulatorError Thrown when there is a problem with the output
     */
    private void writeReport(SimulatedObject obj) throws SimulatorError{
        try{
            obj.generateReport(ticks).store(output);
            output.write('\n');
        }
        catch(IOException e){
            throw new SimulatorError("Error with " + obj.getClass() +" while storing report..." , e);
        }
    }
 
    /**Main loop of the simulator. It executes the events for the current time, 
     * invoke the method advance for roads and junctions and increases the internal counter.
     * Finally, it writes the report for all the objects in the simulation.
     * 
     * @param numberOfTicks Number of repetitions
     */
    public void run(int numberOfTicks){
        int timeLimit = ticks + numberOfTicks;
        ArrayList<Event> eventsList;
        ArrayList<Road> roadsList;
        ArrayList<Junction> junctionsList;
        try{
            while(ticks < timeLimit){ 
                // Execute the events for the current time
                eventsList =mapOfEvents.getOrDefault(ticks, null);
                if(eventsList != null){
                    eventsList.forEach((Event e) -> e.execute(roadMap));
                }            
                // Invoke method advance for roads
                roadsList = (ArrayList<Road>) roadMap.getRoads();
                if(!roadsList.isEmpty()){
                    roadsList.forEach((Road r) -> r.advance());
                }            
                // Invoke method advance for junction
                junctionsList = (ArrayList<Junction>) roadMap.getJunctions();
                if(!junctionsList.isEmpty()){
                    junctionsList.forEach((Junction j)->j.advance());
                }
                // current time increases
                ticks++;
                // write report
                if(output != null){
                    roadMap.getJunctions().forEach((Junction j) -> writeReport(j));
                    roadMap.getRoads().forEach((Road r)-> writeReport(r));
                    roadMap.getVehicles().forEach((Vehicle v) -> writeReport(v));
                }
            }
        }
        catch(Exception e){
            throw new SimulatorError("Error in TrafficSimulator at " + ticks + " time...", e);
        }
    }
    
    /**Adds a new event to the simulation. Given a new event it is added to the 
     * list of events to be executed during the execution of the simulation.
     * 
     * @param e New event to be added
     */
    public void addEvent(Event e){
        mapOfEvents.putValue(e.getScheduleTime(), e);
    }
    
    /**Sets to its initial value the simulation.
     * 
     */
    public void reset(){
        mapOfEvents = new MultiTreeMap<>( (a,b) -> a-b );
        this.output = null;
        ticks = 0;     
    }
    
    /**Changes the output stream. It changes the object output to a new value of OutputStream.
     * 
     * @param output Output stream
     */
    public void setOutputStream(OutputStream output){
        this.output = output;
    }
    
}
