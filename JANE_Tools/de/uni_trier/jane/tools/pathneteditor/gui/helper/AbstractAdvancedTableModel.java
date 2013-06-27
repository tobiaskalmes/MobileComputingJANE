/*
 * Created on May 4, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.uni_trier.jane.tools.pathneteditor.gui.helper;

import javax.swing.table.AbstractTableModel;

/**
 * @author steffen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class AbstractAdvancedTableModel extends AbstractTableModel implements AdvancedTableModel {

	public String getRowName(int row) {
		return "";
	}

	public String getColumnTooltip(int column) {	
		return null;
	}
	
	public String getRowTooltip(int row) {
		return null;
	}
	
	public String getTooltipText(int row, int col) {
		return null;
	}
	
	public void update() {
		fireTableStructureChanged();
	}
}
