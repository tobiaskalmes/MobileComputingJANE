/*****************************************************************************
 * 
 * NetworkServiceID.java
 * 
 * $Id: NetworkServiceID.java,v 1.1 2007/06/25 07:23:46 srothkugel Exp $
 *  
 * Copyright (C) 2002-2004 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.service.network.link_layer; 

import de.uni_trier.jane.basetypes.*;

/**
 * @author goergen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NetworkServiceID extends ServiceID {

    private int receivePort;
    private int sendPort;
    private Class className;

    /**
     * Constructor for class <code>NetworkServiceID</code>
     * @param receivePort
     * @param sendPort
     * @param className
     */
    public NetworkServiceID(int receivePort, int sendPort, Class className) {
        this.receivePort=receivePort;
        this.sendPort=sendPort;
        this.className=className;

    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return className+" "+ receivePort+":"+sendPort;
    }



    public int hashCode() {
        final int PRIME = 1000003;
        int result = 0;
        result = PRIME * result + receivePort;
        result = PRIME * result + sendPort;
        if (className != null) {
            result = PRIME * result + className.hashCode();
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

        NetworkServiceID other = (NetworkServiceID) oth;

        if (this.receivePort != other.receivePort) {
            return false;
        }

        if (this.sendPort != other.sendPort) {
            return false;
        }
        if (this.className == null) {
            if (other.className != null) {
                return false;
            }
        } else {
            if (!this.className.equals(other.className)) {
                return false;
            }
        }

        return true;
    }

    //
    public Class getListenerClass() {
        return className;
    }

    //
    public int getCodingSize() {
        //ignored on platform
        return 0;
    }
}
