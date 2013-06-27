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
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.simulation.parametrized.parameters.InitializationContext;
import de.uni_trier.jane.simulation.parametrized.parameters.Parameter;
import de.uni_trier.jane.simulation.parametrized.parameters.service.IdentifiedServiceElement;

/**
 * This service implements a simple neighbor discovery service which determines all devices which
 * can transmit a message to this device in one hop. It uses the IntelligentGenericNeighborDiscoveryService to create its neighbor knowledge.
 */
public class IntelligentOneHopNeighborDiscoveryService extends IntelligentGenericNeighborDiscoveryService {

	private PropagationStateInterface state;
	
	public static final IdentifiedServiceElement INITIALIZATION_ELEMENT = new IdentifiedServiceElement("oneHopNeighborDiscovery", "This service implements a simple neighbor discovery service which determines all devices which can transmit a message to this device in one hop.") {
		public void createInstance(ServiceID ownServiceID, InitializationContext initializationContext, ServiceUnit serviceUnit) {
			ServiceID beaconingServiceID = GenericBeaconingService.REFERENCE.getInstance(serviceUnit, initializationContext);
			boolean includeOwnDevice = INCLUDE_OWN_DEVICE.getValue(initializationContext);
			serviceUnit.addService(new IntelligentOneHopNeighborDiscoveryService(ownServiceID, beaconingServiceID, includeOwnDevice));
		}
		public Parameter[] getParameters() {
			return new Parameter[] { GenericBeaconingService.REFERENCE, INCLUDE_OWN_DEVICE };
		}
	};

	
    /**
     * Create an instance of this service in the given service unit
     * @param serviceUnit the service unit
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit) {
    	return createInstance(serviceUnit, true);
    }

    /**
     * Create an instance of this service in the given service unit
     * @param serviceUnit the service unit
     * @param includeOwnDevice use this flag in order to determine if own data is stored as 0-hop information as well
     * @return 
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit, boolean includeOwnDevice) {
    	if(!serviceUnit.hasService(BeaconingService_sync.class)) {
    		RandomBeaconingService.createInstance(serviceUnit);
    	}
    	ServiceID beaconingService = serviceUnit.getService(BeaconingService_sync.class);
    	ServiceID ownServiceID = new StackedClassID(IntelligentOneHopNeighborDiscoveryService.class.getName(), beaconingService);
    	Service oneHopNeighborDiscoveryService = new IntelligentOneHopNeighborDiscoveryService(ownServiceID, beaconingService, includeOwnDevice);
    	return serviceUnit.addService(oneHopNeighborDiscoveryService);
    }

    /**
     * Create an instance of this service in the given service unit
     * @param serviceUnit the service unit
     * @param includeOwnDevice use this flag in order to determine if own data is stored as 0-hop information as well
     * @param propagateCount An int value for deciding how many times neighbor information should be propagated after a change of the data set.
     * @return 
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit, boolean includeOwnDevice,PropagationStateInterface propagateState) {
    	if(!serviceUnit.hasService(BeaconingService_sync.class)) {
    		RandomBeaconingService.createInstance(serviceUnit);
    	}
    	ServiceID beaconingService = serviceUnit.getService(BeaconingService_sync.class);
    	ServiceID ownServiceID = new StackedClassID(IntelligentOneHopNeighborDiscoveryService.class.getName(), beaconingService);
    	Service oneHopNeighborDiscoveryService = new IntelligentOneHopNeighborDiscoveryService(ownServiceID, beaconingService, includeOwnDevice,propagateState);
    	return serviceUnit.addService(oneHopNeighborDiscoveryService);
    }
    
    public IntelligentOneHopNeighborDiscoveryService(ServiceID beaconingServiceID, boolean includeOwnDevice) {
    	this(new StackedClassID(IntelligentOneHopNeighborDiscoveryService.class.getName(), beaconingServiceID), beaconingServiceID, includeOwnDevice);
    }
    
    /**
     * Construct a new discovery service.
     * @param linkLayerServiceID the link layer service used to determine the own link layer address
     * @param beaconingServiceID the beaconing service used to send own data to all one hop neighbors
     * @param includeOwnDevice this flag determines if own data is stored as 0-hop information as well
     */
    public IntelligentOneHopNeighborDiscoveryService(ServiceID ownServiceID, ServiceID beaconingServiceID, boolean includeOwnDevice) {
    	super(ownServiceID, beaconingServiceID, includeOwnDevice,false);
    	state=new NormalPropagation(); // Default Value
    }
    /**
     * Construct a new discovery service.
     * @param linkLayerServiceID the link layer service used to determine the own link layer address
     * @param beaconingServiceID the beaconing service used to send own data to all one hop neighbors
     * @param includeOwnDevice this flag determines if own data is stored as 0-hop information as well
     * @param propagateCount An int value to set the number of propagation after data changed
     */
    public IntelligentOneHopNeighborDiscoveryService(ServiceID ownServiceID, ServiceID beaconingServiceID, boolean includeOwnDevice,PropagationStateInterface propagateState) {
    	super(ownServiceID, beaconingServiceID, includeOwnDevice,false);
    	state=propagateState;
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

    protected void setNeighborData(LinkLayerInfo linkLayerInfo, double timestamp,  double valityDelta, Data data) {
    	NeighborDiscoveryData neighborDiscoveryData = createNeighborDiscoveryData(linkLayerInfo, timestamp, valityDelta, data);
    	setNeighborDiscoveryData(neighborDiscoveryData);
    }
    
    protected void updateNeighborData(LinkLayerInfo linkLayerInfo, double timestamp, double valityDelta, Data data) {
    	NeighborDiscoveryData neighborDiscoveryData = createNeighborDiscoveryData(linkLayerInfo, timestamp, valityDelta, data);
    	updateNeighborDiscoveryData(neighborDiscoveryData);
    }

    protected void removeNeighborData(Address address) {
    	removeNeighborDiscoveryData(address);
    }
    
    private NeighborDiscoveryData createNeighborDiscoveryData(LinkLayerInfo linkLayerInfo, double timestamp,double valityDelta, Data data) {

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
		NeighborDiscoveryData neighborData = new NeighborDiscoveryData(linkLayerInfo, timestamp, valityDelta, neighborDataMap, 1, false);
		return neighborData;

    }

    private static class OneHopNeighborDiscoveryData implements Data {

		private static final long serialVersionUID = 0L;

		public static final DataID DATA_ID = new ClassDataID(OneHopNeighborDiscoveryData.class);
		static{
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

	
		public boolean equals(Object object) {
			if (object instanceof OneHopNeighborDiscoveryData) {
				OneHopNeighborDiscoveryData data = (OneHopNeighborDiscoveryData) object;
				//check whether data object und current object contains the same data
				return dataMap.equals(data.getDataMap());
			}
			return false;
		}
        
        
    }

	protected Data createBeaconingData() {
		//System.err.println(getOwnAddress() + ": Creating new BeaconingData");
		//Hier Code einbauen, der ueberprueft, ob sich etwas geaendert hat, falls ja die infos 
		//propagateCount mal senden, ansonsten nur NullData senden.
		NeighborDiscoveryData data = getOwnNeighborDiscoveryData();
		DataMap dataMap = data.getDataMap();
		Address[] addresses=getNeighbors();
		if(!state.propagateData(dataMap,addresses)) {
			dataMap=new DefaultDataMap();
			((DefaultDataMap)dataMap).set(new NullData());
		}
		return new OneHopNeighborDiscoveryData(dataMap);
	}

	protected DataID getDataID() {
		return OneHopNeighborDiscoveryData.DATA_ID;
	}

	protected boolean containsData(DataID dataID, Data data) {
		OneHopNeighborDiscoveryData neighborDiscoveryData=(OneHopNeighborDiscoveryData) data;
		return neighborDiscoveryData.getDataMap().hasData(dataID);
	}

	protected void transmissionSend() {
		// ignore
		
	}
}
