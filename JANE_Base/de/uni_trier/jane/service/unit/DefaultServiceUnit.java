/*
 * Created on 07.01.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.unit;

import java.io.*;
import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.parameter.todo.*;
import de.uni_trier.jane.service.unit.ServiceCollection.*;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DefaultServiceUnit implements ServiceUnit,Serializable {

    /**
     * @author goergen
     *
     * TODO comment class
     */
    private static final class DefaultServiceID extends ObjectID {
        private String serviceClassName;
        private int timestamp;
        private DeviceID deviceID;

        
        
        
        /**
         * Constructor for class <code>StartUpServiceID</code>
         * @param serviceClassName
         * @param timestamp
         */
        public DefaultServiceID(String serviceClassName,int timestamp,DeviceID  deviceID) {
            this.serviceClassName = serviceClassName;
            this.timestamp = timestamp;
            this.deviceID=deviceID;
        }
        //
        public long getCreationTimestamp() {
            return timestamp;
        }
        
        //
        public ObjectID getCreator() {
         
            return null;//DeviceObjectID(deviceID);
        }
        
        //
        public Class getListenerClass() {
            try {
                return Class.forName(serviceClassName);
            } catch (ClassNotFoundException ex) {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }
            return null;
        }
        


//        public Class getListenerClass() {
//            return serviceClass;
//        }


        public String toString() {
            return serviceClassName+":("+timestamp+")";
        }
        

        public int getCodingSize() {
            //return serviceClassName.length()*8+4*8;
            return 128+4*8;
        }
        public int hashCode() {
            final int PRIME = 1000003;
            int result = 0;
            result = PRIME * result + (timestamp);
            if (serviceClassName != null) {
                result = PRIME * result + serviceClassName.hashCode();
            }

            return result;
        }

        public boolean equals(Object oth) {
            if (this == oth) {
                return true;
            }

            if (!(oth instanceof DefaultServiceID)) return false;

            DefaultServiceID other = (DefaultServiceID) oth;

            if (this.timestamp != other.timestamp) {
                return false;
            }
            if (this.serviceClassName == null) {
                if (other.serviceClassName != null) {
                    return false;
                }
            } else {
                if (!this.serviceClassName.equals(other.serviceClassName)) {
                    return false;
                }
            }

            return true;
        }


    }
	/**
	 * @author Daniel G�rgen
	 *
	 * To change this generated comment go to 
	 * Window>Preferences>Java>Code Generation>Code and Comments
	 */
	private static final class ServiceObject implements Serializable {

		private Class serviceClass;
		private ServiceID serviceID;
		private DefaultParameters defaultParameters;
		private String toString;

		/**
		 * @param service
		 * 
		 */
		public ServiceObject(Service service,ServiceID serviceID) {
			serviceClass=service.getClass();
			this.serviceID=serviceID;
			defaultParameters=new DefaultParameters();
			service.getParameters(defaultParameters);
			toString=service.getClass().getName(); //toString();
		}
		
		public String toString() {
			return toString;
		}
		public DefaultParameters getParameters() {
			return defaultParameters;
		}
		public Class getServiceClass() {
			return serviceClass;
		}
		public ServiceID getServiceID() {
			return serviceID;
		}
	}
	private DeviceID deviceID;
	private volatile ServiceCollection serviceCollection;
	private String label;
	private DefaultServiceUnit parent;
	private Set visibleServiceSet;
	private int firstFreeID;

	
	private Set addedServiceSet;
	private Map labelChildMap;
	private volatile List serviceFactoryList;
	
	private Shape defaultShape;
    
    private int firstFreeServiceID;

    private boolean visualizeAdded;
    
    private transient DistributionCreator distributionCreator;

	public DefaultServiceUnit(DeviceID deviceID, ServiceCollection serviceCollection, DistributionCreator distributionCreator) {
		this(deviceID, serviceCollection, "ROOT", null, distributionCreator);
	}
	
	private DefaultServiceUnit(DeviceID deviceID, ServiceCollection serviceCollection, String label, DefaultServiceUnit parent, DistributionCreator distributionCreator) {
		this(deviceID, serviceCollection, label, parent, new HashSet(),1,1, distributionCreator);
	}

	private DefaultServiceUnit(DeviceID deviceID, ServiceCollection serviceCollection, String label, DefaultServiceUnit parent, Set visibleServiceSet, int firstFreeID, int firstFreeServiceID, DistributionCreator distributionCreator) {
		this.deviceID = deviceID;
		this.serviceCollection = serviceCollection;
		this.label = label;
		this.parent = parent;
		this.visibleServiceSet = visibleServiceSet;
		this.firstFreeID = firstFreeID;
		this.firstFreeServiceID=firstFreeServiceID;
		addedServiceSet = new HashSet();
		labelChildMap = new HashMap();
		serviceFactoryList = new ArrayList();
		ShapeCollection shape=new ShapeCollection();
		shape.addShape( new EllipseShape(deviceID,new Extent(5,5),Color.BLACK,true));
		shape.addShape(new TextShape(deviceID.toString(),deviceID,Color.BLACK,new Position(-3,-3)));
		defaultShape=shape;
		visualizeAdded = true;
		this.distributionCreator = distributionCreator;
	}
	
	public DeviceID getDeviceID() {
		return deviceID;
	}
	
	public String getLabel() {
		return label;
	}
	
	public ServiceID getService(Class type) {
		ServiceID result = getServiceSub(type);
		if(result == null) {
			throw new BaseException("The requested service type '" + type + "' is not found in this unit.");
		}
		return result;
	}

	public boolean hasService(Class type) {
		return getServiceSub(type) != null;
	}

	public ServiceID addService(Service service) {
		return addService(service, visualizeAdded);
	}

	public ServiceID addService(Service service, boolean visualize) {
		
		ServiceObject serviceObject=new ServiceObject(service,checkServiceID(service));
		serviceCollection.add(service,serviceObject.getServiceID(), visualize);
		addedServiceSet.add(serviceObject);
		visibleServiceSet.add(serviceObject);
		//addedServiceSet.add(service);
		//visibleServiceSet.add(service);
		return serviceObject.getServiceID();
	}

	public boolean hasChildUnit(String label) {
		return labelChildMap.containsKey(label);
	}

	public ServiceUnit getChildUnit(String label) {
		return (DefaultServiceUnit)labelChildMap.get(label);
	}

	public ServiceUnit createChildUnit() {
		String label = createLabel();
		return createChildUnit(label);
	}

	public ServiceUnit createChildUnit(String label) {
		if(hasChildUnit(label)) {
			throw new BaseException("The label is already used.");
		}
		DefaultServiceUnit child = new DefaultServiceUnit(deviceID, serviceCollection, label, this, distributionCreator);
		labelChildMap.put(label, child);
		return child;
	}
	
	public void addServiceFactory(ServiceFactory serviceFactory) {
		serviceFactoryList.add(serviceFactory);
	}
	
	public DefaultServiceUnit copy(DeviceID deviceID, ServiceCollection serviceCollection) {
		return copy(deviceID, serviceCollection, null);
	}

	private DefaultServiceUnit copy(DeviceID deviceID, ServiceCollection serviceCollection, DefaultServiceUnit parent) {
		DefaultServiceUnit copy = new DefaultServiceUnit(deviceID, serviceCollection, label, parent, new HashSet(visibleServiceSet), firstFreeID,firstFreeServiceID, distributionCreator);
		Iterator iterator = labelChildMap.values().iterator();
		while (iterator.hasNext()) {
			DefaultServiceUnit child = (DefaultServiceUnit) iterator.next();
			DefaultServiceUnit childCopy = child.copy(deviceID, serviceCollection, copy);
			copy.labelChildMap.put(childCopy.getLabel(), childCopy);
		}
		
		iterator = serviceFactoryList.iterator();
		while (iterator.hasNext()) {
			ServiceFactory serviceFactory = (ServiceFactory) iterator.next();
			serviceFactory.initServices(copy);
		}
		return copy;
		
	}
	
	private String createLabel() {
		String label;
		do {
			label = "Unit" + firstFreeID;
			firstFreeID++;
		} while(hasChildUnit(label));
		return label;
	}

	private ServiceID getServiceSub(Class type) {
		ServiceID result = null;
		if(parent != null) {
			result = parent.getServiceSub(type);
		}
		Iterator iterator = visibleServiceSet.iterator();
		while (iterator.hasNext()) {
			ServiceObject serviceObject = (ServiceObject)iterator.next();
			//if(type.isInstance(serviceObject.)) {
			if(type.isAssignableFrom(serviceObject.getServiceClass())){
				if(result != null) {
					throw new BaseException("The requested service type is found more than once in this unit.");
				}
				result = serviceObject.getServiceID();
			}
		}
		return result;
	}

	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("Device " + deviceID + "\n");
		toString(stringBuffer, "    ");
		return stringBuffer.toString();
	}

	private void toString(StringBuffer stringBuffer, String indent) {
		stringBuffer.append(indent + label + "\n");
		indent += "    ";
		Iterator iterator = visibleServiceSet.iterator();
		while(iterator.hasNext()) {
			ServiceObject service = (ServiceObject)iterator.next();
			String tag;
			if(addedServiceSet.contains(service)) {
				tag = " (added)";
			}
			else {
				tag = " (visible)";
			}
			stringBuffer.append(indent + service.toString() + tag + "\n");
			DefaultParameters parameters = service.getParameters();
			//new DefaultParameters();
			//(parameters);
			Iterator keyIterator = parameters.getKeys().iterator();
			while (keyIterator.hasNext()) {
				String key = (String)keyIterator.next();
				String value = parameters.getValue(key);
				stringBuffer.append(indent + "    " + key + " = " + value + "\n");
			}
		}
		iterator = labelChildMap.values().iterator();
		while (iterator.hasNext()) {
			DefaultServiceUnit child = (DefaultServiceUnit) iterator.next();
			child.toString(stringBuffer, indent);
		}
	}
	
    public Shape getDefaultShape() {
        return defaultShape;
    }
    public void setDefaultShape(Shape defaultShape) {
        this.defaultShape = defaultShape;
    }
    
    public void addShape(Shape shape) {
        ShapeCollection coll;
        if (defaultShape instanceof ShapeCollection) {
             coll= (ShapeCollection)defaultShape;
        }else{
            coll=new ShapeCollection();
            coll.addShape(defaultShape);
        }
        coll.addShape(shape);
        defaultShape=coll;
        
    }

    /**
     * TODO Comment method
     * @param class1
     * @return
     */
    public ServiceID checkServiceID(Service service) {
    	if (parent!=null){
    		return parent.checkServiceID(service);
    	}else{
    		ServiceID serviceID=service.getServiceID();
    		if (serviceID==null){
    			serviceID=new DefaultServiceID(service.getClass().getName(), firstFreeServiceID++,deviceID);
    		}
//        if (!serviceID.getListenerClass().isInstance(service)){
//            throw new OperatingServiceException("The listenerClass of the serviceID must be the class or a superclass of the assigned service");
//        }
    		return serviceID;
    	}
    }

	public void setVisualizeAddedServices(boolean visualize) {
		visualizeAdded = visualize;
	}

	public boolean getVisualizeAddedServices() {
		return visualizeAdded;
	}

	public DistributionCreator getDistributionCreator() {
		return distributionCreator;
	}

}
