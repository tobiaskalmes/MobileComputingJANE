/**
 * 
 */
package lu.uni.jane.debug.util;

import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.basetypes.ServiceID;

/**
 * @author adrian.andronache
 *
 * Used to transport the information about a debug log.
 *
 */
public class DebugMessage 
{
	// the device id 
	protected DeviceID deviceID;
	// the service id
	protected ServiceID serviceID;
	// the log level
	protected int level;
	// the line number
	protected int line;
	// triggering method infos
	protected String methodName;
	protected String fullClassName;
	protected String className;
	protected String packageName;
	// the message to log
	protected String message;
	
	
	/**
	 * Constructor which only needs the the StackTraceElement.
	 */
	public DebugMessage
			( 
				StackTraceElement stackTraceElement, 
				String message, 
				int level,
				DeviceID deviceID,
				ServiceID serviceID
			)
	{
		// set the log level
		this.level = level;
		// set the DeviceID
		this.deviceID = deviceID;
		// set the service id
		this.serviceID = serviceID;
		// the line number
		this.line = stackTraceElement.getLineNumber();
		// the method name
		this.methodName = stackTraceElement.getMethodName();
		// the full qualified class name
		this.fullClassName = stackTraceElement.getClassName();
		// the class name
        this.className = Debug.extractSimpleClassName( this.fullClassName );
		// the package name
        this.packageName = Debug.extractPackageName( this.fullClassName );
		// the message to log
		this.message = message;
	}
	
	/**
	 * Constructor which only needs the the full qualified class name.
	 */
	public DebugMessage
			( 
				String fqcn, 
				String methodName, 
				int lineNumber, 
				String message, 
				int level,
				DeviceID deviceID,
				ServiceID serviceID
			)
	{
		// set the log level
		this.level = level;
		// set the DeviceID
		this.deviceID = deviceID;
		// set the service id
		this.serviceID = serviceID;
		// the line number
		this.line = lineNumber;
		// the method name
		this.methodName = methodName;
		// the full qualified class name
		this.fullClassName = fqcn;
		// the class name
        this.className = Debug.extractSimpleClassName( fqcn );
		// the package name
        this.packageName = Debug.extractPackageName( fqcn );
		// the message to log
		this.message = message;
	}
	
	/**
	 * Constructor.
	 */
	public DebugMessage
			( 
				String fullClassName, 
				String packageName, 
				String className, 
				String methodName, 
				int lineNumber, 
				String message, 
				int level,
				DeviceID deviceID,
				ServiceID serviceID
			)
	{
		// set the log level
		this.level = level;
		// set the DeviceID
		this.deviceID = deviceID;
		// set the service id
		this.serviceID = serviceID;
		// the line number
		this.line = lineNumber;
		// the method name
		this.methodName = methodName;
		// the full qualified class name
		this.fullClassName = fullClassName;
		// the class name
        this.className = className;
		// the package name
        this.packageName = packageName;
		// the message to log
		this.message = message;
	}

	/**
	 * Get member methods... o_o
	 */
	public int getLevel()				{ return this.level; }
	public DeviceID getDeviceID()		{ return this.deviceID; }
	public ServiceID getServiceID() 	{ return this.serviceID; }
	public int getLine()				{ return this.line; }
	public String getMethodName()		{ return this.methodName; }
	public String getFullClassName()	{ return this.fullClassName; }
	public String getClassName()		{ return this.className; }
	public String getPackageName()		{ return this.packageName; }
	public String getMessage()			{ return this.message; }
}
