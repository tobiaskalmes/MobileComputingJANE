/*****************************************************************************
 * 
 * GridVisualisationService.java
 * 
 * $Id: GridVisualisationService.java,v 1.1 2007/06/25 07:24:49 srothkugel Exp $
 *  
 * Copyright (C) 2002-2005 Hannes Frey and Daniel Goergen and Johannes K. Lehnert
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
package de.uni_trier.jane.service.routing.multicast.spbm;

import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.simulation.service.GlobalOperatingSystem;
import de.uni_trier.jane.simulation.service.GlobalService;
import de.uni_trier.jane.visualization.Color;
import de.uni_trier.jane.visualization.shapes.*;

/**
 * @author goergen
 * 
 * TODO comment class
 */
public class GridVisualisationService implements GlobalService {
	/**
	 * @uml.property  name="griddepth"
	 */
	private int griddepth;

	private static boolean drawNames;

	private static Grid rootgrid;

	public GridVisualisationService(double xsize, double ysize, int griddepth, boolean drawNames) {
		this.griddepth = griddepth;
		GridVisualisationService.drawNames = drawNames;
		rootgrid = new Grid(0, xsize, 0, ysize);
	}

	public GridVisualisationService(double xsize, double ysize, int griddepth) {
		this(xsize, ysize, griddepth, false);
	}

	public void start(GlobalOperatingSystem globalOperatingSystem) {
		// TODO Auto-generated method stub

	}

	public ServiceID getServiceID() {
		// TODO Auto-generated method stub
		return null;
	}

	public void finish() {
		// TODO Auto-generated method stub

	}

	public Shape getShape() {
		ShapeCollection collection = new ShapeCollection();
		addGrid(rootgrid, collection);
		return collection;
	}

	/**
	 * TODO Comment method
	 * 
	 * @param collection
	 */
	private void addGrid(Grid currentGrid, ShapeCollection collection) {
		collection.addShape(new RectangleShape(new Rectangle(currentGrid.maxx, currentGrid.maxy, currentGrid.minx,
				currentGrid.miny), Color.GREY, false, (float) (griddepth - currentGrid.getDepth()) + 1));
		if (drawNames && currentGrid.getDepth() == griddepth) {
			TextShape name = new TextShape(currentGrid.toString(), new Rectangle(currentGrid.maxx, currentGrid.maxy,
					currentGrid.minx, currentGrid.miny), Color.LIGHTGREEN, new Position(-10, 5));
			collection.addShape(name);
		}
		if (currentGrid.getDepth() == griddepth)
			return;

		for (int i = 1; i <= 4; i++)
			addGrid(new Grid(currentGrid, i), collection);
	}

	public void getParameters(Parameters parameters) {
		// TODO Auto-generated method stub

	}

}
