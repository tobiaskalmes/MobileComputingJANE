package de.uni_trier.jane.simulation.parametrized.parameters.initialization;

import de.uni_trier.jane.simulation.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;

public class InitializationList extends ListParameter implements SimulationInitializator {

	public InitializationList(String key, InitializationElement[] parameters) {
		this(key, parameters, "");
	}

	public InitializationList(String key, InitializationElement[] parameters, String description) {
		super(key, parameters, description);
	}

	public void initializeInternal(InitializationContext initializationContext, SimulationParameters simulationParameters) {
		InitializationContextImpl ici = (InitializationContextImpl)initializationContext;
		EnumerationElement[] elements = getEnumerationElements(initializationContext);
		for(int i=0; i<elements.length; i++) {
			ici.push(getKey() + "-" + (i+1));
			InitializationElement element = (InitializationElement)elements[i];
			element.initializeInternal(initializationContext, simulationParameters);
			ici.pop();
		}
	}

}
