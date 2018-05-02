// Not finished. Not working
package pr5.model;

import pr5.ini.IniSection;
import pr5.model.Junction.IncomingRoad;

/**
 * Defines a circular junction.
 *
 * @see Junction
 */
public class RoundRobinJunction extends TimeSliceJunction {

    private static final String TYPE = "rr";
    private final int minTimeSlice;
    private final int maxTimeSlice;



    /**
     * Class constructor specifying id, minimum time slice and maximum time
     * slice.
     *
     * @param id
     * @param minTimeSlice
     * @param maxTimeSlice
     */
    public RoundRobinJunction(String id, int minTimeSlice, int maxTimeSlice) {
        super(id);
        this.minTimeSlice = minTimeSlice;
        this.maxTimeSlice = maxTimeSlice;
        currentRoad = null;
    }

    @Override
    protected IncomingRoad createIncomingRoadQueue(Road r) {
        TimeSliceIncomingRoad newIncomingRoad = new TimeSliceIncomingRoad(r, maxTimeSlice);

        return newIncomingRoad;
    }

    @Override
    public void addIncomingRoad(Road newRoad) {
        incomingRoadMap.put(newRoad, createIncomingRoadQueue(newRoad));
    }

    @Override
    protected void switchLights() {
        if (currentRoad == null) {
            nextRoad = incomingRoadMap.keySet().iterator();
            currentRoad = (TimeSliceIncomingRoad) incomingRoadMap.get(nextRoad.next());
            currentRoad.onGreenLight();            
        } else if (currentRoad.timeIsOver()) { // if the interval time is used up
            currentRoad.offGreenLight(); // set the lights off
            if (!currentRoad.used()) { // there was no reduction in time
                currentRoad.setIntervalTime(Math.max(currentRoad.getIntervalTime() - 1, minTimeSlice));
            } else if (currentRoad.completelyUsed()) { // time completely used
                currentRoad.setIntervalTime(Math.min(currentRoad.getIntervalTime() + 1, maxTimeSlice));
            }

            currentRoad.reset(); // set spent-time to zero

            if (nextRoad == null || !nextRoad.hasNext()) {
                nextRoad = incomingRoadMap.keySet().iterator();
            }
            currentRoad = (TimeSliceIncomingRoad) incomingRoadMap.get(nextRoad.next());
            currentRoad.onGreenLight();
        } 

    }

    @Override
    protected void fillReportDetails(IniSection sec) {
        super.fillReportDetails(sec);
        sec.setValue("type", TYPE);
    }
}
