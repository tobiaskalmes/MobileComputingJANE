/*****************************************************************************
 * 
 * ${Id}$
 *  
 ***********************************************************************
 *  
 * JANE - The Java Ad-hoc Network simulation and evaluation Environment
 *
 ***********************************************************************
 *
 * Copyright (C) 2002-2006
 * Hannes Frey and Daniel Goergen and Johannes K. Lehnert
 * Systemsoftware and Distributed Systems
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
package de.uni_trier.jane.service.operatingSystem.manager; 

import de.uni_trier.jane.basetypes.ListenerID;

/**
 * @author goergen
 *
 * TODO comment class
 */
public final class EventDBListenerID extends ListenerID {

    private long stamp;

    /**
     * Constructor for class <code>EventDBListenerID</code>
     * @param i
     */
    public EventDBListenerID(long stamp) {
        this.stamp=stamp;
    }

    public int hashCode() {
            final int PRIME = 1000003;
            int result = 0;
            result = PRIME * result + (int) (stamp >>> 32);
            result = PRIME * result + (int) (stamp & 0xFFFFFFFF);
    
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
    
            EventDBListenerID other = (EventDBListenerID) oth;
    
            if (this.stamp != other.stamp) {
                return false;
            }
    
            return true;
        }

        public int getCodingSize() {
            throw new IllegalAccessError("should _never_ leave the EventDB");
        }

        public String toString() {
            return "EventDBLID:"+stamp;
        }

}
