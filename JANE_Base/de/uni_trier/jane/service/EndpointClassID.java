/*
 * Created on 08.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service;

import de.uni_trier.jane.basetypes.*;


/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
// TODO: benennen in ClassServiceID
public class EndpointClassID extends ServiceID {

    private String serviceClassName;

    /**
     * 
     * Constructor for class <code>EndpointClassID</code>
     *
     * @param serviceClassName
     */
    public EndpointClassID(String serviceClassName) {
        this.serviceClassName = serviceClassName;
    }

    /**
     * Constructor for class EndpointClassID 
     *
     * @param serviceClass
     * @deprecated use EndpointClassID(String) instead
     */
    public EndpointClassID(Class serviceClass) {
        this(serviceClass.getName());
    }

    public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null) {
			return false;
		}
		if (object.getClass() != getClass()) {
			return false;
		}
		EndpointClassID other = (EndpointClassID )object;
		return serviceClassName.equals(other.serviceClassName);
    }

    public int hashCode() {
        return serviceClassName.hashCode();
    }

    public String toString() {
        return serviceClassName;
    }


//    public Class getListenerClass() {
//        return serviceClass;
//    }


    public int getCodingSize() {
        //return serviceClassName.length()*8;
        return 128;
    }

}
