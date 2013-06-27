package de.uni_trier.jane.service.routing.logging;

import de.uni_trier.jane.basetypes.*;

public class ConstantEnergyConsumption implements EnergyModel {

	private double amount;

	public ConstantEnergyConsumption(double amount) {
		this.amount = amount;
	}

	public double calculate(Position senderPosition, Position receiverPosition) {
		return amount;
	}

}
