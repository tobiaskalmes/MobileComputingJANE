/*
 * Created on Nov 19, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.uni_trier.jane.simulation.global_knowledge;

import java.util.*;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.AddressDeviceMapping;
import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.basetypes.DeviceIDIterator;
import de.uni_trier.jane.basetypes.DeviceIDSet;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.basetypes.Trajectory;
import de.uni_trier.jane.basetypes.TrajectoryMapping;
import de.uni_trier.jane.simulation.device.DeviceManager;
import de.uni_trier.jane.visualization.shapes.Shape;

/**
 * @author daniel
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class GlobalKnowledgeImplementation implements GlobalKnowledge {
	
	private AddressDeviceMapping addressDeviceMapping;
	
    private DeviceManager deviceManager;
    private Set linkListeners;
    private Set deviceListeners;
    private double minimumTransmissionRadius;
    private double maximumTransmissionRadius;
	
    /**
     * 
     * Constructor for class <code>GlobalKnowledgeImplementation</code>
     * @param deviceManager
     * @param minimumTransmissionRadius
     * @param maximumTransmissionRadius
     */
    public GlobalKnowledgeImplementation(DeviceManager deviceManager, double minimumTransmissionRadius, double maximumTransmissionRadius, AddressDeviceMapping addressDeviceMapping) {
    	this.addressDeviceMapping = addressDeviceMapping;
        this.deviceManager=deviceManager;
        linkListeners=new LinkedHashSet();
        deviceListeners=new LinkedHashSet();
        this.minimumTransmissionRadius=minimumTransmissionRadius;
        this.maximumTransmissionRadius=maximumTransmissionRadius;
        
        
    }
  
	/**
	 * Get the addresses of all devices in this network.
	 * @return an iterator over the addresses of all devices in this network
	 */
	public DeviceIDSet getNodes(){
	    return deviceManager.getNodes();
	}

	/**
	 * Test if there is a connection from sender to receiver.
	 * @param sender the sender address
	 * @param receiver the receiver address
	 * @return true <=> there is a connection
	 */
	public boolean isConnected(DeviceID sender, DeviceID receiver){
		return deviceManager.getMobileDevice(sender).getNeighbours().contains(receiver);
	}

	/**
	 * Get the connected devices of a sender.
	 * @param sender the address of the sender
	 * @return an iterator over the addresses of all connected devices
	 */
	public DeviceIDIterator getConnected(DeviceID deviceID){
	    return deviceManager.getMobileDevice(deviceID).getNeighbours().iterator();
	    
	}

	/**
	 * Get the connected devices of a sender.
	 * @param sender the address of the sender
	 * @return the setof all connected devices
	 */
	public DeviceIDSet getConnectedSet(DeviceID sender){
	    return deviceManager.getMobileDevice(sender).getNeighbours();
		
	}

	/**
	 * Returns the current trajectory of the device.
	 * @param address the address of the device
	 * @return the trajectory
	 */
	public Trajectory getTrajectory(DeviceID deviceID){
	    return deviceManager.getMobileDevice(deviceID).getTrajectory();
		
	}

	// christian.hiedels @ 08.07.2005
    /**
     * Returns true if a given device runs a given service
     */
	public boolean hasServiceID(DeviceID deviceID, ServiceID serviceID) {
		return deviceManager.getMobileDevice(deviceID).getServiceManager().hasService(serviceID);
	}
	// christian.hiedels @ 08.07.2005

	/**
	 * Returns the current sending radius of a device.
	 * @param address the device address
	 * @return the sending radius
	 */
	public double getSendingRadius(DeviceID deviceID){
		return deviceManager.getMobileDevice(deviceID).getSendingRadius();
	}

	/**
	 * @param sender
	 * @param receiver
	 */
	public void handleAttach(DeviceID sender, DeviceID receiver) {
		Iterator iterator=linkListeners.iterator();
		while (iterator.hasNext()){
			((LinkListener)iterator.next()).handleAttach(sender,receiver);
		}
		
		
	}

	/**
	 * @param sender
	 * @param receiver
	 */
	public void handleDetach(DeviceID sender, DeviceID receiver ) {
		Iterator iterator=linkListeners.iterator();
		while (iterator.hasNext()){
			((LinkListener)iterator.next()).handleDetach(sender,receiver);
		}
		
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.ubi.appsim.kernel.device.GlobalKnowledge#addLinkListener(de.uni_trier.ubi.appsim.kernel.device.LinkListenerSim)
	 */
	public void addLinkListener(LinkListener linkListener) {
		linkListeners.add(linkListener);
		
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.ubi.appsim.kernel.device.GlobalKnowledge#removeLinkListener(de.uni_trier.ubi.appsim.kernel.device.LinkListenerSim)
	 */
	public void removeLinkListener(LinkListener linkListener) {
		linkListeners.remove(linkListener);
		
	}

	/**
	 * @param address
	 * @param trajectoryMapping
	 * @param suspended
	 */
	public void changeTrack(DeviceID address, TrajectoryMapping trajectoryMapping, boolean suspended) {
		Iterator iterator=deviceListeners.iterator();
		while(iterator.hasNext()){
			((DeviceListener)iterator.next()).changeTrack(address, trajectoryMapping, suspended) ;
		}
		
	}


	/**
	 * @param address
	 */
	public void notifyEnter(DeviceID address){
		Iterator iterator=deviceListeners.iterator();
		while(iterator.hasNext()){
			((DeviceListener)iterator.next()).enter(address);
		}
	}

	/**
	 * @param address
	 */
	public void notifyExit(DeviceID address) {
		Iterator iterator=deviceListeners.iterator();
		while(iterator.hasNext()){
			((DeviceListener)iterator.next()).exit(address);
		}
		
	}
	
	public void addDeviceListener(DeviceListener deviceListener){
		deviceListeners.add(deviceListener);
		
	}
	public void removeDeviceListener(DeviceListener deviceListener){
		deviceListeners.remove(deviceListener);
	}

	


    public double getMinimumTransmissionRadius() {
        return minimumTransmissionRadius;
    }

    public double getMaximumTransmissionRadius() {
        return maximumTransmissionRadius;
    }

    /**
     * @return
     */
    public Shape getShape() {
        // TODO Auto-generated method stub
        return null;
    }

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.simulation.global_knowledge.GlobalKnowledge#getDeviceID(de.uni_trier.jane.basetypes.Address)
	 */
	public DeviceID getDeviceID(Address address) {
        DeviceID deviceID=addressDeviceMapping.getDeviceID(address);
		return deviceID;
	}

}
