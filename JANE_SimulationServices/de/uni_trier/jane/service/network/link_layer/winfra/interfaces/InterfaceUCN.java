/*
 * Created on 18.07.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.network.link_layer.winfra.interfaces;

import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.basetypes.Dispatchable;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.Signal;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.signaling.SignalListener;

/**
 * @author christian.hiedels
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface InterfaceUCN {
	/**
	 * Register the ROS of a BaseStation
	 */
	public abstract void registerBS(DeviceID deviceID,
			RuntimeOperatingSystem ros);
	public static final class UCNStub implements InterfaceUCN {
		private RuntimeOperatingSystem operatingSystem;
		private ServiceID UCNServiceID;
		public UCNStub(
			RuntimeOperatingSystem operatingSystem,
			ServiceID UCNServiceID) {
			this.operatingSystem = operatingSystem;
			this.UCNServiceID = UCNServiceID;
		}

		public void registerAtService() {
			operatingSystem.registerAtService(UCNServiceID, InterfaceUCN.class);
		}

		public void unregisterAtService() {
			operatingSystem.unregisterAtService(UCNServiceID, InterfaceUCN.class);
		}

		private static final class RegisterBSSignal implements Signal {
			private DeviceID deviceID;
			private RuntimeOperatingSystem ros;
			public RegisterBSSignal(
				DeviceID deviceID,
				RuntimeOperatingSystem ros) {
				this.deviceID = deviceID;
				this.ros = ros;
			}

			public Dispatchable copy() {
				return this;
			}

			public Class getReceiverServiceClass() {
				return InterfaceUCN.class;
			}

			public void handle(SignalListener signalListener) {
				((InterfaceUCN) signalListener).registerBS(deviceID, ros);
			}

		}
		public void registerBS(DeviceID deviceID, RuntimeOperatingSystem ros) {
			operatingSystem.sendSignal(UCNServiceID, new RegisterBSSignal(
				deviceID,
				ros));
		}

	}}