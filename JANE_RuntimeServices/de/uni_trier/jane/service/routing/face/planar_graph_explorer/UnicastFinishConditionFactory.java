package de.uni_trier.jane.service.routing.face.planar_graph_explorer;

import de.uni_trier.jane.basetypes.*;

public interface UnicastFinishConditionFactory {

	public FinishCondition createFinishCondition(Address destinationAddress, Position destinationPosition);
	
}
