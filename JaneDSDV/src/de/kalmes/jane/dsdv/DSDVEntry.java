package de.kalmes.jane.dsdv;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Tobias
 * Date: 02.07.13
 * Time: 22:00
 * To change this template use File | Settings | File Templates.
 */
public class DSDVEntry {
    private String destination;
    private String nextHop;
    private int numberOfHops;
    private int sequenceNumber;
    private Date updateTime;

    public DSDVEntry(String destination, String nextHop, int sequenceNumber) {
        this.destination = destination;
        this.nextHop = nextHop;
        this.sequenceNumber = sequenceNumber;
        numberOfHops = 0;
        updateTime = new Date();
    }

    public void incHopCount() {
        ++numberOfHops;
    }

    public void setNextHop(String nextHop) {
        this.nextHop = nextHop;
    }

    public String getDestination() {
        return destination;
    }

    public String getNextHop() {
        return nextHop;
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
    }
}
