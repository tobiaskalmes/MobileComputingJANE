/*****************************************************************************
 * 
 * DefaultDataSerializer.java
 * 
 * $Id: DefaultDataSerializer.java,v 1.1 2007/06/25 07:24:16 srothkugel Exp $
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

import java.io.*;
import java.util.HashMap;
import java.util.zip.*;




/**
 * @author goergen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DefaultDataSerializer implements DataSerializer {

    private static class SerializerEntry {

        private MessageSerializer serializer;
        private int uniqueID;

        public SerializerEntry(MessageSerializer serializer, int uniqueID) {
            this.serializer=serializer;
            this.uniqueID=uniqueID;
        }
        
        public MessageSerializer getSerializer() {
            return serializer;
        }
        
        public int getUniqueID() {
            return uniqueID;
        }
        
        


    }

    private static HashMap serMap;
    private static HashMap uniqueIDMap;

    static{
        serMap=new HashMap();
        uniqueIDMap=new HashMap();
        
    }
    
    
    
    public static void map(Class messageClass,int uniqueID,MessageSerializer serializer){
        Integer uid=new Integer(uniqueID);
        if (uniqueIDMap.containsKey(uid)){
            if (!uniqueIDMap.get(uid).equals(messageClass)){
                throw new IllegalStateException("given ID does already exist for another mapping");
            }
        }
        serMap.put(messageClass,new SerializerEntry(serializer,uniqueID));
        uniqueIDMap.put(uid,messageClass);
    }
    
    
    
    public byte[] getData(LinkLayerMessage message) throws DataSerializerException{
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
        //OutputStream dos=baos;
		DeflaterOutputStream dos=new DeflaterOutputStream(baos);
		try {
            ObjectOutputStream oos=new ObjectOutputStream(dos);
            writeMessage(message, oos);
            dos.finish();
        } catch (IOException e) {
          throw new DataSerializerException(e.getMessage());
        }
		return baos.toByteArray();
	}

    /**
     * TODO: comment method 
     * @param message
     * @param oos
     * @throws IOException
     */
    private void writeMessage(LinkLayerMessage message, ObjectOutputStream oos) throws IOException {
        boolean individual=serMap.containsKey(message.getClass());
        oos.writeBoolean(individual);
        if (individual){
            SerializerEntry entry=(SerializerEntry)serMap.get(message.getClass());
            oos.writeInt(entry.getUniqueID());
            entry.getSerializer().writeMessage(oos,message);
            oos.close();
           
        }else{
            oos.writeObject(message);
        }
    }
    

    /**
     * TODO: comment method 
     * @param ois
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private LinkLayerMessage readMessage(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        if (ois.readBoolean()){
            int uuid=ois.readInt();
            SerializerEntry entry=(SerializerEntry)serMap.get(uniqueIDMap.get(new Integer(uuid)));
            return entry.getSerializer().readMessage(ois);
            //return null;
                
            
        }
        return (LinkLayerMessage)ois.readObject();
    }
    
    public LinkLayerMessage getMessage(byte[] data) throws DataSerializerException {
    	try {
            ObjectInputStream ois=new ObjectInputStream(
                    new InflaterInputStream(
                            new ByteArrayInputStream(data)
                    )
            );
            return readMessage(ois);
        } catch (IOException e) {
            throw new DataSerializerException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new DataSerializerException(e.getMessage());
        }
	}


}
