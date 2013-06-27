package de.uni_trier.jane.service.planarizer.dp;
/*****************************************************************************
* 
* DirectPlanarizer.java
* 
* $Id: DirectPlanarizer.java,v 1.1 2007/06/25 07:24:01 srothkugel Exp $
*
***********************************************************************
*  
* JANE - The Java Ad-hoc Network simulation and evaluation Environment
*
***********************************************************************
*
* Copyright (C) 2002-2006 
* Hannes Frey and Daniel Goergen and Johannes K. Lehnert
* Systemsoftware and Distrubuted Systems
* University of Trier 
* Germany
* http://syssoft.uni-trier.de/jane
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
import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.*;
import de.uni_trier.jane.service.neighbor_discovery.views.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.util.*;

public class DirectPlanarizer extends AbstractPlanarizerService implements NeighborDiscoveryListener {

	private Map alreadyConsidered;
	private PlanarGraphNode planarGraphNode;

	private LocationDataView locationDataView;

	public static void createInstance(ServiceUnit serviceUnit){
		if(!serviceUnit.hasService(NeighborDiscoveryService_sync.class)){
			TwoHopNeighborDiscoveryService.createInstance(serviceUnit,true,false);
		}
		ServiceID neighborDiscoveryServiceID = serviceUnit.getService(NeighborDiscoveryService_sync.class);
		Service planarizerService = new DirectPlanarizer(neighborDiscoveryServiceID);
		serviceUnit.addService(planarizerService);
	}

	public DirectPlanarizer(ServiceID neighborDiscoveryServiceID) {
		super(neighborDiscoveryServiceID);
		planarGraphNode = null;
		alreadyConsidered = new HashMap();
	}

	public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
		super.start(runtimeOperatingSystem);
		locationDataView = new LocationDataView(neighborDiscoveryService);
		//neighborDiscoveryService.registerAtService();
	}

	public void finish() {
//		neighborDiscoveryServiceStub.unregisterAtService(); // TODO NeighborDiscoveryService wurde vorher schon abgeschossen!
		super.finish();
	}

	public PlanarGraphNode getPlanarGraphNode() {

		if(planarGraphNode != null) {
			return planarGraphNode;
		}
		
		Address a = neighborDiscoveryService.getOwnAddress();

		if(!locationDataView.hasPosition(a)) {
			// TODO keine eigene Positionsinformation
		}

		List allOneHopNeighborsList = new ArrayList();
		Map addressPlanarNodeMap = new HashMap();

		Position ap = locationDataView.getPosition(a);
		
		Address[] na = neighborDiscoveryService.getNeighbors(
				NeighborDiscoveryFilter.ONE_HOP_NEIGHBOR_FILTER);
		for(int i=0; i<na.length; i++) {
			Address b = na[i];
			if(!b.equals(a) && locationDataView.hasPosition(b)) {
				Position bp = locationDataView.getPosition(b);

				PlanarGraphNodeImpl node = new PlanarGraphNodeImpl(b, bp);
				allOneHopNeighborsList.add(node);

				if(isPlanarConnected(a, ap, b, bp)) {
					addressPlanarNodeMap.put(b, node);
				}
			}
		}

		PlanarGraphNode[] allOneHopNeighbors =
			(PlanarGraphNode[])allOneHopNeighborsList.toArray(
					new PlanarGraphNode[allOneHopNeighborsList.size()]);

		planarGraphNode = new PlanarGraphNodeImpl(a, ap, addressPlanarNodeMap, allOneHopNeighbors);
		
		return planarGraphNode;

	}

	private boolean isPlanarConnected(Address a, Position ap, Address b, Position bp) {
		Address[] na = neighborDiscoveryService.getNeighbors(
				NeighborDiscoveryFilter.ONE_HOP_NEIGHBOR_FILTER);
		
		for(int i=0; i<na.length; i++) {
			Address c = na[i];
			if(!c.equals(b) && !c.equals(a) && locationDataView.hasPosition(c)) {
				Position cp = locationDataView.getPosition(c);
				Address[] nc = neighborDiscoveryService.getNeighborNodes(c);
				for(int j=0; j<nc.length; j++) {
					Address d = nc[j];
					if(!d.equals(c) && !d.equals(b) && !d.equals(a) && locationDataView.hasPosition(d)) {
						Position dp = locationDataView.getPosition(d);
						if(GeometryCalculations.checkIntersect(ap,bp,cp,dp)) {
							double x = Math.max(GeometryCalculations.getAngle(ap,cp,bp),
									GeometryCalculations.getAngle(ap,dp,bp));
							double y = Math.max(GeometryCalculations.getAngle(cp,ap,dp),
									GeometryCalculations.getAngle(cp,bp,dp));
							if(x > y || (x==y && GeometryCalculations.lessThan(ap, bp, cp, dp))) {
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}

	private class PlanarGraphNodeImpl implements PlanarGraphNode {

		private Address address;
		private Position position;
		private Map addressNodeMap;
		private NetworkNode[] allOneHopNeighbors;
		private PlanarGraphNode[] adjacentNodes;
		
		public PlanarGraphNodeImpl(Address address, Position position) {
			this(address, position, null, null);
		}
		
		public PlanarGraphNodeImpl(Address address, Position position,
				Map addressNodeMap, NetworkNode[] allOneHopNeighbors) {
			this.address = address;
			this.position = position;
			this.addressNodeMap = addressNodeMap;
			this.allOneHopNeighbors = allOneHopNeighbors;
			if(addressNodeMap != null) {
				Collection values = addressNodeMap.values();
				adjacentNodes = (PlanarGraphNode[])values.toArray(
						new PlanarGraphNode[values.size()]);
			}
			else {
				adjacentNodes = new PlanarGraphNode[0];
			}
		}

		public PlanarGraphNode[] getAdjacentNodes() {
			return adjacentNodes;
		}

		public boolean hasAdjacentNodes() {
			return adjacentNodes.length > 0;
		}

		public PlanarGraphNode getAdjacentNode(Address address) {
			return (PlanarGraphNode)addressNodeMap.get(address);
		}

		public NetworkNode[] getAllOneHopNeighbors() {
			return allOneHopNeighbors;
		}

		public boolean isStopNode() {
			return isOneHopNeighbor();
		}

		public Address getAddress() {
			return address;
		}

		public Position getPosition() {
			return position;
		}

		public boolean isOneHopNeighbor() {
			return addressNodeMap == null;
		}

		public boolean isVirtual() {
			return false;
		}

		public NetworkNode getRelayNode() {
			return this;
		}
		
	}

	public void setNeighborData(NeighborDiscoveryData neighborData) {
		alreadyConsidered.put(neighborData.getSender(), getPosition(neighborData));
		planarGraphNode = null;
	}

	public void updateNeighborData(NeighborDiscoveryData neighborData) {
		Address neighbor = neighborData.getSender();
		Position oldPosition = (Position)alreadyConsidered.get(neighbor);
		Position newPosition = getPosition(neighborData);
		if(oldPosition == null && newPosition == null) {
			return;
		}
		if(oldPosition == null || !oldPosition.equals(newPosition) || neighborData.neighborSetChanged()) {
			alreadyConsidered.put(neighbor, newPosition);
			planarGraphNode = null;
		}
	}

	public void removeNeighborData(Address linkLayerAddress) {
		alreadyConsidered.remove(linkLayerAddress);
		planarGraphNode = null;
	}

	private Position getPosition(NeighborDiscoveryData neighborData) {
		LocationData locationData =
			(LocationData)neighborData.getDataMap().getData(LocationData.DATA_ID);
		if(locationData != null) {
			return locationData.getPosition();
		}
		return null;
	}

}
