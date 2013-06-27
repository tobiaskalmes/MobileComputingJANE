package de.uni_trier.jane.service.beaconing;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.basetypes.Dispatchable;
import de.uni_trier.jane.service.Signal;
import de.uni_trier.jane.signaling.SignalListener;


/**
 * A beaconing service listener which has prevoiusly registered at a beconing service channel
 * will be notified when a beacon message has been transmitted, beacon information
 * from a neighbor node arrived, or when beacon information about a neighbor is no more
 * available.
 * @see de.uni_trier.jane.service.beaconing.BeaconingService_sync
 */
public interface BeaconingListener extends SignalListener{

    /**
     * This method is called when a beacon message for a new neighbor arrived, i.e.
     * information about this neighbor was not previously stored in the beaconing
     * service.
     * @param data the beacon information about the neighbor
     */
    public void setNeighbor(BeaconingData data);

    /**
     * This method is called when new information about a previously stored neighbor
     * arrived in a new beacon message.
     * @param data the new beacon information for the neighbor
     */
    public void updateNeighbor(BeaconingData data);

    /**
     * This method is called when no beacon information about the neighbor node was
     * received for a certain time interval or the neighbor was removed manually.
     * @param address the address of the removed neighbor
     */
    public void removeNeighbor(Address address);

    /**
     * This method is called after a beacon message has been transmitted.
     */
    public void notifyTransmission();
    
    /**
     * Use this signal in order to provide beacon information to all beaconing
     * listeners when a new neighbor appeared.
     */
    public static class SetNeighborSignal implements Signal {

        private BeaconingData data;

        /**
         * Construct a new set neighbor signal.
         * @param beaconInfo the beacon information of the new neighbor
         */
        public SetNeighborSignal(BeaconingData beaconInfo) {
            this.data = beaconInfo;
        }

        public void handle(SignalListener service) {
            BeaconingListener listener = (BeaconingListener)service;
            listener.setNeighbor(data);
        }

        public Dispatchable copy() {
        	return this;
//            BeaconingData dataCopy = data.copy();
//            if(dataCopy == data) {
//                return this;
//            }
//            return new SetNeighborSignal(dataCopy);
        }

        public Class getReceiverServiceClass() {
            return BeaconingListener.class;
        }
        
    }

    /**
     * Use this signal in order to provide updated beacon information
     * to all beaconing listeners.
     */
    public static class UpdateNeighborSignal implements Signal {

        private BeaconingData data;

        /**
         * Construct a new update neighbor signal.
         * @param data the updated beacon info
         */
        public UpdateNeighborSignal(BeaconingData data) {
            this.data = data;
        }

        public void handle(SignalListener service) {
            BeaconingListener listener = (BeaconingListener)service;
            listener.updateNeighbor(data);
        }

        public Dispatchable copy() {
        	return this;
//            BeaconingData dataCopy = data.copy();
//            if(dataCopy == data) {
//                return this;
//            }
//            return new UpdateNeighborSignal(dataCopy);
        }

        public Class getReceiverServiceClass() {
            return BeaconingListener.class;
        }
        
    }    

    /**
     * Use this signal in order to notify all beaconing listeners
     * about a removed neighbor.
     */
    public static class RemoveNeighborSignal implements Signal {

        private Address  address;

        /**
         * Construct a remove neighbor signal.
         * @param address the address of the neighbor to be removed
         */
        public RemoveNeighborSignal(Address  address) {
            this.address = address;
        }

        public void handle(SignalListener service) {
            BeaconingListener listener = (BeaconingListener)service;
            listener.removeNeighbor(address);
        }

        public Dispatchable copy() {
            return this;
        }

        public Class getReceiverServiceClass() {
            return BeaconingListener.class;
        }

    }
    
    /**
     * Use this signal in order to notify all beaconing listeners that the next beacon
     * has been transmitted.
     */
    public static class NotifyTransmissionSignal implements Signal {

        public void handle(SignalListener service) {
            BeaconingListener listener = (BeaconingListener)service;
            listener.notifyTransmission();
        }

        public Dispatchable copy() {
            return this;
        }

        public Class getReceiverServiceClass() {
            return BeaconingListener.class;
        }

    }

}
