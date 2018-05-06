package pr6.model;

import java.util.List;
import pr6.ini.IniSection;

/**
 * Defines bike: a type of vehicle.
 *
 * @see Vehicle
 */
public class Bike extends Vehicle {

    private static final String TYPE = "bike";

    /**
     * Class constructor specifying id, maximum speed and itinerary.
     *
     * @param id
     * @param maxSpeed
     * @param itinerary
     */
    public Bike(String id, int maxSpeed, List<Junction> itinerary) {
        super(id, maxSpeed, itinerary);
    }

    @Override
    public void makeFaulty(int counter) {
        if (faulty > 0 || currentSpeed > maxSpeed / 2) {
            super.makeFaulty(counter);
        }
    }

    @Override
    public void fillReportDetails(IniSection sec) {
        sec.setValue("type", TYPE);
        super.fillReportDetails(sec);
    }
}
