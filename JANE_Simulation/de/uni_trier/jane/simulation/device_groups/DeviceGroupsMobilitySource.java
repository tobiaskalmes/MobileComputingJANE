/*
 * Created on 01.06.2005
 * File: ServiceStartMobilitySource.java
 */
package de.uni_trier.jane.simulation.device_groups;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.uni_trier.jane.basetypes.Clock;
import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.basetypes.Rectangle;
import de.uni_trier.jane.basetypes.SimulationDeviceID;
import de.uni_trier.jane.service.unit.ServiceFactory;
import de.uni_trier.jane.simulation.*;
import de.uni_trier.jane.simulation.dynamic.mobility_source.ClickAndPlayMobilitySource;
import de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource;
import de.uni_trier.jane.simulation.kernel.Condition;
import de.uni_trier.jane.visualization.shapes.Shape;
import de.uni_trier.jane.visualization.shapes.ShapeCollection;

/**
 * Use the <code>DeviceGroupsMobilitySource</code> class to define different 
 * groups of devices with different properties. To define a group the
 * <code>DeviceGroup</code> class is used. Each group has her own mobility
 * source and implements the <code>ServiceFactory</code> interface to specify
 * the services to start on the devices in the group. You can also load the
 * device configuration from a XML file (see the corresponding document type
 * definition file <code>DeviceConfiguration.dtd</code>).
 *  
 * <p>
 * Example:
 * <pre>
 * public class MySimulation extends DeviceGroupsSimulation {
 *     public void initSimulation(SimulationParameters parameters) {
 *         // change some simulation parameters
 *         // Note: The initMobilitySource method is called before initSimulation.
 *         // So the parameters.getMobilitySource method returns a DevcieGroupsMobitySource.
 *         parameters.useVisualization(new ClickAndPlaySimulationFrame(
 *         	   (DeviceGroupsMobilitySource) parameters.getMobilitySource()
 *         ));
 *     }
 *     
 *     public void init GlobalServices(ServiceUnit serviceUnit) {
 *         // start your global services here
 *     }
 *     
 * 	   public DeviceGroupsMobilitySource initMobilitySource(SimulationParameters parameters) {
 *         // create some groups
 *         DeviceGroup[] groups = new DeviceGruop[] {
 *                 new MyDeviceGroup1(parameters, DEVICE_COUNT_1),
 *                 new MyDeviceGroup2(parameters, DEVICE_COUNT_2)
 *         };
 *         // create the mobility source
 *	       DeviceGroupsMobilitySource mobilitySrc = new DeviceGroupsMobilitySource(groups);
 *		   return mobilitySrc;
 *	   }
 * }
 * </pre>
 * </p>
 * 
 * @author christian.hoff
 * @version 01.06.2005
 */
public class DeviceGroupsMobilitySource implements ClickAndPlayMobilitySource {
	AddressManagement groups;
    int deviceCount;
    Rectangle bounds;
    ShapeCollection shape;
    
    public DeviceGroupsMobilitySource(DeviceGroup[] deviceGroups,SimulationParameters simulationParameters) {
    	init(deviceGroups,simulationParameters);
    }
    
    public DeviceGroupsMobilitySource(
    		SimulationParameters parameters,
    		String deviceConfigurationFileName
    ) {
    	// create a factory to obtain a parser that produces DOM object trees
    	// from the speciefied XML file
    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    	dbf.setIgnoringComments(true);
    	dbf.setValidating(true);
    	dbf.setIgnoringElementContentWhitespace(true);
    	// get the DOM document from the XML file
    	Document doc = null;
    	try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(deviceConfigurationFileName);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		// parse the DOM document and create the device groups
		NodeList nodeListDeviceGroups = doc.getElementsByTagName("DeviceGroup");
		DeviceGroup[] deviceGroups = new DeviceGroup[nodeListDeviceGroups.getLength()];
		for(int i = 0; i < nodeListDeviceGroups.getLength(); i++) {
			// now we have a single DeviceGroup
			Node nodeDeviceGroup = nodeListDeviceGroups.item(i);
			NamedNodeMap attributes = nodeDeviceGroup.getAttributes();
			// get the deviceClass for this DeviceGroup
			String deviceClass = attributes.getNamedItem("deviceClass").getNodeValue();
			// get the numberOfDevices
			// note: this is an implied attribute
			Node nodeNumberOfDevices = attributes.getNamedItem("numberOfDevices");
			int numberOfDevices = 0;
			if(nodeNumberOfDevices != null) {
				numberOfDevices = Integer.parseInt(nodeNumberOfDevices.getNodeValue());
			}
            numberOfDevices = Math.max(numberOfDevices, nodeDeviceGroup.getChildNodes().getLength());
			// use reflection to create an instance of the current device group
			try {
				Class groupClass = Class.forName(deviceClass);
				if(groupClass.getSuperclass() != DeviceGroup.class) {
					// TODO error: this should not happen
				}
				Constructor constructor = groupClass.getConstructor(
						new Class[] { SimulationParameters.class , int.class }
				);
				deviceGroups[i] = (DeviceGroup) constructor.newInstance(
						new Object[] { parameters, new Integer(numberOfDevices) }
				);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// process fixed devices in this DeviceGroup
			NodeList devices = nodeDeviceGroup.getChildNodes();
			for(int j = 0; j < devices.getLength(); j++) {
				Node nodeDevice = devices.item(j);
				attributes = nodeDevice.getAttributes();
				int x = Integer.parseInt(
						attributes.getNamedItem("x").getNodeValue()
				);
				int y = Integer.parseInt(
						attributes.getNamedItem("y").getNodeValue()
				);
				deviceGroups[i].addPosition(new Position(x, y));
			}
		}
		init(deviceGroups,parameters);
    }

    private void init(DeviceGroup[] deviceGroups, SimulationParameters simulationParameters) {
    	
        this.deviceCount = 0;
        this.bounds = new Rectangle(0, 0, 0, 0);
        this.shape = new ShapeCollection();
        for(int i = 0; i < deviceGroups.length; i++) {
            deviceGroups[i].init(simulationParameters);
            
        	MobilitySource ms = deviceGroups[i].getMobilitySource();
            shape.addShape(ms.getShape());
        	bounds = bounds.union(ms.getRectangle());
        	deviceCount += ms.getTotalDeviceCount();
        }
        groups = new AddressManagement(deviceGroups);
    }
    
    /**
     * Gets the corresponding <code>ServiceFactory</code> for the specified
     * device.
     * @param device The device we are looking for a <code>ServiceFactory</code>.
     * @return The <code>ServiceFactory</code> for the specified device.
     */
    public ServiceFactory getServiceFactory(DeviceID device) {
    	return groups.getDeviceGroup(device);
    }
 
    //### ClickAndPlayMobilitySource methods ###################################
 
    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.dynamic.mobility_source.ClickAndPlayMobilitySource#setPosition(de.uni_trier.jane.basetypes.DeviceID, de.uni_trier.jane.basetypes.Position)
     */
    public void setPosition(DeviceID device, Position newPosition) {
        // get the mobility source for the specified address
        MobilitySource ms = groups.getDeviceGroup(device).getMobilitySource();
        // map the specified address to the original address
        DeviceID orgAddress = groups.getOriginalAddress(device);
		// check if the mobility source is a click and play mobility source
		if(ms instanceof ClickAndPlayMobilitySource) {
	        // forward the setPosition request
			((ClickAndPlayMobilitySource) ms).setPosition(orgAddress, newPosition);
		}
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.dynamic.mobility_source.ClickAndPlayMobilitySource#getAddress(de.uni_trier.jane.basetypes.Rectangle)
     */
    public DeviceID[] getAddress(Rectangle rectangle) {
        Group[] groups = this.groups.groups;
    	ArrayList devices = new ArrayList();
    	for(int i = 0; i < groups.length; i++) {
            MobilitySource ms = groups[i].getMobilitySource();
    		// check if the mobility source is a click and play mobility source
    		if(ms instanceof ClickAndPlayMobilitySource) {
    			// forward the getAddress request
                DeviceID[] addresses = ((ClickAndPlayMobilitySource) ms).getAddress(rectangle);
                // translate the addresses
                for(int j = 0; j < addresses.length; j++) {
                    addresses[j] = groups[i].getAddress(addresses[j]);
                }
                // collect the results
    			devices.addAll(Arrays.asList(addresses));
    		}
    	}
    	return (DeviceID[]) devices.toArray(new DeviceID[devices.size()]);
    }

    //### MobilitySource methods ###############################################

    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource#hasNextEnterInfo()
     */
    public boolean hasNextEnterInfo() {
        return groups.hasNextEnterInfo();
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource#getNextEnterInfo()
     */
    public EnterInfo getNextEnterInfo() {
        return groups.getNextEnterInfo();
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource#hasNextArrivalInfo(de.uni_trier.jane.basetypes.DeviceID)
     */
    public boolean hasNextArrivalInfo(DeviceID address) {
        // get the mobility source for the specified address
        MobilitySource mobilitySource = 
            groups.getDeviceGroup(address).getMobilitySource();
        // map the specified address to the original address
        DeviceID orgAddress = groups.getOriginalAddress(address);
        return mobilitySource.hasNextArrivalInfo(orgAddress);
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource#getNextArrivalInfo(de.uni_trier.jane.basetypes.DeviceID)
     */
    public ArrivalInfo getNextArrivalInfo(DeviceID address) {
        // get the mobility source for the specified address
        MobilitySource mobilitySource =
        	groups.getDeviceGroup(address).getMobilitySource();
        // map the specified address to the original address
        DeviceID orgAddress = groups.getOriginalAddress(address);
//        Shape shape = groups.getDeviceGroup(address).getShape(orgAddress);
//        if(shape != null) {
//            this.shape.addShape(shape);
//        }
        return mobilitySource.getNextArrivalInfo(orgAddress);
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource#getRectangle()
     */
    public Rectangle getRectangle() {
        return bounds;
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource#getShape()
     */
    public Shape getShape() {
        return shape;
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource#getTotalDeviceCount()
     */
    public int getTotalDeviceCount() {
        return deviceCount;
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource#getTerminalCondition(de.uni_trier.jane.basetypes.Clock)
     */
    public Condition getTerminalCondition(Clock clock) {
        // TODO Auto-generated method stub
        return null;
    }

    //### class Group ##########################################################
    
    class Group {
        DeviceGroup deviceGroup;
        HashMap addressMap;
        int nextPositionPointer;
        
        Group(DeviceGroup group) {
            this.deviceGroup = group;
            addressMap = new HashMap();
            nextPositionPointer = 0;
        }
        
        DeviceID getAddress(DeviceID orgAddress) {
            return (DeviceID) addressMap.get(orgAddress);
        }
        
        MobilitySource getMobilitySource() {
            return deviceGroup.mobilitySource;
        }
        
        boolean hasNextPosition() {
        	return nextPositionPointer < deviceGroup.getPositionCount();
        }
        
        Position getNextPosition() {
        	return deviceGroup.getPosition(nextPositionPointer++);
        }
        
        double getMinimumTransmissionRange() {
        	return deviceGroup.getMinimumTransmissionRange();
        }

        double getMaximumTransmissionRange() {
        	return deviceGroup.getMaximumTransmissionRange();
        }


    }

    //### class AddressManagement ##############################################
    
    class AddressManagement {
        int enterInfoPointer;
        int lastAssignedAddress;
    	Group[] groups;
    	HashMap globalAddressMap;
    	HashMap addressGroupMap;
    	
    	double minimumRadius;
    	double maximumRadius;
    	
    	AddressManagement(DeviceGroup[] groups) {
    		this.groups = new Group[groups.length];
            this.enterInfoPointer = 0;
            this.lastAssignedAddress = 0;
            
            minimumRadius = Double.MAX_VALUE;
            maximumRadius = 0;
            
    		for(int i = 0; i < groups.length; i++) {
    			this.groups[i] = new Group(groups[i]);
    			
    			minimumRadius = Math.min(minimumRadius, this.groups[i].getMinimumTransmissionRange());
    			maximumRadius = Math.max(maximumRadius, this.groups[i].getMaximumTransmissionRange());
    			
    		}
    		globalAddressMap = new HashMap();
    		addressGroupMap = new HashMap();
    	}
    	
    	DeviceGroup getDeviceGroup(DeviceID device) {
    		return ((Group) addressGroupMap.get(device)).deviceGroup;
    	}
    	
    	DeviceID getOriginalAddress(DeviceID device) {
    		return (DeviceID) globalAddressMap.get(device);
    	}
    	
    	MobilitySource[] getMobilitySources() {
    		MobilitySource[] mss = new MobilitySource[groups.length];
    		for(int i = 0; i < mss.length; i++) {
    			mss[i] = groups[i].deviceGroup.mobilitySource;
    		}
    		return mss;
    	}
    	
        boolean hasNextEnterInfo() {
            if(!(enterInfoPointer < groups.length)) {
                return false;
            }
            // is there an enter info in the current mobility source left?
            if(groups[enterInfoPointer].deviceGroup.mobilitySource.hasNextEnterInfo()) {
                return true;
            }
            enterInfoPointer++;
            return hasNextEnterInfo();
        }
        
        EnterInfo getNextEnterInfo() {
            Group group = groups[enterInfoPointer];
            EnterInfo org = group.deviceGroup.getMobilitySource().getNextEnterInfo();
            DeviceID address = new SimulationDeviceID(++lastAssignedAddress);
            globalAddressMap.put(address, org.getAddress());
            group.addressMap.put(org.getAddress(), address);
            addressGroupMap.put(address, group);
            if(group.hasNextPosition()) {
            	MobilitySource ms = group.getMobilitySource();
        		if(ms instanceof ClickAndPlayMobilitySource) {
        	        // set the position
        			((ClickAndPlayMobilitySource) ms).setPosition(
        					org.getAddress(),
        					group.getNextPosition()
        			);
        		}

            }
            Shape shape = group.deviceGroup.getShape(org.getAddress());
            if(shape != null) {
                DeviceGroupsMobilitySource.this.shape.addShape(shape);
            }
            return new EnterInfo(address, org.getSendingRadius(), org.getArrivalInfo());
        }

		public double getMaximumRadius() {
			return maximumRadius;
		}

		public double getMinimumRadius() {
			return minimumRadius;
		}
        
        
        
    }

	public double getMinimumTransmissionRange() {
		return groups.getMinimumRadius();
	}

	public double getMaximumTransmissionRange() {
		return groups.getMaximumRadius();
	}

}
