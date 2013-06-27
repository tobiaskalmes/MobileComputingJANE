/*
 * Created on 07.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.beaconing;

import de.uni_trier.jane.basetypes.*;

import java.io.*;

/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface DataMap extends Serializable{

	/**
	 * Check if there is information available for the given data ID.
	 * @param dataID the data ID to be tested
	 * @return true if there is inforamtion available
	 */
	public boolean hasData(DataID dataID);

	/**
	 * Get the data stored in this data collection which matches the given data ID.
	 * @param dataID the ID of the data to be retreived
	 * @return the matching data or null if nothing has matched
	 */
	public Data getData(DataID dataID);

	/**
	 * Get all data objects which are stored in the this data structure.
	 * @return the data objects
	 */
	public Data[] getData();

	/**
	 * Copy this data structure.
	 * @return the copy of this object
	 */
	public DataMap copy();
	
	// TODO comment
	public int getSize();

//    public void write(ObjectOutputStream out) throws IOException;
//
//    public void read(ObjectInputStream in) throws IOException, ClassNotFoundException;

    

}