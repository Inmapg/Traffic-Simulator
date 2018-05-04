package pr5.model;

import java.util.List;
import java.util.Map;
import pr5.ini.IniSection;
import pr5.util.MultiTreeMap;

/**
 * Defines one of the main types of Simulated Object.
 *
 * @see SimulatedObject
 */
public class Road extends SimulatedObject {

    private static final String SECTION_TAG_NAME = "road_report";
    private Junction sourceJunction;
    private Junction destinationJunction;
    private static final int INITIAL_POS = 0;
    /**
     * List of vehicles ordered by insertion
     *
     * @see Vehicle
     */
    protected MultiTreeMap<Integer, Vehicle> vehiclesList = new MultiTreeMap<>((a, b) -> b - a);
    protected final int length;
    protected final int maxSpeed;

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
    public Road(String id, int length, int maxSpeed, Junction sourceJunction,
            Junction destinationJunction) {
        super(id);
        this.length = length;
        this.maxSpeed = maxSpeed;
        this.sourceJunction = sourceJunction;
        this.destinationJunction = destinationJunction;
    }

    /**
     * @return Source junction of the road
     */
    public Junction getSource() {
        return sourceJunction;
    }

    /**
     * @return Destionation junction of the road
     */
    public Junction getDestination() {
        return destinationJunction;
    }

    /**
     * Puts a new vehicle on road.
     *
     * @param newVehicle
     */
    public void enter(Vehicle newVehicle) {
        vehiclesList.putValue(INITIAL_POS, newVehicle);
    }

    /**
     * Takes a vehicle off the road.
     *
     * @param exitVehicle Outgoing vehicle
     */
    public void exit(Vehicle exitVehicle) {
        vehiclesList.removeValue(exitVehicle.getLocation(), exitVehicle);
    }

    /**
     * @return Base speed of the road
     */
    protected int calculateBaseSpeed() {
        return (int) Math.min(maxSpeed, maxSpeed / (Math.max(1, vehiclesList.sizeOfValues())) + 1);
    }

    /**
     * Returns the speed factor of the road.
     *
     * @param brokenVehicles Number of faulty vehicles
     * @return 2 if there are faulty vehicles, 1 if not
     */
    protected int reduceSpeedFactor(int brokenVehicles) {
        return (brokenVehicles > 0) ? 2 : 1;
    }

    @Override
    public void advance() {
        MultiTreeMap<Integer, Vehicle> updated = new MultiTreeMap<>((a, b) -> b - a);
        int brokenVehicles = 0;
        for (Vehicle v : vehiclesList.innerValues()) {
            if (v.getFaultyTime() > 0) {
                brokenVehicles++;
            }
            v.setSpeed(calculateBaseSpeed() / reduceSpeedFactor(brokenVehicles));
            v.advance();
            updated.putValue(v.getLocation(), v);
        }
        vehiclesList.clear();
        vehiclesList = updated;
    }

    /**
     * @return Length of the road
     */
    public int getLength() {
        return length;
    }

    @Override
    protected String getReportSectionTag() {
        return SECTION_TAG_NAME;
    }

    @Override
    protected void fillReportDetails(IniSection sec) {
        StringBuilder sb = new StringBuilder();
        if (!vehiclesList.isEmpty()) {
            vehiclesList.innerValues().forEach((Vehicle v)
                    -> sb.append("(")
                            .append(v.getId()).append(",").append(v.getLocation())
                            .append("),"));
            sec.setValue("state", sb.substring(0, sb.length() - 1));
        } else {
            sec.setValue("state", "");
        }
    }

    @Override
    public void describe(Map<String, String> out) {
        super.describe(out);
        out.put("Source", sourceJunction.getId());
        out.put("Target", destinationJunction.getId());
        out.put("Length", "" + length);
        out.put("Max Speed", "" + maxSpeed);

        StringBuilder sb = new StringBuilder();
        if (!vehiclesList.isEmpty()) {
            vehiclesList.innerValues().forEach((Vehicle v)
                    -> sb.append(v.getId()).append(","));
            out.put("Vehicles", "[" + sb.substring(0, sb.length() - 1) + "]");
        } else {
            out.put("Vehicles", "[]");
        }

    }

    public List<Vehicle> getVehicleList() {
        return vehiclesList.valuesList();
    }

}
