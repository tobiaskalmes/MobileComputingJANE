package de.uni_trier.jane.service.routing.dsr;

import de.uni_trier.jane.service.ServiceTimeout;
import de.uni_trier.jane.service.routing.greedy.PendingMessageEntry;

import java.util.*;

/**
 * An entry for route discovery attempts inlcuding timeouts
 * @author daniel
 **/

final class RouteDiscoveryEntry 
{
	/**
	 * The timeout of the entry
	 */
    private ServiceTimeout timeout;
    
    /**
     * TODO comment me!
     */
    private HashSet set;

    /**
     * Constructor for class <code>RouteDiscoveryEntry</code>
     *
     * @param timeout
     */
    public RouteDiscoveryEntry(ServiceTimeout timeout) 
    {
        this.timeout = timeout;
        this.set	 = new HashSet();
    }

    /**
     * Adds a new (pending) message entry to the internal set 
     * @param entry
     */
    public void add(PendingMessageEntry entry) 
    {
        set.add(entry);
    }

    /**
     * Returns an interator for the peding message entries 
     * @return
     */
    public Iterator iterator() 
    {
        return set.iterator();
    }

    /**
     * Removes the specifed entry from the interanl set
     * @param entry
     */
    public void remove(RouteDiscoveryEntry entry) 
    {
        set.remove(entry);
    }

    /**
     * Returns if the entry set is empty 
     * @return
     */
    public boolean isEmpty() 
    {
        return set.isEmpty();
    }

    /**
     * Returns the timeout
     * @return Returns the timeout.
     */
    public ServiceTimeout getTimeout() 
    {
        return timeout;
    }
    
    /**
     * Sets the timeout
     * @param the timeout
     */
    public void setTimeout(ServiceTimeout timeout) 
    {
        this.timeout=timeout;
    }
}