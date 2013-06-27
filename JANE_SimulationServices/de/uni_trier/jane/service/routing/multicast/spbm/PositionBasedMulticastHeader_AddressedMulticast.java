package de.uni_trier.jane.service.routing.multicast.spbm;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.locationManager.basetypes.Location;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.multicast.MulticastGroupID;
import de.uni_trier.jane.visualization.shapes.Shape;

import java.util.*;
import java.util.Map.Entry;

/**
 * TODO: comment class
 * @author  daniel
 */

public final class PositionBasedMulticastHeader_AddressedMulticast extends DefaultRoutingHeader 
    implements LocationBasedRoutingHeader//Falsch, aber Face delegiert so :(
    {
	// use immutable data so that it is possible to return this if copy is
	// called!
	MulticastGroupID dest;

	//LinkedList<Grid> grids = new LinkedList<Grid>();
    HashMap gridMap= new HashMap(); 
    //can be optimized
	/**
	 * Constructor for class <code>PositionBasedMulticastHeader</code>
	 * 
	 * @param multicastGroupID
	 */
	public PositionBasedMulticastHeader_AddressedMulticast(MulticastGroupID multicastGroupID) {
		super(null, null, true, false);
		dest = multicastGroupID;
	}

	/**
	 * Constructor for class <code>PositionBasedMulticastHeader</code>
	 * 
	 * @param header
	 */
	public PositionBasedMulticastHeader_AddressedMulticast(PositionBasedMulticastHeader_AddressedMulticast header) {
		super(header);
		gridMap = new HashMap(header.gridMap);
		dest = header.dest;
	}

	public PositionBasedMulticastHeader_AddressedMulticast(RoutingHeader routingHeaderWithDelegationData) {
		super((DefaultRoutingHeader) routingHeaderWithDelegationData);
		
        PositionBasedMulticastDelegationData_AddressedMulticast data=  ((PositionBasedMulticastDelegationData_AddressedMulticast) routingHeaderWithDelegationData
				.getDelegationData());
		gridMap.put(routingHeaderWithDelegationData.getLinkLayerInfo().getReceiver(),data.getGrids());;
        dest = data.getDest();
	}

	/**
	 * @return  Returns the dest.
	 */
	public MulticastGroupID getDest() {
		return dest;
	}
	
	// Adrian, 20.10.2006, java 1.3
//	public int getCodingSize() {
//        int size = 0;
//        size += getDest().getCodingSize();
//        Iterator<Entry<Address, LinkedList<Grid>>> iterator = gridMap.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Entry<Address, LinkedList<Grid>> element = iterator.next();
//            size+=element.getKey().getCodingSize();
//            size+=element.getValue().size() * ((PositionBasedMulticastRoutingAlgorithmImplementation.griddepth * 2) + 8);   
//        } 
//        return size;
//	}

	public int getCodingSize() 
	{
	  int size = 0;
	  size += getDest().getCodingSize();
	  Iterator iterator = gridMap.entrySet().iterator();
	  while (iterator.hasNext()) {
	      Entry element = (Entry) iterator.next();
	      size += ((Address) element.getKey()).getCodingSize();
	      size += ((LinkedList) element.getValue()).size() * ((PositionBasedMulticastRoutingAlgorithmImplementation.griddepth * 2) + 8);   
	  } 
	  return size;
	}
	
	/**
	 * @return  Returns the grids.
	 */
	public LinkedList getGrids(Address a) {
		return (LinkedList) gridMap.get(a);
	}

	/**
	 * @param grids  The grids to set.
	 */
	public void setGrids(LinkedList l, Address a) {
		gridMap.put(a,l);
	}
    
    public void clearGrids(){
        gridMap.clear();
    }

	public LinkLayerInfo copy() {
		return new PositionBasedMulticastHeader_AddressedMulticast(this);
	}

	public ServiceID getRoutingAlgorithmID() {
		return PositionBasedMulticastRoutingAlgorithm_AddressedMulticast.SERVICE_ID;
	}

	public Shape getShape() {
		return null;
	}

    public HashMap getGridMap() {
        return gridMap;
    }

    //
    public Location getTargetLocation() {
        throw new IllegalAccessError("this header does not provide location information!");
        
    }
}