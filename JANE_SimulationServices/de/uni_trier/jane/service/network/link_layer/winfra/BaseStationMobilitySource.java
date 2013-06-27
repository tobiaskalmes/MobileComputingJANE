/*
 * Created on 27.06.2005
 */
package de.uni_trier.jane.service.network.link_layer.winfra;

import java.util.HashMap;

import de.uni_trier.jane.basetypes.Clock;
import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.basetypes.Extent;
import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.basetypes.Rectangle;
import de.uni_trier.jane.basetypes.SimulationDeviceID;
import de.uni_trier.jane.random.ContinuousDistribution;
import de.uni_trier.jane.random.DiscreteDistribution;
import de.uni_trier.jane.random.DistributionCreator;
import de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource;
import de.uni_trier.jane.simulation.dynamic.mobility_source.PositionDeviceMap;
import de.uni_trier.jane.simulation.kernel.Condition;
import de.uni_trier.jane.visualization.shapes.EmptyShape;
import de.uni_trier.jane.visualization.shapes.Shape;

/**
 * @author christian.hiedels
 *
 * This Mobility Source is used by BaseStations of the Winfra Network.
 * Note that Devices using this MobilitySource won't be able to move.
 * This is basically the same as the Jane <code>ClickAndPlayMobilitySourceSimple</code>
 * without the ClickAndPlayMobilitySource part ;)
 */
public class BaseStationMobilitySource implements MobilitySource {

	private int numberDevices;
	private Extent extent;
	private int count;
	
	private static final double UPDATE_DELTA = 0.05;
	private static final double START_TIME = 0;
	
	private DiscreteDistribution xDistribution;
	private DiscreteDistribution yDistribution;
	private PositionDeviceMap positionDeviceMap;
	private HashMap deviceEntityMap;
	private Position[] baseStationPositions;
	
	private ContinuousDistribution speedDistribution;
	private ContinuousDistribution sendingRangeDistribution;
	
	/**
	 * This class represents a Device and stores Positions as well as a time value. 
	 */
	private class DeviceEntity{
		private Position currentPosition;
		private Position nextPosition;
		private double currentTime;
		
		/**
		 * Constructor which sets the initial Position of the Device 
		 */
		public DeviceEntity( Position initialPosition ){
			currentPosition = initialPosition;
			nextPosition = initialPosition;
			currentTime = START_TIME;
		}
	
		/**
		 * Sets the next Position
		 */
		public void setNextPosition( Position nextPosition ) {
			currentPosition = this.nextPosition;
			this.nextPosition = nextPosition;
		}
		
		/** 
		 * Returns true if the Device has moved
		 */
		public boolean hasMoved(){
			return !currentPosition.equals(nextPosition);
		}
	
		/**
		 * Returns the ArrivalInfo of the MobilitySource Interface
		 */
		public ArrivalInfo getNextArrivalInfo() {
			if( hasMoved() ) {
				currentTime += currentPosition.distance(nextPosition) / speedDistribution.getNext(currentTime);
				currentPosition = nextPosition;
			} else {
				currentTime += UPDATE_DELTA;
			}
			return new ArrivalInfo( nextPosition, currentTime );
		}

		/**
		 * Returns the Current Position
		 */
		public Position getCurrentPosition() {
			return currentPosition;
		}
	}
	
	/**
	 * Constructor
	 */
	public BaseStationMobilitySource( int numberDevices, Extent extent, ContinuousDistribution speedDistribution, ContinuousDistribution sendingRangeDistribution, DistributionCreator distributionCreator ) {
		if( speedDistribution.getInfimum() <= 0 ) throw new IllegalArgumentException( "Speed must be greater than 0" );
		this.numberDevices = numberDevices;
		this.extent = extent;
		this.speedDistribution = speedDistribution;
		this.sendingRangeDistribution = sendingRangeDistribution;
		this.count = 1;
		this.xDistribution = distributionCreator.getDiscreteUniformDistribution( 0,(int)extent.getHeight() );
		this.yDistribution = distributionCreator.getDiscreteUniformDistribution( 0,(int)extent.getWidth() );
		this.positionDeviceMap = new PositionDeviceMap();
		this.deviceEntityMap = new HashMap();
		this.baseStationPositions = BaseStationPositions.getPositions( extent, numberDevices );
	}
	
	/**
	 * Return true if there are Devices left, which were not added to the Simulation yet.
	 */
	public boolean hasNextEnterInfo() {
		return count < numberDevices;
	}

	/**
	 * Returns the EnterInfo Object of the next Device that entered the Simulation.
	 */
	public EnterInfo getNextEnterInfo() {
		//Position position = new Position( xDistribution.getNext(0), yDistribution.getNext(0) );
		Position position = baseStationPositions[count];//BaseStationPositions.getNextBSP( extent );
		DeviceID address = new SimulationDeviceID( count );
		positionDeviceMap.addDevice( address,position );
		deviceEntityMap.put( address, new DeviceEntity(position) );
		count++;
		return new EnterInfo( address, sendingRangeDistribution.getNext(), new ArrivalInfo(position,START_TIME) );
	}

	public boolean hasNextArrivalInfo( DeviceID address ) {
		return true;
	}

	public ArrivalInfo getNextArrivalInfo( DeviceID address ) {
		DeviceEntity deviceEntity = (DeviceEntity)deviceEntityMap.get( address );
		return deviceEntity.getNextArrivalInfo();
	}

	/**
	 * Returns the Rectangle which contains the 'Simulation Plane'
	 */
	public Rectangle getRectangle() {
		return new Rectangle( extent );
	}

	public Shape getShape() {
		return EmptyShape.getInstance();
	}

	/**
	 * Return the number of Devices which are part of this MobilitySource
	 */
	public int getTotalDeviceCount() {
		return numberDevices;
	}

	public Condition getTerminalCondition(Clock clock) {
		return null;
	}

	public double getMinimumTransmissionRange() {
		return sendingRangeDistribution.getInfimum();
	}

	public double getMaximumTransmissionRange() {
		return sendingRangeDistribution.getSupremum();
	}
	
}
