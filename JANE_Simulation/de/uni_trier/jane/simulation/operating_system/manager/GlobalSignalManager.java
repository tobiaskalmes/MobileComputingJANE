/*
 * Created on Jun 16, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.simulation.operating_system.manager;

import java.util.Iterator;

import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.basetypes.DeviceIDIterator;
import de.uni_trier.jane.basetypes.ListenerID;
import de.uni_trier.jane.basetypes.TrajectoryMapping;
import de.uni_trier.jane.service.ListenerFinishedHandler;
import de.uni_trier.jane.service.operatingSystem.ServiceContext;
import de.uni_trier.jane.service.operatingSystem.manager.LocalSignalManager;
import de.uni_trier.jane.signaling.SignalListener;
import de.uni_trier.jane.simulation.global_knowledge.DeviceListener;
import de.uni_trier.jane.simulation.global_knowledge.GlobalKnowledge;

/**
 * @author goergen
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GlobalSignalManager extends LocalSignalManager {

	
	private DeviceKnowledge deviceKnowledge;
	private GlobalKnowledge globalKnowledge;


	/**
     * 
     * Constructor for class <code>GlobalSignalManager</code>
     * @param deviceKnowledge2
     * @param globalKnowledge
	 * @param eventTemplateReflectionDepth 
	 */
	public GlobalSignalManager(DeviceKnowledge deviceKnowledge2,GlobalKnowledge globalKnowledge, int eventTemplateReflectionDepth) {
		super(eventTemplateReflectionDepth);
		this.deviceKnowledge=deviceKnowledge2;
		this.globalKnowledge=globalKnowledge;
		globalKnowledge.addDeviceListener(new DeviceListener() {
			/* (non-Javadoc)
			 * @see de.uni_trier.jane.simulation.global_knowledge.DeviceListener#enter(de.uni_trier.jane.basetypes.DeviceID)
			 */
			public void enter(DeviceID deviceID) {
				LocalSignalManager signalManager=deviceKnowledge.getSignalManager(deviceID);
				Iterator iterator=IDListenerMap.keySet().iterator();
				while (iterator.hasNext()) {
					ListenerID element = (ListenerID) iterator.next();
					ListenerWrapper listenerWrapper;
					addListenerToDevice(signalManager, element);
					
				}
				

			}

			public void exit(DeviceID deviceID) {/*ignore*/}

			public void changeTrack(DeviceID deviceID,
					TrajectoryMapping trajectoryMapping, boolean suspended) {/*ignore*/}
		});
		
	
	}
	
	protected boolean registerListener(ServiceContext currentContext,
			SignalListener listener, ListenerID listenerID, boolean oneShot) {
		
		
		boolean ok=super.registerListener(currentContext, listener, listenerID,oneShot);
		if (ok){
			super.addFinishListener(currentContext.getOSContext(),listenerID, new ListenerFinishedHandler() {
				/* (non-Javadoc)
				 * @see de.uni_trier.jane.service.ListenerFinishedHandler#handleFinished(de.uni_trier.jane.basetypes.ListenerID)
				 */
				public void handleFinished(ListenerID listenerID) {
					finishAddAllDevices(executionManager.getCallerContext(),listenerID);

				}

				
			});
			DeviceIDIterator iterator=globalKnowledge.getNodes().iterator();
			while (iterator.hasNext()) {
				DeviceID element = (DeviceID) iterator.next();
				ok=addListenerToDevice(deviceKnowledge.getSignalManager(element),listenerID);
			
			}
		}
		return ok ;
	}
	
	private void finishAddAllDevices(ServiceContext callerContext, ListenerID listenerID) {
		DeviceIDIterator iterator=globalKnowledge.getNodes().iterator();
		while (iterator.hasNext()) {
			DeviceID element = (DeviceID) iterator.next();
			deviceKnowledge.getSignalManager(element).finish(callerContext,listenerID);
		
		}
		
	}

	/**
	 * @param signalManager
	 * @param element
	 */
	private boolean addListenerToDevice(LocalSignalManager signalManager, ListenerID element) {
		ListenerWrapper listenerWrapper=(ListenerWrapper)IDListenerMap.get(element);
		boolean ok;
		if (listenerWrapper.isOneShot()){
			ok=signalManager.registerOneShotListener(listenerWrapper.getContext(),listenerWrapper.getListener(),listenerWrapper.getListenerID());
		}else{
			ok=signalManager.registerListener(listenerWrapper.getContext(),listenerWrapper.getListener(),listenerWrapper.getListenerID());
		}
		signalManager.addFinishListener(listenerWrapper.getContext().getOSContext(),listenerWrapper.getListenerID(),new ListenerFinishedHandler() {
			
			public void handleFinished(ListenerID listenerID) {
				finish(executionManager.getCallerContext(),listenerID);
			}
		});
		return ok;
	}
}
