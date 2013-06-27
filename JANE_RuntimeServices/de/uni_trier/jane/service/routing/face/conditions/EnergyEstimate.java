/*
 * @author Stefan Peters
 * Created on 31.03.2005
 */
package de.uni_trier.jane.service.routing.face.conditions;

import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;

/**
 * An implementatio of Estimate. It appricate the energy demand from the neighbor node to destination node, using a complex function
 * described in a paper I don't know. Something with energy aware routing.
 * @author Stefan Peters
 */
public class EnergyEstimate implements Estimate {
	
	
	private double alpha;
    private double t;
    private double c;
	

	/**
	 * Creates an EnergyEstimate object
	 * @param exponent the exponent a (should be >= 2)
     * @param threshold the predetection threshold t (should be > 0)
     * @param additional the additional power expenditure c (should be > 0)
	 */
	public EnergyEstimate(double exponent, double threshold, double additional) {
		this.alpha = exponent;
		this.t = threshold;
		this.c = additional;
	}
	
	
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.routing.planar_graph_explorer.Assessment#calculateAssessment(de.uni_trier.jane.basetypes.Position, de.uni_trier.jane.basetypes.Position, de.uni_trier.jane.basetypes.Position)
	 */
	public double calculateEstimate(Position current, Position destination,
			Position neighbor) {
		double r=current.distance(neighbor);
		double s=neighbor.distance(destination);
		return s*c*Math.pow((t*(alpha-1))/c,1/alpha) + s*t*Math.pow((t*(alpha-1))/c,(1-alpha)/alpha);
	}

}
