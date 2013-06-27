/**
 * 
 */
package lu.uni.jane.debug.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import lu.uni.jane.debug.service.DebugGlobalService;
import lu.uni.jane.debug.util.DebugMessage;
import lu.uni.jane.debug.util.FilterItem;

/**
 * @author adrian.andronache
 *
 */
public class PackageFilterTablePane extends JPanel
{
    protected JScrollPane scrollPane;
    protected JTable table;
    protected PackageFilterTableModel model;
	protected DebugGlobalService debugService;

    public PackageFilterTablePane( DebugGlobalService debugService, ClassFilterTableModel classmodel ) 
	{
		super( false );

		this.debugService = debugService;
		model = new PackageFilterTableModel( debugService, classmodel );
		table = new JTable( model );
		model.addTableModelListener( table );
		
		// set the columns width
		TableColumn column = null;
		// index
		column = table.getColumnModel().getColumn( 0 );
		column.setPreferredWidth( 50 );
		// class name
		column = table.getColumnModel().getColumn( 1 );
		column.setPreferredWidth( 550 );

		scrollPane = new JScrollPane( table );
		// set the pane size = sum(colums.size)
		scrollPane.setPreferredSize( new Dimension( 600, 150 ) );
		
		//Add the scroll pane to this panel.
		setLayout( new GridLayout(1, 0) );
		add( scrollPane );
    }
	
    // used to notify the model that an row was added
    public void addedRow( int index ) 
	{
		model.addedRow( index );
    }

}
