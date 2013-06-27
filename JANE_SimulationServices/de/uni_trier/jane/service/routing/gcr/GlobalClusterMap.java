package de.uni_trier.jane.service.routing.gcr;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.routing.gcr.map.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.service.*;
import de.uni_trier.jane.visualization.shapes.*;

public class GlobalClusterMap implements GlobalService, ClusterMapService {

	public static final ServiceID SERVICE_ID = new EndpointClassID(
			GlobalClusterMap.class.getName());
	
	private ClusterMap clusterMap;
	
	public static final void createInstance(ServiceUnit serviceUnit, ClusterMap clusterMap) {
		serviceUnit.addService(new GlobalClusterMap(clusterMap));
	}
	
	public GlobalClusterMap(ClusterMap map) {
		clusterMap = map;
	}

	public void start(GlobalOperatingSystem globalOperatingSystem) {
		globalOperatingSystem.registerAccessListener(ClusterMapService.class);
	}

	public ServiceID getServiceID() {
		return SERVICE_ID;
	}

	public void finish() {
		// ignore
	}

	public Shape getShape() {
		return clusterMap.getShape();
	}

	public void getParameters(Parameters parameters) {
		parameters.addParameter("clusterMap", clusterMap.toString());
	}

	public ClusterMap getClusterMap() {
		return clusterMap;
	}

}
