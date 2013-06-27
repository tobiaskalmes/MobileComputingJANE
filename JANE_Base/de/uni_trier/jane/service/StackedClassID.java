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
public class StackedClassID extends ServiceID {

    private String serviceClassName;
    private ServiceID underlyingService;

    /**
     * @param serviceClass
     * @param underlyingService
     */
    public StackedClassID(String serviceClassName, ServiceID underlyingService) {
        this.serviceClassName = serviceClassName;
        this.underlyingService = underlyingService;
    }

	/**
     * Constructor for class StackedClassID 
     *
     * @param class1
     * @param linkLayerService
     * @deprecated use StackedClassID(String,ServiceID) instead
     */
    public StackedClassID(Class serviceClass, ServiceID linkLayerService) {
        this(serviceClass.getName(),linkLayerService);

    }

//    public boolean equals(Object object) {
//		if (this == object) {
//			return true;
//		}
//		if (object == null) {
//			return false;
//		}
//		if (object.getClass() != getClass()) {
//			return false;
//		}
//		StackedClassID other = (StackedClassID) object;
//		return other.serviceClassName.equals(serviceClassName) && other.underlyingService.equals(underlyingService);
//	}
//    
//	public int hashCode() {
//	    return serviceClassName.hashCode() + underlyingService.hashCode();
//	}

    public String toString() {
        return serviceClassName + ":" + underlyingService.toString();
    }

    //
//    public Class getListenerClass() {
//        return serviceClass;
//    }
    

    public int getCodingSize() {
        return underlyingService.getCodingSize()+serviceClassName.length()*8;
    }

    public int hashCode() {
            final int PRIME = 1000003;
            int result = 0;
            if (serviceClassName != null) {
                result = PRIME * result + serviceClassName.hashCode();
            }
            if (underlyingService != null) {
                result = PRIME * result + underlyingService.hashCode();
            }
    
            return result;
        }
    
        public boolean equals(Object oth) {
            if (this == oth) {
                return true;
            }
    
            if (oth == null) {
                return false;
            }
    
            if (oth.getClass() != getClass()) {
                return false;
            }
    
            StackedClassID other = (StackedClassID) oth;
            if (this.serviceClassName == null) {
                if (other.serviceClassName != null) {
                    return false;
                }
            } else {
                if (!this.serviceClassName.equals(other.serviceClassName)) {
                    return false;
                }
            }
            if (this.underlyingService == null) {
                if (other.underlyingService != null) {
                    return false;
                }
            } else {
                if (!this.underlyingService.equals(other.underlyingService)) {
                    return false;
                }
            }
    
            return true;
        }
	
}
