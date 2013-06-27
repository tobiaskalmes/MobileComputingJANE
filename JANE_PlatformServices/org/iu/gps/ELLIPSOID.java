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
 *  ELLIPSOID specifies an earth ellipsoid.
 *
 *@author     Uli Walther
 *@version    1.0
 */

public class ELLIPSOID {
	/**
	 *  Ellipsoid's name.
	 */
	public String name;
	/* name of ellipsoid */
	/**
	 *  Semi-major axis (in meters).
	 */
	public double a;
	/* semi-major axis, meters */
	/**
	 *  Inverse flattening.
	 */
	public double invf;


	/* 1/f */
	/**
	 *  Create ELLIPSOID instance.
	 *
	 *@param  _n  name
	 *@param  _a  semi-major axis (in meters).
	 *@param  _i  inverse flattening.
	 */
	public ELLIPSOID( String _n, double _a, double _i )
	{
		name = _n;
		a = _a;
		invf = _i;
	}
}
