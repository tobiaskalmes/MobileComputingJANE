package de.uni_trier.jane.service.routing.logging;


public class EnergyModelHCB extends PathLossModel {

	public EnergyModelHCB() {
		super(2, 1.0, 2000.0);
	}

	public String toString() {
		return "HCB-Model";
	}

}
