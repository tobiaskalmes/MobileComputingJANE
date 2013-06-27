package de.uni_trier.jane.service.routing.gcr.topology;

import de.uni_trier.jane.service.routing.gcr.map.*;

/**
 * This interface is used by ClusterToplogyInfo in order to provide
 * additional Information to a cluster if it is a dominating set node
 * or not.
 */
public interface ClusterInfo extends Cluster {

    /**
     * Check whether this cluster is a dominatin set node or not.
     * Return always true if no dominating set construction is beeing
     * used.
     * @return true if it is a dominating set node
     */
    public boolean isDominatingSetCluster();

}
