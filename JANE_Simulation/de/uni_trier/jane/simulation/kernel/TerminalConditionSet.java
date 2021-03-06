/*****************************************************************************
 * 
 * TerminalConditionSet.java
 * 
 * $Id: TerminalConditionSet.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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
package de.uni_trier.jane.simulation.kernel; 

import java.util.*;

/**
 * @author goergen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TerminalConditionSet implements Condition {

    private Set conditionSet;


    /**
     * Constructor for class <code>TerminalConditionSet</code>
     * 
     */
    public TerminalConditionSet() {

        conditionSet=new LinkedHashSet();
    }
    
    /**
     * Adds a new condition to the set
     * @param condition
     */
    public void addCondition(Condition condition){
        if (condition!=null){
            conditionSet.add(condition);
        }
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.simulation.kernel.Condition#reached()
     */
    public boolean reached() {
        Iterator iterator=conditionSet.iterator();
        while(iterator.hasNext()){
            if (((Condition)iterator.next()).reached()) return true;
        }
        return false;
    }

}
