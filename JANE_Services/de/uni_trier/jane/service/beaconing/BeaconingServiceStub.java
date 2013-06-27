/*
 * Created on 11.03.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.beaconing;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.signaling.*;


public final class BeaconingServiceStub {
	private RuntimeOperatingSystem operatingSystem;
	private ServiceID BeaconingServiceServiceID;
	public BeaconingServiceStub(RuntimeOperatingSystem operatingSystem, ServiceID BeaconingServiceServiceID) {
		this.operatingSystem = operatingSystem;
		this.BeaconingServiceServiceID = BeaconingServiceServiceID;
	}

	public void registerAtService() {
		operatingSystem.registerAtService(BeaconingServiceServiceID, BeaconingService_sync.class);
	}

	public void unregisterAtService() {
		operatingSystem.unregisterAtService(BeaconingServiceServiceID, BeaconingService_sync.class);
	}

	private static final class GetOwnAddressSyncAccess implements ListenerAccess {
		public GetOwnAddressSyncAccess() {
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return BeaconingService_sync.class;
		}

		/* (non-Javadoc)
		 * @see de.uni_trier.jane.service.ListenerAccess#handle(de.uni_trier.jane.signaling.SignalListener)
		 */
		public Object handle(SignalListener listener) {
			return ((BeaconingService_sync) listener).getOwnAddress();
		}

	}
	public Address getOwnAddress() {
		return (Address) operatingSystem.accessSynchronous(BeaconingServiceServiceID, new GetOwnAddressSyncAccess());
	}

	private static final class AddBeaconDataSignal implements Signal {
		private Data beaconData;
		public AddBeaconDataSignal(Data beaconData) {
			this.beaconData = beaconData;
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return BeaconingService_sync.class;
		}

		public void handle(SignalListener listener) { 
			((BeaconingService_sync) listener).addBeaconData(beaconData);		
		}
	}
	public void addBeaconData(Data beaconData) {
		operatingSystem.sendSignal(BeaconingServiceServiceID, new AddBeaconDataSignal(beaconData));
	}

	private static final class HasBeaconDataSyncAccess implements ListenerAccess {
		private DataID dataID;
		public HasBeaconDataSyncAccess(DataID dataID) {
			this.dataID = dataID;
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return BeaconingService_sync.class;
		}

		/* (non-Javadoc)
		 * @see de.uni_trier.jane.service.ListenerAccess#handle(de.uni_trier.jane.signaling.SignalListener)
		 */
		public Object handle(SignalListener listener) {
			return new Boolean(((BeaconingService_sync) listener).hasBeaconData(dataID));
		}

	}
	public boolean hasBeaconData(DataID dataID) {
		return ((Boolean) operatingSystem.accessSynchronous(BeaconingServiceServiceID, new HasBeaconDataSyncAccess(dataID))).booleanValue();
	}

	private static final class RemoveBeaconDataSignal implements Signal{
		private DataID dataID;
		public RemoveBeaconDataSignal(DataID dataID) {
			this.dataID = dataID;
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return BeaconingService_sync.class;
		}

		/* (non-Javadoc)
		 * @see de.uni_trier.jane.service.Signal#handle(de.uni_trier.jane.signaling.SignalListener)
		 */
		public void handle(SignalListener listener) {
			((BeaconingService_sync) listener).removeBeaconData(dataID);
		}

	}
	public void removeBeaconData(DataID dataID) {
		operatingSystem.sendSignal(BeaconingServiceServiceID, new RemoveBeaconDataSignal(dataID));
	}

}