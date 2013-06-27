/*****************************************************************************
 * 
 * ListenerHandlerID.java
 * 
 * $Id: ListenerHandlerID.java,v 1.1 2007/06/25 07:22:41 srothkugel Exp $
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
package de.uni_trier.jane.hybrid.basetypes; 

import de.uni_trier.jane.service.operatingSystem.*;

import java.io.Serializable;

/**
 * @author daniel
 *
 * TODO comment class
 */
public class ListenerHandlerID  implements Serializable{
     private long id;
    private ServiceContext executingContext;
    /**
     * Constructor for class <code>ListenerHandlerID</code>
     * @param executingService
     * @param id
     */
    public ListenerHandlerID(ServiceContext executingService, long id) {
        this.id = id;
        this.executingContext=executingService;
    }
    
    /**
     * @return Returns the executingService.
     */
    public ServiceContext getExecutingContext() {
        return executingContext;
    }
    
  
    public int hashCode() {
        final int PRIME = 1000003;
        int result = 0;
        result = PRIME * result + (int) (id >>> 32);
        result = PRIME * result + (int) (id & 0xFFFFFFFF);
        if (executingContext != null) {
            result = PRIME * result + executingContext.hashCode();
        }

        return result;
    }

    public boolean equals(Object oth) {
        if (this == oth) {
            return true;
        }

        if (oth == null) {
            return false;
        }

        if (oth.getClass() != getClass()) {
            return false;
        }

        ListenerHandlerID other = (ListenerHandlerID) oth;

        if (this.id != other.id) {
            return false;
        }
        if (this.executingContext == null) {
            if (other.executingContext != null) {
                return false;
            }
        } else {
            if (!this.executingContext.equals(other.executingContext)) {
                return false;
            }
        }

        return true;
    }
}
