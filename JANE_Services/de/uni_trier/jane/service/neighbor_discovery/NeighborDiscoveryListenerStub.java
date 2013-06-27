
package de.uni_trier.jane.service.neighbor_discovery;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.operatingSystem.*;
import de.uni_trier.jane.signaling.*;

public final class NeighborDiscoveryListenerStub {

	private RuntimeOperatingSystem operatingSystem;

	public NeighborDiscoveryListenerStub(RuntimeOperatingSystem operatingSystem) {
		this.operatingSystem = operatingSystem;
	}

	public void setNeighborData(NeighborDiscoveryData neighborData) {
		operatingSystem.sendSignal(new SetNeighborDataSignal(neighborData));
	}

	public void updateNeighborData(NeighborDiscoveryData neighborData) {
		operatingSystem.sendSignal(new UpdateNeighborDataSignal(neighborData));
	}

	public void removeNeighborData(Address linkLayerAddress) {
		operatingSystem.sendSignal(new RemoveNeighborDataSignal(linkLayerAddress));
	}

    private static class SetNeighborDataSignal implements Signal {

        private NeighborDiscoveryData data;

        public SetNeighborDataSignal(NeighborDiscoveryData data) {
            this.data = data;
        }

        public void handle(SignalListener service) {
            NeighborDiscoveryListener listener = (NeighborDiscoveryListener)service;
            listener.setNeighborData(data);
        }

        public Dispatchable copy() {
        	return this;
//            NeighborDiscoveryData dataCopy = data.copy();
//            if(dataCopy == data) {
//                return this;
//            }
//            return new SetNeighborDataSignal(dataCopy);
        }

        public Class getReceiverServiceClass() {
            return NeighborDiscoveryListener.class;
        }
        
    }

    private static class UpdateNeighborDataSignal implements Signal {

        private NeighborDiscoveryData data;

        public UpdateNeighborDataSignal(NeighborDiscoveryData data) {
            this.data = data;
        }

        public void handle(SignalListener service) {
            NeighborDiscoveryListener listener = (NeighborDiscoveryListener)service;
            listener.updateNeighborData(data);
        }

        public Dispatchable copy() {
        	return this;
//            NeighborDiscoveryData dataCopy = data.copy();
//            if(dataCopy == data) {
//                return this;
//            }
//            return new UpdateNeighborDataSignal(dataCopy);
        }

        public Class getReceiverServiceClass() {
            return NeighborDiscoveryListener.class;
        }
        
    }    

    private static class RemoveNeighborDataSignal implements Signal {

        private Address address;

        public RemoveNeighborDataSignal(Address address) {
            this.address = address;
        }

        public void handle(SignalListener service) {
            NeighborDiscoveryListener listener = (NeighborDiscoveryListener)service;
            listener.removeNeighborData(address);
        }

        public Dispatchable copy() {
            return this;
        }

        public Class getReceiverServiceClass() {
            return NeighborDiscoveryListener.class;
        }

    }

}