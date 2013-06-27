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


public final class DominatingSetListenerStub {

	private RuntimeOperatingSystem operatingSystem;
	
	public DominatingSetListenerStub(RuntimeOperatingSystem operatingSystem) {
		this.operatingSystem = operatingSystem;
	}

	private static final class UpdateMembershipSignal
		implements
			Signal {
		private boolean membership;
		public UpdateMembershipSignal(boolean membership) {
			this.membership = membership;
		}

		public Dispatchable copy() {
			return this;
		}

		public Class getReceiverServiceClass() {
			return DominatingSetListener.class;
		}

		public void handle(SignalListener service) {
			((DominatingSetListener) service).updateMembership(membership);
		}

	}
	public void updateMembership(boolean membership) {
		operatingSystem.sendSignal(new UpdateMembershipSignal(membership));
	}

}