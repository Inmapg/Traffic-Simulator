package pr5.control;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import pr5.events.*;
import pr5.exception.SimulatorError;
import pr5.ini.Ini;
import pr5.ini.IniSection;
import pr5.model.TrafficSimulator;

/**Executes and controls the program.
 * @author Inmapg
 * @author Arturacu
 * @version 2.0
 */
public class Controller {
    /**Traffic simulator object
     * @see TrafficSimulator
     */
    private TrafficSimulator trafficSim;
    /**Period of time in which the traffic simulator will be running*/
    private int time;
    /**Output stream*/
    private OutputStream output;
    
    // The order matters
    /**List of Traffic Simulator's events*/
    public static Event.Builder[] availableEventBuilders = { 
        new LaneRoadEvent.Builder(),
        new DirtRoadEvent.Builder(),
        new VehicleFaultyEvent.Builder(),
        new CarEvent.Builder(),
        new BikeEvent.Builder(),
        new MostCrowdedJunctionEvent.Builder(),
        new RoundRobinJunctionEvent.Builder(),
        new JunctionEvent.Builder(),
        new RoadEvent.Builder(),
        new VehicleEvent.Builder()
    };
    
    /**Class constructor specifying time and output.
     * 
     * @param time Period of time in which the traffic simulator will be running
     * @param output Output stream
     */
    public Controller(int time, OutputStream output){
       this.trafficSim = new TrafficSimulator(output);
       this.time = time;
       this.output = output;
    }
    
    /**Class constructor specifying output.
     * Time is zero-initialized
     * 
     * @param output Output stream 
     */
    public Controller(OutputStream output){
        this.trafficSim = new TrafficSimulator(output);
        this.time = 0;
        this.output = output;
    }
    
    /**Parses the event that will be created
     * 
     * @param sec Information about the event created
     * @return New event created, null if not
     * @see Event
     */
    private static Event parse(IniSection sec){
        int i = 0; 
        Event newEvent = null;
        while(newEvent == null && i < availableEventBuilders.length){
            newEvent = availableEventBuilders[i].parse(sec);
            i++;
        }
        return newEvent;
    }
    
    /**Loads events from a file.
     * 
     * @param input File name
     * @throws IOException If an input or output exception occurred
     */
    public void loadEvents(String input) throws IOException{
        Ini ini = new Ini(new FileInputStream(input));
            ini.getSections().forEach((IniSection sec) -> {
                Event newEvent = parse(sec);
                if(newEvent == null){
                    throw new SimulatorError();
                }
                trafficSim.addEvent(newEvent);
            });
        
    }
    
    /**Runs the program.
     * 
     * @param input File name
     * @param timeLimit Period of time in which the programme will be executing
     */
    public void run(String input, int timeLimit){
        try{
            loadEvents(input);
        }
        catch(IOException | SimulatorError e){
            throw new SimulatorError("Error while loading events...", e);
        }
        try{
            trafficSim.run(timeLimit);
        }catch(SimulatorError e){
            throw new SimulatorError("Error when executing running method in Traffic Simulator...", e);
        }
        
    }

}
