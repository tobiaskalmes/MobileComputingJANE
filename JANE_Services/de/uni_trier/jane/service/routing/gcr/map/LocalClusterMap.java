package de.uni_trier.jane.service.routing.gcr.map;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.routing.gcr.ClusterMapService;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.visualization.shapes.*;

public class LocalClusterMap implements RuntimeService, ClusterMapService {

	public static final ServiceID SERVICE_ID = new EndpointClassID(
			LocalClusterMap.class.getName());
	
	private ClusterMap clusterMap;
	
	public static final void createInstance(ServiceUnit serviceUnit, ClusterMap clusterMap) {
		serviceUnit.addService(new LocalClusterMap(clusterMap));
	}
	
	public LocalClusterMap(ClusterMap map) {
		clusterMap = map;
	}

	public void start(RuntimeOperatingSystem operatingSystem) {
		operatingSystem.registerAccessListener(ClusterMapService.class);
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
