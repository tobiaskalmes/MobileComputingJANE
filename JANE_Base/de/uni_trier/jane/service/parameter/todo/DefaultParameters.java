/*
 * Created on 15.01.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.parameter.todo;

import java.util.*;

import de.uni_trier.jane.basetypes.*;

/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DefaultParameters implements Parameters {

	private HashMap map;
	
	public DefaultParameters() {
		map = new HashMap();
	}

	public Set getKeys() {
		return map.keySet();
	}
	
	public String getValue(Object key) {
		return (String)map.get(key);
	}
	
	public void addParameter(String key, boolean value) {
		map.put(key, "" + value);
	}

	public void addParameter(String key, int value) {
		map.put(key, "" + value);
	}

	public void addParameter(String key, double value) {
		map.put(key, "" + value);
	}

	public void addParameter(String key, String value) {
		map.put(key, "" + value);
	}

	public void addParameter(String key, ServiceID value) {
		map.put(key, "" + value);
	}

    public void addAll(Parameters parameters) {
        //TODO: getter fuer parameter => kein cast
        map.putAll(((DefaultParameters)parameters).map);
        
    }

	public void addParameter(String key, Object object) {
		map.put(key, "" + object);
	}

	public void addParameter(String key, Object[] values) {
		StringBuffer buffer = new StringBuffer();
		int len = values.length;
		for(int i=0; i<len; i++) {
			buffer.append(values[i].toString());
			if(i < len - 1) {
				buffer.append(", ");
			}
		}
		map.put(key, buffer.toString());
	}

}
