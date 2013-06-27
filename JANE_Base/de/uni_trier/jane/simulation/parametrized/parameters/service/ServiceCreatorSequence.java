package de.uni_trier.jane.simulation.parametrized.parameters.service;

import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;
import de.uni_trier.jane.simulation.parametrized.parameters.base.*;

public class ServiceCreatorSequence {

	private static final BooleanParameter VISUALIZE = new BooleanParameter("visualize", true);

	private ServiceCreator[] elements;

	public ServiceCreatorSequence(ServiceCreator[] elements) {
		this.elements = elements;
	}

	public void initialize(InitializationContext initializationContext, ServiceUnit serviceUnit) {
		int len = elements.length;
		for(int i=0; i<len; i++) {

			// get service creation object
			ServiceCreator creator = elements[i];

			// If visualization is activated set desired visualization mode for this service
			boolean visualizeOld = serviceUnit.getVisualizeAddedServices();
			if(visualizeOld) {
				InitializationContextImpl ici = (InitializationContextImpl)initializationContext;
				ici.push(creator.getKey());
				boolean visualize = VISUALIZE.getValue(ici);
				ici.pop();
				serviceUnit.setVisualizeAddedServices(visualize);
			}

			// create services
			creator.createInstanceInternal(initializationContext, serviceUnit);
			
			// restore visualization mode
			serviceUnit.setVisualizeAddedServices(visualizeOld);
			
		}
	}
	
	public void printUsage() {
		int len = elements.length;
		for(int i=0; i<len; i++) {
			
			// print service creator usage
			elements[i].printUsage("");

			// print visualization option
			System.out.println("  " + elements[i].getKey() + "." + VISUALIZE.toString());
			
		}
	}
	
}
