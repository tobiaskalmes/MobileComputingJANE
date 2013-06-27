/*****************************************************************************
* 
* $Id: OneHopNeighborDiscoveryService.java,v 1.1 2007/06/25 07:24:01 srothkugel Exp $
*  
***********************************************************************
*  
* JANE - The Java Ad-hoc Network simulation and evaluation Environment
*
***********************************************************************
*
* Copyright (C) 2002-2006
* Hannes Frey and Daniel Goergen and Johannes K. Lehnert
* Systemsoftware and Distributed Systems
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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.Service;
import de.uni_trier.jane.service.StackedClassID;
import de.uni_trier.jane.service.beaconing.BeaconingService_sync;
import de.uni_trier.jane.service.beaconing.DataMap;
import de.uni_trier.jane.service.beaconing.DataMapper;
import de.uni_trier.jane.service.beaconing.DataSerializer;
import de.uni_trier.jane.service.beaconing.DefaultDataMap;
import de.uni_trier.jane.service.beaconing.GenericBeaconingService;
import de.uni_trier.jane.service.beaconing.RandomBeaconingService;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.simulation.parametrized.parameters.InitializationContext;
import de.uni_trier.jane.simulation.parametrized.parameters.Parameter;
import de.uni_trier.jane.simulation.parametrized.parameters.service.IdentifiedServiceElement;

/**
 * This service implements a simple neighbor discovery service which determines all devices which
 * can transmit a message to this device in one hop.
 */
public class OneHopNeighborDiscoveryService extends GenericNeighborDiscoveryService {

	public static final IdentifiedServiceElement INITIALIZATION_ELEMENT = new IdentifiedServiceElement("oneHopNeighborDiscovery", "This service implements a simple neighbor discovery service which determines all devices which can transmit a message to this device in one hop.") {
		public void createInstance(ServiceID ownServiceID, InitializationContext initializationContext, ServiceUnit serviceUnit) {
			ServiceID beaconingServiceID = GenericBeaconingService.REFERENCE.getInstance(serviceUnit, initializationContext);
			boolean includeOwnDevice = INCLUDE_OWN_DEVICE.getValue(initializationContext);
            boolean includeEvents = USE_EVENTS.getValue(initializationContext);
			serviceUnit.addService(new OneHopNeighborDiscoveryService(ownServiceID, beaconingServiceID, includeOwnDevice,includeEvents));
		}
		public Parameter[] getParameters() {
			return new Parameter[] { GenericBeaconingService.REFERENCE, INCLUDE_OWN_DEVICE, USE_EVENTS };
		}
	};

	
    /**
     * Create an instance of this service in the given service unit
     * @param serviceUnit the service unit
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit) {
    	return createInstance(serviceUnit, true,false);
    }

    public static ServiceID createInstance(ServiceUnit serviceUnit, boolean includeOwnDevice){
        return createInstance(serviceUnit,includeOwnDevice,false);
    }
    /**
     * Create an instance of this service in the given service unit
     * @param serviceUnit the service unit
     * @param includeOwnDevice use this flag in order to determine if own data is stored as 0-hop information as well
     * @param propagateEvents if true, the Service also generates <code>NeighborDiscoveryDataEvent</code>s 
     * @return  the ID of the Service
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit, boolean includeOwnDevice, boolean propagateEvents) {
    	if(!serviceUnit.hasService(BeaconingService_sync.class)) {
    		RandomBeaconingService.createInstance(serviceUnit);
    	}
    	ServiceID beaconingService = serviceUnit.getService(BeaconingService_sync.class);
    	ServiceID ownServiceID = new StackedClassID(OneHopNeighborDiscoveryService.class.getName(), beaconingService);
    	Service oneHopNeighborDiscoveryService = new OneHopNeighborDiscoveryService(ownServiceID, beaconingService, includeOwnDevice,propagateEvents);
    	return serviceUnit.addService(oneHopNeighborDiscoveryService);
    }

    /**
     * 
     * Constructor for class <code>OneHopNeighborDiscoveryService</code>
     *
     * @param beaconingServiceID
     * @param includeOwnDevice
     */
    public OneHopNeighborDiscoveryService(ServiceID beaconingServiceID, boolean includeOwnDevice, boolean propagateEvents) {
    	this(new StackedClassID(OneHopNeighborDiscoveryService.class.getName(), beaconingServiceID), 
                beaconingServiceID, includeOwnDevice,propagateEvents);
    }
    
    /**
     * Construct a new discovery service.
     * @param ownServiceID 
     * @param beaconingServiceID the beaconing service used to send own data to all one hop neighbors
     * @param includeOwnDevice this flag determines if own data is stored as 0-hop information as well
     */
    public OneHopNeighborDiscoveryService(ServiceID ownServiceID, ServiceID beaconingServiceID, boolean includeOwnDevice, boolean propagateEvents) {
    	super(ownServiceID, beaconingServiceID, includeOwnDevice,propagateEvents);
    }

	public Address[] getGatewayNodes(Address destination) {
		if(getOwnAddress().equals(destination) || neighborDiscoveryDataMap.containsKey(destination)) {
			return new Address[] { destination }; 
		}
		return null;
	}

	public Address[] getNeighborNodes(Address gateway) {
		if(getOwnAddress().equals(gateway)) {
			return getNeighbors();
		}
		return null;
	}

    protected void setNeighborData(LinkLayerInfo linkLayerInfo, double timestamp, double validityDelta, Data data) {
    	NeighborDiscoveryData neighborDiscoveryData = createNeighborDiscoveryData(linkLayerInfo, timestamp, validityDelta, data);
    	setNeighborDiscoveryData(neighborDiscoveryData);
    }
    
    protected void updateNeighborData(LinkLayerInfo linkLayerInfo, double timestamp, double validityDelta, Data data) {
    	NeighborDiscoveryData neighborDiscoveryData = createNeighborDiscoveryData(linkLayerInfo, timestamp, validityDelta, data);
    	updateNeighborDiscoveryData(neighborDiscoveryData);
    }

    protected void removeNeighborData(Address address) {
    	removeNeighborDiscoveryData(address);
    }
    
    private NeighborDiscoveryData createNeighborDiscoveryData(LinkLayerInfo linkLayerInfo, double timestamp, double validityDelta, Data data) {

    	// get the discovery data
    	OneHopNeighborDiscoveryData discoveryData = (OneHopNeighborDiscoveryData)data;

    	// create store and return the new one-hop neighbor data
    	DataMap neighborDataMap;
		if(discoveryData != null) {
			neighborDataMap = discoveryData.getDataMap().copy();
		}
		else {
			neighborDataMap = new DefaultDataMap();
		}
		NeighborDiscoveryData neighborData = new NeighborDiscoveryData(linkLayerInfo, timestamp,validityDelta, neighborDataMap, 1, false);
		return neighborData;

    }

    /**
     * 
     * @author goergen
     *
     * TODO comment class
     */
    public static class OneHopNeighborDiscoveryData implements Data {

		private static final long serialVersionUID = 0L;

		public static final DataID DATA_ID = new ClassDataID(OneHopNeighborDiscoveryData.class);
		static{
            
		    map();
        }

        /**
         * TODO Comment method
         */
        public static void map() {
            DataMapper.map(OneHopNeighborDiscoveryData.class,serialVersionUID,new DataSerializer() {
            
                public Object readData(ObjectInputStream in) throws IOException, ClassNotFoundException {
                    
                    return new OneHopNeighborDiscoveryData(DefaultDataMap.read(in));
                }
            
                public void write(Object data, ObjectOutputStream out) throws IOException {
                    OneHopNeighborDiscoveryData nd=(OneHopNeighborDiscoveryData)data;
                    ((DefaultDataMap)nd.dataMap).write(out);
            
                }
            
            });
        }
        private DataMap dataMap;

        public OneHopNeighborDiscoveryData(DataMap dataMap) {
            this.dataMap = dataMap;
        }

		public DataMap getDataMap() {
			return dataMap;
		}

//		public Data copy() {
//            return new OneHopNeighborDiscoveryData(dataMap.copy());
//        }

        public int getSize() {
            return dataMap.getSize();
        }

        public DataID getDataID() {
            return DATA_ID;
        }
        
    }

	protected Data createBeaconingData() {
		NeighborDiscoveryData data = getOwnNeighborDiscoveryData();
		DataMap dataMap = data.getDataMap();
		return new OneHopNeighborDiscoveryData(dataMap);
	}

	protected DataID getDataID() {
		return OneHopNeighborDiscoveryData.DATA_ID;
	}

	protected void transmissionSend() {
		//ignore
		
	}

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

}
