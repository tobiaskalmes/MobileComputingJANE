/*
 * Created on 13.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.network.link_layer;

import de.uni_trier.jane.basetypes.Address;

import java.io.Serializable;

/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LinkLayerProperties implements Serializable{

	private Address linkLayerAddress;
    private boolean promiscuousMode;
	private double minimumTransmissionRadius;
	private double maximumTransmissionRadius;

	/**
	 * @param linkLayerAddress
     * @param promiscuousMode
	 * @param minimumTransmissionRadius
	 * @param maximumTransmissionRadius
	 */
	public LinkLayerProperties(Address linkLayerAddress,
            boolean promiscuousMode,
			double minimumTransmissionRadius, double maximumTransmissionRadius
            ) {
		this.linkLayerAddress = linkLayerAddress;
        this.promiscuousMode=promiscuousMode;
        
        
		this.minimumTransmissionRadius = minimumTransmissionRadius;
		this.maximumTransmissionRadius = maximumTransmissionRadius;
	}
    
    




    
    /**
     * @return Returns the linkLayerAddress.
     */
    public Address getLinkLayerAddress() {
        return this.linkLayerAddress;
    }
    
    /**
     * @return Returns the promiscuousMode.
     */
    public boolean isPromiscuousMode() {
        return promiscuousMode;
    }

	/**
	 * @return
	 * @deprecated
	 */
	// TODO Sollen diese Werte irgendwann mal richtig gesetzt werden?
	public double getMaximumTransmissionRadius() {
		return maximumTransmissionRadius;
	}

	/**
	 * @return
	 * @deprecated
	 */
	// TODO Sollen diese Werte irgendwann mal richtig gesetzt werden?
	public double getMinimumTransmissionRadius() {
		return minimumTransmissionRadius;
	}

}
