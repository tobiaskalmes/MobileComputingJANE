/*
 * Created on Nov 19, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.uni_trier.jane.service.network.arp;

import java.util.*;

import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.network.transport_layer.*;

/**
 * @author daniel
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class ARPAddressMappings {

	private HashMap linkToTransportMap;

	/**
	 * @param deviceIDToAdressMappingMap
	 */
	public ARPAddressMappings(Map deviceIDToAdressMappingMap) {
		linkToTransportMap=new HashMap();
		Iterator iterator=deviceIDToAdressMappingMap.values().iterator();
		while (iterator.hasNext()){
			AddressMapping addressMapping=(AddressMapping)iterator.next();
			linkToTransportMap.put(addressMapping.getLinkLayerAddress(),addressMapping.getTransportLayerAddress());
		}
	}
	
	public TransportLayerAddress getTransportLayerAddress(LinkLayerAddress linkLayerAddress){
		return (TransportLayerAddress)linkToTransportMap.get(linkLayerAddress);
	}

}
