package de.uni_trier.jane.service.routing.multicast.spbm;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryData;
import de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryListener;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.LocationData;

/**
 * TODO: comment class
 * 
 * @author daniel
 */

class MyBeaconingListener implements NeighborDiscoveryListener {

	/**
	 * the PositionBasedMulticastRoutingAlgorithm this Listener belongs to
	 */
	private final PositionBasedMulticastRoutingAlgorithm algorithm;

	/**
	 * @param algorithm
	 */
	MyBeaconingListener(PositionBasedMulticastRoutingAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

	public void setNeighborData(NeighborDiscoveryData data) 
	{
		if (data.getDataMap().hasData(LocationData.DATA_ID)) {
			LocationData locationData = (LocationData) data.getDataMap().getData(LocationData.DATA_ID);
			if (data.getDataMap().hasData(LocationData.DATA_ID)) {
				MyBeaconingData myBeaconingData = (MyBeaconingData) data.getDataMap().getData(MyBeaconingData.DATA_ID);
				if (data.getSender() != algorithm.getAddress())
					algorithm.getNeighbors().put(data.getSender(), new MulticastNeighborEntry(locationData
							.getPosition(), myBeaconingData.getMyGroups()));
			}
		}
	}

	public void updateNeighborData(NeighborDiscoveryData data) {
		setNeighborData(data);
	}

	public void removeNeighborData(Address address) {
		algorithm.getNeighbors().remove(address);
	}
}