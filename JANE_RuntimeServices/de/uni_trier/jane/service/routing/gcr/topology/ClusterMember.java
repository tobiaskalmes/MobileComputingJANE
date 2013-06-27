package de.uni_trier.jane.service.routing.gcr.topology;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.routing.gcr.map.*;

/**
 * This interface describes the information about neighbor devices obtained from
 * the cluster topology implementation. The information stored in this object
 * has not to be dynamic, i.e. position, canReach, getCluster, and getDistance
 * do not change over the time.
 */
public interface ClusterMember {

    /**
     * Get the address of the device.
     * @return the device address
     */
    public Address getAddress();

    /**
     * Get the position of the device.
     * @return the device position
     */
    public Position getPosition();

    /**
     * Get the cluster this device is located in.
     * @return the cluster
     */
    public Cluster getCluster();

    /**
     * Check whether this device can reach the given cluster, i.e. if the device
     * is inside the cluster, or has at least a neighbor which can reach the cluster.
     * @param cluster the cluster to be tested
     * @return true if the device can reach the cluster
     */
    public boolean canReach(Cluster cluster);

    /**
     * Get the number of hops needed to reach the given cluster. A hop number
     * of n means that the node has a neighbor which can reach the given cluster
     * with n-1 hops.
     * @param cluster the cluster to be tested
     * @return the number of hops, 0 if it is inside the cluster, or -1
     * if the cluster is not reachable from this node
     */
    public int getDistance(Cluster cluster);

}
