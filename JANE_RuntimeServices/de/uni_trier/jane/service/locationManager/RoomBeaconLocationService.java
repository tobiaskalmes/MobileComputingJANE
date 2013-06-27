/*****************************************************************************
 * 
 * RoomBeaconLocationService.java
 * 
 * $Id: RoomBeaconLocationService.java,v 1.1 2007/06/25 07:24:00 srothkugel Exp $
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



import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.locationManager.*;
import de.uni_trier.jane.service.locationManager.basetypes.*;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.util.HashMapSet;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.*;

import java.util.*;

public class RoomBeaconLocationService implements RuntimeService,
        LocationManager_sync, LocationManager,BeaconLocationService {

    private static final class BeaconLocation implements Location {

        private double time;
        private Address address;
        private Position position;
        private double distance;

        public BeaconLocation(double time, Address address, Position position, double distance) {
            this.time=time;
            this.address=address;
            this.position=position;
            this.distance=distance;
        }

        public Address getAddress() {
            return address;
        }

        public double getDistance() {
            return distance;
        }

        public Position getPosition() {
            return position;
        }

        public double getTime() {
            return time;
        }

        public void setTime(double time) {
            this.time=time;   
        }

        public Class getLocationManagerClass() {
            return RoomBeaconLocationService.class;
        }

        public ServiceID createLocationManagerService(ServiceUnit serviceUnit) {
            return RoomBeaconLocationService.createInstance(serviceUnit);
        }
        
        public boolean isLocatedAt(RuntimeOperatingSystem operatingSystem) {
            LocationManager_sync locationManager_sync=
                (LocationManager_sync) operatingSystem.getAccessListenerStub(serviceID,LocationManager_sync.class);
            
            return locationManager_sync.locatedAt(this);
        }

        public int getCodingSize() {
            // TODO Auto-generated method stub
            return 0;
        }

        public Shape getShape(DeviceID address, Color color, boolean filled) {
            
            return null;
        }
        

        public int hashCode() {
            final int PRIME = 1000003;
            int result = 0;
            if (address != null) {
                result = PRIME * result + address.hashCode();
            }

            return result;
        }

        public boolean equals(Object oth) {
            if (this == oth) {
                return true;
            }

            if (oth == null) {
                return false;
            }

            if (oth.getClass() != getClass()) {
                return false;
            }

            BeaconLocation other = (BeaconLocation) oth;
            if (this.address == null) {
                if (other.address != null) {
                    return false;
                }
            } else {
                if (!this.address.equals(other.address)) {
                    return false;
                }
            }

            return true;
        }

    }
    public static final ServiceID serviceID=new EndpointClassID(RoomBeaconLocationService.class.getName());
    private RuntimeOperatingSystem operatingSystem;
    private BeaconLocation currentLocation;
    private double cleanUpDelta;
    private double cleanUpInterval;
    private Shape noLocationShape;
    private Shape locationShape;
    private HashMapSet locationListeners;
    private Map locationListenersR;
    
    
    /**
     * 
     * TODO: comment method 
     * @param serviceUnit
     * @return
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit) {
        return serviceUnit.addService(new RoomBeaconLocationService(2,0.5));
    }
    
    /**
     * 
     * TODO: comment method 
     * @param serviceUnit
     * @param cleanUpDelta
     * @param cleanUpInterval
     * @return
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit,double cleanUpDelta, double cleanUpInterval) {
        return serviceUnit.addService(new RoomBeaconLocationService(cleanUpDelta,cleanUpInterval));
    }

    /**
     * Constructor for class <code>RoomBeaconLocationService</code>
     *
     * @param cleanUpDelta
     * @param cleanUpInterval
     */
    public RoomBeaconLocationService(double cleanUpDelta, double cleanUpInterval) {
        if (cleanUpInterval<=0) throw new IllegalArgumentException("cleanUpInterval must ne greater than 0");
        if (cleanUpDelta<cleanUpInterval)throw new IllegalArgumentException("cleanUpDelta must ne greater than cleanUpInterval");
        this.cleanUpDelta = cleanUpDelta;
        this.cleanUpInterval = cleanUpInterval;
        locationListeners=new HashMapSet();
        locationListenersR=new HashMap();
    }



    public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
        operatingSystem=runtimeOperatingSystem;
        noLocationShape=new CrossShape(operatingSystem.getDeviceID(),Position.NULL_POSITION,5,Color.RED,false,false);

    }

    public ServiceID getServiceID() {
        return serviceID;
    }

    public void finish() {
        // TODO Auto-generated method stub

    }

    public Shape getShape() {
        if (currentLocation==null){
            return noLocationShape;
        }
        return locationShape;
    }

    public void getParameters(Parameters parameters) {
        // TODO Auto-generated method stub

    }
    
    public void receiveBeaconSignal(Address address, Position position, double signalStength) {
        BeaconLocation newLocation = new BeaconLocation(operatingSystem.getTime(),address,position,signalStength);
        if (currentLocation==null){
            operatingSystem.setTimeout(new ServiceTimeout(cleanUpInterval){
                public void handle() {
                    if (operatingSystem.getTime()-currentLocation.getTime()>cleanUpDelta){
                        
                        leftLocation();
                        currentLocation=null;
                    }else{
                        operatingSystem.setTimeout(this);
                    }
                    
                }
            });
            currentLocation=newLocation;
            newLocation();
            return;
            
        }
        
        if (currentLocation.getAddress().equals(newLocation.getAddress())){
            currentLocation=newLocation;
        }else if (currentLocation.getDistance()>=newLocation.getDistance()){
            leftLocation();
            currentLocation=newLocation;
            newLocation();
        }
            
        
        locationShape=new TextShape("Loc:"+currentLocation.getAddress(),
                operatingSystem.getDeviceID(),Color.RED);
        
    }

    protected void leftLocation() {
        
        Address address=currentLocation.getAddress();
        Location location=new DeviceAddressLocation(address);
        operatingSystem.sendSignal(new LocationListener.LeftLocationSignal(location));
        if (!locationListeners.containsKey(address)) return;
        
        Iterator iterator=locationListeners.get(address).iterator();
        operatingSystem.sendSignal(new LocationListener.LeftLocationSignal(location));
        while (iterator.hasNext()) {
            ListenerID element = (ListenerID) iterator.next();
            operatingSystem.sendSignal(element,new LocationListener.LeftLocationSignal(location));
            
        }
        
    }

    private void newLocation() {
        Address address=currentLocation.getAddress();
        Location location=new DeviceAddressLocation(address);
        operatingSystem.sendSignal(new LocationListener.EnteredLocationSignal(location));
        if (!locationListeners.containsKey(address)) return;
        Iterator iterator=locationListeners.get(address).iterator();
        while (iterator.hasNext()) {
            ListenerID element = (ListenerID) iterator.next();
            operatingSystem.sendSignal(element,new LocationListener.EnteredLocationSignal(location));
            
        }
        
    }

    public boolean locatedAt(Location location) {
        
        if (currentLocation!=null &&location instanceof DeviceAddressLocation) {
            DeviceAddressLocation deviceLocation = (DeviceAddressLocation) location;
            return currentLocation.getAddress().equals(deviceLocation.getNeighborDeviceAddress());
        }
        return false;
    }

    public boolean isResponsibleFor(Location location) {
        
        return (location instanceof DeviceAddressLocation);
    }

    public Location getCurrentLocation(Location locationExample) {
        if (currentLocation!=null){
            return new DeviceAddressLocation(currentLocation.getAddress());
        }
        return null;
    }

    public void startLocationListenerTask(Location location, ListenerID taskID) {
        if (location instanceof DeviceAddressLocation) {
            Address address=((DeviceAddressLocation)location).getNeighborDeviceAddress();
            locationListeners.put(address,taskID);
            locationListenersR.put(taskID,address);
            if (currentLocation!=null&&currentLocation.getAddress().equals(address)){
                operatingSystem.sendSignal(taskID,new LocationListener.EnteredLocationSignal(location));
            }
        }

    }

    public void changeLocationListenerTask(Location location, ListenerID taskID) {
        if (location instanceof DeviceAddressLocation) {
            Address address=((DeviceAddressLocation)location).getNeighborDeviceAddress();
            locationListeners.remove(address,taskID);
            startLocationListenerTask(location,taskID);
        }
        
        

    }

    public void stopLocationListenerTask(ListenerID listenerID) {
        Address address=(Address)locationListenersR.remove(listenerID);
        locationListeners.remove(address,listenerID);
        if (locationListeners.get(address).isEmpty()){
            locationListeners.remove(address);
        }
        
        operatingSystem.finishListener(listenerID);

    }

}
