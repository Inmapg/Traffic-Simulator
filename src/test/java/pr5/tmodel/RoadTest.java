package pr5.tmodel;

import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import pr5.ini.IniSection;
import pr5.model.Junction;
import pr5.model.Road;
import pr5.model.Vehicle;

public class RoadTest {
    
    @Test
    public void OneVehicleTest(){
        Junction sourceJunction = new Junction("j1");
        Junction destinationJunction = new Junction("j2");
        Road oneVehicleRoad = new Road("r1", 40, 20, sourceJunction, destinationJunction);
        sourceJunction.addOutGoingRoad(oneVehicleRoad, destinationJunction);
        destinationJunction.addIncomingRoad(oneVehicleRoad);
        ArrayList<Junction> itinerary = new ArrayList<>();
        itinerary.add(sourceJunction);
        itinerary.add(destinationJunction);
        Vehicle vehicle = new Vehicle("v1", 10, itinerary); 
        IniSection result = oneVehicleRoad.generateReport(0);
        IniSection correct = new IniSection("road_report");
        
        correct.setValue("time", "0");
        correct.setValue("id", "r1");
        correct.setValue("state", "(v1,0)");
        assertEquals(correct, result);
        
        oneVehicleRoad.advance();
        oneVehicleRoad.advance();
        result = oneVehicleRoad.generateReport(2);
        correct.setValue("time", "2");
        correct.setValue("state", "(v1,20)");
        assertEquals(correct, result);
        
        oneVehicleRoad.advance();
        oneVehicleRoad.advance();
        result = oneVehicleRoad.generateReport(4);
        correct.setValue("time", "4");
        correct.setValue("state", "(v1,40)");
        assertEquals(correct, result);
    }
    
    @Test
    public void MultipleVehicleTest(){
        Junction sourceJunction = new Junction("j1");
        Junction destinationJunction = new Junction("j2");
        Road MultipleVehicleRoad = new Road("r1", 40, 20, sourceJunction, destinationJunction);
        sourceJunction.addOutGoingRoad(MultipleVehicleRoad, destinationJunction);
        destinationJunction.addIncomingRoad(MultipleVehicleRoad);
        ArrayList<Junction> itinerary = new ArrayList<>();
        itinerary.add(sourceJunction);
        itinerary.add(destinationJunction);
        Vehicle v1 = new Vehicle("v1", 10, itinerary); 
        Vehicle v2 = new Vehicle("v2", 15, itinerary); 
        Vehicle v3 = new Vehicle("v3", 20, itinerary); 
        Vehicle v4 = new Vehicle("v4", 5, itinerary); 
        IniSection result = MultipleVehicleRoad.generateReport(0);
        IniSection correct = new IniSection("road_report");
        
        correct.setValue("time", "0");
        correct.setValue("id", "r1");
        correct.setValue("state", "(v1,0),(v2,0),(v3,0),(v4,0)");
        assertEquals(correct, result);
        
        MultipleVehicleRoad.advance();
        result = MultipleVehicleRoad.generateReport(1);
        correct.setValue("time", "1");
        correct.setValue("state", "(v1,6),(v2,6),(v3,6),(v4,5)");
        assertEquals(correct, result);
        
        for(int i = 0; i < 6; i++){
            MultipleVehicleRoad.advance();
        } 
        result = MultipleVehicleRoad.generateReport(7);
        correct.setValue("time", "7");
        correct.setValue("state", "(v1,40),(v2,40),(v3,40),(v4,35)");
        assertEquals(correct, result);
        
        MultipleVehicleRoad.advance();
        result = MultipleVehicleRoad.generateReport(8);
        correct.setValue("time", "8");
        correct.setValue("state", "(v1,40),(v2,40),(v3,40),(v4,40)");
        assertEquals(correct, result);
    }
    
    @Test
    public void CrowdedRoadWithFaultyVehiclesTest(){
        Junction sourceJunction = new Junction("j1");
        Junction destinationJunction = new Junction("j2");
        Road CrowedRoadWithFaultyVehicles = new Road("r1", 20, 20, sourceJunction, destinationJunction);
        sourceJunction.addOutGoingRoad(CrowedRoadWithFaultyVehicles, destinationJunction);
        destinationJunction.addIncomingRoad(CrowedRoadWithFaultyVehicles);
        ArrayList<Junction> itinerary = new ArrayList<>();
        itinerary.add(sourceJunction);
        itinerary.add(destinationJunction);
        Vehicle v1 = new Vehicle("v1", 10, itinerary); 
        Vehicle v2 = new Vehicle("v2", 15, itinerary); 
        Vehicle v3 = new Vehicle("v3", 20, itinerary); 
        Vehicle v4 = new Vehicle("v4", 5, itinerary); 
        v3.makeFaulty(3);
        v2.makeFaulty(2);
        IniSection result = CrowedRoadWithFaultyVehicles.generateReport(0);
        IniSection correct = new IniSection("road_report");
        
        correct.setValue("time", "0");
        correct.setValue("id", "r1");
        correct.setValue("state", "(v1,0),(v2,0),(v3,0),(v4,0)");
        assertEquals(correct, result);
        
        CrowedRoadWithFaultyVehicles.advance();
        result = CrowedRoadWithFaultyVehicles.generateReport(1);
        correct.setValue("time", "1"); 
        correct.setValue("state", "(v1,6),(v4,3),(v2,0),(v3,0)");
        assertEquals(correct, result);
        
        CrowedRoadWithFaultyVehicles.advance();
        CrowedRoadWithFaultyVehicles.advance();
        CrowedRoadWithFaultyVehicles.advance();
        result = CrowedRoadWithFaultyVehicles.generateReport(4);
        correct.setValue("time", "4"); 
        correct.setValue("state", "(v1,20),(v4,18),(v2,12),(v3,6)");
        assertEquals(correct, result);
    } 
            
    
}
