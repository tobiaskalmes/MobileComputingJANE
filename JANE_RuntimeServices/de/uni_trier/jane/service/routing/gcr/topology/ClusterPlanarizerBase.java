package de.uni_trier.jane.service.routing.gcr.topology;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;
import de.uni_trier.jane.simulation.parametrized.parameters.base.*;

public class ClusterPlanarizerBase {

	private static final ServiceIDParameter CLUSTER_PLANARIZER_ID = new ServiceIDParameter("clusterPlanarizerID");

	public static final ServiceID getInstance(ServiceUnit serviceUnit, InitializationContext initializationContext) {
		ServiceID clusterPlanarizerID = CLUSTER_PLANARIZER_ID.getValue(initializationContext);
		if(clusterPlanarizerID == null) {
	    	if(!serviceUnit.hasService(ClusterPlanarizerService.class)) {
	    		ShortEdgePlanarizer.createInstance(serviceUnit);
	    	}
	    	clusterPlanarizerID = serviceUnit.getService(GenericClusterPlanarizer.class);
		}
		return clusterPlanarizerID;
	}

}
