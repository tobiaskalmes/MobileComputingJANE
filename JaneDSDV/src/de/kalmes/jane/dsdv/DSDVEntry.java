package de.kalmes.jane.dsdv;

import de.uni_trier.jane.basetypes.Address;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Tobias
 * Date: 02.07.13
 * Time: 22:00
 * To change this template use File | Settings | File Templates.
 */
public class DSDVEntry implements Serializable {
    private Address destination;
    private Address nextHop;
    private int     numberOfHops;
    private int     sequenceNumber;
    private Date    updateTime;

    public DSDVEntry(Address destination, Address nextHop, int sequenceNumber) {
        this.destination = destination;
        this.nextHop = nextHop;
        this.sequenceNumber = sequenceNumber;
        numberOfHops = 0;
        updateTime = new Date();
    }

    public static DSDVEntry createNewEntry(DSDVEntry oldEntry) {
        DSDVEntry newEntry = new DSDVEntry(oldEntry.getDestination(), oldEntry.nextHop, oldEntry.sequenceNumber);
        newEntry.numberOfHops = oldEntry.numberOfHops;
        newEntry.updateTime = oldEntry.updateTime;
        return newEntry;
    }

    public void incHopCount() {
        ++numberOfHops;
    }

    public void resetHopCount() {
        numberOfHops = 0;
    }

    public Address getDestination() {
        return destination;
    }

    public Address getNextHop() {
        return nextHop;
    }

    public void setNextHop(Address nextHop) {
        this.nextHop = nextHop;
    }

    public int getNumberOfHops() {
        return numberOfHops;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void update(DSDVEntry newEntry) {
        sequenceNumber = newEntry.sequenceNumber;
        nextHop = newEntry.nextHop;
        numberOfHops = newEntry.numberOfHops;
        updateTime = newEntry.updateTime;
    }

    @Override
    public boolean equals(Object o) {
        return (o.getClass() == this.getClass()) && ((DSDVEntry) o).destination.compareTo(this.destination) == 0;
    }

    public DSDVEntry copy() {
        DSDVEntry copy = new DSDVEntry(this.destination, this.nextHop, this.sequenceNumber);
        copy.updateTime = this.updateTime;
        copy.nextHop = this.nextHop;
        return copy;
    }
}
