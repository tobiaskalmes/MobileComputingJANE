/*****************************************************************************
 * 
 * RoomBeaconService.java
 * 
 * $Id: RoomBeaconService.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
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
import de.uni_trier.jane.service.locationManager.BeaconLocationService;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.simulation.global_knowledge.GlobalKnowledge;
import de.uni_trier.jane.simulation.service.*;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.*;

import java.util.Iterator;

public class RoomBeaconService implements Service, SimulationService {

    private double beaconRadius;
    private double beaconInterval;
    private SimulationOperatingSystem operatingSystem;
    private Shape shape;

    public RoomBeaconService(double beaconRadius,double beaconInterval) {
        this.beaconRadius=beaconRadius;
        this.beaconInterval=beaconInterval;
        //shape=new EllipseShape(operatingSystem.getDeviceID(),new Extent(beaconRadius,beaconRadius),Color.RED,false);
    }

    public ServiceID getServiceID() {
        // TODO Auto-generated method stub
        return null;
    }

    public void finish() {
        // TODO Auto-generated method stub

    }

    public Shape getShape() {
        return shape;
    }

    public void getParameters(Parameters parameters) {
        // TODO Auto-generated method stub

    }

    public void start(SimulationOperatingSystem simulationOperatingSystem) {
        operatingSystem=simulationOperatingSystem;
        sendBacon();
        operatingSystem.setTimeout(new ServiceTimeout(beaconInterval){
        
            public void handle() {
                operatingSystem.setTimeout(this);
                sendBacon();
                shape=new EllipseShape(operatingSystem.getDeviceID(),new Extent(beaconRadius*2,beaconRadius*2),Color.RED,false);
                operatingSystem.setTimeout(new ServiceTimeout(beaconInterval/2){
                    public void handle() {
                        shape=EmptyShape.getInstance();
                        
                    }
                });
                
            }
        
        });
    }

    private void sendBacon() {
        GlobalKnowledge globalKnowledge=operatingSystem.getGlobalKnowledge();
        Position myPosition=globalKnowledge.getTrajectory(operatingSystem.getDeviceID()).getPosition();
        DeviceIDIterator devices=globalKnowledge.getNodes().iterator();
        
        while (devices.hasNext()){
            DeviceID deviceID=devices.next();
            Position other=globalKnowledge.getTrajectory(deviceID).getPosition();
            double distance=myPosition.distance(other);
            if (distance<=beaconRadius){
                if (operatingSystem.hasService(deviceID,BeaconLocationService.class)){
                    ServiceID[] beaconServices=operatingSystem.getServiceIDs(deviceID,BeaconLocationService.class);
                    for (int i=0;i<beaconServices.length;i++){
                        operatingSystem.sendSignal(deviceID,beaconServices[i],
                            new BeaconLocationService.ReceiveBeaconSignal(operatingSystem.getDeviceID(),myPosition,distance/beaconRadius));
                    }
                }
            }
        }
        
    }



}
