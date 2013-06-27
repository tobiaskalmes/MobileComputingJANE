/**
 * 
 */
package lu.uni.jane.debug.service;

import lu.uni.jane.debug.util.DebugMessage;
import de.uni_trier.jane.basetypes.Dispatchable;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.Signal;
import de.uni_trier.jane.service.operatingSystem.RuntimeEnvironment;
import de.uni_trier.jane.signaling.SignalListener;
import de.uni_trier.jane.simulation.service.GlobalOperatingSystem;

/**
 * @author Adrian Andronache
 *
 * This signal is used to send a debug message to the <code>DebugGlobalService</code>.
 * It uses the global operating system to send the signal.
 * This class is used by the <code>Debug</code> class, wich has a pointer to the global OS.
 */
public interface GlobalLogSignal 
{
	/**
	 * Implement this method to receive the log message signal.
	 * 
	 * @param message The log message.
	 */
	public abstract void logGlobalSignal( DebugMessage message );
	
	
	public static final class LogGlobalSignalStub 
	{
		private RuntimeEnvironment operatingSystem;
		private ServiceID logSignalServiceID;
		
		public LogGlobalSignalStub( RuntimeEnvironment operatingSystem, ServiceID logSignalServiceID ) 
		{
			this.operatingSystem = operatingSystem;
			this.logSignalServiceID = logSignalServiceID;
		}

		public void registerAtService() 
		{
			operatingSystem.registerAtService( this.logSignalServiceID, GlobalLogSignal.class );
		}

		public void unregisterAtService() 
		{
			operatingSystem.unregisterAtService( this.logSignalServiceID, GlobalLogSignal.class );
		}

		private static final class ReceiveGlobalLogSignal implements Signal 
		{
			/**
			 * Comment for <code>serialVersionUID</code>
			 */
			private static final long serialVersionUID = 1L;
			/**
			 * The log message.
			 */
			private DebugMessage message;
			
			public ReceiveGlobalLogSignal( DebugMessage message ) 
			{
				this.message = message;
			}

			public Dispatchable copy() 
			{
				return this;
			}

			public Class getReceiverServiceClass() 
			{
				return GlobalLogSignal.class;
			}

			public void handle( SignalListener signalListener ) 
			{
				((GlobalLogSignal)signalListener).logGlobalSignal( message );
			}

		}
		
		public void receiveGlobalLogSignal( DebugMessage message ) 
		{

			operatingSystem.sendSignal
			( 
					logSignalServiceID, 
					new ReceiveGlobalLogSignal( message ) 
			);
		}

	}
}
