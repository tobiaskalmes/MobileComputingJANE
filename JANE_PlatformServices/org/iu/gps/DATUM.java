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
 *  DATUM specifies a geodetic earth datum, that is, an ellipsoid whose center
 *  is moved from the center.
 *
 *@author     Uli Walther
 *@created    30. Oktober 2001
 *@version    1.0
 */

public class DATUM {
	/**
	 *  Datum's name. E.g.: Standard datum is WGS84.
	 */
	public String name;
	/**
	 *  This datum's ellipsoid (index into ellipsoid table in class {@link COORD}).
	 */
	public int ellipsoid;
	/**
	 *  dx from center.
	 */
	public int dx;
	/**
	 *  dy from center.
	 */
	public int dy;
	/**
	 *  dz from center.
	 */
	public int dz;


	/**
	 *  DATUM constructor.
	 *
	 *@param  _n   Name
	 *@param  _e   Ellipsoid (index into table in class {@link COORD}).
	 *@param  _dx  dx
	 *@param  _dy  dy
	 *@param  _dz  dz
	 */
	public DATUM( String _n, int _e, int _dx, int _dy, int _dz )
	{
		name = _n;
		ellipsoid = _e;
		dx = _dx;
		dy = _dy;
		dz = _dz;
	}
}
