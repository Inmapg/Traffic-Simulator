package pr6.control;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import pr6.events.*;
import pr6.exception.SimulatorError;
import pr6.ini.Ini;
import pr6.ini.IniError;
import pr6.ini.IniSection;
import pr6.model.SimulatedObject;
import pr6.model.TrafficSimulator;

/**
 * Executes and controls the program.
 */
public class Controller {

    /**
     * Traffic simulator object
     *
     * @see TrafficSimulator
     */
    private final TrafficSimulator trafficSim;
    /**
     * Period of time in which the traffic simulator will be running
     */
    private int time;
    /**
     * Output stream
     */
    private OutputStream output;
    /**
     * Thread of the simulator
     */
    private Thread mainThread;
    /**
     * List of Traffic Simulator's events. The order matters.
     */
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

    /**
     * Class constructor specifying time and output.
     *
     * @param time Period of time in which the traffic simulator will be running
     * @param output
     */
    public Controller(int time, OutputStream output) {

        this.trafficSim = new TrafficSimulator(output);
        this.time = time;
        this.output = output;
        mainThread = new Thread(this.trafficSim);
    }

    /**
     * Class constructor specifying output. Time is zero-initialized
     *
     * @param output
     */
    public Controller(OutputStream output) {
        this.trafficSim = new TrafficSimulator(output);
        this.time = 0;
        this.output = output;
        mainThread = new Thread(this.trafficSim);
    }

    /**
     * Parses the event that will be created
     *
     * @param sec Information about the event created
     * @return New event created, null if not
     * @see Event
     */
    private static Event parse(IniSection sec) {
        int i = 0;
        Event newEvent = null;
        while (newEvent == null && i < availableEventBuilders.length) {
            newEvent = availableEventBuilders[i].parse(sec);
            i++;
        }
        return newEvent;
    }

    /**
     * Loads events from a file.
     *
     * @param input File name
     * @throws IOException If an input or output exception occurred
     */
    public void loadEvents(String input) throws IOException {
        Ini ini = new Ini(new FileInputStream(input));
        ini.getSections().forEach((IniSection sec) -> {
            Event newEvent = parse(sec);
            if (newEvent == null) {
                throw new SimulatorError();
            }
            trafficSim.addEvent(newEvent);
        });

    }

    public void loadEvents(InputStream input) throws IOException, IniError {
        Ini ini = new Ini(input);
        ini.getSections().forEach((IniSection sec) -> {
            try {
                Event newEvent = parse(sec);
                if (newEvent == null) {
                    throw new SimulatorError("The section with tag " + sec.getTag()
                            + " is not a valid event");
                }
                trafficSim.addEvent(newEvent);
            } catch (NullPointerException e) {
                throw new SimulatorError("The event was not correctly defined", e);
            } catch (NumberFormatException e) {
                throw new SimulatorError("A numeric field is not correctly filled out", e);
            }
        });
    }

    /**
     * Runs the program.
     *
     * @param input File name
     * @param timeLimit Period of time in which the programme will be executing
     */
    public void run(String input, int timeLimit) {
        try {
            loadEvents(input);
        } catch (IOException | SimulatorError e) {
            throw new SimulatorError("Error while loading events from file "
                    + input, e);
        }
        run(timeLimit);
    }

    /**
     * Runs the program. Events have been loaded previously.
     *
     * @param timeLimit Period of time in which the programme will be executing
     */
    public void run(int timeLimit) {
        try {
            trafficSim.setTicksToExecute(timeLimit);
            mainThread = new Thread(trafficSim);
            mainThread.start();
        } catch (SimulatorError e) {
            throw new SimulatorError("Error when executing running method in"
                    + " Traffic Simulator...", e);
        }
    }

    /**
     * Runs the program specifiying a sleep time for each execution of the main
     * thread.
     *
     * @param timeLimit
     * @param sleepTime
     */
    public void run(int timeLimit, int sleepTime) {
        trafficSim.setSleepTime(sleepTime);
        run(timeLimit);
    }

    /**
     * Stops the main thread's execution.
     */
    public void stop() {
        if (mainThread.isAlive()) {
            mainThread.interrupt();
        }
    }

    /**
     *
     * @return the default time value of the simulation
     */
    public int getDefaultTime() {
        return time;
    }

    /**
     * Adds a new listener to the simulator.
     *
     * @param newListener
     */
    public void addSimulatorListener(TrafficSimulator.TrafficSimulatorListener newListener) {
        trafficSim.addSimulatorListener(newListener);
    }

    /**
     * Changes the output strem.
     *
     * @param output
     */
    public void setOutputStream(OutputStream output) {
        trafficSim.setOutputStream(output);
    }

    /**
     * Resets the simulator.
     */
    public void reset() {
        trafficSim.reset();
    }

    /**
     * Writes the report of a determined list of simulated object.
     *
     * @param ini Where the report is going to be writen
     * @param simObjectList
     */
    public void writeReport(Ini ini, List<? extends SimulatedObject> simObjectList) {
        trafficSim.writeReport(ini, simObjectList);
    }
}