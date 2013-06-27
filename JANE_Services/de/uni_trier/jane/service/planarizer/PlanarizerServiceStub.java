/*
 * @author Stefan Peters
 * Created on 09.05.2005
 */
package de.uni_trier.jane.service.planarizer;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.signaling.*;

/**
 * @author Stefan Peters
 */
public final class PlanarizerServiceStub implements PlanarizerService {

	private RuntimeOperatingSystem operatingSystem;
	private ServiceID planarizerServiceID;

	public PlanarizerServiceStub(RuntimeOperatingSystem operatingSystem, ServiceID planarizerServiceID) {
		this.operatingSystem = operatingSystem;
		this.planarizerServiceID = planarizerServiceID;
	}

	public void register() {
		operatingSystem.registerAtService(planarizerServiceID,PlanarizerService.class );
	}

	public void unregister() {
		operatingSystem.unregisterAtService(planarizerServiceID,PlanarizerService.class );
	}

	private static final class GetPlanargraphNodeSyncAccess implements ListenerAccess {

		private static final long serialVersionUID = 1192579161907630012L;

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return PlanarizerService.class;
		}

		public Object handle(SignalListener service) {
			return ((PlanarizerService) service).getPlanarGraphNode();
		}

	}
	
	public PlanarGraphNode getPlanarGraphNode() {
		return  (PlanarGraphNode) operatingSystem.accessSynchronous(planarizerServiceID,new GetPlanargraphNodeSyncAccess());
	}
	
	public void removeNeighbor(Address address) {
		//TODO was muss ich hier machen
		
	}
	
}
