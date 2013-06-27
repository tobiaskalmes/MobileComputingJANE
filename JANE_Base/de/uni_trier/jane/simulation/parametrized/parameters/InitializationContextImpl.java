package de.uni_trier.jane.simulation.parametrized.parameters;

import java.util.*;

public class InitializationContextImpl implements InitializationContext {

	private Properties properties;
	private LinkedList prefixList;

	private boolean verbose;

	public InitializationContextImpl(Properties properties) {
		this(properties, false);
	}

	public InitializationContextImpl(Properties properties, boolean verbose) {
		this.properties = properties;
		this.verbose = verbose;
		prefixList = new LinkedList();
	}

	public String getProperty(String key, String defaultValue) {
		String k = createPrefixString() + key;
		String v = properties.getProperty(k, defaultValue);
		if(verbose) {
			System.out.print("Parameter: " + k + " = " + v);
			if(!properties.containsKey(k)) {
				System.out.print(" (default)");
			}
			System.out.println("");
		}
		return v;
	}

	public void push(String key) {
		prefixList.addLast(key);
	}

	public void pop() {
		prefixList.removeLast();
	}

	private String createPrefixString() {
		StringBuffer buffer = new StringBuffer();
		Iterator iterator = prefixList.iterator();
		while (iterator.hasNext()) {
			String prefix = (String) iterator.next();
			buffer.append(prefix);
			buffer.append(".");
		}
		return buffer.toString();
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

}
