package de.uni_trier.jane.service.planarizer.rdg;

import de.uni_trier.jane.service.planarizer.NetworkNode;

public class NetworkEdge {
	private NetworkNode nodeA;
	private NetworkNode nodeB;
	
	public NetworkEdge(NetworkNode a, NetworkNode b){
		nodeA=a;
		nodeB=b;
	}

	public NetworkNode getNodeA() {
		return nodeA;
	}

	public NetworkNode getNodeB() {
		return nodeB;
	}

	
	public int hashCode() {
		return nodeA.getAddress().hashCode() + nodeB.getAddress().hashCode();
	}

	public boolean equals(Object edge) {
		try {
			NetworkEdge networkEdge=(NetworkEdge) edge;
			if(networkEdge.getNodeA().getAddress().equals(nodeA.getAddress())&& networkEdge.getNodeB().getAddress().equals(nodeB.getAddress()))
				return true;
			if(networkEdge.getNodeA().getAddress().equals(nodeB.getAddress())&& networkEdge.getNodeB().getAddress().equals(nodeA.getAddress()))
				return true;
		}catch ( ClassCastException e){
			return false;
		}
		return false;
	}
	
	 
}
