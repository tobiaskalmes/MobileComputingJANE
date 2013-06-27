/*
 * Created on 21.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.simulation.basetypes;

import de.uni_trier.jane.basetypes.*;


/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ExactClock implements RuntimeClock {

    private Clock clock;
    private double offset;

    public ExactClock(Clock clock) {
        this.clock = clock;
        offset = 0;
    }

    public double getTime() {
        return clock.getTime() + offset;
    }

    public double getDelta(double delta) {
        return delta;
    }

    public void setTime(double time) {
        double currentTime = clock.getTime();
        offset = time-currentTime;
    }

}
