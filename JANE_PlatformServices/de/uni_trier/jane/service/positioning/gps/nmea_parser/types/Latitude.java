package de.uni_trier.jane.service.positioning.gps.nmea_parser.types;

import de.uni_trier.jane.service.positioning.gps.nmea_parser.enums.*;

public class Latitude {

	private double value;
	private NorthSouthIndicator northSouthIndicator;

	public Latitude(double value, NorthSouthIndicator northSouthIndicator) {
		this.value = value;
		this.northSouthIndicator = northSouthIndicator;
	}

	public double getValue() {
		return value;
	}

	public NorthSouthIndicator getNorthSouthIndicator() {
		return northSouthIndicator;
	}

}
