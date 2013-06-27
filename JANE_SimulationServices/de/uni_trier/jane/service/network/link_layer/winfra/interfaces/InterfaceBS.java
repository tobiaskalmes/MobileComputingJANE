/*
 * Created on 26.07.2005
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
public interface InterfaceBS {
	public abstract void sendCellBroadcast(LinkLayerMessage llm);

	public abstract void receiveMessageFromBSO(LinkLayerMessage llm, DeviceID finalReceiver);

	public abstract void receiveMessageFromCN(LinkLayerMessage llm, DeviceID targetBSreceiver, DeviceID finalReceiver);
	
	public static final class BSStub implements InterfaceBS {
		private RuntimeOperatingSystem operatingSystem;
		private ServiceID BSServiceID;
		public BSStub(
			RuntimeOperatingSystem operatingSystem,
			ServiceID BSServiceID) {
			this.operatingSystem = operatingSystem;
			this.BSServiceID = BSServiceID;
		}

		public void registerAtService() {
			operatingSystem.registerAtService(BSServiceID, InterfaceBS.class);
		}

		public void unregisterAtService() {
			operatingSystem.unregisterAtService(BSServiceID, InterfaceBS.class);
		}

		private static final class SendCellBroadcastSignal implements Signal {
			private LinkLayerMessage llm;
			public SendCellBroadcastSignal(LinkLayerMessage llm) {
				this.llm = llm;
			}

			public Dispatchable copy() {
				return this;
			}

			public Class getReceiverServiceClass() {
				return InterfaceBS.class;
			}

			public void handle(SignalListener signalListener) {
				((InterfaceBS) signalListener).sendCellBroadcast(llm);
			}

		}
		public void sendCellBroadcast(LinkLayerMessage llm) {
			operatingSystem.sendSignal(
				BSServiceID,
				new SendCellBroadcastSignal(llm));
		}

		private static final class ReceiveMessageFromBSOSignal
			implements
				Signal {
			private LinkLayerMessage llm;
			private DeviceID finalReceiver;
			public ReceiveMessageFromBSOSignal(LinkLayerMessage llm, DeviceID finalReceiver) {
				this.llm = llm;
				this.finalReceiver = finalReceiver;
			}

			public Dispatchable copy() {
				return this;
			}

			public Class getReceiverServiceClass() {
				return InterfaceBS.class;
			}

			public void handle(SignalListener signalListener) {
				((InterfaceBS) signalListener).receiveMessageFromBSO(llm,finalReceiver);
			}

		}
		public void receiveMessageFromBSO(LinkLayerMessage llm, DeviceID finalReceiver) {
			operatingSystem.sendSignal(
				BSServiceID,
				new ReceiveMessageFromBSOSignal(llm,finalReceiver));
		}

		private static final class ReceiveMessageFromCNSignal implements Signal {
			private LinkLayerMessage llm;
			private DeviceID targetBSreceiver;
			private DeviceID finalReceiver;
			public ReceiveMessageFromCNSignal(
				LinkLayerMessage llm,
				DeviceID targetBSreceiver,
				DeviceID finalReceiver) {
				this.llm = llm;
				this.targetBSreceiver = targetBSreceiver;
				this.finalReceiver = finalReceiver;
			}

			public Dispatchable copy() {
				return this;
			}

			public Class getReceiverServiceClass() {
				return InterfaceBS.class;
			}

			public void handle(SignalListener signalListener) {
				((InterfaceBS) signalListener).receiveMessageFromCN(
					llm,
					targetBSreceiver,
					finalReceiver);
			}

		}
		public void receiveMessageFromCN(
			LinkLayerMessage llm,
			DeviceID targetBSreceiver,
			DeviceID finalReceiver) {
			operatingSystem.sendSignal(
				BSServiceID,
				new ReceiveMessageFromCNSignal(llm, targetBSreceiver, finalReceiver));
		}

	}}