package de.uni_trier.jane.service.positioning.gps.nmea_parser.enums;

public class NorthSouthIndicator {

	public static final NorthSouthIndicator NORTH = new NorthSouthIndicator("NORTH");
	public static final NorthSouthIndicator SOUTH = new NorthSouthIndicator("SOUTH");

	private String name;

	private NorthSouthIndicator(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
	
	public static NorthSouthIndicator parse(String token) {
		if(token.equals("N")) {
			return NORTH;
		}
		else if(token.equals("S")) {
			return SOUTH;
		}
		else {
			throw new IllegalArgumentException("The given token is no NorthSouthIndicator.");
		}
	}

}
