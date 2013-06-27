package de.uni_trier.jane.service.planarizer.rdg.delaunay;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.planarizer.*;

public class NetworkNodePoint extends Pnt {

    private NetworkNode node;
    
    public NetworkNodePoint(NetworkNode node) {
    	super(node.getPosition().getX(), node.getPosition().getY());
    	this.node = node;
    }

    public NetworkNodePoint(Position position) {
    	super(position.getX(), position.getY());
    	node = null;
    }

	public NetworkNode getNode() {
		return node;
	}

}
