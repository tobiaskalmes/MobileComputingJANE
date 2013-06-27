/*****************************************************************************
 * 
 * GeographicLocationManager.java}
 * 
 * $Id: GeographicLocationManager.java,v 1.1 2007/06/25 07:24:00 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Daniel Goergen and Hannes Frey and Johannes K. Lehnert
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
import de.uni_trier.jane.service.locationManager.basetypes.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.service.positioning.*;
import de.uni_trier.jane.service.positioning.PositioningService.*;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.visualization.shapes.Shape;

import java.util.*;

/**
 * TODO: comment class  
 * @author daniel
 */
public class GeographicLocationManager implements LocationManager, LocationManager_sync, RuntimeService, PositioningListener {

    // set during runtime 
    private Position position;

    private RuntimeOperatingSystem operatingSystem;

//set at startup
private ServiceID positioningServiceID;

    private Map listenLocationMap;

    public static final ServiceID serviceID=new EndpointClassID(GeographicLocationManager.class.getName());


    /**
     * TODO: comment method 
     * @param operatingSystem
     * @return
     */
    public static ServiceID createInstance(RuntimeEnvironment operatingSystem) {
        ServiceID positioningService;
        if (operatingSystem.hasService(PositioningService.class)){
            ServiceID[] services=operatingSystem.getServiceIDs(PositioningService.class);
            if (services.length>1){
                operatingSystem.write("DEBUG: more than one Positioning service exists. Using first.");
                
            }
            positioningService=services[0];
        }else{
            throw new IllegalStateException("cannot create Positioning service at runtime");
        }
        return operatingSystem.startService(new GeographicLocationManager(positioningService));
    }
    
    /**
     * TODO: comment method 
     * @param serviceUnit
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit) {
        ServiceID positioningService;
        if (serviceUnit.hasService(PositioningService.class)){
            ServiceID services=serviceUnit.getService(PositioningService.class);
            //if (services.length>1){
              //  operatingSystem.write("DEBUG: more than one Positioning service exists. Using first.");
                
           // }
            positioningService=services;
        }else{
            throw new IllegalStateException("cannot create Positioning service ");
        }
        return serviceUnit.addService(new GeographicLocationManager(positioningService));
        
    }
    

    /**
     * Constructor for class GeographicLocationManager 
     *
     * @param positioningServiceID
     */
    public GeographicLocationManager(ServiceID positioningServiceID) {
        super();
        this.positioningServiceID = positioningServiceID;
        listenLocationMap=new HashMap();
       //serviceID=new StackedClassID(GeographicLocationManager.class.getName(),positioningServiceID);
    }
    
    
    public boolean locatedAt(Location location) {
        if (isResponsibleFor(location)){
            return ((GeographicLocation)location).isInside(position);
        }
        throw new IllegalArgumentException("location is not a geographic location");
        //return null;
    }

 

    public void startLocationListenerTask(Location location, ListenerID handle) {
        if (isResponsibleFor(location)){
            if (!listenLocationMap.containsKey(handle)){
                operatingSystem.addListenerHandler(handle, new ListenerFinishedHandler() {
               
                    public void handleFinished(ListenerID listenerID) {
                        stopLocationListenerTask(listenerID);

                    }
                });
            }
            GeographicLocation geoLocation=(GeographicLocation)location;
            listenLocationMap.put(handle,geoLocation);
            if (geoLocation.isInside(position)){
                operatingSystem.sendSignal(handle,
                        new LocationListener.EnteredLocationSignal(location));
            }
        }else{
            throw new IllegalArgumentException("location is not a geographic location");
        }
        

    }
    
    /**
     * TODO: comment method 
     * @param location
     * @return
     */
    public boolean isResponsibleFor(Location location) {
        return location instanceof GeographicLocation;
    }

    public void changeLocationListenerTask(Location location, ListenerID taskID) {
        startLocationListenerTask(location,taskID);
        
    }

   

    public void stopLocationListenerTask(ListenerID handle) {
        operatingSystem.finishListener(handle);
        listenLocationMap.remove(handle);

    }

    public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
        operatingSystem=runtimeOperatingSystem;
        
        //operatingService.setTimeout(new ServiceTimeout(0) {
        //    public void handle() {
                init();

        //    }
        //});
    }
    private void init(){
        operatingSystem.registerSignalListener(LocationManager.class);
        operatingSystem.registerAccessListener(LocationManager_sync.class);
        PositioningServiceStub positioningService = new PositioningServiceStub(operatingSystem,positioningServiceID);
        positioningService.registerAtService();
        position=positioningService.getPositioningData().getPosition();
        //operatingService.registerAtService(positioningServiceID,PositioningService.class);
    }

    public ServiceID getServiceID() {
        return serviceID;
    }

    public void finish() {
        // TODO Auto-generated method stub
        
    }

    public Shape getShape() {
        // TODO Auto-generated method stub
        return null;
    }

    public void getParameters(Parameters parameters) {
        // TODO Auto-generated method stub
        
    }

    public void updatePositioningData(PositioningData info) {
        if (!position.equals(info.getPosition())){
            Position oldPosition=position;
            position=info.getPosition();
            Iterator iterator=listenLocationMap.keySet().iterator();
            while (iterator.hasNext()) {
                //TODO: das geht auch effizienter:
                // kürzeste distanzen der Location zur aktuellen position merken,
                //sortiert speichern, und aufsteigend die überprüfen die kleiner als distanzänderungen sind...
                // zum Beispiel...
                ListenerID handle = (ListenerID) iterator.next();
                GeographicLocation location=(GeographicLocation)listenLocationMap.get(handle);
                boolean reached_old=location.isInside(oldPosition);
                boolean reached_new=location.isInside(position);
                
                if (reached_old&&!reached_new){
                    operatingSystem.sendSignal(handle,
                            new LocationListener.LeftLocationSignal(location));
                    
                }
                if (!reached_old&&reached_new){
                    operatingSystem.sendSignal(handle,
                            new LocationListener.EnteredLocationSignal(location));
                }
                
            }
        }
        
        
        
    }

    public void removePositioningData() {
        // TODO Auto-generated method stub
        
    }
    
    public Location getCurrentLocation(Location locationExample) {
        return ((GeographicLocation)locationExample).newCenter(position);
        //return null;
    }







  

}
