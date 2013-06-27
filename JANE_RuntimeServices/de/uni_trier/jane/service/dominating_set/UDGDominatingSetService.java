/*****************************************************************************
 * 
 * UDGDominatingSetService.java
 * 
 * $Id: UDGDominatingSetService.java,v 1.1 2007/06/25 07:24:01 srothkugel Exp $
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
package de.uni_trier.jane.service.dominating_set;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.beaconing.*;
import de.uni_trier.jane.service.link_layer.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.*;
import de.uni_trier.jane.service.neighbor_discovery.filter.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;
import de.uni_trier.jane.simulation.parametrized.parameters.base.*;
import de.uni_trier.jane.simulation.parametrized.parameters.service.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.deprecated_shapes.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * According to the algorithm applied in "Internal node and shortcut based
 * routing with guaranteed delivery in wireless networks, Susanta Datta, Ivan
 * Stojmenovic, and Jie Wu" this class implements a localized dominating set
 * construction based on the unit disk graph assumption (a network where each
 * node has a unique sending radius R). Dominating set construction follows the
 * following rules.
 * 
 * A node u is termed an intermediate node if it has two one hop neighbors v and
 * w which are not connected. When applied with no further reduction rules, the
 * dominating set is defined exactly by all intermediate nodes. The size of the
 * dominating set may be reduced by applying the followuing two rules.
 * 
 * Rule 1: Each intermediate node u is removed from the dominating set if there
 * exists a neighbor node v of u (which is an intermediate node) and which
 * satisfies N(u) <= N(v) and id(u) < id(v).
 * 
 * Rule 2: Each remaining intermediate node is removed from the dominating set
 * if there exist two neighbor nodes v and w which are intermediate nodes and
 * which satisfy N(u) <= N(v) + N(w) and id(u) = min{id(u),id(v),id(w)}
 * 
 * @author Hannes Frey
 */
public class UDGDominatingSetService implements RuntimeService, DominatingSetService, NeighborDiscoveryListener {

	/**
	 * The CVS version number of this class.
	 */
	public static final String VERSION = "$Id: UDGDominatingSetService.java,v 1.1 2007/06/25 07:24:01 srothkugel Exp $";
	
    private static final BooleanParameter USE_RULE_1 = new BooleanParameter("useRule1", true);
    private static final BooleanParameter USE_RULE_2 = new BooleanParameter("useRule2", true);

	public static final IdentifiedServiceElement INITIALIZER = new IdentifiedServiceElement("udgDominatingSet") {
		public void createInstance(ServiceID ownServiceID, InitializationContext initializationContext, ServiceUnit serviceUnit) {
			ServiceID linkLayerID = LinkLayerBase.REQUIRED_SERVICE.getInstance(serviceUnit, initializationContext);
			ServiceID neighborDiscoveryID = GenericNeighborDiscoveryService.REQUIRED_SERVICE.getInstance(serviceUnit, initializationContext);
			boolean useRule1 = USE_RULE_1.getValue(initializationContext);
			boolean useRule2 = USE_RULE_2.getValue(initializationContext);
			Service service = new UDGDominatingSetService(ownServiceID, linkLayerID, neighborDiscoveryID, useRule1, useRule2);
			serviceUnit.addService(service);
		}
		public Parameter[] getParameters() {
			return new Parameter[] { LinkLayerBase.REQUIRED_SERVICE,
					GenericNeighborDiscoveryService.REQUIRED_SERVICE,
					USE_RULE_1, USE_RULE_2 };
		}
	};

	public static final ServiceElement SERVICE_ELEMENT = new ServiceElement("udgDominatingSet") {
		public void createInstance(InitializationContext initializationContext, ServiceUnit serviceUnit) {
			boolean useRule1 = USE_RULE_1.getValue(initializationContext);
			boolean useRule2 = USE_RULE_2.getValue(initializationContext);
			UDGDominatingSetService.createInstance(serviceUnit, useRule1, useRule2);
		}
		public Parameter[] getParameters() {
			return new Parameter[] { USE_RULE_1, USE_RULE_2 };
		}
	};

    public static void createInstance(ServiceUnit serviceUnit) {
    	createInstance(serviceUnit, true, true);
    }

    public static void createInstance(ServiceUnit serviceUnit, boolean useRule1, boolean useRule2) {
    	ServiceID linkLayerService = serviceUnit.getService(LinkLayer.class);
    	
    	if(!serviceUnit.hasService(NeighborDiscoveryService_sync.class)) {
    		OneHopNeighborDiscoveryService.createInstance(serviceUnit);
    	}
    	ServiceID neighborDiscoveryService = serviceUnit.getService(NeighborDiscoveryService_sync.class);
    	
    	if(!serviceUnit.hasService(LocationDataDisseminationService.class)) {
    		LocationDataDisseminationService.createInstance(serviceUnit);
    	}
    	Service dominatingSetService = new UDGDominatingSetService(new EndpointClassID(UDGDominatingSetService.class.getName()), linkLayerService, neighborDiscoveryService, useRule1, useRule2);
		serviceUnit.addService(dominatingSetService);
    }

    // initialized in constructor
    private ServiceID ownServiceID;
    private ServiceID linkLayerServiceID;
    private ServiceID neighborDiscoveryServiceID;
    private boolean useRule1;
    private boolean useRule2;
    private boolean membership;
    private boolean intermediate;
    
    private Color color = Color.BLUE;

    // initialized on startup
    private NeighborDiscoveryServiceStub neighborDiscoveryServiceStub;
    private DominatingSetListenerStub dominatingSetListenerStub;
    private Shape memberShape;
    private Address linkLayerAddress;
    private double sendingRange;

    /**
     * Construct a new dominating set construction service
     * @param linkLayerServiceID the ID of a link layer providing the UDG property
     * @param neighborDiscoveryServiceID the ID of the neighbor discovery service
     * @param useRule1 if <code>true</code> rule 1 is applied in order to reduce the dominating set
     * @param useRule2 if <code>true</code> rule 2 is applied in order to reduce the dominating set
     */
    public UDGDominatingSetService(ServiceID ownServiceID, ServiceID linkLayerServiceID, ServiceID neighborDiscoveryServiceID, boolean useRule1, boolean useRule2) {
    	this.ownServiceID = ownServiceID;
    	this.linkLayerServiceID = linkLayerServiceID;
    	this.neighborDiscoveryServiceID = neighborDiscoveryServiceID;
    	this.useRule1 = useRule1;
    	this.useRule2 = useRule2;
        membership = false;
        intermediate = false;
    }

    public ServiceID getServiceID() {
        return ownServiceID;
    }

    public void start(RuntimeOperatingSystem runtimeOperatingSystem) {
    	
    	// determine the own link layer address and UDG radius
    	UDGLinkLayer linkLayer = (UDGLinkLayer)runtimeOperatingSystem.getAccessListenerStub(
    			linkLayerServiceID, UDGLinkLayer.class);
    	sendingRange = linkLayer.getUDGRadius();
    	linkLayerAddress = linkLayer.getNetworkAddress();

    	// create the required service stubs
    	neighborDiscoveryServiceStub = new NeighborDiscoveryServiceStub(
    			runtimeOperatingSystem, neighborDiscoveryServiceID);
    	dominatingSetListenerStub = new DominatingSetListenerStub(runtimeOperatingSystem);

    	// register as signal listener
    	runtimeOperatingSystem.registerAtService(neighborDiscoveryServiceID, NeighborDiscoveryService_sync.class);

    	// check if the neighbor discovery service stores our own location information
    	NeighborDiscoveryProperties neighborDiscoveryProperties =
    		neighborDiscoveryServiceStub.getNeighborDiscoveryProperties();
    	if(!neighborDiscoveryProperties.isNotifyAboutOwnChanges()) {
    		throw new ServiceException("This service assumes that the neighbor discovery " +
    				"service notifies about own changes."); // TODO ist diese notifikation wirklich notwendig???
    	}

    	memberShape = new RelativeCrossShape(runtimeOperatingSystem.getDeviceID(), 6.0, color, true);

    }

    public void finish() {
        // ignore
    }

    public Shape getShape() {
        if(membership) {
            return memberShape;
        }
        return null;
    }

	public void getParameters(Parameters parameters) {
		parameters.addParameter("useRule1", useRule1);
		parameters.addParameter("useRule2", useRule2);
	}

	public Boolean isMember() {
		return new Boolean(membership);
	}

	public void setNeighborData(NeighborDiscoveryData neighborData) {
		updateMembership();
	}

	public void updateNeighborData(NeighborDiscoveryData neighborData) {
		if(!neighborData.getSender().equals(linkLayerAddress)) {
			updateMembership();
		}
	}

	public void removeNeighborData(Address linkLayerAddress) {
		updateMembership();
	}

	// update all data after a neighbor has been changed
    private void updateMembership() {

        // get the open set of one-hop neighbors which have location data
        NeighborDiscoveryData neighbors[] = neighborDiscoveryServiceStub.
			getNeighborDiscoveryData(TypedDataFilter.OPEN_ONE_HOP_NEIGHBOR_LOCATION_FILTER);

        // check if we are a domintaing set neighbor due to the intermediate node property
        boolean newIntermediate = checkIntermediate(neighbors);
        boolean newMembership = newIntermediate;

        // check if we remain in the dominating set according to rule 1 and 2 (if applied)
        if(newMembership) {
        	newMembership = useRule1 ? !checkRule1Removal(neighbors) : true;
        	if(newMembership) {
        		newMembership = useRule2 ? !checkRule2Removal(neighbors) : true;
        	}
        }

		// update intermediate state and possibly set new neighbor discovery data
		if(intermediate != newIntermediate) {
			intermediate = newIntermediate;
			Data data = new IntermediateData(intermediate);
			neighborDiscoveryServiceStub.setOwnData(data);
		}

        // notify all listeners when dominating set membership has been changed
        if(newMembership != membership) {
            membership = newMembership;
            dominatingSetListenerStub.updateMembership(membership);
        }

    }

    // Check if the current node u is an intermediate node, i.e. there exists two
    // neighbor nodes v and w in N(u) which are not connected.
    private boolean checkIntermediate(NeighborDiscoveryData[] oneHopNeighbors) {
        for (int i = 0; i < oneHopNeighbors.length; i++) {
			Position pos_v = LocationData.getPosition(oneHopNeighbors[i]);
			for (int j = i + 1; j < oneHopNeighbors.length; j++) {
				Position pos_w = LocationData.getPosition(oneHopNeighbors[j]);
				if (pos_v.distance(pos_w) > sendingRange) {
					return true;
				}
			}
		}
        return false;
    }

	// Check if the current node has to be removed from the dominating set
    // due to rule 1.
    private boolean checkRule1Removal(NeighborDiscoveryData[] oneHopNeighbors) {
		for (int i = 0; i < oneHopNeighbors.length; i++) {
			NeighborDiscoveryData v = oneHopNeighbors[i];
			if(IntermediateData.isIntermediateNode(v)) {
				Position pos_v = LocationData.getPosition(v);
				boolean subset = true;
				for (int j = 0; j < oneHopNeighbors.length && subset; j++) {
					Position pos = LocationData.getPosition(oneHopNeighbors[j]);
					if (pos.distance(pos_v) > sendingRange) {
						subset = false;
					}
				}
				if (subset) {
					if (linkLayerAddress.compareTo(v.getSender()) < 0) {
						return true;
					}
				}
			}
		}
		return false;
    }

	// Check if the current node has to be removed from the dominating set
    // due to rule 2.
    private boolean checkRule2Removal(NeighborDiscoveryData[] oneHopNeighbors) {
		for (int i = 0; i < oneHopNeighbors.length; i++) {
			NeighborDiscoveryData v = oneHopNeighbors[i];
			if(IntermediateData.isIntermediateNode(v)) {
				Position pos_v = LocationData.getPosition(v);
				for (int j = i + 1; j < oneHopNeighbors.length; j++) {
					NeighborDiscoveryData w = oneHopNeighbors[j];
					if(IntermediateData.isIntermediateNode(w)) {
						Position pos_w = LocationData.getPosition(w);
						if(pos_v.distance(pos_w) <= sendingRange) {
							boolean subset = true;
							for (int k = 0; k < oneHopNeighbors.length && subset; k++) {
								Position pos = LocationData.getPosition(oneHopNeighbors[k]);
								if(pos.distance(pos_v) > sendingRange && pos.distance(pos_w) > sendingRange) {
									subset = false;
								}
							}
							if (subset && linkLayerAddress.compareTo(v.getSender()) < 0 && linkLayerAddress.compareTo(w.getSender()) < 0) {
								return true;
							}
						}
					}
				}
			}
		}
    	return false;
    }

    // used internally in order to disseminate intermediate node information to all one hop neighbors
    private static class IntermediateData implements Data {
    	
    	private static DataID DATA_ID = new ClassDataID(IntermediateData.class);
    	
    	private static boolean isIntermediateNode(NeighborDiscoveryData data) {
    		IntermediateData intermediateData = (IntermediateData)data.getDataMap().getData(DATA_ID);
    		if(intermediateData == null) {
    			return false;
    		}
    		return intermediateData.isIntermediateNode();
    	}
    	
    	private boolean intermediateNode;
    	
    	private IntermediateData(boolean intermediateNode) {
    		this.intermediateNode = intermediateNode;
    	}

		private boolean isIntermediateNode() {
			return intermediateNode;
		}

		public DataID getDataID() {
			return DATA_ID;
		}

		public int getSize() {
			return 1;
		}

    }

}
