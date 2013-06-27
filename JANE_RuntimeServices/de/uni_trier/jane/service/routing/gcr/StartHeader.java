package de.uni_trier.jane.service.routing.gcr;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;
import de.uni_trier.jane.service.routing.gcr.map.*;
import de.uni_trier.jane.service.routing.gcr.topology.*;
import de.uni_trier.jane.service.routing.positionbased.PositionbasedRoutingHeader;

public class StartHeader extends GeographicClusterRoutingHeader {

	public StartHeader(Address sourceAddress, Position sourcePosition, Address destinationAddress,
			Position destinationPosition) {
		super(sourceAddress, sourcePosition, destinationAddress, destinationPosition);
	}

	public StartHeader(GeographicClusterRoutingHeader other) {
		super(other);
	}

	/**
     * Constructor for class <code>StartHeader</code>
     *
     * @param positionbasedRoutingHeader
	 * @param greeedyFailurePosition 
	 * @param greedyFailureAddress 
     */
    public StartHeader(PositionbasedRoutingHeader positionbasedRoutingHeader,  Address greedyFailureAddress,Position greeedyFailurePosition) {
        super(positionbasedRoutingHeader,greedyFailureAddress,greeedyFailurePosition);
    }

    public void handle(RoutingTaskHandler handler, AbstractRoutingAlgorithm algorithm) {
		startForwarding(handler, (GeographicClusterRoutingAlgorithm)algorithm);
	}

	public LinkLayerInfo copy() {
		return new StartHeader(this);
	}

}
