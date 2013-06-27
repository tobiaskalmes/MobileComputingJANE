/*****************************************************************************
* 
* $Id: RoutingHeader.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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

import java.io.Serializable;
import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.network.link_layer.LinkLayerInfo;


/**
 * 
 * @author goergen
 *
 * TODO comment class
 */
public interface RoutingHeader extends Serializable, LinkLayerInfo {

    /**
     * @return  true, if the header supports unique message ids
     */
    public boolean hasMessageID();

    /**
     * @return Returns the messageID if the header contains it
     * @throws IllegalAccessError   if the header does not support this information
     */
    public MessageID getMessageID();

    /**
     * @return Returns the receiver of the message
     * @throws IllegalAccessError   if the header does not support this information
     */
    public Address getReceiver();

    /**
     * @return true, if the header contains the receiver address
     */
    public boolean hasReceiver();

    /**
     * @return Returns the sender.
     */
    public Address getSender();

    /**
     * @return routing header contains a message route
     */
    public boolean hasRoute();

    /**
     * @return Returns the route of the message if it exists.
     * @throws IllegalAccessError   if the header does not support this information
     */
    public List getRoute();

    /**
     * @return true, if the header counts the routing hops
     */
    public boolean hasHopCount();

    /**
     * @return the hop count of the message if the header contains the hop count
     * @throws IllegalAccessError   if the header does not support this information
     */
    public int getHopCount();
    
    /**
     * Returns the id of the routing service
     * @return the routing service id
     */
    public abstract ServiceID getRoutingAlgorithmID();
    

    /**
     * Returns true, if this header contains information for generic delegation to other routing algorithms
     * 
     * @return true, if delegation data is present
     */
    public boolean hasDelegationData();
    
    /**
     * Returns the delegation data for this message
     * 
     * @return
     */
    public DelegationData getDelegationData();
    
    /**
     * TODO Comment method
     * @param data
     */
    public void setDelegationData(DelegationData data);
    
    
    
    //public void setDelegationData(DelegationData delegationData);

    /**
     * Returns true, if the corresponding message should be delivered to all visited hosts.
     * @return
     */
    public boolean isPromiscousMessage();
    
    /**
     * 
     * @return
     */
    public boolean isPromiscousHeader();
    
    /**
     * Returns the LinkLayerInformation of the last message transmission
     * @return the linkLayerInformation
     */
    public LinkLayerInfo getLinkLayerInfo();


    //public void handleRouting(RuntimeOperatingSystem runtimeOperatingSystem,RoutingTaskHandler handler);

}