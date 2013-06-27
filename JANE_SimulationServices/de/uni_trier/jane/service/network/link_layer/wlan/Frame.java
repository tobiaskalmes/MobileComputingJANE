package de.uni_trier.jane.service.network.link_layer.wlan;

import de.uni_trier.jane.service.network.link_layer.LinkLayerMessage;
import de.uni_trier.jane.visualization.shapes.Shape;

// Oberklasse für alle verwendeten Frame Typen
public abstract class Frame implements LinkLayerMessage {

	protected LinkLayerMessage message;

    public Shape getShape() {        
		return message.getShape();
    } 

    public LinkLayerMessage getMessage() {        
		return message;
    } 

    public abstract String getName();

    public abstract void handlePromisc(MacHeader macHeader, MacLayer80211 layer) ;
 }
