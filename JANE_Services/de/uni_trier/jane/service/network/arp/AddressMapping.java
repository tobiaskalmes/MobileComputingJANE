/*
 * Created on Nov 19, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.uni_trier.jane.service.network.arp;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.network.transport_layer.*;

/**
 * @author daniel
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class AddressMapping extends ID{
	private LinkLayerAddress linkLayerAddress;
	private TransportLayerAddress transportLayerAddress;

	/**
	 * @param linkLayerAddress
	 * @param transportLayerAddress
	 */
	public AddressMapping(LinkLayerAddress linkLayerAddress,
			TransportLayerAddress transportLayerAddress) {
		super();
		this.linkLayerAddress = linkLayerAddress;
		this.transportLayerAddress = transportLayerAddress;
	}
	/**
	 * @return Returns the linkLayerAddress.
	 */
	public LinkLayerAddress getLinkLayerAddress() {
		return linkLayerAddress;
	}
	/**
	 * @return Returns the transportLayerAddress.
	 */
	public TransportLayerAddress getTransportLayerAddress() {
		return transportLayerAddress;
	}
	public int hashCode() {
		final int PRIME = 1000003;
		int result = 0;
		if (linkLayerAddress != null) {
			result = PRIME * result + linkLayerAddress.hashCode();
		}
		if (transportLayerAddress != null) {
			result = PRIME * result + transportLayerAddress.hashCode();
		}

		return result;
	}

	public boolean equals(Object oth) {
		if (this == oth) {
			return true;
		}

		

		AddressMapping other = (AddressMapping) oth;
		if (this.linkLayerAddress == null) {
			if (other.linkLayerAddress != null) {
				return false;
			}
		} else {
			if (!this.linkLayerAddress.equals(other.linkLayerAddress)) {
				return false;
			}
		}
		if (this.transportLayerAddress == null) {
			if (other.transportLayerAddress != null) {
				return false;
			}
		} else {
			if (!this.transportLayerAddress.equals(other.transportLayerAddress)) {
				return false;
			}
		}

		return true;
	}
	//
    public String toString() {
     
        return linkLayerAddress+":"+transportLayerAddress;
    }
}
