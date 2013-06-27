package de.uni_trier.jane.service.routing.greedy.metric;

import java.io.Serializable;

import de.uni_trier.jane.basetypes.*;

/**
 * This interface describes a localized routing metric used by greedy next hop
 * selection methods.
 * 
 * @author Hannes Frey
 */
public interface LocalRoutingMetric extends Serializable{

	/**
	 * Calculate the weight for the selected receiver node
	 * 
	 * @param sender
	 *            the current node doing the routing decision
	 * @param destination
	 *            the final destination node
	 * @param receiver
	 *            the next hop candidate node
	 * @return the weight for this node
	 */
	public abstract double calculate(Position sender, Position destination,
			Position receiver);
}