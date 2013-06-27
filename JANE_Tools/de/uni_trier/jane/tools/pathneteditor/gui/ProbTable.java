package de.uni_trier.jane.tools.pathneteditor.gui;

import java.awt.Color;
import java.awt.Point;

import javax.swing.JMenuItem;
import javax.swing.table.DefaultTableCellRenderer;

import de.uni_trier.jane.tools.pathneteditor.constants.PathNetConstants;
import de.uni_trier.jane.tools.pathneteditor.gui.contextmenu.ContextHandler;
import de.uni_trier.jane.tools.pathneteditor.gui.contextmenu.ContextMenu;
import de.uni_trier.jane.tools.pathneteditor.gui.contextmenu.WaypointAndTargetContextHandler;
import de.uni_trier.jane.tools.pathneteditor.gui.helper.AbstractAdvancedTableModel;
import de.uni_trier.jane.tools.pathneteditor.gui.helper.RowHeaderTable;
import de.uni_trier.jane.tools.pathneteditor.model.PathNetModel;
import de.uni_trier.jane.tools.pathneteditor.model.PathNetModelAdapter;
import de.uni_trier.jane.tools.pathneteditor.model.PathNetModelEvent;
import de.uni_trier.jane.tools.pathneteditor.objects.*;
import de.uni_trier.jane.tools.pathneteditor.tools.Settings;


/**
 * @author salewski
 *
 * A table to show the prob settings for each waypoint/target pair
 * of the PathNetModel
 */
public final class ProbTable extends RowHeaderTable implements PathNetConstants {
	// serializable
	static final long serialVersionUID = 3214168476169362815L;	
	
	private static final Color CELL_TARGET_BG = new Color(0, 50, 255);
	private static final Color CELL_WAYPOINT_BG = new Color(80, 120, 255);
	private static final Color CELL_TARGET_FG = Color.WHITE;
	private static final Color CELL_WAYPOINT_FG = CELL_TARGET_FG;
	private static final Color CELL_LOCKED_FG = Color.DARK_GRAY;
	private static final Color CELL_LOCKED_BG = Color.DARK_GRAY;
	private static final Color CELL_SELECTED_FG = Settings.getColor(TABLE_DEFAULT_FOREGROUND);
	private static final Color CELL_SELECTED_BG = Settings.getColor(TABLE_DEFAULT_BACKGROUND);
		
	private PathNetModel 	model;
	private MainTM			tableModel;
	
	/*
	 * The Row Header Table model
	 */
	private class MainTM extends AbstractAdvancedTableModel {

		static final long serialVersionUID = 1L;
		private PathNetModel model;
		
		public MainTM(PathNetModel model) {
			this.model = model;
			
			model.addPathNetListener(new PathNetModelAdapter() {
				public void objectAdded(PathNetModelEvent event) {
					update();
				}

				public void objectDeleted(PathNetModelEvent event) {
					objectAdded(event);
				}

				public void objectPropertyChanged(ObjectEvent e) {
				}

				public void modelDataCleared(PathNetModelEvent e) {				
					objectAdded(e);
				}
				
				public void modelDataLoaded(PathNetModelEvent e) {
					objectAdded(e);
				}
				
				public void modelSelectionChanged(PathNetModelEvent e) {
				}
				
				public void modelEventsReenabled(PathNetModelEvent e) {
					objectAdded(e);
				}
			});
		}
		
		public int getRowCount() {						
			return model.getTargetsAndWaypoints().length;
		}

		public int getColumnCount() {
			return model.getTargets().length;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {			
			if (rowIndex == columnIndex)
				return new TableCell("", CELL_LOCKED_FG, CELL_LOCKED_BG, rowIndex, columnIndex);
			
			Waypoint w = model.getTargetsAndWaypoints()[rowIndex];
			Target t = model.getTargets()[columnIndex];
			
			String s = "";
			Edge[] ed = model.getEdges(w);
			for (int i=0; i<ed.length; i++)
				s += model.getProb(w,t,ed[i])!=0 
					? ed[i].getDescription()+"["+i+"]: "+model.getProb(w, t, ed[i]) + "; "
					: "";
					
			return new TableCell(
					s,
					w.getObjectType() == TARGET ? CELL_TARGET_FG : CELL_WAYPOINT_FG ,
					w.getObjectType() == TARGET ? CELL_TARGET_BG : CELL_WAYPOINT_BG ,
					rowIndex,
					columnIndex
			);
		}
		
		public String getColumnName(int column) {			
			return model.getTargets()[column].getDescription();
		}
		
		public String getRowName(int row) {
			return row >= 0 ? model.getTargetsAndWaypoints()[row].getDescription() : "";
		}
		
		public String getColumnTooltip(int column) {
			return column >= 0 ? model.getTargets()[column].getTooltipText(model) : "";
		}
		
		public String getRowTooltip(int row) {
			return model.getTargetsAndWaypoints()[row].getTooltipText(model);
		}
		
		public String getTooltipText(int row, int column) {
			if (row == column)
				return null;
			
			Waypoint w = model.getTargetsAndWaypoints()[row];
			Target t = model.getTargets()[column];
			
			double[] probs = model.getProbs(w, t);
			Edge[] edges = model.getEdges(w);
								
			String s = "<html>Probabilities<br>From waypoint: <b>["
					+ w.getDescription()+ "]</b><br>To target: <b>[" +t.getDescription()+"]</b><br>";
			if (edges.length == 0)
				s += "<br>This waypoint has no edges";
			else {
				for (int i=0; i<probs.length; i++)
					s += "<br>["+edges[i].getDescription()+"]: " + probs[i];
			}
			
			return s + "</html>";
		}
	};
	
	/*
	 *  The TableCell class provides colors for the table cells
	 */
	private class TableCell {
		public Object 	value;
		public Color	fg, bg;
		public int		row, column;
		
		public TableCell(Object value, Color fg, Color bg, int row, int column) {			
			this.value = value;
			this.fg = fg;
			this.bg = bg;
			this.row = row;
			this.column = column;
		}
	};
	
	/*
	 * The TableCellRenderer colorizes the table cells
	 */
	private class TableCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		protected void setValue(Object value) {
			if (value instanceof TableCell) {
				TableCell tc = (TableCell)value;
				Color oldFG = getForeground();
				Color oldBG = getBackground();
								
				// if cell is selected
				if (tc.row == ProbTable.this.getSelectedRow() && tc.column == ProbTable.this.getSelectedColumn()) {
					setForeground(CELL_SELECTED_FG);
					setBackground(CELL_SELECTED_BG);
				}
				
				// if cell is NOT selected
				else {
					setForeground(tc.fg);
					setBackground(tc.bg);
				}				
				
				super.setValue(tc.value);				
			}
			
			else
				super.setValue(value);
		}
	};
		
	/*
	 * Constructors
	 */
	public ProbTable(PathNetModel model) {
		this.model = model;
		this.tableModel = new MainTM(model);
		init();
	}
		
	/*
	 * gui init
	 */
	private void init() {
		final ProbTable main_T = this;
		
		/*
		 * Main configuration for table
		 */
		main_T.setModel(tableModel);
		main_T.setAutoCreateColumnsFromModel(true);
		main_T.setTableCellRenderer(new TableCellRenderer());
		main_T.setColumnWidth(40);
		main_T.setRowHeaderWidth(50);
				
		/*
		 * Adding the context menu to this table column headers
		 */
		ContextMenu cm = new ContextMenu(main_T.getColumnTableHeader());
		
		cm.addContextHandler(new ContextHandler() {			
			private JMenuItem[] menuItems = new JMenuItem[4];
				
			public JMenuItem[] getMenuItems(Point p) {
				Waypoint w = model.getTargets()[main_T.getColumnTableHeader().columnAtPoint(p)];
				
				WaypointAndTargetContextHandler.getMenuItems(p, menuItems, w, model);
				
				return menuItems;
			}

			public boolean acceptsPoint(Point p) {				
				return true;
			}

			public String getLabel(Point p) {				
				return "Target Properties";
			}			
			
		});
		
		/*
		 * Adding the context menu to this table row headers
		 */
		ContextMenu cm2 = new ContextMenu(main_T.getRowHeaderTable());
		
		cm2.addContextHandler(new ContextHandler() {			
			private JMenuItem[] menuItems = new JMenuItem[4];
				
			public JMenuItem[] getMenuItems(Point p) {
				Waypoint w = model.getTargetsAndWaypoints()[main_T.getRowHeaderTable().rowAtPoint(p)];
				
				WaypointAndTargetContextHandler.getMenuItems(p, menuItems, w, model);
				
				return menuItems;
			}

			public boolean acceptsPoint(Point p) {				
				return true;
			}

			public String getLabel(Point p) {				
				return "Target and Waypoint Properties";
			}			
			
		});
		
	}	
}
