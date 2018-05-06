package pr5.model;

import pr5.ini.IniSection;

/**
 * Defines a dirt road.
 *
 * @see Road
 */
public class DirtRoad extends Road {

    private static final String TYPE = "dirt";

    /**
     * Class constructor specifying id, length, maximum speed, source junction
     * and destination junction.
     *
     * @param id
     * @param length
     * @param maxSpeed
     * @param sourceJunction
     * @param destinationJunction
     */
    public DirtRoad(String id, int length, int maxSpeed, Junction sourceJunction,
            Junction destinationJunction) {
        super(id, length, maxSpeed, sourceJunction, destinationJunction);
    }

    @Override
    protected int reduceSpeedFactor(int brokenVehicles) {
        return 1 + brokenVehicles;
    }

    @Override
    protected int calculateBaseSpeed() {
        return maxSpeed;
    }

    @Override
    protected void fillReportDetails(IniSection sec) {
        sec.setValue("type", TYPE);
        super.fillReportDetails(sec);
    }
}
