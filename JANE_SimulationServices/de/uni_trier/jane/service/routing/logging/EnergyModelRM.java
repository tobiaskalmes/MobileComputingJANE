package de.uni_trier.jane.service.routing.logging;

public class EnergyModelRM extends PathLossModel {

	public EnergyModelRM() {
		super(4.0, 1.0, 200000000.0);
	}

	public String toString() {
		return "RM-Model";
	}

}
