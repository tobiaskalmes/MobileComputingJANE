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
 * This Class represents a DeviceGroup of Base Station Devices. They have a BaseStationMobilitySource which does
 * not allow them to be moved around the Plane. Furthermore a BaseStationService is started on each Base Station.
 */
public abstract class DeviceGroupBaseStationDevices extends DeviceGroup {

	public DeviceGroupBaseStationDevices( SimulationParameters parameters, int numberOfDevices ) {
        super(parameters, numberOfDevices);
	}
	
//	public abstract MobilitySource initMobilitySource( SimulationParameters parameters, int numberOfDevices );
//
//	public abstract void initServices( ServiceUnit serviceUnit );
}

