package pr5.model;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import pr5.events.Event;
import pr5.exception.SimulatorError;
import pr5.util.MultiTreeMap;

/**
 * Simulates a system of vehicles driving through some roads and around
 * specified junctions.
 */
public class TrafficSimulator {

    private OutputStream output;
    /**
     * Map of events to be executed ordered by the time when they will be
     * executed
     */
    private MultiTreeMap<Integer, Event> mapOfEvents = new MultiTreeMap<>((a, b) -> a - b);
    /**
     * Internal counter
     */
    private int ticks;
    /**
     * Road map storing all the objects in the simulatation
     */
    private RoadMap roadMap;

    /**
     * List of observers to be used during the GUI execution
    */
    private List<TrafficSimulatorObserver> observers;
    
    /**
     * Class Constructor specifying output stream.
     *
     * @param output
     */
    public TrafficSimulator(OutputStream output) {
        this.output = output;
        roadMap = new RoadMap();
        ticks = 0;
    }

    /**
     * Given a certain SimulatedObject it generates its report.
     *
     * @param simObject
     * @throws SimulatorError Thrown when there is a problem with the output
     */
    private void writeReport(SimulatedObject simObject) throws SimulatorError {
        try {
            simObject.generateReport(ticks).store(output);
            output.write('\n');
        } catch (IOException e) {
            throw new SimulatorError("Error with " + simObject.getClass()
                    + " while storing report...", e);
        }
    }

    /**
     * Main loop of the simulator. It executes the events for the current time,
     * invoke the method advance for roads and junctions and increases the
     * internal counter. Finally, it writes the report for all the objects in
     * the simulation.
     *
     * @param numberOfTicks Number of repetitions
     */
    public void run(int numberOfTicks) {
        int timeLimit = ticks + numberOfTicks;
        ArrayList<Event> eventsList;
        
        try {
            while (ticks < timeLimit) {
                // Execute the events for the current time
                eventsList = mapOfEvents.getOrDefault(ticks, null);
                if (eventsList != null) {
                    eventsList.forEach((Event e) -> e.execute(roadMap));
                }
                // Invoke method advance for roads
                roadMap.getRoads().forEach((Road r) -> r.advance());
                
                // Invoke method advance for junction
                roadMap.getJunctions().forEach((Junction j) -> j.advance());
                
                // Current time increases
                ticks++;
                // Write report
                if (output != null) {
                    roadMap.getJunctions().forEach((Junction j) -> writeReport(j));
                    roadMap.getRoads().forEach((Road r) -> writeReport(r));
                    roadMap.getVehicles().forEach((Vehicle v) -> writeReport(v));
                }
            }
        } catch (Exception e) {
            throw new SimulatorError("Error in TrafficSimulator at " + ticks + " time...", e);
        }
    }

    /**
     * Adds a new event to the simulation. Given a new event it is added to the
     * list of events to be executed during the execution of the simulation.
     *
     * @param event
     */
    public void addEvent(Event event) {
        mapOfEvents.putValue(event.getScheduleTime(), event);
    }

    /**
     * Sets to its initial value the simulation.
     */
    public void reset() {
        mapOfEvents = new MultiTreeMap<>((a, b) -> a - b);
        this.output = null;
        ticks = 0;
    }

    /**
     * Changes the output stream. It changes the object output to a new value of
     * OutputStream.
     *
     * @param output Output stream
     */
    public void setOutputStream(OutputStream output) {
        this.output = output;
    }
    
    
    public void addObserver(TrafficSimulatorObserver o){
        
    }
    
    public void removeObserver(TrafficSimulatorObserver o){
        
    }
    
    private void notifyRegistered(TrafficSimulatorObserver o){
        
    }
    
    private void notifyReset(){
        
    }
    
    private void notifyEventAdded(){
        
    }
    
    private void notifyAdvanced(){
        
    }
    
    private void notifyError(SimulatorError e){
        
    }
    
    /**
     * Interfece which provides a way of dealing with events and the execution 
     * of a TrafficSimulator externally. 
     */
    public interface TrafficSimulatorObserver{
        public void registered(int time, RoadMap map, List<Event> events);
        public void reset(int time, RoadMap map, List<Event> events);
        public void eventAdded(int time, RoadMap map, List<Event> events);
        public void advanced(int time, RoadMap map, List<Event> events);
        public void simulatorError(int time, RoadMap map, List<Event> events, SimulatorError e);
        
    } 
}
