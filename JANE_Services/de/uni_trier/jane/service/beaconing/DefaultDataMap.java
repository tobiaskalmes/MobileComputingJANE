/*
 * Created on 07.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.beaconing;

import de.uni_trier.jane.basetypes.*;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;


public class DefaultDataMap implements DataMap {
    
    private transient Map map;
    private transient int size;
    
    /**
     * Creates a new DataMap
     * @param map
     * @return 
     */
    public static DataMap createDataMap(Map map) {
    	return new DefaultDataMap(new HashMap(map),map.size());
    }
    
    public DefaultDataMap() {
        map = new HashMap();
        size = 0;
    }

    private DefaultDataMap(Map map, int size) {
        this.map = map;
        this.size = size;
    }

    public void set(Data dataCopy) {
       // Data dataCopy = data.copy();
    	
        Data oldData = (Data)map.put(dataCopy.getDataID(), dataCopy);
        if(oldData != null) {
        	size -= oldData.getSize();
        }
        size += dataCopy.getSize();
    }
    
    public void remove(DataID dataID) {
    	Data data = (Data)map.remove(dataID);
    	if(data != null) {
    		size -= data.getSize();
    	}
    }

    public DataMap copy() {
        Map mapCopy = new HashMap(map);
        return new DefaultDataMap(mapCopy, size);
    }

    public int getSize() {
        return size;
    }

    public boolean hasData(DataID dataID) {
        return map.containsKey(dataID);
    }
    
    public Data getData(DataID dataID) {
        Data data = (Data)map.get(dataID);
        if(data == null) {
        	return null;
        }
        return data;//.copy();
    }

    public Data[] getData() {
//    	ArrayList result = new ArrayList();
//    	Iterator iterator = map.values().iterator();
//    	while(iterator.hasNext()) {
//    		Data data = (Data)iterator.next();
//    		result.add(data.copy());
//    	}
//        return (Data[])result.toArray(new Data[result.size()]);
    	return (Data[])map.values().toArray(new Data[map.size()]);
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        write(out);
        
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        map=new HashMap();
        read2(in);
        
    }

    /**
     * TODO: comment method 
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void read2(ObjectInputStream in) throws IOException, ClassNotFoundException {
        int mapSize=in.readInt();
        for (int i=0;i<mapSize;i++){
            Data data=(Data)DataMapper.readData(in);
            map.put(data.getDataID(),data);
        }
    }
   

    public void write(ObjectOutputStream out) throws IOException{
        Iterator iterator=map.entrySet().iterator();
        out.writeInt(map.size());
        while (iterator.hasNext()){
            Entry entry=(Entry)iterator.next();
            
            DataMapper.writeData(out,(DataID) entry.getKey(),(Data)entry.getValue());
            //out.writeInt(bs.length);
            
            //out.write(bs);
        }
        
    }

    public static DataMap read(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        DefaultDataMap dataMap=new DefaultDataMap();
        dataMap.read2(ois);
        return dataMap;
    }

	
	public boolean equals(Object object) {
		if (object instanceof DataMap) {
			DataMap dataMap = (DataMap) object;
			Data[] datas=dataMap.getData();
			for(int i=0;i<datas.length;i++) {
				DataID dataID=datas[i].getDataID();
				if(map.containsKey(dataID)) {
					Data data=(Data)map.get(dataID);
					if(data==null)
						return false;
					if(!data.equals(datas[i]))
						return false;
				}else {
					return false;
				}
			}
			return true;
		}
		return false;
	}
    
   

}