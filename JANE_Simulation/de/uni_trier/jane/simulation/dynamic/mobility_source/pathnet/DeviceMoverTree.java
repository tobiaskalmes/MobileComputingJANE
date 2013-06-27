/*****************************************************************************
 * 
 * DeviceMoverTree.java
 * 
 * $Id: DeviceMoverTree.java,v 1.1 2007/06/25 07:24:32 srothkugel Exp $
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

package de.uni_trier.jane.simulation.dynamic.mobility_source.pathnet;


import java.util.*;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.visualization.shapes.*;


/**
 * An object of this Class supports the combination of arbitrary device mover
 * objects to one compound device mover. These are organized as a tree with one
 * root device mover and several sub device mover. Every location of a sub
 * device mover whose name matches the name of one location in the root device
 * mover is glued to the root device mover and is no more visible to the user of
 * this device mover tree.
 */
public class DeviceMoverTree implements DeviceMover {

	/**
	 * Construct a device mover tree consisting of one root and several sub
	 * device movers. The sub device movers are placed on a position so that
	 * every location which is glued to the root, has the same position as the
	 * root location.
	 * @param name the name of this device mover.
	 * @param root the root device mover in the tree.
	 * @param sub the sub device mover glued to the root.
	 */
	public DeviceMoverTree(String name, DeviceMover root, DeviceMover[] sub) {
		this.name = name;
		this.root = root;
		this.sub = sub;
		
		subEndpointTable = new Hashtable();
		rootEndpointSet = new HashSet();
		rootEndpointSet = createNameSet(root);
		HashSet rootNameSet = createNameSet(root);
		
		for(int i=0; i<sub.length; i++) {
		
			String[] subName = sub[i].getLocationNames();
			boolean connected = false;
			boolean endpoint = false;
			for(int j=0; j<subName.length; j++) {
				rootEndpointSet.remove(subName[j]);
				if(rootNameSet.contains(subName[j])) {
					try {
						if(!connected) {
							Position pos = new Position(
								root.getLocationPosition(subName[j]));
							pos=pos.sub(sub[i].getLocationPosition(subName[j]));
							pos=pos.add(sub[i].getPosition());
							sub[i].setPosition(pos);
						}
						else {
							
// TODO: mit epsilon testen
//							if(!sub[i].getLocationPosition(subName[j]).equals(
//								root.getLocationPosition(subName[j]))) {
//								throw new IllegalArgumentException(
//									"the location '" + subName[j] +
//									"' of sub device mover '" +
//									sub[i].getName() +
//									"' has not the same position as the root");
//							}
						}
						connected = true;
					}
					catch(UnknownLocationException e) {
						throw new IllegalArgumentException(e.toString());
					}
				}
				else {
					if(subEndpointTable.containsKey(subName[j])) {
						throw new IllegalArgumentException("the location '" +
							subName[j] + "' of sub device mover '" +
							sub[i].getName() + "' had been added before.");
					}
					subEndpointTable.put(subName[j], sub[i]);
					endpoint = true;
				}
			}
			if(!connected) {
				throw new IllegalArgumentException("the sub device mover '" +
					sub[i].getName() + "' had not been connected");
			}
			if(!endpoint) {
				throw new IllegalArgumentException("the sub device mover '" +
					sub[i].getName() + "' did not add any end point location.");
			}
		}
	}



	/**
	 * @see DeviceMover#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see DeviceMover#getPosition()
	 */
	public Position getPosition() {
		return getRectangle().getCenter();
	}

	/**
	 * @see DeviceMover#setPosition(Position)
	 */
	public void setPosition(Position pos) {
		Position offset = new Position(pos);
		offset= offset.sub(getPosition());
		move(root, offset);
		for(int i=0; i<sub.length; i++) {
			move(sub[i], offset);
		}
	}

	/**
	 * @see DeviceMover#getRectangle()
	 */
	public Rectangle getRectangle() {
		Rectangle result = root.getRectangle();
		for(int i=0; i<sub.length; i++) {
			result=result.union(sub[i].getRectangle());
		}
		return result;
	}

	/**
	 * @see DeviceMover#getLocationNames()
	 */
	public String[] getLocationNames() {
		ArrayList nameList = new ArrayList();
		Enumeration en = subEndpointTable.keys();
		while(en.hasMoreElements()) {
			nameList.add(en.nextElement());
		}
		Iterator it = rootEndpointSet.iterator();
		while(it.hasNext()) {
			nameList.add(it.next());
		}
		String[] names = new String[nameList.size()];
		for(int i=0; i<nameList.size(); i++) {
			names[i] = (String)nameList.get(i);
		}
		return names;
	}

	/**
	 * Returns the Rectangle for the Location with the given name 
	 * @param name		the locations name
	 * @return			the locations <code>Rectangle</code>
	 * @throws UnknownLocationException
	 */
	public Rectangle getLocationRectangle(String name)throws UnknownLocationException {
		if(rootEndpointSet.contains(name)) {
			return root.getRectangle();
		}
		else if(subEndpointTable.containsKey(name)) {
			return ((DeviceMover)
				(subEndpointTable.get(name))).getRectangle();
		}
		else {
			throw new UnknownLocationException("the location '" + name +
				"' does not exist.");
		}	
	}
	
	/**
	 * @see DeviceMover#getLocationPosition(String)
	 */
	public Position getLocationPosition(String name)
		throws UnknownLocationException {
		if(rootEndpointSet.contains(name)) {
			return root.getLocationPosition(name);
		}
		else if(subEndpointTable.containsKey(name)) {
			return ((DeviceMover)
				(subEndpointTable.get(name))).getLocationPosition(name);
		}
		else {
			throw new UnknownLocationException("the location '" + name +
				"' does not exist.");
		}
	}

	/**
	 * @see DeviceMover#getMinDistance(String, String)
	 */
	public double getMinDistance(String l1, String l2)
		throws UnknownLocationException {
		DeviceMover dm1 = findDeviceMover(l1);
		DeviceMover dm2 = findDeviceMover(l2);
		if(dm1 == dm2) {
			return dm1.getMinDistance(l1, l2);
		}
		else if((dm1 != root) && (dm2 != root)) {
			String[] rootLocation = findBestRootLocations(
				dm1,
				dm2,
				l1,
				findConnectors(dm1, root),
				findConnectors(dm2, root),
				l2
			);
			return dm1.getMinDistance(l1, rootLocation[0]) + 
				root.getMinDistance(rootLocation[0], rootLocation[1]) +
				dm2.getMinDistance(rootLocation[1], l2);
		}
		else {
			String mid = findBestLocation(dm1, dm2,
				l1, findConnectors(dm1, dm2),
				l2);
			return dm1.getMinDistance(l1, mid) + dm2.getMinDistance(mid, l2);
		}
	}



	private HashSet createNameSet(DeviceMover dm) {
		HashSet nameSet = new HashSet();
		String[] name = dm.getLocationNames();
		for(int i=0; i<name.length; i++) {
			nameSet.add(name[i]);
		}
		return nameSet;
	}

	private DeviceMover findDeviceMover(String location)
		throws UnknownLocationException {
		DeviceMover dm = (DeviceMover)subEndpointTable.get(location);
		if(dm == null) {
			if(createNameSet(root).contains(location)) {
				return root;
			}
			else {
				throw new UnknownLocationException("the location '" +
					location + "'does not exist.");
			}
		}
		else {
			return dm;
		}
	}

	private HashSet findConnectors(DeviceMover dm1, DeviceMover dm2) {
		HashSet res = new HashSet();
		HashSet ns1 = createNameSet(dm1);
		HashSet ns2 = createNameSet(dm2);
		Iterator it = ns1.iterator();
		while(it.hasNext()) {
			Object obj = it.next();
			if(ns2.contains(obj)) {
				res.add(obj);
			}
		}
		return res;
	}

	private double calcMinLength(DeviceMover dms, DeviceMover dmf, String s,
		String i1, String i2, String f) throws UnknownLocationException {
		return dms.getMinDistance(s,i1) + root.getMinDistance(i1,i2) +
			dmf.getMinDistance(i2,f);
	}
	
	private String[] findBestRootLocations(DeviceMover dms, DeviceMover dmf,
		String s, HashSet is1, HashSet is2, String f)
		throws UnknownLocationException {
		String[] res = new String[2];
		Iterator it1 = is1.iterator();
		Iterator it2 = is2.iterator();
		double min = 0;
		boolean first = true;
		while(it1.hasNext()) {
			String i1 = (String)it1.next();
			while(it2.hasNext()) {
				String i2 = (String)it2.next();
				double calc = calcMinLength(dms, dmf, s, i1, i2, f);
				if(first || calc < min) {
					first = false;
					min = calc;
					res[0] = i1;
					res[1] = i2;
				}
			}
		}
		return res;
	}
	
	private String findBestLocation(DeviceMover dm1, DeviceMover dm2, String s,
		HashSet is, String f) throws UnknownLocationException {
		String res = "";
		Iterator it = is.iterator();
		double min = 0;
		boolean first = true;
		while(it.hasNext()) {
			String i = (String)it.next();
			double calc = dm1.getMinDistance(s,i) + dm2.getMinDistance(i,f);
			if(first || calc < min) {
				first = false;
				min = calc;
				res = i;
			}
		}
		return res;
	}

	private void createPath(DevicePath devicePath, DeviceMover startDeviceMover,
		DeviceMover finishDeviceMover, String start, String finish)
		throws UnknownLocationException {
		
		if(startDeviceMover == finishDeviceMover) {
			startDeviceMover.createPath(devicePath, start, finish);
			arrived(devicePath,finish,finish);
		}
		else if((startDeviceMover != root) && (finishDeviceMover != root)) {
			String[] rootLocation = findBestRootLocations(
				startDeviceMover,
				finishDeviceMover,
				start,
				findConnectors(startDeviceMover, root),
				findConnectors(finishDeviceMover, root),
				finish
			);
			startDeviceMover.createPath(devicePath, start, rootLocation[0]);
			arrived(devicePath,rootLocation[0],finish);
		}
		else {
			String mid = findBestLocation(startDeviceMover, finishDeviceMover,
				start, findConnectors(startDeviceMover, finishDeviceMover),
				finish);
			startDeviceMover.createPath(devicePath, start, mid);
			arrived(devicePath,mid,finish);
		}
		
	}

	
	private void arrived(DevicePath devicePath, String location, String finishLocation) {
	//	String finish = (String)finishTable.get(device);
	//	finishTable.remove(device);
		if(!location.equals(finishLocation)) {
			try {
				DeviceMover finishDeviceMover =
					(DeviceMover)subEndpointTable.get(finishLocation);
				HashSet nameSet = createNameSet(finishDeviceMover);
				if(nameSet.contains(location)) {
					createPath(devicePath, finishDeviceMover, finishDeviceMover,
					location, finishLocation);
				}
				else {
					createPath(devicePath, root, finishDeviceMover, location,
					finishLocation);
				}
			}
			catch(UnknownLocationException e) {
				throw new IllegalArgumentException(e.toString());
			}
		}
		//else {
			//if(arrivalListener != null) {
			//	arrivalListener.arrived(device, finish);
			//}
		//}
	}
	
	
	
	
	private void move(DeviceMover dm, Position offset) {
		Position pos = dm.getPosition();
		dm.setPosition(pos.add(offset));
	}
	
	private String name;
	private DeviceMover root;
	private DeviceMover[] sub;
	private Hashtable subEndpointTable;
	private HashSet rootEndpointSet;

	/**
	 * @see de.uni_trier.jane.simulation.dynamic.mobility_source.pathnet.DeviceMover#getShape()
	 */
	public Shape getShape() {
		ShapeCollection result= new ShapeCollection();
		for(int i=0; i<sub.length; i++) {
			result.addShape(sub[i].getShape(),Position.NULL_POSITION);
		}
		result.addShape(root.getShape(),Position.NULL_POSITION);
		return result;
	}
	
	
	/**
	 * @see de.uni_trier.jane.simulation.dynamic.mobility_source.pathnet.DeviceMover#createPath(de.uni_trier.ubi.appsim.kernel.dynamic.mobility_source.pathnet.DevicePath, java.lang.String, java.lang.String)
	 */
	public void createPath(DevicePath devicePath, String start, String finish) throws UnknownLocationException {
		DeviceMover startDeviceMover = findDeviceMover(start);
		DeviceMover finishDeviceMover = findDeviceMover(finish);
		createPath(devicePath, startDeviceMover, finishDeviceMover, start, finish);
		
	}

}
