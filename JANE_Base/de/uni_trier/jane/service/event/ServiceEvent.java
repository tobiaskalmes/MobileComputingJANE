/*****************************************************************************
 * 
 * Event.java
 * 
 * $Id: ServiceEvent.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
package de.uni_trier.jane.service.event; 


import de.uni_trier.jane.basetypes.Dispatchable;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.Signal;
import de.uni_trier.jane.signaling.SignalListener;

/**
 * @author goergen
 *
 * TODO comment class
 */
public class ServiceEvent implements Signal {
    /** The serviceID of the event sender **/
    /** Should not be renamed! @see de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystemImpl#sendEvent(ServiceEvent) **/
    private ServiceID eventSenderID;
    /** The service class of the event sender **/
    private Class eventSenderClass;
    
    
    /**
     * SenderConstructor for class <code>ServiceEvent</code>
     * called by the original event
     */
    public ServiceEvent() {
        // eventSenderID and eventSenderClass are initialized by the OperatingSystem! 
    }
    
    
        
    /**
     * TemplateConstructor for class <code>ServiceEvent</code>
     * Should be only called by event templates _not_ by fired events!
     * These attibutes are overidden by the OperatingSystem.
     * @param eventSenderID
     * @param eventSenderClass
     * 
     */
    public ServiceEvent(ServiceID eventSenderID, Class eventSenderClass) {
        this.eventSenderID = eventSenderID;
        this.eventSenderClass = eventSenderClass;
    }



    /**
     * Constructor for class <code>ServiceEvent</code>
     * @param event
     */
    protected ServiceEvent(ServiceEvent event) {
        eventSenderClass=event.eventSenderClass;
        eventSenderID=event.eventSenderID;
    }



    /**
     * @return Returns the eventSenderClass.
     */
    public Class getEventSenderClass() {
        return eventSenderClass;
    }
    
    /**
     * @return Returns the eventSenderID.
     */
    public ServiceID getEventSenderID() {
        return eventSenderID;
    }
    
    
    /**
     * Overide this method if the event is mutable
     * @return a copy of this event
     */
    public Dispatchable copy() {
        return this;
    }
    
    public Class getReceiverServiceClass() {
         return EventListener.class;
    }
    
    
    public void handle(SignalListener listener) {
        ((EventListener)listener).handle(this);
    }
    
    
    

}
