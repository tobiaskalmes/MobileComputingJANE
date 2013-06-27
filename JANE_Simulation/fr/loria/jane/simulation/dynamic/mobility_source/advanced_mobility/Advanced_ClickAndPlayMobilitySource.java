/*****************************************************************************
 * 
 * Advanced_ClickAndPlayMobilitySource.java
 * 
 * @author Tom Leclerc (tom.leclerc{at}loria.fr)
 * 
 * NB: this class was done by using as base the ClickAndPlayMobilitySourceSimple class
 * 
 * *********************************************************************** *
 * JANE - The Java Ad-hoc Network simulation and evaluation Environment
 * 
 * **********************************************************************  
 * 
 * Copyright (C) 2007 Tom Leclerc
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
package fr.loria.jane.simulation.dynamic.mobility_source.advanced_mobility;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.simulation.*;
import de.uni_trier.jane.simulation.dynamic.mobility_source.ClickAndPlayMobilitySource;
import de.uni_trier.jane.simulation.dynamic.mobility_source.FixedNodes;
import de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource;
import de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySourceBase;
import de.uni_trier.jane.simulation.dynamic.mobility_source.PositionDeviceMap;
import de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource.ArrivalInfo;
import de.uni_trier.jane.simulation.dynamic.position_generator.*;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;
import de.uni_trier.jane.simulation.parametrized.parameters.initialization.*;
import de.uni_trier.jane.simulation.parametrized.parameters.object.*;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * 
 * This <code>MobilitySource</code> can be used for the
 * <code>Advanced_ClickAndPlayMobilitySource</code> Devices are moved by
 * selecting them with the mouse within the
 * <code>Advanced_ClickAndPlaySimulationFrame</code>
 * 
 * 1) Select a node with left click REMARK: Nodes can be select during
 * movements! 
 * 2.1) Move to a position by clicking left on that position 
 * or
 * 2.2) Move toward a position (node continues beyond the position until the end of
 * the simulation frame) by clicking shift+left on that position 
 * or 
 * 2.3) Stop moving with the middle click
 * 
 * REMARK: At any time you can change between the 3 movements!
 * 
 * 3) Release the node by clicking left
 * 
 */
public class Advanced_ClickAndPlayMobilitySource extends MobilitySourceBase
		implements ClickAndPlayMobilitySource {

	public final static String VERSION = "$Id: ClickAndPlayMobilitySourceSimple.java,v 1.8 2005/10/31 09:14:14 frey_cvs Exp $";

	public static final InitializationObjectElement MOBILITY_SOURCE_CLICK_AND_PLAY_OBJECT = new InitializationObjectElement(
			"clickAndPlay") {
		public Object getValue(InitializationContext initializationContext,
				SimulationParameters simulationParameters) {
			int n = NUMBER_OF_NODES.getValue(initializationContext);
			double w = AREA_WIDTH.getValue(initializationContext);
			double h = AREA_HEIGHT.getValue(initializationContext);
			double r = SENDING_RADIUS.getValue(initializationContext);
			Extent extent = new Extent(w, h);
			DistributionCreator distributionCreator = simulationParameters
					.getDistributionCreator();
			ContinuousDistribution speed = distributionCreator
					.getContinuousUniformDistribution(10.0, 20.0);
			ContinuousDistribution sendingRange = distributionCreator
					.getContinuousDeterministicDistribution(r);
			return new Advanced_ClickAndPlayMobilitySource(n, extent, speed,
					sendingRange, distributionCreator);
		}

		public Parameter[] getParameters() {
			return new Parameter[] { NUMBER_OF_NODES, AREA_WIDTH, AREA_HEIGHT,
					SENDING_RADIUS };
		}
	};

	public static final InitializationElement MOBILITY_SOURCE_CLICK_AND_PLAY = new InitializationElement(
			"clickAndPlay") {
		public void initialize(InitializationContext initializationContext,
				SimulationParameters simulationParameters) {
			int n = NUMBER_OF_NODES.getValue(initializationContext);
			double w = AREA_WIDTH.getValue(initializationContext);
			double h = AREA_HEIGHT.getValue(initializationContext);
			double r = SENDING_RADIUS.getValue(initializationContext);
			Extent extent = new Extent(w, h);
			DistributionCreator distributionCreator = simulationParameters
					.getDistributionCreator();
			ContinuousDistribution speed = distributionCreator
					.getContinuousUniformDistribution(10.0, 20.0);
			ContinuousDistribution sendingRange = distributionCreator
					.getContinuousDeterministicDistribution(r);
			Advanced_ClickAndPlayMobilitySource mobilitySource = new Advanced_ClickAndPlayMobilitySource(
					n, extent, speed, sendingRange, distributionCreator);
			simulationParameters.setMobilitySource(mobilitySource);
		}

		public Parameter[] getParameters() {
			return new Parameter[] { NUMBER_OF_NODES, AREA_WIDTH, AREA_HEIGHT,
					SENDING_RADIUS };
		}
	};

	protected PositionDeviceMap positionDeviceMap;

	protected HashMap deviceEntityMap;

	private static final double UPDATE_DELTA = 0.05;

	private static final double START_TIME = 0;

	private ContinuousDistribution speedDistribution;

	private Output output;

	private MobilitySource initMobilitySource;

	private Extent extent;

	// ///////////////////////////////////////////////
	protected class DeviceEntity {
		private Position currentPosition;

		private Position nextPosition;

		private Position finalPosition;

		private MutablePosition step;

		private double startTime;

		private double currentTime;

		private double arrivalTime;

		private int stepCounter = 0;

		private int limit = 0;

		/**
		 * 
		 * @param startPosition
		 */
		public DeviceEntity(Position startPosition) {
			currentPosition = startPosition;
			nextPosition = startPosition;
			startTime = START_TIME;
			currentTime = START_TIME;
			finalPosition = startPosition;
		}

		/**
		 * move the device toward the position and stop there
		 * 
		 * @param position
		 *            the final position of the device
		 */
		public void setNextPosition(Position position) {
			// currentPosition=this.nextPosition;
			this.finalPosition = position;
			setStepAndLimit(false);
		}

		/**
		 * stopps the move of the device
		 */
		public void stopMoving() {
			finalPosition = currentPosition;
		}

		/**
		 * move the device toward the position and stop there
		 * 
		 * @param position
		 *            the final position of the device
		 */
		public void moveToward(Position position) {
			this.finalPosition = position;
			setStepAndLimit(true);
		}

		/**
		 * calculates the step vector and sets the end limit
		 * 
		 * @param moveToward
		 *            if true: set the limit to infinity (-1) else: calculate
		 *            the limit
		 */
		public void setStepAndLimit(boolean moveToward) {
			// build the step
			step = new MutablePosition(this.finalPosition);
			// normalize the step to 0
			step.sub(currentPosition);
			// set the step to: 1 / ( distance to from start position to final
			// position / speed )
			step
					.scale(1 / (currentPosition.distance(finalPosition) / speedDistribution
							.getNext(currentTime)));

			if (!moveToward)
				limit = (int) (currentPosition.distance(finalPosition) / speedDistribution
						.getNext(currentTime));
			else
				limit = -1;
			stepCounter = 0;
		}

		/**
		 * checks if device finished moving
		 * 
		 * @return true if not finished moving
		 */
		public boolean hasMoved() {
			return !currentPosition.equals(finalPosition);
		}

		/**
		 * keeps the given position in the bounds of the simulation area
		 * 
		 * @param position
		 *            position to check
		 */
		public Position keepInBounds(Position position) {
			boolean outOfBounds = false;
			double x = position.getX();
			double y = position.getY();

			// check bounds
			if (x > getRectangle().getWidth()) {
				x = getRectangle().getWidth();
				outOfBounds = true;
			}
			if (y > getRectangle().getHeight()) {
				y = getRectangle().getHeight();
				outOfBounds = true;
			}
			if (x < 0) {
				x = 0;
				outOfBounds = true;
			}
			if (y < 0) {
				y = 0;
				outOfBounds = true;
			}
			// if out of bounds, stop moving
			if (outOfBounds)
				finalPosition = new Position(x, y);

			// return in bounds position
			return new Position(x, y);
		}

		/**
		 * Main method to move a device
		 * 
		 * @param deviceID
		 *            device address
		 * @return arrivalInfo -> position and time of the next move
		 */
		// private long nano = 0;
		public ArrivalInfo getNextArrivalInfo(DeviceID deviceID) {
			// if (deviceID.toString().equalsIgnoreCase("4")){
			//			
			// System.out.println("getArrival "+System.nanoTime());
			// }
			if (hasMoved()) {
				stepCounter++;
				// if number of steps reached the limit
				if (stepCounter == limit) {
					// stop moving further
					currentTime += currentPosition.distance(finalPosition)
							/ speedDistribution.getNext(currentTime);
					currentPosition = finalPosition;
					stepCounter = 0;
					positionDeviceMap.removeDevice(deviceID);
					positionDeviceMap.addDevice(deviceID, currentPosition);
					return new ArrivalInfo(finalPosition, currentTime);
				}
				// else
				// move current position to the next step
				MutablePosition pos = new MutablePosition(currentPosition);
				pos.add(step);

				// adapt arrival time (here current time) for the move
				currentTime += currentPosition.distance(pos)
						/ speedDistribution.getNext(currentTime);

				// set the device on the new position on the position device map
				// Used for the selection of a device
				positionDeviceMap.removeDevice(deviceID);
				positionDeviceMap.addDevice(deviceID, currentPosition);
				// adapt position to the bounds
				currentPosition = keepInBounds(pos.getPosition());

				// if not moving
			} else {
				// update time
				currentTime += UPDATE_DELTA;
			}

			// return the next position and corresponding arrival time
			return new ArrivalInfo(currentPosition, currentTime);
		}

		/**
		 * gets the current position of the device
		 * 
		 * @return the current position
		 */
		public Position getCurrentPosition() {

			return currentPosition;
		}
	}

	/**
	 * Constructor for class<code>ClickAndPlayMobilitySource</code>
	 * 
	 * @param numberDevices
	 *            the amount of devices
	 * @param extent
	 *            the extent whithin devices should be initially placed
	 * @param speedDistribution
	 *            the distribution for selecting the moving speed of a device,
	 *            changes for each move
	 * @param sendingRangeDistribution
	 *            the distribution for selecting the initial sending range of a
	 *            device
	 * @param distributionCreator
	 *            the <code>DistributionCreator</code>
	 */
	public Advanced_ClickAndPlayMobilitySource(int numberDevices,
			Extent extent, ContinuousDistribution speedDistribution,
			ContinuousDistribution sendingRangeDistribution,
			DistributionCreator distributionCreator, Output output) {
		this(FixedNodes.createRandom(numberDevices,
				new RandomPositionGenerator(
						distributionCreator.getContinuousUniformDistribution(0,
								extent.getWidth()), distributionCreator
								.getContinuousUniformDistribution(0, extent
										.getHeight())),
				sendingRangeDistribution, EmptyShape.getInstance()),
				speedDistribution, output);
		this.extent = extent;
	}

	/**
	 * 
	 * Constructor for class <code>ClickAndPlayMobilitySourceSimple</code>
	 * 
	 * @param initMobilitySource
	 * @param speedDistribution
	 */
	public Advanced_ClickAndPlayMobilitySource(
			MobilitySource initMobilitySource,
			ContinuousDistribution speedDistribution) {
		this(initMobilitySource, speedDistribution, null);
	}

	/**
	 * 
	 * Constructor for class <code>ClickAndPlayMobilitySourceSimple</code>
	 * 
	 * @param initMobilitySource
	 * @param speedDistribution
	 * @param output
	 */
	public Advanced_ClickAndPlayMobilitySource(
			MobilitySource initMobilitySource,
			ContinuousDistribution speedDistribution, Output output) {
		if (speedDistribution.getInfimum() <= 0)
			throw new IllegalArgumentException("Speed must be greater than 0");
		this.speedDistribution = speedDistribution;
		positionDeviceMap = new PositionDeviceMap();
		deviceEntityMap = new HashMap();
		// Position[] posi = { new Position(10, 10), new Position(70, 70),
		// new Position(140, 140), new Position(210, 210),
		// new Position(280, 280) };
		// this.initMobilitySource = FixedNodes.create(posi, 50.0);
		this.initMobilitySource = initMobilitySource;
		this.output = output;
		// extent=initMobilitySource.getRectangle().getExtent();
	}

	/**
	 * 
	 * Constructor for class <code>ClickAndPlayMobilitySourceSimple</code>
	 * 
	 * @param initMobilitySource
	 * @param minSpeed
	 * @param maxSpeed
	 * @param output
	 * @param distributionCreator
	 */
	public Advanced_ClickAndPlayMobilitySource(
			MobilitySource initMobilitySource, double minSpeed,
			double maxSpeed, Output output,
			DistributionCreator distributionCreator) {
		this(initMobilitySource, distributionCreator
				.getContinuousUniformDistribution(minSpeed, maxSpeed), output);
	}

	/**
	 * Constructor for class<code>ClickAndPlayMobilitySource</code>
	 * 
	 * @param numberDevices
	 *            the amount of devices
	 * @param extent
	 *            the extent whithin devices should be initially placed
	 * @param speedDistribution
	 *            the distribution for selecting the moving speed of a device,
	 *            changes for each move
	 * @param sendingRangeDistribution
	 *            the distribution for selecting the initial sending range of a
	 *            device
	 * @param distributionCreator
	 *            the <code>DistributionCreator</code>
	 */
	public Advanced_ClickAndPlayMobilitySource(int numberDevices,
			Extent extent, ContinuousDistribution speedDistribution,
			ContinuousDistribution sendingRangeDistribution,
			DistributionCreator distributionCreator) {
		this(numberDevices, extent, speedDistribution,
				sendingRangeDistribution, distributionCreator, null);
	}

	/**
	 * 
	 * Constructor for class <code>ClickAndPlayMobilitySourceSimple</code>
	 * 
	 * @param numberDevices
	 * @param extent
	 * @param minSpeed
	 * @param maxSpeed
	 * @param minSendingRange
	 * @param maxSendingRange
	 * @param distributionCreator
	 * @param output
	 */
	public Advanced_ClickAndPlayMobilitySource(int numberDevices,
			Extent extent, double minSpeed, double maxSpeed,
			double minSendingRange, double maxSendingRange,
			DistributionCreator distributionCreator, Output output) {
		this(numberDevices, extent, distributionCreator
				.getContinuousUniformDistribution(minSpeed, maxSpeed),
				distributionCreator.getContinuousUniformDistribution(
						minSendingRange, maxSendingRange), distributionCreator,
				output);
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
	public Advanced_ClickAndPlayMobilitySource(int numberDevices,
			Extent extent, double minSpeed, double maxSpeed,
			double minSendingRange, double maxSendingRange,
			DistributionCreator distributionCreator) {
		this(numberDevices, extent, minSpeed, maxSpeed, minSendingRange,
				maxSendingRange, distributionCreator, null);
	}

	/**
	 * TODO Comment method
	 */
	private void saveOutput() {
		if (output != null) {
			output.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			output
					.println("<!DOCTYPE scripted_mobility SYSTEM \"CommandList.dtd\">");
			output
					.println("<COMMANDLIST BOTTOMLEFTX=\"0.0\" BOTTOMLEFTY=\"0.0\" TOPRIGHTX=\""
							+ extent.getHeight()
							+ "\" TOPRIGHTY=\""
							+ extent.getWidth() + "\">");
		}

	}

	/**
	 * TODO Comment method
	 * 
	 * @param enterInfo
	 */
	private void saveOutput(EnterInfo enterInfo) {
		if (output != null) {
			output.println("<ENTER TIME=\""
					+ enterInfo.getArrivalInfo().getTime() + "\" ADDRESS=\""
					+ enterInfo.getAddress() + "\" RADIUS=\""
					+ enterInfo.getSendingRadius() + "\" ENDTIME=\""
					+ enterInfo.getArrivalInfo().getTime() + "\" STARTX=\""
					+ enterInfo.getArrivalInfo().getPosition().getX()
					+ "\" STARTY=\""
					+ enterInfo.getArrivalInfo().getPosition().getY()
					+ "\" ENDX=\""
					+ enterInfo.getArrivalInfo().getPosition().getX()
					+ "\" ENDY=\""
					+ enterInfo.getArrivalInfo().getPosition().getY() + "\"/>");
		}

	}

	/**
	 * TODO Comment method
	 * 
	 * @param nextPosition
	 * @param currentTime
	 */
	public void saveOutput(DeviceID deviceID, Position nextPosition,
			double currentTime) {
		if (output != null) {
			output.println("<ARRIVAL TIME=\"0.0\" ADDRESS=\"" + deviceID
					+ "\" ENDTIME=\"" + currentTime
					+ "\" STARTX=\"0.0\" STARTY=\"0.0\" ENDX=\""
					+ nextPosition.getX() + "\" ENDY=\"" + nextPosition.getY()
					+ "\"/>");
			output.flush();
		}

	}

	public boolean hasNextEnterInfo() {

		return initMobilitySource.hasNextEnterInfo();
	}

	public EnterInfo getNextEnterInfo() {
		EnterInfo enterInfo = initMobilitySource.getNextEnterInfo();

		Position position = enterInfo.getArrivalInfo().getPosition();// new
		// Position(xDistribution.getNext(0),yDistribution.getNext(0));
		DeviceID address = enterInfo.getAddress();// new
		// SimulationDeviceID(count);
		positionDeviceMap.addDevice(address, position);
		deviceEntityMap.put(address, new DeviceEntity(position));
		saveOutput(enterInfo);
		return enterInfo;
	}

	public boolean hasNextArrivalInfo(DeviceID address) {
		return true;
	}

	public ArrivalInfo getNextArrivalInfo(DeviceID address) {
		DeviceEntity deviceEntity = (DeviceEntity) deviceEntityMap.get(address);
		return deviceEntity.getNextArrivalInfo(address);
	}

	public Rectangle getRectangle() {
		return new Rectangle(extent);
	}

	// buils the shape for the simulation: intial shape with an rectangle
	// delimiting the simulation area
	public Shape getShape() {
		Shape shape = initMobilitySource.getShape();
		ShapeCollection shapeCol = new ShapeCollection();
		shapeCol.addShape(shape);
		shapeCol.addShape(new RectangleShape(getRectangle().getCenter(),
				getRectangle().getExtent(), Color.BLACK, true), new Position(0,
				0));
		shape = shapeCol;
		return shape;
	}

	public int getTotalDeviceCount() {
		return initMobilitySource.getTotalDeviceCount();
	}

	/**
	 * move the device toward the position and stop there
	 * 
	 * @param device
	 *            the device address of the device
	 * @param position
	 *            the new position of the device
	 */
	public void setPosition(DeviceID device, Position position) {
		DeviceEntity deviceEntity = (DeviceEntity) deviceEntityMap.get(device);
		deviceEntity.setNextPosition(position);
	}

	/**
	 * stopps the move of the device
	 * 
	 * @param device
	 *            the device address of the device
	 */
	public void stopMoving(DeviceID device) {
		DeviceEntity deviceEntity = (DeviceEntity) deviceEntityMap.get(device);
		deviceEntity.stopMoving();
	}

	/**
	 * move toward and beyond the click position until you reach the bounds of
	 * the simulation area
	 * 
	 * @param device
	 *            the device address of the device
	 * @param position
	 *            the new position of the device
	 */
	public void moveToward(DeviceID device, Position position) {
		DeviceEntity deviceEntity = (DeviceEntity) deviceEntityMap.get(device);
		deviceEntity.moveToward(position);
	}

	public Position getCurrentPosition(DeviceID device) {
		DeviceEntity deviceEntity = (DeviceEntity) deviceEntityMap.get(device);
		return deviceEntity.getCurrentPosition();
	}

	/**
	 * Returns all addresses of (non moving) devices within a rectangle
	 * 
	 * @param rectangle
	 *            the rectangle
	 * @return adresses of all devices within the given rectangle
	 */
	public DeviceID[] getAddress(Rectangle rectangle) {
		return positionDeviceMap.getDevices(rectangle);
	}

	/*
	 * (non-Javadoc)
	 * 
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
