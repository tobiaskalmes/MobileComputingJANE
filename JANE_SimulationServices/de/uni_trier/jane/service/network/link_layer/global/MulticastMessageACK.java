/*
 * Created on 09.01.2005
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.uni_trier.jane.service.network.link_layer.global;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.routing.MessageID;
import de.uni_trier.jane.signaling.SignalListener;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * @author Daniel G�rgen
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MulticastMessageACK implements MulticastMessage {


	private MessageID messageID;

	/**
	 * @param messageID
	 */
	public MulticastMessageACK(MessageID messageID) {
		this.messageID=messageID;

	}

	/* (non-Javadoc)
	 * @see de.uni_trier.ssds.service.network.link_layer.LinkLayerMessage#handle(de.uni_trier.ssds.service.network.link_layer.LinkLayerInfo, de.uni_trier.jane.service.Service)
	 */
	public void handle(LinkLayerInfo info, SignalListener listener) {
		((GlobalMulticastNetworkLinkLayer)listener).receiveMulticastACK(info.getSender(),messageID);

	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.basetypes.Dispatchable#copy()
	 */
	public Dispatchable copy() {
		return this;
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.basetypes.Dispatchable#getReceiverServiceClass()
	 */
	public Class getReceiverServiceClass() {
		return GlobalMulticastNetworkLinkLayer.class;
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.basetypes.Sendable#getSize()
	 */
	public int getSize() {
		return messageID.getCodingSize();
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.basetypes.Sendable#getShape()
	 */
	public Shape getShape() {
		return new RectangleShape(Position.NULL_POSITION,new Extent(4,4),Color.BLUE,false);
	}

}
