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
 *  The COORD class encapsulates all methods related to earth coordinate
 *  transformations. It can transform earth coordinates between a huge number of
 *  earth dates, and it can transform from / to GaussKrueger coordinates.
 *
 *@author    walther
 */

public class COORD {
	/**
	 *  List of geodetic earth ellipsoids.
	 */
	public final static ELLIPSOID[] gEllipsoid = new ELLIPSOID[]{
			new ELLIPSOID( "Airy 1830", 6377563.396, 299.3249646 ),
			new ELLIPSOID( "Modified Airy", 6377340.189, 299.3249646 ),
			new ELLIPSOID( "Australian National", 6378160.0, 298.25 ),
			new ELLIPSOID( "Bessel 1841", 6377397.155, 299.1528128 ),
			new ELLIPSOID( "Clarke 1866", 6378206.4, 294.9786982 ),
			new ELLIPSOID( "Clarke 1880", 6378249.145, 293.465 ),
			new ELLIPSOID( "Everest (India 1830)", 6377276.345, 300.8017 ),
			new ELLIPSOID( "Everest (1948)", 6377304.063, 300.8017 ),
			new ELLIPSOID( "Modified Fischer 1960", 6378155.0, 298.3 ),
			new ELLIPSOID( "Everest (Pakistan)", 6377309.613, 300.8017 ),
			new ELLIPSOID( "Indonesian 1974", 6378160.0, 298.247 ),
			new ELLIPSOID( "GRS 80", 6378137.0, 298.257222101 ),
			new ELLIPSOID( "Helmert 1906", 6378200.0, 298.3 ),
			new ELLIPSOID( "Hough 1960", 6378270.0, 297.0 ),
			new ELLIPSOID( "International 1924", 6378388.0, 297.0 ),
			new ELLIPSOID( "Krassovsky 1940", 6378245.0, 298.3 ),
			new ELLIPSOID( "South American 1969", 6378160.0, 298.25 ),
			new ELLIPSOID( "Everest (Malaysia 1969)", 6377295.664, 300.8017 ),
			new ELLIPSOID( "Everest (Sabah Sarawak)", 6377298.556, 300.8017 ),
			new ELLIPSOID( "WGS 72", 6378135.0, 298.26 ),
			new ELLIPSOID( "WGS 84", 6378137.0, 298.257223563 ),
			new ELLIPSOID( "Bessel 1841 (Namibia)", 6377483.865, 299.1528128 ),
			new ELLIPSOID( "Everest (India 1956)", 6377301.243, 300.8017 )
			};

	/**
	 *  List of geodetic earth dates.
	 */
	public final static DATUM[] gDatum = new DATUM[]{
			new DATUM( "Adindan", 5, -162, -12, 206 ),
			new DATUM( "Afgooye", 15, -43, -163, 45 ),
			new DATUM( "Ain el Abd 1970", 14, -150, -251, -2 ),
			new DATUM( "Anna 1 Astro 1965", 2, -491, -22, 435 ),
			new DATUM( "Arc 1950", 5, -143, -90, -294 ),
			new DATUM( "Arc 1960", 5, -160, -8, -300 ),
			new DATUM( "Ascension Island `58", 14, -207, 107, 52 ),
			new DATUM( "Astro B4 Sorol Atoll", 14, 114, -116, -333 ),
			new DATUM( "Astro Beacon \"E\"", 14, 145, 75, -272 ),
			new DATUM( "Astro DOS 71/4", 14, -320, 550, -494 ),
			new DATUM( "Astronomic Stn `52", 14, 124, -234, -25 ),
			new DATUM( "Australian Geod `66", 2, -133, -48, 148 ),
			new DATUM( "Australian Geod `84", 2, -134, -48, 149 ),
			new DATUM( "Bellevue (IGN)", 14, -127, -769, 472 ),
			new DATUM( "Bermuda 1957", 4, -73, 213, 296 ),
			new DATUM( "Bogota Observatory", 14, 307, 304, -318 ),
			new DATUM( "Campo Inchauspe", 14, -148, 136, 90 ),
			new DATUM( "Canton Astro 1966", 14, 298, -304, -375 ),
			new DATUM( "Cape", 5, -136, -108, -292 ),
			new DATUM( "Cape Canaveral", 4, -2, 150, 181 ),
			new DATUM( "Carthage", 5, -263, 6, 431 ),
			new DATUM( "CH-1903", 3, 674, 15, 405 ),
			new DATUM( "Chatham 1971", 14, 175, -38, 113 ),
			new DATUM( "Chua Astro", 14, -134, 229, -29 ),
			new DATUM( "Corrego Alegre", 14, -206, 172, -6 ),
			new DATUM( "Djakarta (Batavia)", 3, -377, 681, -50 ),
			new DATUM( "DOS 1968", 14, 230, -199, -752 ),
			new DATUM( "Easter Island 1967", 14, 211, 147, 111 ),
			new DATUM( "European 1950", 14, -87, -98, -121 ),
			new DATUM( "European 1979", 14, -86, -98, -119 ),
			new DATUM( "Finland Hayford", 14, -78, -231, -97 ),
			new DATUM( "Gandajika Base", 14, -133, -321, 50 ),
			new DATUM( "Geodetic Datum `49", 14, 84, -22, 209 ),
			new DATUM( "Guam 1963", 4, -100, -248, 259 ),
			new DATUM( "GUX 1 Astro", 14, 252, -209, -751 ),
			new DATUM( "Hjorsey 1955", 14, -73, 46, -86 ),
			new DATUM( "Hong Kong 1963", 14, -156, -271, -189 ),
			new DATUM( "Indian Bangladesh", 6, 289, 734, 257 ),
			new DATUM( "Indian Thailand", 6, 214, 836, 303 ),
			new DATUM( "Ireland 1965", 1, 506, -122, 611 ),
			new DATUM( "ISTS 073 Astro `69", 14, 208, -435, -229 ),
			new DATUM( "Johnston Island", 14, 191, -77, -204 ),
			new DATUM( "Kandawala", 6, -97, 787, 86 ),
			new DATUM( "Kerguelen Island", 14, 145, -187, 103 ),
			new DATUM( "Kertau 1948", 7, -11, 851, 5 ),
			new DATUM( "L.C. 5 Astro", 4, 42, 124, 147 ),
			new DATUM( "Liberia 1964", 5, -90, 40, 88 ),
			new DATUM( "Luzon Mindanao", 4, -133, -79, -72 ),
			new DATUM( "Luzon Philippines", 4, -133, -77, -51 ),
			new DATUM( "Mahe 1971", 5, 41, -220, -134 ),
			new DATUM( "Marco Astro", 14, -289, -124, 60 ),
			new DATUM( "Massawa", 3, 639, 405, 60 ),
			new DATUM( "Merchich", 5, 31, 146, 47 ),
			new DATUM( "Midway Astro 1961", 14, 912, -58, 1227 ),
			new DATUM( "Minna", 5, -92, -93, 122 ),
			new DATUM( "NAD27 Alaska", 4, -5, 135, 172 ),
			new DATUM( "NAD27 Bahamas", 4, -4, 154, 178 ),
			new DATUM( "NAD27 Canada", 4, -10, 158, 187 ),
			new DATUM( "NAD27 Canal Zone", 4, 0, 125, 201 ),
			new DATUM( "NAD27 Caribbean", 4, -7, 152, 178 ),
			new DATUM( "NAD27 Central", 4, 0, 125, 194 ),
			new DATUM( "NAD27 CONUS", 4, -8, 160, 176 ),
			new DATUM( "NAD27 Cuba", 4, -9, 152, 178 ),
			new DATUM( "NAD27 Greenland", 4, 11, 114, 195 ),
			new DATUM( "NAD27 Mexico", 4, -12, 130, 190 ),
			new DATUM( "NAD27 San Salvador", 4, 1, 140, 165 ),
			new DATUM( "NAD83", 11, 0, 0, 0 ),
			new DATUM( "Nahrwn Masirah Ilnd", 5, -247, -148, 369 ),
			new DATUM( "Nahrwn Saudi Arbia", 5, -231, -196, 482 ),
			new DATUM( "Nahrwn United Arab", 5, -249, -156, 381 ),
			new DATUM( "Naparima BWI", 14, -2, 374, 172 ),
			new DATUM( "Observatorio 1966", 14, -425, -169, 81 ),
			new DATUM( "Old Egyptian", 12, -130, 110, -13 ),
			new DATUM( "Old Hawaiian", 4, 61, -285, -181 ),
			new DATUM( "Oman", 5, -346, -1, 224 ),
			new DATUM( "Ord Srvy Grt Britn", 0, 375, -111, 431 ),
			new DATUM( "Pico De Las Nieves", 14, -307, -92, 127 ),
			new DATUM( "Pitcairn Astro 1967", 14, 185, 165, 42 ),
			new DATUM( "Prov So Amrican `56", 14, -288, 175, -376 ),
			new DATUM( "Prov So Chilean `63", 14, 16, 196, 93 ),
			new DATUM( "Puerto Rico", 4, 11, 72, -101 ),
			new DATUM( "Qatar National", 14, -128, -283, 22 ),
			new DATUM( "Qornoq", 14, 164, 138, -189 ),
			new DATUM( "Reunion", 14, 94, -948, -1262 ),
			new DATUM( "Rome 1940", 14, -225, -65, 9 ),
			new DATUM( "RT 90", 3, 498, -36, 568 ),
			new DATUM( "Santo (DOS)", 14, 170, 42, 84 ),
			new DATUM( "Sao Braz", 14, -203, 141, 53 ),
			new DATUM( "Sapper Hill 1943", 14, -355, 16, 74 ),
			new DATUM( "Schwarzeck", 21, 616, 97, -251 ),
			new DATUM( "South American `69", 16, -57, 1, -41 ),
			new DATUM( "South Asia", 8, 7, -10, -26 ),
			new DATUM( "Southeast Base", 14, -499, -249, 314 ),
			new DATUM( "Southwest Base", 14, -104, 167, -38 ),
			new DATUM( "Timbalai 1948", 6, -689, 691, -46 ),
			new DATUM( "Tokyo", 3, -128, 481, 664 ),
			new DATUM( "Tristan Astro 1968", 14, -632, 438, -609 ),
			new DATUM( "Viti Levu 1916", 5, 51, 391, -36 ),
			new DATUM( "Wake-Eniwetok `60", 13, 101, 52, -39 ),
			new DATUM( "WGS 72", 19, 0, 0, 5 ),
			new DATUM( "WGS 84", 20, 0, 0, 0 ),
			new DATUM( "Zanderij", 14, -265, 120, -358 ),
			new DATUM( "Bessel GaussKrueger", 3, 0, 0, 0 ),
			new DATUM( "Test Uli", 3, -585, -87, -409 ),
			};

	final static double Degree = 1.74532925199e-2;

	/* define constants */
	final static double PI = Math.PI;
	/* WGS84 semimajor axis */
	final static double WGSa = 6378137.0;
	/* WGS84 1/f */
	final static double WGSinvf = 298.257223563;
	/* Index of WGS84 earth datum */
	final static int WGS84ID = 100;

	/*
	 * Earth datum instance variable.
	 */
	protected int datum;


	/**
	 *  Constructor for the COORD object
	 *
	 *@param  _datum  Index into gDatum table of datum to use.
	 */
	public COORD( int _datum )
	{
		datum = _datum;
	}


	/**
	 *  Translate coordinates related to one earth datum (fromID) to another earth
	 *  datum (toID). The IDs are indeces into {@link gDatum}.
	 *
	 *@param  fromID     earth datum to translate from
	 *@param  toID       earth datum to translate to
	 *@param  latitude   latitude of coord. to translate
	 *@param  longitude  longitude of coord. to translate
	 *@return            converted coordinate in XY struct
	 */
	public static XY translate( int fromID, int toID, double latitude, double longitude )
	{
		XY temp = translate( false, latitude, longitude, fromID );
		return translate( true, temp.x, temp.y, toID );
	}


	/**
	 *  Method to convert coordinates from/to WGS84 coordinates. ID is an index
	 *  into {@link gDatum}.
	 *
	 *@param  fromWGS84  Parameter
	 *@param  latitude   Parameter
	 *@param  longitude  Parameter
	 *@param  datumID    Parameter
	 *@return            Returned Value
	 *@return            Returned Value
	 *@return            Returned Value
	 *@return            Returned Value
	 *@return            Returned Value
	 *@return            Returned Value
	 *@return            Returned Value
	 *@return            Returns converted coordinates
	 */
	public static XY translate( boolean fromWGS84, double latitude, double longitude,
			int datumID )
	{
		double dx = gDatum[datumID].dx;
		double dy = gDatum[datumID].dy;
		double dz = gDatum[datumID].dz;

		double phi = latitude * PI / 180.0;
		double lambda = longitude * PI / 180.0;
		double a0;
		double b0;
		double es0;
		double f0;
		/* Reference ellipsoid of input data */
//		double a1;
		/* Reference ellipsoid of input data */
//		double b1;
		/* Reference ellipsoid of input data */
		double es1;
		/* Reference ellipsoid of input data */
		double f1;
		/* Reference ellipsoid of output data */
		double psi;
		/* geocentric latitude */
		double x;
		/* geocentric latitude */
		double y;
		/* geocentric latitude */
		double z;
		/* 3D coordinates with respect to original datum */
		double psi1;
		/* transformed geocentric latitude */
		if ( datumID == WGS84ID )
		{
			/* do nothing if current datum is WGS84 */
			return new XY( latitude, longitude );
		}

		if ( fromWGS84 )
		{
			/* convert from WGS84 to new datum */
			a0 = WGSa;
			/* WGS84 semimajor axis */
			f0 = 1.0 / WGSinvf;
			/* WGS84 flattening */
//			a1 = gEllipsoid[gDatum[datumID].ellipsoid].a;
			f1 = 1.0 / gEllipsoid[gDatum[datumID].ellipsoid].invf;
		}
		else
		{
			/* convert from datum to WGS84 */
			a0 = gEllipsoid[gDatum[datumID].ellipsoid].a;
			/* semimajor axis */
			f0 = 1.0 / gEllipsoid[gDatum[datumID].ellipsoid].invf;
			/* flattening */
//			a1 = WGSa;
			/* WGS84 semimajor axis */
			f1 = 1 / WGSinvf;
			/* WGS84 flattening */
			dx = -dx;
			dy = -dy;
			dz = -dz;
		}

		b0 = a0 * ( 1 - f0 );
		/* semiminor axis for input datum */
		es0 = 2 * f0 - f0 * f0;
		/* eccentricity^2 */
//		b1 = a1 * ( 1 - f1 );
		/* semiminor axis for output datum */
		es1 = 2 * f1 - f1 * f1;
		/* eccentricity^2 */
		/* Convert geodedic latitude to geocentric latitude, psi */
		if ( latitude == 0.0 || latitude == 90.0 || latitude == -90.0 )
		{
			psi = phi;
		}
		else
		{
			psi = Math.atan( ( 1 - es0 ) * Math.tan( phi ) );
		}

		/* Calc x and y axis coordinates with respect to original ellipsoid */
		if ( longitude == 90.0 || longitude == -90.0 )
		{
			x = 0.0;
			y = Math.abs( a0 * b0 / Math.sqrt( b0 * b0 + a0 * a0 * Math.pow( Math.tan( psi ), 2.0 ) ) );
		}
		else
		{
			x = Math.abs( ( a0 * b0 ) /
					Math.sqrt( ( 1 + Math.pow( Math.tan( lambda ), 2.0 ) ) *
					( b0 * b0 + a0 * a0 * Math.pow( Math.tan( psi ), 2.0 ) ) ) );
			y = Math.abs( x * Math.tan( lambda ) );
		}

		if ( longitude < -90.0 || longitude > 90.0 )
		{
			x = -x;
		}
		if ( longitude < 0.0 )
		{
			y = -y;
		}

		/* Calculate z axis coordinate with respect to the original ellipsoid */
		if ( latitude == 90.0 )
		{
			z = b0;
		}
		else if ( latitude == -90.0 )
		{
			z = -b0;
		}
		else
		{
			z = Math.tan( psi ) * Math.sqrt( ( a0 * a0 * b0 * b0 ) / ( b0 * b0 + a0 * a0 * Math.pow( Math.tan( psi ), 2.0 ) ) );
		}

		/* Calculate the geocentric latitude with respect to the new ellipsoid */
		psi1 = Math.atan( ( z - dz ) / Math.sqrt( ( x - dx ) * ( x - dx ) + ( y - dy ) * ( y - dy ) ) );

		/* Convert to geocentric latitude and save return value */
		latitude = Math.atan( Math.tan( psi1 ) / ( 1 - es1 ) ) * 180.0 / PI;

		/* Calculate the longitude with respect to the new ellipsoid */
		longitude = Math.atan( ( y - dy ) / ( x - dx ) ) * 180.0 / PI;

		/* Correct the resultant for negative x values */
		if ( x - dx < 0.0 )
		{
			if ( y - dy > 0.0 )
			{
				longitude = 180.0 + longitude;
			}
			else
			{
				longitude = -180.0 + longitude;
			}
		}

		return new XY( latitude, longitude );
	}


	/**
	 *  Converts latitude / longitude (rel. to given datum) coordinates to German
	 *  GaussKrueger coordinates.
	 *
	 *@param  lat    Latitude
	 *@param  lon    Longitude
	 *@param  datum  Datum of the given coord.
	 *@return        Converted GaussKrueger coordinates
	 */
	// Note: takes the "Uli" datum as reference geodetic earth datum.

	public static XY convertToGaussKrueger( double lat, double lon, int datum )
	{
		COORD c = new COORD( datum );
		XY result = new XY( 1e8, 0 );
		int resultmerid = 0;

		double merid;
		int imerid;

		for ( merid = 3.0, imerid = 1; imerid <= 6; merid += 3.0, imerid++ )
		{
			XY temp = c.convertToTM( lat, lon, 0.0, merid, 1.0 );

			if ( Math.abs( temp.x ) < Math.abs( result.x ) )
			{
				result = temp;
				resultmerid = imerid;
			}
		}

		result.x += 500000;
		result.x += 1e6 * resultmerid;

		return result;
	}


	/**
	 *  Converts latitude / longitude (WGS84) coordinates to German GaussKrueger
	 *  coordinates.
	 *
	 *@param  lat  Latitude
	 *@param  lon  Longitude
	 *@return      Converted GaussKrueger coordinates
	 */
	public static XY convertToGaussKrueger( double lat, double lon )
	{
		return convertToGaussKrueger( lat, lon, WGS84ID );
	}


	/**
	 *  Converts GaussKrueger coordinates to latitude / longitude relative to the
	 *  WGS84 earth datum.
	 *
	 *@param  x  Parameter
	 *@param  y  Parameter
	 *@return    Returned Value
	 *@return    Returned Value
	 *@return    Returned Value
	 *@return    Returned Value
	 *@return    Returned Value
	 *@return    Returned Value
	 *@return    Returned Value
	 *@return    Converted coordinates.
	 */
	// Note: takes the "Uli" datum as reference geodetic earth datum.

	public static XY convertToLatLong( double x, double y )
	{
		return convertToLatLong( x, y, WGS84ID );
	}


	/**
	 *  Converts to latitude / longitude using the given earth datum.
	 *
	 *@param  x  GaussKrueger X.
	 *@param  y  GaussKrueger Y.
	 *@param  d  Earth datum to use for resulting coordinates.
	 *@return    Converted coordinates.
	 */
	public static XY convertToLatLong( double x, double y, int d )
	{
		COORD c = new COORD( d );
		int merid = 3 * ( ( int ) ( x / 1e6 ) );
		x %= 1e6;
		x -= 500000;

		return c.convertFromTM( x, y, 0.0, merid, 1.0 );
	}


	/**
	 *  Converts Gauss-Krueger to lat/long.
	 *
	 *@param  x  Parameter
	 *@param  y  Parameter
	 *@return    Returned Value
	 */
	public static XY convertToLL( double x, double y )
	{
		XY gk = COORD.convertToLatLong( x, y, 102 );
		return gk;
	}


	/**
	 *  Converts lat/long to Gauss-Krueger.
	 *
	 *@param  lat  Parameter
	 *@param  lon  Parameter
	 *@return      Returned Value
	 */
	public static XY convertToGK( double lat, double lon )
	{
		return COORD.convertToGaussKrueger( lat, lon, 102 );
	}


	/**
	 *  Retrieve earth datum parameters for given ID.
	 *
	 *@param  datum  Parameter
	 *@return        Returned Value
	 */
	protected XY datumParams( int datum )
	{
		double f = 1.0 / gEllipsoid[gDatum[datum].ellipsoid].invf;
		// flattening
		double a;
		// flattening
		double es;
		es = ( 2 * f - f * f );
		// eccentricity^2
		a = ( gEllipsoid[gDatum[datum].ellipsoid].a );
		// semimajor axis

		return new XY( a, es );
	}


	/**
	 *  Performs a transmercator (TM) projection.
	 *
	 *@param  lat   Parameter
	 *@param  lon   Parameter
	 *@param  lat0  Parameter
	 *@param  lon0  Parameter
	 *@param  k0    Parameter
	 *@return       Returned Value
	 */
	protected XY convertToTM( double lat, double lon, double lat0, double lon0, double k0 )
	{
		double m;
		double et2;
		double n;
		double t;
		double c;
		double A;
		double a;
		double m0;
		double es;
		double lambda;
		double phi;
		double lambda0;
		double phi0;

		XY result = new XY();

		XY val = datumParams( datum );
		a = val.x;
		es = val.y;

		lambda = lon * Degree;
		phi = lat * Degree;

		phi0 = lat0 * Degree;
		lambda0 = lon0 * Degree;

		m0 = M( phi0, a, es );
		m = M( phi, a, es );

		et2 = es / ( 1 - es );

		n = a / Math.sqrt( 1 - es * Math.pow( Math.sin( phi ), 2.0 ) );
		t = Math.pow( Math.tan( phi ), 2.0 );
		c = et2 * Math.pow( Math.cos( phi ), 2.0 );
		A = ( lambda - lambda0 ) * Math.cos( phi );
		result.x = k0 * n * ( A + ( 1.0 - t + c ) * A * A * A / 6.0
				 + ( 5.0 - 18.0 * t + t * t + 72.0 * c - 58.0 * et2 ) * Math.pow( A, 5.0 ) / 120.0 );
		result.y = k0 * ( m - m0 + n * Math.tan( phi ) * ( A * A / 2.0
				 + ( 5.0 - t + 9.0 * c + 4 * c * c ) * Math.pow( A, 4.0 ) / 24.0
				 + ( 61.0 - 58.0 * t + t * t + 600.0 * c - 330.0 * et2 ) *
				Math.pow( A, 6.0 ) / 720.0 ) );

		return result;
	}


	/**
	 *  Performs re-projection from TM.
	 *
	 *@param  x     Parameter
	 *@param  y     Parameter
	 *@param  lat0  Parameter
	 *@param  lon0  Parameter
	 *@param  k0    Parameter
	 *@return       Returned Value
	 */
	protected XY convertFromTM( double x, double y, double lat0, double lon0, double k0 )
	{
		double a;
		double m0;
		double es;
		double et2;
		double m;
		double e1;
		double mu;
		double phi1;
		double c1;
		double t1;
		double n1;
		double r1;
		double d;
		double phi0;
		double lambda0;
		double lat;
		double lon;

		phi0 = lat0 * Degree;
		lambda0 = lon0 * Degree;

		//datumParams(gFilePrefs.datum, &a, &es);
		XY val = datumParams( datum );
		a = val.x;
		es = val.y;

		m0 = M( phi0, a, es );
		et2 = es / ( 1.0 - es );
		m = m0 + y / k0;

		e1 = ( 1.0 - Math.sqrt( 1.0 - es ) ) / ( 1.0 + Math.sqrt( 1.0 - es ) );
		mu = m / ( a * ( 1.0 - es / 4.0 - 3.0 * es * es / 64.0 - 5.0 * es * es * es / 256.0 ) );
		phi1 = mu + ( 3.0 * e1 / 2.0 - 27.0 * Math.pow( e1, 3.0 ) / 32.0 ) * Math.sin( 2.0 * mu )
				 + ( 21.0 * e1 * e1 / 16.0 - 55.0 * Math.pow( e1, 4.0 ) / 32.0 )
				 * Math.sin( 4.0 * mu ) + 151.0 * Math.pow( e1, 3.0 ) / 96.0 * Math.sin( 6.0 * mu )
				 + 1097.0 * Math.pow( e1, 4.0 ) / 512.0 * Math.sin( 8.0 * mu );
		c1 = et2 * Math.pow( Math.cos( phi1 ), 2.0 );
		t1 = Math.pow( Math.tan( phi1 ), 2.0 );
		n1 = a / Math.sqrt( 1 - es * Math.pow( Math.sin( phi1 ), 2.0 ) );
		r1 = a * ( 1.0 - es ) / Math.pow( 1.0 - es * Math.pow( Math.sin( phi1 ), 2.0 ), 1.5 );
		d = x / ( n1 * k0 );
		lat = ( phi1 - n1 * Math.tan( phi1 ) / r1
				 * ( d * d / 2.0 - ( 5.0 + 3.0 * t1 + 10.0 * c1 - 4.0 * c1 * c1 - 9.0 * et2 )
				 * Math.pow( d, 4.0 ) / 24.0 + ( 61.0 + 90.0 * t1 + 298.0 * c1 + 45.0 * t1 * t1
				 - 252.0 * et2 - 3.0 * c1 * c1 ) * Math.pow( d, 6.0 ) / 720.0 ) ) / Degree;
		lon = ( lambda0 + ( d - ( 1.0 + 2.0 * t1 + c1 ) * Math.pow( d, 3.0 ) / 6.0
				 + ( 5.0 - 2.0 * c1 + 28.0 * t1 - 3.0 * c1 * c1 + 8.0 * et2 + 24.0 * t1 * t1 )
				 * Math.pow( d, 5.0 ) / 120.0 ) / Math.cos( phi1 ) ) / Degree;

		return new XY( lat, lon );
	}


	/**
	 *  Calculate M.
	 *
	 *@param  phi  Parameter
	 *@param  a    Parameter
	 *@param  es   Parameter
	 *@return      Returned Value
	 */
	protected double M( double phi, double a, double es )
	{
		double fix;

		if ( phi == 0.0 )
		{
			return 0.0;
		}
		else
		{
			fix = a * (
					( 1.0 - es / 4.0 - 3.0 * es * es / 64.0 - 5.0 * es * es * es / 256.0 ) * phi -
					( 3.0 * es / 8.0 + 3.0 * es * es / 32.0 + 45.0 * es * es * es / 1024.0 ) *
					Math.sin( 2.0 * phi ) +
					( 15.0 * es * es / 256.0 + 45.0 * es * es * es / 1024.0 ) * Math.sin( 4.0 * phi ) -
					( 35.0 * es * es * es / 3072.0 ) * Math.sin( 6.0 * phi ) );
			return fix;
		}
	}
}
