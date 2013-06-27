package de.uni_trier.jane.simulation.parametrized.parameters.service;

import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;


public interface ServiceCreator {

	public String getKey();
	
	public void createInstanceInternal(InitializationContext initializationContext, ServiceUnit serviceUnit);

	public void printUsage(String prefix);

}
