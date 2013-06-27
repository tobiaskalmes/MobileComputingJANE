package de.uni_trier.jane.simulation.parametrized.parameters.object;

import de.uni_trier.jane.simulation.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;

public class InitializationObjectEnumeration extends EnumerationParameter {

	public InitializationObjectEnumeration(String key, int defaultIndex, InitializationObjectElement[] parameters) {
		this(key, defaultIndex, parameters, "");
	}

	public InitializationObjectEnumeration(String key, int defaultIndex, InitializationObjectElement[] parameters, String description) {
		super(key, defaultIndex, parameters, description);
	}

	public Object getValue(InitializationContext initializationContext, SimulationParameters simulationParameters) {
		InitializationContextImpl ici = (InitializationContextImpl)initializationContext;
		InitializationObjectElement objectParameter = (InitializationObjectElement)getEnumerationElement(initializationContext);
		ici.push(getKey());
		Object result = objectParameter.getValueInternal(initializationContext, simulationParameters);
		ici.pop();
		return result;
	}

}
