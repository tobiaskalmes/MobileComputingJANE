package de.uni_trier.jane.service.routing.gcr.map;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * This interface partitions the plane by an infinite mesh of regular polygons
 * (i.e. regular triangles, regular squares, or regular hexagons). Each
 * regular polygon defines a geographical cluster, which is used to enable
 * clustering of network nodes without any message exchange. Each node is
 * assigned to exactly the cluster (i.e. the polygon) where it is located in.
 * Ties (i.e. nodes located at the border of adjacent polygons) have to be
 * broken in a deterministic well defined manner.
 */
public interface ClusterMap {

    /**
     * Get the cluster where a network node with the given position is located in.
     * @param position the position of the network node
     * @return the cluster
     */
    public Cluster getCluster(Position position);

    public Cluster getCluster(Address address);
    
    /**
     * Use this method in order to dislpay the infinite mesh of polygons
     * bounded by a visualization rectangle. For each cluster the border,
     * the center, and its cluster address should be displayed in the shape.
     * @return the shape of the cluster map
     */
    public Shape getShape();
    
    public boolean isInReach(Address source, Address destination);
    public int getRequiredBits();

    public double getClusterDiameter();
}
