/*****************************************************************************
 * 
 * ClickAndPlayMobilitySource.java
 * 
 * $Id: ClickAndPlayMobilitySourceSimple.java,v 1.2 2007/08/27 10:20:28 srothkugel Exp $
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
package de.uni_trier.jane.simulation.dynamic.mobility_source;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.simulation.*;
import de.uni_trier.jane.simulation.dynamic.position_generator.*;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;
import de.uni_trier.jane.simulation.parametrized.parameters.initialization.*;
import de.uni_trier.jane.simulation.parametrized.parameters.object.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * 
 * This <code>MobilitySource</code> can be used for the <code>ClickAndPlaySimulationFrame</code>
 * Devices are moved by selecting them with the mouse within the <code>ClickAndPlaySimulationFrame</code>
 */
public class ClickAndPlayMobilitySourceSimple extends MobilitySourceBase implements ClickAndPlayMobilitySource {

	
	
	public final static String VERSION = "$Id: ClickAndPlayMobilitySourceSimple.java,v 1.2 2007/08/27 10:20:28 srothkugel Exp $";

	
	public static final InitializationObjectElement MOBILITY_SOURCE_CLICK_AND_PLAY_OBJECT = new InitializationObjectElement("clickAndPlay") {
		public Object getValue(InitializationContext initializationContext, SimulationParameters simulationParameters) {
			int n = NUMBER_OF_NODES.getValue(initializationContext);
			double w = AREA_WIDTH.getValue(initializationContext);
			double h = AREA_HEIGHT.getValue(initializationContext);
			double r = SENDING_RADIUS.getValue(initializationContext);
			Extent extent = new Extent(w, h);
			DistributionCreator distributionCreator = simulationParameters.getDistributionCreator();
			ContinuousDistribution speed = distributionCreator.getContinuousUniformDistribution(10.0, 20.0);
			ContinuousDistribution sendingRange = distributionCreator.getContinuousDeterministicDistribution(r);
		    return new ClickAndPlayMobilitySourceSimple(n, extent, speed, sendingRange, distributionCreator);
		}
		public Parameter[] getParameters() {
			return new Parameter[] { NUMBER_OF_NODES, AREA_WIDTH, AREA_HEIGHT, SENDING_RADIUS };
		}
	};

	
	public static final InitializationElement MOBILITY_SOURCE_CLICK_AND_PLAY = new InitializationElement("clickAndPlay") {
		public void initialize(InitializationContext initializationContext, SimulationParameters simulationParameters) {
			int n = NUMBER_OF_NODES.getValue(initializationContext);
			double w = AREA_WIDTH.getValue(initializationContext);
			double h = AREA_HEIGHT.getValue(initializationContext);
			double r = SENDING_RADIUS.getValue(initializationContext);
			Extent extent = new Extent(w, h);
			DistributionCreator distributionCreator = simulationParameters.getDistributionCreator();
			ContinuousDistribution speed = distributionCreator.getContinuousUniformDistribution(10.0, 20.0);
			ContinuousDistribution sendingRange = distributionCreator.getContinuousDeterministicDistribution(r);
		    ClickAndPlayMobilitySourceSimple mobilitySource=new ClickAndPlayMobilitySourceSimple(n, extent, speed, sendingRange, distributionCreator);
		    simulationParameters.setMobilitySource(mobilitySource);
		}
		public Parameter[] getParameters() {
			return new Parameter[] { NUMBER_OF_NODES, AREA_WIDTH, AREA_HEIGHT, SENDING_RADIUS };
		}
	};


	private PositionDeviceMap positionDeviceMap;
	private HashMap deviceEntityMap;
	
	private static final double UPDATE_DELTA = 0.05;
	private static final double START_TIME = 0;
	
	
	private ContinuousDistribution speedDistribution;
	

    private Output output;

    private MobilitySource initMobilitySource;

    private Extent extent;
	
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
				currentTime+=currentPosition.distance(nextPosition)/speedDistribution.getNext(currentTime);
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
	 * Constructor for class<code>ClickAndPlayMobilitySource</code>
	 * @param numberDevices				the amount of devices
	 * @param extent					the extent whithin devices should be initially placed 
	 * @param speedDistribution			the distribution for selecting the moving speed of a device, changes for each move
	 * @param sendingRangeDistribution	the distribution for selecting the initial sending range of a device
	 * @param distributionCreator		the <code>DistributionCreator</code>
	 */
	public ClickAndPlayMobilitySourceSimple(int numberDevices, Extent extent, ContinuousDistribution speedDistribution, ContinuousDistribution sendingRangeDistribution,DistributionCreator distributionCreator,Output output) {
		
        this(FixedNodes.createRandom(numberDevices,
                new RandomPositionGenerator(distributionCreator.getContinuousUniformDistribution(0,extent.getWidth()),
                                            distributionCreator.getContinuousUniformDistribution(0,extent.getHeight())),
                                            sendingRangeDistribution,EmptyShape.getInstance()),speedDistribution,output);
	}
    
    
    
    /**
     * 
     * Constructor for class <code>ClickAndPlayMobilitySourceSimple</code>
     * @param initMobilitySource
     * @param speedDistribution
     */
    public ClickAndPlayMobilitySourceSimple(MobilitySource initMobilitySource,ContinuousDistribution speedDistribution){
        this(initMobilitySource,speedDistribution,null);
    }
    
    /**
     * 
     * Constructor for class <code>ClickAndPlayMobilitySourceSimple</code>
     * @param initMobilitySource
     * @param speedDistribution
     * @param output
     */
    public ClickAndPlayMobilitySourceSimple(MobilitySource initMobilitySource,ContinuousDistribution speedDistribution, Output output) {
        if (speedDistribution.getInfimum()<=0) throw new IllegalArgumentException("Speed must be greater than 0");
        this.speedDistribution=speedDistribution;
        positionDeviceMap=new PositionDeviceMap();
        deviceEntityMap=new HashMap();
        this.initMobilitySource=initMobilitySource;
        this.output=output;
        extent=initMobilitySource.getRectangle().getExtent();
    }
    
    /**
     * 
     * Constructor for class <code>ClickAndPlayMobilitySourceSimple</code>
     * @param initMobilitySource
     * @param minSpeed
     * @param maxSpeed
     * @param output
     * @param distributionCreator
     */
    public ClickAndPlayMobilitySourceSimple(MobilitySource initMobilitySource,double minSpeed, double  maxSpeed, Output output, DistributionCreator distributionCreator) {
        this(initMobilitySource,distributionCreator.getContinuousUniformDistribution(minSpeed,maxSpeed),output);
    }
    


    /**
     * Constructor for class<code>ClickAndPlayMobilitySource</code>
     * @param numberDevices             the amount of devices
     * @param extent                    the extent whithin devices should be initially placed 
     * @param speedDistribution         the distribution for selecting the moving speed of a device, changes for each move
     * @param sendingRangeDistribution  the distribution for selecting the initial sending range of a device
     * @param distributionCreator       the <code>DistributionCreator</code>
     */
    public ClickAndPlayMobilitySourceSimple(int numberDevices, Extent extent, ContinuousDistribution speedDistribution, ContinuousDistribution sendingRangeDistribution,DistributionCreator distributionCreator ) {
        this(numberDevices,extent,speedDistribution,sendingRangeDistribution,distributionCreator,null);
    }
    
	/**
     * 
     * Constructor for class <code>ClickAndPlayMobilitySourceSimple</code>
     * @param numberDevices
     * @param extent
     * @param minSpeed
     * @param maxSpeed
     * @param minSendingRange
     * @param maxSendingRange
     * @param distributionCreator
     * @param output
	 */
	public ClickAndPlayMobilitySourceSimple(int numberDevices, Extent extent, double minSpeed, double maxSpeed, double minSendingRange, double maxSendingRange,DistributionCreator distributionCreator, Output output ) {
        this(numberDevices,extent,
                distributionCreator.getContinuousUniformDistribution(minSpeed,maxSpeed),
                distributionCreator.getContinuousUniformDistribution(minSendingRange,maxSendingRange),
                distributionCreator,output);
//	    if (minSpeed<=0) throw new IllegalArgumentException("Speed must be greater than 0");
//	    if (minSpeed>maxSpeed)throw new IllegalArgumentException("maximum speed must be greater or equal than minimum speed");
//	    if (minSendingRange<=0) throw new IllegalArgumentException("Sending radius must be greater 0");
//	    if (minSendingRange>maxSendingRange)throw new IllegalArgumentException("maximum sending radius must be greater or equal than minimum radius");
//		speedDistribution=distributionCreator.getContinuousUniformDistribution(minSpeed,maxSpeed);
//		sendingRangeDistribution=distributionCreator.getContinuousUniformDistribution(minSendingRange,maxSendingRange);
//		
//		this.numberDevices=numberDevices;
//		this.extent=extent;
//		count =0;
//		xDistribution=distributionCreator.getDiscreteUniformDistribution(0,(int)extent.getHeight());
//		yDistribution=distributionCreator.getDiscreteUniformDistribution(0,(int)extent.getWidth());
//		positionDeviceMap=new PositionDeviceMap();
//		deviceEntityMap=new HashMap();
//        this.output=output;
//        saveOutput();
	}

    /**
     * 
     * Constructor for class ClickAndPlayMobilitySource 
     *
     * @param numberDevices
     * @param extent
     * @param minSpeed
     * @param maxSpeed
     * @param minSendingRange
     * @param maxSendingRange
     * @param distributionCreator
     */
    public ClickAndPlayMobilitySourceSimple(int numberDevices, Extent extent, double minSpeed, double maxSpeed, double minSendingRange, double maxSendingRange,DistributionCreator distributionCreator ) {
        this(numberDevices,extent,minSpeed,maxSpeed,minSendingRange,maxSendingRange,distributionCreator,null);
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
		
		return initMobilitySource.hasNextEnterInfo();
	}

	
	public EnterInfo getNextEnterInfo() {
        EnterInfo enterInfo= initMobilitySource.getNextEnterInfo();
        
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
		return new Rectangle(extent);
	}

	
	public Shape getShape() {
		return initMobilitySource.getShape();
	}


	public int getTotalDeviceCount() {
		return initMobilitySource.getTotalDeviceCount();
	}
	
	/**
	 * Changes the position of a device
	 * @param device		the device address of the device
	 * @param newPosition	the new position of the device
	 */
	public void setPosition(DeviceID device, Position newPosition){
		DeviceEntity deviceEntity=(DeviceEntity)deviceEntityMap.get(device);
		Position currentPosition=deviceEntity.getCurrentPosition();
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
		return initMobilitySource.getMinimumTransmissionRange();
	}



	public double getMaximumTransmissionRange() {
		return initMobilitySource.getMaximumTransmissionRange();
	}

    
}
