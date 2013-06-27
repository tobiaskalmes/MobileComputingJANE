/*****************************************************************************
 * 
 * RandomMobilitySource.java
 * 
 * $Id: RandomMobilitySource.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
 *  
 * Copyright (C) 2002 Hannes Frey and Johannes K. Lehnert
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

import com.sun.jndi.cosnaming.IiopUrl.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.simulation.*;
import de.uni_trier.jane.simulation.dynamic.position_generator.*;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;
import de.uni_trier.jane.simulation.parametrized.parameters.initialization.*;
import de.uni_trier.jane.simulation.parametrized.parameters.object.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * MobilitySource implementation where devices move randomly between positions provided
 * by a position generator. Devices walk to a new destination, pause for some time and 
 * start walking again. The sending radius, speed and pause duration of the devices are 
 * provided by random distributions.
 */
public class RandomMobilitySource extends MobilitySourceBase {

	/**
	 * The CVS version of this class.
	 */
	public final static String VERSION = "$Id: RandomMobilitySource.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";

	private int numberOfDevices;
	private IntervalGenerator lifeTimeDistribution;
	private PositionGenerator positionGenerator;
	private ContinuousDistribution pauseDistribution;
	private ContinuousDistribution sendingRadiusDistribution;
	private ContinuousDistribution speedDistribution;
	private int currentAddress;
	private Map addressMobilityInfoMap;

	public static final InitializationObjectElement SIMPLE_RANDOM_WAYPOINT_OBJECT = new InitializationObjectElement("simpleRandomWaypoint") {
		public Object getValue(InitializationContext initializationContext, SimulationParameters simulationParameters) {
			DistributionCreator distributionCreator = simulationParameters.getDistributionCreator();
			int deviceCount = NUMBER_OF_NODES.getValue(initializationContext);
			double lifeTime = 60.0 * 60.0 * 24.0 * 365.0; // life time of one year :)
			double width = AREA_WIDTH.getValue(initializationContext);
			double height = AREA_HEIGHT.getValue(initializationContext);
			double pauseTime = 0.0;
			double sendingRadius = SENDING_RADIUS.getValue(initializationContext);
			double speed = MOVING_SPEED.getValue(initializationContext);
			return createRandomWaypointMobilitySource(distributionCreator, 
					deviceCount, lifeTime, width, height, pauseTime, sendingRadius, sendingRadius, speed, speed);
		}
		public Parameter[] getParameters() {
			return new Parameter[] { NUMBER_OF_NODES, AREA_WIDTH, AREA_HEIGHT, SENDING_RADIUS, MOVING_SPEED };
		}
	};

	/**
	 * This initialization element can be used to create a simple instance of this mobility source.
	 */
	public static final InitializationElement SIMPLE_RANDOM_WAYPOINT = new InitializationElement("simpleRandomWaypoint") {
		public void initialize(InitializationContext initializationContext, SimulationParameters simulationParameters) {
			DistributionCreator distributionCreator = simulationParameters.getDistributionCreator();
			int deviceCount = NUMBER_OF_NODES.getValue(initializationContext);
			double lifeTime = 60.0 * 60.0 * 24.0 * 365.0; // life time of one year :)
			double width = AREA_WIDTH.getValue(initializationContext);
			double height = AREA_HEIGHT.getValue(initializationContext);
			double pauseTime = 0.0;
			double sendingRadius = SENDING_RADIUS.getValue(initializationContext);
			double speed = MOVING_SPEED.getValue(initializationContext);
			MobilitySource mobilitySource = createRandomWaypointMobilitySource(distributionCreator, 
					deviceCount, lifeTime, width, height, pauseTime, sendingRadius, sendingRadius, speed, speed);
			simulationParameters.setMobilitySource(mobilitySource);
		}
		public Parameter[] getParameters() {
			return new Parameter[] { NUMBER_OF_NODES, AREA_WIDTH, AREA_HEIGHT, SENDING_RADIUS, MOVING_SPEED };
		}
	};

	/**
	 * Create an instance of this class by using the given parameters. The resulting
	 * graph has the unit graph property, i.e. all devices have the same sending radius.
	 * @param distributionCreator the source of random
	 * @param deviceCount the number of devices
	 * @param lifeTime the life time of a device
	 * @param width the width of the plane
	 * @param height the height of the plane
	 * @param pauseTime the pause time of a device
	 * @param minSendingRadius the minimum sending radius of a device
     * @param maxSendingRadius the maximum sending radius of a device
	 * @param minSpeed the minimum speed of a device
	 * @param maxSpeed the maximum speed of a device
	 * @return the mobility source instance
	 */
	public static MobilitySource createRandomWaypointMobilitySource(DistributionCreator distributionCreator, 
            int deviceCount,
            double lifeTime, 
            double width, 
            double height,
            double pauseTime, 
            double minSendingRadius,
            double maxSendingRadius, 
            double minSpeed, 
            double maxSpeed) {
        return createRandomWaypointMobilitySource(distributionCreator,deviceCount,0, lifeTime,new Rectangle(0,0,width,height),
                    pauseTime,minSendingRadius,maxSendingRadius,minSpeed,maxSpeed);
    }   
    
        
        /**
         * Create an instance of this class by using the given parameters. The resulting
         * graph has the unit graph property, i.e. all devices have the same sending radius.
         * @param distributionCreator the source of random
         * @param deviceCount the number of devices
         * @param lifeTime the life time of a device
         * @param plane  the plane
         * @param pauseTime the pause time of a device
         * @param minSendingRadius the minimum sending radius of a device
         * @param maxSendingRadius the maximum sending radius of a device
         * @param minSpeed the minimum speed of a device
         * @param maxSpeed the maximum speed of a device
         * @return the mobility source instance
         */
        public static MobilitySource createRandomWaypointMobilitySource(DistributionCreator distributionCreator, 
                int deviceCount,
                double enterTime,
                double lifeTime, 
                Rectangle plane,
                double pauseTime, 
                double minSendingRadius,
                double maxSendingRadius, 
                double minSpeed, 
                double maxSpeed) {        
		DoubleMapping dm1 = new ConstantDoubleMapping(enterTime);
		DoubleMapping dm2 = new ConstantDoubleMapping(lifeTime);
		ContinuousDistribution cd1 = distributionCreator.getContinuousDeterministicDistribution(dm1);
		ContinuousDistribution cd2 = distributionCreator.getContinuousDeterministicDistribution(dm2);
		IntervalGenerator lifeTimeDistribution = new RandomIntervalGenerator(cd1, cd2);
        
		cd1 = distributionCreator.getContinuousUniformDistribution(plane.getBottomLeft().getX(), plane.getTopRight().getX());
		cd2 = distributionCreator.getContinuousUniformDistribution(plane.getBottomLeft().getY(), plane.getTopRight().getY());
		PositionGenerator positionGenerator = new RandomPositionGenerator(cd1, cd2);
		DoubleMapping dm = new ConstantDoubleMapping(1.0 / pauseTime);
		ContinuousDistribution pauseDistribution = distributionCreator.getExponentialDistribution(dm);
		
		ContinuousDistribution sendingRadiusDistribution = distributionCreator.getContinuousUniformDistribution(minSendingRadius,maxSendingRadius);
		dm1 = new ConstantDoubleMapping(minSpeed);
		dm2 = new ConstantDoubleMapping(maxSpeed);
		ContinuousDistribution speedDistribution = distributionCreator.getContinuousUniformDistribution(dm1, dm2);
		return new RandomMobilitySource(deviceCount, lifeTimeDistribution, positionGenerator, pauseDistribution, sendingRadiusDistribution, speedDistribution);
	}
    
    /**
     * 
     * TODO Comment method
     * @param distributionCreator
     * @param deviceCount
     * @param lifeTime
     * @param xWidth
     * @param yWidth
     * @param zWidth
     * @param pauseTime
     * @param minSendingRadius
     * @param maxSendingRadius
     * @param minSpeed
     * @param maxSpeed
     * @return
     */
    
    public static MobilitySource createRandomWaypointMobilitySource(
                DistributionCreator distributionCreator, 
                int deviceCount, 
                double lifeTime, 
                double xWidth, 
                double yWidth,
                double zWidth, 
                double pauseTime, 
                double minSendingRadius, 
                double maxSendingRadius,
                double minSpeed, 
                double maxSpeed) {
        
        ContinuousDistribution cd1 = distributionCreator.getContinuousDeterministicDistribution(0);
        ContinuousDistribution cd2 = distributionCreator.getContinuousDeterministicDistribution(lifeTime);
        IntervalGenerator lifeTimeDistribution = new RandomIntervalGenerator(cd1, cd2);
        
        cd1 = distributionCreator.getContinuousUniformDistribution(0,xWidth);
        cd2 = distributionCreator.getContinuousUniformDistribution(0,yWidth);
        ContinuousDistribution cd3 = distributionCreator.getContinuousUniformDistribution(0,zWidth);
        PositionGenerator positionGenerator = new RandomPositionGenerator(cd1, cd2, cd3);
        
        ContinuousDistribution pauseDistribution = distributionCreator.getExponentialDistribution(1.0/pauseTime);
        
        ContinuousDistribution sendingRadiusDistribution = distributionCreator.getContinuousUniformDistribution(minSendingRadius,maxSendingRadius);
        
        ContinuousDistribution speedDistribution = distributionCreator.getContinuousUniformDistribution(minSpeed, maxSpeed);
        return new RandomMobilitySource(deviceCount, lifeTimeDistribution, positionGenerator, pauseDistribution, sendingRadiusDistribution, speedDistribution);
    }

	/**
	 * Constructs a new RandomMobilitySource.
	 * @param numberOfDevices the number of devices this mobility source should provide
	 * @param lifeTimeDistribution an interval generator providing the enter and exit time of the devices
	 * @param positionGenerator a position generator providing the positions of the destinations the devices walk to
	 * @param pauseDistribution a continuous distribution providing the pause times of the devices
	 * @param sendingRadiusDistribution a continuous distribution providing the sending radii of the devices. 
	 *                                  Please note that this distribution is only called with time 0.
	 * @param speedDistribution a continuous distribution providing the speed of the devices
	 */
	public RandomMobilitySource(int numberOfDevices, IntervalGenerator lifeTimeDistribution, PositionGenerator positionGenerator, ContinuousDistribution pauseDistribution, ContinuousDistribution sendingRadiusDistribution, ContinuousDistribution speedDistribution) {
		this.numberOfDevices = numberOfDevices;
		this.lifeTimeDistribution = lifeTimeDistribution;
		this.positionGenerator = positionGenerator;
		this.pauseDistribution = pauseDistribution;
		this.sendingRadiusDistribution = sendingRadiusDistribution;
		this.speedDistribution = speedDistribution;
		currentAddress = 0;
		addressMobilityInfoMap = new HashMap();
		if(numberOfDevices <= 0) {
			throw new IllegalArgumentException("The number of devices has to be at least one.");
		}
		if(lifeTimeDistribution.getSupremum().getFirst() < 0) {
			throw new IllegalArgumentException("A negative arrival time is not allowed.");
		}
		if(pauseDistribution.getInfimum() < 0) {
			throw new IllegalArgumentException("A negative pause interval is not allowed.");
		}
		if(sendingRadiusDistribution.getInfimum() < 0) {
			throw new IllegalArgumentException("A negative sending radius is not allowed.");
		}
		if(speedDistribution.getInfimum() <= 0) {
			throw new IllegalArgumentException("The device speed has to be positive.");
		}
	}
	
	
	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#hasNextEnterInfo()
	 */
	public boolean hasNextEnterInfo() {
		return currentAddress < numberOfDevices;
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getNextEnterInfo()
	 */
	public EnterInfo getNextEnterInfo() {
		
		if(currentAddress >= numberOfDevices) {
			throw new IllegalStateException("The maximum number of devices is exceeeded.");
		}
		DeviceID address = new SimulationDeviceID(currentAddress+1);
		currentAddress++;
		MobilityInfo mobilityInfo = new MobilityInfo();
		addressMobilityInfoMap.put(address, mobilityInfo);
		return new EnterInfo(address, sendingRadiusDistribution.getNext(0), mobilityInfo.getArrivalInfo());
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#hasNextArrivalInfo(Address)
	 */
	public boolean hasNextArrivalInfo(DeviceID address) {
		return getMobilityInfo(address).hasNext();
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getNextArrivalInfo(Address)
	 */
	public ArrivalInfo getNextArrivalInfo(DeviceID address) {
		return getMobilityInfo(address).calculateNext();
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getTotalDeviceCount()
	 */
	public int getTotalDeviceCount() {
		return numberOfDevices;
	}

	private MobilityInfo getMobilityInfo(DeviceID address) {
		MobilityInfo mobilityInfo = (MobilityInfo)addressMobilityInfoMap.get(address);
		if(mobilityInfo == null) {
			throw new IllegalArgumentException("The given address does not exist.");
		}
		return mobilityInfo;
	}

	private class MobilityInfo {
		private double exitTime;
		private boolean isMoving;
		private ArrivalInfo arrivalInfo;
		public MobilityInfo() {
			DoubleInterval lifeTime = lifeTimeDistribution.getNext(0);
			exitTime = lifeTime.getLast();
			isMoving = false;
			arrivalInfo = new ArrivalInfo(positionGenerator.getNext(0), lifeTime.getFirst());
		}
		public ArrivalInfo getArrivalInfo() {
			return arrivalInfo;
		}
		public boolean hasNext() {
			return arrivalInfo.getTime() < exitTime;
		}
		public ArrivalInfo calculateNext() {
			if(exitTime <= arrivalInfo.getTime()) {
				throw new IllegalStateException("There is no more mobility information.");
			}
			double time;
			if(isMoving) {
				Position position = arrivalInfo.getPosition();
				time = arrivalInfo.getTime() + pauseDistribution.getNext(arrivalInfo.getTime());
				if(time > exitTime) {
					time = exitTime;
				}
				arrivalInfo = new ArrivalInfo(position, time);
			}
			else {
				Position origPosition = positionGenerator.getNext(arrivalInfo.getTime());
				MutablePosition pos = new MutablePosition(origPosition);
				pos.sub(arrivalInfo.getPosition());
				double delta = pos.length() / speedDistribution.getNext(arrivalInfo.getTime());
				time = arrivalInfo.getTime() + delta;
				if (time > exitTime) {
					time = exitTime;
					pos.scale((time - arrivalInfo.getTime()) / delta);
					pos.add(arrivalInfo.getPosition());
					arrivalInfo = new ArrivalInfo(pos.getPosition(), time);
				} else {
					arrivalInfo = new ArrivalInfo(origPosition, time);
				}
			}
			isMoving = ! isMoving;			
			return arrivalInfo;
		}
	}
	
	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getRectangle()
	 */
	public Rectangle getRectangle() {
		return positionGenerator.getRectangle();
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getShape()
	 */
	public Shape getShape() {
		Rectangle rectangle = positionGenerator.getRectangle();
		ShapeCollection shape =new ShapeCollection();
		shape.addShape(new RectangleShape(rectangle.getCenter(), rectangle.getExtent(), Color.WHITE, true),new Position (0,0));
		shape.addShape(positionGenerator.getShape(),new Position(0,0));
		return shape;
	}

    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource#getTerminalCondition(de.uni_trier.jane.basetypes.Clock)
     */
    public Condition getTerminalCondition(Clock clock) {
        // TODO Auto-generated method stub
        return null;
    }

	public double getMinimumTransmissionRange() {
		return sendingRadiusDistribution.getInfimum();
	}

	public double getMaximumTransmissionRange() {
		return sendingRadiusDistribution.getSupremum();
	}

}

