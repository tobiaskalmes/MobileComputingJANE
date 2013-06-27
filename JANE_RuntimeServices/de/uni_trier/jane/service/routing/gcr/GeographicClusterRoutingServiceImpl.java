// TODO Exportieren der Service-Interfaces sollte automatisch passieren
// TODO Die Trennung zwischen synchron und asynchron sollte über annotierte Methoden geschehen
// TODO Der erzeugte Stub könnte alle Interfaces implementieren; damit ist die Angabe der Klasse
//		bei Erzeugung des Stubs nicht mehr notwendig
// TODO Die Erzeugung des richtigen Headers sollte in der handleDelegate Methode passieren;
//		damit erspart man sich den Zoo von verschiedenen Header-Erzeuger-Interfaces
// TODO OS-Einträge bzgl. Listener-Stub automatisch bei finalize entfernen. Damit wird das ganze
//      RegisterOneshotListener-Geraffel unnötig.
// TODO Conditions sollten kopierbar sein
package de.uni_trier.jane.service.routing.gcr;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.location_directory.*;
import de.uni_trier.jane.service.neighbor_discovery.*;
import de.uni_trier.jane.service.neighbor_discovery.dissemination.*;
import de.uni_trier.jane.service.network.link_layer.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.*;
import de.uni_trier.jane.service.routing.face.conditions.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;
import de.uni_trier.jane.service.routing.gcr.topology.*;
import de.uni_trier.jane.service.routing.greedy.*;
import de.uni_trier.jane.service.routing.positionbased.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;
import de.uni_trier.jane.simulation.parametrized.parameters.base.*;
import de.uni_trier.jane.simulation.parametrized.parameters.service.*;
import de.uni_trier.jane.visualization.shapes.*;


public class GeographicClusterRoutingServiceImpl extends AbstractPositionBasedRoutingAlgorithm implements GeographicClusterRoutingAlgorithm {

	// TODO TEST
	private static boolean ENERGY_TEST = false;
	
	private static final ServiceIDParameter RECOVERED_ROUTING_ALGORITM_ID = new ServiceIDParameter("");
	
	
	public static final IdentifiedServiceElement INITIALIZER = new IdentifiedServiceElement("geographicClusterRoutingAlgorithm") {
		public void createInstance(ServiceID ownServiceID, InitializationContext initializationContext, ServiceUnit serviceUnit) {
			
			// get (and possibly create) all required services
			ServiceID clusterDiscoveryID = ClusterDiscoveryBase.DEFAULT.getInstance(serviceUnit, initializationContext);
			ServiceID clusterPlanarizerID = ClusterPlanarizerBase.getInstance(serviceUnit, initializationContext);
			ServiceID locationDirectoryID = LocationDirectoryBase.getInstance(serviceUnit, initializationContext);
			ServiceID neighborDiscoveryID = NeighborDiscoveryBase.getInstance(serviceUnit, initializationContext);
	    	if(!serviceUnit.hasService(LocationDataDisseminationService.class)) {
	    		LocationDataDisseminationService.createInstance(serviceUnit, true);
	    	}
//	    	ServiceID neighborDiscoveryID = serviceUnit.getService(NeighborDiscoveryService.class);


		}
		public Parameter[] getParameters() {
			return new Parameter[] { ClusterDiscoveryBase.DEFAULT };
		}
	};
	
	
    public static ServiceID createInstance(
    		ServiceUnit serviceUnit,
			StartConditionFactory startConditionFactory,
			CrossingConditionFactory crossingConditionFactory,
			TurnConditionFactory turnConditionFactory,
			ResumeGreedyConditionFactory resumeGreedyConditionFactory,
			BreakConditionFactory breakConditionFactory,
			FinishConditionFactory finishConditionFactory,
			ServiceID greedyRoutingID) {

    	// determine the required service IDs
    	if(!serviceUnit.hasService(ClusterDiscoveryService.class)) {
    		OneHopClusterDiscoveryService.createInstance(serviceUnit);
    	}
    	ServiceID clusterDiscoveryID = serviceUnit.getService(ClusterDiscoveryService.class);
    	if(!serviceUnit.hasService(GenericClusterPlanarizer.class)) { // TODO Unterscheidung zwischen NetworkPlanarizer und ClusterPlanarizer
    		ShortEdgePlanarizer.createInstance(serviceUnit);
    	}
    	ServiceID clusterPlanarizerID = serviceUnit.getService(GenericClusterPlanarizer.class);
    	ServiceID locationDirectoryID = serviceUnit.getService(LocationDirectoryService.class);
    	if(!serviceUnit.hasService(NeighborDiscoveryService.class)) {
    		OneHopNeighborDiscoveryService.createInstance(serviceUnit);
    	}
    	if(!serviceUnit.hasService(LocationDataDisseminationService.class)) {
    		LocationDataDisseminationService.createInstance(serviceUnit, true);
    	}
    	ServiceID neighborDiscoveryID = serviceUnit.getService(NeighborDiscoveryService.class);

    	
    	// add service
    	Service service = new GeographicClusterRoutingServiceImpl(
    			startConditionFactory,
    			crossingConditionFactory,
    			turnConditionFactory,
    			resumeGreedyConditionFactory,
    			breakConditionFactory,
    			finishConditionFactory,
    			clusterDiscoveryID,
    			clusterPlanarizerID,
    			locationDirectoryID,
    			neighborDiscoveryID,
    			greedyRoutingID);
		return serviceUnit.addService(service);

    }

	public static final ServiceID SERVICE_ID = new EndpointClassID(GeographicClusterRoutingServiceImpl.class.getName());

	// initialized in constructor
	private StartConditionFactory startConditionFactory;
	private CrossingConditionFactory crossingConditionFactory;
	private TurnConditionFactory turnConditionFactory;
	private ResumeGreedyConditionFactory resumeGreedyConditionFactory;
	private BreakConditionFactory breakConditionFactory;
	private FinishConditionFactory finishConditionFactory;
	private ServiceID clusterDiscoveryID;
	private ServiceID clusterPlanarizerID;
	private ServiceID greedyRoutingID;
	private PlanarGraphExplorer planarGraphExplorer;

	// initialized on demand
	private ClusterDiscoveryService clusterDiscovery;
	private ClusterPlanarizerService clusterPlanarizer;
	private PositionUnicastRoutingAlgorithm_Sync greedyRouting;

	public GeographicClusterRoutingServiceImpl(
			StartConditionFactory startConditionFactory,
			CrossingConditionFactory crossingConditionFactory,
			TurnConditionFactory turnConditionFactory,
			ResumeGreedyConditionFactory resumeGreedyConditionFactory,
			BreakConditionFactory breakConditionFactory,
			FinishConditionFactory finishConditionFactory,
			ServiceID clusterDiscoveryID,
			ServiceID clusterPlanarizerID,
			ServiceID locationDirectoryID,
			ServiceID neighborDiscoveryID,
			ServiceID greedyRoutingID) {
		super(locationDirectoryID, neighborDiscoveryID);
		this.startConditionFactory = startConditionFactory;
		this.crossingConditionFactory = crossingConditionFactory;
		this.turnConditionFactory = turnConditionFactory;
		this.resumeGreedyConditionFactory = resumeGreedyConditionFactory;
		this.breakConditionFactory = breakConditionFactory;
		this.finishConditionFactory = finishConditionFactory;
		this.clusterDiscoveryID = clusterDiscoveryID;
		this.clusterPlanarizerID = clusterPlanarizerID;
		this.greedyRoutingID = greedyRoutingID;
		planarGraphExplorer = new PlanarGraphExplorer();
	}

	public ServiceID getServiceID() {
		return SERVICE_ID;
	}

	public void finish() {
		// ignore
	}

	public Shape getShape() {
		return null;
	}

	public void getParameters(Parameters parameters) {
		// TODO fill all members into parameters
	}

	public PositionbasedRoutingHeader getPositionBasedHeader(Address destinationAddress, Position destinationPosition) {
		Address ownAddress = getOwnAddress();
		Position ownPosition = getOwnPosition();
		return new StartHeader(ownAddress, ownPosition, destinationAddress, destinationPosition);
	}

	public PositionbasedRoutingHeader getPositionBasedHeader(PositionbasedRoutingHeader positionbasedRoutingHeader) {
        //return null;
		return new StartHeader(positionbasedRoutingHeader,getOwnAddress(),getOwnPosition());
	}


	public RoutingHeader getUnicastHeader(Address destination) {
		return getPositionBasedHeader(destination, null);
	}

	// determine the stub for accessing the header creation mechanism of greedy routing
	public PositionUnicastRoutingAlgorithm_Sync getRecovery() {
		if (greedyRouting == null) {
			greedyRouting = (PositionUnicastRoutingAlgorithm_Sync) runtimeOperatingSystem
					.getAccessListenerStub(greedyRoutingID,
							PositionUnicastRoutingAlgorithm_Sync.class);
		}
		return greedyRouting;
	}

	public PlanarGraphExplorer getPlanarGraphExplorer() {
		return planarGraphExplorer;
	}

	public ClusterDiscoveryService getClusterDiscoveryService() {
		if(clusterDiscovery == null) {
			clusterDiscovery = (ClusterDiscoveryService)runtimeOperatingSystem.getAccessListenerStub(
					clusterDiscoveryID, ClusterDiscoveryService.class);
		}
		return clusterDiscovery;
	}
	
	public ClusterPlanarizerService getClusterPlanarizerService() {
		if(clusterPlanarizer == null) {
			clusterPlanarizer = (ClusterPlanarizerService) runtimeOperatingSystem
					.getAccessListenerStub(clusterPlanarizerID,
							ClusterPlanarizerService.class);
		}
		return clusterPlanarizer;
	}

	public StartCondition createStartCondition() {
		return startConditionFactory.createStartCondition();
	}

	public CrossingCondition createCrossingCondition() {
		return crossingConditionFactory.createCrossingCondition();
	}

	public TurnCondition createTurnCondition() {
		return turnConditionFactory.createTurnCondition();
	}

	public ResumeGreedyCondition createResumeGreedyCondition() {
		return resumeGreedyConditionFactory.createResumeGreedyCondition();
	}

	public BreakCondition createBreakCondition() {
		return breakConditionFactory.createBreakCondition();
	}

	public FinishCondition createFinishCondition(Address destination) {
		return finishConditionFactory.createFinishCondition(destination);
	}

	public ClusterTransitionNodeOrder getClusterNodeOrder() {
		// TODO TEST!!!
//		return new CenteredClusterTransitionNodeOrder();
		return new DefaultClusterTransitionNodeOrder();
	}

	public NextNodeSelector getNextNodeSelector() {
		// TODO TEST!!!
		if(ENERGY_TEST) {
			return new EnergyAwareNextNodeSelector();
		}
		else {
			double udgRadius = getClusterDiscoveryService().getClusterMap().getClusterDiameter();
			return new DefaultNextNodeSelector(udgRadius);
		}
	}

	public DestinationClusterNodeOrder getDestinationClusterNodeOrder() {
		return new DefaultDestinationClusterNodeOrder();
	}

	public DestinationClusterNextNodeSelector getDestinationClusterNextNodeSelector() {
		// TODO TEST
		if(ENERGY_TEST) {
			return new EnergyAwareDestinationNodeSelector();
		}
		else {
			return new DefaultDestinationClusterNextNodeSelector();
		}
	}

}
