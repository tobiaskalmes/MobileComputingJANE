/*****************************************************************************
 * 
 * ScriptedMobility.java
 * 
 * $Id: AnsimMobility.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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

import de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource.ArrivalInfo;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This class allows the definition of scripted mobility patterns. See the
 */
public class AnsimMobility implements MobilitySource {

	
	private final static String VERSION = "$Id: AnsimMobility.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";
	
	private ScriptedMobilityData scriptedMobilityData;
	private int currentNode;
	private Set arrivedSet;

	private AnsimMobility(ScriptedMobilityData scriptedMobilityData) {
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
		ScriptedMobilityData data = parseScriptedMobility(document.getElementsByTagName("simulation").item(0));
		return new AnsimMobility(data);
	}

	private static ScriptedMobilityData parseScriptedMobility(Node node) {
		Map map = new HashMap();
		List list = new ArrayList();
		Rectangle rectangle = null;
        ScriptedMobilityData mobiltyData=new ScriptedMobilityData();
		NodeList nodeList = node.getChildNodes();
        

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node item = nodeList.item(i);
			String nodeName = item.getNodeName();
			if (nodeName.equalsIgnoreCase("parameter")) {
                mobiltyData.initParameters(item);
				
			}else if (nodeName.equalsIgnoreCase("node_settings")) {
                mobiltyData.initNodes(item);
                
            }else if (nodeName.equalsIgnoreCase("mobility")) {
                mobiltyData.initMobility(item);
            }
		}
		return mobiltyData;
	}

	 

	private static Position parsePosition(Node node) {
		double x = 0;
		double y = 0;
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node item = nodeList.item(i);
			String nodeName = item.getNodeName();
			if (nodeName.equalsIgnoreCase("xpos")) {
				x = parseDouble(item);
			}
			else if (nodeName.equalsIgnoreCase("ypos")) {
				y = parseDouble(item);
			}
		}
		return new Position(x, y);
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

        private double sendingRange;
		public ScriptedMobilityData() {
		    addressNodeDataMap=new HashMap();
            nodeDataList=new ArrayList();
		}
		/**
         * TODO Comment method
         * @param item
         */
        public void initMobility(Node node) {
            NodeList nodeList = node.getChildNodes();
            
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node item = nodeList.item(i);
                String nodeName = item.getNodeName();
                if (nodeName.equalsIgnoreCase("position_change")) {
                    NodeList deviceList = item.getChildNodes();
                    
                    DeviceID address=null;
                    Position position=null;
                    double speed=0;
                    double time=0;            
                    for (int j = 0; j < deviceList.getLength(); j++) {
                        Node device = deviceList.item(j);

                        String deviceName = device.getNodeName();
                        if (deviceName.equalsIgnoreCase("node_id")) {
                            address=new SimulationDeviceID(parseInt(device)+1);
                        }else if (deviceName.equalsIgnoreCase("start_time")) {
                            time=parseDouble(device);
                        }else if (deviceName.equalsIgnoreCase("destination")) {
                            position=parsePosition(device);
                        }else if (deviceName.equalsIgnoreCase("velocity")) {
                            speed=parseDouble(device);
                        }
                    }
                    if (address!=null){
                        NodeData data = (NodeData)addressNodeDataMap.get(address);
                        data.addEvent(time,speed,position);
                    }
                }
            }
            
        }
        /**
         * TODO Comment method
         * @param item
         */
        public void initNodes(Node node) {
            NodeList nodeList = node.getChildNodes();
            
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node item = nodeList.item(i);
                String nodeName = item.getNodeName();
                if (nodeName.equalsIgnoreCase("node")) {
                    DeviceID address=null;
                    Position position=null;
                    NodeList deviceList = item.getChildNodes();
                    
                   
                    for (int j = 0; j < deviceList.getLength(); j++) {
                        Node device = deviceList.item(j);
                        String deviceName = device.getNodeName();
                        if (deviceName.equalsIgnoreCase("node_id")) {
                            address=new SimulationDeviceID(parseInt(device)+1);
                        }else if(deviceName.equalsIgnoreCase("position")) {
                            position=parsePosition(device);
                        }
                    }
                    if (position!=null&&address!=null){
                        NodeData nodeData=new NodeData(address,position,sendingRange);
                        addressNodeDataMap.put(address,nodeData);
                        nodeDataList.add(nodeData);
                        
                    }
                }
            }
            
        }
        /**
         * TODO Comment method
         * @param item
         */
        public void initParameters(Node node) {
            double xsize=0,ysize=0;
            NodeList nodeList = node.getChildNodes();
        
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node item = nodeList.item(i);
                String nodeName = item.getNodeName();
                if (nodeName.equalsIgnoreCase("xsize")) {
                    xsize = parseDouble(item);
                }else if (nodeName.equalsIgnoreCase("ysize")) {
                    ysize = parseDouble(item);
                }else if (nodeName.equalsIgnoreCase("range")) {
                    sendingRange= parseDouble(item);
                }
            }
            rectangle=new Rectangle(0,0,xsize,ysize);
            
            
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
			return addressNodeDataMap.size();
		}
		public double getMaximumRadius() {
			return sendingRange;
		}
		public double getMinimumRadius() {
			return sendingRange;
		}
	}

	private static class NodeData {
		private DeviceID address;
		private Position position;
		private double sendingRadius;
		private MobilityData mobilityData;
		public NodeData(DeviceID address, Position position, double sendingRadius) {
			this.address = address;
			this.position = position;
			this.sendingRadius = sendingRadius;
            mobilityData=new MobilityData(position);
            
		
		}
		/**
         * TODO Comment method
         * @param time
         * @param speed
         * @param position2
         */
        public void addEvent(double time, double speed, Position position) {
            mobilityData.addEvent(time,speed,position);
            
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
		
		public double getSendingRadius() {
			return sendingRadius;
		}
	}

	private static class MobilityData {
		//private List moveDataList;
		private Position oldPosition;
		//private Iterator iterator;
		//private MoveData currentMoveData;
		private boolean isFinished;
        
        private TreeMap treeMap;
        private Element currentMove;
        
        
        private class Element{
            
            double time;
            double speed;
            Position position;
            
            private boolean done;
            private boolean moving;
            /**
             * Constructor for class <code>Element</code>
             * @param speed
             * @param position
             */
            public Element(double time,double speed, Position position) {
                this.time=time;
                this.speed = speed;
                this.position = position;
            }
            
            public Element(){
                done=true;
            }
            
            public boolean isDone(){
                return done;
            }

            /**
             * TODO Comment method
             * @param oldPosition
             * @return
             */
            public ArrivalInfo getNextArrivalInfo(Position oldPosition,double nextTime) {
                if (!moving){
                    moving=true;
                    return new ArrivalInfo(oldPosition,time);
                }else{
                    done=true;
                    double ntime=Math.min(nextTime,time+oldPosition.distance(position)/(speed));
                    
                    return new ArrivalInfo(position,ntime);
                    /////////////kaputtes modell--
                }
            }
          
            
            
        }
		
		public MobilityData(Position startPosition) {
			
		    treeMap=new TreeMap();
			this.oldPosition = startPosition;
			currentMove=new Element();
			isFinished = false;
		}
		/**
         * TODO Comment method
         * @param time
         * @param speed
         * @param position
         */
        public void addEvent(double time, double speed, Position position) {
            treeMap.put(new Double(time), new Element(time,speed,position));
            
        }
        public boolean hasNextArrivalInfo() {
			return !treeMap.isEmpty()||!currentMove.isDone();
		}
		public ArrivalInfo getNextArrivalInfo() {
            if (currentMove.isDone()){
                currentMove=(Element) treeMap.remove(treeMap.firstKey());
            }
            double nextTime;
            if (treeMap.isEmpty()){
                nextTime=Double.MAX_VALUE;
            }else{
                nextTime=((Double)treeMap.firstKey()).doubleValue();
            }
            ArrivalInfo info= currentMove.getNextArrivalInfo(oldPosition,nextTime);
            oldPosition=info.getPosition();
            return info;
            
            
            
            

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
