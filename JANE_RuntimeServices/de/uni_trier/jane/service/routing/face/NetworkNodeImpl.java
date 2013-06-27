/*
 * Created on 04.10.2004
 *
 */
package de.uni_trier.jane.service.routing.face;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.service.planarizer.NetworkNode;

/**
 * A simple implementation of a NetworkNode
 * @author Stefan Peters
 *
 */
public class NetworkNodeImpl implements NetworkNode {
    
    private Address address;
    private Position position;
    private boolean oneHopNeighbor;

    
    /**
     * The constructor
     * @param address The address of the node
     * @param position The knwon position of the node
     * @param oneHopNeighbor Is the node a one hop neighbor
     */
    public NetworkNodeImpl(Address address, Position position,
            boolean oneHopNeighbor) {
        super();
        this.address = address;
        this.position = position;
        this.oneHopNeighbor = oneHopNeighbor;
    }
    
    
    /**
     * The constructor
     * @param sourceAddress The address of the node
     * @param position The knwon position of the node
     */
    public NetworkNodeImpl(Address sourceAddress, Position position) {
        super();
        this.address = sourceAddress;
        this.position = position;
    }
    
    /**
     * @param address The address of the node
     */
    public NetworkNodeImpl(Address address) {
        super();
        this.address = address;
    }
    /**
     * @param address The address to set.
     */
    public void setAddress(Address address) {
        this.address = address;
    }
    /**
     * @param oneHopNeighbor The oneHopNeighbor to set.
     */
    public void setOneHopNeighbor(boolean oneHopNeighbor) {
        this.oneHopNeighbor = oneHopNeighbor;
    }
    /**
     * @param position The position to set.
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    public Address getAddress() {
        return address;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isOneHopNeighbor() {
        return oneHopNeighbor;
    }

	public String toString() {
		return address.toString();
	}

}
