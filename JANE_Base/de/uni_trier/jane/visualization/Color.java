/*****************************************************************************
 * 
 * Color.java
 * 
 * $Id: Color.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $
 *  
 * Copyright (C) 2002 Hannes Frey and Johannes K. Lehnert
 * 
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation; either version 2 
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with this program; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 *****************************************************************************/
package de.uni_trier.jane.visualization;

import java.io.*;

/**
 * This class stores colors encoded with RGB-values.
 */
public class Color implements Serializable{

	public final static String VERSION = "$Id: Color.java,v 1.1 2007/06/25 07:21:36 srothkugel Exp $";

	public static final Color BLACK = new Color(0, 0, 0, "black");
	public static final Color WHITE = new Color(255, 255, 255, "white");
	public static final Color RED = new Color(255, 0, 0, "red");
	public static final Color GREEN = new Color(0, 255, 0, "green");
	public static final Color BLUE = new Color(0, 0, 255, "blue");
	public static final Color DARKRED = new Color(128, 0, 0, "darkred");
	public static final Color DARKGREEN = new Color(0, 128, 0, "darkgreen");
	public static final Color DARKBLUE = new Color(0, 0, 128, "darkblue");
	public static final Color LIGHTGREY = new Color(192, 192, 192, "lightgrey");
	public static final Color GREY = new Color(128, 128, 128, "grey");
	public static final Color DARKGREY = new Color(64, 64, 64, "darkgrey");
	public static final Color YELLOW = new Color(255, 255, 0, "yellow");
	public static final Color ORANGE = new Color(255, 128, 0, "orange");
	public static final Color CYAN = new Color(128, 255, 255, "cyan");
	public static final Color BROWN = new Color(128, 64, 64, "brown");
	public static final Color PINK = new Color(128, 0, 128, "pink");
	public static final Color LIGHTBLUE = new Color(135, 206, 250, "lighblue");
	public static final Color LIGHTRED = new Color(255, 127, 127, "lightred");
	public static final Color LIGHTGREEN = new Color(127, 255, 127, "lightgreen");

	public static final Color[] COLOR_MAP = new Color[] { BLACK, WHITE, RED, GREEN, BLUE, LIGHTGREY, GREY,
		DARKGREY, YELLOW, ORANGE, CYAN, BROWN, PINK, LIGHTBLUE, LIGHTRED, LIGHTGREEN, DARKRED, DARKGREEN, DARKBLUE};
	
	private int red;
	private int green;
	private int blue;
	private String name;

	/**
	 * Construct a new color.
	 * @param red the red value
	 * @param green the green value
	 * @param blue the blue value
	 */
	public Color(int red, int green, int blue) {
		this(red, green, blue, "");
	}

	public Color(int red, int green, int blue, String name) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.name = name;
	}


	/**
	 * Get the blue value.
	 * @return the blue value
	 */
	public int getBlue() {
		return blue;
	}


	/**
	 * Get the green value.
	 * @return the green value
	 */
	public int getGreen() {
		return green;
	}


	/**
	 * Get the red value.
	 * @return the red value
	 */
	public int getRed() {
		return red;
	}

	/**
	 * Mix the two given color values to a new one. The alpha value in [0,1] determines how many of
	 * the first and second color value is used. If the given alpha value is less or equal than 0 / greater
	 * or equal than 1 the first one / second one is returned.
	 * @param color1 the first color value
	 * @param color2 the second color value
	 * @param alpha the fraction of the first color
	 * @return the mixed color
	 */
	public static Color mixColor(Color color1, Color color2, double alpha) {
		if(alpha <= 0.0) {
			return color1;
		}
		if(alpha >= 1.0) {
			return color2;
		}
		int red = (int)((1.0 - alpha) * color1.getRed() + alpha * color2.getRed());
		int green = (int)((1.0 - alpha) * color1.getGreen() + alpha * color2.getGreen());
		int blue = (int)((1.0 - alpha) * color1.getBlue() + alpha * color2.getBlue());
		return new Color(red, green, blue);
	}

	public int hashCode() {
		final int PRIME = 1000003;
		int result = 0;
		result = PRIME * result + red;
		result = PRIME * result + green;
		result = PRIME * result + blue;

		return result;
	}

	public boolean equals(Object oth) {
		if (this == oth) {
			return true;
		}

		if (oth == null) {
			return false;
		}

		if (oth.getClass() != getClass()) {
			return false;
		}

		Color other = (Color) oth;

		if (this.red != other.red) {
			return false;
		}

		if (this.green != other.green) {
			return false;
		}

		if (this.blue != other.blue) {
			return false;
		}

		return true;
	}

	public String toString() {
		if(name.length() == 0) {
			return "(" + red + "," + green + "," + blue + ")";
		}
		return name;
	}
	
}

