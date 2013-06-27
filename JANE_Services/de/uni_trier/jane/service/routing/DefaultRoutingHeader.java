/*****************************************************************************
* 
* $Id: DefaultRoutingHeader.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
*  
***********************************************************************
*  
* JANE - The Java Ad-hoc Network simulation and evaluation Environment
*
***********************************************************************
*
* Copyright (C) 2002-2006
* Hannes Frey and Daniel Goergen and Johannes K. Lehnert
* Systemsoftware and Distrubuted Systems
* University of Trier 
* Germany
* http://syssoft.uni-trier.de/jane
* 
* This program is free software; you can redistribute it and/or 
* modify it under the terms of the GNU General Public License 
* as published by the Free Software Foundation; either version 2 
* of the License, or (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful, 
* but WITHOUT ANY WARRANTY; without even the implied warranty of 
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
* General Public License for more details.
* 
* You should have received a copy of the GNU General Public License 
* along with this program; if not, write to the Free Software 
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
* 
*****************************************************************************/
package de.uni_trier.jane.service.routing; 

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;
import de.uni_trier.jane.service.routing.logging.loop_checker.*;
import de.uni_trier.jane.visualization.shapes.*;


/**
 * 
 * @author goergen
 *
 * TODO comment class
 */
public abstract class DefaultRoutingHeader implements RoutingHeader {
	
// Should not be serialized (Hybrid mode RMI schon...)
	//private Address previousNode; // TODO: Diese Information ist eigentlich geschenkt!!!
    //Receive strength from the last hop
    private LinkLayerInfo linkLayerInfo;
	//private double signalStrength;
    //private boolean isUnicast;
    
// must be serialized
    private Address sourceAddress;
    protected Position sourcePosition;
    protected Address destinationAddress;
	protected Position receiverPosition;

    private MessageID messageID;
    protected List route;
    private int hopCount;
    private int maxHops;
    private DelegationData delegationData;

	private transient LoopChecker loopChecker;
    private boolean promiscousMessage;
    private boolean promiscousHeader;
    private boolean traceRoute;
    
    
    
    /**
     * 
     * Constructor for class <code>RoutingHeaderImplementation</code>
     * @param sender
     * @param receiver
     * @param countHops
     * @param traceRoute
     */
    public DefaultRoutingHeader(Address sender, Address receiver,boolean countHops, boolean traceRoute) {
        
        this.destinationAddress=receiver;
        if (countHops){
            hopCount=0;
        }else{
            hopCount=-1;
        }
        maxHops=-1;
        this.traceRoute=traceRoute;
        if (traceRoute){
            route=new ArrayList();
            if (sender!=null){
                route.add(sender);
            }
        }else{
            this.sourceAddress=sender;    
        }
        linkLayerInfo = null;
        loopChecker = null;
    }
    
    protected DefaultRoutingHeader(DefaultRoutingHeader header) {
        sourceAddress=header.sourceAddress;
        destinationAddress=header.destinationAddress;
        messageID=header.messageID;
        hopCount=header.hopCount;
        delegationData=header.delegationData;
        if (header.route!=null){
            route=new ArrayList(header.route);
        }
        traceRoute=header.traceRoute;
        
       // previousNode = header.previousNode;
        sourcePosition = header.sourcePosition;
        receiverPosition=header.receiverPosition;
        maxHops=header.maxHops;

        loopChecker = header.loopChecker;
        promiscousHeader=header.promiscousHeader;
        promiscousMessage=header.promiscousMessage;
        //no!! copy lokal reset....
        //linkLayerInfo=null;
        linkLayerInfo=header.linkLayerInfo;
    }
    
    /**
     * @param maxHops The maxHops to set.
     */
    public void setMaxHops(int maxHops) {
        hopCount=0;
        this.maxHops = maxHops;
    }
    
    
    /**
     * 
     * tests wheter the maximum hopcount is reached
     * @return
     */
    public boolean hopCountReached(){
        if (hasHopCount()&&maxHops>0){
            return maxHops<getHopCount();
        }
        throw new IllegalAccessError("This routing header does not provide hop counting");
    }
    
    
    public boolean isPromiscousMessage() {
        return promiscousMessage;
    }
    
    public boolean isPromiscousHeader() {
    
    	return promiscousHeader;
    }
   
    public void setPromiscousHeader(boolean promiscousHeader) {
		this.promiscousHeader = promiscousHeader;
	}
    
    public void setPromiscousMessage(boolean promiscousMessage) {
		this.promiscousMessage = promiscousMessage;
	}
    
    /**
     * 
     * TODO Comment method
     * @return
     */
    public boolean hasMaxHopCount(){
        return maxHops>=0;
    }
    
    
    /**
     * @return  true, if the header supports unique message ids
     */
    public boolean hasMessageID(){
        return messageID!=null;
    }
    
    /**
     * @return Returns the messageID if the header contains it
     * @throws IllegalAccessError   if the header does not support this information
     */
    public MessageID getMessageID() {
        if (messageID==null) throw new IllegalAccessError("This routing header does not provide unique message identifiers");
        return messageID;
    }
    /**
     * @param messageID The messageID to set.
     */
    public void setMessageID(MessageID messageID) {
        this.messageID = messageID;
    }
    /**
     * @return Returns the receiver of the message
     * @throws IllegalAccessError   if the header does not support this information
     */
    public Address getReceiver() {
        if (destinationAddress==null) throw new IllegalAccessError("This routing header does not provide receiver information");
        return destinationAddress;
    }
    
    /**
     * @return true, if the header contains the receiver address
     */
    public boolean hasReceiver(){
        return destinationAddress!=null;
    }
    
    /**
     * @param receiver The receiver to set.
     */
    public void setDestinationAddress(Address receiver) {
        this.destinationAddress = receiver;
    }
    /**
     * @return Returns the sender.
     */
    public Address getSender() {
        if (route!=null&&route.size()>=1){
            return (Address)route.get(0);
        }
        return sourceAddress;
    }
    /**
     * @param sender The sender to set.
     */
    public void setSourceAddress(Address sender) {
        if (route!=null){
            if (route.isEmpty()||!route.get(0).equals(sender)){
                route.add(0,sender);
            }
            
            
        }else{
            this.sourceAddress = sender;
        }
    }
    
    public void setSourcePosition(Position senderPosition) {
        this.sourcePosition = senderPosition;
    }
    
    public boolean hasSenderPosition(){
        return sourcePosition!=null;
    }
    
    public Position getSenderPosition() {
        if (sourcePosition!=null){
            return sourcePosition;
        }
        throw new IllegalAccessError("This routing header does not provide sender position information");
        
    }
    
    /**
     * @return routing header contains a message route
     */
    public boolean hasRoute(){
        return route!=null;
    }

	public void setTraceRoute() {
        if (route!=null){
            traceRoute=true;
            route=new ArrayList();
            if (sourceAddress!=null){
                route.add(sourceAddress);
                sourceAddress = null;    
            }    
        }
		
	}
    

    /**
     * Adds the next hop the the routing headers message route if it has a route
     * @param address       the address of the next hop
     * @throws IllegalAccessError   if the header does not support this information
     */
    public void addHop(Address address){
        if (route==null) throw new IllegalAccessError("This routing header does not provide route information");
        route.add(address);
        
    }
    
    /**
     * @return Returns the route of the message if it exists.
     * @throws IllegalAccessError   if the header does not support this information
     */
    public List getRoute() {
        if (route==null) throw new IllegalAccessError("This routing header does not provide route information");
        return new ArrayList(route);
    }
    
    /**
     * @return true, if the header counts the routing hops
     */
    public boolean hasHopCount(){
        return hopCount >=0;//||route!=null;
    }
	public void setHopCount(int i) {
		hopCount=i;
		
	}
	
    
    /**
     * @return the hop count of the message if the header contains the hop count
     * @throws IllegalAccessError   if the header does not support this information
     */
    public int getHopCount() {
        if(hopCount>=0){
            return hopCount;
        }
        throw new IllegalAccessError("This routing header does not provide hop count information");
        
    }
    
    /**
     * increases the hop count counter in this routing header by 1 
     * @throws IllegalAccessError   if the header does not support this information
     */
    public void nextHop(){
          
        if (hopCount<0) throw new IllegalAccessError("This routing header does not provide hop count information");
        hopCount++;
    }
    

    public boolean hasDelegationData(){
        return delegationData!=null;
    }
    
    public DelegationData getDelegationData() {
        if (delegationData==null) throw new IllegalAccessError("This routing header does not provide delegation information");
        return delegationData;
    }
    
    public void setDelegationData(DelegationData delegationData) {
        this.delegationData = delegationData;
    }

    
    
    
    public Address getPreviousNode() {
        if (linkLayerInfo!=null){
            return linkLayerInfo.getSender();
        }
        return null;
            
	}

//	public void setPreviousNode(Address previousNode) {
//		this.previousNode = previousNode;
//	}


	public boolean hasReceiverPosition() {
		return receiverPosition != null;
	}
	
	public Position getReceiverPosition() {
		return receiverPosition;
	}

	public void setReceiverPosition(Position position) {
		this.receiverPosition = position;
	}


	
	/**
     * Create a copy of the header. This method is called when the routing
     * message has to be duplicated at the network
     * @return a copy of the header. You must always return a new header object and call the copy constructur of the super class.
     */
    public abstract LinkLayerInfo copy();

    /**
     * Get the size of the data stored in the header.
     * @return the size (e.g. in Bits)
     */
    public int getSize(){
        int size=0;
        if (destinationAddress!=null) size+=destinationAddress.getCodingSize();
        if (sourceAddress!=null) size+=sourceAddress.getCodingSize();
        if (messageID!=null) size+=messageID.getCodingSize();
        if (hopCount>=0) size+=4*8;
        if(maxHops>0) size+=4*8;
        if (route!=null&&route.size()>0){
            size+=((Address)route.get(0)).getCodingSize()*route.size();
        }
        
        if (delegationData!=null) size+=delegationData.getCodingSize();
        
        return size+getCodingSize();
        
    }

    /**
     * Get the size of the data stored in the header.
     * @return the size (e.g. in Bits)
     */
    public abstract int getCodingSize();

    /**
     * The shape of the routing header will determine the look of the routing
     * message when the simulation is visualizing messages. Return "null" if you
     * want to use the default look of routing messages.
     * @return the shape used for visualization purposes
     */
    public abstract Shape getShape();
    
    /**
     * Returns the id of the routing service
     * @return the routing service id
     */
    public abstract ServiceID getRoutingAlgorithmID();

    
    /**
     * 
     * TODO: comment method 
     * @param loopChecker
     */
	public void setLoopChecker(LoopChecker loopChecker) {
		this.loopChecker = loopChecker;
	}

    /**
     * 
     * TODO: comment method 
     * @return
     */
	public LoopChecker getLoopChecker() {
		return loopChecker;
	}

    public double getSignalStrength() {
        return linkLayerInfo.getSignalStrength();
    }
    
    public boolean isUnicastMessage() {
        //return isUnicast;
        throw new IllegalAccessError("Not yet provided");
    }
    
    public boolean isBroadcastMessage() {
        throw new IllegalAccessError("Not yet provided");
     //   return false;
    }
    
    public boolean isReliable() {
        throw new IllegalAccessError("Not yet provided");
        //return false;
    }

    /**
     * TODO Comment method
     * @param info
     */
    public void setLinkLayerInfo(LinkLayerInfo info) {
        this.linkLayerInfo=info;
        
    }
    /**
     * @return Returns the linkLayerInfo.
     */
    public LinkLayerInfo getLinkLayerInfo() {
        return this.linkLayerInfo;
    }

    /**
     * TODO: comment method 
     * @return
     */
    public boolean traceRoute() {
        return traceRoute;
    }


	
}
