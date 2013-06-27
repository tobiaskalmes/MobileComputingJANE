/*****************************************************************************
 * 
 * ClassManager.java
 * 
 * $Id: ClassManager.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.ubi.appsim.kernel.dynamic.mobility_source.pathnet.timetable.*;

/**
 * 
 * This class parses the device and class definitions for the time table.
 * It parses all device, application and user parameters for each device and 
 * provides this informations, organized be device classes, to the timetable
 */
public class ClassManager {
	
	private Hashtable table;
	private TreeMap allDevices;
    private DistributionCreator distributionCreator; 
	
	/**
	 * Constructor for class <code>ClassManager</code>
	 * @param classes						the XERCES  node list of the definition of all classes and their devices
	 * @throws InvalidTimeTableException
	 */
	public ClassManager(NodeList classes, DistributionCreator distributionCreator) throws InvalidTimeTableException {
	    this.distributionCreator=distributionCreator;
		table = new Hashtable();
		allDevices=new TreeMap();
		buildTable(classes);
	}
		
	private void buildTable(NodeList classes) throws InvalidTimeTableException {
		for(int i=0; i<classes.getLength(); i++) {
			Node cl = classes.item(i);
			NodeList memberList = cl.getChildNodes();
			MobileDeviceParameter[] members = new MobileDeviceParameter[memberList.getLength()];
			
			for(int j=0; j<memberList.getLength(); j++) {
				Node member = memberList.item(j);
				String name = getAttributeValue(member, "NAME");
				DeviceID address;
				double power = 0.0;
				double minSpeed=0.0;
				double maxSpeed=0.0;
				String id=getAttributeValue(member, "ID");
				if (!id.startsWith("D")){
					throw new InvalidTimeTableException("Could not parse ID definition: ID must start with D for input: "+id);
				}
				id=id.substring(1);
				try {
					
					address=new SimulationDeviceID(Long.parseLong(id));
				} catch (NumberFormatException e) {
					throw new InvalidTimeTableException("Could not parse ID definition: "+
					                                    e.getMessage()+
														"Format of unique(!) ID: \"Dx\" where x is an integer");
				}

				try {
					power = Double.parseDouble(getAttributeValue(member, "POWER"));
				} catch (NumberFormatException e) {
					throw new InvalidTimeTableException("Could not parse power definition: "+
					                                    e.getMessage());
				}
				try {
					minSpeed = Double.parseDouble(getAttributeValue(member, "MINSPEED"));
				} catch (NumberFormatException e) {
					throw new InvalidTimeTableException("Could not parse minimum speed definition: "+
					                                    e.getMessage());
				}
				if (hasAttributeValue(member,  "MAXSPEED")){
					try {
						maxSpeed = Double.parseDouble(getAttributeValue(member, "MAXSPEED"));
					} catch (NumberFormatException e) {
						throw new InvalidTimeTableException("Could not parse maximum speed definition: "+
								e.getMessage());
					}
				}else{
					maxSpeed=minSpeed;
				}
				
				members[j]=new MobileDeviceParameter(address, name, power, distributionCreator.getContinuousUniformDistribution(minSpeed, maxSpeed));
				loadApplication(member.getChildNodes().item(0).getChildNodes(), members[j],name);
				
				
				allDevices.put(members[j].getAddress(),members[j]);
				
			}
			String className = getAttributeValue(cl, "NAME");
			table.put(className, members);
		}
	}	
		
	/**
	 * @param list
	 * @param parameter
	 * @param deviceName
	 */
	private void loadApplication(NodeList list, MobileDeviceParameter parameter, String deviceName) {
	    for (int i=0;i<list.getLength();i++){
	        Node userClass=list.item(i);
	        ServiceParameters userParameters =new ServiceParameters(getAttributeValue(userClass, "NAME"),deviceName, hasAttributeValue(userClass,"VISUALIZE"));
	        NodeList paramList = userClass.getChildNodes();
	        for(int k=0; k<paramList.getLength(); k++) {
	            Node paramNode = paramList.item(k);
	            userParameters.addParameter(getAttributeValue(paramNode, "NAME"), 
			                        getAttributeValue(paramNode, "VALUE"));
	        }
	        parameter.addServiceClassParameters(userParameters);
	    }
	

	}

	/**
	 * returns all MobileDeviceParameters for a given class.
	 * Each MobileDeviceParameter desribes one mobile device.
	 * isClasssDefined should be called first.
	 * @param className		the unique name of a class
	 * @return	the MobileDeviceParameters
	 */
	public MobileDeviceParameter[] getMembersOfClass(String className) {
		return (MobileDeviceParameter[]) table.get(className);
	}
	
	/**
	 * Checks  wether a class with the name className exists
	 * @param className		the name of the class
	 * @return			true if the class is defined
	 */
	public boolean isClassDefined(String className) {
		return table.containsKey(className);
	}
	
	public String toString() {
		StringBuffer result = new StringBuffer();
		Enumeration enumeration = table.keys();
		while (enumeration.hasMoreElements()) {
			String className = (String) enumeration.nextElement();
			result.append(className);
			result.append(": ");
			MobileDeviceParameter[] device = (MobileDeviceParameter[]) table.get(className);
			for(int i=0; i<device.length; i++) {
				result.append(device[i].getName());
				result.append(" ");
			}
			result.append("\n");
		}
		return result.toString();
	}
	
	private String getAttributeValue(Node node, String attribute) {
		NamedNodeMap nodeMap = node.getAttributes();
		Node attrNode = nodeMap.getNamedItem(attribute);
		
		return attrNode.getNodeValue();
	}
	private boolean hasAttributeValue(Node node, String attribute) {
		NamedNodeMap nodeMap = node.getAttributes();
		Node attrNode = nodeMap.getNamedItem(attribute);
		
		return attrNode!=null;
	}

	/**
	 * Returns a Collection of all known <code>MobileDeviceParameter</code>s
	 * @return	the <code>Collection</code>
	 */
	 
	public Collection getAllDevices() {
		return allDevices.values();
		
	}
	
}

