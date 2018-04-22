package pr5.model;

import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import pr5.ini.IniSection;
import pr5.model.Junction;
import pr5.model.Road;
import pr5.model.Vehicle;

public class RoadTest {
    
    @Test
    /**
     * Checks the functionality of a road with one vehicle.
     */
    public void oneVehicleTest(){
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
        assertEquals("Road's state should be ", correct, result);
        
        oneVehicleRoad.advance();
        oneVehicleRoad.advance();
        result = oneVehicleRoad.generateReport(2);
        correct.setValue("time", "2");
        correct.setValue("state", "(v1,20)");
        assertEquals("Road's state should be ", correct, result);
        
        oneVehicleRoad.advance();
        oneVehicleRoad.advance();
        result = oneVehicleRoad.generateReport(4);
        correct.setValue("time", "4");
        correct.setValue("state", "(v1,40)");
        assertEquals("Road's state should be ", correct, result);
    }
    
    @Test
    /**
     * Checks the functionality of a road with multiple vehicles.
     */
    public void multipleVehicleTest(){
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
        assertEquals("Road's state should be ", correct, result);
        
        MultipleVehicleRoad.advance();
        result = MultipleVehicleRoad.generateReport(1);
        correct.setValue("time", "1");
        correct.setValue("state", "(v1,6),(v2,6),(v3,6),(v4,5)");
        assertEquals("Road's state should be ", correct, result);
        
        for(int i = 0; i < 6; i++){
            MultipleVehicleRoad.advance();
        } 
        result = MultipleVehicleRoad.generateReport(7);
        correct.setValue("time", "7");
        correct.setValue("state", "(v1,40),(v2,40),(v3,40),(v4,35)");
        assertEquals("Road's state should be ", correct, result);
        
        MultipleVehicleRoad.advance();
        result = MultipleVehicleRoad.generateReport(8);
        correct.setValue("time", "8");
        correct.setValue("state", "(v1,40),(v2,40),(v3,40),(v4,40)");
        assertEquals("Road's state should be ", correct, result);
    }
    
    @Test
    /**
     * Checks functionality of a road with faulty vehicles.
     */
    public void roadWithFaultyVehiclesTest(){
        Junction sourceJunction = new Junction("j1");
        Junction destinationJunction = new Junction("j2");
        Road roadWithFaultyV = new Road("r1", 20, 20, sourceJunction, destinationJunction);
        sourceJunction.addOutGoingRoad(roadWithFaultyV, destinationJunction);
        destinationJunction.addIncomingRoad(roadWithFaultyV);
        ArrayList<Junction> itinerary = new ArrayList<>();
        itinerary.add(sourceJunction);
        itinerary.add(destinationJunction);
        Vehicle v1 = new Vehicle("v1", 10, itinerary); 
        Vehicle v2 = new Vehicle("v2", 15, itinerary); 
        Vehicle v3 = new Vehicle("v3", 20, itinerary); 
        Vehicle v4 = new Vehicle("v4", 5, itinerary); 
        v3.makeFaulty(3);
        v2.makeFaulty(2);
        IniSection result = roadWithFaultyV.generateReport(0);
        IniSection correct = new IniSection("road_report");
        
        correct.setValue("time", "0");
        correct.setValue("id", "r1");
        correct.setValue("state", "(v1,0),(v2,0),(v3,0),(v4,0)");
        assertEquals("Road's state should be ", correct, result);
        
        roadWithFaultyV.advance();
        result = roadWithFaultyV.generateReport(1);
        correct.setValue("time", "1"); 
        correct.setValue("state", "(v1,6),(v4,3),(v2,0),(v3,0)");
        assertEquals("Road's state should be ", correct, result);
        
        roadWithFaultyV.advance();
        roadWithFaultyV.advance();
        roadWithFaultyV.advance();
        result = roadWithFaultyV.generateReport(4);
        correct.setValue("time", "4"); 
        correct.setValue("state", "(v1,20),(v4,18),(v2,12),(v3,6)");
        assertEquals("Road's state should be ", correct, result);
    } 
            
    
}
