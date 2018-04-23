package pr5.model;

import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class RoadMapTest {

    /**
     *
     * @return a generated road map
     */
    public RoadMap generateCommonRoadMap() {
        RoadMap roadmap = new RoadMap();
        Junction j1 = new Junction("j1");
        Junction j2 = new Junction("j2");
        ArrayList<Junction> itinerary1 = new ArrayList<>();
        itinerary1.add(j1);
        itinerary1.add(j2);
        roadmap.addJunction(j1);
        roadmap.addJunction(j2);
        Road r1 = new Road("road1", 40, 20, j1, j2);
        Road r2 = new DirtRoad("dirt1", 23, 20, j1, j2);
        Road r3 = new LaneRoad("lane1", 14, 10, j1, j2, 4);
        roadmap.addRoad(r1);
        roadmap.addRoad(r2);
        roadmap.addRoad(r3);
        roadmap.addVehicle(new Car("car1", 20, itinerary1, 20, 0.2, 10, 123456789));
        roadmap.addVehicle(new Car("car2", 15, itinerary1, 15, 0.3, 10, 123456789));
        roadmap.addVehicle(new Bike("bike1", 20, itinerary1));
        return roadmap;
    }

    @Test
    /**
     * Checks the storage of the road map.
     */
    public void storageFunctionalityTest() {
        RoadMap roadmap = generateCommonRoadMap();
        String ids = "road1,dirt1,lane1,car1,car2,bike1,j1,j2";
        StringBuilder sb = new StringBuilder("");
        roadmap.getRoads().forEach(obj -> sb.append(obj.getId()).append(","));
        roadmap.getVehicles().forEach(obj -> sb.append(obj.getId()).append(","));
        roadmap.getJunctions().forEach(obj -> sb.append(obj.getId()).append(","));
        assertEquals("The road map should contain ", ids, sb.substring(0, sb.length() - 1));
    }

    @Test
    /**
     * Checks if excepctions are correctly controlled.
     */
    public void exceptionsWithIdTest() {
        RoadMap roadmap = generateCommonRoadMap();
        try {
            roadmap.addJunction(new Junction("j1"));
        } catch (java.lang.IllegalArgumentException e) {
            System.err.println("- Expected exception while adding junction in method exceptionsWithIdTest");
        }
        try {
            roadmap.addJunction(new Junction("j2"));
        } catch (java.lang.IllegalArgumentException e) {
            System.err.println("- Expected exception while adding junction in method exceptionsWithIdTest");
        }
        try {
            Junction j1 = new Junction("j");
            roadmap.addRoad(new Road("road1", 40, 20, j1, j1));
        } catch (java.lang.IllegalArgumentException e) {
            System.err.println("- Expected exception while adding road in method exceptionsWithIdTest");
        }
        try {
            Junction j1 = new Junction("j");
            ArrayList<Junction> path = new ArrayList<Junction>();
            path.add(j1);
            roadmap.addVehicle(new Car("car2", 15, path, 15, 0.3, 10, 123456789));
        } catch (java.lang.IllegalArgumentException e) {
            System.err.println("- Expected exception while adding vehicle in method exceptionsWithIdTest");
        }
    }

    @Test
    /**
     * Checks addition of itineraries to the road map.
     */
    public void getItineraryTest() {
        RoadMap roadmap = generateCommonRoadMap();
        Junction j1 = new Junction("jx1");
        Junction j2 = new Junction("jx2");
        roadmap.addJunction(j1);
        roadmap.addJunction(j2);
        Road r1 = new Road("roadx1", 40, 20, j1, j2);
        Road r2 = new DirtRoad("dirtx1", 23, 20, j1, j2);
        Road r3 = new LaneRoad("lanex1", 14, 10, j2, j1, 4);
        roadmap.addRoad(r1);
        roadmap.addRoad(r2);
        roadmap.addRoad(r3);
        String[] itinerary1 = {"jx1", "jx2"};
        String[] itinerary2 = {"jx2", "jx1"};
        ArrayList<Junction> itineraryPath1 = (ArrayList<Junction>) roadmap.getItinerary(itinerary1);
        ArrayList<Junction> itineraryPath2 = (ArrayList<Junction>) roadmap.getItinerary(itinerary2);
        ArrayList<Junction> expectedItineraryPath1 = new ArrayList<>();
        ArrayList<Junction> expectedItineraryPath2 = new ArrayList<>();
        expectedItineraryPath1.add(j1);
        expectedItineraryPath1.add(j2);
        expectedItineraryPath2.add(j2);
        expectedItineraryPath2.add(j1);
        assertEquals("The first itinerary should have been ", expectedItineraryPath1, itineraryPath1);
        assertEquals("The second itinerary should have been ", expectedItineraryPath2, itineraryPath2);
    }
}
