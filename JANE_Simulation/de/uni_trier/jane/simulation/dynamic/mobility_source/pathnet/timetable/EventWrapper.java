/*****************************************************************************
 * 
 * EventWrapper.java
 * 
 * $Id: EventWrapper.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
package de.uni_trier.jane.simulation.dynamic.mobility_source.pathnet.timetable;

import java.util.*;

import org.w3c.dom.*;

import de.uni_trier.jane.basetypes.*;

/**
 * This class represents a timetable event. It parses its timetable information from a given XML node.
 * A timetable event has group of participating devices which is the union of all participating classes.
 * It has a set of possible locations including the propability for sel;ecting this place.
 * Moreover it has an endtime and a fuzz value for varieng the endtime for each device.
 */

public class EventWrapper implements Comparable {
	private Node node;
	private int time;
	private int fuzz;
	private DeviceID[] participants;
	private Place[] places;
	private ClassManager classManager;
	
	/**
	 * Constructor for class <code>EventWrapper</code>
	 * @param node					the XERCES XML Node containing all event information
	 * @param classManager			the <code>ClassManager</code> containing all class/device mappings
	 * @throws InvalidTimeTableException		if a parse error occurs
	 */
	public EventWrapper(Node node,ClassManager classManager) throws InvalidTimeTableException {
		this.classManager=classManager;
		this.node = node;
		time = 0;
		fuzz = 0;
		try {
			time = Integer.parseInt(getAttributeValue(node, "END"));
			fuzz = Integer.parseInt(getAttributeValue(node, "FUZZ"));
		} catch (NumberFormatException e) {
			throw new InvalidTimeTableException("Could not parse END/FUZZ attribute");
		}					
		NodeList childs = node.getChildNodes();
		participants = extractParticipants(childs);
		places = extractPlaces(childs);
	}

	private DeviceID[] extractParticipants(NodeList childs) throws InvalidTimeTableException {
		ArrayList tmp = new ArrayList();							
		for(int i=0; i<childs.getLength(); i++) {
			Node childNode = childs.item(i);
			if (childNode.getNodeName().equals("PARTICIPANTS")) {
				String className = getAttributeValue(childNode, "CLASS");
				if (!classManager.isClassDefined(className)) {
					throw new InvalidTimeTableException("ENTER '"+getAttributeValue(node, "NAME")+
					                                    "' references unknown CLASS '"+className+"'.");
				}else{
					MobileDeviceParameter[] devices=classManager.getMembersOfClass(className);
					for (int j=0;j< devices.length;j++){
						tmp.add(devices[j].getAddress());
					}
				}
				
			} 
		}
		DeviceID[] result = new DeviceID[tmp.size()];
		tmp.toArray(result);
		return result;
	}
	
	private Place[] extractPlaces(NodeList childNodes)
		throws InvalidTimeTableException {
		ArrayList places = new ArrayList();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			if (childNode.getNodeName().equals("PLACE")) {
				String placeName = getAttributeValue(childNode, "NAME");
				double prob = 0.0;
				try {
					prob = Double.parseDouble(getAttributeValue(childNode, "PROB"));
				} catch (NumberFormatException e) {
					throw new InvalidTimeTableException(
						"PROB attribute of PLACE '" + placeName + "' must be a double.");
				}
				places.add(new Place(placeName, prob));
			}
		}
		Collections.sort(places);
		double sumProb = 0.0;
		Iterator iter = places.iterator();
		while (iter.hasNext()) {
			Place element = (Place) iter.next();
			sumProb += element.getProbability();
			element.setProbability(sumProb);
		}
		Place[] result = new Place[places.size()];
		places.toArray(result);
		return result;
	}

	/**
	 * Returns all participants of this event
	 * @return		addresses of all participating devices
	 */
	public DeviceID[] getParticipants() {
		return participants;
	}
	

	/**
	 * Returns  the location name for a given random value between 0 and 1
	 * @param rand	random value between 0 and 1
	 * @return	the name of the random target location
	 */
	public String getPlace(double rand) {
		for (int i=0;i<places.length;i++){
			if (places[i].getProbability()>=rand) return places[i].getName();
		}
		return places[places.length-1].getName();
	
	}
	
	public boolean equals(Object other) {
		// only the field time is significant!
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (other.getClass() != getClass()) {
			return false;
		}
		EventWrapper oth = (EventWrapper) other;
		if (time != oth.time) {
			return false;
		}					
		return true;
	}
	
	public int compareTo(Object other) {
		if (other.getClass() != getClass()) {
			throw new ClassCastException("Can't compare EventWrapper with "+other.getClass().getName());
		} 
		EventWrapper oth = (EventWrapper) other;
		if (oth.time > time) {
			return -1;
		} else if (oth.time == time) {
			return 0;
		} else {
			return 1;
		}
	}
	
//	public Node getNode() {
//		return node;
//	}
//	
	
	
	private String getAttributeValue(Node node, String attribute) {
		NamedNodeMap nodeMap = node.getAttributes();
		Node attrNode = nodeMap.getNamedItem(attribute);
		return attrNode.getNodeValue();
	}

	/**
	 * Returns the Endtime for this event
	 * @return	the endtime
	 */
	public double getTime() {

		return time;//+fuzz*(1-rand);
	}
	
	/**
	 * Returns the fuzz value
	 * @return Returns the fuzz.
	 */
	public int getFuzz() {
		return fuzz;
	}
	
	
	
}