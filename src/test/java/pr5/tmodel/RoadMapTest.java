
package pr5.tmodel;

import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import pr5.model.Bike;
import pr5.model.Car;
import pr5.model.DirtRoad;
import pr5.model.Junction;
import pr5.model.LaneRoad;
import pr5.model.Road;
import pr5.model.RoadMap;

public class RoadMapTest {
    
    public RoadMap generateCommonRoadMap(){
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
    public void StorageFunctionalityTest(){
        RoadMap roadmap = generateCommonRoadMap();
        String ids = "road1,dirt1,lane1,car1,car2,bike1,j1,j2";
        StringBuilder sb = new StringBuilder("");
        roadmap.getRoads().forEach(obj -> sb.append(obj.getId()+","));
        roadmap.getVehicles().forEach(obj -> sb.append(obj.getId()+","));
        roadmap.getJunctions().forEach(obj -> sb.append(obj.getId()+","));
        assertEquals(ids, sb.substring(0, sb.length()-1));
    }
    
    @Test
    public void ExceptionsWithIdTest(){
        RoadMap roadmap = generateCommonRoadMap();
        try{
            roadmap.addJunction(new Junction("j1"));
        }
        catch(java.lang.IllegalArgumentException e){
            e.printStackTrace();
        }
        try{
            roadmap.addJunction(new Junction("j2"));
        }
        catch(java.lang.IllegalArgumentException e){
            e.printStackTrace();
        }
        try{
            Junction j1 = new Junction("j");
            roadmap.addRoad(new Road("road1", 40, 20, j1, j1));
        }
        catch(java.lang.IllegalArgumentException e){
            e.printStackTrace();
        }
        try{
            Junction j1 = new Junction("j");
            ArrayList<Junction> path = new ArrayList<Junction>();
            path.add(j1);
            roadmap.addVehicle(new Car("car2", 15, path, 15, 0.3, 10, 123456789));
        }
        catch(java.lang.IllegalArgumentException e){
            e.printStackTrace();
        }
    }
    
     @Test
    public void getItineraryTest(){
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
        String[] itinerary1 = { "jx1", "jx2" };
        String[] itinerary2 = { "jx2", "jx1" };
        ArrayList<Junction> itineraryPath1 = (ArrayList<Junction>) roadmap.getItinerary(itinerary1);
        ArrayList<Junction> itineraryPath2 = (ArrayList<Junction>) roadmap.getItinerary(itinerary2);
        ArrayList<Junction> expectedItineraryPath1 = new ArrayList<>();
        ArrayList<Junction> expectedItineraryPath2 = new ArrayList<>();
        expectedItineraryPath1.add(j1); expectedItineraryPath1.add(j2);
        expectedItineraryPath2.add(j2); expectedItineraryPath2.add(j1);
        assertEquals(expectedItineraryPath1, itineraryPath1);
        assertEquals(expectedItineraryPath2, itineraryPath2);
    }
}
