package de.uni_trier.jane.service.routing;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.positionbased.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

public abstract class DefaultPositionBasedHeader extends DefaultRoutingHeader implements PositionbasedRoutingHeader {

	private transient static final Shape SHAPE = new EllipseShape(new Extent(20, 20), Color.ORANGE, true);

    public DefaultPositionBasedHeader(Address sender, Address receiver, boolean countHops, boolean traceRoute) {
    	this(sender, receiver, countHops, traceRoute, null, null);
	}

    public DefaultPositionBasedHeader(Address sender, Address receiver, boolean countHops, boolean traceRoute, Position reciverPosition, Position senderPosition) {
		super(sender, receiver, countHops, traceRoute);
		this.receiverPosition = reciverPosition;
		this.sourcePosition = senderPosition;
    }

    public DefaultPositionBasedHeader(DefaultPositionBasedHeader other) {
		super(other);
		this.receiverPosition = other.receiverPosition;
    }

	/**
     * Constructor for class <code>DefaultPositionBasedHeader</code>
     *
     * @param positionbasedRoutingHeader
     */
    public DefaultPositionBasedHeader(PositionbasedRoutingHeader positionbasedRoutingHeader) {
        super((DefaultRoutingHeader) positionbasedRoutingHeader);
        
        this.receiverPosition = positionbasedRoutingHeader.getReceiverPosition();
        if (positionbasedRoutingHeader.hasSenderPosition()){
            sourcePosition=positionbasedRoutingHeader.getSenderPosition();
        }
    }

    /**
     * Constructor for class <code>DefaultPositionBasedHeader</code>
     *
     * @param routingHeader
     * @param target
     */
    public DefaultPositionBasedHeader(RoutingHeader routingHeader, Position target) {
        super((DefaultRoutingHeader)routingHeader);
    }

    public int getCodingSize() {
		// TODO was soll die methode getSize() ???
		return 32;
//		return getSize() + (receiverPosition == null ? 0 : 3 * 32);
	}

	public Shape getShape() {
		return SHAPE;
	}
	
	public abstract void handle(RoutingTaskHandler handler, AbstractRoutingAlgorithm algorithm);

}
