/*
 * Created on 17.11.2004
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
public interface Dispatchable {

    /**
     * Get a copy of this signal. Most signal implementations will return "this",
     * when the data stored in this signal is immutable.
     * @return a copy of this signal (possibly "this" if immutable)
     */
    public Dispatchable copy();

    /**
     * Get the type of the class or interface which has to be implemented by
     * the receiver of this signal.
     * @return the receiver service class or interface type
     */
    public Class getReceiverServiceClass();

}