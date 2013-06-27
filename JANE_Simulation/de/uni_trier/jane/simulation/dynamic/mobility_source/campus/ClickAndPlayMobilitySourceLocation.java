/*****************************************************************************
 * 
 * ClickAndPlayMobilitySource.java
 * 
 * $Id: ClickAndPlayMobilitySourceLocation.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $
 *  
 * Copyright (C) 2002-2004 Daniel Goergen and Hannes Frey and Johannes K. Lehnert
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
package de.uni_trier.jane.simulation.dynamic.mobility_source.campus;

import java.util.*;



import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.simulation.dynamic.mobility_source.*;
import de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource.ArrivalInfo;
import de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource.EnterInfo;
import de.uni_trier.jane.simulation.dynamic.position_generator.PositionGenerator;
import de.uni_trier.jane.simulation.dynamic.position_generator.RandomPositionGenerator;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.jane.visualization.shapes.*;


public class ClickAndPlayMobilitySourceLocation implements MobilitySource, ClickAndPlayMobilitySource {
        private final static String VERSION = "$Id: ClickAndPlayMobilitySourceLocation.java,v 1.1 2007/06/25 07:24:33 srothkugel Exp $";
        

        
        private HashMap deviceEntityMap;
        
        private static double UPDATE_DELTA = 0.05;
        private static final double START_TIME = 0;
        
        

        

         private Output output;

         

         


         private DeviceLocation deviceLocation;
         private double speed;
         private double sendingRange;
         private int deviceCount;



         private Rectangle extent;



         private int count;



        private ArrivalInfo[] arrivalInfos;



        private DeviceID[] deviceIDs;



        private PositionDeviceMap positionDeviceMap;
        
        private class DeviceEntity{
            private Position currentPosition;
            private Position nextPosition;
            private double currentTime;
            
            /**
             * 
             * @param startPosition
             */
            public DeviceEntity(Position startPosition){
                currentPosition=startPosition;
                nextPosition=startPosition;
                currentTime=START_TIME;
            }
        
            /**
             * @param nextPosition The nextPosition to set.
             */
            public void setNextPosition(Position nextPosition) {
                currentPosition=this.nextPosition;
                this.nextPosition = nextPosition;
            }
            
            /** 
             * @return
             */
            public boolean hasMoved(){
                return !currentPosition.equals(nextPosition);
            }
        
            /**
             * @param output 
             * @return
             */
            public ArrivalInfo getNextArrivalInfo(DeviceID deviceID) {
                if (hasMoved()){
                     saveOutput(deviceID,currentPosition,currentTime);
                    currentTime+=currentPosition.distance(nextPosition)/speed;
                    currentPosition=nextPosition;
                     
                     saveOutput(deviceID,nextPosition,currentTime);
                }else{
                    currentTime+=UPDATE_DELTA;
                }
                return new ArrivalInfo(nextPosition,currentTime);
            }

            /**
             * @return
             */
            public Position getCurrentPosition() {
                
                return currentPosition;
            }
        }
         
    /**
     * 
     * Constructor for class <code>ClickAndPlayMobilitySourceLocation</code>
     * @param deviceLocation
     * @param speed
     * @param sendingRange
     * @param deviceCount
     * @param updateDelta
     */
    public ClickAndPlayMobilitySourceLocation(DeviceLocation deviceLocation, double speed, double sendingRange, int deviceCount, double updateDelta) {
        UPDATE_DELTA=updateDelta;
        this.deviceLocation=deviceLocation;
        this.speed=speed;
        this.sendingRange=sendingRange;
        this.deviceCount=deviceCount;
         deviceIDs=new DeviceID[deviceCount];
        double[] enterTime=new double[deviceCount];
        double start=0;
        double inc=0.00001;
        for (int i=1;i<=deviceCount;i++){
            deviceIDs[i-1]=new SimulationDeviceID(i);
            enterTime[i-1]=start;
            start+=inc;
        }
        arrivalInfos=deviceLocation.getInitialArrivalInfos(deviceIDs,enterTime);
        positionDeviceMap=new PositionDeviceMap();
        deviceEntityMap=new HashMap();
    }
	

	
    /**
     * TODO Comment method
     */
    private void saveOutput() {
        if (output!=null){
            output.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            output.println("<!DOCTYPE scripted_mobility SYSTEM \"CommandList.dtd\">");
            output.println("<COMMANDLIST BOTTOMLEFTX=\"0.0\" BOTTOMLEFTY=\"0.0\" TOPRIGHTX=\""+extent.getHeight()+"\" TOPRIGHTY=\""+extent.getWidth()+"\">");
        }
        
    }
    
    /**
     * TODO Comment method
     * @param enterInfo
     */
    private void saveOutput(EnterInfo enterInfo) {
        if (output!=null){
            output.println("<ENTER TIME=\""+enterInfo.getArrivalInfo().getTime()+"\" ADDRESS=\""+
                    enterInfo.getAddress()+
                    "\" RADIUS=\""+
                    enterInfo.getSendingRadius()+
                    "\" ENDTIME=\""+
                    enterInfo.getArrivalInfo().getTime()+
                    "\" STARTX=\""+
                    enterInfo.getArrivalInfo().getPosition().getX()+
                    "\" STARTY=\""+
                    enterInfo.getArrivalInfo().getPosition().getY()+
                    "\" ENDX=\""+
                    enterInfo.getArrivalInfo().getPosition().getX()+
                    "\" ENDY=\""+
                    enterInfo.getArrivalInfo().getPosition().getY()+
                    "\"/>");
        }
        
    }
    
    /**
     * TODO Comment method
     * @param nextPosition
     * @param currentTime
     */
    public void saveOutput(DeviceID deviceID,Position nextPosition, double currentTime) {
        if (output!=null){
            output.println("<ARRIVAL TIME=\"0.0\" ADDRESS=\""+
                    deviceID+
                    "\" ENDTIME=\""+
                    currentTime+
                    "\" STARTX=\"0.0\" STARTY=\"0.0\" ENDX=\""+
                    nextPosition.getX()+
                    "\" ENDY=\""+
                    nextPosition.getY()+
                    "\"/>");
            output.flush();
        }
        
    }
    
	public boolean hasNextEnterInfo() {
		return (deviceCount>count);
//		return initMobilitySource.hasNextEnterInfo();
	}

	
	public EnterInfo getNextEnterInfo() {
        EnterInfo enterInfo=new EnterInfo(deviceIDs[count],sendingRange,arrivalInfos[count]);
        count++;
        
		Position position=enterInfo.getArrivalInfo().getPosition();//new Position(xDistribution.getNext(0),yDistribution.getNext(0));
		DeviceID address=enterInfo.getAddress();//new SimulationDeviceID(count);
		positionDeviceMap.addDevice(address,position);
		deviceEntityMap.put(address,new DeviceEntity(position));
		//count++;
		//EnterInfo enterInfo = new EnterInfo(address,sendingRangeDistribution.getNext(),new ArrivalInfo(position,START_TIME));
        saveOutput(enterInfo);
        return enterInfo;
	}

	

	

    public boolean hasNextArrivalInfo(DeviceID address) {	
		return true;
	}

	
	public ArrivalInfo getNextArrivalInfo(DeviceID address) {
		DeviceEntity deviceEntity=(DeviceEntity)deviceEntityMap.get(address);
		
		return deviceEntity.getNextArrivalInfo(address);
	}

	
	public Rectangle getRectangle() {
		return deviceLocation.getRectangle();
	}

	
	public Shape getShape() {
		return deviceLocation.getShape();
	}


	public int getTotalDeviceCount() {
		return deviceCount;
	}
	
	/**
	 * Changes the position of a device
	 * @param device		the device address of the device
	 * @param newPosition	the new position of the device
	 */
	public void setPosition(DeviceID device, Position newPosition){
		DeviceEntity deviceEntity=(DeviceEntity)deviceEntityMap.get(device);
		Position currentPosition=deviceEntity.getCurrentPosition();
        if (deviceLocation.getRectangle().contains(newPosition)){
            newPosition=deviceLocation.getDevicePath(device,newPosition,currentPosition)[0];
        }else if (deviceLocation.getRectangle().contains(currentPosition)){
        	deviceLocation.removeDevice(device,currentPosition);
        }
        
		positionDeviceMap.removeDevice(device);
		positionDeviceMap.addDevice(device,newPosition);
		
		deviceEntity.setNextPosition(newPosition);
		
		
	}
	
	/**
	 * Returns all addresses of (non moving) devices within a rectangle 
	 * @param rectangle		the rectangle
	 * @return	adresses of all devices within the given rectangle
	 */
	public DeviceID[] getAddress(Rectangle rectangle){
		return positionDeviceMap.getDevices(rectangle);
	}

    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource#getTerminalCondition(de.uni_trier.jane.basetypes.Clock)
     */
    public Condition getTerminalCondition(Clock clock) {

        return null;
    }

	public double getMinimumTransmissionRange() {
		return sendingRange;
	}

	public double getMaximumTransmissionRange() {
		return sendingRange;
	}

}
