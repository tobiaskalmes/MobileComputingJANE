package de.uni_trier.jane.simulation.parametrized.parameters.initialization;

import de.uni_trier.jane.simulation.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;

public class SimulationInitializerSequence {

	private SimulationInitializator[] elements;

	public SimulationInitializerSequence(SimulationInitializator[] elements) {
		this.elements = elements;
	}

	public void initSimulation(InitializationContext initializationContext, SimulationParameters simulationParameters) {
		int len = elements.length;
		for(int i=0; i<len; i++) {
			SimulationInitializator initializator = elements[i];
			initializator.initializeInternal(initializationContext, simulationParameters);
		}
	}
	
	public void printUsage() {
		int len = elements.length;
		for(int i=0; i<len; i++) {
			elements[i].printUsage("");
		}
	}
	
}
