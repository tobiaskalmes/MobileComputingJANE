package de.uni_trier.jane.service.neighbor_discovery;

import de.uni_trier.jane.basetypes.*;

/**
 * An interface describing the behaviour the IntelligentOneHopNeighborDiscoveryService
 * @author Stefan Peters
 *
 */
public interface OneHopNeighborDiscoveryIntelligentBehaviourInterface {
	
	
	/**
	 * 
	 * @return Returns the beacon data to propagate
	 */
	public Data createBeaconingData();

	/**
	 * 
	 * @return Returns the id of the data
	 */
	public DataID getDataID();

	/**
	 * This method is called after the BeaconingService send a signal saying a new neighbor is available
	 * @param address The address of the new neighbor
	 * @param timestamp The timestamp of the received beacon data
	 * @param validityDelta The validity data of the beacon data
	 * @param data The received beaconing data
	 */
	public void setNeighborData(Address address, double timestamp,double validityDelta, Data data);

	/**
	 * This method is called after the BeaconingService send a signal saying data of a neighbor has changed
	 * @param address The address of the neighbor
	 * @param timestamp The timestamp of the received beacon data
	 * @param validityDelta The validity data of the beacon data
	 * @param data The received beaconing data
	 */
	public void updateNeighborData(Address address, double timestamp,double validityDelta, Data data);

	/**
	 * This method is called after thne BeaconingService send a signla saying a node is not a neighbor anymore
	 * @param address The address of the node to remove from neighborlist
	 */
	public void removeNeighborData(Address address);

	/**
	 * 
	 * This method is called after the GenericNeighborDiscoveryService received the transmission send signal
	 * fom the BeaconingService
	 */
	public void transmissionSend();
}
