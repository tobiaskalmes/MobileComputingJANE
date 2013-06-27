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
public class MessageCellRenderer extends JTextArea implements TableCellRenderer
{
	public MessageCellRenderer() 
	{
		setLineWrap( true );
		setWrapStyleWord( true );
		setBorder( BorderFactory.createEmptyBorder( 3, 5, 3, 5 ) );
		
		//setBackground( Color.RED );
		//setForeground( Color.GREEN );
	}
	
	public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column ) 
	{
		setText( (String)value );
		setSize( table.getColumnModel().getColumn(column).getWidth(),getPreferredSize().height );
		
		if( table.getRowHeight( row ) != getPreferredSize().height ) 
		{
			table.setRowHeight( row, getPreferredSize().height );
		}
		
		return this;
	}

}
