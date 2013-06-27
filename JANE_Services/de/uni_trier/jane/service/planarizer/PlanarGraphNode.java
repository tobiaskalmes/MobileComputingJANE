package de.uni_trier.jane.service.planarizer;
import de.uni_trier.jane.basetypes.*;

/**
 * In a localized planar graph construction method, each mobile device uses
 * only information about neighbor nodes in its vincinity (e.g. its 1-hop and
 * 2-hop neighbors) in order to construct a local view of the complete planar
 * graph. Use this interface to describe the nodes of the locally constructed
 * fragment of the complete planar graph. Note, that edges between two nodes
 * are assumed to be bidirectional, i.e. if node v is adjacent to this node,
 * then this node will also be adjacent to v.
 * 
 * @author Hannes Frey
 */
public interface PlanarGraphNode extends NetworkNode {

	/**
	 * Get all adjacent neighbor nodes of this planar graph node. If this information
	 * is not available, the method should return null (e.g. when 2-hop neigbor beaconing
	 * is being applied and this node is a 2-hop neighbor).
	 * @return an array containing all planar graph neighbors (or null if this information
	 * is not available)
	 */
	public PlanarGraphNode[] getAdjacentNodes();

	/**
	 * 
	 * @return Returns true if this node has adjacent nodes
	 */
	public boolean hasAdjacentNodes();

	/**
	 * Get the adjacent neighbor node with the given address.
	 * @param address the address of the neighbor
	 * @return the neighbor node or null if there is no neighbor node with this address
	 */
	public PlanarGraphNode getAdjacentNode(Address address);

	/**
	 * This Method will return all neighbors (i.e. not only its planar graph neighbors)
	 * of this node. If this information is not available, the method should return null
	 * (e.g. when 2-hop neigbor beaconing is being applied and this node is a 2-hop neighbor).
	 * @return an array containing all neighbors (or null if this information is not
	 * available)
	 */
	public NetworkNode[] getAllOneHopNeighbors();

	/**
	 * The planar graph explorer will be used to explore the fragement of the planar graph by
	 * starting at a given start node. From there on face exploration will be applied until a
	 * stop node is reached for the first time. The complete path from the start node to the
	 * reached stop node will be returned by the planar graph explorer.
	 * @return true if this node is a stop node
	 */
	public boolean isStopNode();
	
	// a virtual node is not reachable directly
	public boolean isVirtual();
	
	// a virtual node has a relay node
	public NetworkNode getRelayNode();

}
