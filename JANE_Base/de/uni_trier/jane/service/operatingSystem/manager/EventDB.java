/*****************************************************************************
 * 
 * EventDB.java
 * 
 * $Id: EventDB.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
package de.uni_trier.jane.service.operatingSystem.manager;

import java.util.Set;

import de.uni_trier.jane.basetypes.ListenerID;
import de.uni_trier.jane.service.event.ServiceEvent;

/**
 * @author goergen
 *
 * TODO comment class
 */
public interface EventDB {

    
    /**
     * Returns a set of all listenerIDs that registered an event template fitting to this event
     * @param event     the event for searching templates
     * @return  the set of registered IDs
     */
    public Set getListeners(ServiceEvent event);

    /**
     * Maps the given eventTemplate to the given ID 
     * @param eventByExample
     * @param listenerID
     */
    public void registerEventListener(ServiceEvent eventByExample,
            ListenerID listenerID);

    /**
     * Removes all templates registered by the given ID
     * @param listenerID
     */
    public void removeEventListener(ListenerID listenerID);

}