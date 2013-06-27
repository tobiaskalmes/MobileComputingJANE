/**
 * 
 */
package de.uni_trier.jane.service.routing.gcr.topology;

import java.io.*;
import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.beaconing.*;
import de.uni_trier.jane.service.neighbor_discovery.*;

public class ReachableClusterTable implements Data {
	
	private static final long serialVersionUID = -8493860252518546959L;
    public static final DataID DATA_ID = new ClassDataID(ReachableClusterTable.class);
    static{
        map();
    }

    /**
     * TODO Comment method
     */
    public static void map() {
        DataMapper.map(ReachableClusterTable.class,serialVersionUID,new DataSerializer() {
        
            public Object readData(ObjectInputStream in) throws IOException,
                    ClassNotFoundException {
                
                return new ReachableClusterTable(
                        in.readLong(),
                        (Address)in.readObject(),
                        (Set)in.readObject(),
                        (Set)in.readObject(),
                        0);
                
            }
        
            public void write(Object data, ObjectOutputStream out) throws IOException {
                ReachableClusterTable table=(ReachableClusterTable)data;
                out.writeLong(table.clockValue);
                out.writeObject(table.hostCluster);
                out.writeObject(table.oneHopClusterSet);
                out.writeObject(table.twoHopClusterSet);
                
        
            }
        
        });
    }

	public static ReachableClusterTable fromNeighborDiscoveryData(NeighborDiscoveryData data) {
		return (ReachableClusterTable)data.getDataMap().getData(DATA_ID);
	}
	
	

	private long clockValue;
	private Address hostCluster;
	private Set oneHopClusterSet;
	private Set twoHopClusterSet;
	private int requiredBits;

	public String toString() {
		return "(hostCluster=" + hostCluster + ", oneHopClusters=" + oneHopClusterSet +
		", twoHopClusters=" + twoHopClusterSet + ", requiredBits=" + requiredBits + ")";
	}
	public ReachableClusterTable(long clockValue, Address hostCluster, Set oneHopClusterSet, Set twoHopClusterSet, int requiredBits) {
		this.clockValue = clockValue;
		this.hostCluster = hostCluster;
		this.oneHopClusterSet = oneHopClusterSet;
		this.twoHopClusterSet = twoHopClusterSet;
		this.requiredBits = requiredBits;
	}

	// get the number of hops this cluster can be reached by this node
	// return int max value if the cluster is not reachable
	public int getHopDistance(Address cluster) {
		if(cluster.equals(hostCluster)) {
			return 0;
		}
		if(oneHopClusterSet.contains(cluster)) {
			return 1;
		}
		if(twoHopClusterSet.contains(cluster)) {
			return 2;
		}
		return Integer.MAX_VALUE;
	}

	public Address getHostCluster() {
		return hostCluster;
	}

	public long getClockValue() {
		return clockValue;
	}
	
	public Address[] getOneHopClusters() {
		return (Address[])oneHopClusterSet.toArray(new Address[oneHopClusterSet.size()]);
	}

	public Address[] getTwoHopClusters() {
		return (Address[])twoHopClusterSet.toArray(new Address[twoHopClusterSet.size()]);
	}

	public Set getOneHopClusterSet() {
		return Collections.unmodifiableSet(oneHopClusterSet);
	}

	public Set getTwoHopClusterSet() {
		return Collections.unmodifiableSet(twoHopClusterSet);
	}

	public DataID getDataID() {
		return DATA_ID;
	}

	public int getSize() {
		return hostCluster.getCodingSize() + requiredBits;
	}

	public boolean differsFrom(ReachableClusterTable other) {
		if(!hostCluster.equals(other.hostCluster)) {
			return true;
		}
		if(!oneHopClusterSet.equals(other.oneHopClusterSet)) {
			return true;
		}
		if(!twoHopClusterSet.equals(other.twoHopClusterSet)) {
			return true;
		}
		return false;
	}
	
}