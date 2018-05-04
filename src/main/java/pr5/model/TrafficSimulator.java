package pr5.model;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;
import pr5.events.Event;
import pr5.exception.SimulatorError;
import pr5.ini.Ini;
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
    private int ticks = 0;
    /**
     * Road map storing all the objects in the simulatation
     */
    private RoadMap roadMap = new RoadMap();

    /**
     * List of observers to be used during the GUI execution
     */
    private List<TrafficSimulatorListener> listeners = new ArrayList<>();

    /**
     * Class Constructor specifying output stream.
     *
     * @param output
     */
    public TrafficSimulator(OutputStream output) {
        this.output = output;
    }

    /**
     * Given a certain SimulatedObject list it generates its report into a ini.
     *
     * @param ini
     * @param simObjectList
     */
    public void writeReport(Ini ini, List<? extends SimulatedObject> simObjectList) {
        simObjectList.forEach((sim) -> {
            ini.addSection(sim.generateReport(ticks));
        });
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

                // listeners are notified
                notifyAdvanced();

                // Write report
                if (output != null) {
                    roadMap.getJunctions().forEach((Junction j) -> writeReport(j));
                    roadMap.getRoads().forEach((Road r) -> writeReport(r));
                    roadMap.getVehicles().forEach((Vehicle v) -> writeReport(v));
                }
            }
        } catch (Exception e) {
            notifyError(new SimulatorError("Error in TrafficSimulator at " + ticks + " time...", e));
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
        notifyEventAdded();
    }

    /**
     * Sets to its initial value the simulation.
     */
    public void reset() {
        mapOfEvents = new MultiTreeMap<>((a, b) -> a - b);
        roadMap = new RoadMap();
        this.output = null;
        ticks = 0;
        notifyReset();
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

    /**
     * Adds a new simulator listener to the simulation.
     *
     * @param newListener
     */
    public void addSimulatorListener(TrafficSimulatorListener newListener) {
        listeners.add(newListener);
        UpdateEvent ue = new UpdateEvent(EventType.REGISTERED);
        SwingUtilities.invokeLater(() -> newListener.registered(ue));
    }

    /**
     * Removes a simulator listener from the simulation.
     *
     * @param newListener
     */
    public void removeSimulatorListener(TrafficSimulatorListener newListener) {
        listeners.remove(newListener);
    }

    /**
     * Notifies the listeners in case of resetting the simulator.
     */
    private void notifyReset() {
        listeners.forEach((l) -> {
            l.reset(new UpdateEvent(EventType.RESET));
        });
    }

    /**
     * Notifies the listeners in case of adding a new event to the simulator.
     */
    private void notifyEventAdded() {
        listeners.forEach((l) -> {
            l.newEvent(new UpdateEvent(EventType.NEW_EVENT));
        });
    }

    /**
     * Notifies the listeners in case of advancing the simulator.
     */
    private void notifyAdvanced() {
        listeners.forEach((l) -> {
            l.advanced(new UpdateEvent(EventType.ADVANCED));
        });
    }

    /**
     * Notifies the listeners when an error occurs during the simulation.
     */
    private void notifyError(SimulatorError e) {
        listeners.forEach((l) -> {
            l.error(new UpdateEvent(EventType.ERROR), e.getMessage());
        });
    }

    /**
     * Interfece which provides a way of dealing with events and the execution
     * of a TrafficSimulator externally.
     */
    public interface TrafficSimulatorListener {

        /**
         * Used to register an event.
         *
         * @param updateEvent
         */
        public void registered(UpdateEvent updateEvent);

        /**
         * Used when the simulator has been reset.
         *
         * @param updateEvent
         */
        public void reset(UpdateEvent updateEvent);

        /**
         * Used when a new event occurs.
         *
         * @param updateEvent
         */
        public void newEvent(UpdateEvent updateEvent);

        /**
         * Used when the simulator has advanced.
         *
         * @param updateEvent
         */
        public void advanced(UpdateEvent updateEvent);

        /**
         * Used when an error occurs during the simulation.
         *
         * @param updateEvent
         * @param errorMessage
         */
        public void error(UpdateEvent updateEvent, String errorMessage);

    }

    /**
     * Contains the different types of listeners.
     */
    public enum EventType {
        REGISTERED, RESET, NEW_EVENT, ADVANCED, ERROR
    };

    /**
     * Contains the information of the event.
     */
    public class UpdateEvent {

        private final EventType type;

        /**
         * Class constructor specifying the type of event.
         *
         * @param eventType
         */
        public UpdateEvent(EventType eventType) {
            this.type = eventType;
        }

        /**
         *
         * @return the type of event
         */
        public EventType getEvent() {
            return type;
        }

        /**
         *
         * @return the road map
         */
        public RoadMap getRoadMap() {
            return roadMap;
        }

        /**
         *
         * @return the queue of events
         */
        public List<Event> getEventQueue() {
            return mapOfEvents.valuesList();
        }

        /**
         *
         * @return the current time
         */
        public int getCurrentTime() {
            return ticks;
        }
    }

}
