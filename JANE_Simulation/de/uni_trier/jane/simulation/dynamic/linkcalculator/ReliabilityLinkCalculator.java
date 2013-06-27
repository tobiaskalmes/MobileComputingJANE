/*****************************************************************************
 * 
 * ReliabilityLinkCalculator.java
 * 
 * $Id: ReliabilityLinkCalculator.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
 *  
 * Copyright (C) 2002, 2003 Hannes Frey and Daniel G?rgen and Johannes K. Lehnert
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
package de.uni_trier.jane.simulation.dynamic.linkcalculator;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.simulation.dynamic.*;
import de.uni_trier.jane.simulation.dynamic.mobility_source.*;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.jane.visualization.shapes.*;


public class ReliabilityLinkCalculator implements DynamicSource {

	private final static String VERSION = "$Id: ReliabilityLinkCalculator.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";

	private MobilitySource mobilitySource;
	private Output output;
	private CommandStack commandStack;
	private LinkReliabilityCalculator linkReliabilityCalculator;
	private AddressDeviceInfoMap addressDeviceInfoMap;
	
	private boolean first;

	/**
	 * Constructs a new <code>LinkCalculator</code>. If an output is defined (i.e. not null) then an XML file containing 
	 * all Actions calculated by the LinkCalculator will be written to the output.
	 * @param mobilitySource the source providing all information about device mobility
	 * @param output the output to use. If output is null, no output will be written.
	 */
	public ReliabilityLinkCalculator(MobilitySource mobilitySource, LinkReliabilityCalculator linkReliabilityCalculator, Output output) {
		this.linkReliabilityCalculator=linkReliabilityCalculator;
		this.mobilitySource = mobilitySource;
		this.output = output;
		commandStack = new CommandStack();
		addressDeviceInfoMap = new AddressDeviceInfoMap();
		first = true;
		while(mobilitySource.hasNextEnterInfo()) {
			MobilitySource.EnterInfo enterInfo = mobilitySource.getNextEnterInfo();
			commandStack.push(new EnterCommand(enterInfo.getArrivalInfo().getTime(), enterInfo.getAddress(), enterInfo.getArrivalInfo().getPosition(), enterInfo.getSendingRadius()));
		}
	}
	
	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.DynamicSource#hasNext()
	 */
	public boolean hasNext() {
		boolean hasNext = !commandStack.isEmpty();
		if(output != null) {
			if(first) {
				Rectangle rectangle = mobilitySource.getRectangle();
				output.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				output.println("<!DOCTYPE COMMANDLIST SYSTEM \"CommandList.dtd\">");
				output.print("<COMMANDLIST BOTTOMLEFTX=\"");
				output.print(String.valueOf(rectangle.getBottomLeft().getX()));
				output.print("\" BOTTOMLEFTY=\"");
				output.print(String.valueOf(rectangle.getBottomLeft().getY()));								
				output.print("\" TOPRIGHTX=\"");								
				output.print(String.valueOf(rectangle.getTopRight().getX()));								
				output.print("\" TOPRIGHTY=\"");								
				output.print(String.valueOf(rectangle.getTopRight().getY()));
				output.println("\">");
			}
			if(!hasNext) {
				output.println("</COMMANDLIST>");
			}
		}
		first = false;
		return hasNext;
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.DynamicSource#next()
	 */
	public Action next() {
		Command command = commandStack.pop();
		Action action = command.handle();
		if(output != null) {
			command.save(output);
		}
		return action;
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.DynamicSource#getRectangle()
	 */
	public Rectangle getRectangle() {
		return mobilitySource.getRectangle();
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.DynamicSource#getShape()
	 */
	public Shape getShape() {
		return mobilitySource.getShape();
	}
	
    public Condition getTerminalCondition(Clock clock) {     
        return mobilitySource.getTerminalCondition(clock);
    }



	private void insertLinkCommands(DeviceID address) {
		DeviceInfo info = addressDeviceInfoMap.getDeviceInfo(address);
		info.notifyInsertLink();
		LinearMotion linearMotion = info.getLinearMotion();
		double sendingRadius = info.getSendingRadius();
		DeviceIDIterator it = addressDeviceInfoMap.getAddressIterator();
		while(it.hasNext()) {
			DeviceID key = it.next();
			if(!key.equals(address)) {
				LinearMotion value = addressDeviceInfoMap.getDeviceInfo(key).getLinearMotion();
				
				generateCommands(
					address,
					key,
					linearMotion,
					value,
					sendingRadius,
					info.isFirstMove());
				
				
				generateCommands(
					key,
					address,
					value,
					linearMotion, 
					addressDeviceInfoMap.getDeviceInfo(key).getSendingRadius(),
					info.isFirstMove());

			}
		}
	}

	private void generateCommands(
		DeviceID sender,
		DeviceID receiver,
		LinearMotion senderLinearMotion,
		LinearMotion receiverLinearMotion,
		double sendingRadius,
		boolean firstMove){
			
		ClippedLinkInfo linkInfo = senderLinearMotion.getClippedLinkInfo(receiverLinearMotion, sendingRadius);
		DoubleMappingInterval linkReliability;
		DistanceMapping distanceMapping= new DistanceMapping(
							senderLinearMotion.getTrajectoryMapping(),
							receiverLinearMotion.getTrajectoryMapping());
		if(linkInfo != null) {
			
			double reliabilityEndTime;
		
			
			if(linkInfo.detachInsideClip()) {
				commandStack.push(new DetachCommand(linkInfo.getDetachTime(), sender, receiver));
				reliabilityEndTime=linkInfo.getDetachTime();
			}else{
				reliabilityEndTime=linkInfo.clippingEnd;
			}
					
			if(linkInfo.attachInsideClip()) {
			
				linkReliability= linkReliabilityCalculator.getLinkReliability(sendingRadius,
																			  linkInfo.getAttachTime(),
																			  reliabilityEndTime,
																			  distanceMapping);
				commandStack.push(new AttachCommand(linkInfo.getAttachTime(),linkReliability, sender, receiver));
					
			}
			else if(firstMove && senderLinearMotion.getPosition(senderLinearMotion.getStartTime()).distance(receiverLinearMotion.getPosition(senderLinearMotion.getStartTime())) == sendingRadius) {
				linkReliability= linkReliabilityCalculator.getLinkReliability(sendingRadius,
																			  senderLinearMotion.getStartTime(),
																			  reliabilityEndTime,
																			  distanceMapping);
				
				commandStack.push(new AttachCommand(senderLinearMotion.getStartTime(),linkReliability, sender, receiver));
			}
			if (!linkInfo.attachInsideClip()&&
				senderLinearMotion.getPosition(linkInfo.clippingStart).distance(receiverLinearMotion.getPosition(linkInfo.clippingStart))<=sendingRadius&&
				linkInfo.clippingStart<reliabilityEndTime
				){
		
		
				linkReliability= linkReliabilityCalculator.getLinkReliability(sendingRadius,
																			  linkInfo.clippingStart,
																			  reliabilityEndTime,
																			  distanceMapping);
				
			
				
				commandStack.push(new SetLinkReliabilityCommand(linkInfo.clippingStart,linkReliability,sender,receiver));
			}
		}else {
			double startTime=Math.max(senderLinearMotion.getStartTime(),receiverLinearMotion.getStartTime());
			double endTime=Math.min(senderLinearMotion.getEndTime(),receiverLinearMotion.getEndTime());
			if (senderLinearMotion.getPosition(startTime).distance(receiverLinearMotion.getPosition(startTime))<sendingRadius&&startTime<endTime){
				linkReliability= linkReliabilityCalculator.getLinkReliability(sendingRadius,
																			  startTime,
																			  endTime,
																			  distanceMapping);
				
			
				
				commandStack.push(new SetLinkReliabilityCommand(startTime,linkReliability,sender,receiver));
				
			}
		}
	}
	

	private void insertEnterLinkCommands(DeviceID address, double time) {
		LinearMotion linearMotion = addressDeviceInfoMap.getDeviceInfo(address).getLinearMotion();
		double sendingRadius = addressDeviceInfoMap.getDeviceInfo(address).getSendingRadius();
		DeviceIDIterator it = addressDeviceInfoMap.getAddressIterator();
		while(it.hasNext()) {
			DeviceID key = it.next();
			if(!key.equals(address)) {
				LinearMotion value = addressDeviceInfoMap.getDeviceInfo(key).getLinearMotion();
				DistanceMapping distanceMapping=new DistanceMapping(linearMotion.getTrajectoryMapping(),value.getTrajectoryMapping());

				if(linearMotion.isInReach(time, value, sendingRadius)) {
					DoubleMappingInterval linkReliability=linkReliabilityCalculator.getLinkReliability(
												sendingRadius,
												time,
												Math.min(linearMotion.getEndTime(),value.getEndTime()),
												distanceMapping);
					
					commandStack.push(new AttachCommand(time,linkReliability, address, key));
				}
				if(value.isInReach(time, linearMotion, addressDeviceInfoMap.getDeviceInfo(key).getSendingRadius())) {
					DoubleMappingInterval linkReliability=linkReliabilityCalculator.getLinkReliability(
							addressDeviceInfoMap.getDeviceInfo(key).getSendingRadius(),
							time,
							Math.min(linearMotion.getEndTime(),value.getEndTime()),
							distanceMapping);

					commandStack.push(new AttachCommand(time,linkReliability, key, address));
				}
			}
		}
	}

	private ExitInfo[] insertExitLinkCommands(DeviceID address, double time) {
		List exitInfoList = new LinkedList();
		LinearMotion linearMotion = addressDeviceInfoMap.getDeviceInfo(address).getLinearMotion();
		double sendingRadius = addressDeviceInfoMap.getDeviceInfo(address).getSendingRadius();
		DeviceIDIterator it = addressDeviceInfoMap.getAddressIterator();
		while(it.hasNext()) {
			DeviceID key = it.next();
			if(!key.equals(address)) {				
				LinearMotion value = addressDeviceInfoMap.getDeviceInfo(key).getLinearMotion();
				ClippedLinkInfo linkInfo = linearMotion.getClippedLinkInfo(value, sendingRadius);
				if(linkInfo== null || !linkInfo.detachInsideClip()) {				
					if(linearMotion.isInReach2(time, value, sendingRadius)) {
						exitInfoList.add(new ExitInfo(key, false));
					}
				}
				linkInfo = value.getClippedLinkInfo(linearMotion, addressDeviceInfoMap.getDeviceInfo(key).getSendingRadius());
				if(linkInfo== null || !linkInfo.detachInsideClip()) {
					if(value.isInReach2(time, linearMotion, addressDeviceInfoMap.getDeviceInfo(key).getSendingRadius())) {
						exitInfoList.add(new ExitInfo(key, true));
					}
				}
			}
		}
		return (ExitInfo[])exitInfoList.toArray(new ExitInfo[exitInfoList.size()]);
	}

	private static class ExitInfo {
		private DeviceID address;
		private boolean isSender;
		public ExitInfo(DeviceID address, boolean isSender) {
			this.address = address;
			this.isSender = isSender;
		}
		public DeviceID getAddress() {
			return address;
		}
		public boolean isSender() {
			return isSender;
		}
	}



	private class EnterCommand extends Command {
		private DeviceID address;
		private Position position;
		private double sendingRadius;
		public EnterCommand(double time, DeviceID address, Position position, double sendingRadius) {
			super(time, 0);
			this.address = address;
			this.position = position;
			this.sendingRadius = sendingRadius;
		}
		public Action handle() {
			if(mobilitySource.hasNextArrivalInfo(address)) {
				MobilitySource.ArrivalInfo arrivalInfo = mobilitySource.getNextArrivalInfo(address);
				LinearMotion linearMotion = new LinearMotion(getTime(), arrivalInfo.getTime(), position, arrivalInfo.getPosition());

				addressDeviceInfoMap.setDeviceInfo(address, new DeviceInfo(sendingRadius, linearMotion));

				commandStack.push(new ArrivalCommand(arrivalInfo.getTime(), address, arrivalInfo.getPosition()));
				insertLinkCommands(address);
				insertEnterLinkCommands(address, getTime());

				return new EnterAction(getTime(), address, linearMotion.getTrajectoryMapping(),false, new ConstantDoubleMapping(sendingRadius));
			}
			else {
				throw new IllegalStateException("An entered device ("+address+") is not allowed to exit immediately.");
			}
		}
		public void save(Output output) {
			LinearMotion linearMotion = addressDeviceInfoMap.getDeviceInfo(address).getLinearMotion();
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("  <ENTER TIME=\"");
			stringBuffer.append(getTime());
			stringBuffer.append("\" ADDRESS=\"");
			stringBuffer.append(address);
			stringBuffer.append("\" RADIUS=\"");
			stringBuffer.append(sendingRadius);
			stringBuffer.append("\" ");
			output.print(stringBuffer.toString());
			linearMotion.save(output);
			output.println("/>");
		}

	}

	private class ArrivalCommand extends Command {
		
		private DeviceID address;
		private Position position;
		
		private boolean saveArrival;
		private ExitInfo detachedDevice;

		public ArrivalCommand(double time, DeviceID address, Position position) {
			super(time, 1);
			this.address = address;
			this.position = position;
			saveArrival = false;
			detachedDevice = null;
		}
		public Action handle() {
			if(mobilitySource.hasNextArrivalInfo(address)) {
				LinearMotion linearMotion;
				MobilitySource.ArrivalInfo arrivalInfo = mobilitySource.getNextArrivalInfo(address);
				linearMotion = new LinearMotion(getTime(), arrivalInfo.getTime(), position, arrivalInfo.getPosition());

				addressDeviceInfoMap.getDeviceInfo(address).setLinearMotion(linearMotion);

				commandStack.push(new ArrivalCommand(arrivalInfo.getTime(), address, arrivalInfo.getPosition()));
				insertLinkCommands(address);

				saveArrival = true;
				return new SetPositionMappingAction(getTime(), linearMotion.getTrajectoryMapping(),false, address);
			}
			else {
				commandStack.push(new ExitCommand(getTime(), address));
				ExitInfo[] exitInfoArray = insertExitLinkCommands(address, getTime());
				if(exitInfoArray.length > 0) {
					for(int i=0; i<exitInfoArray.length; i++) {
						if(exitInfoArray[i].isSender()) {
							commandStack.push(new DetachCommand(getTime(), exitInfoArray[i].getAddress(), address));
						}
						else {
							commandStack.push(new DetachCommand(getTime(), address, exitInfoArray[i].getAddress()));
						}
					}
				}
				addressDeviceInfoMap.removeDeviceInfo(address);
				return next();

			}
		}
		public void save(Output output) {
			if (saveArrival) {
				LinearMotion linearMotion = addressDeviceInfoMap.getDeviceInfo(address).getLinearMotion();
				StringBuffer stringBuffer = new StringBuffer();
				stringBuffer.append("  <ARRIVAL TIME=\"");
				stringBuffer.append(getTime());
				stringBuffer.append("\" ADDRESS=\"");
				stringBuffer.append(address);
				stringBuffer.append("\" ");
				output.print(stringBuffer.toString());
				linearMotion.save(output);
				output.println("/>");
			}
		}
	}

	private class AttachCommand extends Command {
		private DoubleMappingInterval linkReliability;
		
		private DeviceID sender;
		private DeviceID receiver;
		public AttachCommand(double time, DoubleMappingInterval linkReliability, DeviceID sender, DeviceID receiver) {
			super(time, 2);
			this.sender = sender;
			this.linkReliability=linkReliability;
		
			this.receiver = receiver;
		}
		public Action handle() {
			return new AttachAction(getTime(), sender, receiver, 
				linkReliability);
		}
		public void save(Output output) {
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("  <ATTACH TIME=\"");
			stringBuffer.append(getTime());
			stringBuffer.append("\" SENDER=\"");
			stringBuffer.append(sender);
			stringBuffer.append("\" RECEIVER=\"");
			stringBuffer.append(receiver);
			stringBuffer.append("\"/>");
			output.println(stringBuffer.toString());
		}


	}
	
	private class SetLinkReliabilityCommand extends Command{

		//private double validTime;
		private DeviceID sender;
		private DeviceID receiver;
		private DoubleMappingInterval linkReliability;
		
		
		/**
		 * Constructor for SetLinkReliabilityCommand.
		 * @param receiver
		 * @param sender
		 * @param validTime
		 */
		public SetLinkReliabilityCommand(
			double time,
			//double validTime,
			DoubleMappingInterval linkReliability,
			
			DeviceID sender,
			DeviceID receiver) {
			
			super(time,3);
			this.receiver = receiver;
			this.sender = sender;
			//this.validTime = validTime;
			this.linkReliability=linkReliability;
		}
		/* (non-Javadoc)
		 * @see de.uni_trier.ubi.appsim.kernel.dynamic.LinkCalculator.Command#handle()
		 */
		public Action handle() {
			return new SetLinkReliabilityAction(getTime(),linkReliability,sender,receiver);//new DoubleMappingIntervalImplementation(new ConstantDoubleMapping(1.0),getTime(),validTime));
		}
		/* (non-Javadoc)
		 * @see de.uni_trier.ubi.appsim.kernel.dynamic.LinkCalculator.Command#save(de.uni_trier.ubi.appsim.kernel.Output)
		 */
		public void save(Output output) {
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("  <LINKRELIABILITYCHANGE TIME=\"");
			stringBuffer.append(getTime());
		//	stringBuffer.append("\" VALIDTIME=\"");
		//	stringBuffer.append(validTime);
			stringBuffer.append("\" SENDER=\"");
			stringBuffer.append(sender);
			stringBuffer.append("\" RECEIVER=\"");
			stringBuffer.append(receiver);
			
			stringBuffer.append("\"/>");
			output.println(stringBuffer.toString());
			
		}

	}

	private class DetachCommand extends Command {
		private DeviceID sender;
		private DeviceID receiver;
		public DetachCommand(double time, DeviceID sender, DeviceID receiver) {
			super(time, 3);
			this.sender = sender;
			this.receiver = receiver;
		}
		public Action handle() {
			return new DetachAction(getTime(), sender, receiver);
		}
		public void save(Output output) {
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("  <DETACH TIME=\"");
			stringBuffer.append(getTime());
			stringBuffer.append("\" SENDER=\"");
			stringBuffer.append(sender);
			stringBuffer.append("\" RECEIVER=\"");
			stringBuffer.append(receiver);
			stringBuffer.append("\"/>");
			output.println(stringBuffer.toString());
		}
	}

	private class ExitCommand extends Command {
		private DeviceID address;
		public ExitCommand(double time, DeviceID address) {
			super(time, 4);
			this.address = address;
		}
		public Action handle() {
			return new ExitAction(getTime(), address);
		}
		public void save(Output output) {
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("  <EXIT TIME=\"");
			stringBuffer.append(getTime());
			stringBuffer.append("\" ADDRESS=\"");
			stringBuffer.append(address);
			stringBuffer.append("\"/>");
			output.println(stringBuffer.toString());
		}
	}

	private abstract class Command {
		private double time;
		private int weight;

		public Command(double time, int weight) {
			this.time = time;
			this.weight = weight;
		}
		public double getTime() {
			return time;
		}
		public int getWeight() {
			return weight;
		}
		public abstract Action handle();
		public abstract void save(Output output);
	}

	private static class CommandStack {
		private FibonacciHeap fibonacciHeap;
		private double time;

		public CommandStack() {
			fibonacciHeap = new FibonacciHeap(new Comparator() {
				public int compare(Object o1, Object o2) {
					double t1 = ((Command) o1).getTime();
					double t2 = ((Command) o2).getTime();
					if (t1 < t2) {
						return -1;
					} else if (t1 == t2) { 
						int w1 = ((Command) o1).getWeight();
						int w2 = ((Command) o2).getWeight();
						if (w1 < w2) {
							return -1;
						} else if (w1 == w2) {
							return 0; 
						} else {
							return 1;
						}
					} else {
						return 1;
					}
				}
				public boolean equals(Object obj) {
					return false;
				}
			});
			time = 0;
		}

		public boolean isEmpty() {
			return fibonacciHeap.isEmpty();
		}

		public void push(Command command) {
			if (command.getTime() < time) {
				throw new IllegalArgumentException("Can't add command in the past!");
			}
			fibonacciHeap.insert(command);
		}

		public Command pop() {
			if(fibonacciHeap.isEmpty()) {
				throw new IllegalStateException("The stack is empty.");
			}
			Command command = (Command) fibonacciHeap.extractMinimum();
			time = command.getTime();
			return command;
		}
	}


	private static class DeviceInfo {
		private double sendingRadius;
		private LinearMotion linearMotion;
		private int insertCount;
		public DeviceInfo(double sendingRadius, LinearMotion linearMotion) {
			this.sendingRadius = sendingRadius;
			this.linearMotion = linearMotion;
			insertCount = 0;
		}
		public void notifyInsertLink() {
			if(insertCount < 3) {
				insertCount++;
			}
		}
		public double getSendingRadius() {
			return sendingRadius;
		}
		public LinearMotion getLinearMotion() {
			return linearMotion;
		}
		public void setLinearMotion(LinearMotion linearMotion) {
			this.linearMotion = linearMotion;
		}
		public boolean isFirstMove() {
			return insertCount == 1;
		}
	}

	private static class AddressDeviceInfoMap {
		private HashMap addressDeviceInfoMap;
		public AddressDeviceInfoMap() {
			addressDeviceInfoMap = new HashMap();
		}
		public void setDeviceInfo(DeviceID address, DeviceInfo deviceInfo) {
			addressDeviceInfoMap.put(address, deviceInfo);
		}
		public DeviceInfo getDeviceInfo(DeviceID address) {
			return (DeviceInfo)addressDeviceInfoMap.get(address);
		}
		public void removeDeviceInfo(DeviceID address) {
			addressDeviceInfoMap.remove(address);
		}
		public DeviceIDIterator getAddressIterator() {
			return new DeviceIDIterator() {
				private Iterator iterator = addressDeviceInfoMap.keySet().iterator();
				public boolean hasNext() {
					return iterator.hasNext();
				}
				public DeviceID next() {
					return (DeviceID)iterator.next();
				}
				
				/* (non-Javadoc)
				 * @see de.uni_trier.ubi.appsim.kernel.basetype.AddressIterator#remove()
				 */
				public void remove() {
					throw new IllegalAccessError("Remove not supported");
					//iterator.remove();

				}

			};
		}
	}

	private static class LinearMotion {
		private double startTime;
		private double endTime;
		private Position startPosition;
		private Position endPosition;
		public LinearMotion(double startTime, double endTime, Position startPosition, Position endPosition) {
			if(startTime > endTime || (startTime==endTime && !startPosition.equals(endPosition))) {
				throw new IllegalArgumentException("start time has to be less than end time or if equal positions have to be the same.");
			}
			this.startTime = startTime;
			this.endTime = endTime;
			this.startPosition = startPosition;
			this.endPosition = endPosition;
		}
		public TrajectoryMapping getTrajectoryMapping() {
			MutablePosition pos = getPosition(1);
			pos.sub(getPosition(0));
			return new PositionMappingTrajectory(new LinearPositionMapping(startTime, endTime, startPosition, endPosition), new ConstantPositionMapping(pos.getPosition()));
		}
		public ClippedLinkInfo getClippedLinkInfo(LinearMotion linearMotion, double radius) {
			double clipStart = Math.max(startTime, linearMotion.startTime);
			double clipEnd = Math.min(endTime, linearMotion.endTime);
			if(clipStart > clipEnd) {
				return null;
			}
			MutablePosition normalizedStartPosition = linearMotion.getPosition(0);
			normalizedStartPosition.sub(getPosition(0));
			MutablePosition normalizedEndPosition = linearMotion.getPosition(1);
			normalizedEndPosition.sub(getPosition(1));
			double sdx = normalizedStartPosition.getX();
			double edx = normalizedEndPosition.getX();
			double sdy = normalizedStartPosition.getY();
			double edy = normalizedEndPosition.getY();
			double a = sdx * sdx + sdy * sdy - radius * radius;
			double b = 2 * (sdx * (edx - sdx) + (sdy * (edy - sdy)));
			double c = Math.pow(edx - sdx, 2) + Math.pow(edy - sdy, 2);
			if (c == 0.0) {
				return null;
			}
			double underSqrt = b * b / (4 * c * c) - a / c;
			double first = -b / (2 * c);
			if (underSqrt < 0) {
				return null;
			}
			double sqrt = Math.sqrt(underSqrt);
			if (sqrt == 0.0) { 
				return null;
			}
			double s1 = first + sqrt;
			double s2 = first - sqrt;
			return new ClippedLinkInfo(Math.min(s1,s2), Math.max(s1,s2), clipStart, clipEnd);
		}
		public boolean isInReach(double time, LinearMotion linearMotion, double radius) {
			return getPosition(time).distance(linearMotion.getPosition(time)) < radius;
		}
		public boolean isInReach2(double time, LinearMotion linearMotion, double radius) {
			return getPosition(time).distance(linearMotion.getPosition(time)) <= radius;
		}		
		public void save(Output output) {
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("ENDTIME=\"");
			stringBuffer.append(endTime);
			stringBuffer.append("\" STARTX=\"");
			stringBuffer.append(startPosition.getX());
			stringBuffer.append("\" STARTY=\"");
			stringBuffer.append(startPosition.getY());
			stringBuffer.append("\" ENDX=\"");
			stringBuffer.append(endPosition.getX());
			stringBuffer.append("\" ENDY=\"");
			stringBuffer.append(endPosition.getY());
			stringBuffer.append("\"");
			output.print(stringBuffer.toString());
		}
		MutablePosition getPosition(double time) {
			if (time==startTime) return new MutablePosition(startPosition);
			MutablePosition pos = new MutablePosition(endPosition);
			pos.sub(startPosition);
			pos.scale((time - startTime) / (endTime - startTime));
			pos.add(startPosition);
			return pos;
		}
		public double getEndTime() {
			return endTime;
		}
		public double getStartTime() {
			return startTime;
		}
	}

	private static class ClippedLinkInfo {
		private double attachTime;
		private double detachTime;
		double clippingStart;
		double clippingEnd;
		public ClippedLinkInfo(double attachTime, double detachTime, double clippingStart, double clippingEnd) {
			this.attachTime = attachTime;
			this.detachTime = detachTime;
			this.clippingStart = clippingStart;
			this.clippingEnd = clippingEnd;
		}
		public double getAttachTime() {
			return attachTime;
		}
		public double getDetachTime() {
			return detachTime;
		}
		public boolean attachInsideClip() {
			return attachTime >= clippingStart && attachTime <= clippingEnd;
		}
		public boolean detachInsideClip() {
			return detachTime >= clippingStart && detachTime <= clippingEnd;
		}
	}

}