package de.uni_trier.jane.simulation.parametrized.parameters.initialization;

import de.uni_trier.jane.simulation.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;

public class InitializationEnumeration extends EnumerationParameter implements SimulationInitializator {

	public InitializationEnumeration(String key, int defaultIndex, InitializationElement[] elements) {
		this(key, defaultIndex, elements, "");
	}

	public InitializationEnumeration(String key, int defaultIndex, InitializationElement[] elements, String description) {
		super(key, defaultIndex, elements, description);
	}

	public void initializeInternal(InitializationContext initializationContext, SimulationParameters simulationParameters) {
		InitializationContextImpl ici = (InitializationContextImpl)initializationContext;
		InitializationElement element = (InitializationElement)getEnumerationElement(initializationContext);
		ici.push(getKey());
		element.initializeInternal(initializationContext, simulationParameters);
		ici.pop();
	}

}
