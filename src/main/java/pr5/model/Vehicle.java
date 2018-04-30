package pr5.model;
// Revisar el final void

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import pr5.ini.IniSection;

/**
 * Defines one of the main types of Simulated Object.
 *
 * @see SimulatedObject
 */
public class Vehicle extends SimulatedObject {

    private static final String SECTION_TAG_NAME = "vehicle_report";
    private final String itineraryToString;
    protected final int maxSpeed;
    protected final ArrayList<Junction> itinerary;
    protected int currentJunction;
    protected int currentSpeed;
    protected int kilometrage;
    protected int location;
    protected int faulty;
    /**
     * Shows if the vehicle has arrived to its destination
     */
    protected boolean hasArrived;
    protected Road currentRoad;

    /**
     * Class constructor. The rest of attributes are zero-initialized.
     *
     * @param id
     * @param maxSpeed
     * @param itinerary
     */
    public Vehicle(String id, int maxSpeed, List<Junction> itinerary) {
        super(id);
        this.itinerary = new ArrayList(itinerary);
        this.maxSpeed = maxSpeed;
        currentJunction = 0;
        currentSpeed = 0;
        kilometrage = 0;
        moveToNextRoad();
        ArrayList<String> aux = new ArrayList<>();
        itinerary.forEach((j) -> { aux.add(j.getId());  });
        itineraryToString = "[" + String.join(",", aux) + "]";
    }

    /**
     * Moves itself to next road.
     */
    public final void moveToNextRoad() {
        if (currentJunction > 0) {
            currentRoad.exit(this);
        }
        location = 0;
        if (currentJunction < itinerary.size() - 1) {
            currentRoad = itinerary.get(currentJunction).roadTo(itinerary.get(++currentJunction));
            currentRoad.enter(this);
        } else {
            hasArrived = true;
        }

    }

    /**
     * Breaks down the vehicle.
     *
     * @param duration
     */
    public void makeFaulty(int duration) {
        faulty += duration;
        setSpeed(0);
    }

    /**
     * Sets the speed of the vehicle.
     *
     * @param newSpeed
     */
    public void setSpeed(int newSpeed) {
        currentSpeed = (getFaultyTime() == 0 && currentRoad.getLength() != location)
                ? Math.min(maxSpeed, newSpeed) : 0;
    }

    /**
     * @return breakdown duration
     */
    public int getFaultyTime() {
        return faulty;
    }

    /**
     * @return Location
     */
    public int getLocation() {
        return location;
    }

    /**
     * @return Road where the vehicle is
     * @see Road
     */
    public Road getRoad() {
        return currentRoad;
    }

    @Override
    protected void advance() {
        if (faulty > 0) {
            makeFaulty(-1);
        } else if (location != currentRoad.getLength()) {
            kilometrage -= location;
            location = Math.min(currentRoad.getLength(), location + currentSpeed);
            kilometrage += location;
            if (location == currentRoad.getLength()) {
                currentSpeed = 0;
                itinerary.get(currentJunction).enter(this);
            }
        }
    }

    @Override
    protected String getReportSectionTag() {
        return SECTION_TAG_NAME;
    }

    @Override
    protected void fillReportDetails(IniSection sec) {
        sec.setValue("speed", "" + currentSpeed);
        sec.setValue("kilometrage", "" + kilometrage);
        sec.setValue("faulty", "" + faulty);
        if (!hasArrived) {
            StringBuilder sb = new StringBuilder();
            sb.append('(').append(currentRoad.getId()).append(',').append(location).append(')');
            sec.setValue("location", sb.toString());
        } else {
            sec.setValue("location", "arrived");
        }
    }

     public void describe(Map<String, String> out){
         super.describe(out);
        out.put("Road", currentRoad.getId());
        out.put("Location", "" + location);
        out.put("Speed", "" + currentSpeed);
        out.put("Km", "" + kilometrage);
        out.put("Faulty units", "" + faulty);
        out.put("Itinerary", itineraryToString);
   }
}
