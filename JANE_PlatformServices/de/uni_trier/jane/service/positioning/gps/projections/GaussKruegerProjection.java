package de.uni_trier.jane.service.positioning.gps.projections;



import org.iu.gps.COORD;
import org.iu.gps.XY;

import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.service.positioning.gps.nmea_parser.types.*;


public class GaussKruegerProjection implements MapProjector {

	private double centerLatitude;
	private double centerLongitude;
	private Position center;

	public GaussKruegerProjection(double centerLatitude, double centerLongitude) {
		this.centerLatitude = centerLatitude;
		this.centerLongitude = centerLongitude;
		XY xy = COORD.convertToGaussKrueger(0,0);
		
		center=new Position(xy.x,xy.y);
		
	}

	public Position project(Latitude latitude, Longitude longitude) {
		if(Math.abs(longitude.getValue()-centerLongitude) >= 4.0) {
			throw new IllegalArgumentException("The given longiude is not inside the meridian stripe.");
		}
		double lat = latitude.getValue() - centerLatitude;
		double lon = longitude.getValue() - centerLongitude;
		XY xy = COORD.convertToGaussKrueger(lat, lon);
		return new Position(xy.x, xy.y).sub(center);
	}

}
