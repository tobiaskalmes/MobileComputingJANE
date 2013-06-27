package de.uni_trier.jane.service.routing.multicast.spbm;

import java.util.*;

import de.uni_trier.jane.basetypes.Address;
import de.uni_trier.jane.service.routing.multicast.MulticastGroupID;

class GroupManagement {
	static GroupManagement instance = null;

	HashSet algos = new HashSet();

	protected GroupManagement() {
	}


	public static GroupManagement getInstance() {
		if (instance == null) {
			instance = new GroupManagement();
		}
		return instance;
	}

	public boolean gridHasMember(Grid g, MulticastGroupID mid) {
		Iterator i = algos.iterator();
		while (i.hasNext()) {
			PositionBasedMulticastRoutingAlgorithm pbm = (PositionBasedMulticastRoutingAlgorithm) i.next();
			if (pbm.isInGroup(mid) && g.isInside(pbm.getGrid()))
				return true;
		}
		return false;
	}

	public LinkedList getGridReceivers(Grid g, MulticastGroupID mid) {
		LinkedList l = new LinkedList();
		Iterator i = algos.iterator();
		while (i.hasNext()) {
			PositionBasedMulticastRoutingAlgorithm pbm = (PositionBasedMulticastRoutingAlgorithm) i.next();
			if (pbm.isInGroup(mid) && pbm.getGrid().equals(g))
				l.add(pbm.getAddress());
		}
		return l;
	}

	public void replaceGrids(LinkedList l, Grid mygrid, MulticastGroupID mid) {
		LinkedList l2 = new LinkedList(l);
		l.clear();
		while (l2.size() > 0) {
			Grid g = (Grid) l2.get(0);
			l2.remove(0);
			if (!g.isInside(mygrid) || g.equals(mygrid))
				// just inherit from old paket, mygrid will be replaced by local
				// receivers
				l.add(g);
			else
				for (int j = 1; j <= 4; j++)
					if (gridHasMember(new Grid(g, j), mid))
						l2.add(new Grid(g, j));
		}
	}

	public void addAlgo(PositionBasedMulticastRoutingAlgorithm algo) {
		algos.add(algo);
	}
    
    public void removeAlgo(PositionBasedMulticastRoutingAlgorithm algo) {
        algos.remove(algo);
    }
}
