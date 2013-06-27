/*
 * Created on 22.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.visualization.deprecated_shapes;

import de.uni_trier.jane.basetypes.*;

/**
 * @author Hannes Frey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 * @deprecated
 */
public class ProgressPositionCalculator implements PositionCalculator {

    private DeviceID from;
    private DeviceID to;
    private double progress;

    /**
     * @param from
     * @param to
     * @param progress
     */
    public ProgressPositionCalculator(DeviceID from, DeviceID to, double progress) {
        this.from = from;
        this.to = to;
        this.progress = progress;
    }

    public Position calculatePosition(DeviceIDPositionMap addressPositionMap) {
        Position fromPosition = addressPositionMap.getPosition(from);
        Position toPosition = addressPositionMap.getPosition(to);
        return toPosition.sub(fromPosition).scale(progress).add(fromPosition);
    }

}
