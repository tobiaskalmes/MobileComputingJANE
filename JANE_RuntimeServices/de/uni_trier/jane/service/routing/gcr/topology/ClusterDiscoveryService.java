package de.uni_trier.jane.service.routing.gcr.topology;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.routing.gcr.map.*;

public interface ClusterDiscoveryService {

//	public ClusterMap getClusterMap();
	
	public boolean hasValidHostData();
	
	public Cluster getHostCluster();

	// all clusters which are reachable by at least one node in the host cluster excluding the host itself
	public Cluster[] getClustersReachableFromHost();
	
	public boolean isReachableFromHost(Cluster cluster);
	
	
	// all clusters which are reachable by this node
	public Cluster[] getClusterReachableFromNode(Address node);

	public boolean isReachableFromNode(Address cluster);
	
	public Cluster[] getClusterReachableFromNode();
	
	public Cluster[] getClustersFromClusterAddresses(Address[] addresses);

	public boolean isInHostCluster(Address node);
	
	public boolean hasData(Address address);

	public Cluster getNodesCluster(Address w);

	public Cluster getClusterFromAddress(Address address);

//	public boolean isReachableFromNode(Address node, Address cluster);

	// all nodes which can reach the cluster in at least one hop
	public Address[] getOneHopGatewayNodes(Address cluster);

	// all nodes which can reach the cluster in at least two hops
	public Address[] getTwoHopGatewayNodes(Address cluster);

	// TODO diese Methode hat hier eigentlich nichts zu suchen
	public ClusterMap getClusterMap();
	
}