/*****************************************************************************
 * 
 * HashMapSet.java
 * 
 * $Id: HashMapSet.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Daniel Goergen and Hannes Frey and Johannes K. Lehnert
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
package de.uni_trier.jane.util;



import java.util.*;

/**
 * TODO: comment class  
 * @author daniel
 **/

public class HashMapSet {

    private HashMap map;
    
    /**
     * Constructor for class HashMapSet 
     * every key is assigned to a set of values
     * 
     */
    public HashMapSet() {
        map=new HashMap();
        
    }
    
    /**
     * Add value to the set assigned to key
     * @param key
     * @param value
     */
    public boolean put(Object key, Object value) {
        Set set=(Set)map.get(key);
        if (set==null){
            set=new HashSet();
            map.put(key,set);
        }
        return set.add(value);
        
    }

    /**
     * Remove the key and all assigned values
     * @param key	the key to remove
     * @return	the set of assigned values
     */
    public Set remove(Object key) {
        return (Set)map.remove(key);
    }
    
    /**
     * Return all values assigned to key 
     * @param key	the key
     * @return		the set of all values assigned to key
     */
    public Set get(Object key){
        return (Set)map.get(key);
    }
    
    /**
     * Rmove the value from the value set assigned to key 
     * @param key	
     * @param value	
     * @return true if a th mapping key/value exists
     */
    public boolean remove(Object key, Object value){
        if (map.containsKey(key)){
            Set set=(Set)map.get(key);
            boolean retval=set.remove(value);
            if (set.isEmpty()){
                map.remove(key);
            }
            return retval;
        }
        return false;
    }

    /**
     * Checks whether the given key exists 
     * @param key	the key to check
     * @return	true, if the key exists
     */
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }
    
    /**
     * Checks whether the mapping key/value exists
     * @param key	the key	
     * @param value	the value
     * @return	true, if the mapping exists
     */
    public boolean contains(Object key, Object value){
        Set set=(Set)map.get(key);
        
        return (set!=null&&set.contains(value));
    }

    /**
     * TODO Comment method
     * @return
     */
    public Set keySet() {
        return map.keySet();
    }

    /**
     * TODO Comment method
     * @return
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * 
     * TODO Comment method
     * @return
     */
    public int size() {
        return this.map.size();
    }

    /**
     * 
     * TODO Comment method
     */
	public void clear() {
		map.clear();
		
	}
    
 
}
