package de.uni_trier.jane.service;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.signaling.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This interface describes a service which can be run concurrently with other services.
 * 
 */
public interface Service extends SignalListener{

    /**
     * Get the unique identifier describing this service.
     * @return the unique identifier
     */
    public ServiceID getServiceID();
    
	/**
	 * Start the service on this device.
	 */
//	public void start();

	/**
	 * Called when the device exits the simulation.
	 */
	public void finish();



	/**
	 * Get the global shape of this object. This shape is visualized in absolute simulation
	 * coordinates. You may return null if no shape has to be visualized.
	 * @return the global shape
	 */
	public Shape getShape();
	
	// TODO: evtl. in getProperties und Properties umbenennen
	public void getParameters(Parameters parameters);

		
}
