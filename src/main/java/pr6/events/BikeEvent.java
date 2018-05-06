package pr6.events;

import pr6.exception.SimulatorError;
import pr6.ini.IniSection;
import pr6.model.Bike;
import pr6.model.RoadMap;

/**
 * Creates a new bike.
 */
public class BikeEvent extends VehicleEvent {

    /**
     * Class constructor specifying time, id, itinerary and maximum speed.
     *
     * @param time When the event occurs
     * @param id
     * @param itinerary
     * @param maxSpeed
     */
    public BikeEvent(int time, String id, String[] itinerary, int maxSpeed) {
        super(time, id, maxSpeed, itinerary);
    }

    @Override
    public void execute(RoadMap roadmap) {
        try {
            roadmap.addVehicle(new Bike(id, maxSpeed,
                    roadmap.getItinerary(itinerary)));
        } catch (SimulatorError e) {
            throw e;
        }
    }

    /**
     * Builds the bike event.
     *
     * @see Event.Builder
     */
    public static class Builder implements Event.Builder {

        @Override
        public Event parse(IniSection sec) {
            if (!"new_vehicle".equals(sec.getTag())
                    || !"bike".equals(sec.getValue("type"))) {
                return null;
            }
            return new BikeEvent(
                    parseInt(sec, "time", 0),
                    parseString(sec, "id"),
                    parseStringList(sec, "itinerary"),
                    parseInt(sec, "max_speed", 1)
            );
        }

    }
}
