package de.uni_trier.jane.service.neighbor_discovery;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.beaconing.*;


/**
 * 
 * @author Stefan Peters
 *
 */
interface PropagationStateInterface {

	/**
	 * This methode decides whether to send normal beacon data, or a NullDataObject
	 * @param dataMap A Map of all beacon data send by NeighborDiscoveryService
	 * @param neighbors An array of all neighbors
	 * @return Returns true, if NeighborData should be propageted, false if not
	 */
	public abstract boolean propagateData(DataMap dataMap, Address[] neighbors);

}