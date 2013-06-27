/*****************************************************************************
 * 
 * LocationSelect.java
 * 
 * $Id: LocationSelect.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
 *  
 * Copyright (C) 2002-2004 Hannes Frey, Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.simulation.dynamic.mobility_source.pathnet.timetable;

import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.random.*;
import de.uni_trier.jane.simulation.dynamic.mobility_source.pathnet.*;

/**
 * This class is used for randomly selecting locations from a given PathNet
 * 
 * 
 */
public class LocationSelect {
	private ContinuousDistribution selectDistribution;
	private TreeMap sizeLocationMap;
	private double totalSize;

	/**
	 *	Construtor for class <code>LocationSelect</code> 
	 * @param campus				the PathNet campus
	 * @param distributionCreator	the simulation <code>DistributionCreator</code>
	 */
	public LocationSelect(Campus campus,DistributionCreator distributionCreator) {
		selectDistribution=distributionCreator.getContinuousUniformDistribution(0,1);
		sizeLocationMap=new TreeMap();
		String[] locations=campus.getLocationNames();
		for (int i=0;i<locations.length;i++){
			Rectangle rectangle=null;
			try {
				rectangle = campus.getLocationRectangle(locations[i]);
			} catch (UnknownLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			totalSize+=rectangle.getWidth()*rectangle.getHeight();
			sizeLocationMap.put(new Double(totalSize),locations[i]);
		}
	}
	
	/**
	 * Randomly select a location name.
	 * 
	 * @return	a randomly selected location name
	 */
	public String next(){
		double random=selectDistribution.getNext()*totalSize;
		Iterator iterator=sizeLocationMap.keySet().iterator();
		while (iterator.hasNext()){
			Double size=(Double)iterator.next();
			if (size.doubleValue()>=random){
				return (String)sizeLocationMap.get(size);
			}
		}
		throw new IllegalStateException("tschaaa");
	}
}
