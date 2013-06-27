package de.uni_trier.jane.service.routing.logging;

import de.uni_trier.jane.basetypes.*;

public interface EnergyModel {

	double calculate(Position senderPosition, Position receiverPosition);

}
