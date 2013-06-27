package de.uni_trier.jane.service.network.link_layer.wlan;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.signaling.SignalListener;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.*;

// Ein Objekt dieser Klasse stellt ein CTS-Frame dar
public class CTSFrame extends Frame {

    private int size = 14 * 8;

    public CTSFrame() {        
		message = null;
    } 

    public void handle(LinkLayerInfo info, SignalListener listener) {        
		((MacLayerInterface) listener).handleCTSFrame((MacHeader) info);
    } 
    
    
    public void handlePromisc(MacHeader macHeader, MacLayer80211 layer) {
        //ignore
        
    }

    public Dispatchable copy() {        
		return this;
    } 

    public Class getReceiverServiceClass() {        
		return MacLayerInterface.class;
    } 

    public int getSize() {        
		return size;
    } 

    public Shape getShape() {        
        return new RectangleShape(Position.NULL_POSITION,new Extent(5,5),Color.mixColor(Color.YELLOW,Color.BLACK, 0.3),true);
    } 

    public String getName() {        
		return "CTS Frame";
    } 
 }
