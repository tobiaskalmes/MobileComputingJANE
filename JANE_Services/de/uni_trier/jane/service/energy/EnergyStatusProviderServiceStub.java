/*
 * Created on 19.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.energy;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.signaling.*;


public final class EnergyStatusProviderServiceStub implements EnergyStatusProviderService {
	private RuntimeOperatingSystem operatingSystem;
	private ServiceID EnergyStatusProviderServiceServiceID;
	public EnergyStatusProviderServiceStub(RuntimeOperatingSystem operatingSystem, ServiceID EnergyStatusProviderServiceServiceID) {
		this.operatingSystem = operatingSystem;
		this.EnergyStatusProviderServiceServiceID = EnergyStatusProviderServiceServiceID;
	}

	public void registerAtService() {
		operatingSystem.registerAtService(EnergyStatusProviderServiceServiceID, EnergyStatusProviderService.class);
	}

	public void unregisterAtService() {
		operatingSystem.unregisterAtService(EnergyStatusProviderServiceServiceID, EnergyStatusProviderService.class);
	}

	private static final class GetEnergyStatusSyncAccess implements ListenerAccess {
		public GetEnergyStatusSyncAccess() {
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return EnergyStatusProviderService.class;
		}

		public Object handle(SignalListener service) {
			return ((EnergyStatusProviderService) service).getEnergyStatus();
		}

	}
	public EnergyStatus getEnergyStatus() {
		return (EnergyStatus) operatingSystem.accessSynchronous(EnergyStatusProviderServiceServiceID, new GetEnergyStatusSyncAccess());
	}

}