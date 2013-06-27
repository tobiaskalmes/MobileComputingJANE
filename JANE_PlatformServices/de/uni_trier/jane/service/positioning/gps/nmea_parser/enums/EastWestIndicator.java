package de.uni_trier.jane.service.positioning.gps.nmea_parser.enums;

public class EastWestIndicator {

	public static final EastWestIndicator EAST = new EastWestIndicator("EAST");
	public static final EastWestIndicator WEST = new EastWestIndicator("WEST");

	private String name;

	private EastWestIndicator(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	public static EastWestIndicator parse(String token) {
		if(token.equals("E")) {
			return EAST;
		}
		else if(token.equals("W")) {
			return WEST;
		}
		else {
			throw new IllegalArgumentException("The given token is no EastWestIndicator.");
		}
	}

}
