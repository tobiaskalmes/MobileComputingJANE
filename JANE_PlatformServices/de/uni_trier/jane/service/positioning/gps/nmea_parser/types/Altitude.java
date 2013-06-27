package de.uni_trier.jane.service.positioning.gps.nmea_parser.types;

import de.uni_trier.jane.service.positioning.gps.nmea_parser.enums.*;

public class Altitude {

	private double value;
	private Units units;

	public Altitude(double value, Units units) {
		this.value = value;
		this.units = units;
	}

	public Units getUnits() {
		return units;
	}

	public double getValue() {
		return value;
	}

}
