package de.uni_trier.jane.service.routing.gcr.map;

import java.io.Serializable;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.network.link_layer.LinkLayerAddress;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * A geographic cluster is defined by a regular polygon. Each network node whose position
 * is inside the regular polygon is assigned to the cluster defined by that polygon.
 */
public interface Cluster extends Serializable{

    /**
     * Get the address of the cluster. The address is a unique identifier of the
     * cluster within the cluster map.
     * @return the cluster address
     */
    public LinkLayerAddress getAddress();
    
    /**
     * Get the center position of the cluster.
     * @return the center position
     */
    public Position getCenter();
    
    /**
     * Check whether the network node with the given position is within the cluster.
     * Note, that a position lying on the polygon border may be assigned to the other
     * cluster sharing the same border. If the cluster map would have returned the other
     * cluster in this case this method must return false! In other words, the method has
     * to return true if and only if the cluster map would return this cluster for the
     * given position.
     * @param position the node position
     * @return true if the node is is within or on the cluster border (and assigned to this cluster).
     */
    public boolean isInside(Position position);
    
    /**
     * Let two clusters be termed as adjacent if their bounding polygons share a common
     * edge. Note, clusters whose bounding polygons are sharing only a common vertice
     * are not adjacent! The distance between two clusters defines the minimum number of
     * adjacent clusters one has to traverse in order to reach the given destination cluster.
     * @param cluster the desitnation cluster.
     * @return the minimum number of adjacent clusters to be traversed.
     */
    public int getDistance(Cluster cluster);

    /**
     * The shape of the cluster should consist of the bounding polygon,
     * the address string and the center position.
     * @return the shape of this cluster
     */
    public Shape getShape();
    
}
