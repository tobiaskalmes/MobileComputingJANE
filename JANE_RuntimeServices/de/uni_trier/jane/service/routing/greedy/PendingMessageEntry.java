/*****************************************************************************
 * 
 * PendingMessageEntry.java
 * 
 * $Id: PendingMessageEntry.java,v 1.1 2007/06/25 07:24:00 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.service.routing.greedy; 


import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.dsr.DSRRoutingHeader;

public class PendingMessageEntry {//extends Hashable{

    private RoutingHeader routingHeader;
    private RoutingTaskHandler routingTaskHandler;
    private int retries;

    /** 
     * Constructor for class <code>PendingMessageEntry</code>
     *
     * @param routingTaskHandler
     * @param routingHeader
     */
    public PendingMessageEntry(RoutingTaskHandler routingTaskHandler, DSRRoutingHeader routingHeader, int retries) {
        this.routingHeader=routingHeader;
        this.routingTaskHandler=routingTaskHandler;
        this.retries=retries;
        
    }
    
    public PendingMessageEntry(RoutingTaskHandler routingTaskHandler, RoutingHeader routingHeader) {
        this.routingHeader=routingHeader;
        this.routingTaskHandler=routingTaskHandler;

        
    }

//    public PendingMessageEntry(double timeout, Address receiver, RoutingData data, UnicastStatusHandler callbackHandler) {
//        // TODO Auto-generated constructor stub
//    }

    public boolean isRetryMessage(){
        return retries>0; 
    }
    
    public void retry(){
        retries--;
    }
    /**
     * @return Returns the routingHeader.
     */
    public RoutingHeader getRoutingHeader() {
        return routingHeader;
    }

    /**
     * @return Returns the routingTaskHandler.
     */
    public RoutingTaskHandler getRoutingTaskHandler() {
        return routingTaskHandler;
    }

//    public int hashCode() {
//         throw new IllegalAccessError("must implement hashcode!");
//        //return 0;
//    }
//
//    public boolean equals(Object obj) {
//        // TODO Auto-generated method stub
//        return false;
//    }

}
