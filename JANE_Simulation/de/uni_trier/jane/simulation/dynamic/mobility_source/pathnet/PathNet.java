/*****************************************************************************
 * 
 * PathNet.java
 * 
 * $Id: PathNet.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
 *  
 * Copyright (C) 2002-2004 Hannes Frey, Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.simulation.dynamic.mobility_source.pathnet;



import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.visualization.shapes.*;



/**
 * This class models an area consisting of a network of paths between starting
 * and ending locations. Mobile devices are moved along these paths from source
 * locations to destination locations.
 */
public class PathNet implements DeviceMover{
	private Hashtable destTable;
	private Hashtable crossTable;
	private PathNetPath[] path;
	
	private ContinuousDistribution selectDistribution;


	/**
	 * Construct a path net object and load the definition from the specified
	 * XML file.
	 * @param fileName 				the name of the XML file containing the path net definition.
	 * @param distributionCreator	the simulation <code>DistributionCreator</code>
	 * @throws InvalidPathNetException if the given XML file is not valid.
	 * @throws ParserConfigurationException if a DocumentBuilder cannot be created which satisfies the configuration requested.
	 * @throws SAXException if any parse errors occur.
	 * @throws IOException if any IO errors occur.
	 */	
	public PathNet(String fileName, DistributionCreator distributionCreator) throws InvalidPathNetException, ParserConfigurationException, SAXException, IOException{
		selectDistribution=distributionCreator.getContinuousUniformDistribution(0,1);
		destTable=new Hashtable();
		crossTable=new Hashtable();
		loadDefinition(fileName);
		if(destTable.isEmpty()) {
			throw new InvalidPathNetException(
				"there has to be at least one destination node.");
		}
		
	}
	
	private void loadDefinition(String fileName)
	throws InvalidPathNetException, ParserConfigurationException,
			SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringComments(true);
		dbf.setValidating(true);
		dbf.setIgnoringElementContentWhitespace(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		db.setErrorHandler(new ErrorHandler() {
			public void warning(SAXParseException e) throws SAXException {
				throw e;
			}
			public void error(SAXParseException e) throws SAXException {
				throw e;
			}
			public void fatalError(SAXParseException e) throws SAXException {
				throw e;
			}
		});
		Document doc = db.parse(fileName);
		NodeList destNodeList = doc.getElementsByTagName("DEST");
		NodeList crossNodeList = doc.getElementsByTagName("CROSS");
		NodeList pathNodeList = doc.getElementsByTagName("PATH");
		Hashtable nodeConstructTable = new Hashtable();
		Hashtable pathConstructTable = new Hashtable();
		addNodes(destNodeList, nodeConstructTable, true);
		addNodes(crossNodeList, nodeConstructTable, false);
		addPaths(pathNodeList, nodeConstructTable, pathConstructTable);
		setRoutingInformation(destNodeList, nodeConstructTable,
				pathConstructTable);
		setRoutingInformation(crossNodeList, nodeConstructTable,
				pathConstructTable);
		//checkCycle(nodeConstructTable);
	}

private void addNodes(NodeList nodeList, Hashtable nodeConstructTable,
			boolean isDest) throws InvalidPathNetException {
		for (int i = 0; i < nodeList.getLength(); i++) {
			org.w3c.dom.Node domNode = nodeList.item(i);
			String name = getAttributeValue(domNode, "NAME");
			double x, y, width;
			try {
				x = Double.parseDouble(getAttributeValue(domNode, "X"));
				y = Double.parseDouble(getAttributeValue(domNode, "Y"));
				width = Double.parseDouble(getAttributeValue(domNode, "W"));
			} catch (NumberFormatException e) {
				throw new InvalidPathNetException(
						"The given position of node '" + name
								+ "' is not valid.");
			}
			PathNetNode node = new PathNetNode(name, new Position(x, y), width,selectDistribution);
			nodeConstructTable.put(name, node);
			if (isDest) {
				destTable.put(name, node);
			} else {
				crossTable.put(name, node);
			}
		}
	}

private void addPaths(NodeList nodeList, Hashtable nodeConstructTable,
			Hashtable pathConstructTable) throws InvalidPathNetException {
		path = new PathNetPath[nodeList.getLength()];
		for (int i = 0; i < nodeList.getLength(); i++) {
			org.w3c.dom.Node domNode = nodeList.item(i);
			String name = getAttributeValue(domNode, "NAME");
			String first = getAttributeValue(domNode, "FIRST");
			String last = getAttributeValue(domNode, "LAST");
			if (first.equals(last)) {
				throw new InvalidPathNetException(
						"The first and the last node of path '" + name
								+ "' has to be different.");
			}
			NodeList childNodeList = domNode.getChildNodes();
			Position[] pos = new Position[childNodeList.getLength() + 2];
			double[] width = new double[childNodeList.getLength() + 2];
			PathNetNode firstNode = (PathNetNode) nodeConstructTable.get(first);
			if (firstNode == null) {
				throw new InvalidPathNetException(
						"The 'FIRST' attribute of path '"
								+ name
								+ "' is set to '"
								+ first
								+ "' which is no name of a 'DEST' or a 'CROSS'.");
			}
			pos[0] 	= firstNode.getPosition();
			width[0]= firstNode.getWidth();
			for (int j = 0; j < childNodeList.getLength(); j++) {
				org.w3c.dom.Node childDomNode = childNodeList.item(j);
				double x, y, w;
				try {
					x = Double
							.parseDouble(getAttributeValue(childDomNode, "X"));
					y = Double
							.parseDouble(getAttributeValue(childDomNode, "Y"));
					w = Double
							.parseDouble(getAttributeValue(childDomNode, "W"));

				} catch (NumberFormatException e) {
					throw new InvalidPathNetException(
							"One given position of an inner node in path '"
									+ name + "' is not valid.");
				}
				pos[j + 1] = new Position(x, y);
				width[j+1] = w;
			}
			PathNetNode lastNode = (PathNetNode) nodeConstructTable.get(last);
			if (lastNode == null) {
				throw new InvalidPathNetException(
						"The 'LAST' attribute of path '"
								+ name
								+ "' is set to '"
								+ last
								+ "' which is no name of a 'DEST' or a 'CROSS'.");
			}
			pos[childNodeList.getLength() + 1] = lastNode.getPosition();
			width[childNodeList.getLength() + 1]= lastNode.getWidth();
			path[i] = new PathNetPath(name, firstNode, lastNode, pos,width,selectDistribution);
			pathConstructTable.put(name, path[i]);
		}
	}
	private void setRoutingInformation(NodeList nodeList,
			Hashtable nodeConstructTable, Hashtable pathConstructTable)
			throws InvalidPathNetException {
		HashSet destSet = new HashSet();
		for (int i = 0; i < nodeList.getLength(); i++) {
			org.w3c.dom.Node domNode = nodeList.item(i);
			String nodeName = getAttributeValue(domNode, "NAME");
			PathNetNode node = (PathNetNode) nodeConstructTable.get(nodeName);
			NodeList routingNodeList = domNode.getChildNodes();
			destSet.clear();
			for (int j = 0; j < routingNodeList.getLength(); j++) {
				org.w3c.dom.Node routingDomNode = routingNodeList.item(j);
				String destName = getAttributeValue(routingDomNode, "DEST");
				if (!destTable.containsKey(destName)) {
					throw new InvalidPathNetException(
							"The routing destination '"
									+ destName
									+ "' of node '"
									+ nodeName
									+ "' is no known destination node identifier.");
				}
				if (!destSet.add(destName)) {
					throw new InvalidPathNetException(
							"The routing information for destination '"
									+ destName + "' is set twice in node '"
									+ nodeName + "'.");
				}
				NodeList branchNodeList = routingDomNode.getChildNodes();
				double[] prob = new double[branchNodeList.getLength()];
				PathNetPath[] path = new PathNetPath[branchNodeList.getLength()];
				double sum = 0;
				for (int k = 0; k < branchNodeList.getLength(); k++) {
					org.w3c.dom.Node branchDomNode = branchNodeList.item(k);
					try {
						prob[k] = Double.parseDouble(getAttributeValue(
								branchDomNode, "PROB"));
					} catch (NumberFormatException e) {
						prob[k] = -1.0;
					}
					if (prob[k] < 0.0 || prob[k] > 1.0) {
						throw new InvalidPathNetException(
								"The given probability of routing destination '"
										+ destName + "' in node '" + nodeName
										+ "' is has to be a value in (0,1].");
					}
					String pathName = getAttributeValue(branchDomNode, "PATH");
					sum += prob[k];
					PathNetPath altPath = (PathNetPath) pathConstructTable.get(pathName);
					if (altPath == null) {
						throw new InvalidPathNetException(
								"The given path name '"
										+ pathName
										+ "' of routing destination '"
										+ destName
										+ "' in node '"
										+ nodeName
										+ "' is no identifier of an existing path.");
					}
					if (node != altPath.getFirstNode()
							&& node != altPath.getLastNode()) {
						throw new InvalidPathNetException("The given path '"
								+ pathName + "' of routing destination '"
								+ destName + "' in node '" + nodeName
								+ "' is no path to or from '" + nodeName + "'.");
					}
					path[k] = altPath;
				}
                sum=Math.round(sum*1000)/1000;
				if ( sum!= 1.0) {
					throw new InvalidPathNetException(
							"The probability of routing destination '"
									+ destName + "' in node '" + nodeName
									+ "' does not sum to 1.0.");
				}
				node.setRoutingInformation(destName, path, prob);
			}
			int size = destTable.size();
			if (destTable.containsKey(nodeName)) {
				size--;
			}
			if (destSet.size() != size) {
				String missingList = "";
				Iterator keyIterator = destTable.keySet().iterator();
				boolean first = true;
				while (keyIterator.hasNext()) {
					String key = (String) keyIterator.next();
					if (!destSet.contains(key)) {
						if (first) {
							first = false;
						} else {
							missingList += ", ";
						}
						missingList += key;
					}
				}
				throw new InvalidPathNetException("The destination nodes '"
						+ missingList
						+ "' are missing in the routing information of node '"
						+ nodeName + "'.");
			}
		}
	}
	private String getAttributeValue(org.w3c.dom.Node node, String attribute) {
		NamedNodeMap nodeMap = node.getAttributes();
		org.w3c.dom.Node attrNode = nodeMap.getNamedItem(attribute);
		return attrNode.getNodeValue();
	}
	private void checkCycle(Hashtable nodeTable) throws InvalidPathNetException {
		Enumeration destEnum = destTable.keys();
		while (destEnum.hasMoreElements()) {
			String dest = (String) destEnum.nextElement();
			Enumeration nodeEnum = nodeTable.elements();
			while (nodeEnum.hasMoreElements()) {
				PathNetNode start = (PathNetNode) nodeEnum.nextElement();
				HashSet reachable = getReachable(start, dest);
				Iterator reachableIterator = reachable.iterator();
				while (reachableIterator.hasNext()) {
					String nodeName = (String) reachableIterator.next();
					if (!nodeName.equals(dest)
							&& !nodeName.equals(start.getName())) {
						PathNetNode reached = (PathNetNode) nodeTable.get(nodeName);
						HashSet reachedReachable = getReachable(reached, dest);
						Iterator reachedReachableIterator = reachedReachable
								.iterator();
						while (reachedReachableIterator.hasNext()) {
							String reachedReachableNodeName = (String) reachedReachableIterator
									.next();
							if (reachedReachableNodeName
									.equals(start.getName())) {
								throw new InvalidPathNetException(
										"There is a cycle in routing context '"
												+ dest + "' from node '"
												+ start.getName()
												+ "' to node '"
												+ reached.getName()
												+ "' and back.");
							}
						}
					}
				}
			}
		}
	}
	private HashSet getReachable(PathNetNode start, String dest) {
		HashSet reached = new HashSet();
		Hashtable nextReached = new Hashtable();
		reached.add(start.getName());
		nextReached.put(start.getName(), start);
		while (!nextReached.isEmpty()) {
			nextReached = getNextReachable(reached, nextReached, dest);
		}
		return reached;
	}
	private Hashtable getNextReachable(HashSet reached, Hashtable lastReached,
			String dest) {
		Hashtable result = new Hashtable();
		Enumeration elementEnum = lastReached.elements();
		while (elementEnum.hasMoreElements()) {
			PathNetNode node = (PathNetNode) elementEnum.nextElement();
			PathNetNode[] following = node.getFollowingNodes(dest);
			for (int i = 0; i < following.length; i++) {
				if (reached.add(following[i].getName())) {
					result.put(following[i].getName(), following[i]);
				}
			}
		}
		return result;
	}



	/**
	 * Return the names of all existing starting/destination locations.
	 * @return an array of all location names.
	 */
	public String[] getLocationNames() {
		Set keySet = destTable.keySet();
		return (String[])keySet.toArray(new String[keySet.size()]);
	}

	/**
	 * Return the position of the location with the given name.
	 * @param name 	the name of the location.
	 * @return the position.
	 * @throws UnknownLocationException the given location does not exist.
	 */
	public Position getLocationPosition(String name)
		throws UnknownLocationException {
		PathNetNode node = (PathNetNode)destTable.get(name);
		if(node == null) {
			throw new UnknownLocationException("the location with name '" +
				name + "' is unknown.");
		}
		return new Position(node.getPosition());
	}

	/**
	 * Create a <code>String</code> representation for an object of
	 * <code>PathNet</code> class.
	 * @return the <code>String</code> representation.
	 */
	public String toString() {
		String res = "DEST:\n";
		Enumeration nodeEnum = destTable.elements();
		while(nodeEnum.hasMoreElements()) {
			Node node = (Node)nodeEnum.nextElement();
			res += node.toString() + "\n";
		}
		res += "CROSS:\n";
		Enumeration crossEnum = crossTable.elements();
		while(crossEnum.hasMoreElements()) {
			Node node = (Node)crossEnum.nextElement();
			res += node.toString() + "\n";
		}
		res += "PATH:\n";
		for(int i=0; i<path.length; i++) {
			res += path[i].toString() + "\n";
		}
		return res;
	}

	/**
	 * @see DeviceMover#getRectangle()
	 */
	public Rectangle getRectangle() {
		Rectangle res = new Rectangle(
			((PathNetNode)(destTable.elements().nextElement())).getPosition()
			,Position.NULL_POSITION); //TODO
		for(int i=0; i<path.length; i++) {
			res.union(path[i].getRectangle());
		}
		return res;
	}

	/**
	 * @see de.uni_trier.jane.simulation.dynamic.mobility_source.pathnet.DeviceMover#getName()
	 */
	public String getName() {
		return null;
	}

	/**
	 * @see de.uni_trier.jane.simulation.dynamic.mobility_source.pathnet.DeviceMover#getShape()
	 */
	public Shape getShape() {
		ShapeCollection result=new ShapeCollection();
		for (int i=0;i<path.length;i++){
			result.addShape(path[i].getShape(),Position.NULL_POSITION);
		}
		return result;
	}

	/**
	 * @see DeviceMover#getPosition()
	 */
	public Position getPosition() {
		return getRectangle().getCenter();
	}

	/**
	 * @see DeviceMover#setPosition(Position)
	 */
	public void setPosition(Position pos) {
		Position offset = new Position(pos);
		offset=offset.sub(getRectangle().getCenter());

		Enumeration en = destTable.elements();
		while(en.hasMoreElements()) {
			((PathNetNode)en.nextElement()).move(offset);
		}
		en = crossTable.elements();
		while(en.hasMoreElements()) {
			((PathNetNode) en.nextElement()).move(offset);
		}
		for(int i=0; i<path.length; i++) {
			path[i].move(offset);
		}
	}

	
	
	
	/**
	 * @see DeviceMover#createPath(DevicePath, String, String)
	 */
	public void createPath(DevicePath devicePath, String start, String finish)
		throws UnknownLocationException {
		PathNetNode startNode = (PathNetNode)destTable.get(start);
		PathNetNode endNode = (PathNetNode)destTable.get(finish);
		if(startNode == null) {
			throw new UnknownLocationException("The start location '" + start +
				"' is unknown.");
		}
		if(endNode == null) {
			throw new UnknownLocationException("The end location '" + finish +
				"' is unknown.");
		}
		startNode.createPath(devicePath, finish);
	}
	
	/**
	 * @see DeviceMover#getMinDistance(String, String)
	 */
	public double getMinDistance(String l1, String l2)
		throws UnknownLocationException {
		return 0; // TODO:
	}

}


