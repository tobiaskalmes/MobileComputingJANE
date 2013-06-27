/*****************************************************************************
* 
* TwoHopNeighborDiscoveryService.java
* 
* $Id: TwoHopNeighborDiscoveryService.java,v 1.1 2007/06/25 07:24:01 srothkugel Exp $
*
***********************************************************************
*  
* JANE - The Java Ad-hoc Network simulation and evaluation Environment
*
***********************************************************************
*
* Copyright (C) 2002-2006 
* Hannes Frey and Daniel Goergen and Johannes K. Lehnert
* Systemsoftware and Distrubuted Systems
* University of Trier 
* Germany
* http://syssoft.uni-trier.de/jane
* 
* This program is free software; you can redistribute it and/or 
* modify it under the terms of the GNU General Public License 
* as published by the Free Software Foundation; either version 2 
* of the License, or (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful, 
* but WITHOUT ANY WARRANTY; without even the implied warranty of 
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
* General Public License for more details.
* 
* You should have received a copy of the GNU General Public License 
* along with this program; if not, write to the Free Software 
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
* 
*****************************************************************************/
package de.uni_trier.jane.service.neighbor_discovery;

import java.util.HashSet;
import java.util.Iterator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.Service;
import de.uni_trier.jane.service.StackedClassID;
import de.uni_trier.jane.service.beaconing.BeaconingService_sync;
import de.uni_trier.jane.service.beaconing.DataMap;
import de.uni_trier.jane.service.beaconing.DefaultDataMap;
import de.uni_trier.jane.service.beaconing.GenericBeaconingService;
import de.uni_trier.jane.service.beaconing.RandomBeaconingService;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfoImplementation;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.simulation.parametrized.parameters.InitializationContext;
import de.uni_trier.jane.simulation.parametrized.parameters.Parameter;
import de.uni_trier.jane.simulation.parametrized.parameters.service.IdentifiedServiceElement;

/**
 * This service implements a neighbor discovery service which determines all devices which
 * can transmit a message to this device in one or two hops.
 */

public class TwoHopNeighborDiscoveryService extends GenericNeighborDiscoveryService {

	public static final IdentifiedServiceElement INITIALIZATION_ELEMENT = new IdentifiedServiceElement(
			"twoHopNeighborDiscovery",
			"This service implements a simple neighbor discovery service which determines all devices which "
					+ "can transmit a message to this device in one or two hop.") {
		public void createInstance(ServiceID ownServiceID,
				InitializationContext initializationContext,
				ServiceUnit serviceUnit) {
			ServiceID beaconingServiceID = GenericBeaconingService.REFERENCE
					.getInstance(serviceUnit, initializationContext);
			boolean includeOwnDevice = INCLUDE_OWN_DEVICE
					.getValue(initializationContext);
            boolean propagateEvents = USE_EVENTS
            .getValue(initializationContext);
			serviceUnit.addService(new TwoHopNeighborDiscoveryService(
					ownServiceID, beaconingServiceID, includeOwnDevice,propagateEvents));
		}

		public Parameter[] getParameters() {
			return new Parameter[] { GenericBeaconingService.REFERENCE,
					INCLUDE_OWN_DEVICE, USE_EVENTS };
		}
	};

	// intialized in constructor
    private Map neighborMap;
    
    /**
     * Create an instance of this service in the given service unit
     * @param serviceUnit the service unit
     * @param includeOwnDevice use this flag in order to determine if own data is stored as 0-hop information as well
     */
    public static void createInstance(ServiceUnit serviceUnit, boolean includeOwnDevice, boolean propagateEvents) {
    	if(!serviceUnit.hasService(BeaconingService_sync.class)) {
    		RandomBeaconingService.createInstance(serviceUnit);
    	}
    	ServiceID beaconingService = serviceUnit.getService(BeaconingService_sync.class);
    	ServiceID ownServiceID = new StackedClassID(OneHopNeighborDiscoveryService.class.getName(), beaconingService);
    	Service twoHopNeighborDiscoveryService = new TwoHopNeighborDiscoveryService(ownServiceID, beaconingService, includeOwnDevice,propagateEvents);
    	serviceUnit.addService(twoHopNeighborDiscoveryService);
    }

    /**
     * Construct a new discovery service.
     * @param dataID the unique identifier used to transmit the data of this service instance over the beaconing service
     */
    public TwoHopNeighborDiscoveryService(ServiceID ownServiceID, ServiceID beaconingService, boolean includeOwnDevice, boolean propagateEvents) {
    	super(ownServiceID, beaconingService, includeOwnDevice,propagateEvents);
    	
        neighborMap = new HashMap();

    }

    public void start(RuntimeOperatingSystem operatingSystem) {
    	super.start(operatingSystem);

        // this node is always in the neighbor map
        Neighbor neighbor = new Neighbor(getOwnAddress());
        neighbor.connect();
        neighborMap.put(getOwnAddress(), neighbor);

    }

	public Address[] getGatewayNodes(Address destination) {
		Neighbor neighbor = (Neighbor)neighborMap.get(destination);
		if(neighbor == null) {
			return null;
		}
		return neighbor.getGatewayNodes();
	}
	
	public Address[] getNeighborNodes(Address gateway) {
		Neighbor neighbor = (Neighbor)neighborMap.get(gateway);
		if(neighbor == null) {
			return null;
		}
		return neighbor.getNeighborNodes();
	}
    


	private void handleRemoveNeighbor(Address address) {

		// disconnect the neighbor and possibly remember it to be removed
		Set removeSet = new HashSet();
		Neighbor gateway = (Neighbor)neighborMap.get(address);
		gateway.disconnect();
		if(gateway.isRemovable()) {
			removeSet.add(address);
		}
		
		// remove this neighbor as gateway from all its neighbors and possibly remove these neighbors
		Iterator iterator = gateway.getNeighborSet().iterator();
		while (iterator.hasNext()) {
			Address neighborAddress = (Address) iterator.next();
			Neighbor neighbor = (Neighbor)neighborMap.get(neighborAddress);
			neighbor.removeGateway(address);
			if(neighbor.isRemovable()) {
				removeSet.add(neighborAddress);
			}
		}

		// remove neighbors and notify all listeners
		iterator = removeSet.iterator();
		while (iterator.hasNext()) {
			Address neighborAddress = (Address) iterator.next();
			removeNeighborDiscoveryData(neighborAddress);
//			neighborDiscoveryData.remove(neighborAddress);
			neighborMap.remove(neighborAddress); // TODO: wird der Nachbar immer entfernt???
//			notifyRemoved(neighborAddress);
		}
		if(!removeSet.isEmpty()) {
			notifyChanged();
		}
		
	}

    private void handleBeaconingData(LinkLayerInfo layerInfo, double timestamp, double validityDelta, Data data) {
        final Address sender=layerInfo.getSender();
        //TODO: calculate correct validity delta for two hop!
    	// get the beacon data of interest
    	TwoHopNeighborDiscoveryData discoveryData = (TwoHopNeighborDiscoveryData)data;
    	DataMap dataMap;
    	Map neighborDataMap;
    	Set neighborSet;
    	if(discoveryData == null) {
        	dataMap = new DefaultDataMap();
        	neighborDataMap = new HashMap();
    	}
    	else {
        	dataMap = discoveryData.getDataMap();
        	neighborDataMap = discoveryData.getNeighborDataMap();
    	}
    	neighborSet = new LinkedHashSet(neighborDataMap.keySet());

    	// create the sets used to remember all actions regarding the neighbor map
    	Set addedSet = new LinkedHashSet();
    	Set updatedSet = new LinkedHashSet();
    	Set removedSet = new LinkedHashSet();

    	// create or get the stored information about the one-hop neighbor
    	Neighbor neighbor = (Neighbor)neighborMap.get(sender);
		if(neighbor == null) {
			neighbor = new Neighbor(sender);
			neighborMap.put(sender, neighbor);
			addedSet.add(sender);
		}
		else {
			updatedSet.add(sender);
		}
		
    	// update data of the one-hop neighbor
		neighbor.connect();
    	Set removedFromSenderSet = neighbor.update(layerInfo,timestamp, validityDelta, dataMap, neighborSet);

    	// possibly create new two-hop neighbors and add beacon sender as gateway
    	Iterator iterator = neighborSet.iterator();
    	while(iterator.hasNext()) {
    		Address address = (Address)iterator.next();
    		DataMap map = (DataMap)neighborDataMap.get(address);
    		Neighbor senderNeighbor = (Neighbor)neighborMap.get(address);
			if(senderNeighbor == null) {
				senderNeighbor = new Neighbor(layerInfo, validityDelta /*???*/, timestamp, map);
				neighborMap.put(address, senderNeighbor);
				addedSet.add(address);
			}
			else {
				if(senderNeighbor.update(timestamp,validityDelta /*???*/, map)) {
					updatedSet.add(address);
				}
			}
			senderNeighbor.addGateway(sender);
    	}

    	// remove beacon sender as gateway and possibly remove neighbors
    	iterator = removedFromSenderSet.iterator();
    	while (iterator.hasNext()) {
			Address address = (Address) iterator.next();
			Neighbor senderNeighbor = (Neighbor)neighborMap.get(address);
			senderNeighbor.removeGateway(sender);
			if(senderNeighbor.isRemovable()) {
				neighborMap.remove(address);
			}
		}

    	// update neighbor discovery data and notify all neighbor discovery listeners
    	iterator = addedSet.iterator();
    	while (iterator.hasNext()) {
			Address address = (Address) iterator.next();
			Neighbor senderNeighbor = (Neighbor)neighborMap.get(address);
			NeighborDiscoveryData neighborData = senderNeighbor.getNeighborData();
			setNeighborDiscoveryData(neighborData);
		}
    	iterator = updatedSet.iterator();
    	while (iterator.hasNext()) {
			Address address = (Address) iterator.next();
			Neighbor senderNeighbor = (Neighbor)neighborMap.get(address);
			NeighborDiscoveryData neighborData = senderNeighbor.getNeighborData();
			updateNeighborDiscoveryData(neighborData);
		}
    	iterator = removedSet.iterator();
    	while (iterator.hasNext()) {
			Address address = (Address) iterator.next();
			removeNeighborDiscoveryData(address);
		}

    	// notify all listeners about the complete neighbor discovery data
    	notifyChanged();
    	
    }

    private static class TwoHopNeighborDiscoveryData implements Data {

		private static final long serialVersionUID = -5241047309426247531L;

		public static final DataID DATA_ID = new ClassDataID(TwoHopNeighborDiscoveryData.class);

        private DataMap dataMap;
        private Map neighborDataMap;
        private int size;

        public TwoHopNeighborDiscoveryData(DataMap dataMap, Map neighborDataMap) {
            this.dataMap = dataMap;
            this.neighborDataMap = neighborDataMap;
            size = dataMap.getSize();
            Iterator iterator = neighborDataMap.keySet().iterator();
            while (iterator.hasNext()) {
				Address address = (Address) iterator.next();
				DataMap map = (DataMap)neighborDataMap.get(address);
				size += address.getCodingSize() + map.getSize();
			}
        }

		public DataMap getDataMap() {
			return dataMap;
		}

		public Map getNeighborDataMap() {
			return neighborDataMap;
		}

		public Data copy() {
            return this;
        }
		
        public int getSize() {
            return size;
        }

        public DataID getDataID() {
            return DATA_ID;
        }
        
    }

	private static class Neighbor {

    	
    	private double timestamp;
    	private DataMap dataMap;
    	private boolean connected;
    	private Set neighborSet;
    	private Set gatewaySet;
    	
    	private boolean neighborSetChanged;
        private double valitDelta;
        private LinkLayerInfo linkLayerInfo;
        
        /**
         * 
         * Constructor for class <code>Neighbor</code>
         *
         * @param address
         */
		public Neighbor(Address address) {
			this(new LinkLayerInfoImplementation(address,null,false,Double.MIN_VALUE), Double.NaN, Double.NaN, null);
		}

        /**
         * 
         * Constructor for class <code>Neighbor</code>
         *
         * @param linkLayerInfo
         * @param timestamp
         * @param valitDelta
         * @param dataMap
         */
		public Neighbor(LinkLayerInfo linkLayerInfo, double timestamp,double valitDelta, DataMap dataMap) {
			this.linkLayerInfo=linkLayerInfo;
			this.timestamp = timestamp;
            this.valitDelta=valitDelta;
			this.dataMap = dataMap;
			connected = false;
			neighborSet = new LinkedHashSet();
			gatewaySet = new LinkedHashSet();
			neighborSetChanged = false;
		}

		public Address[] getGatewayNodes() {
			if(connected) {
				return new Address[] { linkLayerInfo.getSender()};
			}
			return (Address[])gatewaySet.toArray(new Address[gatewaySet.size()]);
		}

		public Address[] getNeighborNodes() {
			if(connected) {
				return (Address[])neighborSet.toArray(new Address[neighborSet.size()]);
			}
			return null;
		}

		public void connect() {
			connected = true;
		}

		public void disconnect() {
			connected = false;
			neighborSetChanged = !neighborSet.isEmpty();
			neighborSet.clear();
		}

		public boolean update( double timestamp, double valitDelta, DataMap dataMap) {
			if(!connected) {
                this.valitDelta=valitDelta;
				this.timestamp = timestamp;
				this.dataMap = dataMap;
				return true;
			}
			return false;
		}

		public Set update(LinkLayerInfo info,double timestamp, double valitDelta, DataMap dataMap, Set neighborSet) {
			
            if (info.getSender().equals(linkLayerInfo.getSender())){
                throw new IllegalStateException("Given linklayer info is not from the same device");
            }
            linkLayerInfo=info;
			neighborSetChanged = !this.neighborSet.containsAll(neighborSet) || !neighborSet.containsAll(this.neighborSet);

			this.valitDelta=valitDelta;
            
			this.timestamp = timestamp;
			this.dataMap = dataMap;
			this.neighborSet.removeAll(neighborSet);
			Set result = this.neighborSet;
			this.neighborSet = neighborSet;
			
			
			return result;
		}

		public void addGateway(Address gateway) {
			gatewaySet.add(gateway);
		}

		public void removeGateway(Address gateway) {
			gatewaySet.remove(gateway);
		}

		public boolean isRemovable() {
			return !connected && gatewaySet.isEmpty();
		}

		public NeighborDiscoveryData getNeighborData() {
			int hopDistance = connected ? 1 : 2;
			return new NeighborDiscoveryData(linkLayerInfo, timestamp,valitDelta, dataMap//.copy()
					, hopDistance, neighborSetChanged);
		}

		public Set getNeighborSet() {
			return neighborSet;
		}

    }

	protected Data createBeaconingData() {

    	Map neighborDataMap = new HashMap();
    	NeighborDiscoveryData[] neighborDatas = getNeighborDiscoveryData();
    	int n = neighborDatas.length;

    	for(int i=0; i<n; i++) {
    		NeighborDiscoveryData neighborData = neighborDatas[i];
    		if(neighborData.getHopDistance() == 1) {
    			Address address = neighborData.getSender();
				DataMap dataMap = neighborData.getDataMap();//.copy();
    			neighborDataMap.put(address, dataMap);
    		}
    	}

    	DataMap dataMap = getOwnNeighborDiscoveryData().getDataMap();
    	TwoHopNeighborDiscoveryData data = new TwoHopNeighborDiscoveryData(dataMap//.copy()
    				, neighborDataMap);
    	return data;

	}

	protected DataID getDataID() {
		return TwoHopNeighborDiscoveryData.DATA_ID;
	}

	protected void setNeighborData(LinkLayerInfo layerInfo, double timestamp, double validityDelta, Data data) {
		handleBeaconingData(layerInfo, timestamp, validityDelta, data);
	}

	protected void updateNeighborData(LinkLayerInfo layerInfo, double timestamp, double validityDelta, Data data) {
		handleBeaconingData(layerInfo, timestamp, validityDelta, data);
	}

	protected void removeNeighborData(Address address) {
		handleRemoveNeighbor(address);
	}

	protected void transmissionSend() {
		// ignore
		
	}

//	/* (non-Javadoc)
//	 * @see de.uni_trier.jane.service.neighbor_discovery.GenericNeighborDiscoveryService#setNeighborData(de.uni_trier.jane.service.network.link_layer.LinkLayerInfo, double, double, de.uni_trier.jane.service.beaconing.Data)
//	 */
//	protected void setNeighborData(LinkLayerInfo linkLayerInfo, double timestamp, double validityDelta, Data data) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	/* (non-Javadoc)
//	 * @see de.uni_trier.jane.service.neighbor_discovery.GenericNeighborDiscoveryService#updateNeighborData(de.uni_trier.jane.service.network.link_layer.LinkLayerInfo, double, double, de.uni_trier.jane.service.beaconing.Data)
//	 */
//	protected void updateNeighborData(LinkLayerInfo linkLayerInfo, double timestamp, double validityDelta, Data data) {
//		// TODO Auto-generated method stub
//		
//	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.neighbor_discovery.GenericNeighborDiscoveryService#setNeighborData(de.uni_trier.jane.service.network.link_layer.LinkLayerInfo, double, double, de.uni_trier.jane.service.beaconing.Data)
	 */
//	protected void setNeighborData(LinkLayerInfo linkLayerInfo, double timestamp, double validityDelta, Data data) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	/* (non-Javadoc)
//	 * @see de.uni_trier.jane.service.neighbor_discovery.GenericNeighborDiscoveryService#updateNeighborData(de.uni_trier.jane.service.network.link_layer.LinkLayerInfo, double, double, de.uni_trier.jane.service.beaconing.Data)
//	 */
//	protected void updateNeighborData(LinkLayerInfo linkLayerInfo, double timestamp, double validityDelta, Data data) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	/* (non-Javadoc)
//	 * @see de.uni_trier.jane.service.neighbor_discovery.GenericNeighborDiscoveryService#setNeighborData(de.uni_trier.jane.service.network.link_layer.LinkLayerInfo, double, double, de.uni_trier.jane.service.beaconing.Data)
//	 */
//	protected void setNeighborData(LinkLayerInfo linkLayerInfo, double timestamp, double validityDelta, Data data) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	/* (non-Javadoc)
//	 * @see de.uni_trier.jane.service.neighbor_discovery.GenericNeighborDiscoveryService#updateNeighborData(de.uni_trier.jane.service.network.link_layer.LinkLayerInfo, double, double, de.uni_trier.jane.service.beaconing.Data)
//	 */
//	protected void updateNeighborData(LinkLayerInfo linkLayerInfo, double timestamp, double validityDelta, Data data) {
//		// TODO Auto-generated method stub
//		
//	}

}
