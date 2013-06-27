/*
 * Created on Feb 2, 2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.uni_trier.jane.basetypes;



/**
 * @author daniel
 * 
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public abstract class ObjectID extends ServiceID{

    
    /**
     * Returns the coding size of this gizmoID
     * @return the coding size in bits
     */
    //public abstract int getCodingSize();

    //public abstract Class getListenerClass();
    public abstract Class getListenerClass();

    public abstract long getCreationTimestamp();

    public abstract ObjectID getCreator();

}
