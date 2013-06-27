package de.uni_trier.jane.service.routing.face.conditions;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;

public interface FinishConditionFactory {
	public FinishCondition createFinishCondition(Address destination);
}
