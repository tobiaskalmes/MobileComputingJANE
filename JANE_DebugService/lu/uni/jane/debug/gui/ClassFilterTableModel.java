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
public class ClassFilterTableModel extends AbstractTableModel
{
	   private String[] columnNames = { "Log", "Class" };
		
	    private Vector data;
		protected DebugGlobalService debugService;

	    public ClassFilterTableModel( DebugGlobalService debugService ) 
		{
			this.debugService = debugService;
	        data = debugService.getClassVector();
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
	            FilterItem classItem = (FilterItem)(data.get( row ));
	            switch( col ) 
	            {
					case 0:
						return new Boolean( classItem.FLAG );
					case 1:
						return classItem.NAME;
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
	            FilterItem classItem = (FilterItem)(data.get( row ));
	            switch( col ) 
	            {
					case 0:
						classItem.FLAG = ((Boolean)value).booleanValue();
						// now the service must clar the message table
						// and show the messages matching the new filter data
						debugService.handleFilterChange();
					case 1:
						classItem.NAME = (String)value;
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
		
		public void packageChanged( String packageName, boolean flag )
		{
			// now set the flag for each class in this package
			// get the size of the class vector
			int cSize = data.size();
			// search for classes
			for( int j = 0; j < cSize; j++ )
			{
				String className = ((FilterItem)data.elementAt( j )).NAME;
				
				// check if the class belongs to the package
				if( className.startsWith( packageName ) )
				{
					// if yes set the flag
					((FilterItem)data.elementAt( j )).FLAG = flag;
					// fire update event
					fireTableCellUpdated( j, 0 );
				}
			}	
		}
}
