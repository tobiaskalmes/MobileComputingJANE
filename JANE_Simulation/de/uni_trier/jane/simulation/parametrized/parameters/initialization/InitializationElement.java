package de.uni_trier.jane.simulation.parametrized.parameters.initialization;

import de.uni_trier.jane.simulation.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;

public abstract class InitializationElement extends EnumerationElement implements SimulationInitializator {

	public InitializationElement(String key) {
		this(key, "");
	}

	public InitializationElement(String key, String description) {
		super(key, description);
	}

	public void initializeInternal(InitializationContext initializationContext, SimulationParameters simulationParameters) {
		InitializationContextImpl ici = (InitializationContextImpl)initializationContext;
		ici.push(getKey());
		initialize(initializationContext, simulationParameters);
		ici.pop();
	}
	
	public abstract void initialize(InitializationContext initializationContext, SimulationParameters simulationParameters);

}
