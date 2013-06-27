package de.uni_trier.jane.tools.pathneteditor.gui.contextmenu;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import de.uni_trier.jane.tools.pathneteditor.constants.PathNetConstants;
import de.uni_trier.jane.tools.pathneteditor.gui.GraphicsPanel;
import de.uni_trier.jane.tools.pathneteditor.model.PathNetModel;
import de.uni_trier.jane.tools.pathneteditor.model.PathNetModelAdapter;
import de.uni_trier.jane.tools.pathneteditor.model.PathNetModelEvent;
import de.uni_trier.jane.tools.pathneteditor.objects.*;


public class WaypointAndTargetContextHandler extends PathNetObjectContextHandler {

	private JMenuItem[] menuItems;
	private Waypoint	act_waypoint = null;
	
	private class EdgeActionListener implements ActionListener {
		PathNetModel model;
		Waypoint source, target;
		
		public EdgeActionListener(PathNetModel model, Waypoint source, Waypoint target) {		
			this.model = model;
			this.source = source;
			this.target = target;
		}
		
		public void actionPerformed(ActionEvent e) {
			model.addEdge(new Edge(source, target));
			act_waypoint = null;
		}
	}
		
	public WaypointAndTargetContextHandler(GraphicsPanel panel) {
		super(panel);
				
		menuItems = new JMenuItem[4];
				
		model.addPathNetListener(new PathNetModelAdapter() {

			public void objectAdded(PathNetModelEvent event) {
				act_waypoint = null;
			}

			public void objectDeleted(PathNetModelEvent event) {
				objectAdded(event);
			}

			public void modelDataLoaded(PathNetModelEvent e) {
				objectAdded(e);
			}

			public void modelEventsReenabled(PathNetModelEvent e) {
				objectAdded(e);
			}
		});
	}
	
	public JMenuItem[] getMenuItems(Point p) {
		if (!acceptsPoint(p))
			return null;
				
		act_waypoint = (Waypoint)getObjectAt(p);
				
		getMenuItems(p, menuItems, act_waypoint, model);
		return menuItems;
	}

	public boolean acceptsPoint(Point p) {		
		return (getObjectAt(p)!=null && 
				( getObjectAt(p).getObjectType()==PathNetObject.TARGET
						|| getObjectAt(p).getObjectType()==PathNetObject.WAYPOINT)
		);
	}
	
	public String getLabel(Point p) {		
		return act_waypoint==null
			? ""
			: act_waypoint.getObjectType()==PathNetConstants.WAYPOINT
				? "Waypoint "
				: "Target "
			
			+ super.getLabel(p);
	}
	
	public static void getMenuItems(Point p, JMenuItem[] menuItems, final Waypoint act_waypoint, final PathNetModel model) {						
		class EdgeActionListener implements ActionListener {
			PathNetModel model;
			Waypoint source, target;
			
			public EdgeActionListener(PathNetModel model, Waypoint source, Waypoint target) {		
				this.model = model;
				this.source = source;
				this.target = target;
			}
			
			public void actionPerformed(ActionEvent e) {
				model.addEdge(new Edge(source, target));				
			}
		}
		
		// Item 0
		menuItems[0] = new JMenuItem("Delete " + 
				( act_waypoint.getObjectType()==PathNetConstants.WAYPOINT ? "waypoint" : "target" ) );
		menuItems[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.delete(act_waypoint);				
			}
		});
		
		// Item 1
		menuItems[1] = new JMenu("Create edge to...");
		
		JMenu to_target = new JMenu("Target");
		JMenu to_waypoint = new JMenu("Waypoint");
		menuItems[1].add(to_target);
		menuItems[1].add(to_waypoint);
						
		// get already connected endpoints
		Edge[] edges = model.getEdges(act_waypoint);
		HashSet endpoints = new HashSet();				
		for (int i=0; i<edges.length; i++)
			endpoints.add( edges[i].getSource()==act_waypoint
				? edges[i].getTarget()
				: edges[i].getSource()
			);
		endpoints.add(act_waypoint);
		
        // add (unconnected) targets
		Target[] targets = model.getTargets();
		for (int i=0; i<targets.length; i++) {
			if (!endpoints.contains(targets[i])) {
				JMenuItem mi = new JMenuItem(targets[i].getDescription());			
				mi.addActionListener(new EdgeActionListener(model, act_waypoint, targets[i]));
				to_target.add(mi);				
			}				
		}
		
		// add (unconnected) waypoints
		Waypoint[] waypoints = model.getWaypoints();
		for (int i=0; i<waypoints.length; i++) {
			if (!endpoints.contains(waypoints[i])) {
				JMenuItem mi = new JMenuItem(waypoints[i].getDescription());			
				mi.addActionListener(new EdgeActionListener(model, act_waypoint, waypoints[i]));
				to_waypoint.add(mi);				
			}				
		}
		
		// set enable flags
		to_target.setEnabled( to_target.getSubElements().length != 0 );
		to_waypoint.setEnabled( to_waypoint.getSubElements().length != 0 );
		
		// Item 2
		menuItems[2] = ContextHandler.SEPARATOR;
		
		// Item 3
		menuItems[3] = new JMenuItem("Properties...");
		menuItems[3].addActionListener(getDefaultPropertiesActionListener(act_waypoint, p, model));		
	}

}
