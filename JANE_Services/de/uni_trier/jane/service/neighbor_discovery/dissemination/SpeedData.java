/*
 * Created on 19.05.2005
 */
package de.uni_trier.jane.service.neighbor_discovery.dissemination;

import java.io.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.beaconing.DataMapper;
import de.uni_trier.jane.service.beaconing.DataSerializer;
import de.uni_trier.jane.service.neighbor_discovery.*;

/**
 * Wrapper class to provide the beacon service with the current speed vector
 * of a device. These are disseminated by LocationAndSpeedDataDisseminationService
 * 
 * @author Christoph Lange
 * @see de.uni_trier.jane.service.neighbor_discovery.dissemination.LocationAndSpeedDataDisseminationService
 */
public class SpeedData implements Data {
    private static final long serialVersionUID = -7549467177723500301L;

    public static final DataID DATA_ID = new ClassDataID(SpeedData.class);
    static void map() {
        DataMapper.map(SpeedData.class, 1024, new DataSerializer() {

            public Object readData(ObjectInputStream in) throws IOException {
                return new SpeedData(new Position(in.readDouble(), in
                        .readDouble()));
            }

            public void write(Object data, ObjectOutputStream out)
                    throws IOException {
                SpeedData locationData = (SpeedData) data;
                out.writeDouble(locationData.getSpeedVector().getX());
                out.writeDouble(locationData.getSpeedVector().getY());

            }
        });
    }
    static{
        map();
    }
    
    
    
    public static Position getSpeed(NeighborDiscoveryData data) {
    	SpeedData speedData = (SpeedData)data.getDataMap().getData(DATA_ID);
    	if(speedData == null) {
    		return null;
    	}
    	return speedData.getSpeedVector();
    }

    /**
     * the current speed vector
     */
    private Position speedVector;
    
	/**
	 * @param speedVector the speed vector to be disseminated
	 */
	public SpeedData(Position speedVector) {
		this.speedVector = speedVector;
	}
	
	public DataID getDataID() {
		return DATA_ID;
	}

	public Data copy() {
		return this;
	}

	public int getSize() {
		return 32; // as in LocationData.getSize
	}

	/**
	 * @return the speed vector
	 */
	public Position getSpeedVector() {
		return speedVector;
	}


	public boolean equals(Object arg0) {
		if (arg0 instanceof SpeedData) {
			SpeedData speedData = (SpeedData) arg0;
            if (speedData.getSpeedVector()==null){
                return speedVector==null;
            }
			return speedData.getSpeedVector().equals(speedVector);
		}
		return false;
	}
	
}
