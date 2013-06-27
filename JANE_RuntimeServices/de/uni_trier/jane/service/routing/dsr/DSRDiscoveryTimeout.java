package de.uni_trier.jane.service.routing.dsr;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.service.ServiceTimeout;

/**
 * A wrapper object for DSR discovery timeouts
 * @author Alexander Höhfeld
 */
public class DSRDiscoveryTimeout extends ServiceTimeout
{
	/**
	 * The <code>address</code> of the destination
	 */
    private final Address destination;
    
    /**
     * The <code>DSRService</code>
     */
    private DSRService dsrService;
    
    /**
     * Constructor
     * @param sendBuffer the send buffer
     * @param destination the destination
     * @param routingHeader the routing header
     * @param delta the time delta allowed
     */
    public DSRDiscoveryTimeout(DSRService dsrService, Address destination,  double delta)
    {
        super(delta);
        this.dsrService	 = dsrService;
        this.destination = destination;
    }

    /**
     * Handle for the timeout
     */
    public void handle() 
    {
        dsrService.targetFailure(destination);
    }
}
