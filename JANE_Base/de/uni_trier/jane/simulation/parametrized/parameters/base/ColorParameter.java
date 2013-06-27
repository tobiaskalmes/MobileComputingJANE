package de.uni_trier.jane.simulation.parametrized.parameters.base;

import java.util.*;

import de.uni_trier.jane.simulation.parametrized.parameters.*;
import de.uni_trier.jane.visualization.*;

public class ColorParameter extends Parameter {

	private String defaultValue;
	private Map colors;
	
	public ColorParameter(String key, Color defaultValue) {
		this(key, defaultValue, "");
	}
	
	public ColorParameter(String key, Color defaultValue, String description) {
		super(key, description);
		this.defaultValue = defaultValue.toString().trim().toLowerCase();
		colors = new HashMap();
		colors.put(this.defaultValue, defaultValue);
		for(int i=0; i<Color.COLOR_MAP.length; i++) {
			Color color = Color.COLOR_MAP[i];
			colors.put(color.toString().trim().toLowerCase(), color);
		}
	}

	public Color getValue(InitializationContext initializationContext) {
		String value = initializationContext.getProperty(getKey(), defaultValue);
		Color color = (Color)colors.get(value.trim().toLowerCase());
		return color;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getKey() + "=");
		Object[] keys = colors.keySet().toArray();
		for(int i=0; i<keys.length; i++) {
			Object key = keys[i];
			if(key.equals(defaultValue)) {
				buffer.append("*" + key + "*");
			}
			else {
				buffer.append(key);
			}
			if(i < keys.length - 1) {
				buffer.append(",");
			}
		}
		return buffer.toString();
	}

}
