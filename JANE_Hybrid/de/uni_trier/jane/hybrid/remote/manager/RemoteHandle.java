/*
 * Created on Jan 16, 2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.uni_trier.jane.hybrid.remote.manager;

/**
 * @author daniel
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class RemoteHandle {
    int id;
    
    /**
     * @param id
     */
    public RemoteHandle(int id) {
        super();
        this.id = id;
    }
    public int hashCode() {
        final int PRIME = 1000003;
        int result = 0;
        result = PRIME * result + id;

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

        RemoteHandle other = (RemoteHandle) oth;

        if (this.id != other.id) {
            return false;
        }

        return true;
    }
}
