/*****************************************************************************
* 
* GenericBeaconingService.java
* 
* $Id: GenericBeaconingService.java,v 1.1 2007/06/25 07:24:00 srothkugel Exp $
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
package de.uni_trier.jane.service.beaconing;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.Data;
import de.uni_trier.jane.basetypes.DataID;
import de.uni_trier.jane.basetypes.Dispatchable;
import de.uni_trier.jane.basetypes.Extent;
import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.RuntimeService;
import de.uni_trier.jane.service.ServiceTimeout;
import de.uni_trier.jane.service.Signal;
import de.uni_trier.jane.service.beaconing.events.BeaconDataEvent;
import de.uni_trier.jane.service.network.link_layer.DefaultDataSerializer;
import de.uni_trier.jane.service.network.link_layer.LinkLayer;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.service.network.link_layer.LinkLayerMessage;
import de.uni_trier.jane.service.network.link_layer.LinkLayerObserver;
import de.uni_trier.jane.service.network.link_layer.LinkLayer_async;
import de.uni_trier.jane.service.network.link_layer.LinkLayer_sync;
import de.uni_trier.jane.service.network.link_layer.MessageSerializer;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.signaling.SignalListener;
import de.uni_trier.jane.simulation.parametrized.parameters.service.ServiceReference;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.EllipseShape;
import de.uni_trier.jane.visualization.shapes.Shape;



/**
 * This class is a generic beaconing service implementation. Override the abstract methods in order
 * to determine the time and period of beacon message transmissions and cleanups.
 */
public abstract class GenericBeaconingService implements RuntimeService, 
                BeaconingService_sync, LinkLayerObserver {

	public static final ServiceReference REFERENCE = new ServiceReference("beaconing") {
		public ServiceID getServiceID(ServiceUnit serviceUnit) {
			if(!serviceUnit.hasService(BeaconingService.class)) {
				RandomBeaconingService.createInstance(serviceUnit);
			}
			return serviceUnit.getService(BeaconingService.class);
		}
	};

	
    private static final double BEACON_DISPLAY_DELTA = 0.5;

    // initialized in constructor
    protected ServiceID linkLayerService;
    private ServiceID ownServiceID;
    protected DefaultDataMap dataMap;
    protected Map deviceTimestampMap;
    private double lastBeaconTime;
    
    // initialized on startup
    protected RuntimeOperatingSystem runtimeOperatingSystem;
    protected Address linkLayerAddress;
    private Shape beaconShape;


    protected LinkLayer_async linkLayer_async;


    protected BeaconingTimer beaconingTimer;


    protected LinkLayer_sync linkLayer_sync;

	

    /**
     * Construct a new generic beaconing service.
     * @param linkLayerService the ID of the link layer service used to send the broadcast messages
     */
    public GenericBeaconingService(ServiceID ownServiceID, ServiceID linkLayerService) {
    	this.linkLayerService = linkLayerService;
    	this.ownServiceID = ownServiceID;
        dataMap = new DefaultDataMap();
        deviceTimestampMap = new HashMap();
        lastBeaconTime = Double.NEGATIVE_INFINITY;
        DefaultDataSerializer.map(BeaconingMessage.class,0,new MessageSerializer() {
        

            public LinkLayerMessage readMessage(ObjectInputStream ois)
                    throws IOException, ClassNotFoundException {
                
                return new BeaconingMessage(null,DefaultDataMap.read(ois));
            }
        
            public void writeMessage(ObjectOutputStream oos, LinkLayerMessage message)
                    throws IOException {
                BeaconingMessage bmessage=(BeaconingMessage)(message);
                ((DefaultDataMap)bmessage.getDataMap()).write(oos);
        
            }
        
        });
    }

    public ServiceID getServiceID() {
        return ownServiceID;
    }
    
    public Address[] getNeighbors(){
        return (Address[])deviceTimestampMap.keySet().toArray(new Address[deviceTimestampMap.size()]);
    }

    public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
    	this.runtimeOperatingSystem = runtimeOperatingSystem;
    	runtimeOperatingSystem.registerAccessListener(BeaconingService_sync.class);
    	runtimeOperatingSystem.registerSignalListener(BeaconingService.class);
    	linkLayer_async=(LinkLayer_async)runtimeOperatingSystem.getSignalListenerStub(linkLayerService,LinkLayer_async.class);
		//linkLayerFacade = new LinkLayer.LinkLayerStub(runtimeOperatingSystem, linkLayerService);
        linkLayer_sync = (LinkLayer_sync)runtimeOperatingSystem.getAccessListenerStub(linkLayerService,LinkLayer_sync.class);
		linkLayerAddress = linkLayer_sync.getNetworkAddress();
        beaconingTimer=new BeaconingTimer();
        beaconingTimer.start(); 
        ServiceTimeout cleanupTimeout = new CleanupTimeout(getCleanupDelta());
        
        runtimeOperatingSystem.setTimeout(cleanupTimeout);
        runtimeOperatingSystem.registerAtService(linkLayerService, LinkLayer.class);
        beaconShape = new EllipseShape(runtimeOperatingSystem.getDeviceID(), new Extent(10,10), Color.BLUE, false);
    	
    }

    public Address getOwnAddress() {
    	return linkLayerAddress;
    }
    
    public void finish() {
        // ignore
    }

    public Shape getShape() {
        double time = runtimeOperatingSystem.getTime();
        if(time <= lastBeaconTime + BEACON_DISPLAY_DELTA) {
            return beaconShape;
        }
        return null;
    }

    public void getParameters(Parameters parameters) {
    	parameters.addParameter("linkLayerID", linkLayerService);
	}

	public void addBeaconData(Data beaconData) {
//System.out.println(runtimeOperatingSystem.getCallingServiceID());
        dataMap.set(beaconData);
    }
    
    public boolean hasBeaconData(DataID dataID) {
    	return dataMap.hasData(dataID);
    }

    public void removeBeaconData(DataID dataID) {
        dataMap.remove(dataID);
    }
    
	public void notifyUnicastLost(Address receiver, LinkLayerMessage message) {
		handleUnicastError(receiver);
	}
	
	public void notifyUnicastProcessed(Address receiver, LinkLayerMessage message) {
		// ignore
	}
	
	public void notifyUnicastReceived(Address receiver, LinkLayerMessage message) {
		// ignore
	}
	
	public void notifyUnicastUndefined(Address receiver, LinkLayerMessage message) {
		handleUnicastError(receiver);
	}
	
	public void notifyBroadcastProcessed(LinkLayerMessage message) {
		// ignore
	}
    
    /**
     * This method determines the time delta between two successive beaconing transmissions
     * @return the time delta
     */
    protected abstract double getBeaconingDelta();

    /**
     * This method determines the time delta between two successive cleanups which are used
     * in order to remove all devices a beacon message was not received for a certain expiration
     * delta.
     * @return the time delta
     */
    protected abstract double getCleanupDelta();

    /**
     * This method determines the time delta the service waits for the next becon message before
     * the device is being removed.
     * @return the expiration delta
     */
    protected abstract double getExpirationDelta();

    /**
     * On cleanup timeout remove all entries which have an expired time stamp, i.e. whose time stamp is older than
     * current time - expiration delta.
     */
    protected void handleCleanupTimeout() {
        List removeList = new ArrayList();
        double smallestTimeStamp = runtimeOperatingSystem.getTime() - getExpirationDelta();
        Iterator iterator = deviceTimestampMap.keySet().iterator();
        while(iterator.hasNext()) {
            Address device = (Address)iterator.next();
            Double timeStamp = (Double)deviceTimestampMap.get(device);
            if(timeStamp.doubleValue() < smallestTimeStamp) {
                removeList.add(device);
            }
        }
        iterator = removeList.iterator();
        while(iterator.hasNext()) {
            deviceTimestampMap.remove(iterator.next());
        }
        iterator = removeList.iterator();
        while(iterator.hasNext()) {
        	Address address = (Address)iterator.next();
            notifyRemoved(address);
        }
        ServiceTimeout cleanupTimeout = new CleanupTimeout(getCleanupDelta());
        runtimeOperatingSystem.setTimeout(cleanupTimeout);
    }

    /**
     * Iterate over all beacon data and send a beacon containing all non-null data.
     */
    protected void handleBeaconingTimeout() {
        LinkLayerMessage message = new BeaconingMessage(linkLayerAddress, dataMap);
        //Signal broadcastSignal = new LinkLayer.SendBroadcastSignal(message);
//      runtimeOperatingSystem.sendSignal(linkLayerService, broadcastSignal);
        linkLayer_async.sendBroadcast(message);
        
        beaconingTimer.reset();
        
    }

    /**
     * Notify all beacon listeners about new and updated neighbor data.
     * @param layerInfo the linklayerInfo of the beacon message
     * @param dataMap the data stored in the beacon message
     */
    protected void handleBeaconMessage(LinkLayerInfo layerInfo,  DataMap dataMap) {
        Address sender=layerInfo.getSender();
        if(!sender.equals(linkLayerAddress)) {
            double time = runtimeOperatingSystem.getTime();
            Double timeStamp = new Double(time);
            BeaconingData data = new BeaconingData(layerInfo, time, getExpirationDelta(), dataMap);
            boolean newEntry = false;
            if(!deviceTimestampMap.containsKey(sender)) {
                newEntry = true;
            }
            deviceTimestampMap.put(sender, timeStamp);
            Signal signal;
            Data[] datas=dataMap.getData();
            for (int i=0;i<datas.length;i++){
                runtimeOperatingSystem.sendEvent(new BeaconDataEvent(datas[i],sender));
            }
            
        	if(newEntry) {
        	    signal = new BeaconingListener.SetNeighborSignal(data);
            }
            else {
        	    signal = new BeaconingListener.UpdateNeighborSignal(data);
            }
        	runtimeOperatingSystem.sendSignal(signal);
        }
    }

    // remove the receiver of a lost unicast transmission
    private void handleUnicastError(Address address) {
        if (deviceTimestampMap.remove(address)!=null)
            notifyRemoved(address);
    }

    // notify all beacon listeners, that a neighbor has been removed
    private void notifyRemoved(Address address) {
        Signal signal = new BeaconingListener.RemoveNeighborSignal(address);
        runtimeOperatingSystem.sendSignal(signal);
    }

    protected class BeaconingTimer{
        private ServiceTimeout timeout;
        
        
        public void reset(){
            if (timeout!=null){
                runtimeOperatingSystem.removeTimeout(timeout);
            }
            setTimeout();
            lastBeaconTime = runtimeOperatingSystem.getTime();
            Signal notificationSignal = new BeaconingListener.NotifyTransmissionSignal();
            runtimeOperatingSystem.sendSignal(notificationSignal);
        }


        /**
         * TODO: comment method 
         */
        private void setTimeout() {
            timeout=new ServiceTimeout(getBeaconingDelta()){
                public void handle() {
                    timeout=null;
                    handleBeaconingTimeout();
                };
            };
            runtimeOperatingSystem.setTimeout(timeout);
        }


        public void start() {
            setTimeout();
            
        }
    }

    private class CleanupTimeout extends ServiceTimeout {
        /**
         * 
         * Constructor for class <code>CleanupTimeout</code>
         * @param delta
         */
        public CleanupTimeout(double delta) {
            super(delta);
        }

        public void handle() {
            handleCleanupTimeout();
        }

    }

    public static class BeaconingMessage implements LinkLayerMessage {

    	// TODO: muss die Addresse hier wirklich gespeichert werden??? -> was ist z.B. bei piggybacking??? --> evtl. eigenen Nachrichtentyp f?r Pigybacking in speziellem LinkLayer???
        //private LinkLayerAddress address;
        private DataMap dataMap;
        /**
         * 
         * Constructor for class <code>BeaconingMessage</code>
         * @param address
         * @param dataMap
         */
        public BeaconingMessage(Address address, DataMap dataMap) {
         //   this.address = address;
            this.dataMap = dataMap;
        }

		public void handle(LinkLayerInfo info, SignalListener listener) {
            GenericBeaconingService beaconingService = (GenericBeaconingService)listener;
            beaconingService.handleBeaconMessage(info, dataMap);
		}

        public Class getReceiverServiceClass() {
            return GenericBeaconingService.class;
        }

        public int getSize() {
            return dataMap.getSize() +8*8;//TODO: address size in Beaconmessage!
            //+ address.getCodingSize();
        }

        public Shape getShape() {
            return new EllipseShape(Position.NULL_POSITION,new Extent(5,5),Color.ORANGE,true);
        }

        public Dispatchable copy() {
        	// pr?fen, copy ?berhaupt notwendig ist! Evtl. kann man hier auch this zur?ckgeben
           // return new BeaconingMessage(address, dataMap.copy());
        	return this;
        }

        public DataMap getDataMap() {
            return dataMap;
            
        }

    }

}
