/*****************************************************************************
 * 
 * LinkCalculator.java
 * 
 * $Id: GridLinkCalculator.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
public class GridLinkCalculator implements DynamicSource {

	private final static String VERSION = "$Id: GridLinkCalculator.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";

	private MobilitySource mobilitySource;
	private Output output;
	private CommandStack commandStack;
	private AddressDeviceInfoMap addressDeviceInfoMap;
	private GridAddressMap gridAddressMap;
	private boolean first;
	private int maxColumns;
	private int maxRows;

	/**
	 * Constructs a new <code>LinkCalculator</code>. If an output is defined (i.e. not null) then an XML file containing 
	 * all Actions calculated by the LinkCalculator will be written to the output.
	 * @param mobilitySource the source providing all information about device mobility
	 * @param output the output to use. If output is null, no output will be written.
	 */
	public GridLinkCalculator(MobilitySource mobilitySource, Output output) {
		this.mobilitySource = mobilitySource;
		this.output = output;
		commandStack = new CommandStack();
		addressDeviceInfoMap = new AddressDeviceInfoMap();
		gridAddressMap = new GridAddressMap();
		maxRows = ((int)mobilitySource.getRectangle().getHeight())/Grid.GRIDHEIGHT;
		maxColumns = ((int)mobilitySource.getRectangle().getWidth())/Grid.GRIDWIDTH;
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

	private void insertLinkCommands(DeviceID address, DeviceIDSet devices) {
		LinearMotion linearMotion = addressDeviceInfoMap.getDeviceInfo(address).getLinearMotion();
		double sendingRadius = addressDeviceInfoMap.getDeviceInfo(address).getSendingRadius();
		DeviceIDIterator it = devices.iterator();
		while(it.hasNext()) {
			DeviceID key = it.next();
			if(!key.equals(address)) {
				LinearMotion value = addressDeviceInfoMap.getDeviceInfo(key).getLinearMotion();
				ClippedLinkInfo linkInfo = linearMotion.getClippedLinkInfo(value, sendingRadius);
				if(linkInfo != null) {
					if(linkInfo.attachInsideClip()) {
						commandStack.push(new AttachCommand(linkInfo.getAttachTime(), address, key));
					}
					if(linkInfo.detachInsideClip()) {
						commandStack.push(new DetachCommand(linkInfo.getDetachTime(), address, key));
					}
				}
				linkInfo = value.getClippedLinkInfo(linearMotion, addressDeviceInfoMap.getDeviceInfo(key).getSendingRadius());
				if(linkInfo != null) {
					if(linkInfo.attachInsideClip()) {
						commandStack.push(new AttachCommand(linkInfo.getAttachTime(), key, address));
					}
					if(linkInfo.detachInsideClip()) {
						commandStack.push(new DetachCommand(linkInfo.getDetachTime(), key, address));
					}
				}
			}
		}
	}

	private void insertEnterLinkCommands(DeviceID address, DeviceIDSet devices, double time) {
		LinearMotion linearMotion = addressDeviceInfoMap.getDeviceInfo(address).getLinearMotion();
		double sendingRadius = addressDeviceInfoMap.getDeviceInfo(address).getSendingRadius();
		DeviceIDIterator it = devices.iterator();
		while(it.hasNext()) {
			DeviceID key = it.next();
			if(!key.equals(address)) {
				LinearMotion value = addressDeviceInfoMap.getDeviceInfo(key).getLinearMotion();
				if(linearMotion.isInReach(time, value, sendingRadius)) {
					commandStack.push(new AttachCommand(time, address, key));
				}
				if(value.isInReach(time, linearMotion, addressDeviceInfoMap.getDeviceInfo(key).getSendingRadius())) {
					commandStack.push(new AttachCommand(time, key, address));
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
				if(linearMotion.isInReach(time, value, sendingRadius)) {
					exitInfoList.add(new ExitInfo(key, false));
				}
				if(value.isInReach(time, linearMotion, addressDeviceInfoMap.getDeviceInfo(key).getSendingRadius())) {
					exitInfoList.add(new ExitInfo(key, true));
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
				// get all covered grids
				GridSet gridSet = linearMotion.getCoveredGrids(sendingRadius, maxColumns, maxRows);
				addressDeviceInfoMap.setDeviceInfo(address, new DeviceInfo(sendingRadius, linearMotion, gridSet));
				// get affected devices
				DeviceIDSet addressSet = gridAddressMap.getAddressSetForGridSet(gridSet);
				// add this device to the gridmap
				gridAddressMap.addBinding(address, gridSet);
				
				commandStack.push(new ArrivalCommand(arrivalInfo.getTime(), address, arrivalInfo.getPosition()));
				insertLinkCommands(address, addressSet);
				insertEnterLinkCommands(address, addressSet, getTime());
				
				return new EnterAction(getTime(), address, linearMotion.getTrajectoryMapping(),false, new ConstantDoubleMapping(sendingRadius));
			}
			else {
				throw new IllegalStateException("An entered device is not allowed to exit immediately.");
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

		private static final int UNDEF = 0;
		private static final int EXIT = 1;
		private static final int DETACH = 2;
		private static final int ARRIVAL = 3;
		
		private DeviceID address;
		private Position position;
		
		private int state;
		private ExitInfo detachedDevice;

		public ArrivalCommand(double time, DeviceID address, Position position) {
			super(time, 1);
			this.address = address;
			this.position = position;
			state = UNDEF;
			detachedDevice = null;
		}
		public Action handle() {
			if(mobilitySource.hasNextArrivalInfo(address)) {
				LinearMotion linearMotion;
				MobilitySource.ArrivalInfo arrivalInfo = mobilitySource.getNextArrivalInfo(address);
				linearMotion = new LinearMotion(getTime(), arrivalInfo.getTime(), position, arrivalInfo.getPosition());
				DeviceInfo deviceInfo = addressDeviceInfoMap.getDeviceInfo(address);				
				GridSet oldGridSet = deviceInfo.getGridSet();
				gridAddressMap.removeBinding(address, oldGridSet);							
				GridSet gridSet = linearMotion.getCoveredGrids(deviceInfo.getSendingRadius(), maxColumns, maxRows);
				DeviceIDSet devices = gridAddressMap.getAddressSetForGridSet(gridSet);				
				deviceInfo.setLinearMotion(linearMotion);
				deviceInfo.setGridSet(gridSet);
				gridAddressMap.addBinding(address, gridSet);

				commandStack.push(new ArrivalCommand(arrivalInfo.getTime(), address, arrivalInfo.getPosition()));
				insertLinkCommands(address, devices);

				state = ARRIVAL;
				return new SetPositionMappingAction(getTime(), linearMotion.getTrajectoryMapping(),false, address);
			}
			else {
				ExitInfo[] exitInfoArray = insertExitLinkCommands(address, getTime()); // FIXME: change to use Grid!
				if(exitInfoArray.length > 0) {
					commandStack.push(new ExitCommand(getTime(), address));
					for(int i=0; i<exitInfoArray.length-1; i++) {
						if(exitInfoArray[i].isSender()) {
							commandStack.push(new DetachCommand(getTime(), exitInfoArray[i].getAddress(), address));
						}
						else {
							commandStack.push(new DetachCommand(getTime(), address, exitInfoArray[i].getAddress()));
						}
					}
					state = DETACH;
					detachedDevice = exitInfoArray[exitInfoArray.length-1];
					if(exitInfoArray[exitInfoArray.length-1].isSender()) {
						return new DetachAction(getTime(), exitInfoArray[exitInfoArray.length-1].getAddress(), address);
					}
					else {
						return new DetachAction(getTime(), address, exitInfoArray[exitInfoArray.length-1].getAddress());
					}
				}
				else {
					DeviceInfo deviceInfo = addressDeviceInfoMap.getDeviceInfo(address);
					GridSet gridSet = deviceInfo.getGridSet();
					gridAddressMap.removeBinding(address, gridSet);
					addressDeviceInfoMap.removeDeviceInfo(address);
					state = EXIT;
					return new ExitAction(getTime(), address);
				}
			}
		}
		public void save(Output output) {
			switch(state) {
				case UNDEF :
					throw new IllegalStateException("Handle was not previously called.");
				case DETACH :
					StringBuffer stringBuffer = new StringBuffer();
					stringBuffer.append("  <DETACH TIME=\"");
					stringBuffer.append(getTime());
					if(detachedDevice.isSender()) {
						stringBuffer.append("\" SENDER=\"");
						stringBuffer.append(detachedDevice.getAddress());
						stringBuffer.append("\" RECEIVER=\"");
						stringBuffer.append(address);
					}
					else {
						stringBuffer.append("\" SENDER=\"");
						stringBuffer.append(address);
						stringBuffer.append("\" RECEIVER=\"");
						stringBuffer.append(detachedDevice.getAddress());
					}
					stringBuffer.append("\"/>");
					output.println(stringBuffer.toString());
					break;
				case ARRIVAL :
					LinearMotion linearMotion = addressDeviceInfoMap.getDeviceInfo(address).getLinearMotion();
					stringBuffer = new StringBuffer();
					stringBuffer.append("  <ARRIVAL TIME=\"");
					stringBuffer.append(getTime());
					stringBuffer.append("\" ADDRESS=\"");
					stringBuffer.append(address);
					stringBuffer.append("\" ");
					output.print(stringBuffer.toString());
					linearMotion.save(output);
					output.println("/>");
					break;
				case EXIT :
					stringBuffer = new StringBuffer();
					stringBuffer.append("  <EXIT TIME=\"");
					stringBuffer.append(getTime());
					stringBuffer.append("\" ADDRESS=\"");
					stringBuffer.append(address);
					stringBuffer.append("\"/>");
					output.println(stringBuffer.toString());
					break;
			}
		}


	}

	private class AttachCommand extends Command {
		private DeviceID sender;
		private DeviceID receiver;
		public AttachCommand(double time, DeviceID sender, DeviceID receiver) {
			super(time, 2);
			this.sender = sender;
			this.receiver = receiver;
		}
		public Action handle() {
			return new AttachAction(getTime(), sender, receiver,  null);//new ConstantDoubleMapping(1.0));
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
			DeviceInfo deviceInfo = addressDeviceInfoMap.getDeviceInfo(address);
			GridSet gridSet = deviceInfo.getGridSet();
			gridAddressMap.removeBinding(address, gridSet);
			addressDeviceInfoMap.removeDeviceInfo(address);			
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

//	private static class CommandStack {
//		private LinkedList stack;
//		private double time;
//		public CommandStack() {
//			stack = new LinkedList();
//			time = 0;
//		}
//		public boolean isEmpty() {
//			return stack.isEmpty();
//		}
//		public void push(Command command) {
//			if (command.getTime() < time) {
//				throw new IllegalArgumentException("Can't add command in the past.");
//			}
//			ListIterator it = stack.listIterator();
////int counter = 0;			
//			while(it.hasNext()) {
////counter++;				
//				Command entry = (Command)it.next();
//				if(command.getTime() <= entry.getTime()) {
//					it.previous();
////System.out.println("@@@@ counter = "+counter);					
//					it.add(command);
//					return;
//				}
//			}
////System.out.println("@@@@# counter = "+counter);			
//			stack.add(command);			
//		}
//		public Command pop() {
//			if(stack.isEmpty()) {
//				throw new IllegalStateException("The stack is empty.");
//			}
//			Command command = (Command)stack.removeFirst();
//			time = command.getTime();
//			return command;
//		}
//	}

//	private static class CommandStack {
//		private TreeSet treeSet;
//		private double time;
//		
//		public CommandStack() {
//			treeSet = new TreeSet(new Comparator() {
//				public int compare(Object o1, Object o2) {
//					double t1 = ((Command) o1).getTime();
//					double t2 = ((Command) o2).getTime();					
//					if (t1 < t2) {
//						return -1;
//					} else if (t1 == t2) { // FIXME : double and == ?
//						return 0;
//					} else {
//						return 1;
//					}
//				}
//				public boolean equals(Object obj) {
//					return false;
//				}
//			});
//			time = 0;			
//		}
//		
//		public boolean isEmpty() {
//			return treeSet.isEmpty();
//		}
//		
//		public void push(Command command) {
//			if (command.getTime() < time) {
//				throw new IllegalArgumentException("Can't add command in the past!");
//			}
//			treeSet.add(command);
//		}
//		
//		public Command pop() {
//			if(treeSet.isEmpty()) {
//				throw new IllegalStateException("The stack is empty.");
//			}
//			Command command = (Command) treeSet.first();
//			treeSet.remove(command);
//			time = command.getTime();
//			return command;
//		}
//	}

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
		private GridSet gridSet;
		
		public DeviceInfo(double sendingRadius, LinearMotion linearMotion, GridSet gridSet) {
			this.sendingRadius = sendingRadius;
			this.linearMotion = linearMotion;
			this.gridSet = gridSet;
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
		public GridSet getGridSet() {
			return gridSet;
		}
		public void setGridSet(GridSet gridSet) {
			this.gridSet = gridSet;
		}

	}

	private static class GridAddressMap {
		private HashMap gridAddressMap;
		
		public GridAddressMap() {
			gridAddressMap = new HashMap();
		}
		public DeviceIDSet getAddressSetForGridSet(GridSet gridSet) {
			HashSet result = new HashSet();
			Iterator iter = gridSet.iterator();
			while (iter.hasNext()) {
				Grid grid = (Grid) iter.next();
				Set gridAddresses = (Set) gridAddressMap.get(grid);
				if (gridAddresses != null) {
					result.addAll(gridAddresses);
				}
			}
			return new DeviceIDSet(result);
		}
		public void addBinding(DeviceID address, GridSet gridSet) {
			Iterator iter = gridSet.iterator();
			while (iter.hasNext()) {
				Grid grid = (Grid) iter.next();
				Set gridAddresses = (Set) gridAddressMap.get(grid);
				if (gridAddresses != null) {
					gridAddresses.add(address);
				} else {
					Set set = new HashSet();
					set.add(address);
					gridAddressMap.put(grid, set);
				}
			}
		}
		
		public void removeBinding(DeviceID address, GridSet gridSet) {
			Iterator iter = gridSet.iterator();
			while (iter.hasNext()) {
				Grid grid = (Grid) iter.next();
				Set gridAddresses = (Set) gridAddressMap.get(grid);
				if (gridAddresses != null) {
					gridAddresses.remove(address);
				} else {
					throw new Error("Trying to remove non-existing binding!"); // FIXME
				}
			}
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
				public void remove() {
					iterator.remove();
					
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
			if(startTime >= endTime) {
				throw new IllegalArgumentException("start time has to be less than end time.");
			}
			this.startTime = startTime;
			this.endTime = endTime;
			this.startPosition = startPosition;
			this.endPosition = endPosition;
		}
		public TrajectoryMapping getTrajectoryMapping() {
			return new PositionMappingTrajectory(new LinearPositionMapping(startTime, endTime, startPosition, endPosition), new ConstantPositionMapping(getPosition(1).sub(getPosition(0))));
		}
		public ClippedLinkInfo getClippedLinkInfo(LinearMotion linearMotion, double radius) {
			double clipStart = Math.max(startTime, linearMotion.startTime);
			double clipEnd = Math.min(endTime, linearMotion.endTime);
			if(clipStart > clipEnd) {
				return null;
			}
			Position normalizedStartPosition = linearMotion.getPosition(0).sub(getPosition(0));
			Position normalizedEndPosition = linearMotion.getPosition(1).sub(getPosition(1));
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
		private Position getPosition(double time) {
			return endPosition.sub(startPosition).scale((time - startTime) / (endTime - startTime)).add(startPosition);
		}
		
		private GridSet getCoveredGrids(double radius, int maxColumns, int maxRows) {
			if (startPosition.equals(endPosition)) {
				// special case: just find all grids covered by the sending radius
				GridSet gridSet = new GridSet(maxColumns, maxRows);
				Position upperLeft = startPosition.sub(new Position(radius, radius));
				Position lowerRight = startPosition.add(new Position(radius, radius));
				Grid startGrid = new Grid(upperLeft);
				Grid endGrid = new Grid(lowerRight);
				for(int i=startGrid.getColumn(); i<=endGrid.getColumn(); i++) {				
					for(int j=startGrid.getRow(); j<=endGrid.getRow(); j++) {					
						gridSet.add(new Grid(i, j));												
					}
				}
				return gridSet;				
			}		
			double tmp = radius / startPosition.distance(endPosition);
			double b = tmp * startPosition.distanceY(endPosition);
			double a = tmp * startPosition.distanceX(endPosition);
			Position offset1 = new Position(a,b);
			Position offset2 = new Position(-b,a);
			Position p1 = startPosition.sub(offset1).add(offset2);
			Position p2 = endPosition.add(offset1).add(offset2);
			Position p3 = endPosition.add(offset1).sub(offset2);
			Position p4 = startPosition.sub(offset1).sub(offset2);
			
			GridSet gridSet = getLineCoverage(p1, p2, maxColumns, maxRows);
			gridSet.merge(getLineCoverage(p2, p3, maxColumns, maxRows));
			gridSet.merge(getLineCoverage(p3, p4, maxColumns, maxRows));
			gridSet.merge(getLineCoverage(p4, p1, maxColumns, maxRows));			
			// fill the rectangle
			gridSet.fillGaps();			
			// remove virtual grids from gridset
			gridSet.removeVirtual();
			
			return gridSet;
		}
		
		private GridSet getLineCoverage(Position p1, Position p2, int maxColumns, int maxRows) {
//			System.out.println("-----");
			double deltaX = p1.getX()-p2.getX();			
			if (deltaX > 0) {
				// swap points
				Position tmp = p1;
				p1 = p2;
				p2 = tmp;
				deltaX = -deltaX;
			}
			double deltaY = p1.getY() - p2.getY();
			if (deltaX == 0) {
				throw new Error("NOT IMPLEMENTED: Senkrecht!");
			} else if (deltaY == 0) {
				throw new Error("NOT IMPLEMENTED: Waagerecht!");
			}
			
			int yIncrement = (deltaY > 0 ? -1 : 1);			
			GridSet gridSet = new GridSet(maxColumns, maxRows);			
			Grid grid = new Grid(p1);
			Grid endGrid = new Grid(p2);
//			if (grid.equals(endGrid)) {
//				System.out.println("Start: "+grid+", Ende: "+endGrid);
//			}
			while(!grid.equals(endGrid)) {
//				if ((grid.getColumn() > endGrid.getColumn()) ||
//					(yIncrement == -1 && grid.getRow() < endGrid.getRow()) ||
//					(yIncrement == 1 && grid.getRow() > endGrid.getRow())) { 
//					throw new Error("Am Ziel vorbeigelaufen ?!");
//				}
//				System.out.println("adding grid "+grid.getColumn()+","+grid.getRow());
				gridSet.add(grid);
				double yValue = (deltaY/deltaX)*(((grid.getColumn()+1)*Grid.GRIDWIDTH) - p1.getX()) + p1.getY();
				double gridY = grid.getRow()*Grid.GRIDHEIGHT;				
				if (yValue >= gridY && yValue <= gridY+Grid.GRIDHEIGHT) {
					// inside
					grid = new Grid(grid.getColumn()+1, grid.getRow());					
				} else {
					// outside --> move up or down
					grid = new Grid(grid.getColumn(), grid.getRow() + yIncrement);
				}
			}
			gridSet.add(endGrid);
//			System.out.println("adding grid "+endGrid.getColumn()+","+endGrid.getRow());
			return gridSet; 
		}				 
	}
	
	private static class Grid { 
		public static final int GRIDWIDTH  = 20;  // FIXME
		public static final int GRIDHEIGHT = 20;  // FIXME
		private int column;
		private int row;
				
		public Grid(Position position) {
			row = ((int)position.getY())/GRIDHEIGHT; 
			if (position.getY() < 0) {
				row--;
			}
			column = ((int)position.getX())/GRIDWIDTH;
			if (position.getX() < 0) {
				column--;
			}
		}	
				
		public Grid(int column, int row) {
			this.column = column;
			this.row = row;
		}
		
		public int getColumn() {
			return column;
		}

		public int getRow() {
			return row;
		}
		
		public int hashCode() {
			final int PRIME = 1000003;
			int result = 0;
			result = PRIME * result + column;
			result = PRIME * result + row;
			return result;
		}

		public boolean equals(Object oth) {
			if (this == oth) {
				return true;
			}
			if (oth == null) {
				return false;
			}
			if (oth.getClass() != getClass()) {
				return false;
			}
			Grid other = (Grid) oth;
			if (this.column != other.column) {
				return false;
			}
			if (this.row != other.row) {
				return false;
			}
			return true;
		}
		
		public String toString() {
			return "("+column+","+row+")";
		}
	}
	
	private static class GridSet {
		private TreeSet gridSet;
		private int maxRow;
		private int maxColumn;
		
		public GridSet(int maxColumn, int maxRow) {
			this.maxColumn = maxColumn;
			this.maxRow = maxRow;
			gridSet = new TreeSet(new Comparator() {
							public int compare(Object o1, Object o2) {
								Grid a = (Grid) o1;
								Grid b = (Grid) o2;
								if (a.getRow() < b.getRow()) {
									return -1;
								} else if (a.getRow() == b.getRow()) {
									if (a.getColumn() < b.getColumn()) {
										return -1;
									} else if (a.getColumn() == b.getColumn()) {
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
		}
		
		public void add(Grid grid) {
			gridSet.add(grid);
		}
		
		public void merge(GridSet otherGridSet) {
			gridSet.addAll(otherGridSet.gridSet);
		}
		
		public void fillGaps () {
			ArrayList gridsToAdd = new ArrayList();			 
			Iterator iter = gridSet.iterator();
			Grid left = null;
			while (iter.hasNext()) {				
				// get the leftmost element in that row
				if (left == null) {
					left = (Grid) iter.next();
				}
				int row = left.getRow();
				// now search the rightmost one
				Grid right = null;
				Grid next = null;
				while(iter.hasNext()) {
					next = (Grid) iter.next();
					if (next.getRow() != row) {
						break;
					}
					right = next; 
				}
				if (right != null) {
					// more than one element in this row
					int minColumn = left.getColumn();
					int maxColumn = right.getColumn();
					for(int i=minColumn+1; i<maxColumn; i++) {
						gridsToAdd.add(new Grid(i, row));
					}
				}
				left = next;
			}
			// add all new grids
			gridSet.addAll(gridsToAdd);			
		}
		
		private void removeVirtual() {
			Iterator iter = gridSet.iterator();
			while (iter.hasNext()) {
				Grid grid = (Grid) iter.next();
				if (grid.getColumn() < 0 ||
				    grid.getRow() < 0    ||
				    grid.getColumn() > maxColumn ||
				    grid.getRow() > maxRow) {
				   // remove
				   iter.remove();
				}
			}
		}

		public Iterator iterator() {
			return gridSet.iterator(); // FIXME ??
		}		
	}
	
	private static class ClippedLinkInfo {
		private double attachTime;
		private double detachTime;
		private double clippingStart;
		private double clippingEnd;
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


    public Condition getTerminalCondition(Clock clock) {     
        return mobilitySource.getTerminalCondition(clock);
    }

}

