package de.uni_trier.jane.service.traffic.anycast;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.locationManager.basetypes.*;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.positionbased.*;
import de.uni_trier.jane.visualization.shapes.*;
/**
 * 
 * @author Stefan Peters
 *
 */
public class DefaultLocationBasedHeader extends DefaultRoutingHeader implements LocationBasedRoutingHeader,PositionbasedRoutingHeader{

	private static final long serialVersionUID = -2173230682045033944L;
	private GeographicLocation location;
	private ServiceID routingAlgorithmID;
	
	public DefaultLocationBasedHeader(ServiceID routingAlgorithmID,Address sender, GeographicLocation location, boolean countHops, boolean traceRoute) {
		super(sender, null, countHops, traceRoute);
		this.location=location;
		this.routingAlgorithmID=routingAlgorithmID;
	}

	public DefaultLocationBasedHeader(DefaultRoutingHeader header) {
		super(header);
		this.location=(GeographicLocation) ((DefaultLocationBasedHeader)header).getTargetLocation();
		this.routingAlgorithmID=((DefaultLocationBasedHeader)header).getRoutingAlgorithmID();
	}

	public LinkLayerInfo copy() {
		return null;
	}

	public int getCodingSize() {
		return 0;
	}

	public Shape getShape() {
		return null;
	}

	public ServiceID getRoutingAlgorithmID() {
		return routingAlgorithmID;
	}

	public Location getTargetLocation() {
		return location;
	}
	
	
	
}
