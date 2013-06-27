package de.uni_trier.jane.simulation.parametrized.parameters;

import java.util.*;

public abstract class ListParameter extends Parameter {

	private Map parameters;

	public ListParameter(String key, EnumerationElement[] parameters, String description) {
		super(key, description);
		this.parameters = new HashMap();
		int len = parameters.length;
		for(int i=0; i < len; i++) {
			EnumerationElement element = parameters[i];
			this.parameters.put(element.getKey(), element);
		}
	}

	public EnumerationElement[] getEnumerationElements(InitializationContext initializationContext) {
		List result = new ArrayList();
		String value = initializationContext.getProperty(getKey(), "");
		String[] keys = value.split("\\s+");
		int len = keys.length;
		for(int i=0; i < len; i++) {
			String key = keys[i];
			EnumerationElement element = (EnumerationElement)parameters.get(key);
			if(element != null) {
				result.add(element);
			}
		}
		return (EnumerationElement[])result.toArray(new EnumerationElement[result.size()]);
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getKey());
		buffer.append("={");
		int len = parameters.size();
		EnumerationElement[] elements = (EnumerationElement[])parameters.values().toArray(new EnumerationElement[len]);
		for(int i=0; i<len; i++) {
			buffer.append(elements[i].toString());
			if(i < len - 1) {
				buffer.append(",");
			}
		}
		buffer.append("}*");
		return buffer.toString();
	}
	
	public void printUsage(String prefix) {
		System.out.println(prefix + toString());
		int len = parameters.size();
		EnumerationElement[] elements = (EnumerationElement[])parameters.values().toArray(new EnumerationElement[len]);
		for(int i=0; i<len; i++) {
			Parameter value = elements[i];
			value.printUsage("  " + prefix + getKey() + "-i.");
		}
	}

}
