package de.uni_trier.jane.service.operatingSystem.manager;

import de.uni_trier.jane.basetypes.ListenerID;
import de.uni_trier.jane.service.event.ServiceEvent;

/**
 * @author goergen
 *
 * TODO comment class
 */
final class EvenDBEntry {

ServiceEvent eventByExample;
ListenerID listenerID;

/**
 * Constructor for class <code>EvenDBEntry</code>
 * @param eventByExample
 * @param listenerID
 */
public EvenDBEntry(ServiceEvent eventByExample, ListenerID listenerID) {
    this.eventByExample=eventByExample;
    this.listenerID=listenerID;
}

public ServiceEvent getEventByExample() {
    return this.eventByExample;
}

public ListenerID getListenerID() {
    return this.listenerID;
}

}