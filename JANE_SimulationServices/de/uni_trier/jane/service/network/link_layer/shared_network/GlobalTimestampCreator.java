/*
 * @author Stefan Peters
 * Created on 07.04.2005
 */
package de.uni_trier.jane.service.network.link_layer.shared_network;

/**
 * @author Stefan Peters
 */
public class GlobalTimestampCreator {
	private long timestamp;
	
	/**
	 * @return
	 */
	public long next() {
		return timestamp++;
	}

}
