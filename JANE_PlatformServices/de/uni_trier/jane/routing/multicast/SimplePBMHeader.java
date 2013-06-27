package de.uni_trier.jane.routing.multicast;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.service.routing.DefaultRoutingHeader;
import de.uni_trier.jane.service.routing.RoutingHeader;
import de.uni_trier.jane.service.routing.multicast.MulticastGroupID;
import de.uni_trier.jane.visualization.shapes.Shape;

public class SimplePBMHeader extends DefaultRoutingHeader implements
		RoutingHeader {

	private transient MulticastGroupID multicastGroupID;
	
	public SimplePBMHeader(Address sender, Address receiver, boolean countHops, boolean traceRoute) {
		super(sender, receiver, countHops, traceRoute);
		// TODO Auto-generated constructor stub
	}

	public SimplePBMHeader(DefaultRoutingHeader header) {
		super(header);
	}

	public SimplePBMHeader() {
		super(null,null,false,false);
	}

	public LinkLayerInfo copy() {
		return new SimplePBMHeader(this);
	}

	public int getCodingSize() {
		// TODO Auto-generated method stub
		return 0;
	}
	public MulticastGroupID getMulticastGroupID() {
		return multicastGroupID;
	}
	public void setMulticastGroupID(MulticastGroupID multicastGroupID) {
		this.multicastGroupID = multicastGroupID;
	}
	
	
	public Shape getShape() {
		// TODO Auto-generated method stub
		return null;
	}

	public ServiceID getRoutingAlgorithmID() {
		return PositionBasedMulticastModule.SERVICE_ID;
	}

}
