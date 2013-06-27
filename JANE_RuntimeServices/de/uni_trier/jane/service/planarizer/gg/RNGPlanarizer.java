package de.uni_trier.jane.service.planarizer.gg;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.planarizer.*;

/**
 * This class is used to construct the local viev on a planar graph from a
 * given nonplanar graph. The localized construction applies the relative
 * neighborhood method (RNG).
 * @author Hannes Frey
 */
public class RNGPlanarizer implements Planarizer {

    /* (non-Javadoc)
	 * @see de.uni_trier.jane.service.planarizer.gg.Planarizer#stdPlanarizer(de.uni_trier.jane.service.planarizer.NetworkNode, de.uni_trier.jane.service.planarizer.NetworkNode[])
	 */
    public NetworkNode[] stdPlanarizer(NetworkNode u, NetworkNode[] nu) {
    	ArrayList result = new ArrayList();
		for (int i = 0; i < nu.length; i++) {
			NetworkNode v = nu[i];
			if(isEdge(u, v, nu)) {
				result.add(v);
			}
		}
		return (NetworkNode[])result.toArray(new NetworkNode[result.size()]);
	}

	private boolean isEdge(NetworkNode u, NetworkNode v, NetworkNode[] nu) {
		Address au = u.getAddress();
		Address av = v.getAddress();
		if(au.equals(av)) {
			return false;
		}
		Position pu = u.getPosition();
		Position pv = v.getPosition();
		double dist = pu.distance(pv);
		for(int i=0; i<nu.length; i++) {
			NetworkNode w = nu[i];
			Address aw = w.getAddress();
			if(!aw.equals(au) && !aw.equals(av)) {
				Position pw = w.getPosition();
				if(pw.distance(pu) < dist && pw.distance(pv) < dist) {
					return false;
				}
			}
		}
		return true;
	}

}
