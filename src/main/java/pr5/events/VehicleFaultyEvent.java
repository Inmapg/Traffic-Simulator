package pr5.events;

import pr5.ini.IniSection;
import pr5.model.RoadMap;

/**
 * Breaks down an existing vehicle.
 */
public class VehicleFaultyEvent extends Event {

    private final int duration;
    private final String[] vehicles;

    /**
     * Class constructor specifying time, list of vehicles and duration.
     *
     * @param time
     * @param vehicles
     * @param duration
     */
    public VehicleFaultyEvent(int time, String[] vehicles, int duration) {
        super(time);
        this.duration = duration;
        this.vehicles = vehicles;
    }

    @Override
    public void execute(RoadMap roadmap) {
        try {
            for (String vehicleId : vehicles) {
                roadmap.getVehicle(vehicleId).makeFaulty(duration);
            }
        } catch (NullPointerException e) {
            throw e;
        }
    }

    /**
     * Builds the vehicle faulty event.
     *
     * @see Event.Builder
     */
    public static class Builder implements Event.Builder {

        @Override
        public Event parse(IniSection sec) {
            if (!"make_vehicle_faulty".equals(sec.getTag())) {
                return null;
            }
            return new VehicleFaultyEvent(
                    parseInt(sec, "time", 0),
                    parseStringList(sec, "vehicles"),
                    parseInt(sec, "duration", 1)
            );
        }
    }

}
