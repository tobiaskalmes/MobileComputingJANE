package de.uni_trier.jane.service.routing.greedy.metric;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;

/**
 * Calcultes the energy consumption to a neighbor and estimates the energy consumption to destination.
 * @author Stefan Peters
 *
 */
public class EnergyMetric implements LocalRoutingMetric {

    private double exponent;
    private double threshold;
    private double additional;
    private Estimate estimate;

    /**
     * Construct a new energy metric object.
     * @param exponent the exponent a (should be >= 2)
     * @param threshold the predetection threshold t (should be > 0)
     * @param additional the additional power expenditure c (should be > 0)
     * @param estimate An estimate of further power consumption till destination
     */
    public EnergyMetric(double exponent, double threshold, double additional, Estimate estimate) {
        this.exponent = exponent;
        this.threshold = threshold;
        this.additional = additional;
        this.estimate=estimate;
    }

	public double calculate(Position sender,Position destination, Position receiver) {
		double distance = sender.distance(receiver);
		return threshold * Math.pow(distance, exponent) + additional + estimate.calculateEstimate(sender,destination,receiver);
	}
	
	public String toString() {
		return "ENERGY(TODO: parameters missing!)";
	}

}
