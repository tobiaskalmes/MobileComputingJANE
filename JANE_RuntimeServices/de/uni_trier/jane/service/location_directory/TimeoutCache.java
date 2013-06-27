/*****************************************************************************
 * 
 * TimeoutCache.java
 * 
 * $Id: TimeoutCache.java,v 1.1 2007/06/25 07:24:01 srothkugel Exp $
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
package de.uni_trier.jane.service.location_directory; 

import java.util.*;
import java.util.Map.Entry;



import de.uni_trier.jane.service.ServiceTimeout;

import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;

public class TimeoutCache{

    private RuntimeOperatingSystem operatingSystem;
    class CacheEntryTimeout extends ServiceTimeout{



        public CacheEntryTimeout(double delta) {
            super(delta);

        }

        public void handle() {
            cleanup();
        }
        
    }
    
    class CacheEntry {
        Object value;
        Double time;
        private Object key;
        /**
         * Constructor for class <code>CacheEntry</code>
         *
         * @param object
         * @param timeout
         */
        public CacheEntry(Object key,Object value, Double time) {
            super();
            this.value = value;
            this.key=key;
            this.time=time;
            createCacheEntry(key, time);
        }


        /**
         * TODO Comment method
         * @param key
         * @param time
         */
        private void createCacheEntry(Object key, Double time) {
            Set set;
            if (cacheMap.containsKey(time)){
                set=(Set)cacheMap.get(time);
            }else{
                set=new HashSet();
                cacheMap.put(time,set);
            }
            set.add(key);
        }
        

        public Object getValue() {
            return value;
        }
        
        /**
         * 
         * TODO Comment method
         * @param value
         * @param time
         */
        public void updateValue(Object value,Double time) {
            this.value=value;
            Set set=(Set)cacheMap.get(this.time);
            set.remove(key);
            if (set.isEmpty()){
                set.remove(this.time);
            }
            createCacheEntry(key,time);
            this.time=time;
            this.value=value;
        }

    }
    
    private TreeMap cacheMap;
    private HashMap keyValueMap;
    private double cleanupDelta;
    private ServiceTimeout timeout;
    private int maxSize;
    
    public TimeoutCache(double cleanupDelta) {
        this(cleanupDelta,-1);
    }
    
    
    public TimeoutCache(double cleanupDelta, int maxSize) {
        cacheMap=new TreeMap();
        keyValueMap=new HashMap();
        this.cleanupDelta=cleanupDelta;
        this.maxSize=maxSize;
        
    }

    /**
     * TODO Comment method
     */
    public void cleanup() {
        Iterator iterator=cacheMap.entrySet().iterator();
        double ct=operatingSystem.getTime();
        
        while (iterator.hasNext()){
            Entry entry=(Entry) iterator.next();
            
            double current=((Double)entry.getKey()).doubleValue();
            if (current<ct){
                iterator.remove();
                Iterator keyIterator=((Set)entry.getValue()).iterator();
                while(keyIterator.hasNext()){
                    keyValueMap.remove(keyIterator.next());
                }
                
                
            }else{
                break;
            }
        }
        if (keyValueMap.isEmpty()){
            timeout=null;
        }else{
            operatingSystem.setTimeout(timeout);
        }
        
    }

    public void init(RuntimeOperatingSystem operatingSystem){
        this.operatingSystem=operatingSystem;
    }
    
    public void map(Object key, Object value, double cachingDelta) {
        Double exireTime=new Double(operatingSystem.getTime()+cachingDelta);
        if (keyValueMap.containsKey(key)){
            CacheEntry entry=(CacheEntry) keyValueMap.get(key);
            entry.updateValue(value,exireTime);
        }else{
            CacheEntry entry=new CacheEntry(key,value,exireTime);
            keyValueMap.put(key,entry);
            if (timeout==null){
                timeout=new CacheEntryTimeout(cleanupDelta);
                operatingSystem.setTimeout(timeout);
            }else if (maxSize>0&&keyValueMap.size()>maxSize){
                Set set=(Set)cacheMap.remove(cacheMap.firstKey());
                Iterator keyIterator=set.iterator();
                while(keyIterator.hasNext()){
                    keyValueMap.remove(keyIterator.next());
                }
                
            }
        }
        
        
//        CacheEntry cacheEntry=(CacheEntry) map.get(key);
//        ServiceTimeout serviceTimeout=new CacheCacheEntry cacheEntry=(CacheEntry) map.get(key);
//      ServiceTimeout serviceTimeout=new CacheEntryTimeout(cachingDelta,key);
//      operatingSystem.setTimeout(serviceTimeout);
//      if (cacheEntry!=null){
//          operatingSystem.removeTimeout(cacheEntry.getServiceTimeout());
//          cacheEntry.setValue(value);
//      }else{
//          cacheEntry=new CacheEntry(value);
//          map.put(key,cacheEntry);
//      }
//      cacheEntry.setServiceTimeout(serviceTimeout);EntryTimeout(cachingDelta,key);
//        operatingSystem.setTimeout(serviceTimeout);
//        if (cacheEntry!=null){
//            operatingSystem.removeTimeout(cacheEntry.getServiceTimeout());
//            cacheEntry.setValue(value);
//        }else{
//            cacheEntry=new CacheEntry(value);
//            map.put(key,cacheEntry);
//        }
//        cacheEntry.setServiceTimeout(serviceTimeout);
        
    }

    public boolean hasKey(Object key) {
        return keyValueMap.containsKey(key);
    }
    
    public Set keySet() {
        return keyValueMap.keySet();
    }

    public Object get(Object key) {
        CacheEntry cacheEntry=(CacheEntry)keyValueMap.get(key);
        if (cacheEntry!=null){
            return cacheEntry.getValue();
        }
        return null;
    }
    
}