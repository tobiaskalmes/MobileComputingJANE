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


public final class EnergyConsumptionListenerServiceStub implements EnergyConsumptionListenerService {
	private RuntimeOperatingSystem operatingSystem;
	private ServiceID EnergyConsumptionServiceServiceID;
	public EnergyConsumptionListenerServiceStub(RuntimeOperatingSystem operatingSystem, ServiceID EnergyConsumptionServiceServiceID) {
		this.operatingSystem = operatingSystem;
		this.EnergyConsumptionServiceServiceID = EnergyConsumptionServiceServiceID;
	}

	public void registerAtService() {
		operatingSystem.registerAtService(EnergyConsumptionServiceServiceID, EnergyConsumptionListenerService.class);
	}

	public void unregisterAtService() {
		operatingSystem.unregisterAtService(EnergyConsumptionServiceServiceID, EnergyConsumptionListenerService.class);
	}

	private static final class SetCurrentEnergyConsumptionSignal implements Signal {
		private double watt;
		public SetCurrentEnergyConsumptionSignal(double watt) {
			this.watt = watt;
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return EnergyConsumptionListenerService.class;
		}

		public void handle(SignalListener service) {
			((EnergyConsumptionListenerService) service).setCurrentEnergyConsumption(watt);
		}

	}
	public void setCurrentEnergyConsumption(double watt) {
		operatingSystem.sendSignal(EnergyConsumptionServiceServiceID, new SetCurrentEnergyConsumptionSignal(watt));
	}

	private static final class ReduceEnergySignal implements Signal {
		private double joule;
		public ReduceEnergySignal(double joule) {
			this.joule = joule;
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return EnergyConsumptionListenerService.class;
		}

		public void handle(SignalListener service) {
			((EnergyConsumptionListenerService) service).reduceEnergy(joule);
		}

	}
	public void reduceEnergy(double joule) {
		operatingSystem.sendSignal(EnergyConsumptionServiceServiceID, new ReduceEnergySignal(joule));
	}

}