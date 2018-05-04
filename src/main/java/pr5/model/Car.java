package pr5.model;

import java.util.List;
import java.util.Random;
import pr5.ini.IniSection;

/**
 * Defines car: a type of vehicle.
 *
 * @see Vehicle
 */
public class Car extends Vehicle {

    private static final String TYPE = "car";
    private final int resistance;
    /**
     * Kilometres passed since the last breakdown
     */
    private int kmSinceFaulty;
    private Random randomGenerator;
    /**
     * Probability of breakdown
     */
    private final double faultProbability;
    private final int maxFaultDuration;
    private final long seed; // just in case of reset

    /**
     * Class constructor specifying id, maximum speed, itinerary, resistance,
     * probability of breakdown, maximum fault duration and seed. kmSinceFaulty
     * is zero-initialized and randomGenerator is made from seed.
     *
     * @param id
     * @param maxSpeed
     * @param itinerary
     * @param resistance
     * @param faultProbability
     * @param maxFaultDuration
     * @param seed Seed for random number generator
     */
    public Car(String id, int maxSpeed, List<Junction> itinerary, int resistance,
            double faultProbability, int maxFaultDuration, long seed) {

        super(id, maxSpeed, itinerary);
        this.resistance = resistance;
        kmSinceFaulty = 0;
        this.faultProbability = faultProbability;
        this.maxFaultDuration = maxFaultDuration;
        this.seed = seed;
        randomGenerator = new Random(seed);
    }

    @Override
    protected void advance() {
        if (0 == faulty && kilometrage - kmSinceFaulty > resistance
                && randomGenerator.nextDouble() < faultProbability) {
            // shifting [1, maxFaultDuration]
            super.makeFaulty(randomGenerator.nextInt(maxFaultDuration) + 1);
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
