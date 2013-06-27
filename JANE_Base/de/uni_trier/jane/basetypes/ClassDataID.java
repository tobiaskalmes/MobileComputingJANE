/*
 * Created on 08.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.basetypes;


/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ClassDataID extends DataID {

    private Class dataClass;

    /**
     * @param dataClass
     */
    public ClassDataID(Class dataClass) {
        this.dataClass = dataClass;
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
		ClassDataID other = (ClassDataID)object;
		return dataClass.equals(other.dataClass);
    }

    public int hashCode() {
        return dataClass.hashCode();
    }
    
    //
    public String toString() {
        return dataClass.getName();
    }
    

}
