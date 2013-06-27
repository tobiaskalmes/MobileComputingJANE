/*
 * Created on 21.01.2005
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.uni_trier.jane.hybrid.local.manager;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.hybrid.local.*;
import de.uni_trier.jane.hybrid.remote.RemoteOperatingSystemClient;
import de.uni_trier.jane.hybrid.remote.manager.*;
import de.uni_trier.jane.service.Service;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.service.operatingSystem.manager.*;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.visualization.shapes.Shape;

import java.rmi.RemoteException;

/**
 * @author Daniel Görgen
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RemoteService implements Service {
	  private ServiceContext  serviceContext;
	private Parameters parameters;
	private Class serviceClass;
	private RemoteOperatingSystemClient remoteClient;
    private ExecutionManager executionManager;
    private SyncObject syncObject;
    private HybridOperatingSystemServer server;

	/**
	 * 
	 * Constructor for class RemoteService 
	 *
	 * @param serviceContext
	 * @param parameters
	 * @param serviceClass
	 * @param remoteClient
	 * @param executionManager
	 * @param server
	 * @param syncObject
	 */
	public RemoteService(ServiceContext  serviceContext, 
	        Parameters parameters, 
	        Class serviceClass, 
	        RemoteOperatingSystemClient remoteClient, 
	        ExecutionManager executionManager,
	        HybridOperatingSystemServer server, SyncObject syncObject) {
	    this.syncObject=syncObject;
		this.serviceContext=serviceContext;
		this.parameters=parameters;
		this.serviceClass=serviceClass;
		this.remoteClient=remoteClient;
		this.executionManager=executionManager;
		this.server=server;
		
			
	}

	

   
   public ServiceID getServiceID() {
       
       return serviceContext.getServiceID();
   }

  

   public void finish() {
       try {
           
           remoteClient.handleFinish(executionManager.getCallerContext(),serviceContext);
       } catch (RemoteException e) {
          server.hybridFinishFailed(serviceContext);
          // e.printStackTrace();
       }

   }

   

   public Shape getShape() {

       try {
           //syncObject.notify();
           return remoteClient.getShape(serviceContext.getServiceID());
       } catch (RemoteException e) {
           server.hybridCallFailed(serviceContext.getServiceID());
           //e.printStackTrace();
           return null;
       }
   }

   public void getParameters(Parameters param) {
       param.addAll(parameters);

   }

   /**
    * 
    * TODO: comment method 
    * @return
    */
	public Class getServiceClass() {
		return serviceClass;
	}
}
