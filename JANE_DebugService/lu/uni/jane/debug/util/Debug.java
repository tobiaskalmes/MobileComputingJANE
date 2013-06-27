/**
 * 
 */
package lu.uni.jane.debug.util;

import lu.uni.jane.debug.service.DebugGlobalService;
import lu.uni.jane.debug.service.GlobalLogSignal;
import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.operatingSystem.OperatingSystem;
import de.uni_trier.jane.service.operatingSystem.RuntimeEnvironment;
import de.uni_trier.jane.service.operatingSystem.ServiceContext;
import de.uni_trier.jane.simulation.service.GlobalOperatingSystem;;

/**
 * @author Adrian Andronache
 * 
 * This class receives a debug log as String creates a <code>DebugMessage</code> 
 * and send it by signal to the <code>GlobalDebugService</code>.
 * To be able to do this, the class has a static pointer to the global runtimeEnvironment.
 * This static member is set by the <code>GlobalDebugService</code> 
 * at starting time (see the start() method of the GlobalDebugService).
 * 
 * To log a message just use the static method Debug.log( logLevel, DebugMessage ).
 * If there is no GlobalDebugService running, the log method will do nothing.
 */
public class Debug 
{
    // The RuntimeEnvironment
	protected static RuntimeEnvironment globalRuntimeEnvironment = null;
	
	// this flag shows if there is an debug service running or not.
	public static boolean RUN = false;

	/**
	 * Constructor.
	 */
	public Debug( boolean flag ) 
	{
		RUN = flag;
	}

	/**
	 * Sets the OS (static member).
	 * @param runtimeOS
	 */
	public static void setRuntimeEnvironment( RuntimeEnvironment runtimeEnv )
	{
		// set the RuntimeEnvironment
		globalRuntimeEnvironment = runtimeEnv;
		// if an RuntimeEnvironment is set then the debug must run
		RUN = true;
	}
	
	/**
	 * Used to log a debug message.
	 * This methods gets the StackTraceElement and pass
	 * it to the sendDebugMessage method.
	 * 
	 * @param message The debug message as string.
	 */
	public static void log( String message, int level )
	{
		// check if there is a debug service running:
		if( RUN ) 
		{
			// get the StackTraceElement, java 1.4 style...
			Throwable ex = new Throwable();
			StackTraceElement[] stackElements = ex.getStackTrace(); 
			StackTraceElement stackTraceElement = stackElements[1];
			
			// now send the log signal
			sendDebugMessage( stackTraceElement, message, level );
		}
	}
	
	/**
	 * Used to log a debug message. Sets the level = 0!
	 * This methods gets the StackTraceElement and pass
	 * it to the sendDebugMessage method.
	 * 
	 * @param message The debug message as string.
	 */
	public static void log( String message )
	{
		// check if there is a debug service running
		if( RUN ) 
		{			
			// get the StackTraceElement, java 1.4 style...
			Throwable ex = new Throwable();
			StackTraceElement[] stackElements = ex.getStackTrace();
			StackTraceElement stackTraceElement = stackElements[1];
			
			// now send the log signal
			sendDebugMessage( stackTraceElement, message, 0 );
		}
	}

	/**
	 * Extracts the information from the StackTraceElement
	 * and sends a DebugMessage by signal to the debug service.
	 * 
	 * @param stackTraceElement
	 */
	protected static void sendDebugMessage( StackTraceElement stackTraceElement, String message, int level )
	{	
		// get the context
		ServiceContext context = OperatingSystem.getCurrentContext();
		// the RuntimeEnvironment to be used
		RuntimeEnvironment runtimeEnvironment = null;
		
		// check if there is an context...
		if( context != null )
		{
			// set the right RuntimeEnvironment
			runtimeEnvironment = OperatingSystem.getRuntimeEnvironment();
		}
		else
		{
			// well, no context, use the globalRuntimeEnvironment
			runtimeEnvironment = globalRuntimeEnvironment;
		} 

		// get the device id
		DeviceID deviceID = runtimeEnvironment.getDeviceID();
		// get the service id
		ServiceID serviceID = runtimeEnvironment.getServiceID();
		
	
		
		// send the debug message.
		// get the debug service 
		ServiceID debugServiceID = runtimeEnvironment.getServiceIDs( DebugGlobalService.class )[0];
		// get the stub
		GlobalLogSignal.LogGlobalSignalStub debugService = new GlobalLogSignal.LogGlobalSignalStub( runtimeEnvironment, debugServiceID );
		// send the log signal
		debugService.receiveGlobalLogSignal( new DebugMessage( stackTraceElement, message, level, deviceID, serviceID ) );
	}
	
	/**
	 * Returns the package name.
	 * @param fullClassName
	 * @return
	 */
    public static String extractPackageName( String fullClassName )
	{
		if( (null == fullClassName) || ("".equals (fullClassName)) ) return "";

		// The package name is everything preceding the last dot.
		// Is there a dot in the name?
		int lastDot = fullClassName.lastIndexOf ('.');
	
		// Note that by fiat, I declare that any class name that has been
		// passed in which starts with a dot doesn't have a package name.
		if (0 >= lastDot) return "";
		
		// Otherwise, extract the package name.
		return fullClassName.substring( 0, lastDot );
	}

	/**
	 * Returns the simple class name.
	 * @param fullClassName
	 * @return
	 */
    public static String extractSimpleClassName( String fullClassName )
	{
		if( (null == fullClassName) || ("".equals (fullClassName)) )return "";
	
		// The simple class name is everything after the last dot.
		// If there's no dot then the whole thing is the class name.
		int lastDot = fullClassName.lastIndexOf ('.');
		if (0 > lastDot) return fullClassName;
		
		// Otherwise, extract the class name.
		return fullClassName.substring( ++lastDot );
	}

	/**
	 * Returns the direct class name.
	 * @param simpleClassName
	 * @return
	 */
    public static String extractDirectClassName( String simpleClassName )
	{
		if( (null == simpleClassName) || ("".equals (simpleClassName)) ) return "";
	
		// The direct class name is everything after the last '$', if there
		// are any '$'s in the simple class name.  Otherwise, it's just
		// the simple class name.
		int lastSign = simpleClassName.lastIndexOf ('$');
		if (0 > lastSign) return simpleClassName;
	
		// Otherwise, extract the last class name.
		// Note that if you have a multiply-nested class, that this
		// will only extract the very last one.  Extracting the stack of
		// nestings is left as an exercise for the reader.
		return simpleClassName.substring (++lastSign);
	}

	/**
	 * Returns the unmuge simple class name.
	 * @param simpleClassName
	 * @return
	 */
    public static String unmungeSimpleClassName( String simpleClassName )
	{
		if( (null == simpleClassName) || ("".equals (simpleClassName)) ) return "";
	
		// Nested classes are set apart from top-level classes by using
		// the dollar sign '$' instead of a period '.' as the separator
		// between them and the top-level class that they sit
		// underneath. Let's undo that.
		return simpleClassName.replace( '$', '.' );
	}
}
