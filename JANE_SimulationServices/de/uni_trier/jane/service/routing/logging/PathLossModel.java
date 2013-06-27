package de.uni_trier.jane.service.routing.logging;

import de.uni_trier.jane.basetypes.*;

/**
 * This model calcultes the energy consumption needed for sending a unicast message
 * to a to a neighbor by using the path loss formula t * d^a + c.
 * 
 * @author Hannes Frey
 */
public class PathLossModel implements EnergyModel {

    private double exponent;
    private double threshold;
    private double additional;

    /**
     * Construct a new energy model object.
     * @param exponent the exponent a (should be >= 2)
     * @param threshold the predetection threshold t (should be > 0)
     * @param additional the additional power expenditure c (should be > 0)
     */
    public PathLossModel(double exponent, double threshold, double additional) {
        this.exponent = exponent;
        this.threshold = threshold;
        this.additional = additional;
    }

	public String toString() {
		return "PathLossModel(" + threshold + " * d^" + exponent + " + " + additional + ")";
	}

	public double calculate(Position sender, Position receiver) {
		double distance = sender.distance(receiver);
		return threshold * Math.pow(distance, exponent) + additional;
	}

}
