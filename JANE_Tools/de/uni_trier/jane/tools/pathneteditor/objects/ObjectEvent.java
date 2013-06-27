package de.uni_trier.jane.tools.pathneteditor.objects;

import de.uni_trier.jane.tools.pathneteditor.constants.PathNetConstants;

public class ObjectEvent implements PathNetConstants {
	
	private PathNetObject source;
	private int	action_type;
	
	private Object old_value, new_value;
	
	public ObjectEvent(PathNetObject source, int action_type, Object old_value, Object new_value) {
		this.source = source;
		this.action_type = action_type;
		this.old_value = old_value;
		this.new_value = new_value;
	}
	
	public ObjectEvent(PathNetObject source, int action_type) {
		this(source, action_type, null, null);
	}
	
	public int getActionType() { return action_type; }		
	public PathNetObject getSource() { return source; }
	public Object getOldValue() { return old_value; }
	public Object getNewValue() { return new_value; }
}
