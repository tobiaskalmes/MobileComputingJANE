package de.uni_trier.jane.service.routing.dijkstra;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.simulation.global_knowledge.*;


/**
 * This class implements an energy aware metric according to a general power consumption
 * model. The energy used to transmit between two nodes with distance d amounts t*d^a+c.
 * t is termed predetection threshold, a is an exponent >=2, and c is an additional distant
 * independent power constant expenditure > 0. The weight between two nodes which are not
 * connected is set to infinity.
 */
// TODO: EnergyMetric Object verwenden
public class EnergyWeightFunction implements WeightFunction {

    private double exponent;
    private double threshold;
    private double additional;
    
    /**
     * Construct a new power metric object.
     * @param exponent the exponent a (should be >= 2)
     * @param threshold the predetection threshold t (should be > 0)
     * @param additional the additional power expenditure c (should be > 0)
     */
    public EnergyWeightFunction(double exponent, double threshold, double additional) {
        this.exponent = exponent;
        this.threshold = threshold;
        this.additional = additional;
    }

	public double getWeight(DeviceID source, DeviceID destination, GlobalKnowledge globalKnowledge) {
		Position position1 = globalKnowledge.getTrajectory(source).getPosition();
		Position position2 = globalKnowledge.getTrajectory(destination).getPosition();
		double distance = position1.distance(position2);
		if(globalKnowledge.isConnected(source, destination)) {
		    return threshold * Math.pow(distance, exponent) + additional;
		}
		return Double.POSITIVE_INFINITY;
	}

	public String toString() {
		return "Energy(TODO: parameters!!!)";
	}

}
