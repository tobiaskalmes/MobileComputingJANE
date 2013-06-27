package de.uni_trier.jane.simulation.parametrized.parameters.service;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;

public abstract class IdentifiedServiceElement extends Parameter {

	public IdentifiedServiceElement(String key) {
		this(key, "");
	}

	public IdentifiedServiceElement(String key, String description) {
		super(key, description);
	}

	public Parameter[] getParameters() {
		return null;
	}

	public void createInstanceInternal(ServiceID ownServiceID, InitializationContext initializationContext, ServiceUnit serviceUnit) {
		InitializationContextImpl ici = (InitializationContextImpl)initializationContext;
		ici.push(getKey()+"("+ownServiceID+")");
		String visualizeState = initializationContext.getProperty("visualize", "true");
		boolean currentVisualize = Boolean.valueOf(visualizeState).booleanValue();
		boolean oldVizualize = serviceUnit.getVisualizeAddedServices();
		serviceUnit.setVisualizeAddedServices(currentVisualize);
		createInstance(ownServiceID, initializationContext, serviceUnit);
		serviceUnit.setVisualizeAddedServices(oldVizualize);
		ici.pop();
	}
	
	public abstract void createInstance(ServiceID ownServiceID, InitializationContext initializationContext, ServiceUnit serviceUnit);

	public String toString() {
		return getKey() + "(ID)";
	}

	public void printUsage(String prefix) {
		Parameter[] pars = getParameters();
		prefix = prefix + toString() + ".";
		printComment();
		if(pars != null) {
			int maxj = pars.length;
			for(int j=0; j<maxj; j++) {
				Parameter par = pars[j];
				par.printUsage(prefix);
			}
		}
		System.out.println(prefix + "visualize=boolean(true)");
		System.out.println();
	}

}
