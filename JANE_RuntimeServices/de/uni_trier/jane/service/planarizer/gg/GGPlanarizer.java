/*
 * Created on 29.09.2004
 *
 */
package de.uni_trier.jane.service.planarizer.gg;

import java.util.*;

import de.uni_trier.jane.service.planarizer.NetworkNode;
import de.uni_trier.jane.util.*;

/**
 * This class is used to Construct a planar graph from a given nonplanar graph
 * @author Stefan Peters
 *
 */
public class GGPlanarizer implements Planarizer  {

    /**
     * Constructs a planar graph with one hop information and location knowledge 
     * using GABRIEL as algorithm.
     * @param a The local NetworkNode
     * @param neighbors The node's neighbors
     * @return Returns a planar subgraph
     */
    public NetworkNode[] stdPlanarizer(NetworkNode a, NetworkNode[] neighbors){
       NetworkNode[] neighbors1=null;
       Vector vector= new Vector(); 
       
       for(int i=0;i<neighbors.length;i++){
           NetworkNode b=neighbors[i];
           if(a.getAddress().equals(b.getAddress())){
           	continue;
           }
           boolean test=false;
           for(int j=0;j<neighbors.length;j++){
        	   NetworkNode chosen=neighbors[j];
               if(b.getAddress().equals(chosen.getAddress()) || a.getAddress().equals(chosen.getAddress())){
                   continue;
               }
               if(GeometryCalculations.isInCircle(a.getPosition(),b.getPosition(),chosen.getPosition(),false)){ // TODO müsste der letzte Parameter nicht true sein???
            	   //nach der theoretischen Konstruktion ja, nur fliegt die Simulationsumgebunggenau weg, wenn der Punkt genau
            	   // auf dem Kreis liegt.
               	test=true;
               	break;
               }
           }
           if(test==false){
               vector.add(b);
           }
       }
       neighbors1= new NetworkNode[vector.size()];
       for(int i=0;i<vector.size();i++){
           neighbors1[i]=(NetworkNode)vector.get(i);
       }
       return neighbors1; 
    }
   
}
