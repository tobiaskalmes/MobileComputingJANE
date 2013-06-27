/*
 * @author Stefan Peters
 * Created on 30.03.2005
 */
package de.uni_trier.jane.service.routing.greedy.metric;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;

/**
 * @author Stefan Peters
 */
public class GreedyEnergieMetric implements LocalRoutingMetric {

	private double exponent;

	private double threshold;

	private double additional;
	
	private Estimate estimate;

	/**
	 * @param exponent
	 * @param threshold
	 * @param additional
	 */
	public GreedyEnergieMetric(double exponent, double threshold,
			double additional, Estimate estimate) {
		this.exponent = exponent;
		this.threshold = threshold;
		this.additional = additional;
		this.estimate=estimate;
	}

	public double calculate(Position sender, Position destination,
			Position receiver) {
		double distance = receiver.distance(destination);
		if (distance < sender.distance(destination)) {
			return threshold * Math.pow(distance, exponent) + additional + estimate.calculateEstimate(sender,destination,receiver);
		}
		return Double.POSITIVE_INFINITY;
	}

	public String toString() {
		return "ENERGY(TODO:parameters missing)";
	}

}