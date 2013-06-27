/*****************************************************************************
 * 
 * StackedDataID.java
 * 
 * $Id: StackedDataID.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
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


public class StackedDataID extends DataID {
    
    private DataID main;
    private DataID enclosing;

    /**
     * 
     * Constructor for class <code>StackedDataID</code>
     *
     * @param main
     * @param enclosing
     */
    public StackedDataID(DataID main, DataID enclosing) {
        this.main=main;
        this.enclosing=enclosing;
    }

    public String toString() {
        return main+":"+enclosing;
    }





    public int hashCode() {
            final int PRIME = 1000003;
            int result = 0;
            if (main != null) {
                result = PRIME * result + main.hashCode();
            }
            if (enclosing != null) {
                result = PRIME * result + enclosing.hashCode();
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
    
            StackedDataID other = (StackedDataID) oth;
            if (this.main == null) {
                if (other.main != null) {
                    return false;
                }
            } else {
                if (!this.main.equals(other.main)) {
                    return false;
                }
            }
            if (this.enclosing == null) {
                if (other.enclosing != null) {
                    return false;
                }
            } else {
                if (!this.enclosing.equals(other.enclosing)) {
                    return false;
                }
            }
    
            return true;
        }

}
