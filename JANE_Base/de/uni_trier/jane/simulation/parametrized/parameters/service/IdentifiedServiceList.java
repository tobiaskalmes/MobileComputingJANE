package de.uni_trier.jane.simulation.parametrized.parameters.service;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.unit.*;
import de.uni_trier.jane.simulation.parametrized.parameters.*;

public class IdentifiedServiceList extends Parameter implements ServiceCreator {

	private Map elementMap;

	public IdentifiedServiceList(String key, IdentifiedServiceElement[] elements, String description) {
		super(key, description);
		elementMap = new HashMap();
		int len = elements.length;
		for(int i=0; i < len; i++) {
			IdentifiedServiceElement element = elements[i];
			elementMap.put(element.getKey(), element);
		}
	}

	public void createInstanceInternal(InitializationContext initializationContext, ServiceUnit serviceUnit) {
		String value = initializationContext.getProperty(getKey(), "");
		String[] serviceStrings = value.split("\\s+");
		for(int i=0; i <serviceStrings.length; i++) {
			String serviceString = serviceStrings[i];
			String[] serviceStringComponents = serviceString.split("\\(");
			String service = serviceStringComponents[0];
			String id = serviceStringComponents[1].substring(0, serviceStringComponents[1].length()-1);
			IdentifiedServiceElement element = (IdentifiedServiceElement)elementMap.get(service);
			if(element != null) {
				ServiceID serviceID = new EndpointClassID(id);
				element.createInstanceInternal(serviceID, initializationContext, serviceUnit);
			}
		}
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getKey());
		buffer.append("={");
		int len = elementMap.size();
		IdentifiedServiceElement[] elements = (IdentifiedServiceElement[])elementMap.values().toArray(new IdentifiedServiceElement[len]);
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
		int len = elementMap.size();
		IdentifiedServiceElement[] elements = (IdentifiedServiceElement[])elementMap.values().toArray(new IdentifiedServiceElement[len]);
		for(int i=0; i<len; i++) {
			Parameter value = elements[i];
			value.printUsage(prefix);
		}
	}
	

}
