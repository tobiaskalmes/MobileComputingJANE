/***********************************************************************
 *  J a v a G P S - GPS access library and Java API                    *
 *  Copyright (C) 2001 Ulrich Walther                                  *
 *                                                                     *
 *  This program is free software; you can redistribute it and/or      *
 *  modify it under the terms of the GNU General Public License as     *
 *  published by the Free Software Foundation; either version 2 of     *
 *  the License, or (at your option) any later version.                *
 *                                                                     *
 *  This program is distributed in the hope that it will be useful,    *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of     *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU   *
 *  General Public License for more details.                           *
 *                                                                     *
 *  You should have received a copy of the GNU General Public          *
 *  License along with this program; if not, write to the Free         *
 *  Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,     *
 *  MA 02111-1307 USA                                                  *
 ***********************************************************************/

package org.iu.gps;

/**
 *  The XY class represents a tupel of coordinates (GaussKrueger or
 *  Longitude=X/Latitude=Y).
 *
 *@author     Uli Walther
 *@version    1.0
 */

public class XY {
	/**
	 *  GaussKrueger x or latitude.
	 */
	public double x;
	/**
	 *  GaussKrueger y or longitude.
	 */
	public double y;


	/**
	 *  Create uninitialized coordinate.
	 */
	public XY() { }


	/**
	 *  Create a coordinate.
	 *
	 *@param  _x  x or latitude.
	 *@param  _y  y or longitude.
	 */
	public XY( double _x, double _y )
	{
		x = _x;
		y = _y;
	}
}
