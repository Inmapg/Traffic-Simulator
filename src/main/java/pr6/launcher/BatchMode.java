package pr6.launcher;

import pr6.model.TrafficSimulator;

/**
 * Traffic simulator listener for batch mode.
 */
public enum BatchMode implements TrafficSimulator.TrafficSimulatorListener {
    INSTANCE;

    @Override
    public void registered(TrafficSimulator.UpdateEvent updateEvent) {
        System.out.println("[" + updateEvent.getEvent().toString() + "] "
                + "Simulation started...");
    }

    @Override
    public void reset(TrafficSimulator.UpdateEvent updateEvent) {
        System.out.println("[" + updateEvent.getEvent().toString() + "] "
                + "The system will return to its initial state...");
    }

    @Override
    public void newEvent(TrafficSimulator.UpdateEvent updateEvent) {
        System.out.println("[" + updateEvent.getEvent().toString() + "] "
                + "New event added correctly");
    }

    @Override
    public void advanced(TrafficSimulator.UpdateEvent updateEvent) {
        System.out.println("[" + updateEvent.getEvent().toString() + "] "
                + "The simulation has been advanced to time "
                + updateEvent.getCurrentTime());
    }

    @Override
    public void error(TrafficSimulator.UpdateEvent updateEvent, Exception e) {
        System.out.println("[" + updateEvent.getEvent().toString() + "] "
                + e.getMessage());
    }
}
