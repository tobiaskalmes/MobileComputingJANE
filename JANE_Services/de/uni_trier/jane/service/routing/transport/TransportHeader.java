/*****************************************************************************
 * 
 * TransportHeader.java
 * 
 * $Id: TransportHeader.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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
package de.uni_trier.jane.service.routing.transport;

import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.routing.RoutingHeader;

public class TransportHeader {
    
    
    
    private RoutingHeader routingHeader;
    private double timeout;
    private int retries;
    private ServiceID middleEnd;
    private ServiceID replyService;

    public TransportHeader(RoutingHeader routingHeader, double timeout, int retries, ServiceID middleEnd, ServiceID replyService) {
        super();
        this.routingHeader=routingHeader;
        this.timeout=timeout;
        this.retries=retries;
        this.middleEnd=middleEnd;
        this.replyService=replyService;
        
    }
    
    public TransportHeader(RoutingHeader routingHeader) {
        this(routingHeader,-1,0,null,null);
    }
    
    public void initDefault(TransportHeader defaultHeader){
        if (retries<0){
            retries=defaultHeader.retries;
        }
        if (timeout<=0){
            timeout=defaultHeader.timeout;
        }
    }

    public ServiceID getMiddleEnd() {
        return middleEnd;
    }

    public ServiceID getReplyService() {
        return replyService;
    }

    public int getRetries() {
        return retries;
    }

    public RoutingHeader getRoutingHeader() {
        return routingHeader;
    }

    public double getTimeout() {
        return timeout;
    }

    /**
     * TODO Comment method
     * @param locationAnycastID
     */
    public void setMiddleEnd(ServiceID middleEndAlgorithmID) {
        middleEnd=middleEndAlgorithmID;
        
    }
    
    

}
