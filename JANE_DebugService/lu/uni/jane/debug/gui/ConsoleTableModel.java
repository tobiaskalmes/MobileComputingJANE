/**
 * 
 */
package lu.uni.jane.debug.gui;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import lu.uni.jane.debug.util.DebugMessage;


class ConsoleTableModel extends AbstractTableModel 
{
    private String[] columnNames = { "#", "Device ID", "Service ID", "Class", "Method", "Line", "Message" };
	
    private Vector data;

    public ConsoleTableModel() 
	{
        data = new Vector();
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
            DebugMessage message = (DebugMessage)(data.get(row));
            switch( col ) 
            {
				case 0:
					return new Integer(row);
				case 1:
					return message.getDeviceID();
				case 2:
					return message.getServiceID();
				case 3:
					return message.getClassName();
				case 4:
					return message.getMethodName();
				case 5:
					return new Integer( message.getLine() );
				case 6:
					return message.getMessage();
            }
		} 
		catch( Exception e ) 
		{
		}
		
		return "nothing! o_O";
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

	/**
	 * Add a message to the vector and notify the table. 
	 * @param message
	 */
	public void addMessage( DebugMessage message ) 
	{
		data.add( message );
		int lastIndex = data.size() - 1;
		
		// fire the event
		fireTableRowsInserted( lastIndex, lastIndex );
	} 

	/**
	 * This method deletes all messages from the data vector.
	 * Used when the filter data change.
	 */
	public void clearMessages()
	{
		this.data.clear();
	}
}
