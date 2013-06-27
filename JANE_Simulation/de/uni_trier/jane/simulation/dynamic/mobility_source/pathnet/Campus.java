
/*****************************************************************************
 * 
 * Campus.java
 * 
 * $Id: Campus.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.random.*;

/**
 * class Campus is one possible PathNet realization. It tries to model a university campus with lecture rooms and a connecting PathNet.
 * 
 */
public final class Campus extends DeviceMoverTree {
	
	/**
	 * Constructor for class <code>Campus</code>
	 * @param name							the name of this pathnet
	 * @param pathNetFile					the XML file containing the pathnet description 
	 * @param roomCollectionFile			the XML file containing the room descriptions
	 * @param distributionCreator			the simulation <code>DistributionCreatort</code>
	 * @throws InvalidPathNetException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException					
	 */

	public Campus(String name, String pathNetFile, String roomCollectionFile, DistributionCreator distributionCreator)
	
		throws InvalidPathNetException, SAXException,
		ParserConfigurationException, IOException {
		super(name, new PathNet(pathNetFile,distributionCreator),
		
		loadRoomCollection(roomCollectionFile,distributionCreator));
		
	}

	/**
	 * loads the room collection from file
	 * @param fileName				name of XML file  containing the room collection descriptions
	 * @param distributionCreator	the simulation <code>DistributionCreatort</code>
	 * @return		an array of all <code>LectureRooms</code> 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private static DeviceMover[] loadRoomCollection(String fileName, DistributionCreator distributionCreator)
		throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringComments(true);
		dbf.setValidating(true);
		dbf.setIgnoringElementContentWhitespace(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		db.setErrorHandler(
			new ErrorHandler() {
				public void warning(SAXParseException e)
					throws SAXException {
					throw e;
				}
				public void error(SAXParseException e)
					throws SAXException {
					throw e;
				}
				public void fatalError(SAXParseException e)
					throws SAXException {
					throw e;
				}
			}
		);
		Document doc = db.parse(fileName);
		NodeList nodeList = doc.getElementsByTagName("ROOM");
		DeviceMover[] result = new DeviceMover[nodeList.getLength()];
		for(int i=0; i<nodeList.getLength(); i++) {
			try {
				org.w3c.dom.Node node = nodeList.item(i);
				String name = getAttributeValue(node, "NAME");
				double width =
					Double.parseDouble(getAttributeValue(node, "WIDTH"));
				double depth =
					Double.parseDouble(getAttributeValue(node, "DEPTH"));
				
				NodeList childNodeList = node.getChildNodes();
				Position[] pos = new Position[childNodeList.getLength()];
				for(int j=0; j<childNodeList.getLength(); j++) {
					org.w3c.dom.Node childNode = childNodeList.item(j);
					double x =
						Double.parseDouble(getAttributeValue(childNode, "X"));
					double y =
						Double.parseDouble(getAttributeValue(childNode, "Y"));
					
						
					pos[j] = new Position(x, y);
				}
				result[i] = new LectureRoom(name,new Extent(width, depth), pos,distributionCreator);
			}
			catch(NumberFormatException e) {
				// TODO:
				e.printStackTrace();
			}
		}
		return result;
	}

	private static String getAttributeValue(org.w3c.dom.Node node,
		String attribute) {
		NamedNodeMap nodeMap = node.getAttributes();
		org.w3c.dom.Node attrNode = nodeMap.getNamedItem(attribute);
		return attrNode.getNodeValue();
	}

	


}
