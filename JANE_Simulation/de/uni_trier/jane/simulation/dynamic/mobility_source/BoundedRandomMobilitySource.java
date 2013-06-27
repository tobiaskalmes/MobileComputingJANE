/*****************************************************************************
 * 
 * BoundedRandomMobilitySource.java
 * 
 * $Id: BoundedRandomMobilitySource.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
import de.uni_trier.jane.simulation.dynamic.position_generator.*;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This class is nearly identical to the class <code>RandomMobilitySource</code>.
 * The only difference is, that it only allows nearby positions (determined by a
 * movement radius) as the next destination of a mobile device.
 */
public class BoundedRandomMobilitySource implements MobilitySource {

	private final static String VERSION = "$Id: BoundedRandomMobilitySource.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";
	
	private int numberOfDevices;
	private double movementRadius;
	private IntervalGenerator lifeTimeDistribution;
	private PositionGenerator positionGenerator;
	private ContinuousDistribution pauseDistribution;
	private ContinuousDistribution sendingRadiusDistribution;
	private ContinuousDistribution speedDistribution;
	private int currentAddress;
	private Map addressMobilityInfoMap;

	/**
	 * Constructs a new <code>BoundedRandomMobilitySource</code>.
	 * @param numberOfDevices the number of devices this mobility source should provide
	 * @param movementRadius the radius of the area, where the next destination can be choosen.
	 * @param lifeTimeDistribution an interval generator providing the enter and exit time of the devices
	 * @param positionGenerator a position generator providing the positions of the destinations the devices walk to
	 * @param pauseDistribution a continuous distribution providing the pause times of the devices
	 * @param sendingRadiusDistribution a continuous distribution providing the sending radii of the devices. 
	 *                                  Please note that this distribution is only called with time 0.
	 * @param speedDistribution a continuous distribution providing the speed of the devices
	 */
	public BoundedRandomMobilitySource(int numberOfDevices, double movementRadius, IntervalGenerator lifeTimeDistribution, PositionGenerator positionGenerator, ContinuousDistribution pauseDistribution, ContinuousDistribution sendingRadiusDistribution, ContinuousDistribution speedDistribution) {
		this.numberOfDevices = numberOfDevices;
		this.movementRadius = movementRadius;
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
		currentAddress++;
		if(currentAddress > numberOfDevices) {
			throw new IllegalStateException("The maximum number of devices is exceeeded.");
		}
		DeviceID address = new SimulationDeviceID(currentAddress+1);
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
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getRectangle()
	 */
	public Rectangle getRectangle() {
		return positionGenerator.getRectangle(); 
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getShape()
	 */
	public Shape getShape() {
		//return shape;
		Rectangle rectangle = positionGenerator.getRectangle();
		ShapeCollection shape =new ShapeCollection();
		shape.addShape(new RectangleShape(rectangle.getCenter(), rectangle.getExtent(), Color.LIGHTGREY, true),new Position (0,0));
		shape.addShape(positionGenerator.getShape(),new Position(0,0));
		return shape;		
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
				Position origPosition;
				do {
					origPosition = positionGenerator.getNext(arrivalInfo.getTime());
				} while(arrivalInfo.getPosition().distance(origPosition) > movementRadius);
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

