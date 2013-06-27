package de.uni_trier.jane.service.planarizer;

import java.io.Serializable;

import de.uni_trier.jane.basetypes.*;

/**
 * A network node contains the minimum information which should be available about
 * all one-hop (or two-hop) neighbor nodes. This interface will also be used to provide
 * information about distant nodes (e.g. the destination node).
 * 
 * @author Hannes Frey
 */
public interface NetworkNode extends Serializable {

	/**
	 * Get the address of this graph node.
	 * @return the node address
	 */
	public Address getAddress();

	/**
	 * Get the position of this graph node.
	 * @return the node position
	 */
	public Position getPosition();

	/**
	 * This method will always return true if one-hop neighbors are considered only. If also two-hop
	 * neighbors are considered, the method will return true only if the node is a one-hop neighbor.
	 * @return true if the node is a one-hop neighbor
	 */
	public boolean isOneHopNeighbor();
	
}
