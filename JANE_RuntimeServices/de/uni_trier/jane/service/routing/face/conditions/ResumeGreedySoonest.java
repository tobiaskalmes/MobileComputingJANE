/*****************************************************************************
* 
* $Id: ResumeGreedySoonest.java,v 1.1 2007/06/25 07:24:00 srothkugel Exp $
*  
***********************************************************************
*  
* JANE - The Java Ad-hoc Network simulation and evaluation Environment
*
***********************************************************************
*
* Copyright (C) 2002-2006
* Hannes Frey and Daniel Goergen and Johannes K. Lehnert
* Systemsoftware and Distributed Systems
* University of Trier 
* Germany
* http://syssoft.uni-trier.de/jane
* 
* This program is free software; you can redistribute it and/or 
* modify it under the terms of the GNU General Public License 
* as published by the Free Software Foundation; either version 2 
* of the License, or (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful, 
* but WITHOUT ANY WARRANTY; without even the implied warranty of 
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
* General Public License for more details.
* 
* You should have received a copy of the GNU General Public License 
* along with this program; if not, write to the Free Software 
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
* 
*****************************************************************************/
package de.uni_trier.jane.service.routing.face.conditions;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.planarizer.*;
import de.uni_trier.jane.service.routing.face.planar_graph_explorer.*;
import de.uni_trier.jane.service.routing.gcr.ClusterNetworkNodeImpl;
import de.uni_trier.jane.service.routing.greedy.metric.GreedyEnergieMetric;
import de.uni_trier.jane.visualization.*;
import de.uni_trier.jane.visualization.shapes.*;

public class ResumeGreedySoonest implements ResumeGreedyCondition {

	public static transient final ResumeGreedyConditionFactory FACTORY = new ResumeGreedyConditionFactory() {
		public ResumeGreedyCondition createResumeGreedyCondition() {
			return new ResumeGreedySoonest();
		}
	};

	private boolean satisfied;
	private transient Shape shape;
	
	public ResumeGreedySoonest() {
        
		satisfied = false;
		shape = null;
	}

	private ResumeGreedySoonest(boolean satisfied, Shape shape) {
		this.satisfied = satisfied;
	}

	public ResumeGreedyCondition nextNode(NetworkNode startNode,
			NetworkNode destinationNode, NetworkNode greedyFailureNode,
			PlanarGraphNode currentNode, NetworkNode[] neighbors) {

		
//		Shape circle = new EllipseShape(destinationNode.getAddress(), new Extent(greedyFailureNode.getPosition().distance(destinationNode.getPosition()), greedyFailureNode.getPosition().distance(destinationNode.getPosition())), Color.GREY, false);
		Shape circle = null;
        Position destPos;
        //TODO: devicePosition is needed not clusterposition
		if (destinationNode instanceof ClusterNetworkNodeImpl) {
            ClusterNetworkNodeImpl cluster = (ClusterNetworkNodeImpl)destinationNode;
            destPos = cluster.getReceiverPosition();
        }else{
            destPos = destinationNode.getPosition();
        }
		
		double dist = Double.POSITIVE_INFINITY;
		for(int i=0; i<neighbors.length; i++) {
			NetworkNode neighbor = neighbors[i];
            //TODO: aufgrund von rechenfehlern muß auch überprüft werden ob der Nachbar der greedyFehlerKnoten ist.
            //  dies ist kritisch wenn dies der lokale Knoten ist! 
			if(neighbor.isOneHopNeighbor()&&!neighbor.getAddress().equals(greedyFailureNode.getAddress())) {
                
				double d = neighbor.getPosition().distance(destPos);
				if(d < dist) {
					dist = d;
				}
			}
		}

		return new ResumeGreedySoonest(dist < greedyFailureNode.getPosition().distance(destPos), circle);

	}

	public boolean isSatisfied() {
		return satisfied;
	}

	public Shape getShape() {
		return shape;
	}

}
