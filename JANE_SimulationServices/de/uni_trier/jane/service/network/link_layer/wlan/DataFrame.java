package de.uni_trier.jane.service.network.link_layer.wlan;

import de.uni_trier.jane.basetypes.Dispatchable;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.signaling.SignalListener;
import de.uni_trier.jane.visualization.shapes.Shape;

// Ein Objekt dieser Klasse stellt ein Daten-Frame dar
public class DataFrame extends Frame {

    private int messageSize;

    private int headerSize = 28 * 8; // MAC - Header in IBSS
    private Shape messageShape;

    public DataFrame(LinkLayerMessage message) {        
		this.message = message;
		messageSize = message.getSize();
		messageShape = message.getShape();
    } 

    public DataFrame(int messageSize, Shape messageShape) {        
		this.message = null;
		this.messageSize = messageSize;
		this.messageShape = messageShape;
    } 

    public DataFrame(LinkLayerMessage message, int messageSize) {        
		this.message = message;
		this.messageSize = messageSize;	
		messageShape = message.getShape();
    } 

    public void handle(LinkLayerInfo info, SignalListener listener) {        
			((MacLayerInterface) listener).handleDataFrame((MacHeader) info, message);			
    } 
    
    
    public void handlePromisc(MacHeader macHeader, MacLayer80211 layer) {
        layer.handlePromiscDataFrame(macHeader, message);
        
    }
    
    

    public Dispatchable copy() {        
		return this;
    } 

    public Class getReceiverServiceClass() {        
		return MacLayerInterface.class;
    } 

    public int getSize() {        
		return messageSize + headerSize;
    } 

    public Shape getShape() {        
		return messageShape;
    } 

    public String getName() {        
		return "Data Frame";
    } 
 }
