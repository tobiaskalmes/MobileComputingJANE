package de.uni_trier.jane.service.location_service;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.basetypes.ListenerID;
import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.basetypes.Trajectory;
import de.uni_trier.jane.service.EndpointClassID;
import de.uni_trier.jane.service.location_directory.LocationDirectoryEntry;
import de.uni_trier.jane.service.location_directory.LocationDirectoryEntryReplyHandler;
import de.uni_trier.jane.service.location_directory.LocationDirectoryService;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.simulation.global_knowledge.GlobalKnowledge;
import de.uni_trier.jane.simulation.service.SimulationOperatingSystem;
import de.uni_trier.jane.simulation.service.SimulationService;
import de.uni_trier.jane.visualization.shapes.Shape;

/**
 * This is a simple implementation of a location service using simulation
 * global knowledge in order to provide the most current location information
 * about the device.
 */
public class SimulationLocationDirectoryService implements SimulationService, LocationDirectoryService {

	private static final ServiceID SERVICE_ID =
		new EndpointClassID(SimulationLocationDirectoryService.class.getName());

    private SimulationOperatingSystem simulationOperatingSystem;
    private GlobalKnowledge globalKnowledge;

    public static void createInstance(ServiceUnit serviceUnit) {
    	serviceUnit.addService(new SimulationLocationDirectoryService());
    }

	public ServiceID getServiceID() {
		return SERVICE_ID;
	}

	public void start(SimulationOperatingSystem operatingService) {
        this.simulationOperatingSystem = operatingService;
        globalKnowledge = operatingService.getGlobalKnowledge();
        operatingService.registerSignalListener(LocationDirectoryService.class);
    }

    public void finish() {
        // ignore
    }

    public Shape getShape() {
        return null;
    }

    public void requestLocationDirectoryEntry(Address address, ListenerID listener) {
        requestLocationDirectoryEntry(address, listener, Double.POSITIVE_INFINITY);
    }
    
    public void requestLocationDirectoryEntry(Address address, ListenerID listener, double timeout) {
    	
        /* Note: We ignore the timeout value. It is irrelevant for this special 
         * implementation, for there will always be an "instant" response.
         */
    	
    	// determine the location of the requested device by using the global knowledge
    	DeviceID deviceID = globalKnowledge.getDeviceID(address);
        Trajectory trajectory = globalKnowledge.getTrajectory(deviceID);
        Position position = trajectory.getPosition();
        LocationDirectoryEntry info = new LocationDirectoryEntry(address, position);

        // reply location information
        simulationOperatingSystem.sendSignal(listener,
                new LocationDirectoryEntryReplyHandler.Reply(info));
    }

	public void getParameters(Parameters parameters) {
		// ignore
	}

}
