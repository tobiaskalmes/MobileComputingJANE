package de.uni_trier.jane.service.routing.dsr;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.locationManager.basetypes.GeographicLocation;
import de.uni_trier.jane.service.locationManager.basetypes.Location;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * A routing header for all messages within the DSR protocol
 * @author Alexander Hoehfeld
 */
public final class DSRRoutingHeader extends DefaultRoutingHeader implements LocationBasedRoutingHeader 
{
    private Location targetLocation;
    private boolean validRoute;
    private boolean protocoll;
    
    /**
     * Constructor for class <code>AdvancedDSRRoutingHeader</code>
     * @param receiver the reciever	
     * @param targetLocation the target (area)
     * @param route the route
     * @param promisc 
     */
    public DSRRoutingHeader(Address receiver, Location targetLocation, List route, boolean promisc,boolean protocoll) 
    {
        super(null, null, true, false);
        validRoute= !(route == null||route.isEmpty()); 
        if (route == null)
            route = new ArrayList();
        
        if (!route.contains(receiver) && receiver != null)
            route.add(receiver);
        
        setRoute            	(route);
        setTargetLocation   	(targetLocation);
        setPromiscousHeader( 	true);
        setPromiscousMessage(promisc);
        this. protocoll=protocoll;
    }

    /**
     * Constructor for class <code>AdvancedDSRRoutingHeader</code>
     * @param receiver the receiver
     * @param target the target
     */
    public DSRRoutingHeader(Address receiver, Location target)
    {
        this(receiver, target, null,false,false);
    }
    
    public DSRRoutingHeader(Address receiver, GeographicLocation geoLocation, DefaultRoutingHeader otherRoutingHeader) {
		super(otherRoutingHeader);
		
		if (!otherRoutingHeader.hasHopCount()){
			super.setHopCount(0);
		}
        if (route == null)
            route = new ArrayList();
        
        if (!route.contains(receiver) && receiver != null)
            route.add(receiver);
        
        setRoute            	(route);
        setTargetLocation   	(geoLocation);
        setPromiscousHeader 	(true);
		
	}

    /**
     * Copy constructor for class <code>AdvancedDSRRoutingHeader</code>
     * @param header the original header
     */
    public DSRRoutingHeader(DSRRoutingHeader header) 
    {
        super(header);
        validRoute = header.validRoute;
        protocoll  = header.protocoll;
        setTargetLocation(header.targetLocation);
    }
    
    /**
     * @return Returns the protocol.
     */
    public boolean isProtocolMessage() 
    {
        return protocoll;
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.RoutingHeader#hasRoute()
     */
    public boolean hasRoute() 
    {
    	return route.size() > 1;
    }

	/**
     * Returns whether the header has a target location or not
     * @return <code>true</code> if the header has a target location
     */
    public boolean hasLocation()
    {
        return targetLocation != null;
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.LocationBasedRoutingHeader#getTargetLocation()
     */
    public Location getTargetLocation() 
    {
        return targetLocation;
    }

    /**
     * Sets the target location
     * @param location the target location
     */
    public void setTargetLocation(Location targetLocation) 
    {
        this.targetLocation = targetLocation;
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.DefaultRoutingHeader#addHop(de.uni_trier.jane.basetypes.Address)
     */
    public void addHop(Address address) 
    {
        // ignore!
    }
    
    /**
     * Sets the route of this header
     * @param route the route
     */
    public void setRoute(List route) 
    {
        this.route = route;
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.RoutingHeader#hasReceiver()
     */
    public boolean hasReceiver() 
    {
        return route != null && route.size() >= 1;
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.RoutingHeader#getReceiver()
     */
    public Address getReceiver()
    {
        if (!hasReceiver()) throw new IllegalAccessError("This header does not contain receiver information");
        return (Address)route.get(route.size()-1);
    }
    
    /**
     * Checks whether a next hop is available
     * @return <code>true</code> if a next hop is available
     */
    public boolean hasNextHop()
    {
        return getHopCount() < route.size()-1;
    }
    
    /**
     * Returns the next hop
     * @return the next hop
     */
    public Address getNextHop()
    {
        return (Address)route.get(getHopCount()+1);
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.DefaultRoutingHeader#copy()
     */
    public LinkLayerInfo copy() 
    {
        return new DSRRoutingHeader(this);
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.DefaultRoutingHeader#getCodingSize()
     */
    public int getCodingSize() 
    {        
        return targetLocation.getCodingSize()+((Address)route.get(0)).getCodingSize()*route.size()+4*8;
    }      //   sizeof target            size of one hop                           # hops           for the list                                                                      

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.DefaultRoutingHeader#getShape()
     */
    public Shape getShape() 
    {
        return new EllipseShape(new Extent(3,3), Color.RED,true);
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.RoutingHeader#getRoutingAlgorithmID()
     */
    public ServiceID getRoutingAlgorithmID() 
    {
        return DSRService.SERVICE_ID;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "DSRRoutingHeader (route="+route+")";
    }

    /**
     * Sets the route invalid 
     */
    public void invalidRoute() 
    {
        validRoute = false;
    }

    /**
     * Checks if the route is valid 
     * @return
     */
    public boolean hasValidRoute() 
    {
        return validRoute;
    }
}