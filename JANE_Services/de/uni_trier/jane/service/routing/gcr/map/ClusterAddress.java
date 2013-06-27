/*
 * Created on 01.09.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.uni_trier.jane.service.routing.gcr.map;

//import com.sun.jndi.ldap.Obj;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.network.link_layer.LinkLayerAddress;

/**
 * @author idris
 *
 * a cluster address is a unique identifier for each cluster in the map
 * the cluster address ist assigned according to the zero positive form, 
 * which is explained in the article 
 * @link  www.site.uottawa.ca/~ivan/gsz.pdf 
 */
public class ClusterAddress extends LinkLayerAddress {
	    private int xc;
	    private int yc;
	    private int zc;
	    
	    public ClusterAddress(int aXc, int aYc, int aZc){
	    	this.xc = aXc;
	    	this.yc = aYc;
	    	this.zc = aZc;
	    	}
	    
	    
		/**
		 *  @return the Address lenght
		 */
	    
		public int getLength() {
			return 12;
		}

		/**
		 * @return x-coordinate.
		 */
		public int getXc() {
			return xc;
		}
		
		/**
		 * @return y-coordinate.
		 */
		public int getYc() {
			return yc;
		}
		
		/**
		 * @return z-coordinate.
		 */
		public int getZc() {
			return zc;
		}
		
		/**
		 *  @return hashcode
		 */
        
		public int hashCode(){
			final int seed = 23;
			final int fODD_PRIME_NUMBER = 1000003;
			int result = seed;
			result = (result * fODD_PRIME_NUMBER) + xc;
			result = (result * fODD_PRIME_NUMBER) + yc;
			result = (result * fODD_PRIME_NUMBER) + zc;
			
			return result;
		}
	
		public boolean 	equals(Object aThat){
			
			if(this == aThat) return true;		
			if(!(aThat instanceof ClusterAddress)) return false;
			
			ClusterAddress that = (ClusterAddress) aThat;
			
			return (this.xc == that.xc) && (this.yc == that.yc) && (this.zc == that.zc) ;
			
		}
		public int compareTo(Object oBject) {
			
			if (this == oBject) return 0;
			
			final ClusterAddress that = (ClusterAddress) oBject;
			
			int thisS = this.xc + this.yc;
			int thatS = that.xc + that.yc;
			if(thisS < thatS) return -1;
			if(thisS > thatS) return 1;
			if(thisS == thatS && this.xc < that.xc) return -1;
			if(thisS == thatS && this.xc > that.xc) return 1;
			if(thisS == thatS && this.xc == that.xc && this.yc == that.yc) return 0;
			
			return 4;
			
			
		}

		public String toString() {
				return "(" + xc + "," + yc + "," + zc + ")";
		}
		
		public static void main(String args[]) {
			Address a = new ClusterAddress(0, 1, 0);
			Address b = new ClusterAddress(1, 0, 0);
			System.out.println(a.compareTo(b));
		}
		
		public int getCodingSize() {
			return 32 * 3;
		}

}

