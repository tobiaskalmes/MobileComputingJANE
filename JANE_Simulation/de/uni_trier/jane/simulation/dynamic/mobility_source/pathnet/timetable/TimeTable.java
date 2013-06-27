/*****************************************************************************
 * 
 * TimeTable.java
 * 
 * $Id: TimeTable.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.gui.*;
import de.uni_trier.jane.simulation.kernel.*;
import de.uni_trier.ubi.appsim.kernel.dynamic.mobility_source.pathnet.timetable.*;

/**
 * The TimeTable used for moving devices. The timetable information is parsed from an XML file.
 * This class can also be used as the <code>ApplicationUserSource</code> for the simualtion.
 * Than, the application and the user behaviour for each device are created using the parameters given 
 * in the XML timetable configuration file 
 */
public class TimeTable implements SimulationServiceFactory{
	
	
	private HashMap addressEventMap;
	//private HashMap nameAddresMap;
	
	private double minimumRadius;
	private double maximumRadius;
	
	private ClassManager classManager;
	private ContinuousDistribution selectDistribution;
	private ContinuousDistribution fuzzDistribition;
	//private HashMap addressUserMap;
    private HashMap addressParametersMap;
    private HashMap addressDeviceTimetable;
	
	/**
	 * Constructor for class <code>TimeTable</code>
	 * @param filename					the filename containing the timetable configuration data 
	 * @param distributionCreator		the simualtion <code>DistributionCreator</code>
	 * @throws InvalidTimeTableException 	is thrown when a parse error occurs	
	 */
	public TimeTable(String filename, DistributionCreator distributionCreator) throws InvalidTimeTableException {
		
		selectDistribution=distributionCreator.getContinuousUniformDistribution(0,1);
		fuzzDistribition=distributionCreator.getNormalDistribution(0,1);
		addressParametersMap=new HashMap();
		addressEventMap=new HashMap();
		addressDeviceTimetable=new HashMap();
		//nameAddresMap=new HashMap();
		//addressUserMap=new HashMap();
		
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringComments(true);
		dbf.setValidating(true);
		dbf.setIgnoringElementContentWhitespace(true);
		Document doc = null;
		try {
		DocumentBuilder db = dbf.newDocumentBuilder();
		// no error handler for the moment // FIXME
		doc = db.parse(new File(filename));		
		} catch (ParserConfigurationException e) {
			throw new InvalidTimeTableException(e.getMessage());
		} catch (SAXException e) {
			throw new InvalidTimeTableException(e.getMessage()); 
		} catch (IOException e) {
			throw new InvalidTimeTableException(e.getMessage());
		}
		// get all classes
		NodeList classNodeList = doc.getElementsByTagName("CLASS");
		classManager = new ClassManager(classNodeList,distributionCreator);
		
		Collection allDevices=classManager.getAllDevices();
		Iterator iterator=allDevices.iterator();
		int count=0;
		
		minimumRadius = Double.MAX_VALUE;
		maximumRadius = 0;
		
		while (iterator.hasNext()){
			
			MobileDeviceParameter parameter=(MobileDeviceParameter)iterator.next();
			
			minimumRadius = Math.min(minimumRadius, parameter.getPower());
			maximumRadius = Math.max(maximumRadius, parameter.getPower());
			
			DeviceID address=parameter.getAddress();
			MoveEventProvider moveEventProvider=new TimetableMoveEventProvider(parameter.getName());
			addressEventMap.put(address,moveEventProvider);
			addressDeviceTimetable.put(address,new DeviceTimeTable(parameter,moveEventProvider));
			addressParametersMap.put(parameter.getAddress(),parameter);
///createApplicationUserPair(parameter);
			//nameAddresMap.put(parameter.getName(),address);
			count++;
		}
		// get all events
		NodeList enterNodeList   = doc.getElementsByTagName("ENTER");
		NodeList exitNodeList    = doc.getElementsByTagName("EXIT");
		NodeList eventNodeList 	 = doc.getElementsByTagName("EVENT");
		NodeList suspendNodeList = doc.getElementsByTagName("SUSPEND");
		createEnterEvents(enterNodeList);
		createEventEvents(eventNodeList);
		createEventEvents(exitNodeList);
		createSuspendEvents(suspendNodeList);
	}

	/**
	 * 
	 * @param parameter
	 * @param serviceUnit
	 * @throws InvalidTimeTableException
	 */
	private void createServices(MobileDeviceParameter parameter, ServiceUnit serviceUnit) throws InvalidTimeTableException {
		ServiceParameters[] parameters=parameter.getServiceClassParameters();
		for (int i=0;i<parameters.length;i++){
			try {
				Class classToLoad = Class.forName(parameters[i].getClassName());
				Constructor constructor = classToLoad.getConstructor(new Class[] {});
				Service service=(Service) constructor.newInstance(new Object[] { });
				serviceUnit.addService(service,parameters[i].visualize());
				if (parameters[i].hasParameters()){
					if (TimeTableCreateable.class.isAssignableFrom(service.getClass())){
						((TimeTableCreateable)service).inititializeParameters(parameters[i]);
					}else throw new InvalidTimeTableException("User class for member "+parameter.getName()+" has parameters but is not TimeTableCreateble");
				}
				
			} catch (ClassNotFoundException e) {
				throw new InvalidTimeTableException("Class for member "+parameter.getName()+" not found: "+e.getMessage());
			} catch (NoSuchMethodException e) {
				throw new InvalidTimeTableException("Class for member "+parameter.getName()+" has no constructor ()");
			} catch (InstantiationException e) {
				throw new InvalidTimeTableException("Can't instantiate class for member "+parameter.getName()+": "+e.getMessage());
			} catch (InvocationTargetException e) {
				throw new InvalidTimeTableException("Error while creating object for member "+parameter.getName()+": "+e.getTargetException().getMessage());
			} catch (IllegalAccessException e) {
				throw new InvalidTimeTableException("Illegal access while creating object for member "+parameter.getName()+": "+e.getMessage());
			}
		}
		

	}

	/**
	 * Returns all <code>DeviceTimeTable</code>s for all devices.
	 * Device adresses are mapped to their DeviceTimeTable
	 * @return	the addressDeviceTimeTableMap
	 */
	public HashMap getDeviceTimeTableMap(){
		return addressDeviceTimetable;
		
	}
	
	/**
	 * 
	 * @param eventNodeList
	 * @throws InvalidTimeTableException
	 */
	private void createEventEvents(NodeList eventNodeList) throws InvalidTimeTableException {
		for(int i=0; i<eventNodeList.getLength(); i++) {
			EventWrapper eventWrapper=new EventWrapper(eventNodeList.item(i),classManager);
			
			DeviceID[] participatingDevices=eventWrapper.getParticipants();
			for (int j=0;j<participatingDevices.length;j++){
				//Address deviceAddress=(Address)(participatingDevices[j]);
			    TimetableMoveEventProvider deviceTimeTable=(TimetableMoveEventProvider)addressEventMap.get(participatingDevices[j]);
				String placeName=eventWrapper.getPlace(selectDistribution.getNext());
				
				double enterTime=eventWrapper.getTime()+computeRealFuzz(eventWrapper.getFuzz());
				
				
				deviceTimeTable.setMainMoveEvent(placeName,enterTime);
				
			}
			
		}
		
	}
	
	/**
	 * 
	 * @param eventNodeList
	 * @throws InvalidTimeTableException
	 */
	private void createSuspendEvents(NodeList eventNodeList) throws InvalidTimeTableException {
		for(int i=0; i<eventNodeList.getLength(); i++) {
			EventWrapper eventWrapper=new EventWrapper(eventNodeList.item(i),classManager);
			
			DeviceID[] participatingDevices=eventWrapper.getParticipants();
			for (int j=0;j<participatingDevices.length;j++){
				//Address deviceAddress=(Address)(participatingDevices[j]);
			    TimetableMoveEventProvider deviceTimeTable=(TimetableMoveEventProvider)addressEventMap.get(participatingDevices[j]);
				String placeName=eventWrapper.getPlace(selectDistribution.getNext());
				
				double enterTime=eventWrapper.getTime()+computeRealFuzz(eventWrapper.getFuzz());
				
				
				deviceTimeTable.setSuspendMoveEvent(placeName,enterTime);
				
			}
			
		}
		
	}

	/**
	 * 
	 * @param fuzz
	 * @return	double
	 */
	private double computeRealFuzz(double fuzz) {
		double z = 2.6; // the corresponding alpha is 0.99
		double random;
		do {
			random = (fuzzDistribition.getNext() * (fuzz/z) + fuzz);
		} while ((random < 0) || (random > 2*fuzz));
		return random;			
	}
	
	/**
	 * 
	 * @param enterNodeList
	 * @throws InvalidTimeTableException
	 */
	private void createEnterEvents(NodeList enterNodeList) throws InvalidTimeTableException {
		for(int i=0; i<enterNodeList.getLength(); i++) {
			EventWrapper eventWrapper=new EventWrapper(enterNodeList.item(i),classManager);
			DeviceID[] participatingDevices=eventWrapper.getParticipants();
			for (int j=0;j<participatingDevices.length;j++){
				//Address deviceAddress=(Address)nameAddresMap.get(participatingDevices[j]);
			    TimetableMoveEventProvider deviceTimeTable=(TimetableMoveEventProvider)addressEventMap.get(participatingDevices[j]);
				String placeName=eventWrapper.getPlace(selectDistribution.getNext()); 
				double enterTime=eventWrapper.getTime()+computeRealFuzz(eventWrapper.getFuzz());
				deviceTimeTable.setEnterEvent(placeName,enterTime);
				
			}
			
		}
	
		
	}

	
    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.kernel.ServiceFactory#initServices(de.uni_trier.jane.basetypes.DeviceID, de.uni_trier.jane.simulation.kernel.ServiceCollection)
     */
    public void initServices(ServiceUnit serviceUnit) {
        MobileDeviceParameter deviceParameter=(MobileDeviceParameter)addressParametersMap.get(serviceUnit.getDeviceID());
        try {
            createServices(deviceParameter,serviceUnit);
        } catch (InvalidTimeTableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.kernel.ServiceFactory#initGlobalServices(de.uni_trier.jane.simulation.kernel.ServiceCollection)
     */
    public void initGlobalServices(ServiceUnit serviceUnit) {
        // TODO Auto-generated method stub
        
    }

	public double getMaximumRadius() {
		return maximumRadius;
	}

	public double getMinimumRadius() {
		return minimumRadius;
	}
	
}
