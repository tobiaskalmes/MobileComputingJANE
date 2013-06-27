package de.uni_trier.jane.tools.pathneteditor.model;

import de.uni_trier.jane.tools.pathneteditor.constants.PathNetConstants;
import de.uni_trier.jane.tools.pathneteditor.objects.PathNetObject;

public final class PathNetModelEvent implements PathNetConstants {

	private PathNetObject	source;	
	
	public PathNetModelEvent(PathNetObject source) {
		this.source = source;				
	}
	
	public Object	getSource()		{ return source; }	
}
