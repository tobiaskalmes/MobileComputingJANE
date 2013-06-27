/*****************************************************************************
 * 
 * DeviceIDLocationManager.java
 * 
 * $Id: DeviceIDLocationManager.java,v 1.1 2007/06/25 07:24:00 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.service.locationManager; 

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.beaconing.*;
import de.uni_trier.jane.service.locationManager.*;
import de.uni_trier.jane.service.locationManager.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.util.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * @author goergen
 *
 * TODO comment class
 */
public class DeviceIDLocationManager implements LocationManager,
        LocationManager_sync, RuntimeService {

    
    public static final ServiceID serviceID = new EndpointClassID(DeviceIDLocationManager.class.getName());

    public static ServiceID createInstance(ServiceUnit serviceUnit){
        ServiceID beaconingServiceID;
        if (serviceUnit.hasService(BeaconingService.class)){
            beaconingServiceID=serviceUnit.getService(BeaconingService.class);
        }else{
            beaconingServiceID=RandomBeaconingService.createInstance(serviceUnit);
        }
        return serviceUnit.addService(new DeviceIDLocationManager(beaconingServiceID));
    }
    
    
    public static ServiceID createInstance(RuntimeEnvironment serviceUnit){
        ServiceID beaconingServiceID;
        if (serviceUnit.hasService(BeaconingService.class)){
            beaconingServiceID=serviceUnit.getServiceIDs(BeaconingService.class)[0];
        }else{
            //TODO;
            throw new IllegalStateException("cannot create Beaconing service at runtime");
        }
        return serviceUnit.startService(new DeviceIDLocationManager(beaconingServiceID));
    }
    
    
    
    private ServiceID beaconingServiceID;
    
    private HashMapSet neighborListenerMap;
    private Map listenerNeighborMap;
    protected Set currentNeighbors;
    protected RuntimeOperatingSystem operatingSystem;

    private Location myLocation;

    /**
     * Constructor for class <code>DeviceIDLocationManager</code>
     * @param beaconingServiceID
     * 
     */
    public DeviceIDLocationManager(ServiceID beaconingServiceID) {
        super();
        this.beaconingServiceID=beaconingServiceID;
        neighborListenerMap=new HashMapSet();
        listenerNeighborMap=new HashMap();
        currentNeighbors=new LinkedHashSet();
    }



    public void startLocationListenerTask(Location location, ListenerID taskID) {
        if (!isResponsibleFor(location)){
            throw new IllegalStateException("This LocationManager uses only DeviceIDLocations");
            //mmmh: eine lustige Idee. Leite Anfrage an den in location angegebene dienst weiter; ggf neu starten :)
        }
        Address neighbor =((DeviceAddressLocation)location).getNeighborDeviceAddress();
        neighborListenerMap.put(neighbor,taskID);
        listenerNeighborMap.put(taskID,neighbor);
        if (currentNeighbors.contains(neighbor)){
            operatingSystem.sendSignal(taskID,new LocationListener.EnteredLocationSignal(location));
        }
    }

    /**
     * TODO: comment method 
     * @param location
     * @return
     */
    public boolean isResponsibleFor(Location location) {
        return (location instanceof DeviceAddressLocation);
    }


    public void changeLocationListenerTask(Location location, ListenerID taskID) {
        Object device=listenerNeighborMap.remove(taskID);
        if (device==null) return;
        neighborListenerMap.remove(device,taskID);
        
        if (neighborListenerMap.get(device).isEmpty()){
            neighborListenerMap.remove(device);
        }
        startLocationListenerTask(location,taskID);
        
    }

    public void stopLocationListenerTask(ListenerID taskID) {
        Object device=listenerNeighborMap.remove(taskID);
        if (device==null) return;
        neighborListenerMap.remove(device,taskID);
        if (neighborListenerMap.get(device).isEmpty()){
            neighborListenerMap.remove(device);
        }
        operatingSystem.finishListener(taskID);

    }


    public boolean locatedAt(Location location) {
        if (!isResponsibleFor(location)){
            throw new IllegalStateException("This LocationManager uses only DeviceIDLocations");
            //or return false?
            //mmmh: eine lustige Idee. Leite Anfrage an den in location angegebene dienst weiter; ggf neu starten :)
        }
        return currentNeighbors.contains(((DeviceAddressLocation)location).getNeighborDeviceAddress());
        //return neighborDiscoveryService.hasNeighborDiscoveryData(neighbor);
    }


    public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
        operatingSystem=runtimeOperatingSystem;
        operatingSystem.registerSignalListener(LocationManager.class);
        operatingSystem.registerAccessListener(LocationManager_sync.class);
        BeaconingService_sync beaconingService=(BeaconingService_sync)runtimeOperatingSystem.
        	getAccessListenerStub(beaconingServiceID,BeaconingService_sync.class);
        myLocation=new DeviceAddressLocation(beaconingService.getOwnAddress());
        currentNeighbors.addAll(Arrays.asList(beaconingService.getNeighbors()));
        currentNeighbors.add(beaconingService.getOwnAddress());
        runtimeOperatingSystem.registerAtService(beaconingServiceID,new BeaconingListener() {

           public void setNeighbor(BeaconingData data) {
               Address address=data.getSender();
               currentNeighbors.add(address);
               
                if(!neighborListenerMap.containsKey(address)) return;
                Location location=new DeviceAddressLocation(address);
                Iterator listeners=neighborListenerMap.get(address).iterator();
                
                while (listeners.hasNext()) {
                    ListenerID current = (ListenerID) listeners.next();
                    operatingSystem.sendSignal(current,new LocationListener.EnteredLocationSignal(
                            location));
                }
            }

            public void removeNeighbor(Address linkLayerAddress) {
                currentNeighbors.remove(linkLayerAddress);
                if(!neighborListenerMap.containsKey(linkLayerAddress)) return;
                Location location=new DeviceAddressLocation(linkLayerAddress);
                Iterator listeners=neighborListenerMap.get(linkLayerAddress).iterator();
                while (listeners.hasNext()) {
                    ListenerID current = (ListenerID) listeners.next();
                    operatingSystem.sendSignal(current,
                            new LocationListener.LeftLocationSignal(location));
                }
            }
            public void notifyTransmission() {/*ignore*/}
            public void updateNeighbor(BeaconingData neighborData) {/*ignore*/}
        },BeaconingService.class);

    }


    public ServiceID getServiceID() {
        return serviceID;
    }



    public void finish() {
        // TODO Auto-generated method stub

    }

    public Shape getShape() {
        Iterator iterator=currentNeighbors.iterator();
        ShapeCollection collection=new ShapeCollection();
        while (iterator.hasNext()) {
            Address element = (Address) iterator.next();
            if (neighborListenerMap.containsKey(element)){
                collection.addShape(new LineShape(element,operatingSystem.getDeviceID(),Color.BLUE));
            }
            
        }
        return collection;
    }



    public void getParameters(Parameters parameters) {
        // TODO Auto-generated method stub

    }


    public Location getCurrentLocation(Location locationExample) {
        return myLocation;
    }

}
