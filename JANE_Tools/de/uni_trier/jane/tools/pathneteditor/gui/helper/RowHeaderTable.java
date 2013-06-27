/*
 * Created on May 2, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.uni_trier.jane.tools.pathneteditor.gui.helper;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * @author steffen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RowHeaderTable extends JScrollPane {
	
	private static final long serialVersionUID = 3258134669454094392L;
	private static final Color ROW_HEADER_COLOR = Color.LIGHT_GRAY;
	
	private JTable 		dataTable;
	private JTable		headerTable;
	private JViewport	rowViewport;	
	
	private RowHeaderTableModel rowModel;	
	private TableCellRenderer	tableCellRenderer = new DefaultTableCellRenderer();
	private int					columnWidth = 30, rowHeaderWidth = 50;
	
	/*
	 * TableColumn Model for the data table
	 */
	private TableColumnModel dataColumnModel = new DefaultTableColumnModel() {
		private static final long serialVersionUID = 1L;
		
		public void addColumn(TableColumn tc) {
			if (tc.getModelIndex() == 0) {
				// drop row header column
				return;
			}
			
			tc.setMinWidth(25);
			tc.setPreferredWidth(columnWidth);
									
			super.addColumn(tc);
			
			tc.setCellRenderer(tableCellRenderer);
		}
	};
	
	/*
	 * TableColumn Model for the row header table 
	 */
	private TableColumnModel headerColumnModel = new DefaultTableColumnModel() {
		private static final long serialVersionUID = 1L;
		
		public void addColumn(TableColumn tc) {
			if (tc.getModelIndex() != 0) {
				// drop data columns
				return;
			}
			
			tc.setMinWidth(rowHeaderWidth);
			tc.setPreferredWidth(rowHeaderWidth);
			tc.setMaxWidth(tc.getPreferredWidth());
			
			super.addColumn(tc);			
		}
	};
	
	/*
	 * Intern model for this RowHeaderTable
	 */
	private class RowHeaderTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;
		
		private AdvancedTableModel 	model;		
			
		public RowHeaderTableModel(AdvancedTableModel model) {
			if (model == null)
				throw new NullPointerException("Argument null not allowed as AdvancedTableModel");
			
			this.model = model;		
		}
		
		public int getRowCount() {		
			return model.getRowCount();
		}

		public int getColumnCount() {
			return model.getColumnCount() + 1;
		}

		public String getColumnName(int columnIndex) {		
			return columnIndex == 0 ? "" : model.getColumnName(columnIndex - 1);
		}

		public Class getColumnClass(int columnIndex) {		
			return columnIndex == 0 ? "".getClass() : model.getColumnClass(columnIndex - 1);
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {		
			return columnIndex == 0 ? false : model.isCellEditable(rowIndex, columnIndex - 1);
		}

		public Object getValueAt(int rowIndex, int columnIndex) {		
			return columnIndex == 0
				? model.getRowName(rowIndex)
				: model.getValueAt(rowIndex, columnIndex - 1);
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			model.setValueAt(aValue, rowIndex, columnIndex - 1);		
		}

		public void addTableModelListener(TableModelListener l) {
			model.addTableModelListener(l);
		}

		public void removeTableModelListener(TableModelListener l) {
			model.removeTableModelListener(l);
		}
		
		/*
		 * Additional methods
		 */
		
		public void update() {
			fireTableStructureChanged();			
		}		
	};
	
	/*
	 * Should provide row headers (and a tooltip model ?) 
	 */

	public RowHeaderTable() {
		setModel(new DefaultAdvancedTableModel());
	}
	
	public RowHeaderTable(AdvancedTableModel model) {
		setModel(model);
	}
	
//	public RowHeaderTable(Object[][] data, String[] colNames, String[] rowNames) {
//		TableModel model = new DefaultTableModel(data, colNames);
//		setModel(new RowHeaderTableModel(model, new DefaultRowModel(rowNames)));
//	}

	public void setModel(final AdvancedTableModel model) {
		// check if old and new model are identical
		if (model == rowModel)
			return;
		
		// do not use a already formed table model (OBSOLETE!)
		if (model instanceof RowHeaderTableModel)
			rowModel = (RowHeaderTableModel)model;
		else
			rowModel = new RowHeaderTableModel(model);
		
		// Build RowHeaderTable
		dataTable = new JTable(rowModel, dataColumnModel);
		headerTable = new JTable(rowModel, headerColumnModel);
		
		dataTable.createDefaultColumnsFromModel();
		dataTable.setAutoCreateColumnsFromModel(true);
		headerTable.createDefaultColumnsFromModel();
		headerTable.setAutoCreateColumnsFromModel(true);

		dataTable.setSelectionModel(headerTable.getSelectionModel());
		dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		dataTable.setCellSelectionEnabled(true);
		
		headerTable.setBackground(headerTable.getTableHeader().getBackground());
		headerTable.setColumnSelectionAllowed(false);
		headerTable.setCellSelectionEnabled(false);
		
		rowViewport = new JViewport();
		rowViewport.setView(headerTable);
		rowViewport.setPreferredSize(headerTable.getMaximumSize());
		
		this.setViewportView(dataTable);
		this.setRowHeader(rowViewport);
		this.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, headerTable.getTableHeader());
		
		// set up tooltip managing
		headerTable.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {}
			
			public void mouseMoved(MouseEvent e) {
				int row = headerTable.rowAtPoint(e.getPoint());
				
				dataTable.setToolTipText(null);
				headerTable.setToolTipText(model.getRowTooltip(row));
			}
		});
		
		dataTable.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
			}
			
			public void mouseMoved(MouseEvent e) {
				int row = dataTable.rowAtPoint(e.getPoint());
				int col = dataTable.columnAtPoint(e.getPoint());
				
				headerTable.setToolTipText(null);
				dataTable.setToolTipText(model.getTooltipText(row, col));
			}
		});

		dataTable.getTableHeader().addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {}
			
			public void mouseMoved(MouseEvent e) {
				int col = dataTable.getTableHeader().columnAtPoint(e.getPoint());
								
				headerTable.setToolTipText(null);
				dataTable.getTableHeader().setToolTipText(model.getColumnTooltip(col));
			}
		});
				
	}

	// some additional methods	
	public JTable getRowHeaderTable() {
		return headerTable;
	}
	
	public JTableHeader getColumnTableHeader() {
		return dataTable.getTableHeader();
	}
	
	public void setColumnWidth(int columnWidth) {
		this.columnWidth = columnWidth;
	}
	
	public void setRowHeaderWidth(int rowHeaderWidth) {
		this.rowHeaderWidth = rowHeaderWidth;
	}
		
	/* ****************************************************************************
	 * 
	 * Reimplementation of important JTable functions widely used by this project *
	 * 
	 * ****************************************************************************/
	
	public int rowAtPoint(Point p) {
		return dataTable.rowAtPoint(p) >= 0 ? dataTable.rowAtPoint(p) : headerTable.rowAtPoint(p);
	}
	
	public int columnAtPoint(Point p) {
		return dataTable.columnAtPoint(p) >= 0 ? dataTable.columnAtPoint(p) : headerTable.columnAtPoint(p);
	}
	
	public void setTableHeader(JTableHeader header) {
		dataTable.setTableHeader(header);
	}
	
	public void setRowSelectionAllowed(boolean allowIt) {
		headerTable.setRowSelectionAllowed(allowIt);
		dataTable.setRowSelectionAllowed(allowIt);
	}
	
	public void setColumnSelectionAllowed(boolean allowIt) {
		dataTable.setColumnSelectionAllowed(allowIt);
	}
	
	public void setCellSelectionEnabled(boolean enableIt) {
		dataTable.setCellSelectionEnabled(enableIt);
	}
	
	public void setSelectionBackground(Color bgColor) {
		dataTable.setSelectionBackground(bgColor);
	}
	
	public void setSelectionForeground(Color fgColor) {
		dataTable.setSelectionForeground(fgColor);
	}
	
	public int getSelectedRow() {
		return dataTable.getSelectedRow();
	}
	
	public int getSelectedColumn() {
		return dataTable.getSelectedColumn();
	}
	
	public ListSelectionModel getSelectionModel() {
		return headerTable.getSelectionModel();
	}
	
	public RowHeaderTableModel getModel() {
		return rowModel;
	}
	
	public void setAutoCreateColumnsFromModel(boolean autoCreate) {
		dataTable.setAutoCreateColumnsFromModel(autoCreate);
	}
	
	public void setTableCellRenderer(TableCellRenderer renderer) {
		this.tableCellRenderer = renderer;
	}	
	
	public void addMouseListener(MouseListener l) {
		dataTable.addMouseListener(l);
	}
	
	public void addKeyListener(KeyListener l) {
		dataTable.addKeyListener(l);
	}
	
	/* *************************************************************************** */
	
	public static void main(String[] args) {
		JFrame f = new JFrame();	
	
		String[] data1 = new String[] { "a", "b", "c", "d", "e", "f", "g" };
		String[] data2 = new String[] { "1", "2", "3", "4", "5", "6", "7" };
		
		String[] colnames = new String[] { "c1", "c2", "c3", "c4", "c5", "c6" };
		String[] rownames = new String[] { "r1", "r2" };
		
		String[][] allData = new String[][] { data1, data2 };
		
	}
}
