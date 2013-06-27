/*
 * Created on 19.07.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.network.link_layer.winfra.interfaces;

import de.uni_trier.jane.basetypes.DeviceID;
import de.uni_trier.jane.basetypes.Dispatchable;
import de.uni_trier.jane.basetypes.ServiceID;
import de.uni_trier.jane.service.Signal;
import de.uni_trier.jane.service.network.link_layer.LinkLayerMessage;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.signaling.SignalListener;

/**
 * @author christian.hiedels
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface InterfaceBSO {
	/**
	 * Store RuntimeOperatingSystems of Devices that are running a BaseStation Service in a List
	 */
	public abstract void registerBaseStation(RuntimeOperatingSystem ros);

	/**
	 * Send a Message to a target BaseStation
	 * @param message The Message
	 */
	public abstract void distributeMessage(LinkLayerMessage llm, DeviceID targetBS, DeviceID finalReceiver);
	
	public static final class BSOStub implements InterfaceBSO {
		private RuntimeOperatingSystem operatingSystem;
		private ServiceID BSOServiceID;
		public BSOStub(
			RuntimeOperatingSystem operatingSystem,
			ServiceID BSOServiceID) {
			this.operatingSystem = operatingSystem;
			this.BSOServiceID = BSOServiceID;
		}

		public void registerAtService() {
			operatingSystem.registerAtService(BSOServiceID, InterfaceBSO.class);
		}

		public void unregisterAtService() {
			operatingSystem.unregisterAtService(BSOServiceID, InterfaceBSO.class);
		}

		private static final class DistributeMessageSignal implements Signal {
			private LinkLayerMessage llm;
			private DeviceID targetBS;
			private DeviceID finalReceiver;
			public DistributeMessageSignal(
				LinkLayerMessage llm,
				DeviceID targetBS,
				DeviceID finalReceiver) {
				this.llm = llm;
				this.targetBS = targetBS;
				this.finalReceiver = finalReceiver;
			}

			public Dispatchable copy() {
				return this;
			}

			public Class getReceiverServiceClass() {
				return InterfaceBSO.class;
			}

			public void handle(SignalListener signalListener) {
				((InterfaceBSO) signalListener).distributeMessage(llm, targetBS, finalReceiver);
			}

		}
		public void distributeMessage(LinkLayerMessage llm, DeviceID targetBS, DeviceID finalReceiver) {
			operatingSystem.sendSignal(
				BSOServiceID,
				new DistributeMessageSignal(llm, targetBS, finalReceiver));
		}

		private static final class RegisterBaseStationSignal implements Signal {
			private RuntimeOperatingSystem ros;
			public RegisterBaseStationSignal(RuntimeOperatingSystem ros) {
				this.ros = ros;
			}

			public Dispatchable copy() {
				return this;
			}

			public Class getReceiverServiceClass() {
				return InterfaceBSO.class;
			}

			public void handle(SignalListener signalListener) {
				((InterfaceBSO) signalListener).registerBaseStation(ros);
			}

		}
		public void registerBaseStation(RuntimeOperatingSystem ros) {
			operatingSystem.sendSignal(
				BSOServiceID,
				new RegisterBaseStationSignal(ros));
		}

	}}