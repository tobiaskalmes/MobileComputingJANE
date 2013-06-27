/*
 * Created on 20.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.traffic;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.locationManager.basetypes.*;
import de.uni_trier.jane.signaling.*;
import de.uni_trier.jane.simulation.service.*;


public final class TrafficProxyServiceStub {

	private GlobalOperatingSystem operatingSystem;
	
	public TrafficProxyServiceStub(GlobalOperatingSystem operatingSystem) {
		this.operatingSystem = operatingSystem;
	}
	
	private static final class StartUnicastSignal implements Signal {
		private DeviceID receiver;
		private int payload;
		public StartUnicastSignal(DeviceID  receiver, int payload) {
			this.receiver = receiver;
			this.payload = payload;
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return TrafficProxyService.class;
		}

		public void handle(SignalListener service) {
			((TrafficProxyService) service).startUnicast(receiver, payload);
		}
		

	}
	
	private static final class StartAnycastSignal implements Signal {
		private int payload;
		private GeographicLocation location;
		
		
		public StartAnycastSignal(GeographicLocation location, int payload) {
			this.payload = payload;
			this.location=location;
		}
		public void handle(SignalListener service) {
			((TrafficProxyService) service).startAnyCast(location,payload);
			
		}
		public Dispatchable copy() {
			return this;
		}
		public Class getReceiverServiceClass() {
			return TrafficProxyService.class;
		}
		
	}
	
	public void startUnicast(DeviceID sender, DeviceID receiver, int payload) {
		operatingSystem.sendSignal(sender, TrafficProxyService.SERVICE_ID, new StartUnicastSignal(receiver, payload));
	}
	
	public void startAnyCast(DeviceID sender,GeographicLocation location,int payload) {
		operatingSystem.sendSignal(sender, TrafficProxyService.SERVICE_ID, new StartAnycastSignal(location,payload));
	}
	

}