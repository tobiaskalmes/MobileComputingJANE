/*
 * Created on 08.03.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.dominating_set;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.signaling.*;

public final class DominatingSetServiceStub {

	private RuntimeOperatingSystem operatingSystem;
	private ServiceID DominatingSetServiceServiceID;

	public DominatingSetServiceStub(RuntimeOperatingSystem operatingSystem, ServiceID DominatingSetServiceServiceID) {
		this.operatingSystem = operatingSystem;
		this.DominatingSetServiceServiceID = DominatingSetServiceServiceID;
	}

	public void register() {
		operatingSystem.registerAtService(DominatingSetServiceServiceID, DominatingSetService.class);
	}

	public void unregister() {
		operatingSystem.unregisterAtService(DominatingSetServiceServiceID, DominatingSetService.class);
	}

	private static final class IsMemberSyncAccess implements ListenerAccess {
		public IsMemberSyncAccess() {
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return DominatingSetService.class;
		}

		public Object handle(SignalListener service) {
			return ((DominatingSetService) service).isMember();
		}

	}
	public boolean isMember() {
		return ((Boolean) operatingSystem.accessSynchronous(
			DominatingSetServiceServiceID,
			new IsMemberSyncAccess())).booleanValue();
	}

}