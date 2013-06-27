/*
 * Created on Jun 9, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.reflectionSignal;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.*;


/**
 * @author goergen
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SerializableMethod implements Serializable {

	private transient Method method;

	/**
	 * @param method
	 */
	public SerializableMethod(Method method) {
		
		this.method=method;
	}

	/**
	 * @param listener
	 * @param args
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 */
	public Object invoke(Object object, Object[] args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
	    //Method omethod=object.getClass().getMethod(method.getName(),method.getParameterTypes());
        if (Modifier.isPublic(method.getModifiers())){
            method.setAccessible(true);
        }
        return method.invoke(object,args);
        
		//return method.invoke(object,args);
	}

	private void writeObject(java.io.ObjectOutputStream out)
	      throws IOException{
		out.defaultWriteObject();
		out.writeObject(method.getDeclaringClass());
		out.writeObject(method.getName());
		out.writeObject(method.getParameterTypes());
	  	
	}
	private void readObject(java.io.ObjectInputStream in)
	      throws IOException, ClassNotFoundException{
		in.defaultReadObject();
		Class methodClass=(Class)in.readObject();
		try {
			method=methodClass.getMethod((String)in.readObject(),(Class[])in.readObject());
		} catch (SecurityException e) {
			throw new IOException(e.getMessage());
		} catch (NoSuchMethodException e) {
			throw new IOException(e.getMessage());
		} 
		
	      	
	}
}