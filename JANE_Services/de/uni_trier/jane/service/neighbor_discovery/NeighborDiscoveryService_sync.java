package de.uni_trier.jane.service.neighbor_discovery;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.beaconing.*;
import de.uni_trier.jane.service.network.link_layer.*;


/**
 * A discovery service determines information about nearby devices. The information
 * provided to this service will be disseminated to all nearby devices. When new information
 * is available, all listeners for this event have to be notified.
 * @see de.uni_trier.jane.service.neighbor_discovery.NeighborDiscoveryListener
 * @author Hannes Frey
 */
public interface NeighborDiscoveryService_sync extends NeighborDiscoveryService {

	public NeighborDiscoveryProperties getNeighborDiscoveryProperties();

	public Address getOwnAddress();
	
	/**
	 * This method returns all gateway nodes regarding the given destination.
	 * For a destination node which is an i-hop neighbor, this method returns
	 * those 1-hop neighbors which can reach the destination in i-1 hops. A special
	 * case arises for 1-hop neigbors. In this case the method returns the given
	 * destination node. For a 0-hop neighbor (the current node itself) the method
	 * returns the passed argument as well. If the given destination is not known,
	 * the method will return null.
	 * @param destination the destination node
	 * @return an array containing the addresses of all gateway nodes
	 */
	public Address[] getGatewayNodes(Address destination);
	
	/**
	 * Get all neighbor nodes this passed node is a gateway for.
	 * @param gateway the node
	 * @return the neighbor nodes
	 */
	public Address[] getNeighborNodes(Address gateway);
	
	/**
     * Get all neighbors known at the moment.
     * @return the addresses of all neighbors
     */
    public Address[] getNeighbors();
    
	/**
     * Get all neighbors matching the filter.
     * @param filter the neighbor has to match this filter
     * @return the addresses of all matching neighbors
     */
    public Address[] getNeighbors(NeighborDiscoveryFilter filter);
    
    public int countNeighbors();

    public int countNeighbors(NeighborDiscoveryFilter filter);

    /**
     * Get information about all device which are known at the moment.
     * @return the information about known nodes
     */
    public NeighborDiscoveryData[] getNeighborDiscoveryData();

    public NeighborDiscoveryData[] getNeighborDiscoveryData(NeighborDiscoveryFilter filter);

	/**
     * Check whether there information for the given device currently available.
     * @param address the device address
     * @return true if there is device information available
     */
    public boolean hasNeighborDiscoveryData(Address address);

    public boolean hasNeighborDiscoveryData(Address address, NeighborDiscoveryFilter filter);

    /**
     * Get the stored information about the device. If there is no information stored
     * at the moment the method will return null.
     * @param address the address of the device
     * @return the information stored for the given device
     */
    public NeighborDiscoveryData getNeighborDiscoveryData(Address address);

    /**
     * TODO Comment method
     * @param address
     * @param dataID
     */
    public Data getData(Address address, DataID dataID);
    
    public boolean hasData(Address address, DataID dataID);

}
