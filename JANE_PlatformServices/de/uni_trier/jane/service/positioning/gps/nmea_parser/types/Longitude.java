package de.uni_trier.jane.service.positioning.gps.nmea_parser.types;

import de.uni_trier.jane.service.positioning.gps.nmea_parser.enums.*;

public class Longitude {

	private double value;
	private EastWestIndicator eastWestIndicator;
	
	public Longitude(double value, EastWestIndicator eastWestIndicator) {
		this.value = value;
		this.eastWestIndicator = eastWestIndicator;
	}

	public double getValue() {
		return value;
	}

	public EastWestIndicator getEastWestIndicator() {
		return eastWestIndicator;
	}

}
