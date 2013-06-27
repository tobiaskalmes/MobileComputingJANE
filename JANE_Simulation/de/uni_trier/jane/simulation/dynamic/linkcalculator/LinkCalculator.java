
/*****************************************************************************
 * 
 * LinkCalculator.java
 * 
 * $Id: LinkCalculator.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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

/**
 * The <code>LinkCalculator</code> computes all movements of the devices and all links between 
 * devices on the fly and supplies the corresponding actions. It uses a <code>MobilitySource</code>
 * to get information on the movement of the devices. 
 */
public class LinkCalculator implements MobilityDynamicSource {

	private final static String VERSION = "$Id: LinkCalculator.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";

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
	public LinkCalculator(Output output) {
		linkReliabilityCalculator=new LinearLinkReliabilityCalculator();
		
		this.output = output;
		commandStack = new CommandStack();
		addressDeviceInfoMap = new AddressDeviceInfoMap();
		first = true;
	}
	
	public void start(MobilitySource mobilitySource){
	    commandStack.clear();
	    this.mobilitySource=mobilitySource;
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
		//device is still suspended
		if (info.isSuspended()&&!info.suspendChanged()||info.sendingRadius<0) 
            return;
		
		
		info.notifyInsertLink();
		LinearMotion linearMotion = info.getLinearMotion();
		double sendingRadius = info.getSendingRadius();
		DeviceIDIterator it = addressDeviceInfoMap.getAddressIterator();
		while(it.hasNext()) {
			DeviceID key = it.next();
			if(!key.equals(address)) {
				DeviceInfo keyInfo=addressDeviceInfoMap.getDeviceInfo(key);
				LinearMotion value =keyInfo.getLinearMotion();
				if (linearMotion.isInReach(linearMotion.getStartTime(),value,info.getSendingRadius())&&info.suspendChanged&&!keyInfo.isSuspended()){
					//if the other device is in reach and activ, the link changes.
					if (info.isSuspended()){
						commandStack.push(new DetachCommand(linearMotion.getStartTime(), address, key));
						commandStack.push(new DetachCommand(linearMotion.getStartTime(), key,address));
					}else{
						commandStack.push(new AttachCommand(linearMotion.getStartTime(),linearMotion.getEndTime(), address, key));
						commandStack.push(new AttachCommand(linearMotion.getStartTime(),linearMotion.getEndTime(), key, address));
					}
				}
				
				
				if (!keyInfo.isSuspended()&&!info.isSuspended()&&info.sendingRadius>=0){
					//both devices are activ - calculate links!
					ClippedLinkInfo linkInfo = linearMotion.getClippedLinkInfo(value, sendingRadius);
					if(linkInfo != null) {
						if(linkInfo.attachInsideClip()) {
//							if (keyInfo.isSuspended()&&keyInfo.suspendChanged){
//								commandStack.push(new DetachCommand(linkInfo.getAttachTime(), address, key));
//							}else{
								commandStack.push(new AttachCommand(linkInfo.getAttachTime(),linkInfo.clippingEnd, address, key));
//							}
						}
						else if(info.isFirstMove() && linearMotion.getPosition(linearMotion.getStartTime()).distance(value.getPosition(linearMotion.getStartTime())) == sendingRadius) {
//						if (keyInfo.isSuspended()&&keyInfo.suspendChanged){
//								commandStack.push(new DetachCommand(linkInfo.getAttachTime(), address, key));
//							}else{
								commandStack.push(new AttachCommand(linearMotion.getStartTime(),linkInfo.clippingEnd, address, key));
//							}
						}
						if(linkInfo.detachInsideClip()) {
							if (!info.isSuspended()&&!keyInfo.isSuspended()){
								commandStack.push(new DetachCommand(linkInfo.getDetachTime(), address, key));
							}
						}
						/*linkReliabilityCalculator.getLinkReliability(sendingRadius,
																 linkInfo.clippingStart,
																 linkInfo.clippingEnd,
																 linearMotion.getTrajectoryMapping(),
																 value.getTrajectoryMapping());
																 commandStack.push(new SetLinkReliabilityCommand(linkInfo.clippingStart,linkInfo.clippingEnd,address,key));*/
					}
//						else if (keyInfo.suspendChanged&&linearMotion.getPosition(linearMotion.getStartTime()).distance(value.getPosition(linearMotion.getStartTime())) <= sendingRadius){
//						if (keyInfo.isSuspended()){
//							commandStack.push(new DetachCommand(linearMotion.getStartTime(), address, key));
//							
//						}else{
//							commandStack.push(new AttachCommand(linearMotion.getStartTime(),linearMotion.getEndTime(), address, key));
//						}
//						
//					}
					linkInfo = value.getClippedLinkInfo(linearMotion, addressDeviceInfoMap.getDeviceInfo(key).getSendingRadius());
					if(linkInfo != null) {
						if(linkInfo.attachInsideClip()) {
//							if (keyInfo.isSuspended()&&keyInfo.suspendChanged){
//								commandStack.push(new DetachCommand(linkInfo.getAttachTime(), key,address));
//							}else{

								commandStack.push(new AttachCommand(linkInfo.getAttachTime(),linkInfo.clippingEnd, key, address));
//							}
						}
						else if(info.isFirstMove() && value.getPosition(linearMotion.getStartTime()).distance(linearMotion.getPosition(linearMotion.getStartTime())) == addressDeviceInfoMap.getDeviceInfo(key).getSendingRadius()) {
//							if (keyInfo.isSuspended()&&keyInfo.suspendChanged){
//								commandStack.push(new DetachCommand(linkInfo.getAttachTime(), key,address));
//							}else{

								commandStack.push(new AttachCommand(linearMotion.getStartTime(),linkInfo.clippingEnd, key, address));
//							}
						}
						if(linkInfo.detachInsideClip()) {
//							if (!info.isSuspended()&&!keyInfo.isSuspended()){
								commandStack.push(new DetachCommand(linkInfo.getDetachTime(), key, address));
//							}
						}
					//commandStack.push(new SetLinkReliabilityCommand(linkInfo.clippingStart,linkInfo.clippingEnd,address,key));
					}
//					if (keyInfo.suspendChanged&&linearMotion.getPosition(linearMotion.getStartTime()).distance(value.getPosition(linearMotion.getStartTime())) <= sendingRadius){
//						if (keyInfo.isSuspended()){
//							commandStack.push(new DetachCommand(linearMotion.getStartTime(), key, address));
//							
//						}else{
//							commandStack.push(new AttachCommand(linearMotion.getStartTime(),linearMotion.getEndTime(), key, address));
//						}
//						
//					}
				}
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
				DeviceInfo keyInfo=addressDeviceInfoMap.getDeviceInfo(key);
				if (!keyInfo.isSuspended()){
					LinearMotion value =keyInfo.getLinearMotion();
					if(linearMotion.isInReach2(time, value, sendingRadius)&&addressDeviceInfoMap.getDeviceInfo(key).getSendingRadius()>=0) { // TODO: isInReach wurde mit isInReach2 ausgetauscht -> ist das korrekt so???
						commandStack.push(new AttachCommand(time,Math.min(linearMotion.getEndTime(),value.getEndTime()), address, key));
					}
					if(value.isInReach2(time, linearMotion, addressDeviceInfoMap.getDeviceInfo(key).getSendingRadius())&&sendingRadius>=0) { // TODO: isInReach wurde mit isInReach2 ausgetauscht -> ist das korrekt so???
						commandStack.push(new AttachCommand(time,Math.min(linearMotion.getEndTime(),value.getEndTime()), key, address));
					}
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
				
				
				addressDeviceInfoMap.setDeviceInfo(address,new DeviceInfo(sendingRadius,linearMotion,arrivalInfo.isSuspended())); 


				commandStack.push(new ArrivalCommand(arrivalInfo.getTime(), address, arrivalInfo.getPosition()));
				insertLinkCommands(address);
				if (!arrivalInfo.isSuspended()){
					insertEnterLinkCommands(address, getTime());
				}

				return new EnterAction(getTime(), address, linearMotion.getTrajectoryMapping(),arrivalInfo.isSuspended(), new ConstantDoubleMapping(sendingRadius));
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
				addressDeviceInfoMap.getDeviceInfo(address).setSuspend(arrivalInfo.isSuspended());

				commandStack.push(new ArrivalCommand(arrivalInfo.getTime(), address, arrivalInfo.getPosition()));
				insertLinkCommands(address);

				saveArrival = true;
				return new SetPositionMappingAction(getTime(), linearMotion.getTrajectoryMapping(),arrivalInfo.isSuspended(), address);
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
				//return new NopAction();
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
		private double validTime;
		private DeviceID sender;
		private DeviceID receiver;
		public AttachCommand(double time, double validTime, DeviceID sender, DeviceID receiver) {
			super(time, 2);
			this.sender = sender;
			this.validTime=validTime;
			this.receiver = receiver;
		}
		public Action handle() {
			return new AttachAction(getTime(), sender, receiver, new DoubleMappingIntervalImplementation(new ConstantDoubleMapping(1.0),getTime(),validTime));
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

		private double validTime;
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
			double validTime,
			DoubleMappingInterval linkReliability,
			DeviceID receiver,
			DeviceID sender) {
			
			super(time,2);
			this.receiver = receiver;
			this.sender = sender;
			this.validTime = validTime;
			this.linkReliability=linkReliability;
		}
		/* (non-Javadoc)
		 * @see de.uni_trier.ubi.appsim.kernel.dynamic.LinkCalculator.Command#handle()
		 */
		public Action handle() {
			return new AttachAction(getTime(),sender,receiver,linkReliability);//new DoubleMappingIntervalImplementation(new ConstantDoubleMapping(1.0),getTime(),validTime));
		}
		/* (non-Javadoc)
		 * @see de.uni_trier.ubi.appsim.kernel.dynamic.LinkCalculator.Command#save(de.uni_trier.ubi.appsim.kernel.Output)
		 */
		public void save(Output output) {
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("  <LINKRELIABILITYCHANGE TIME=\"");
			stringBuffer.append(getTime());
			stringBuffer.append("\" VALIDTIME=\"");
			stringBuffer.append(validTime);
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
			init();
		}

		/**
         * 
         */
        public void clear() {
            init();
        }

        /**
         * 
         */
        private void init() {
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
		private boolean suspended;
		boolean suspendChanged;
		
		
		public DeviceInfo(double sendingRadius, LinearMotion linearMotion, boolean suspended) {
			this.sendingRadius = sendingRadius;
			this.linearMotion = linearMotion;
			insertCount = 0;
			this.suspended=suspended;
			suspendChanged=false;
			
		}
		/**
		 * @param b
		 */
		public void setSuspend(boolean suspended) {
			suspendChanged=suspended!=this.suspended;
			this.suspended=suspended;
			
		}
		/**
		 * @return true if suspend flag changed
		 */
		public boolean suspendChanged() {
			
			return suspendChanged;
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
		/**
		 * @return Returns true if the device is suspended.
		 */
		public boolean isSuspended() {
			return suspended;
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
					//iterator.remove();
					throw new IllegalAccessError("Remove not supported");

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
				throw new IllegalArgumentException("start time has to be less than end time or if equal, positions have to be the same.");
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
			if (startTime==endTime){
				return new MutablePosition(startPosition);
			}
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
		private double clippingStart;
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