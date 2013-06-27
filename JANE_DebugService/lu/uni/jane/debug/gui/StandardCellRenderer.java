/**
 * 
 */
package lu.uni.jane.debug.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;


/**
 * @author adrian.andronache
 *
 */
public class StandardCellRenderer extends JTextArea implements TableCellRenderer
{
	public StandardCellRenderer() 
	{
		// set a border
		setBorder( BorderFactory.createEmptyBorder( 3, 5, 3, 5 ) );
	}

	public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column ) 
	{
		setText( value.toString() );		
		return this;
	}

}
