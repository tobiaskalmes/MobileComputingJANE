/**
 * 
 */
package lu.uni.jane.debug.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import lu.uni.jane.debug.util.DebugMessage;

/**
 * @author adrian.andronache
 *
 */
public class ConsoleTablePane extends JPanel
{
    protected JScrollPane scrollPane;
    protected JTable logTable;
    protected ConsoleTableModel model;

    public ConsoleTablePane() 
	{
		super( false );

		model = new ConsoleTableModel();
		logTable = new JTable( model )
		{
	        public Component prepareRenderer(TableCellRenderer renderer,
                    int rowIndex, int vColIndex) {
Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
if (rowIndex % 2 == 0 && !isCellSelected(rowIndex, vColIndex)) {
c.setBackground( new Color( 220, 220, 220 ) );
} else {
// If not shaded, match the table's background
c.setBackground(getBackground());
}
return c;
}
		};
		model.addTableModelListener( logTable );
		
		// set the columns width
		TableColumn column = null;
		// index
		column = logTable.getColumnModel().getColumn( 0 );
		column.setPreferredWidth( 30 );
		column.setCellRenderer( new StandardCellRenderer() );
		// device id
		column = logTable.getColumnModel().getColumn( 1 );
		column.setPreferredWidth( 55 );
		column.setCellRenderer( new StandardCellRenderer() );
		// service id
		column = logTable.getColumnModel().getColumn( 2 );
		column.setPreferredWidth( 65 );
		column.setCellRenderer( new StandardCellRenderer() );
		// class name
		column = logTable.getColumnModel().getColumn( 3 );
		column.setPreferredWidth( 80 );
		column.setCellRenderer( new StandardCellRenderer() );
		// method name
		column = logTable.getColumnModel().getColumn( 4 );
		column.setPreferredWidth( 100 );
		column.setCellRenderer( new StandardCellRenderer() );
		// log line number
		column = logTable.getColumnModel().getColumn( 5 );
		column.setPreferredWidth( 50 );
		column.setCellRenderer( new StandardCellRenderer() );
		// log message
		column = logTable.getColumnModel().getColumn( 6 );
		column.setPreferredWidth( 644 );
		column.setCellRenderer( new MessageCellRenderer() );
		
		scrollPane = new JScrollPane( logTable );
		// set the pane size = sum(colums.size)
		scrollPane.setPreferredSize( new Dimension( 1024, 300 ) );
		
		//Add the scroll pane to this panel.
		setLayout( new GridLayout(1, 0) );
		add( scrollPane );
    }
	
    // adds or updates an row
    public void addMessage( DebugMessage message ) 
	{
		model.addMessage( message );
    }

	/**
	 * Used to clear the message table...
	 */
	public void clearMessages() 
	{
		model.clearMessages();
	}
	
	// scroll the table to the last row
	public void scrollDown()
	{
		//JScrollBar bar = scrollPane.getVerticalScrollBar();
		//bar.setValue( bar.getMaximum() );
		
		// get the index of the last row
		int index =  logTable.getRowCount() - 1;
		
		Rectangle rect = logTable.getCellRect( index, 0, true );
		logTable.scrollRectToVisible( rect );
		logTable.clearSelection();
		logTable.setRowSelectionInterval( index, index );
	}
}
