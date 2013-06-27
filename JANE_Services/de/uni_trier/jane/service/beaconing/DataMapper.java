/*****************************************************************************
 * 
 * DataMapper.java
 * 
 * $Id: DataMapper.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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
package de.uni_trier.jane.service.beaconing;

import de.uni_trier.jane.basetypes.*;


import java.io.*;
import java.util.HashMap;

public class DataMapper {

    private static class SerializerEntry {

        private DataSerializer serializer;
        private long uniqueID;

        public SerializerEntry(DataSerializer serializer, long uniqueID) {
            this.serializer=serializer;
            this.uniqueID=uniqueID;
        }
        
        public DataSerializer getSerializer() {
            return serializer;
        }
        
        public long getUniqueID() {
            return uniqueID;
        }
        
        


    }

    private static HashMap serMap;
    private static HashMap uniqueIDMap;
    static{
        serMap=new HashMap();
        uniqueIDMap=new HashMap();
    }

//    public static void map(ID dataID, long uniqueID, DataSerializer serializer){
//        Long uid=new Long(uniqueID);
//        if (uniqueIDMap.containsKey(uid)){
//            if (!uniqueIDMap.get(uid).equals(dataID)){
//                throw new IllegalStateException("given ID does already exist for another mapping");
//            }
//        }
//        serMap.put(dataID,new SerializerEntry(serializer,uniqueID));
//        uniqueIDMap.put(uid,dataID);
//        
//    }
    
    /**
     * 
     */
    public static void map(Class dataClass, long uniqueID, DataSerializer serializer) {
        Long uid=new Long(uniqueID);
        if (uniqueIDMap.containsKey(uid)){
            if (!uniqueIDMap.get(uid).equals(dataClass)){
                throw new IllegalStateException("given ID does already exist for another mapping");
            }
        }
        serMap.put(dataClass,new SerializerEntry(serializer,uniqueID));
        uniqueIDMap.put(uid,dataClass);
        
    }

    public static void writeData(ObjectOutputStream out, ID dataID, Object data) throws IOException {
        boolean individualSerialize=serMap.containsKey(dataID);
        out.writeBoolean(individualSerialize);
        if (individualSerialize){
            SerializerEntry entry=(SerializerEntry)serMap.get(dataID);
            out.writeLong(entry.getUniqueID());
            entry.getSerializer().write(data,out);
            //delemeter?
        }else{
            out.writeObject(data);
        }
         
    }

    public static Object readData(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if(in.readBoolean()){
            long uid=in.readLong();
            DataID dataID=(DataID) uniqueIDMap.get(new Long(uid));
            if (dataID!=null){
                SerializerEntry entry=(SerializerEntry)serMap.get(dataID);
//                byte[] buf=new byte[in.readInt()];
//                in.read(buf);
                return entry.getSerializer().readData(in);
            }
            throw new IllegalStateException();
            //in.mark(1024);
            
            //return 
        }else{
            return in.readObject();
        }
        //return null;
    }





}
