/*
 * Created on May 14, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.uni_trier.ubi.appsim.kernel.dynamic.mobility_source.pathnet.timetable;

import java.util.HashMap;

/**
 * @author goergen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ServiceParameters {
	private String className;
	private HashMap parameters;
	private String deviceName;
    private boolean visualize;

	
	/**
	 * @param className
	 * @param deviceName
	 */
	public ServiceParameters(String className, String deviceName,boolean visualize) {
		this.className=className;
		this.deviceName=deviceName;
		parameters=new HashMap();
		this.visualize=visualize;
	}

	/**
	 * @param key
	 * @param value
	 */
	public void addParameter(String key, String value) {
		parameters.put(key,value);
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean hasParameters(){
		return !parameters.isEmpty();
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public boolean isDefined(String key){
		return parameters.containsKey(key);
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public String get(String key){
		return (String)parameters.get(key);
	}

	/**
	 * @return
	 */
	public String getClassName() {
		return className;
	}
	/**
	 * 
	 * @return
	 */
	public String getDeviceName() {
		return deviceName;
	}

    /**
     * @return
     */
    public boolean visualize() {
        
        return visualize;
    }
}
