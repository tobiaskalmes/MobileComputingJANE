package de.uni_trier.jane.service.network.link_layer.wlan;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.signaling.SignalListener;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.*;

// Ein Objekt dieser Klasse stellt ein Beacon-Frame dar
public class BeaconFrame extends Frame {

    public static final int size = (24 + 31 + 4) * 8; // MAC Header + Beacon Frame Body + FCS in Bit

    public BeaconFrame() {        
			message = null;
    } 

    public void handle(LinkLayerInfo info, SignalListener listener) {        
		MacLayerInterface macLayer = (MacLayerInterface) listener;
		macLayer.handleBeaconFrame((MacHeader) info);
		
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
        return new RectangleShape(Position.NULL_POSITION,new Extent(5,5),Color.BROWN,true);
    } 

    public String getName() {        
		return "Beacon Frame";
    } 
 }
