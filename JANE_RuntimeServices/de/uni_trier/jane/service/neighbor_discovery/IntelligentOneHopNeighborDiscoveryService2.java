package de.uni_trier.jane.service.neighbor_discovery;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;

/**
 * @author Stefan Peters
 *
 */
public class IntelligentOneHopNeighborDiscoveryService2 extends
		GenericNeighborDiscoveryService {

	private OneHopNeighborDiscoveryIntelligentBehaviourInterface behaviourInterface;
	
	public IntelligentOneHopNeighborDiscoveryService2(ServiceID ownServiceID, ServiceID beaconingServiceID, boolean includeOwnDevice,boolean propagateOwnEvents) {
		super(ownServiceID, beaconingServiceID, includeOwnDevice, propagateOwnEvents);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.neighbor_discovery.GenericNeighborDiscoveryService#createBeaconingData()
	 */
	protected Data createBeaconingData() {
		// TODO Auto-generated method stub
		return behaviourInterface.createBeaconingData();
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.neighbor_discovery.GenericNeighborDiscoveryService#getDataID()
	 */
	protected DataID getDataID() {
		// TODO Auto-generated method stub
		return behaviourInterface.getDataID();
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.neighbor_discovery.GenericNeighborDiscoveryService#setNeighborData(de.uni_trier.jane.basetypes.Address, double, double, de.uni_trier.jane.service.beaconing.Data)
	 */
	protected void setNeighborData(Address address, double timestamp,
			double validityDelta, Data data) {
		// TODO Auto-generated method stub
		//Weitergereicht vom BeaconingService
		behaviourInterface.setNeighborData(address,timestamp,validityDelta,data);
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.neighbor_discovery.GenericNeighborDiscoveryService#updateNeighborData(de.uni_trier.jane.basetypes.Address, double, double, de.uni_trier.jane.service.beaconing.Data)
	 */
	protected void updateNeighborData(Address address, double timestamp,
			double validityDelta, Data data) {
		// TODO Auto-generated method stub
//		Weitergereicht vom BeaconingService
		behaviourInterface.updateNeighborData(address,timestamp,validityDelta,data);
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.neighbor_discovery.GenericNeighborDiscoveryService#removeNeighborData(de.uni_trier.jane.basetypes.Address)
	 */
	protected void removeNeighborData(Address address) {
		// TODO Auto-generated method stub
//		Weitergereicht vom BeaconingService
		behaviourInterface.removeNeighborData(address);
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.neighbor_discovery.GenericNeighborDiscoveryService#transmissionSend()
	 */
	protected void transmissionSend() {
		// TODO Auto-generated method stub
//		Weitergereicht vom BeaconingService
		behaviourInterface.transmissionSend();
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryService_sync#getGatewayNodes(de.uni_trier.jane.basetypes.Address)
	 */
	public Address[] getGatewayNodes(Address destination) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryService_sync#getNeighborNodes(de.uni_trier.jane.basetypes.Address)
	 */
	public Address[] getNeighborNodes(Address gateway) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.neighbor_discovery.GenericNeighborDiscoveryService#setNeighborData(de.uni_trier.jane.service.network.link_layer.LinkLayerInfo, double, double, de.uni_trier.jane.service.beaconing.Data)
	 */
	protected void setNeighborData(LinkLayerInfo linkLayerInfo, double timestamp, double validityDelta, Data data) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.neighbor_discovery.GenericNeighborDiscoveryService#updateNeighborData(de.uni_trier.jane.service.network.link_layer.LinkLayerInfo, double, double, de.uni_trier.jane.service.beaconing.Data)
	 */
	protected void updateNeighborData(LinkLayerInfo linkLayerInfo, double timestamp, double validityDelta, Data data) {
		// TODO Auto-generated method stub
		
	}

}
