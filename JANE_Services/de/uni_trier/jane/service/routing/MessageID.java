package de.uni_trier.jane.service.routing;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.network.link_layer.*;

/**
 * This class describes a unique identifier of a routing message.
 */
public class MessageID extends ID {

	private static final int PRIME = 1000003;

	private Address address;
	private long sequenceNumber;
	
	/**
	 * Construct a message ID.
	 * @param address the device address
	 * @param sequenceNumber a unique sequnce number on this device
	 */
	public MessageID(Address address, long sequenceNumber) {
		this.address = address;
		this.sequenceNumber = sequenceNumber;
	}
	
	/**
	 * Returns the simulated coding size of this object in bits 
	 * @return the coding size in bits
	 */
	public int getCodingSize(){
	    return address.getCodingSize()+8*8;
	}

	public String toString() {
		return "[" + address + ":" + sequenceNumber + "]";
	}

//	public int compareTo(Object object) {
//		MessageID messageID = (MessageID)object;
//		int result = address.compareTo(messageID.address);
//		if(result != 0) {
//			return result;
//		}
//		if(sequenceNumber < messageID.sequenceNumber) {
//			return -1;
//		}
//		if(sequenceNumber > messageID.sequenceNumber) {
//			return 1;
//		}
//		return 0;
//	}

    public int hashCode() {
        final int PRIME = 1000003;
        int result = 0;
        if (address != null) {
            result = PRIME * result + address.hashCode();
        }
        result = PRIME * result + (int) (sequenceNumber >>> 32);
        result = PRIME * result + (int) (sequenceNumber & 0xFFFFFFFF);

        return result;
    }

    public boolean equals(Object oth) {
        if (this == oth) {
            return true;
        }

        if (oth == null) {
            return false;
        }

        if (oth.getClass() != getClass()) {
            return false;
        }

        MessageID other = (MessageID) oth;
        if (this.address == null) {
            if (other.address != null) {
                return false;
            }
        } else {
            if (!this.address.equals(other.address)) {
                return false;
            }
        }

        if (this.sequenceNumber != other.sequenceNumber) {
            return false;
        }

        return true;
    }


    /**
     * @return Returns the sequenceNumber.
     */
    public long getSequenceNumber() {
        return sequenceNumber;
    }
}
