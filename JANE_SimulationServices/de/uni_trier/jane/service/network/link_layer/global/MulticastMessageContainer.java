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
import de.uni_trier.jane.visualization.shapes.*;

/**
 * @author Daniel G�rgen
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MulticastMessageContainer implements MulticastMessage{



	private Address[] receivers;
	private LinkLayerMessage message;
	private MessageID messageID;

	/**
	 * @param receivers
	 * @param message
	 * @param messageID
	 */
	public MulticastMessageContainer(Address[] receivers, LinkLayerMessage message, MessageID messageID) {
		this.receivers=receivers;
		this.message=message;
		this.messageID=messageID;

	}

	/* (non-Javadoc)
	 * @see de.uni_trier.ssds.service.network.link_layer.LinkLayerMessage#handle(de.uni_trier.ssds.service.network.link_layer.LinkLayerInfo, de.uni_trier.jane.service.Service)
	 */
	public void handle(LinkLayerInfo info, SignalListener listener) {
		((GlobalMulticastNetworkLinkLayer)listener).receiveMulticastContainer(info,receivers,message,messageID);
		
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.basetypes.Dispatchable#copy()
	 */
	public Dispatchable copy() {
		LinkLayerMessage messageCopy=(LinkLayerMessage)message.copy();
		if (messageCopy==message){
			return this;
		}
		return new MulticastMessageContainer(receivers,messageCopy,messageID);
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
		int size=receivers.length*receivers[0].getCodingSize();
		size+=message.getSize();
		size+=messageID.getCodingSize();
		
		return size;
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.basetypes.Sendable#getShape()
	 */
	public Shape getShape() {
		return message.getShape();
	}

}
