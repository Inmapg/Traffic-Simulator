package pr5.model;

import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import pr5.ini.IniSection;

public class VehicleTest {

    @Test
    /**
     * Checks the functionality of one vehicle.
     */
    public void oneRoadTest() {
        Junction sourceJunction = new Junction("j1");
        Junction destinationJunction = new Junction("j2");
        Road road = new Road("r1", 40, 20, sourceJunction, destinationJunction);
        sourceJunction.addOutGoingRoad(road, destinationJunction);
        destinationJunction.addIncomingRoad(road);
        ArrayList<Junction> itinerary = new ArrayList<>();
        itinerary.add(sourceJunction);
        itinerary.add(destinationJunction);
        Vehicle vehicle = new Vehicle("v1", 15, itinerary);
        IniSection result = vehicle.generateReport(0);
        IniSection correct = new IniSection("vehicle_report");

        correct.setValue("id", "v1");
        correct.setValue("time", "0");
        correct.setValue("speed", "0");
        correct.setValue("kilometrage", "0");
        correct.setValue("faulty", "0");
        correct.setValue("location", "(r1,0)");

        assertEquals("Vehicle's state should be ", correct, result);
        road.advance();
        result = vehicle.generateReport(1);

        correct.setValue("id", "v1");
        correct.setValue("time", "1");
        correct.setValue("speed", "15");
        correct.setValue("kilometrage", "15");
        correct.setValue("faulty", "0");
        correct.setValue("location", "(r1,15)");

        assertEquals("Vehicle's state should be ", correct, result);
    }

    @Test
    /**
     * Checks the functionality of multiple vehicles on a a road.
     */
    public void multipleVehicleTest() {
        Junction sourceJunction = new Junction("j1");
        Junction destinationJunction = new Junction("j2");
        Road road = new Road("r1", 40, 20, sourceJunction, destinationJunction);
        sourceJunction.addOutGoingRoad(road, destinationJunction);
        destinationJunction.addIncomingRoad(road);
        ArrayList<Junction> itinerary = new ArrayList<>();
        itinerary.add(sourceJunction);
        itinerary.add(destinationJunction);
        Vehicle v1 = new Vehicle("v1", 15, itinerary);
        Vehicle v2 = new Vehicle("v2", 20, itinerary);
        IniSection correct1 = new IniSection("vehicle_report");
        IniSection correct2 = new IniSection("vehicle_report");

        road.advance();
        IniSection result1 = v1.generateReport(1);
        IniSection result2 = v2.generateReport(1);

        correct1.setValue("id", "v1");
        correct1.setValue("time", "1");
        correct1.setValue("speed", "11");
        correct1.setValue("kilometrage", "11");
        correct1.setValue("faulty", "0");
        correct1.setValue("location", "(r1,11)");

        correct2.setValue("id", "v2");
        correct2.setValue("time", "1");
        correct2.setValue("speed", "11");
        correct2.setValue("kilometrage", "11");
        correct2.setValue("faulty", "0");
        correct2.setValue("location", "(r1,11)");

        assertEquals("Vehicle1's state should be ", correct1, result1);
        assertEquals("Vehicle2's state should be ", correct2, result2);
    }

    @Test
    /**
     * Checks if a vehicle breaks down properly.
     */
    public void vehicleFaultyTest() {
        Junction sourceJunction = new Junction("j1");
        Junction destinationJunction = new Junction("j2");
        ArrayList<Junction> itinerary = new ArrayList<>();
        itinerary.add(sourceJunction);
        itinerary.add(destinationJunction);
        Road road = new Road("r1", 30, 20, sourceJunction, destinationJunction);
        sourceJunction.addOutGoingRoad(road, destinationJunction);
        destinationJunction.addIncomingRoad(road);

        Vehicle vehicle = new Vehicle("v1", 10, itinerary);
        road.advance();
        IniSection result = vehicle.generateReport(1);
        IniSection correct = new IniSection("vehicle_report");
        correct.setValue("id", "v1");
        correct.setValue("time", "1");
        correct.setValue("speed", "10");
        correct.setValue("kilometrage", "10");
        correct.setValue("faulty", "0");
        correct.setValue("location", "(r1,10)");

        assertEquals("Vehicle's state should be ", correct, result);
        vehicle.makeFaulty(2);
        correct.setValue("time", "2");
        correct.setValue("speed", "0");
        correct.setValue("faulty", "2");
        result = vehicle.generateReport(2);
        assertEquals("Vehicle's state should be ", correct, result);

        road.advance();
        correct.setValue("time", "3");
        correct.setValue("speed", "0");
        correct.setValue("faulty", "1");
        result = vehicle.generateReport(3);
        assertEquals("Vehicle's state should be ", correct, result);

        road.advance();
        correct.setValue("time", "4");
        correct.setValue("faulty", "0");
        result = vehicle.generateReport(4);
        assertEquals("Vehicle's state should be ", correct, result);

        road.advance();
        result = vehicle.generateReport(5);
        correct.setValue("time", "5");
        correct.setValue("speed", "10");
        correct.setValue("kilometrage", "20");
        correct.setValue("location", "(r1,20)");
        assertEquals("Vehicle's state should be ", correct, result);
    }

    @Test
    /**
     * Checks the functionality of a vehicle moving to its next road on its
     * itinerary.
     */
    public void moveToNextRoadTest() {
        Junction sourceJunction = new Junction("j1");
        Junction middleJunction = new Junction("j2");
        Junction destinationJunction = new Junction("j3");
        ArrayList<Junction> itinerary = new ArrayList<>();
        Road r1 = new Road("r1", 20, 20, sourceJunction, middleJunction);
        Road r2 = new Road("r2", 40, 30, middleJunction, destinationJunction);

        sourceJunction.addOutGoingRoad(r1, middleJunction);
        middleJunction.addIncomingRoad(r1);
        middleJunction.addOutGoingRoad(r2, destinationJunction);
        destinationJunction.addIncomingRoad(r2);

        itinerary.add(sourceJunction);
        itinerary.add(middleJunction);
        itinerary.add(destinationJunction);

        Vehicle vehicle = new Vehicle("v1", 20, itinerary);
        r1.advance();
        IniSection result = vehicle.generateReport(1);
        IniSection correct = new IniSection("vehicle_report");
        correct.setValue("id", "v1");
        correct.setValue("time", "1");
        correct.setValue("speed", "0");
        correct.setValue("kilometrage", "20");
        correct.setValue("faulty", "0");
        correct.setValue("location", "(r1,20)");

        assertEquals("Vehicle's state should be ", correct, result);

        r2.advance();
        result = vehicle.generateReport(2);
        correct.setValue("time", "2");
        correct.setValue("speed", "0");
        correct.setValue("kilometrage", "20");
        correct.setValue("location", "(r1,20)");
        assertEquals("Vehicle's state should be ", correct, result);
    }

    @Test
    /**
     * Checks the functionality of a bike.
     */
    public void bikeTest() {
        Junction sourceJunction = new Junction("j1");
        Junction destinationJunction = new Junction("j2");
        Road road = new Road("r1", 40, 20, sourceJunction, destinationJunction);
        sourceJunction.addOutGoingRoad(road, destinationJunction);
        destinationJunction.addIncomingRoad(road);
        ArrayList<Junction> itinerary = new ArrayList<>();
        itinerary.add(sourceJunction);
        itinerary.add(destinationJunction);
        Bike bike = new Bike("b1", 10, itinerary);
        IniSection result = bike.generateReport(0);
        IniSection correct = new IniSection("vehicle_report");

        correct.setValue("id", "b1");
        correct.setValue("time", "0");
        correct.setValue("type", "bike");
        correct.setValue("speed", "0");
        correct.setValue("kilometrage", "0");
        correct.setValue("faulty", "0");
        correct.setValue("location", "(r1,0)");

        assertEquals("Bike's state should be ", correct, result);
        road.advance();
        result = bike.generateReport(1);

        correct.setValue("time", "1");
        correct.setValue("speed", "10");
        correct.setValue("kilometrage", "10");
        correct.setValue("location", "(r1,10)");

        assertEquals("Bike's state should be ", correct, result);
    }

    @Test
    /**
     * Checks the functionality of a car.
     */
    public void carTest() {
        Junction sourceJunction = new Junction("j1");
        Junction destinationJunction = new Junction("j2");
        Road road = new Road("r1", 70, 50, sourceJunction, destinationJunction);
        sourceJunction.addOutGoingRoad(road, destinationJunction);
        destinationJunction.addIncomingRoad(road);
        ArrayList<Junction> itinerary = new ArrayList<>();
        itinerary.add(sourceJunction);
        itinerary.add(destinationJunction);
        Car car = new Car("c1", 10, itinerary, 15, 1.0, 2, 0);

        IniSection result = car.generateReport(0);
        IniSection correct = new IniSection("vehicle_report");

        correct.setValue("id", "c1");
        correct.setValue("time", "0");
        correct.setValue("type", "car");
        correct.setValue("speed", "0");
        correct.setValue("kilometrage", "0");
        correct.setValue("faulty", "0");
        correct.setValue("location", "(r1,0)");
        assertEquals("Car's state should be ", correct, result);
        road.advance();

        correct.setValue("time", "1");
        correct.setValue("speed", "10");
        correct.setValue("kilometrage", "10");
        correct.setValue("location", "(r1,10)");
        result = car.generateReport(1);

        assertEquals("Car's state should be ", correct, result);
    }
}
