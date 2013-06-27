/**
 * 
 */
package de.uni_trier.jane.service.routing.gcr.topology;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.beaconing.*;

public abstract class OneHopClusterTable implements Data {
	
	private Address hostCluster;
	private Set oneHopClusterSet;
	private int requiredBits;
	
	public OneHopClusterTable(Address hostCluster, Set oneHopClusterSet, int requiredBits) {
		this.hostCluster = hostCluster;
		this.oneHopClusterSet = oneHopClusterSet;
		this.requiredBits = requiredBits;
	}

	public Address getHostCluster() {
		return hostCluster;
	}

	public Address[] getOneHopClusters() {
		return (Address[])oneHopClusterSet.toArray(new Address[oneHopClusterSet.size()]);
	}

	public Collection getOneHopClusterSet() {
		return Collections.unmodifiableCollection(oneHopClusterSet);
	}

	public boolean contains(Address clusterAddress) {
		return oneHopClusterSet.contains(clusterAddress);
	}
	
	public int getSize() {
		return hostCluster.getCodingSize() + requiredBits;
	}

	public boolean differsFrom(OneHopClusterTable other) {
		if(!hostCluster.equals(other.hostCluster)) {
			return true;
		}
		if(!oneHopClusterSet.equals(other.oneHopClusterSet)) {
			return true;
		}
		return false;
	}
	
}