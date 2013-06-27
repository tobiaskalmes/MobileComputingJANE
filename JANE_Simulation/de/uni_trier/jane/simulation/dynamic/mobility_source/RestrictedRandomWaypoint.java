/*****************************************************************************
 * 
 * RestrictedRandomWaypoint.java
 * 
 * $Id: RestrictedRandomWaypoint.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This class is an implementation of the "restricted random waypoint" mobility model as
 * proposed in <i>Self Organized Terminode Routing. Ljubica Blazevic, Silvia Giordano,
 * Jean-Yves Le Boudec. Cluster Computing Vol.5, No. 2, April 2002</i>.
 */
public class RestrictedRandomWaypoint extends MobilitySourceBase {

	public final static String VERSION = "$Id: RestrictedRandomWaypoint.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $";
	
	public static final InitializationObjectElement INITIALIZATION_OBJECT = new InitializationObjectElement("restrictedRandomWaypoint") {
		public Object getValue(InitializationContext initializationContext, SimulationParameters simulationParameters) {
			String fileName = MOBILITY_SOURCE_FILE.getValue(initializationContext);
			DistributionCreator distributionCreator = simulationParameters.getDistributionCreator();
			return createFromFile(fileName, distributionCreator);
		}
		public Parameter[] getParameters() {
			return new Parameter[] { MOBILITY_SOURCE_FILE };
		}
	};

	public static final InitializationElement INITIALIZATION_ELEMENT = new InitializationElement("restrictedRandomWaypoint") {
		public void initialize(InitializationContext initializationContext, SimulationParameters simulationParameters) {
			String fileName = MOBILITY_SOURCE_FILE.getValue(initializationContext);
			DistributionCreator distributionCreator = simulationParameters.getDistributionCreator();
			MobilitySource mobilitySource = createFromFile(fileName, distributionCreator);
			simulationParameters.setMobilitySource(mobilitySource);
		}
		public Parameter[] getParameters() {
			return new Parameter[] { MOBILITY_SOURCE_FILE };
		}
	};

	private TownGraph townGraph;
	private int ordinaryCount;
	private int commuterCount;
	private int stayInTownSteps;
	private ContinuousDistribution ordinarySpeed;
	private ContinuousDistribution ordinaryPause;
	private ContinuousDistribution commuterSpeed;
	private ContinuousDistribution commuterPause;
	private ContinuousDistribution sendingRadius;
	private int currentOrdinary;
	private int currentCommuter;
	private int currentAddress;
	private Map addressTerminodeMap;

	private RestrictedRandomWaypoint(
		TownGraph townGraph,
		int ordinaryCount,
		int commuterCount,
		int ordinaryTownSteps,
		ContinuousDistribution ordinarySpeed,
		ContinuousDistribution ordinaryPause,
		ContinuousDistribution commuterSpeed,
		ContinuousDistribution commuterPause,
		ContinuousDistribution sendingRadius) {
		this.townGraph = townGraph;
		this.ordinaryCount = ordinaryCount;
		this.commuterCount = commuterCount;
		this.stayInTownSteps = ordinaryTownSteps;
		this.ordinarySpeed = ordinarySpeed;
		this.ordinaryPause = ordinaryPause;
		this.commuterSpeed = commuterSpeed;
		this.commuterPause = commuterPause;
		this.sendingRadius = sendingRadius;
		currentOrdinary = 0;
		currentCommuter = 0;
		currentAddress = 0;
		addressTerminodeMap = new HashMap();
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#hasNextEnterInfo()
	 */
	public boolean hasNextEnterInfo() {
		return currentOrdinary < ordinaryCount || currentCommuter < commuterCount;
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getNextEnterInfo()
	 */
	public EnterInfo getNextEnterInfo() {
		Terminode terminode;
		if(currentOrdinary < ordinaryCount) {
			terminode = new Terminode(ordinarySpeed, ordinaryPause, stayInTownSteps);
			currentOrdinary++;
		}
		else if(currentCommuter < commuterCount) {
			terminode = new Terminode(commuterSpeed, commuterPause, 1);
			currentCommuter++;
		}
		else {
			throw new IllegalStateException("there are no more terminodes");
		}
		DeviceID address = new SimulationDeviceID(currentAddress+1);
		currentAddress++;
		addressTerminodeMap.put(address, terminode);
		return new EnterInfo(address, sendingRadius.getNext(0), terminode.getNextArrivalInfo());
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#hasNextArrivalInfo(de.uni_trier.ubi.appsim.kernel.basetype.Address)
	 */
	public boolean hasNextArrivalInfo(DeviceID address) {
		return true;
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getNextArrivalInfo(de.uni_trier.ubi.appsim.kernel.basetype.Address)
	 */
	public ArrivalInfo getNextArrivalInfo(DeviceID address) {
		return ((Terminode)addressTerminodeMap.get(address)).getNextArrivalInfo();
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getRectangle()
	 */
	public Rectangle getRectangle() {
		return townGraph.getRectangle();
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getTotalDeviceCount()
	 */
	public int getTotalDeviceCount() {
		return ordinaryCount + commuterCount;
	}

	/**
	 * @see de.uni_trier.ubi.appsim.kernel.dynamic.MobilitySource#getShape()
	 */
	public Shape getShape() {
		return townGraph.getShape();
	}

	/**
	 * Create an instance of this class using an xml description. The xml file has to
	 * follow the format specified by <code>restricted_random_waypoint.dtd</code>.
	 * @param fileName the name of the xml file
	 * @param distributionCreator the generator for new distribution instances
	 * @return an instance of this class
	 */
	public static MobilitySource createFromFile(String fileName, DistributionCreator distributionCreator) {
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
		FileData data = parseRestrictedRandomWaypoint(document.getElementsByTagName("restricted_random_waypoint").item(0));
		Town[] towns = data.getEnvironment().getTowns();
		TownGraph townGraph = new TownGraph(towns, distributionCreator);
		int ordinaryCount = data.getPopulation().getOrdinaryTerminode().getNumber();
		int commuterCount = data.getPopulation().getCommuterTerminode().getNumber();
		int ordinaryTownSteps = data.getPopulation().getOrdinaryTerminode().getStayInTownSteps();
		ContinuousDistribution ordinarySpeed = createSpeedDistribution(data.getPopulation().getOrdinaryTerminode().getSpeed(), distributionCreator);
		ContinuousDistribution ordinaryPause = createPauseDistribution(data.getPopulation().getOrdinaryTerminode().getMeanPause(), distributionCreator);
		ContinuousDistribution commuterSpeed = createSpeedDistribution(data.getPopulation().getCommuterTerminode().getSpeed(), distributionCreator);
		ContinuousDistribution commuterPause = createPauseDistribution(data.getPopulation().getCommuterTerminode().getMeanPause(), distributionCreator);
		ContinuousDistribution sendingRadius = distributionCreator.getContinuousDeterministicDistribution(new ConstantDoubleMapping(data.getPopulation().getGeneral().getSendingRadius()));
		return new RestrictedRandomWaypoint(townGraph, ordinaryCount, commuterCount, ordinaryTownSteps, ordinarySpeed, ordinaryPause, commuterSpeed, commuterPause, sendingRadius);
	}

	private static ContinuousDistribution createPauseDistribution(double meanPause, DistributionCreator distributionCreator) {
		DoubleMapping rate = new ConstantDoubleMapping(1.0 / meanPause);
		return distributionCreator.getExponentialDistribution(rate);
	}

	private static ContinuousDistribution createSpeedDistribution(Speed speed, DistributionCreator distributionCreator) {
		DoubleMapping min = new ConstantDoubleMapping(speed.getMin());
		DoubleMapping max = new ConstantDoubleMapping(speed.getMax());
		return distributionCreator.getContinuousUniformDistribution(min, max);
	}

	private static FileData parseRestrictedRandomWaypoint(Node node) {
		Population population = null;
		Environment environment = null;
		NodeList nodeList = node.getChildNodes();
		for(int i=0; i<nodeList.getLength(); i++) {
			Node item = nodeList.item(i);
			String nodeName = item.getNodeName();
			if(nodeName.equalsIgnoreCase("population")) {
				population = parsePopulation(item);
			}
			else if(nodeName.equalsIgnoreCase("environment")) {
				environment = parseEnvironment(item);
			}
		}
		return new FileData(population, environment);
	}
	
	private static class FileData {
		private Population population;
		private Environment environment;
		public FileData(Population population, Environment environment) {
			this.population = population;
			this.environment = environment;
		}
		public Environment getEnvironment() {
			return environment;
		}

		public Population getPopulation() {
			return population;
		}

	}
	
	private static Population parsePopulation(Node node) {
		General general = null;
		OrdinaryTerminode ordinaryTerminode = null;
		CommuterTerminode commuterTerminode = null;
		NodeList nodeList = node.getChildNodes();
		for(int i=0; i<nodeList.getLength(); i++) {
			Node item = nodeList.item(i);
			String nodeName = item.getNodeName();
			if(nodeName.equalsIgnoreCase("general")) {
				general = parseGeneral(item);
			}
			else if(nodeName.equalsIgnoreCase("ordinary_terminode")) {
				ordinaryTerminode = parseOrdinaryTerminode(item);
			}
			else if(nodeName.equalsIgnoreCase("commuter_terminode")) {
				commuterTerminode = parseCommuterTerminode(item);
			}
		}
		return new Population(general, ordinaryTerminode, commuterTerminode); 
	}

	private static class General {
		private double sendingRadius;
		public General(double sendingRadius) {
			this.sendingRadius = sendingRadius;
		}
		public double getSendingRadius() {
			return sendingRadius;
		}
	}

	private static General parseGeneral(Node node) {
		double sendingRadius = 0.0;
		NodeList nodeList = node.getChildNodes();
		for(int i=0; i<nodeList.getLength(); i++) {
			Node item = nodeList.item(i);
			String nodeName = item.getNodeName();
			if(nodeName.equalsIgnoreCase("sending_radius")) {
				sendingRadius = parseDouble(item);
			}
		}
		return new General(sendingRadius);
	}

	private static class Population {
		private General general;
		private OrdinaryTerminode ordinaryTerminode;
		private CommuterTerminode commuterTerminode;
		public Population(General general, OrdinaryTerminode ordinaryTerminode, CommuterTerminode commuterTerminode) {
			this.general = general;
			this.ordinaryTerminode = ordinaryTerminode;
			this.commuterTerminode = commuterTerminode;
		}
		public General getGeneral() {
			return general;
		}
		public CommuterTerminode getCommuterTerminode() {
			return commuterTerminode;
		}

		public OrdinaryTerminode getOrdinaryTerminode() {
			return ordinaryTerminode;
		}
	}

	private static OrdinaryTerminode parseOrdinaryTerminode(Node node) {	
		int number = 0;
		int stayInTownSteps = 0;
		Speed speed = null;
		double meanPause = 0.0;
		NodeList nodeList = node.getChildNodes();
		for(int i=0; i<nodeList.getLength(); i++) {
			Node item = nodeList.item(i);
			String nodeName = item.getNodeName();
			if(nodeName.equalsIgnoreCase("number")) {
				number = parseInt(item);
			}
			else if(nodeName.equalsIgnoreCase("stay_in_town_steps")) {
				stayInTownSteps = parseInt(item);
			}
			else if(nodeName.equalsIgnoreCase("speed")) {
				speed = parseSpeed(item);
			}
			else if(nodeName.equalsIgnoreCase("mean_pause")) {
				meanPause = parseDouble(item);
			}
		}
		return new OrdinaryTerminode(number, stayInTownSteps, speed, meanPause); 
	}

	private static class OrdinaryTerminode {
		private int number;
		private int stayInTownSteps;
		private Speed speed;
		private double meanPause;
		public OrdinaryTerminode(int number, int stayInTownSteps, Speed speed, double meanPause) {
			this.number = number;
			this.stayInTownSteps = stayInTownSteps;
			this.speed = speed;
			this.meanPause = meanPause;
		}
		public double getMeanPause() {
			return meanPause;
		}

		public int getNumber() {
			return number;
		}

		public Speed getSpeed() {
			return speed;
		}

		public int getStayInTownSteps() {
			return stayInTownSteps;
		}

	}

	private static CommuterTerminode parseCommuterTerminode(Node node) {	
		int number = 0;
		Speed speed = null;
		double meanPause = 0.0;
		NodeList nodeList = node.getChildNodes();
		for(int i=0; i<nodeList.getLength(); i++) {
			Node item = nodeList.item(i);
			String nodeName = item.getNodeName();
			if(nodeName.equalsIgnoreCase("number")) {
				number = parseInt(item);
			}
			else if(nodeName.equalsIgnoreCase("speed")) {
				speed = parseSpeed(item);
			}
			else if(nodeName.equalsIgnoreCase("mean_pause")) {
				meanPause = parseDouble(item);
			}
		}
		return new CommuterTerminode(number, speed, meanPause); 
	}

	private static class CommuterTerminode {
		private int number;
		private Speed speed;
		private double meanPause;
		public CommuterTerminode(int number, Speed speed, double meanPause) {
			this.number = number;
			this.speed = speed;
			this.meanPause = meanPause;
		}
		public double getMeanPause() {
			return meanPause;
		}

		public int getNumber() {
			return number;
		}

		public Speed getSpeed() {
			return speed;
		}

	}

	private static Speed parseSpeed(Node node) {
		double min = 0;
		double max = 0;
		NodeList nodeList = node.getChildNodes();
		for(int i=0; i<nodeList.getLength(); i++) {
			Node item = nodeList.item(i);
			String nodeName = item.getNodeName();
			if(nodeName.equalsIgnoreCase("min")) {
				min = parseDouble(item);
			}
			else if(nodeName.equalsIgnoreCase("max")) {
				max = parseDouble(item);
			}
		}
		return new Speed(min, max); 
	}

	private static class Speed {
		private double min;
		private double max;
		public Speed(double min, double max) {
			this.min = min;
			this.max = max;
		}
		public double getMax() {
			return max;
		}

		public double getMin() {
			return min;
		}

	}

	private static Environment parseEnvironment(Node node) {
		List result = new ArrayList();
		NodeList nodeList = node.getChildNodes();
		for(int i=0; i<nodeList.getLength(); i++) {
			Node item = nodeList.item(i);
			String nodeName = item.getNodeName();
			if(nodeName.equalsIgnoreCase("town")) {
				result.add(parseTown(item));
			}
		}
		Town[] array = new Town[result.size()];
		for(int i=0; i<result.size(); i++) {
			array[i] = (Town)result.get(i);
		}
		return new Environment(array);
	}

	private static Town parseTown(Node node) {
		int id = 0;
		String name = null;
		Position center = null;
		Extent extent = null;
		int[] connected = null;
		NodeList nodeList = node.getChildNodes();
		for(int i=0; i<nodeList.getLength(); i++) {
			Node item = nodeList.item(i);
			String nodeName = item.getNodeName();
			if(nodeName.equalsIgnoreCase("id")) {
				id = parseInt(item);
			}
			else if(nodeName.equalsIgnoreCase("name")) {
				name = parseString(item);
			}
			else if(nodeName.equalsIgnoreCase("center")) {
				center = parseCenter(item);			
			}
			else if(nodeName.equalsIgnoreCase("extent")) {
				extent = parseExtent(item);
			}
			else if(nodeName.equalsIgnoreCase("connected")) {
				connected = parseConnected(item);
			}
		}
		return new Town(id, name, center, extent, connected);
	}

	private static int[] parseConnected(Node node) {
		List result = new ArrayList();
		NodeList nodeList = node.getChildNodes();
		for(int i=0; i<nodeList.getLength(); i++) {
			Node item = nodeList.item(i);
			String nodeName = item.getNodeName();
			if(nodeName.equalsIgnoreCase("id")) {
				result.add(new Integer(parseInt(item)));
			}
		}
		int[] array = new int[result.size()];
		for(int i=0; i<result.size(); i++) {
			array[i] = ((Integer)result.get(i)).intValue();
		}
		return array;
	}

	private static Position parseCenter(Node node) {
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

	private static Extent parseExtent(Node node) {
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
		return new Extent(x, y);
	}

	private static int parseInt(Node node) {
		node = node.getFirstChild();
		return Integer.parseInt(node.getNodeValue());
	}

	private static double parseDouble(Node node) {
		node = node.getFirstChild();
		return Double.parseDouble(node.getNodeValue());
	}

	private static String parseString(Node node) {
		node = node.getFirstChild();
		return node.getNodeValue();
	}

	private class Terminode {
		private ContinuousDistribution speed;
		private ContinuousDistribution pause;
		private int stayInTownSteps;
		private int townIndex;
		private int currentTownStep;
		private boolean pausing;
		private Position position;
		private double time;
		private boolean first;
		public Terminode(ContinuousDistribution speed, ContinuousDistribution pause, int stayInTownSteps) {
			this.speed = speed;
			this.pause = pause;
			this.stayInTownSteps = stayInTownSteps;
			pausing = false;
			time = 0;
			townIndex = townGraph.getStartDistribution().getNext(0);
			currentTownStep = stayInTownSteps;
			position = getArrivalPosition();
			first = true;
		}
		public ArrivalInfo getNextArrivalInfo() {
			if (first) {
				first = false;
				return new ArrivalInfo(position, time);
			} else {
				if (pausing) {
					Position nextPosition = getArrivalPosition();
					time += position.distance(nextPosition) / speed.getNext(time);
					position = nextPosition;
				} else {
					time += pause.getNext(time);
				}
				pausing = !pausing;
				return new ArrivalInfo(position, time);
			}
		}
		private Position getArrivalPosition() {
			if (currentTownStep == 0) {
				townIndex = townGraph.getIndexDistribution(townIndex).getNext(time);
				currentTownStep = stayInTownSteps;
			}
			currentTownStep--;
			return townGraph.getPositionGenerator(townIndex).getNext(time);
		}
	}

	private static class Town {
		private int id;
		private String name;
		private Position center;
		private Extent extent;
		private int[] connected;
		public Town(int id, String name, Position center, Extent extent, int[] connected) {
			this.id = id;
			this.name = name;
			this.center = center;
			this.extent = extent;
			this.connected = connected;
		}
		public int getId() {
			return id;
		}
		public String getName() {
			return name;
		}
		public Position getCenter() {
			return center;
		}
		public Extent getExtent() {
			return extent;
		}
		public int[] getConnected() {
			return connected;
		}
	}

	private static class Environment {
		private Town[] towns;
		public Environment(Town[] towns) {
			this.towns = towns;
		}
		public Town[] getTowns() {
			return towns;
		}
	}

	private static class TownGraph {
		private static final Color SHAPE_COLOR = Color.BLACK;//new Color(250,250,250);
		private List positionGeneratorList;
		private List indexDistriutionList;
		private Shape shape;
		private Rectangle rectangle;
		private DiscreteDistribution startDistriubution;
		public TownGraph(Town[] towns, DistributionCreator distributionCreator) {
			positionGeneratorList = new ArrayList();
			indexDistriutionList = new ArrayList();
			rectangle = null;
			ShapeCollection shapeCollection = new ShapeCollection();
			shapeCollection.addShape(createBackgroundShape(towns), new Position(0, 0));
			IntegerMapping first = new ConstantIntegerMapping(0);
			IntegerMapping last = new ConstantIntegerMapping(towns.length-1);
			startDistriubution = distributionCreator.getDiscreteUniformDistribution(first, last);
			for(int i=0; i<towns.length; i++) {
				Town town = towns[i];
				positionGeneratorList.add(createPositionGenerator(town, distributionCreator));
				indexDistriutionList.add(createIndexDistribution(town, distributionCreator));
				shapeCollection.addShape(createShape(town), new Position(0, 0));
				if(rectangle == null) {
					rectangle = createRectangle(town);
				}
				else {
					rectangle = rectangle.union(createRectangle(town));
				}
			}
			shape = shapeCollection;
		}
		public PositionGenerator getPositionGenerator(int i) {
			return (PositionGenerator)positionGeneratorList.get(i);
		}
		public DiscreteDistribution getIndexDistribution(int i) {
			return (DiscreteDistribution)indexDistriutionList.get(i);
		}
		public DiscreteDistribution getStartDistribution() {
			return startDistriubution;
		}
		public Rectangle getRectangle() {
			return rectangle;
		}
		public Shape getShape() {
			return shape;
		}
		private PositionGenerator createPositionGenerator(Town town, DistributionCreator distributionCreator) {
			DoubleMapping ax = new ConstantDoubleMapping(town.getCenter().getX() - town.getExtent().getWidth()/2);
			DoubleMapping bx = new ConstantDoubleMapping(town.getCenter().getX() + town.getExtent().getWidth()/2);
			DoubleMapping ay = new ConstantDoubleMapping(town.getCenter().getY() - town.getExtent().getHeight()/2);
			DoubleMapping by = new ConstantDoubleMapping(town.getCenter().getY() + town.getExtent().getHeight()/2);
			ContinuousDistribution x = distributionCreator.getContinuousUniformDistribution(ax, bx);
			ContinuousDistribution y = distributionCreator.getContinuousUniformDistribution(ay, by);
			return new RandomPositionGenerator(x, y);
		}
		private DiscreteDistribution createIndexDistribution(Town town, DistributionCreator distributionCreator) {
			int[] connected = town.getConnected();
			IntegerVector integerVector = new IntegerArray(connected);
			IntegerSetParameter integerSetParameter = new ConstantIntegerSetParameter(integerVector);
			double[] values = new double[connected.length];
			double value = 1.0/((double)connected.length);
			for(int i=0; i< connected.length; i++) {
				values[i] = value;
			}
			DoubleVector doubleVector = new DoubleArray(values);
			ProbabilityVector probabilityVector = new ProbabilityVector(doubleVector);
			ProbabilityVectorParameter probabilityVectorParameter = new ConstantProbabilityVectorParameter(probabilityVector);
			return distributionCreator.getDiscreteWeightedDistribution(integerSetParameter, probabilityVectorParameter);
		}
		private Rectangle createRectangle(Town town) {
			return new Rectangle(town.getCenter(), town.getExtent());
		}
		private Shape createShape(Town town) {
			ShapeCollection shape = new ShapeCollection();
			shape.addShape(new RectangleShape(town.getCenter(), town.getExtent(), SHAPE_COLOR, true), new Position(0, 0));
			shape.addShape(new TextShape(town.getName(), SHAPE_COLOR, createRectangle(town).getBottomRight().add(new Position(2,2))), new Position(0, 0));
			return shape;
		}
		private Shape createBackgroundShape(Town[] towns) {
			ShapeCollection shape = new ShapeCollection();
			for(int i=0; i<towns.length; i++) {
				Town town = towns[i];
				int[] connected = town.getConnected();
				for(int j=0; j<connected.length; j++) {
					int k = connected[j];
					if(i != k) {
						shape.addShape(new LineShape(town.getCenter(), towns[k].getCenter(), SHAPE_COLOR), new Position(0,0));
					}
				}
			}
			return shape;
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
		return sendingRadius.getInfimum();
	}

	public double getMaximumTransmissionRange() {
		return sendingRadius.getSupremum();
	}

}
