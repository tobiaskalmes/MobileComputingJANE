/*
 * Created on Feb 17, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.uni_trier.jane.tools.pathneteditor.export;

import java.io.InputStream;
import java.io.OutputStream;

import de.uni_trier.jane.tools.pathneteditor.model.PathNetModel;


/**
 * @author steffen
 *
 * Interface for im-/and export of a PathNetModel. This could be XML, but also other data-formats.
 */
public interface ModelImportExportFilter {
	
	public abstract boolean loadModelData(InputStream source, PathNetModel model);
	public abstract boolean saveModelData(OutputStream target, PathNetModel model);
	
	public abstract boolean acceptsStream(InputStream source);
}
