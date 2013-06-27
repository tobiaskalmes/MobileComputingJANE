package de.uni_trier.jane.simulation.parametrized.parameters.initialization;

import de.uni_trier.jane.simulation.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;


public interface SimulationInitializator {

	public String getKey();
	
	public void initializeInternal(InitializationContext initializationContext, SimulationParameters simulationParameters);

	public void printUsage(String prefix);

}
