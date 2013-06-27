/*
 * Created on May 4, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.uni_trier.jane.tools.pathneteditor.gui.helper;

import javax.swing.table.TableModel;

/**
 * @author steffen
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface AdvancedTableModel extends TableModel {
	
	public String getRowName(int row);
	
	public String getTooltipText(int row, int col);
	public String getColumnTooltip(int column);
	public String getRowTooltip(int row);

}
