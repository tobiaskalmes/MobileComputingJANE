/*
 * Created on Feb 17, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.uni_trier.jane.service.network.link_layer.shared_network;

import de.uni_trier.jane.basetypes.*;

/**
 * @author goergen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface NetworkStatistic {
	public void networkInUse(DeviceID address);
	public void networkFree(DeviceID owner);
	public void unicastCollission(DeviceID sender, DeviceID receiver);
	public void broadcastCollision(DeviceID sender, DeviceID receiver);
	public void unicastReceived(DeviceID sender, DeviceID receiver);
	public void broadcastReceived(DeviceID sender, DeviceID receiver);

}
