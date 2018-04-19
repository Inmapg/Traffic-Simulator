package pr5.model;

import java.util.List;
import java.util.Random;
import pr5.ini.IniSection;

/**Defines car: a type of vehicle.
 * 
 * @author Inmapg
 * @author Arturacu
 * @version 2.0
 * @see Vehicle
 */
public class Car extends Vehicle{
    /**Type of vehicle*/
    private static final String TYPE = "car";
    /**Resistance*/
    private final int resistance;
    /**Kilometres passed since the last breakdown*/
    private int kmSinceFaulty;
    /**Random number generator*/
    private Random randomGenerator;
    /**Probability of breakdown*/
    private final double faultProbability;
    /**Maximum fault duration*/
    private final int maxFaultDuration;
    /**Seed*/
    private final long seed; // just in case of reset
    
    /**Class constructor specifying id, maximum speed, itinerary, resistance, probability of breakdown,
     * maximum fault duration and seed.
     * kmSinceFaulty is zero-initialized and randomGenerator is made from seed.
     * 
     * @param id Identification
     * @param maxSpeed Maximum speed
     * @param itinerary Itinerary
     * @param resistance Resistance
     * @param faultProbability Probability of breaking down
     * @param maxFaultDuration Maximum fault duration
     * @param seed Seed for random number generator
     */
    public Car(String id, int maxSpeed, List<Junction> itinerary, int resistance, double faultProbability, int maxFaultDuration, 
            long seed) {
        super(id, maxSpeed, itinerary);
        this.resistance = resistance;
        kmSinceFaulty = 0;
        this.faultProbability = faultProbability;
        this.maxFaultDuration = maxFaultDuration;
        this.seed = seed;
        randomGenerator = new Random(seed);
    }
    
    @Override
    protected void advance(){
        if(0 == super.faulty && kilometrage - kmSinceFaulty > resistance && randomGenerator.nextDouble() < faultProbability){
            super.makeFaulty(randomGenerator.nextInt(maxFaultDuration)+1); // shifting [1, maxFaultDuration]
            kmSinceFaulty = kilometrage;
        }
        super.advance();
        
        
    }
    
    @Override
    protected void fillReportDetails(IniSection sec) {
        sec.setValue("type", TYPE);
        super.fillReportDetails(sec);
    }
}
