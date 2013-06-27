package de.uni_trier.jane.service.neighbor_discovery;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.beaconing.*;

/**
 * This implementation of the interface PropagationStateInterface handles information propagation
 * in an intelligent way. 
 * @author Stefan Peters
 *
 */
class IntelligentPropagation implements PropagationStateInterface  {  
	
	private int propagateCount;
	private int periodic;
	private int count;
	private int currentCount;
	private DataMap oldDataMap;
	private boolean reseted=false;
	private Set previousNeighbors;
	
	/**
	 * 
	 * @param propagateCount The number 
	 * @param periodic Sets the 
	 */
	public IntelligentPropagation(int propagateCount, int periodic) {
		this.propagateCount = propagateCount;
		this.periodic=periodic;
		count=1;
		currentCount=propagateCount;
	}

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.neighbor_discovery.PropagateStateInterface#isChanged(de.uni_trier.jane.service.beaconing.DataMap, de.uni_trier.jane.basetypes.Address[])
	 */
	public boolean propagateData(DataMap dataMap,Address[] neighbors) {
		if(oldDataMap==null) {
			dekrement();
			oldDataMap=dataMap.copy();
			previousNeighbors=new HashSet(Arrays.asList(neighbors));
			return true;
		}
		Set neighborsSet=new HashSet(Arrays.asList(neighbors));
		if(!dataMap.equals(oldDataMap)&& neighborsSet.equals(previousNeighbors)) {
			reset(); // data has changed, reset propagation counter
			count=1;
		}
		if(!wasReseted())
			dekrement();
		oldDataMap=dataMap.copy();// Muss kopiert werden, ansonsten aendert sich die Map "nie"
		previousNeighbors=neighborsSet;
		if(currentCount<0) {
			count++;
		}
		if(count==periodic) {
			count=1;
			return true;
		}
		return currentCount>=0;
	}
	
	private void dekrement() {
		currentCount--;
	}
	
	private void reset() {
		currentCount=propagateCount;
		reseted=true;
	}
	
	private boolean wasReseted() {
		if(reseted) {
			reseted=false;
			return true;
		}
		return false;
	}
}