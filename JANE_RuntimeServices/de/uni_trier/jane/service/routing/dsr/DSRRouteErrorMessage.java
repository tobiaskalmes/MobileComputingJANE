package de.uni_trier.jane.service.routing.dsr;

import java.util.List;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.Dispatchable;
import de.uni_trier.jane.basetypes.Rectangle;
import de.uni_trier.jane.service.routing.RoutingData;
import de.uni_trier.jane.service.routing.RoutingHeader;
import de.uni_trier.jane.signaling.SignalListener;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.Shape;
import de.uni_trier.jane.visualization.shapes.TextShape;

/**
 * An object wrapping a DSR error message
 * @author Alexander Höhfeld
 */
public class DSRRouteErrorMessage implements RoutingData
{
	private static final Shape SHAPE =  new TextShape("RE",new Rectangle(0,0,4,4), Color.RED);
    private List brokenLink;
	
    /**
     * Constructor for class <code>AdvancedRouteErrorMessage</code>
     * @param brokenLink the broken link
     */
    public DSRRouteErrorMessage(List brokenLink) 
    {
        this.brokenLink = brokenLink;
    }
    
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.routing.RoutingData#handle(de.uni_trier.jane.service.routing.RoutingHeader, de.uni_trier.jane.signaling.SignalListener)
	 */
	public void handle(RoutingHeader routingHeader, SignalListener signalListener)
	{
        ((DSRService)signalListener).handleRouteError(brokenLink);
	}
	
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.basetypes.Dispatchable#copy()
	 */
	public Dispatchable copy()
	{
		return this;
	}
	
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.basetypes.Dispatchable#getReceiverServiceClass()
	 */
	public Class getReceiverServiceClass()
	{
		return DSRService.class;
	}
	
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.basetypes.Sendable#getSize()
	 */
	public int getSize()
	{
		return ((Address)brokenLink.get(0)).getCodingSize()*brokenLink.size()+4*8;
	}
	
	/* (non-Javadoc)
	 * @see de.uni_trier.jane.basetypes.Sendable#getShape()
	 */
	public Shape getShape()
	{
		return SHAPE;
	}
}
