package de.uni_trier.jane.service.routing.multicast.spbm;

import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.multicast.MulticastGroupID;
import de.uni_trier.jane.visualization.shapes.Shape;

import java.util.LinkedList;

/**
 * TODO: comment class
 * @author  daniel
 */

public final class PositionBasedMulticastHeader extends DefaultRoutingHeader {
	// use immutable data so that it is possible to return this if copy is
	// called!
	MulticastGroupID dest;

	LinkedList grids = new LinkedList();

	/**
	 * Constructor for class <code>PositionBasedMulticastHeader</code>
	 * 
	 * @param multicastGroupID
	 */
	public PositionBasedMulticastHeader(MulticastGroupID multicastGroupID) {
		super(null, null, true, false);
		dest = multicastGroupID;
	}

	/**
	 * Constructor for class <code>PositionBasedMulticastHeader</code>
	 * 
	 * @param header
	 */
	public PositionBasedMulticastHeader(PositionBasedMulticastHeader header) {
		super(header);
		grids = header.grids;
		dest = header.dest;
	}

	public PositionBasedMulticastHeader(RoutingHeader routingHeaderWithDelegationData) {
		super((DefaultRoutingHeader) routingHeaderWithDelegationData);
		PositionBasedMulticastDelegationData data = ((PositionBasedMulticastDelegationData) routingHeaderWithDelegationData
				.getDelegationData());
		grids = data.getGrids();
		dest = data.getDest();
	}

	/**
	 * @return  Returns the dest.
	 */
	public MulticastGroupID getDest() {
		return dest;
	}

	public int getCodingSize() {
		int size = 0;
		size += getDest().getCodingSize();
		size += getGrids().size() * (PositionBasedMulticastRoutingAlgorithmImplementation.griddepth * 2);
		return size;
	}

	/**
	 * @return  Returns the grids.
	 */
	public LinkedList getGrids() {
		return grids;
	}

	/**
	 * @param grids  The grids to set.
	 */
	public void setGrids(LinkedList l) {
		this.grids = l;
	}

	public LinkLayerInfo copy() {
		return new PositionBasedMulticastHeader(this);
	}

	public ServiceID getRoutingAlgorithmID() {
		return PositionBasedMulticastRoutingAlgorithmImplementation.SERVICE_ID;
	}

	public Shape getShape() {
		return null;
	}
}