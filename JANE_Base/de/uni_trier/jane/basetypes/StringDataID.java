/*****************************************************************************
 * 
 * StringDataID.java
 * 
 * $Id: StringDataID.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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
package de.uni_trier.jane.basetypes;

public class StringDataID extends DataID {
    
    private String stringID;
    

    /**
     * Constructor for class <code>StringDataID</code>
     *
     * @param stringID
     */
    public StringDataID(String stringID) {
        super();
        // TODO Auto-generated constructor stub
        this.stringID = stringID;
    }

    public String toString() {
        return stringID;
    }

    public int hashCode() {
            final int PRIME = 1000003;
            int result = 0;
            if (stringID != null) {
                result = PRIME * result + stringID.hashCode();
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
    
            StringDataID other = (StringDataID) oth;
            if (this.stringID == null) {
                if (other.stringID != null) {
                    return false;
                }
            } else {
                if (!this.stringID.equals(other.stringID)) {
                    return false;
                }
            }
    
            return true;
        }



}
