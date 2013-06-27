/*
 * Created on 27.06.2005
 */
package de.uni_trier.jane.service.location_directory;

import de.uni_trier.jane.basetypes.Dispatchable;
import de.uni_trier.jane.service.Signal;
import de.uni_trier.jane.signaling.SignalListener;

/**
 * Any service requesting information from a location service must implement this interface.
 * 
 * @see LocationDirectoryService
 * @author Christoph Lange
 */
public interface LocationDirectoryEntryReplyHandler extends SignalListener {
    /**
     * The location service will send an instance of this to me to return an (address, location) pair.
     * 
     * @author Christoph Lange
     */
    public static class Reply implements Signal {
        static final long serialVersionUID = 7325608296105208358L;
        
        private LocationDirectoryEntry locationData;

        public Reply(LocationDirectoryEntry reply) {
            this.locationData = reply;
        }
        
        /**
         * call the client's <code>receiveReply</code> method
         * @see de.uni_trier.jane.service.Signal#handle(de.uni_trier.jane.signaling.SignalListener)
         * @see LocationDirectoryEntryReplyHandler#handleLocationDataReply(LocationDirectoryEntry)
         */
        public void handle(SignalListener listener) {
            ((LocationDirectoryEntryReplyHandler) listener).handleLocationDataReply(locationData);

        }

        public Dispatchable copy() {
            return this;
        }

        public Class getReceiverServiceClass() {
            return LocationDirectoryEntryReplyHandler.class;
        }

    }

    /**
     * @param address the address whose position the client asked for
     * @param location the location of the node with <code>address</code>
     */
    public void handleLocationDataReply(LocationDirectoryEntry reply);
}
