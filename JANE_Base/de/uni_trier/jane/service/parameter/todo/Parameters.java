/*
 * Created on 15.01.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.parameter.todo;

import java.io.*;

import de.uni_trier.jane.basetypes.*;

/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface Parameters extends Serializable{
	public void addParameter(String key, boolean value);

	public void addParameter(String key, int value);

	public void addParameter(String key, double value);

	public void addParameter(String key, String value);

	public void addParameter(String key, ServiceID value);

	public void addParameter(String key, Object[] values);

	public void addParameter(String key, Object object);

    /**
     * TODO Comment method
     * @param parameters
     */
    public void addAll(Parameters parameters);
}