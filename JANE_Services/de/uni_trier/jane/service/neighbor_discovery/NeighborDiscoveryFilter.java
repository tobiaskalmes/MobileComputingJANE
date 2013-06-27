/*
 * Created on 23.02.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.neighbor_discovery;

/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface NeighborDiscoveryFilter {

	public static final NeighborDiscoveryFilter ONE_HOP_NEIGHBOR_FILTER = new NeighborDiscoveryFilter() {
		public boolean matches(NeighborDiscoveryData neighborData) {
			return neighborData.getHopDistance() == 1;
		}
	};
	
	public static final NeighborDiscoveryFilter TOW_HOP_NEIGHBOR_FILTER=new NeighborDiscoveryFilter() {

		public boolean matches(NeighborDiscoveryData neighborData) {
			return neighborData.getHopDistance()==2;
		}
		
	};
	
	public boolean matches(NeighborDiscoveryData neighborData);
	
}
