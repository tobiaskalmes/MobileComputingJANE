/**
 * 
 */
package lu.uni.jane.debug.gui;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import lu.uni.jane.debug.service.DebugGlobalService;
import lu.uni.jane.debug.util.DebugMessage;
import lu.uni.jane.debug.util.FilterItem;

/**
 * @author adrian.andronache
 *
 */
public class PackageFilterTableModel extends AbstractTableModel
{
	private String[] columnNames = { "Log", "Package" };
	private Vector data;
	private ClassFilterTableModel classFilterTableModel;
	protected DebugGlobalService debugService;

	    public PackageFilterTableModel( DebugGlobalService debugService, ClassFilterTableModel model ) 
		{
			this.debugService = debugService;
	        this.data = this.debugService.getPackageVector();
			classFilterTableModel = model;
	    }
		
	    public int getColumnCount() 
		{
	        return columnNames.length;
	    }

	    public int getRowCount() 
		{
	        return data.size();
	    }

	    public String getColumnName( int col ) 
		{
	        return columnNames[col];
	    }

	    public Object getValueAt( int row, int col ) 
		{
			try 
			{
	            FilterItem item = (FilterItem)(data.get( row ));
	            switch( col ) 
	            {
					case 0:
						return new Boolean( item.FLAG );
					case 1:
						return item.NAME;
	            }
			} 
			catch( Exception e ) 
			{
			}
			
			return "nothing! o_O";
	    }

	    public void setValueAt( Object value, int row, int col ) 
		{
			try 
			{
	            FilterItem item = (FilterItem)(data.get( row ));
	            switch( col ) 
	            {
					case 0:
						item.FLAG = ((Boolean)value).booleanValue();
						// if a package is changed, all class in this package must be changed too!
						classFilterTableModel.packageChanged( item.NAME, item.FLAG );
						// well, after this, the service must clar the message table
						// and show the messages concerning the new filter data...
						// BLABLA BLA111
						debugService.handleFilterChange();
						
					case 1:
						item.NAME = (String)value;
	            }
			} 
			catch( Exception e ) 
			{
			}
			
			// fire the event
	        fireTableCellUpdated( row, col );
	    }
		
	    /*
	     * JTable uses this method to determine the default renderer/
	     * editor for each cell.  If we didn't implement this method,
	     * then the last column would contain text ("true"/"false"),
	     * rather than a check box.
	     */
	    public Class getColumnClass( int c ) 
		{
	        return getValueAt( 0, c ).getClass();
	    }
		
	    /*
	     * Don't need to implement this method unless your table's
	     * editable.
	     */
	    public boolean isCellEditable( int row, int col ) 
		{
	        //Note that the data/cell address is constant,
	        //no matter where the cell appears onscreen.
	        if( col > 0 ) 
			{
	            return false;
	        } 
			else 
			{
	            return true;
	        }
	    }

		// used to fire a row added event
		public void addedRow( int index )
		{
			// fire the event
			fireTableRowsInserted( index, index );
		}
}
