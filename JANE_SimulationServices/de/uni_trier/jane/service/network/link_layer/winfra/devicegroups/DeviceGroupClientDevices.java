/*
 * Created on 28.06.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.network.link_layer.winfra.devicegroups;

import de.uni_trier.jane.simulation.SimulationParameters;
import de.uni_trier.jane.simulation.device_groups.DeviceGroup;

/**
 * @author christian.hiedels
 *
 * This Class represents a DeviceGroup of Client Devices. Client Devices have a ClickAndPlayMobilitySource as they
 * can be clicked and moved around the Plane. Furthermore on each Client a ClientService is started.
 */
public abstract class DeviceGroupClientDevices extends DeviceGroup {
	
	public DeviceGroupClientDevices( SimulationParameters parameters, int numberOfDevices ) {
		super( parameters, numberOfDevices );
	}
	
//	public abstract MobilitySource initMobilitySource(SimulationParameters parameters, int numberOfDevices);
//
//	public abstract void initServices( ServiceUnit serviceUnit );
}