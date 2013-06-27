/*
 * Created on Jan 26, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.uni_trier.jane.tools.pathneteditor.gui.contextmenu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import de.uni_trier.jane.tools.pathneteditor.constants.PathNetConstants;
import de.uni_trier.jane.tools.pathneteditor.gui.GraphicsPanel;
import de.uni_trier.jane.tools.pathneteditor.gui.helper.AbstractAdvancedTableModel;
import de.uni_trier.jane.tools.pathneteditor.gui.helper.RowHeaderTable;
import de.uni_trier.jane.tools.pathneteditor.model.DefaultPathNetModel;
import de.uni_trier.jane.tools.pathneteditor.objects.Edge;
import de.uni_trier.jane.tools.pathneteditor.objects.PathNetObject;
import de.uni_trier.jane.tools.pathneteditor.objects.Waypoint;
import de.uni_trier.jane.tools.pathneteditor.tools.PathNetTools;
import de.uni_trier.jane.tools.pathneteditor.tools.Settings;


/**
 * @author steffen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EdgeContextHandler extends PathNetObjectContextHandler {

	private JMenuItem[]	edge_items;
	private Edge		act_edge = null;
		
	private InnerPointListener innerPointListener = new InnerPointListener();
	private WaypointListener waypointListener = new WaypointListener();
	
	private class InnerPointEditor extends JDialog {
		private static final long serialVersionUID = 1L;
		
		// datastructure
		private Edge edge;
		
		// gui
		private JPanel button_P		= new JPanel();		
		private JButton	finish_B	= new JButton("Finish");
				
		private JPanel main_P			= new JPanel();
		private RowHeaderTable main_T 	= new RowHeaderTable();
		
		public InnerPointEditor(Edge edge) {			
			this.edge = edge;
			
			setSize(new Dimension(280, 200));
			setModal(true);
			
			Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
			setLocation((sd.width - getSize().width)/2, (sd.height - getSize().height)/2);
			
			setTitle("Inner Point Editor");
			
			init();			
		}
		
		private void init() {
			button_P.setLayout(new BorderLayout());			
			button_P.add(finish_B, BorderLayout.EAST);
						
			finish_B.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
				}
			});
			
			main_P.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "Inner points of " + edge.getDescription()+"["+edge.getID()+"]"));
			main_P.setLayout(new BorderLayout());
			main_P.add(main_T, BorderLayout.CENTER);
			
			main_T.setColumnWidth(70);
						
			main_T.setModel(new AbstractAdvancedTableModel() {
				private static final long serialVersionUID = 1L;
								
				public int getColumnCount() {					
					return 3;
				}

				public int getRowCount() {
					return edge.getInnerPointsSize();
				}

				public Object getValueAt(int rowIndex, int columnIndex) {
					switch(columnIndex) {
						case 0: // X-Pos column
							return new Integer((int)(edge.getInnerPoints()[rowIndex].getX()));
						case 1: // Y-Pos column
							return new Integer((int)(edge.getInnerPoints()[rowIndex].getY()));
						case 2:
							return new Integer((int)(edge.getWidth(rowIndex + 1)));
					}
					
					return null; // never happens
				}
				
				public String getColumnName(int columnIndex) {
					switch(columnIndex) {
						case 0:
							return "X-Pos";
						case 1:
							return "Y-Pos";
						case 2:
							return "Size";
					}
					
					return "";
				}
				
				public void setValueAt(Object aValue,int rowIndex,int columnIndex) {
					double val = Double.parseDouble(aValue.toString());
					System.err.println("New value for ("+rowIndex+"/"+columnIndex+") is: " + val);
					
					// only accept changes in column 2 for now
					if (columnIndex != 2)
						return;
					
					edge.setWidth(rowIndex + 1, val);
					panel.update();
				}
				
				public boolean isCellEditable(int rowIndex,int columnIndex) {
					return (columnIndex == 2);
				}
				
				public String getColumnTooltip(int column) {
					switch(column) {
						case 0:
							return "The X-Position of the inner point in model coordinates";
						case 1:
							return "The Y-Position of the inner point in model coordinates";
						case 2:
							return "The size of the inner point";
					}
					
					return "";
				}
				
				public String getRowName(int row) {
					return "#"+row;
				}
			});			
			
			Container c = getContentPane();
			c.setLayout(new BorderLayout());
			c.add(main_P, BorderLayout.CENTER);
			c.add(button_P, BorderLayout.SOUTH);
			
			// context menu for deletion option of inner points
			ContextMenu cm = new ContextMenu(main_T.getRowHeaderTable());
			cm.addContextHandler(new ContextHandler() {			
				private JMenuItem[] menuItems = new JMenuItem[1];
					
				public JMenuItem[] getMenuItems(Point p) {					
					final int idx = main_T.getRowHeaderTable().rowAtPoint(p);
					
					menuItems[0] = new JMenuItem("Delete");
					menuItems[0].addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							edge.removeInnerPoint(idx + 1);
							panel.update();
							main_T.revalidate();
							main_T.repaint();
						}
					});
					
					return menuItems;
				}

				public boolean acceptsPoint(Point p) {				
					return true;
				}

				public String getLabel(Point p) {				
					return "Inner Point Properties";
				}			
				
			});
			
			main_T.setBackground(Color.WHITE);
		}
	};
	
	private class InnerPointListener implements ActionListener {
		private Point p = null;
		
		public InnerPointListener() {}
		
		public void setPoint(Point p) {
			this.p = p;
		}
		
		public void actionPerformed(ActionEvent e) {				
				Point vp = PathNetTools.virtualToReal(p, panel.getZoom());
				vp.translate(panel.getOffset().x, panel.getOffset().y);
				
				act_edge.addInnerPoint(act_edge.getNearestOutlinePoint(vp), Settings.getDouble(PathNetConstants.DEFAULT_INNER_POINT_SIZE));
				panel.update();
		}		
	};
	private class WaypointListener implements ActionListener {
		private Point p = null;
				
		public void setPoint(Point p) {
			this.p = p;
		}
		
		public void actionPerformed(ActionEvent e) {				
				Point vp = PathNetTools.virtualToReal(p, panel.getZoom());
				vp.translate(panel.getOffset().x, panel.getOffset().y);
				
				if (model instanceof DefaultPathNetModel)
					((DefaultPathNetModel)model).enableModelEvents(false);
				
				Waypoint wp = new Waypoint();
				Waypoint s = act_edge.getSource();
				Waypoint t = act_edge.getTarget();
				wp.setPosition(act_edge.getNearestOutlinePoint(vp));
				
				// FIXME: To do so will delete all inner points of the original edge
				model.delete(act_edge);
				model.add(wp);
				model.add(new Edge(s, wp));
				model.add(new Edge(wp, t));
				
				if (model instanceof DefaultPathNetModel)
					((DefaultPathNetModel)model).enableModelEvents(true);
				
				panel.update();
		}		
	};
		
	public EdgeContextHandler(GraphicsPanel panel) {
		super(panel);
		
		edge_items = new JMenuItem[6];
	}

	public JMenuItem[] getMenuItems(final Point p) {		
		if (!acceptsPoint(p))
			return null;

		// update the inner point and waypoint listener
		innerPointListener.setPoint(p);
		waypointListener.setPoint(p);
		
		// enable the inner point editor only if there are already inner points
		if (edge_items[4] != null&&act_edge!=null)
			edge_items[4].setEnabled(act_edge.getInnerPointsSize()>0);
		
		if (act_edge != null && act_edge.equals(getObjectAt(p))) 
			return edge_items;
			
		act_edge = (Edge)getObjectAt(p);
		
		// Item 0
		edge_items[0] = new JMenuItem("Delete edge");
		edge_items[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.delete(act_edge);
				act_edge = null;
			}
		});
		
		// Item 1
		edge_items[1] = new JMenuItem("Insert waypoint here");
		edge_items[1].addActionListener(waypointListener);
		
		// Item 2
		edge_items[2] = new JMenuItem("Add inner point");
		edge_items[2].addActionListener(innerPointListener);
		
		// Item 2
		edge_items[3] = SEPARATOR;
		
		// Item 3
		edge_items[4] = new JMenuItem("Edit inner points...");
		edge_items[4].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InnerPointEditor editor = new InnerPointEditor(act_edge);
				editor.setVisible(true);
			}			
		});
		edge_items[4].setEnabled(act_edge.getInnerPointsSize()>0);
		
		// Item 4
		edge_items[5] = new JMenuItem("Properties...");
		edge_items[5].addActionListener(getDefaultPropertiesActionListener(act_edge, p, model));
		
		return edge_items;
	}
	
	public boolean acceptsPoint(Point p) {		
		return (getObjectAt(p)!=null && getObjectAt(p).getObjectType()==PathNetObject.EDGE);
	}

	public String getLabel(Point p) {
		return "Edge: " + super.getLabel(p);
	}
}
