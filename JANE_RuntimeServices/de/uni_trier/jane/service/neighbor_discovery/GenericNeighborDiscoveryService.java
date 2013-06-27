/*****************************************************************************
* 
* $Id: GenericNeighborDiscoveryService.java,v 1.2 2007/07/10 17:36:15 aandronache Exp $
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

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.beaconing.*;
import de.uni_trier.jane.service.event.ServiceEvent;
import de.uni_trier.jane.service.neighbor_discovery.events.*;
import de.uni_trier.jane.service.network.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.parametrized.parameters.base.*;
import de.uni_trier.jane.simulation.parametrized.parameters.service.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This service implements a generic neighbor discovery service which determines all devices which
 * can transmit a message to this device in one hop. It is based on top of a @see de.uni_trier.jane.service.beaconing.BeaconingService.
 * Other servcices are able to add local data which are propagated using the beaconing service to neighboring devices.
 *  
 * Signalling
 * @see de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryListener
 *  
 *  
 * ServiceEvents: 
 * (note, the event propagation must be enabled in some subclasses) 
 * Specific data from one neighbor device
 * has been changed
 * @see de.uni_trier.jane.service.neighbor_discovery.events.NeighborDiscoveryEventUpdate
 * has been removed
 * @see de.uni_trier.jane.service.neighbor_discovery.events.NeighborDiscoveryEventDelete
 * has been received for the first time 
 * @see de.uni_trier.jane.service.neighbor_discovery.events.NeighborDiscoveryEventNew 
 * 
 * @see de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryData of one neighboring device 
 * has been updadated (e.g. <code>Data</code> has been updated, removed or added)
 * @see de.uni_trier.jane.service.neighbor_discovery.events.NeighborDiscoveryDataEventUpdate
 * has been removed (e.g. the device left communiaction renage of the neighboring device)
 * @see de.uni_trier.jane.service.neighbor_discovery.events.NeighborDiscoveryDataEventDelete
 * has been recived for the first time (e.g. the device entered communiaction renage of the neighboring device)
 * @see de.uni_trier.jane.service.neighbor_discovery.events.NeighborDiscoveryDataEventNew
 */
public abstract class GenericNeighborDiscoveryService implements 
        RuntimeService, 
        NeighborDiscoveryService_sync, 
        NeighborDiscoveryService, 
        BeaconingListener,
        UnicastErrorSignalReceiver{

	public static final ServiceReference REQUIRED_SERVICE = new ServiceReference("neighborDiscovery") {
		public ServiceID getServiceID(ServiceUnit serviceUnit) {
			if(!serviceUnit.hasService(NeighborDiscoveryService.class)) {
				OneHopNeighborDiscoveryService.createInstance(serviceUnit);
			}
			return serviceUnit.getService(NeighborDiscoveryService.class);
		}
	};

	protected static final BooleanParameter INCLUDE_OWN_DEVICE = new BooleanParameter("includeOwnDevice", true);
    protected static final BooleanParameter USE_EVENTS = new BooleanParameter("useEvents", true);

	
	// intialized in constructor
    private ServiceID beaconingServiceID;
    private boolean includeOwnDevice;
	private ServiceID ownServiceID;
	private NeighborDiscoveryProperties neighborDiscoveryProperties;
    private DefaultDataMap ownDataMap; // data of this device
    protected Map neighborDiscoveryDataMap; // data of all neighbors

    // intialized on startup
    private RuntimeOperatingSystem operatingSystem;
    private NeighborDiscoveryListenerStub neighborDiscoveryListenerStub;
    private BeaconingServiceStub beaconingServiceStub;
	private Address address;

    private boolean propagateEvents;
	
    /**
     * Construct a new discovery service.
     * @param beaconingServiceID the beaconing service used to send own data to all one hop neighbors
     * @param includeOwnDevice this flag determines if own data is stored as 0-hop information as well
     * @param propagateEvents 
     */
    public GenericNeighborDiscoveryService(ServiceID ownServiceID, ServiceID beaconingServiceID, boolean includeOwnDevice, boolean propagateEvents) {
        this.beaconingServiceID = beaconingServiceID;
        this.includeOwnDevice = includeOwnDevice;
        this.ownServiceID = ownServiceID;
        this.propagateEvents=propagateEvents;
        neighborDiscoveryProperties = new NeighborDiscoveryProperties(ownServiceID, includeOwnDevice);
        ownDataMap = new DefaultDataMap();
        neighborDiscoveryDataMap = new HashMap();
    }

    public void start(RuntimeOperatingSystem operatingSystem) {
        this.operatingSystem = operatingSystem;
        operatingSystem.registerAccessListener(NeighborDiscoveryService_sync.class);
        operatingSystem.registerSignalListener(NeighborDiscoveryService.class);
        operatingSystem.registerAtService(beaconingServiceID, BeaconingService_sync.class);
        neighborDiscoveryListenerStub = new NeighborDiscoveryListenerStub(operatingSystem);
        
        beaconingServiceStub = new BeaconingServiceStub(operatingSystem, beaconingServiceID);
        address=beaconingServiceStub.getOwnAddress();
        if(includeOwnDevice) {
            NeighborDiscoveryData neighborData = getOwnNeighborDiscoveryData();
        	neighborDiscoveryDataMap.put(neighborData.getSender(), neighborData);
        	neighborDiscoveryListenerStub.setNeighborData(neighborData);
        }
    }

    public ServiceID getServiceID() {
        return ownServiceID;
    }

	public NeighborDiscoveryProperties getNeighborDiscoveryProperties() {
		return neighborDiscoveryProperties;
	}

    public void finish() {
    	// ignore
    }

    public Shape getShape() {
    	ShapeCollection shapeCollection = new ShapeCollection();
//    	Address[] neighbors = getNeighbors(NeighborDiscoveryFilter.ONE_HOP_NEIGHBOR_FILTER);
//    	int len = neighbors.length;
//    	for(int i=0; i<len; i++) {
//    		shapeCollection.addShape(new ArrowShape(neighbors[i], address, Color.LIGHTGREY, 2.0));
//    	}
        return shapeCollection;
    }

	public void getParameters(Parameters parameters) {
		parameters.addParameter("includeOwnDevice", includeOwnDevice);
	}

	public Address getOwnAddress() {
		return address;
	}
	
	public void setOwnData(Data data) {
        ownDataMap.set(data);
        notifyChanged();
        if(includeOwnDevice) {
        	NeighborDiscoveryData neighborData = getOwnNeighborDiscoveryData();
        	neighborDiscoveryDataMap.put(neighborData.getSender(), neighborData);
        	neighborDiscoveryListenerStub.updateNeighborData(neighborData);
        }
    }

    public void removeOwnData(DataID id) {
        ownDataMap.remove(id);
        notifyChanged();
        if(includeOwnDevice) {
        	NeighborDiscoveryData neighborData = getOwnNeighborDiscoveryData();
        	neighborDiscoveryDataMap.put(neighborData.getSender(), neighborData);
        	neighborDiscoveryListenerStub.updateNeighborData(neighborData);
        }
    }

    public void setNeighbor(BeaconingData beaconingData) {
    	//TODO generischer Realisieren
    	beaconingData.getSender();
    	
    	if(!beaconingData.getSender().equals(getOwnAddress())) {
        	Data data = beaconingData.getDataMap().getData(getDataID());
        	
        	double timestamp = beaconingData.getTimeStamp();
            
    		setNeighborData(beaconingData.getReceiveInfo(), timestamp, beaconingData.getValidityDelta(), data);
    	}
    }

    public void updateNeighbor(BeaconingData beaconingData) {
//    	TODO generischer Realisieren
    	if(!beaconingData.getSender().equals(getOwnAddress())) {
        	Data data = beaconingData.getDataMap().getData(getDataID());
        	double timestamp = beaconingData.getTimeStamp();
//    		setNeighborData(address, timestamp, data);
    		updateNeighborData(beaconingData.getReceiveInfo(), timestamp, beaconingData.getValidityDelta(), data);
    	}
    }

    public void removeNeighbor(Address address) {
//    	TODO generischer Realisieren
    	//System.err.println("Remove Neighbor");
    	if(!address.equals(getOwnAddress())) {
    		removeNeighborData(address); 
    	}
    }

    public void notifyTransmission() {
//    	TODO generischer Realisieren
        // ignore
    	transmissionSend();
    }

	

	public NeighborDiscoveryData getNeighborDiscoveryData(Address address) {
		return (NeighborDiscoveryData)neighborDiscoveryDataMap.get(address);
	}

	public NeighborDiscoveryData[] getNeighborDiscoveryData() {
		Collection values = neighborDiscoveryDataMap.values();
		return (NeighborDiscoveryData[])values.toArray(new NeighborDiscoveryData[values.size()]);
	}

	public NeighborDiscoveryData[] getNeighborDiscoveryData(NeighborDiscoveryFilter filter) {
		List result = new ArrayList();
		Iterator iterator = neighborDiscoveryDataMap.values().iterator();
		while (iterator.hasNext()) {
			NeighborDiscoveryData neighborData = (NeighborDiscoveryData)iterator.next();
			if(filter.matches(neighborData)) {
				result.add(neighborData);
			}
		}
		return (NeighborDiscoveryData[])result.toArray(new NeighborDiscoveryData[result.size()]);
	}

	public Address[] getNeighbors() {
		Set keySet = neighborDiscoveryDataMap.keySet();
		return (Address[])keySet.toArray(new Address[keySet.size()]);
	}
	
	public Address[] getNeighbors(NeighborDiscoveryFilter filter) {
		List result = new ArrayList();
		Iterator iterator = neighborDiscoveryDataMap.keySet().iterator();
		while (iterator.hasNext()) {
			Address linkLayerAddress = (Address) iterator.next();
			NeighborDiscoveryData neighborData = (NeighborDiscoveryData)neighborDiscoveryDataMap.get(linkLayerAddress);
			if(filter.matches(neighborData)) {
				result.add(linkLayerAddress);
			}
		}
		return (Address[])result.toArray(new Address[result.size()]);
	}

	public int countNeighbors() {
		return neighborDiscoveryDataMap.size();
	}

	public int countNeighbors(NeighborDiscoveryFilter filter) {
		int result = 0;
		Iterator iterator = neighborDiscoveryDataMap.values().iterator();
		while (iterator.hasNext()) {
			NeighborDiscoveryData neighborData = (NeighborDiscoveryData)iterator.next();
			if(filter.matches(neighborData)) {
				result++;
			}
		}
		return result;
	}

	public boolean hasNeighborDiscoveryData(Address address) {
		return neighborDiscoveryDataMap.containsKey(address);
	}

	public boolean hasNeighborDiscoveryData(Address address, NeighborDiscoveryFilter filter) {
		NeighborDiscoveryData neighborData = (NeighborDiscoveryData)neighborDiscoveryDataMap.get(address);
		return filter.matches(neighborData);
	}
	
	public Data getData(Address address, DataID dataID) {
		NeighborDiscoveryData neighborData = (NeighborDiscoveryData)neighborDiscoveryDataMap.get(address);
		return neighborData.getDataMap().getData(dataID);
	}

	public boolean hasData(Address address, DataID dataID) {
		NeighborDiscoveryData neighborData = (NeighborDiscoveryData)neighborDiscoveryDataMap.get(address);
		return neighborData.getDataMap().hasData(dataID);
	}

	protected void setNeighborDiscoveryData(NeighborDiscoveryData neighborDiscoveryData) {
		Address key = neighborDiscoveryData.getSender(); 
        
        
        createDataEvents(neighborDiscoveryData.getSender(),neighborDiscoveryData);
        
		this.neighborDiscoveryDataMap.put(key, neighborDiscoveryData);
        
        
        
		neighborDiscoveryListenerStub.setNeighborData(neighborDiscoveryData);
	}

	/**
     * TODO Comment method
     * @param sender
     * @param neighborDiscoveryData
     */
    private void createDataEvents(Address sender, NeighborDiscoveryData neighborDiscoveryData) {
        if (!propagateEvents) return;
        NeighborDiscoveryData neighborDiscoveryDataOld=(NeighborDiscoveryData) neighborDiscoveryDataMap.get(sender);
        DataMap dataMap=neighborDiscoveryData.getDataMap();
        Data datas[] = dataMap.getData();
        
        if (neighborDiscoveryDataOld!=null){
            DataMap oldMap=neighborDiscoveryDataOld.getDataMap();
        
            boolean update=false;
            for (int i=0;i<datas.length;i++){
                if (oldMap.hasData(datas[i].getDataID())){
                    if (!oldMap.getData(datas[i].getDataID()).equals(datas[i])){
                        operatingSystem.sendEvent(new NeighborDiscoveryDataEventUpdate(datas[i],sender));
                        update=true;
                    }
                }else{
                    operatingSystem.sendEvent(new NeighborDiscoveryDataEventNew(datas[i],sender));
                    update=true;
                }
            }
            Data[] oldDatas=oldMap.getData();
            for (int i=0;i<oldDatas.length;i++){
                if (!dataMap.hasData(oldDatas[i].getDataID())){
                    operatingSystem.sendEvent(new NeighborDiscoveryDataEventDelete(oldDatas[i],sender));
                    update=true;
                }
            }
            if(update){
                operatingSystem.sendEvent(new NeighborDiscoveryEventUpdate(neighborDiscoveryData));
            }
        }else{
            operatingSystem.sendEvent(new NeighborDiscoveryEventNew(neighborDiscoveryData));
            for (int i=0;i<datas.length;i++){
                operatingSystem.sendEvent(new NeighborDiscoveryDataEventNew(datas[i],sender));
            }
        }
        
    }
    
    /**
     * 
     * TODO Comment method
     * @param address
     * @param data
     */
    private void createRemoveEvents(Address address,NeighborDiscoveryData data) {
        if (!propagateEvents) return;
        operatingSystem.sendEvent(new NeighborDiscoveryEventDelete(data));
        if (data!=null){
            DataMap dataMap=data.getDataMap();
            Data datas[] = dataMap.getData();
            for (int i=0;i<datas.length;i++){
                operatingSystem.sendEvent(new NeighborDiscoveryDataEventDelete(datas[i],address));
            }
        }
        
    }

    protected void updateNeighborDiscoveryData(NeighborDiscoveryData neighborDiscoveryData) {
		Address key = neighborDiscoveryData.getSender(); 
        createDataEvents(neighborDiscoveryData.getSender(),neighborDiscoveryData);
		this.neighborDiscoveryDataMap.put(key, neighborDiscoveryData);
        
		neighborDiscoveryListenerStub.updateNeighborData(neighborDiscoveryData);
	}

	protected void removeNeighborDiscoveryData(Address address) {
        
		createRemoveEvents(address,(NeighborDiscoveryData)neighborDiscoveryDataMap.remove(address));
		neighborDiscoveryListenerStub.removeNeighborData(address);
	}



    protected abstract Data createBeaconingData();
	protected abstract DataID getDataID();
	
	
    protected abstract void setNeighborData(LinkLayerInfo linkLayerInfo, double timestamp, double validityDelta, Data data);
    
    // update the discovery info
    protected abstract void updateNeighborData(LinkLayerInfo linkLayerInfo, double timestamp, double validityDelta, Data data);

    protected abstract void removeNeighborData(Address address);

    protected abstract void transmissionSend();
    
    // set the current beacon data at the beaconing service
    protected void notifyChanged() {
        Data data = createBeaconingData();
        beaconingServiceStub.addBeaconData(data);
    }

    public NeighborDiscoveryData getOwnNeighborDiscoveryData() {
    	return new NeighborDiscoveryData(
                new LinkLayerInfoImplementation(getOwnAddress(),getOwnAddress(),false,Double.MAX_VALUE),
                operatingSystem.getTime(),Double.MAX_VALUE, ownDataMap, 0, false);
        //TODo:correct values?
    }
    
    public void addUnicastErrorProvider(ServiceID serviceID) {
        operatingSystem.registerAtService(serviceID,Service.class);//TODO
        
    }
    
    public void handleUnicastError(Address failedAddress) {
//        removeNeighbor(failedAddress);
        
    }

}
