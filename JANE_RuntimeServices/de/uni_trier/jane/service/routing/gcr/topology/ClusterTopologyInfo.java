package de.uni_trier.jane.service.routing.gcr.topology;

import de.uni_trier.jane.service.routing.gcr.map.*;

/**
 * This class describes the information provided by a topology control mechanism applied
 * over the edges resulting from adjacent clusters. The definition of adjacent depends on the
 * implementation of this interface. An example implementation might define two clusters
 * C1 and C2 as adjacent if there exist two connected nodes with one located in C1 and
 * the other located in C2 (a more complex implementation may for example define two
 * clusters as adjacent if their distance is below a certain value and there is a path
 * from C1 to C2).
 */
public interface ClusterTopologyInfo {
    
    /**
     * Get all devices which are currently visible to this node. In general,
     * this will be all one hop neighbors.
     * @return all visible devices
     */
    public ClusterMember[] getAllClusterMembers();

    /**
     * Get all devices visible to this node which are lying within the same cluster where
     * this device is located in.
     * @return all visible devices within this cluster
     */
    public ClusterMember[] getSameClusterMembers();

    /**
     * Get all devices visible to this node and which are lying within the given cluster
     * @param cluster the given cluster
     * @return all visible devices within the given cluster
     */
    public ClusterMember[] getClusterMembers(Cluster cluster);

    /**
     * Get the cluster where this device is currently located in.
     * @return the cluster where the device is currently located in
     */
    public ClusterInfo getCurrentCluster();
    
    /**
     * Get all clusters which can be reached from this one (i.e. which are adjacent).
     * @return all reachable clusters
     */
    public ClusterInfo[] getAdjacentClusters();

    /**
     * Get the reduced set of reachable clusters which are forming a planar graph.
     * @return the reduced set of reachable clusters
     */
    public ClusterInfo[] getPlanarGraphEdges();

    /**
     * Get all adjacent clusters which belong to the connected dominating set constructed
     * over all clusters. This method should always return all adjacent clusters true if
     * no dominating set construction is used.
     * @return the adjacent clusters within the dominating set
     */
    public ClusterInfo[] getDominatingSetClusters();

}
