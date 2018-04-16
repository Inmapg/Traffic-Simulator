package pr5.tmodel;

import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import pr5.ini.IniSection;
import pr5.model.Junction;
import pr5.model.Road;
import pr5.model.Vehicle;

public class JunctionTest {
     @Test
    public void SimpleJunctionTest(){
        Junction sourceJunction = new Junction("j1");
        Junction destinationJunction = new Junction("j2");
        Road road = new Road("r1", 40, 20, sourceJunction, destinationJunction);
        sourceJunction.addOutGoingRoad(road, destinationJunction);
        destinationJunction.addIncomingRoad(road);
        ArrayList<Junction> itinerary = new ArrayList<>();
        itinerary.add(sourceJunction);
        itinerary.add(destinationJunction);
        Vehicle vehicle = new Vehicle("v1", 15, itinerary); 
        IniSection result = sourceJunction.generateReport(0);
        IniSection correct = new IniSection("junction_report");
        correct.setValue("id", "j1");
        correct.setValue("time", "0");
        correct.setValue("queues", "");
        assertEquals(correct, result);
        assertEquals(road, sourceJunction.roadTo(destinationJunction));
        
    }
    
    @Test
    public void SimpleJunctionSimpleIncomingRoadQueueTest(){
        Junction sourceJunction = new Junction("j1");
        Junction destinationJunction = new Junction("j2");
        Road road = new Road("r1", 40, 20, sourceJunction, destinationJunction);
        sourceJunction.addOutGoingRoad(road, destinationJunction);
        destinationJunction.addIncomingRoad(road);
        ArrayList<Junction> itinerary = new ArrayList<>();
        itinerary.add(sourceJunction);
        itinerary.add(destinationJunction);
        destinationJunction.enter(new Vehicle("v1", 15, itinerary));
        destinationJunction.enter(new Vehicle("v2", 15, itinerary));
        destinationJunction.enter(new Vehicle("v3", 15, itinerary));
        destinationJunction.enter(new Vehicle("v4", 15, itinerary));
        IniSection result = destinationJunction.generateReport(0);
        IniSection correct = new IniSection("junction_report");        
        correct.setValue("id", "j2");
        correct.setValue("time", "0");
        correct.setValue("queues", "(r1,red,[v1,v2,v3,v4])");
        assertEquals(correct, result);        
    }
    
}
