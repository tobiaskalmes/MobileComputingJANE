package de.uni_trier.jane.service.routing.gcr;

import com.sun.org.apache.xpath.internal.operations.Gte;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.positionbased.PositionbasedRoutingHeader;

public abstract class PositionBasedRecoveryHeader extends DefaultPositionBasedHeader {

	private Address greedyFailureAddress;
	private Position greedyFailurePosition;
    protected transient boolean localGreedyFailure;
	
	public PositionBasedRecoveryHeader(Address sender, Address receiver, boolean countHops, boolean traceRoute, Position reciverPosition, Position senderPosition, Address greedyFailureAddress, Position greedyFailurePosition) {
		super(sender, receiver, countHops, traceRoute, reciverPosition, senderPosition);
		this.greedyFailureAddress = greedyFailureAddress;
		this.greedyFailurePosition = greedyFailurePosition;
	}

	public PositionBasedRecoveryHeader(PositionBasedRecoveryHeader other) {
		super(other);
		greedyFailureAddress = other.greedyFailureAddress;
		greedyFailurePosition = other.greedyFailurePosition;
        
	}

	/**
     * Constructor for class <code>PositionBasedRecoveryHeader</code>
     *
     * @param positionbasedRoutingHeader
     */
    public PositionBasedRecoveryHeader(PositionbasedRoutingHeader positionbasedRoutingHeader,Address greedyFailureAddress,Position greedyFailurePosition) {
        super(positionbasedRoutingHeader);
        localGreedyFailure=true;
        this.greedyFailureAddress = greedyFailureAddress;
        this.greedyFailurePosition = greedyFailurePosition;
    }

    public Address getGreedyFailureAddress() {
		return greedyFailureAddress;
	}

	public Position getGreedyFailurePosition() {
		return greedyFailurePosition;
	}

}
