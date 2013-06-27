/*****************************************************************************
 * 
 * HandlerListenerPair.java
 * 
 * $Id: HandlerListenerPair.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
package de.uni_trier.jane.basetypes; 

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;


/**
 * TODO: comment class  
 * @author daniel
 **/

public final class HandlerListenerPair extends ID {

    private ListenerFinishedHandler handler;
    private ListenerID listenerID;

    /**
     * Constructor for class HandlerListenrPair 
     *
     * @param handler
     * @param listenerID
     */
    public HandlerListenerPair(ListenerFinishedHandler handler, ListenerID listenerID) {
        this.handler=handler;
        this.listenerID=listenerID;

    }



    public int hashCode() {
        final int PRIME = 1000003;
        int result = 0;
        if (handler != null) {
            result = PRIME * result + handler.hashCode();
        }
        if (listenerID != null) {
            result = PRIME * result + listenerID.hashCode();
        }

        return result;
    }

    public boolean equals(Object oth) {
        if (this == oth) {
            return true;
        }



        HandlerListenerPair other = (HandlerListenerPair) oth;
        if (this.handler == null) {
            if (other.handler != null) {
                return false;
            }
        } else {
            if (!this.handler.equals(other.handler)) {
                return false;
            }
        }
        if (this.listenerID == null) {
            if (other.listenerID != null) {
                return false;
            }
        } else {
            if (!this.listenerID.equals(other.listenerID)) {
                return false;
            }
        }

        return true;
    }
    /**
     * @return Returns the listenerID.
     */
    public ListenerID getListenerID() {
        return listenerID;
    }



    //
    public String toString() {
        return "HandlerListenerPair"+listenerID.toString();
    }
}