/*****************************************************************************
 * 
 * FixedNodes.java
 * 
 * $Id: FixedNodes.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.simulation.*;
import de.uni_trier.jane.simulation.dynamic.position_generator.*;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;
import de.uni_trier.jane.simulation.parametrized.parameters.initialization.*;
import de.uni_trier.jane.simulation.parametrized.parameters.object.*;
import de.uni_trier.jane.util.dijkstra.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This class is used to simulate a fixed network of wireless devices.
 */
public class FixedNodes extends MobilitySourceBase {

	public final static String VERSION = "$Id: FixedNodes.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";

	
	public static final InitializationObjectElement MOBILITY_SOURCE_STATIC_UDG_OBJECT = new InitializationObjectElement("staticUnitDiskGraph") {
		public Object getValue(InitializationContext initializationContext, SimulationParameters simulationParameters) {
			int n = NUMBER_OF_NODES.getValue(initializationContext);
			double w = AREA_WIDTH.getValue(initializationContext);
			double h = AREA_HEIGHT.getValue(initializationContext);
			double r = SENDING_RADIUS.getValue(initializationContext);
			return FixedNodes.createRandom(n, simulationParameters.getDistributionCreator(), w, h, r);
		}
		public Parameter[] getParameters() {
			return new Parameter[] { NUMBER_OF_NODES, AREA_WIDTH, AREA_HEIGHT, SENDING_RADIUS };
		}
	};
	
	public static final InitializationObjectElement MOBILITY_SOURCE_STATIC_FILE_OBJECT = new InitializationObjectElement("staticFromFile") {
		public Object getValue(InitializationContext initializationContext, SimulationParameters simulationParameters) {
			String fileName = MOBILITY_SOURCE_FILE.getValue(initializationContext);
			return FixedNodes.createFromFile(fileName);
		}
		public Parameter[] getParameters() {
			return new Parameter[] { MOBILITY_SOURCE_FILE };
		}
	};

	public static final InitializationElement MOBILITY_SOURCE_STATIC_UDG = new InitializationElement("staticUnitDiskGraph") {
		public void initialize(InitializationContext initializationContext, SimulationParameters simulationParameters) {
			int n = NUMBER_OF_NODES.getValue(initializationContext);
			double w = AREA_WIDTH.getValue(initializationContext);
			double h = AREA_HEIGHT.getValue(initializationContext);
			double r = SENDING_RADIUS.getValue(initializationContext);
			MobilitySource mobilitySource = FixedNodes.createRandom(n, simulationParameters.getDistributionCreator(), w, h, r);
			simulationParameters.setMobilitySource(mobilitySource);
		}
		public Parameter[] getParameters() {
			return new Parameter[] { NUMBER_OF_NODES, AREA_WIDTH, AREA_HEIGHT, SENDING_RADIUS };
		}
	};
	public static final InitializationElement MOBILITY_SOURCE_STATIC_FILE = new InitializationElement("staticFromFile") {
		public void initialize(InitializationContext initializationContext, SimulationParameters simulationParameters) {
			String fileName = MOBILITY_SOURCE_FILE.getValue(initializationContext);
			MobilitySource mobilitySource = FixedNodes.createFromFile(fileName);
			simulationParameters.setMobilitySource(mobilitySource);
		}
		public Parameter[] getParameters() {
			return new Parameter[] { MOBILITY_SOURCE_FILE };
		}
	};

	

	private FixedNodesData fixedNodesData;
	private double lastCommandTime;
	private Set arrivedSet;


    private double firstCommandTime;

	private FixedNodes(FixedNodesData fixedNodesData, double firstCommandTime,double lastCommandTime) {
        if (lastCommandTime>Double.MAX_VALUE){
            throw new IllegalArgumentException("last command time should not be infinity");
        }
		this.fixedNodesData = fixedNodesData;
		this.lastCommandTime = lastCommandTime;
        this.firstCommandTime=firstCommandTime;
		arrivedSet = new HashSet();
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#hasNextEnterInfo()
	 */
	public boolean hasNextEnterInfo() {
		return fixedNodesData.hasNext();
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getNextEnterInfo()
	 */
	public EnterInfo getNextEnterInfo() {
		NodeData data = fixedNodesData.next();
		return new EnterInfo(data.getAddress(), data.getSendingRadius(), new ArrivalInfo(data.getPosition(), firstCommandTime));
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#hasNextArrivalInfo(de.uni_trier.ubi.appsim.kernel.basetype.Address)
	 */
	public boolean hasNextArrivalInfo(DeviceID address) {
		return !arrivedSet.contains(address);
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getNextArrivalInfo(de.uni_trier.ubi.appsim.kernel.basetype.Address)
	 */
	public ArrivalInfo getNextArrivalInfo(DeviceID address) {
		NodeData data = fixedNodesData.getNodeData(address);
		arrivedSet.add(address);
		return new ArrivalInfo(data.getPosition(), lastCommandTime);
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getRectangle()
	 */
	public Rectangle getRectangle() {
		return fixedNodesData.getRectangle();
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getTotalDeviceCount()
	 */
	public int getTotalDeviceCount() {
		return fixedNodesData.getNodeCount();
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getShape()
	 */
	public Shape getShape() {
		return fixedNodesData.getShape();
	}

	public Position getPosition(DeviceID address) {
		return fixedNodesData.getNodeData(address).getPosition();
	}
    
    public static MobilitySource create(Position[] positions, double sendingRadius) {
        DeviceID[] deviceIDs=new DeviceID[positions.length];
        double[] sendingRadii=new double[positions.length];
        for (int i=0;i<positions.length;i++){
            deviceIDs[i]=new SimulationDeviceID(i+1);
            sendingRadii[i]=sendingRadius;
        }
        return create(deviceIDs,positions,sendingRadii);
    }
    

	public static MobilitySource create(DeviceID[] addresses, Position[] positions, double[] sendingRadii) {
		int len = addresses.length;
		Rectangle rectangle = null;
		List nodeDataList = new ArrayList();
		double minimumRadius = Double.MAX_VALUE;
		double maximumRadius = 0;
		for(int i=0; i<len; i++) {
			minimumRadius = Math.min(minimumRadius, sendingRadii[i]);
			maximumRadius = Math.max(maximumRadius, sendingRadii[i]);
			NodeData nodeData = new NodeData(addresses[i], positions[i], sendingRadii[i]);
			nodeDataList.add(nodeData);
			Rectangle additionalRect = new Rectangle(positions[i], new Extent(0,0));
			if(rectangle == null) {
				rectangle = additionalRect;
			}
			rectangle = rectangle.union(additionalRect);
		}
		FixedNodesData data = new FixedNodesData(nodeDataList, rectangle, EmptyShape.getInstance(), minimumRadius, maximumRadius);
		return new FixedNodes(data, 0, Double.MAX_VALUE);
	}

	/**
	 * Create an instance of this class using an xml description. The xml file has to
	 * follow the format specified by <code>fixed_nodes.dtd</code>.
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
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}
		File file = new File(fileName);
		Document document;
		try {
			document = documentBuilder.parse(file);
		} catch (SAXException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		FixedNodesData data = parseFixedNodes(document.getElementsByTagName("fixed_nodes").item(0));
		return new FixedNodes(data,0, Double.MAX_VALUE); 
	}

	/**
	 * Create an instance of this mobility model randomly. The sending radius is adjusted afterwards in order
	 * to get the average degree. The result is a unit disk graph.
	 * @param number the number of devices
	 * @param positionGenerator the generator for positions
	 * @param averageDegree the average degree of a node
	 * @param backgroundShape the background shape of the position generator
	 * @return the mobility source, the adjusted sending radius and a flag indicating if the network is connected
	 */
	public static AdjustedSendingRadiusResult createRandomWithAverageDegree(int number, PositionGenerator positionGenerator, double averageDegree, Shape backgroundShape, double endTime) {
		List positionList = new ArrayList();
		for(int i=0; i<number; i++) {
			positionList.add(positionGenerator.getNext(0));
		}
		SortedSet lengthList = new TreeSet(new Comparator() {
			public int compare(Object o1, Object o2) {
				double d1 = ((Double) o1).doubleValue();
				double d2 = ((Double) o2).doubleValue();
				if (d1 < d2) {
					return -1;
				}
				else if (d1 > d2) {
					return 1;
				}
				else {
					return 1;
				}
			}
		});
		for(int i=0; i<positionList.size(); i++) {
			Position pos_i = (Position)positionList.get(i);
			for(int j=i+1; j<positionList.size(); j++) {
				Position pos_j = (Position)positionList.get(j);
				lengthList.add(new Double(pos_i.distance(pos_j)));
			}
		}
		Double[] lengthArray = (Double[])lengthList.toArray(new Double[lengthList.size()]);
		double sendingRadius = lengthArray[(int)((number * averageDegree) / 2)-1].doubleValue();
		int currentAddress = 0;
		List nodeDataList = new ArrayList();
		Rectangle rectangle = null;
		Iterator it = positionList.iterator();
		while(it.hasNext()) {
			Position pos = (Position)it.next();
			if(rectangle == null) {
				rectangle = new Rectangle(pos, Extent.NULL_EXTENT);
			}
			else {
				rectangle = rectangle.union(new Rectangle(pos, Extent.NULL_EXTENT));
			}
			DeviceID address = new SimulationDeviceID(currentAddress+1);
			NodeData data = new NodeData(address, pos, sendingRadius);
			nodeDataList.add(data);
			currentAddress++;
		}
		boolean connected = checkConnected(nodeDataList);
		return new AdjustedSendingRadiusResult(
				new FixedNodes(new FixedNodesData(nodeDataList, rectangle, backgroundShape, sendingRadius, sendingRadius),0, endTime),
				sendingRadius, connected, new NodeDataGraph(nodeDataList));
	}

	/**
	 * Create an instance of this mobility model randomly. The sending radius is adjusted afterwards in order
	 * to get the average degree. The result is a unit disk graph.
	 * @param number the number of devices
	 * @param distributionCreator the source for random numbers
	 * @param planeSize the x and y size of the plane
	 * @param averageDegree the average degree of a node
	 * @return the mobility source, the adjusted sending radius and a flag indicating if the network is connected
	 */
	public static AdjustedSendingRadiusResult createRandomWithAverageDegree(int number, DistributionCreator distributionCreator, double planeSize, double averageDegree, double endTime) {
		ContinuousDistribution x = distributionCreator.getContinuousUniformDistribution(new ConstantDoubleMapping(0), new ConstantDoubleMapping(planeSize));
		ContinuousDistribution y = distributionCreator.getContinuousUniformDistribution(new ConstantDoubleMapping(0), new ConstantDoubleMapping(planeSize));
		PositionGenerator positionGenerator = new RandomPositionGenerator(x, y);
		return createRandomWithAverageDegree(number, positionGenerator, averageDegree, EmptyShape.getInstance(), endTime);
	}
	
	public static AdjustedSendingRadiusResult createRandomWithAverageDegree(int number, DistributionCreator distributionCreator, double planeSizeX,double planeSizeY, double averageDegree, double endTime) {
		ContinuousDistribution x = distributionCreator.getContinuousUniformDistribution(new ConstantDoubleMapping(0), new ConstantDoubleMapping(planeSizeX));
		ContinuousDistribution y = distributionCreator.getContinuousUniformDistribution(new ConstantDoubleMapping(0), new ConstantDoubleMapping(planeSizeY));
		PositionGenerator positionGenerator = new RandomPositionGenerator(x, y);
		return createRandomWithAverageDegree(number, positionGenerator, averageDegree, EmptyShape.getInstance(), endTime);
	}

	/**
	 * Create an instance of a static network with average degree by adjusting the number of nodes. The result is a unit disk graph.
	 * @param averageNodeCount the average number of neigbor nodes in transmission area (exception are node near the border of the simulated area)
	 * @param windowSize the size of a centered window containing all nodes used to create traffic. Nodes near the border (with less average
	 * degree) will be ignored for a large border size, if sender receiver pairs are generated only for nodes inside the window.
	 * @param borderSize the size of the border containing all nodes not beeing used as sender receiver pairs.
	 * @param sendingRadius the sending radius of each node
	 * @param distributionCreator the source of random
	 * @return the mobility source, the adjusted number of nodes, a flag indicating if the network is connected and an array conaining all nodes
	 * inside the window. Use this array to create the appropriate sender receiver pairs inside the window.
	 */
	public static AdjustedNodeCountResult createRandomWithAverageDegree(double averageNodeCount, double windowSize, double borderSize, double sendingRadius, DistributionCreator distributionCreator) {

		// create list of node data
		double size = windowSize + 2 * borderSize;
		ContinuousDistribution xDistribution = distributionCreator.getContinuousUniformDistribution(0,size);
		ContinuousDistribution yDistribution = distributionCreator.getContinuousUniformDistribution(0,size);
		PositionGenerator positionGenerator = new RandomPositionGenerator(xDistribution, yDistribution);
		double totalArea = size * size;
		double transmissionArea = (sendingRadius * sendingRadius) * Math.PI;
		long nodeCount = Math.round((averageNodeCount + 1) * (totalArea / transmissionArea));
		List dataList = new ArrayList();
		for(long i=1; i<=nodeCount; i++) {
			DeviceID address = new SimulationDeviceID(i);
			Position position = positionGenerator.getNext(0);
			FixedNodes.NodeData data = new FixedNodes.NodeData(address, position, sendingRadius);
			dataList.add(data);
		}

		// determine the nodes within the window used to generate sender reciever pairs
		List nodesInWindowList = new ArrayList();
		Iterator iterator = dataList.iterator();
		Rectangle windowRectangle = new Rectangle(new Position(borderSize, borderSize), new Position(borderSize + windowSize, borderSize + windowSize));
		while (iterator.hasNext()) {
			FixedNodes.NodeData data = (FixedNodes.NodeData) iterator.next();
			if(windowRectangle.contains(data.getPosition())) {
				nodesInWindowList.add(data.getAddress());
			}
		}
		DeviceID[] nodesInWindow = (DeviceID[])nodesInWindowList.toArray(new DeviceID[nodesInWindowList.size()]);

		// create mobility source
		Rectangle completeRectangle = new Rectangle(new Position(0,0), new Position(size,size));
		ShapeCollection shape = new ShapeCollection();
		shape.addShape(new RectangleShape(completeRectangle, Color.BLACK, false), Position.NULL_POSITION);
		shape.addShape(new RectangleShape(windowRectangle,Color.RED, false), Position.NULL_POSITION);
		FixedNodes.FixedNodesData fixedNodesData = new FixedNodes.FixedNodesData(dataList, completeRectangle, shape, sendingRadius, sendingRadius);

		return new AdjustedNodeCountResult( new FixedNodes(fixedNodesData,0, Double.MAX_VALUE), nodeCount, nodesInWindow,dataList,sendingRadius);

	}

	/**
	 * Create an instance of this mobility model randomly. The given position generator and sending radius
	 * distribution is evaluated at time 0.
	 * @param number the number of devices
	 * @param positionGenerator the generator used to produce a new position
	 * @param sendingRadiusDistribution the distribution used to create the sending radius
	 * @param shape the background shape for this mobility source
	 * @return an instance of this class
	 */
	public static MobilitySource createRandom(int number, double startTime, PositionGenerator positionGenerator, ContinuousDistribution sendingRadiusDistribution, Shape shape) {
		List list = new ArrayList();
		Rectangle rectangle = null;
		for(int i=1; i<=number; i++) {
			NodeData data = new NodeData(new SimulationDeviceID(i), positionGenerator.getNext(0),sendingRadiusDistribution.getNext(0));
			list.add(data);
			if(rectangle == null) {
				rectangle = new Rectangle(data.getPosition(), Extent.NULL_EXTENT);
			}
			else {
				rectangle = rectangle.union(new Rectangle(data.getPosition(), Extent.NULL_EXTENT));
			}
		}
		return new FixedNodes(new FixedNodesData(list, rectangle, shape, sendingRadiusDistribution.getInfimum(), sendingRadiusDistribution.getSupremum()),startTime, Double.MAX_VALUE);
	}
    
    /**
     * Create an instance of this mobility model randomly. The given position generator and sending radius
     * distribution is evaluated at time 0.
     * @param number the number of devices
     * @param positionGenerator the generator used to produce a new position
     * @param sendingRadiusDistribution the distribution used to create the sending radius
     * @param shape the background shape for this mobility source
     * @return an instance of this class
     */
    public static MobilitySource createRandom(int number, PositionGenerator positionGenerator, ContinuousDistribution sendingRadiusDistribution, Shape shape) {
        return createRandom(number,0,positionGenerator,sendingRadiusDistribution,shape);
    }

	/**
	 * Create an instance of this mobility model randomly. The result is a unit graph (each device
	 * has the same sending radius) with devices uniformly distributed over the area with the given extent.
	 * @param number the number of devices
	 * @param distributionCreator the generator used to produce random numbers
	 * @param areaWidth the with of the area
	 * @param areaHeight the height of the area
	 * @param sendingRadius the sending radius of each device
	 * @return an instance of this class
	 */
	public static MobilitySource createRandom(int number, DistributionCreator distributionCreator, double areaWidth, double areaHeight, double sendingRadius) {
		return createRandom(number,0,distributionCreator,areaWidth,areaHeight,sendingRadius);
	}
    /**
     * Create an instance of this mobility model randomly. The result is a unit graph (each device
     * has the same sending radius) with devices uniformly distributed over the area with the given extent.
     * @param number the number of devices
     * @param distributionCreator the generator used to produce random numbers
     * @param areaWidth the with of the area
     * @param areaHeight the height of the area
     * @param sendingRadius the sending radius of each device
     * @return an instance of this class
     */
    public static MobilitySource createRandom(int number, double startTime, DistributionCreator distributionCreator, double areaWidth, double areaHeight, double sendingRadius) {
        ContinuousDistribution xDistribution = distributionCreator.getContinuousUniformDistribution(0, areaWidth);
        ContinuousDistribution yDistribution = distributionCreator.getContinuousUniformDistribution(0, areaHeight);
        PositionGenerator positionGenerator = new RandomPositionGenerator(xDistribution, yDistribution);
        ContinuousDistribution sendingRadiusDistribution = distributionCreator.getContinuousUniformDistribution(sendingRadius, sendingRadius);
        Rectangle rectangle = new Rectangle(new Position(0,0), new Position(areaWidth,areaHeight));
        Shape shape = new RectangleShape(rectangle, Color.BLACK, false);
        return createRandom(number,startTime, positionGenerator, sendingRadiusDistribution, shape);
    }
    /**
     * Create an instance of this mobility model randomly. The result is a unit graph (each device
     * has the same sending radius) with devices uniformly distributed over the area with the given extent.
     * @param number the number of devices
     * @param distributionCreator the generator used to produce random numbers
     * @param xWidth the x with of the area
     * @param yWidth the y with of the area
     * @param zWidth the z with of the area
     * @param sendingRadius the sending radius of each device
     * @return an instance of this class
     */
    public static MobilitySource createRandom(int number, DistributionCreator distributionCreator, double xWidth, double yWidth, double zWidth, double sendingRadius) {
        ContinuousDistribution xDistribution = distributionCreator.getContinuousUniformDistribution(0, xWidth);
        ContinuousDistribution yDistribution = distributionCreator.getContinuousUniformDistribution(0, yWidth);
        ContinuousDistribution zDistribution = distributionCreator.getContinuousUniformDistribution(0, zWidth);
        PositionGenerator positionGenerator = new RandomPositionGenerator(xDistribution, yDistribution,zDistribution);
        ContinuousDistribution sendingRadiusDistribution = distributionCreator.getContinuousUniformDistribution(sendingRadius, sendingRadius);
        Rectangle rectangle = new Rectangle(new Position(0,0), new Position(xWidth,yWidth));
        Shape shape = new RectangleShape(rectangle, Color.BLACK, false);
        return createRandom(number, positionGenerator, sendingRadiusDistribution, shape);
    }

	/**
	 * This class is used by the static methods creating a random network with average degree by adjusting the sending radius.
	 */
	public static class AdjustedSendingRadiusResult {

		private FixedNodes mobilitySource;
		private NodeDataGraph nodeDataGraph;
		private double sendingRadius;
		private boolean connected;

		private AdjustedSendingRadiusResult(FixedNodes mobilitySource, double sendingRadius, boolean connected, NodeDataGraph nodeDataGraph) {
			this.mobilitySource = mobilitySource;
			this.nodeDataGraph = nodeDataGraph;
			this.sendingRadius = sendingRadius;
			this.connected = connected;
		}

		/**
		 * Get the mobility source producing the static network graph.
		 * @return the mobility source
		 */
		public MobilitySource getMobilitySource() {
			return mobilitySource;
		}

		/**
		 * Get the globally used sending radius.
		 * @return the sending radius
		 */
		public double getSendingRadius() {
			return sendingRadius;
		}

		/**
		 * Check if the static network graph is connected.
		 * @return true if connected
		 */
		public boolean isConnected() {
			return connected;
		}

		public boolean isConnected(DeviceID node1, DeviceID node2) {
			DijkstraAlgorithm algorithm = new DijkstraAlgorithm(nodeDataGraph);
			int source = nodeDataGraph.getIndex(node1);
			int destination = nodeDataGraph.getIndex(node2);
			DijkstraAlgorithmResult result = algorithm.solve(source);
			return result.getMinimumPathWeigth(destination) < Double.POSITIVE_INFINITY;
		}

		public Position getPosition(DeviceID address) {
			return mobilitySource.getPosition(address);
		}

	}

	/**
	 * This class is used by the static methods creating a random network with average degree by adjusting the node count.
	 */
	public static class AdjustedNodeCountResult {

		private MobilitySource mobilitySource;
		private long nodeCount;
		private boolean connected;
		private DeviceID[] nodesInWindow;
        private List dataList;
        private double sendingRadius;

		protected AdjustedNodeCountResult(MobilitySource mobilitySource, long nodeCount,  DeviceID[] nodesInWindow, List dataList, double sendingRadius) {
			this.mobilitySource = mobilitySource;
			this.nodeCount = nodeCount;
			this.connected = checkConnected(dataList);
			this.nodesInWindow = nodesInWindow;
            this.dataList=dataList;
            this.sendingRadius=sendingRadius;
		}
        
        
        /**
         * TODO Comment method
         * @param positions
         * @return
         */
        public boolean isConnected(Position[] positions) {
            List testList=new ArrayList(dataList);
            for (int i=0;i<positions.length;i++){
                testList.add(new FixedNodes.NodeData(new SimulationDeviceID(nodeCount+i+1),positions[i],sendingRadius));           
            }
            return checkConnected(testList);
        }

		/**
		 * Get the mobility source producing the static network graph.
		 * @return the mobility source
		 */
		public MobilitySource getMobilitySource() {
			return mobilitySource;
		}

		/**
		 * Get the total number of nodes.
		 * @return the sending radius
		 */
		public long getNodeCount() {
			return nodeCount;
		}

		/**
		 * Check if the static network graph is connected.
		 * @return true if connected
		 */
		public boolean isConnected() {
			return connected;
		}
		
		/**
		 * Get the nodes lying in the window used to generate sender receiver pairs
		 * @return the nodes inside the window
		 */
		public DeviceID[] getNodesInWindow() {
			return nodesInWindow;
		}



	}
	
	protected static boolean checkConnected(List nodeDataList) {
		Graph graph = new NodeDataGraph(nodeDataList);
		DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm(graph);
		DijkstraAlgorithmResult result = dijkstraAlgorithm.solve(0);
		int destCount = result.getNodeCount();
		for(int i=0; i<destCount; i++) {
			int dest = result.getNode(i);
			double weight = result.getMinimumPathWeigth(dest);
			if(weight >= Double.POSITIVE_INFINITY) {
				return false;
			}
		}
		return true;
	}

	private static FixedNodesData parseFixedNodes(Node node) {
		List list = new ArrayList();
		Rectangle rectangle = null;
		NodeList nodeList = node.getChildNodes();
		double minimumRadius = Double.MAX_VALUE;
		double maximumRadius = 0;
		for(int i=0; i<nodeList.getLength(); i++) {
			Node item = nodeList.item(i);
			String nodeName = item.getNodeName();
			if(nodeName.equalsIgnoreCase("node")) {
				NodeData data = parseNode(item);
				
				minimumRadius = Math.min(minimumRadius, data.getSendingRadius());
				maximumRadius = Math.max(maximumRadius, data.getSendingRadius());
				
				list.add(data);
				if(rectangle == null) {
					rectangle = new Rectangle(data.getPosition(), Extent.NULL_EXTENT);
				}
				else {
					rectangle = rectangle.union(new Rectangle(data.getPosition(), Extent.NULL_EXTENT));
				}
			}
		}
		return new FixedNodesData(list, rectangle, EmptyShape.getInstance(), minimumRadius, maximumRadius);
	}



	
	private static NodeData parseNode(Node node) {
		DeviceID address = null;
		Position position = null;
		double sendingRadius = 0;
		NodeList nodeList = node.getChildNodes();
		for(int i=0; i<nodeList.getLength(); i++) {
			Node item = nodeList.item(i);
			String nodeName = item.getNodeName();
			if(nodeName.equalsIgnoreCase("address")) {
				address = new SimulationDeviceID(parseInt(item));
			}
			else if(nodeName.equalsIgnoreCase("position")) {
				position = parsePosition(item);
			}
			else if(nodeName.equalsIgnoreCase("sending_radius")) {
				sendingRadius = parseDouble(item);
			}
		}
		return new NodeData(address, position, sendingRadius);
	}

	private static Position parsePosition(Node node) {
		double x = 0;
		double y = 0;
		NodeList nodeList = node.getChildNodes();
		for(int i=0; i<nodeList.getLength(); i++) {
			Node item = nodeList.item(i);
			String nodeName = item.getNodeName();
			if(nodeName.equalsIgnoreCase("x")) {
				x = parseDouble(item);
			}
			else if(nodeName.equalsIgnoreCase("y")) {
				y = parseDouble(item);
			}
		}
		return new Position(x, y);
	}

	private static int parseInt(Node node) {
		node = node.getFirstChild();
		return Integer.parseInt(node.getNodeValue());
	}

	private static double parseDouble(Node node) {
		node = node.getFirstChild();
		return Double.parseDouble(node.getNodeValue());
	}
	
	private static class FixedNodesData {
		private List nodeDataList;
		private Rectangle rectangle;
		private Shape shape;
		private double minimumRadius;
		private double maximumRadius;
		private Map addressNodeDataMap;
		private Iterator valueIterator;
		public FixedNodesData(List nodeDataList, Rectangle rectangle, Shape shape, double minimumRadius, double maximumRadius) {
			this.nodeDataList = nodeDataList;
			this.rectangle = rectangle;
			this.shape = shape;
			this.minimumRadius = minimumRadius;
			this.maximumRadius = maximumRadius;
			addressNodeDataMap = new HashMap();
			Iterator iterator = nodeDataList.iterator();
			while (iterator.hasNext()) {
				NodeData data = (NodeData) iterator.next();
				addressNodeDataMap.put(data.getAddress(), data);
			}
			valueIterator = nodeDataList.iterator();			
		}
		public boolean hasNext() {
			return valueIterator.hasNext();
		}
		public NodeData next() {
			return (NodeData)valueIterator.next();
		}
		public NodeData getNodeData(DeviceID address) {
			return (NodeData)addressNodeDataMap.get(address);
		}
		public Shape getShape() {
			return shape;
		}
		public Rectangle getRectangle() {
			return rectangle;
		}
		public int getNodeCount() {
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
		public NodeData(DeviceID address, Position position, double sendingRadius) {
			this.address = address;
			this.position = position;
			this.sendingRadius = sendingRadius;
		}
		public DeviceID getAddress() {
			return address;
		}
		public Position getPosition() {
			return position;
		}
		public double getSendingRadius() {
			return sendingRadius;
		}
	}

	private static class NodeDataGraph implements Graph {
		private List neighborLists;
		private Map addressIndexMap;
		public NodeDataGraph(List nodeData) {
			addressIndexMap = new HashMap();
			neighborLists = new ArrayList();
			int elementCount = nodeData.size();
			for(int i=0; i<elementCount; i++) {
				List neighborList = new ArrayList();
				FixedNodes.NodeData node1 = (FixedNodes.NodeData)nodeData.get(i);
				for(int j=0; j<elementCount; j++) {
					FixedNodes.NodeData node2 = (FixedNodes.NodeData)nodeData.get(j);
					if(!node1.getAddress().equals(node2.getAddress())) {
						double sendingRadius = Math.min(node1.getSendingRadius(), node2.getSendingRadius());
						if(node1.getPosition().distance(node2.getPosition())<sendingRadius) {
							neighborList.add(new Integer(j));
						}
					}
				}
				neighborLists.add(i, neighborList);
				addressIndexMap.put(node1.getAddress(), new Integer(i));
			}
		}
		public int getNodeCount() {
			return neighborLists.size();
		}
		public int getNode(int number) {
			return number;
		}
		public int getNeighborCount(int node) {
			return ((List)(neighborLists.get(node))).size();
		}
		public int getNeighbor(int node, int number) {
			List neighborList = (List)(neighborLists.get(node));
			return ((Integer)neighborList.get(number)).intValue();
		}
		public double getWeight(int source, int destination) {
			return 1;
		}
		public int getIndex(DeviceID address) {
			return ((Integer)addressIndexMap.get(address)).intValue();
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
		return fixedNodesData.getMinimumRadius();
	}

	public double getMaximumTransmissionRange() {
		return fixedNodesData.getMaximumRadius();
	}

}
