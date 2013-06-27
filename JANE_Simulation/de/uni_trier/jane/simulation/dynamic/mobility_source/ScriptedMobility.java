/*****************************************************************************
 * 
 * ScriptedMobility.java
 * 
 * $Id: ScriptedMobility.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
 *  
 * Copyright (C) 2003 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This class allows the definition of scripted mobility patterns. See the
 */
public class ScriptedMobility implements MobilitySource {

	
	private final static String VERSION = "$Id: ScriptedMobility.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";
	
	private ScriptedMobilityData scriptedMobilityData;
	private int currentNode;
	private Set arrivedSet;

	private ScriptedMobility(ScriptedMobilityData scriptedMobilityData) {
		this.scriptedMobilityData = scriptedMobilityData;
		currentNode = 0;
		arrivedSet = new HashSet();
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#hasNextEnterInfo()
	 */
	public boolean hasNextEnterInfo() {
		return currentNode < scriptedMobilityData.getSize();
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getNextEnterInfo()
	 */
	public EnterInfo getNextEnterInfo() {
		NodeData data = scriptedMobilityData.getNodeData(currentNode);
		currentNode++;
		return data.getEnterInfo();
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#hasNextArrivalInfo(de.uni_trier.ubi.appsim.kernel.basetype.Address)
	 */
	public boolean hasNextArrivalInfo(DeviceID address) {
		NodeData data = scriptedMobilityData.getNodeData(address);
		return data.hasNextArrivalInfo();
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getNextArrivalInfo(de.uni_trier.ubi.appsim.kernel.basetype.Address)
	 */
	public ArrivalInfo getNextArrivalInfo(DeviceID address) {
		NodeData data = scriptedMobilityData.getNodeData(address);
		return data.getNextArrivalInfo();
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getRectangle()
	 */
	public Rectangle getRectangle() {
		return scriptedMobilityData.getRectangle();
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getTotalDeviceCount()
	 */
	public int getTotalDeviceCount() {
		return scriptedMobilityData.getSize();
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getShape()
	 */
	public Shape getShape() {
		return EmptyShape.getInstance();
	}

	/**
	 * Create an instance of this class using an xml description. The xml file has to
	 * follow the format specified by <code>scripted_mobility.dtd</code>.
	 * @param fileName the name of the xml file
	 * @return an instance of this class
	 */
	public static MobilitySource createFromFile(String fileName) {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setIgnoringElementContentWhitespace(true);
		documentBuilderFactory.setIgnoringComments(true);
		documentBuilderFactory.setValidating(true);
		DocumentBuilder documentBuilder;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}
		File file = new File(fileName);
		Document document;
		try {
			document = documentBuilder.parse(file);
		}
		catch (SAXException e) {
			e.printStackTrace();
			return null;
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		ScriptedMobilityData data = parseScriptedMobility(document.getElementsByTagName("scripted_mobility").item(0));
		return new ScriptedMobility(data);
	}

	private static ScriptedMobilityData parseScriptedMobility(Node node) {
		Map map = new HashMap();
		List list = new ArrayList();
		Rectangle rectangle = null;
		NodeList nodeList = node.getChildNodes();
		double minimumRadius = Double.MAX_VALUE;
		double maximumRadius = 0;
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node item = nodeList.item(i);
			String nodeName = item.getNodeName();
			if (nodeName.equalsIgnoreCase("node")) {
				NodeData data = parseNode(item);
				minimumRadius = Math.min(minimumRadius, data.getSendingRadius());
				maximumRadius = Math.max(maximumRadius, data.getSendingRadius());
				map.put(data.getAddress(), data);
				list.add(data);
				if (rectangle == null) {
					rectangle = data.getRectangle();
				}
				else {
					rectangle = rectangle.union(data.getRectangle());
				}
			}
		}
		return new ScriptedMobilityData(map, list, rectangle, minimumRadius, maximumRadius);
	}

	private static NodeData parseNode(Node node) {
		DeviceID address = null;
		Position position = null;
		double sendingRadius = 0;
		MobilityData mobilityData = null;
		NodeList nodeList = node.getChildNodes();
		Node mobilityItem = null;
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node item = nodeList.item(i);
			String nodeName = item.getNodeName();
			if (nodeName.equalsIgnoreCase("address")) {
				address = new SimulationDeviceID(parseInt(item));
			}
			else if (nodeName.equalsIgnoreCase("position")) {
				position = parsePosition(item);
			}
			else if (nodeName.equalsIgnoreCase("sending_radius")) {
				sendingRadius = parseDouble(item);
			}
			else if (nodeName.equalsIgnoreCase("mobility")) {
				mobilityItem = item;
			}
		}
		if (mobilityItem != null) {
			mobilityData = parseMobility(mobilityItem, position);
		}
		else {
			mobilityData = new MobilityData(new ArrayList(), position);
		}
		return new NodeData(address, position, sendingRadius, mobilityData);
	}

	private static Position parsePosition(Node node) {
		double x = 0;
		double y = 0;
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node item = nodeList.item(i);
			String nodeName = item.getNodeName();
			if (nodeName.equalsIgnoreCase("x")) {
				x = parseDouble(item);
			}
			else if (nodeName.equalsIgnoreCase("y")) {
				y = parseDouble(item);
			}
		}
		return new Position(x, y);
	}

	private static MobilityData parseMobility(Node node, Position startPosition) {
		List mobilityList = new ArrayList();
	//
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node item = nodeList.item(i);
			String nodeName = item.getNodeName();
			if (nodeName.equalsIgnoreCase("move")) {
				mobilityList.add(parseMoveData(item));
			}
		}
		return new MobilityData(mobilityList, startPosition);
	}

	/**
	 * @param item
	 * @return
	 */
	private static ExitData parseExitData(Node node) {
		double time=0;
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node item = nodeList.item(i);
			String nodeName = item.getNodeName();
			if (nodeName.equalsIgnoreCase("time")) {
				time = parseDouble(item);
			}
		}
		return new ExitData(time);
	}

	private static MoveData parseMoveData(Node node) {
		double startTime = 0;
		double endTime = 0;
		Position newPosition = null;
		NodeList nodeList = node.getChildNodes();
		boolean suspended=false;
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node item = nodeList.item(i);
			String nodeName = item.getNodeName();
			if (nodeName.equalsIgnoreCase("start_time")) {
				startTime = parseDouble(item);
			}
			else if (nodeName.equalsIgnoreCase("end_time")) {
				endTime = parseDouble(item);
			}
			else if (nodeName.equalsIgnoreCase("new_position")) {
				newPosition = parsePosition(item);
			}else if (nodeName.equalsIgnoreCase("suspend")){
				suspended=true;
			}
		}
		return new MoveData(startTime, endTime, newPosition, suspended);
	}

	private static int parseInt(Node node) {
		node = node.getFirstChild();
		return Integer.parseInt(node.getNodeValue());
	}

	private static double parseDouble(Node node) {
		node = node.getFirstChild();
		return Double.parseDouble(node.getNodeValue());
	}

	private static class ScriptedMobilityData {
		private Map addressNodeDataMap;
		private List nodeDataList;
		private Rectangle rectangle;
		private double minimumRadius;
		private double maximumRadius;
		public ScriptedMobilityData(Map addressNodeDataMap, List nodeDataList, Rectangle rectangle, double minimumRadius, double maximumRadius) {
			this.addressNodeDataMap = addressNodeDataMap;
			this.nodeDataList = nodeDataList;
			this.rectangle = rectangle;
			this.minimumRadius = minimumRadius;
			this.maximumRadius = maximumRadius;
		}
		public Rectangle getRectangle() {
			return rectangle;
		}
		public NodeData getNodeData(DeviceID address) {
			return (NodeData) addressNodeDataMap.get(address);
		}
		public NodeData getNodeData(int i) {
			return (NodeData) nodeDataList.get(i);
		}
		public int getSize() {
			return nodeDataList.size();
		}
		public double getMaximumRadius() {
			return maximumRadius;
		}
		public double getMinimumRadius() {
			return minimumRadius;
		}
	}

	private static class NodeData {
		private DeviceID address;
		private Position position;
		private double sendingRadius;
		private MobilityData mobilityData;
		public NodeData(DeviceID address, Position position, double sendingRadius, MobilityData mobilityData) {
			this.address = address;
			this.position = position;
			this.sendingRadius = sendingRadius;
			this.mobilityData = mobilityData;
		}
		public EnterInfo getEnterInfo() {
			return new EnterInfo(address, sendingRadius, new ArrivalInfo(position, 0));
		}
		public boolean hasNextArrivalInfo() {
			return mobilityData.hasNextArrivalInfo();
		}
		public ArrivalInfo getNextArrivalInfo() {
			return mobilityData.getNextArrivalInfo();
		}
		public DeviceID getAddress() {
			return address;
		}
		public Rectangle getRectangle() {
			return mobilityData.getRectangle(position);
		}
		public double getSendingRadius() {
			return sendingRadius;
		}
	}

	private static class MobilityData {
		private List moveDataList;
		private Position oldPosition;
		private Iterator iterator;
		private MoveData currentMoveData;
		private boolean isFinished;
		
		public MobilityData(List moveDataList, Position startPosition) {
			this.moveDataList = moveDataList;
		
			this.oldPosition = startPosition;
			iterator = moveDataList.iterator();
			currentMoveData = nextMoveData();
			isFinished = false;
		}
		public boolean hasNextArrivalInfo() {
			return !isFinished;
		}
		public ArrivalInfo getNextArrivalInfo() {
			if (currentMoveData == null) {
				isFinished = true;
				return new ArrivalInfo(oldPosition, Double.MAX_VALUE);
			}
			else {
				if (!currentMoveData.hasNextArrivalInfo()) {
					oldPosition = currentMoveData.getNewPosition();
					currentMoveData = nextMoveData();
					return getNextArrivalInfo();
				}
				else {
					return currentMoveData.getNextArrivalInfo(oldPosition);
				}
			}
		}
		public Rectangle getRectangle(Position startPosition) {
			Rectangle result = new Rectangle(startPosition, Extent.NULL_EXTENT);
			Iterator it = moveDataList.iterator();
			while (it.hasNext()) {
				MoveData moveData = (MoveData) it.next();
				result = result.union(new Rectangle(moveData.getNewPosition(), Extent.NULL_EXTENT));
			}
			return result;
		}
		private MoveData nextMoveData() {
			if (iterator.hasNext()) {
				return (MoveData) iterator.next();
			}
			else {
				return null;
			}
		}
	}

	private static class MoveData {
		private double startTime;
		private double endTime;
		private Position newPosition;
		private boolean isPausing;
		private boolean isFinished;
		private boolean suspended;
		public MoveData(double startTime, double endTime, Position newPosition, boolean suspended) {
			this.startTime = startTime;
			this.endTime = endTime;
			this.newPosition = newPosition;
			isPausing = true;
			isFinished = false;
			this.suspended=suspended;
		}
		public Position getNewPosition() {
			return newPosition;
		}
		public boolean hasNextArrivalInfo() {
			return !isFinished;
		}
		public ArrivalInfo getNextArrivalInfo(Position oldPosition) {
			if (isPausing) {
				isPausing = false;
				return new ArrivalInfo(oldPosition, startTime);
			}
			else {
				isFinished = true;
				return new ArrivalInfo(newPosition, endTime,suspended);
			}
		}
	}
	
	/**
	 * @author goergen
	 *
	 * To change the template for this generated type comment go to
	 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
	 */
	private static class ExitData {

		
		private double time;
		/**
		 * @param time
		 */
		public ExitData(double time) {
			
			this.time=time;
		}

		
		/**
		 * @return Returns the time.
		 */
		public double getTime() {
			return time;
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
		return scriptedMobilityData.getMinimumRadius();
	}

	public double getMaximumTransmissionRange() {
		return scriptedMobilityData.getMaximumRadius();
	}

}
