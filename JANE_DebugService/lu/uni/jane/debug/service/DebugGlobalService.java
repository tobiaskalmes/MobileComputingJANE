/**
 * 
 */
package lu.uni.jane.debug.service;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import lu.uni.jane.debug.gui.DebugSwingFrame;
import lu.uni.jane.debug.util.Debug;
import lu.uni.jane.debug.util.DebugMessage;
import lu.uni.jane.debug.util.FilterItem;

import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.EndpointClassID;
import de.uni_trier.jane.service.operatingSystem.RuntimeEnvironment;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.simulation.service.GlobalOperatingSystem;
import de.uni_trier.jane.simulation.service.GlobalService;
import de.uni_trier.jane.visualization.shapes.Shape;


/**
 * This is the global debug service class.
 * 
 * @author Adrian Andronache
 *
 */
public class DebugGlobalService implements GlobalService, GlobalLogSignal
{
	// the service id
	private ServiceID serviceID;
	
	// the GUI frame
	protected DebugSwingFrame frame;
	
	// the RuntimeEnvironment
	protected RuntimeEnvironment runtimeEnvironment;
	
	// log levels
	int startLevel = -1;
	int endLevel = -1;

	// this vector stores the messages.
	Vector messages;
	// the packages wich are sending debug messages.
	// vector is needed for the jtable model...
	Vector packageVector;
	// an hashmap to map package<->index in the vector
	HashMap packageIndex;
	// the classes wich are sending debug messages
	// vector is needed for the jtable model...
	Vector classVector;
	// an hashmap to map class<->index in the vector
	HashMap classIndex;
	
	/**
	 * Static createInstance method.
	 * @param serviceUnit
	 */
    public static void createInstance( ServiceUnit serviceUnit ) 
	{
    	serviceUnit.addService( new DebugGlobalService() );
    }
	
	/**
	 * Constructor
	 */
	public DebugGlobalService() 
	{
		serviceID = new EndpointClassID( getClass().getName() );
		messages = new Vector();
		
		// init vectors and hasmaps
		packageVector = new Vector();
		packageIndex = new HashMap();
		classVector = new Vector();
		classIndex = new HashMap();
		
		// create and show the debug frame
		frame = new DebugSwingFrame( this, "Global debug service" );
	}
	
	//
	// ########## GlobalService methods ##########
	//
	
	public void start( GlobalOperatingSystem globalOS ) 
	{
		runtimeEnvironment = globalOS;
		// set the OS for the debug proxy
		Debug.setRuntimeEnvironment( runtimeEnvironment );
	}

	public ServiceID getServiceID() 
	{
		return null;
	}

	public void finish() 
	{
		
	}

	public Shape getShape() 
	{
		return null;
	}

	public void getParameters( Parameters parameters ) 
	{
		
	}

	//
	// ########## LogSignal methods ##########
	//
	
	/**
	 * Log message handle method.
	 * 
	 * @param message The log message.
	 */
	public void logGlobalSignal( DebugMessage message ) 
	{
		boolean log = false;
		
		// log level check
		// ---------------
		// check if the message log level is whitin the log level span.
		if( startLevel == endLevel )
		{
			if( startLevel == -1 ) log = true;
			else
				if( startLevel == message.getLevel() ) log = true;
		}
		else
		{
			if( (message.getLevel() >= startLevel) && ( startLevel > endLevel ) ) log = true;
			else 
				if( (message.getLevel() >= startLevel) && (message.getLevel() <= endLevel) ) log = true;
		}
		
		// package check
		// -------------
		// make an package item whit flag = flase		
		if( packageIndex.containsKey( message.getPackageName() ) )
		{
			// if the package is present get its index in the vector
			int index = ((Integer)packageIndex.get( message.getPackageName() )).intValue();

			// the class flag is important only if the log is not false yet.
			if( log ) log = ((FilterItem)packageVector.get( index )).FLAG;
		}
		else
		{
			// class not found -> its a new debugging class
			// make an class item whit flag = true		
			FilterItem messagePackage = new FilterItem( new String(message.getPackageName()), true );
			
			// insert the item into the vector
			packageVector.add( messagePackage );
			// get its index...
			int index = packageVector.size() - 1;
			// map class<->index
			packageIndex.put( message.getPackageName(), new Integer( index ) );
			// notify the GUI
			frame.addedPackageRow( index );
		}
		
		// class check
		// -----------
		if( classIndex.containsKey( message.getFullClassName() ) )
		{
			// if the class is present get its index in the vector
			int index = ((Integer)classIndex.get( message.getFullClassName() )).intValue();

			// the class flag is important only if the log is not false yet.
			if( log ) log = ((FilterItem)classVector.get( index )).FLAG;
		}
		else
		{
			// class not found -> its a new debugging class
			// make an class item whit flag = true		
			FilterItem messageClass = new FilterItem( new String(message.getFullClassName()), true );
			
			// insert the item into the vector
			classVector.add( messageClass );
			// get its index...
			int index = classVector.size() - 1;
			// map class<->index
			classIndex.put( message.getFullClassName(), new Integer( index ) );
			// notify the GUI
			frame.addedClassRow( index );
		}
		
		// now log the message... -.-
		// add the message to the vector
		messages.add( message );
		// and show the message in the console if required
		if( log ) frame.log( message );
			
	}
	
	//
	// ########## Debugger methods ##########
	//
	
	/**
	 * Get methods...
	 */
	public Vector getMessages()			{ return messages; }
	public Vector getPackageVector()	{ return packageVector; }
	public Vector getClassVector()		{ return classVector; }
	
	/**
	 * Sets the level span of the debug service.
	 * The service will log messages whit levels between start and end.
	 */
	public void setLogLevelSpan( int start, int end )
	{
		this.startLevel = start;
		this.endLevel = end;
		handleFilterChange();
	}
	
	/**
	 * Sets the log level.
	 * Only the messages whit this log level will be logged.
	 * If level = -1 all levels will be logged.
	 * @param level
	 */
	public void setLogLevel( int level )
	{
		setLogLevelSpan( level, level );
	}

	/**
	 * Sets the debug flag for a package.
	 * Also changes the flag for all classes wich are contained in the package.
	 * 
	 * @param packageName
	 * @param flag
	 */
	public void setPackage( String packageName, boolean flag )
	{
		// check if the package exists
		if( packageIndex.containsKey( packageName ) )
		{
			// package found, get the index
			int index = ((Integer)packageIndex.get( packageName )).intValue();
			// now set the flag
			((FilterItem)packageVector.get( index )).FLAG = flag;
			
			// now set the flag for each class in this package
			// get the size of the class vector
			int cSize = classVector.size();
			// search for classes
			for( int j = 0; j < cSize; j++ )
			{
				String className = ((FilterItem)classVector.elementAt( j )).NAME;
				
				// check if the class belongs to the package
				if( className.startsWith( packageName ) )
				{
					// if yes set the flag
					((FilterItem)classVector.elementAt( j )).FLAG = flag;
				}
			}	
		}		
	}
	
	/**
	 * Sets the debug flag for a class.
	 * 
	 * @param className
	 * @param flag
	 */
	public void setClass( String className, boolean flag )
	{
		// check if the class is into the vector
		if( classIndex.containsKey( className ) )
		{
			// get the index
			int index = ((Integer)classIndex.get( className )).intValue();
			// set the flag
			((FilterItem)classVector.elementAt( index )).FLAG = flag;
		}
	}

	/**
	 * Called when the filter rules change.
	 * Clear the message table and fill it whit messages maching the new filter rules.
	 */
	public void handleFilterChange()
	{
		// clear the message table
		frame.clearMessages();
		
		// and show the messages matching the new rules
		boolean log = false;
		
		// check all messagesw
		for( int i = 0; i < messages.size(); i++ )
		{
			// get the message
			DebugMessage message = (DebugMessage)messages.get( i );
			
			// log level check
			// ---------------
			// check if the message log level is whitin the log level span.
			if( startLevel == endLevel )
			{
				if( startLevel == -1 ) log = true;
				else
					if( startLevel == message.getLevel() ) log = true;
			}
			else
			{
				if( (message.getLevel() >= startLevel) && ( startLevel > endLevel ) ) log = true;
				else 
					if( (message.getLevel() >= startLevel) && (message.getLevel() <= endLevel) ) log = true;
			}
			
			// perform package+class check only if the log level alows to show this message
			if( log )
			{
				// get the package index
				int index = ((Integer)packageIndex.get( message.getPackageName() )).intValue();

				// check the package log flag
				log = ((FilterItem)packageVector.get( index )).FLAG;
				
				// now, if log is still true, check the log flag of the class
				if( log )
				{
					// get the class index
					index = ((Integer)classIndex.get( message.getFullClassName() )).intValue();

					// the class flag is important only if the log is not false yet.
					log = ((FilterItem)classVector.get( index )).FLAG;
				}
			}
			
			// show the message in the console if allowed
			if( log ) frame.log( message );
		}
	}
}
