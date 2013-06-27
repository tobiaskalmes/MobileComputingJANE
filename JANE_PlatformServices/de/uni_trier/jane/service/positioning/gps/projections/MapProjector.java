package de.uni_trier.jane.service.positioning.gps.projections;

import de.uni_trier.jane.basetypes.Position;
import de.uni_trier.jane.service.positioning.gps.nmea_parser.types.*;


public interface MapProjector {

	public Position project(Latitude latitude, Longitude longitude);

}
