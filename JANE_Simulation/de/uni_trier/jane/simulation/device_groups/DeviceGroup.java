/*
 * Created on 01.06.2005
 * File: ServiceDeviceConfiguration.java
 */
package de.uni_trier.jane.simulation.device_groups;

import java.util.ArrayList;

import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.service.unit.ServiceFactory;
import de.uni_trier.jane.simulation.*;
import de.uni_trier.jane.simulation.dynamic.mobility_source.MobilitySource;
import de.uni_trier.jane.visualization.shapes.Shape;

/**
 * The <code>DeviceGroup</code> class combines several devices with similar
 * properties. This class is used in combination with the 
 * <code>DeviceGroupsMobilitySource</code> class that allows to start different
 * device groups. Each <code>DeviceGroup</code> has a mobility source to specify
 * the mobility of the devices in the group and implements the
 * <code>ServiceFactory</code> interface to specify the services to start on the
 * devices. If the mobility source is a <code>ClickAndPlayMobilitySource</code>
 * you can add some positions to the device group, so to each position one device
 * is moved.
 * The user extends this class to define his own device groups. He must implement
 * the <code>initServices</code> method to specify the services to start and the
 * <code>initMobilitySource</code> method to specify the mobility source. Also he
 * must provide a constructor with the following argument list:
 * <ul>
 * <li><code>SimulationParameters parameters</code>: the parameters for the simulation</li>
 * <li><code>int numberOfDevices</code>: the number of devices in this group</li>
 * </ul>
 * Normally the user will only call the super constructor with the same argument
 * list.
 * 
 * <p>
 * Example:
 * <pre>
 * public class MyDeviceGroup extends DeviceGroup {
 *     public MyDeviceGroup(SimulationParameters parameters, int numberOfDevices) {
 *         super(parameters, numberOfDevices);
 *     }
 *     
 *     public MobilitySource initMobilitySource(Simulation parameters, int numberOfDevices) {
 *         // create the mobility source
 *         MobilitySource mobilitySource = ...
 *         
 *         return mobilitySource;
 *     }
 *     
 *     public void initServices(ServiceUnit serviceUnit) {
 *         // add the services to start initially on the devices in this group
 *         // to the service unit
 *         // Note: You also can change the default visualization shape for the
 *         // devices here.
 *         serviceUnit.setDefaultShape(new RelativeEllipseShape(...));
 *     }
 * }
 * </pre>
 * </p>
 * 
 * @author christian.hoff
 * @version 01.06.2005
 */
public abstract class DeviceGroup implements ServiceFactory {
    MobilitySource mobilitySource;
    ArrayList positions;
    private int numberOfDevices;
   
    /**
     * This constructor must be called from derived classes. Normally this is the
     * only thing to do in the constructor of the derived class.
     * 
     * @param parameters The simulation parameters.
     * @param numberOfDevices The number of devices in this device group.
     */
    public DeviceGroup(SimulationParameters parameters, int numberOfDevices) {
    	//this.mobilitySource = initMobilitySource(parameters, numberOfDevices);
        this.numberOfDevices=numberOfDevices;
        this.positions = new ArrayList();
    }
    
    /**
     * Implement this method to specify the mobility source used in the
     * device group. This method is called implicitly to instantiate the mobility
     * source of the device group.
     * 
     * @param parameters The simulation parameters.
     * @param numberOfDevices The number of devices in this device group.
     * @return The mobility source to use in this device group.
     */
    public abstract MobilitySource initMobilitySource(SimulationParameters parameters, int numberOfDevices);     
    
    /**
     * Override this method to specify a shape object for the devices in the 
     * device group.
     * 
     * @param device The <code>DeviceID</code> of the device to get a shape for.
     * @return The shape for the specified device.
     */
    public Shape getShape(DeviceID device) {
        return null;
    }

    /**
     * Adds a new position to the device group. This ensures that a device is
     * placed at this position if a <code>ClickAndPlayMobilitySource</code> is
     * used for this device group.
     * 
     * @param position The position to add.
     */
    public void addPosition(Position position) {
    	positions.add(position);
    }
    
    /**
     * Gets the position at the specified index.
     * @param index The index of the position.
     * @return The position at the specified index.
     */
    public Position getPosition(int index) {
    	return (Position) positions.get(index);
    }
  
    /**
     * @return The number of positions added to the device group.
     */
    public int getPositionCount() {
    	return positions.size();
    }
    
    /**
     * @return Returns the mobilitySource.
     */
    public MobilitySource getMobilitySource() {
        return mobilitySource;
    }
	/**
	 * @param mobilitySource The mobilitySource to set.
	 */
	protected void setMobilitySource(MobilitySource mobilitySource) {
		this.mobilitySource = mobilitySource;
	}
    /**
     * @return Returns the number of devices in this <code>DeviceGroup</code>.
     */
    public int getTotalDeviceCount() {
        return mobilitySource.getTotalDeviceCount();
    }

    /**
     * Intializes the deviceGroup 
     * @param parameters    the simulation parameters
     */
    public void init(SimulationParameters parameters) {
        mobilitySource=initMobilitySource(parameters,numberOfDevices);
        
    }
    
    public double getMinimumTransmissionRange() {
    	return mobilitySource.getMinimumTransmissionRange();
    }

    public double getMaximumTransmissionRange() {
    	return mobilitySource.getMaximumTransmissionRange();
    }

}
