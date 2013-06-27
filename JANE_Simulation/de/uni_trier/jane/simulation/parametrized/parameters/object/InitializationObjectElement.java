package de.uni_trier.jane.simulation.parametrized.parameters.object;

import de.uni_trier.jane.simulation.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;

public abstract class InitializationObjectElement extends EnumerationElement {

	public InitializationObjectElement(String key) {
		this(key, "");
	}

	public InitializationObjectElement(String key, String description) {
		super(key, description);
	}

	public Object getValueInternal(InitializationContext initializationContext, SimulationParameters simulationParameters) {
		InitializationContextImpl ici = (InitializationContextImpl)initializationContext;
		ici.push(getKey());
		Object result = getValue(initializationContext, simulationParameters);
		ici.pop();
		return result;
	}
	
	protected abstract Object getValue(InitializationContext initializationContext, SimulationParameters simulationParameters);

}
