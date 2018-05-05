package pr5.launcher;

import pr5.model.TrafficSimulator;

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
    public void error(TrafficSimulator.UpdateEvent updateEvent, String errorMessage) {
        System.out.println("[" + updateEvent.getEvent().toString() + "] "
                + errorMessage);
    }

}
