/*
 * Copyright NEC Europe Ltd. 2006-2007
 * 
 * This file is part of the context simulator called Siafu.
 * 
 * Siafu is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * Siafu is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.nec.nle.siafu.graphics;

import org.eclipse.swt.graphics.RGB;

import de.nec.nle.siafu.graphics.markers.InvalidColorException;

/**
 * An utility class to parse colors back and forth from String form to RGB.
 * 
 * @author Miquel Martin
 * 
 */
public final class ColorTools {
	/** Position of Red in the color string. */
	private static final int RED_POSITION = 1;

	/** Position of green in the color string. */
	private static final int GREEN_POSITION = 3;

	/** Position of blue in the color string. */
	private static final int BLUE_POSITION = 5;

	/** Base 16. Checkstyle forced me. */
	private static final int BASE_16 = 16;

	/** Length of a properly formatted color string. */
	private static final int COLOR_STRING_LENGTH = 7;

	/** Keep this class from being instantiated. */
	private ColorTools() {
	}

	/** Default color for an agent. */
	public static final String COLOR_AGENT = "#C00000";

	/** Default color for a place. */
	public static final String COLOR_PLACE = "#0000CC";

	/**
	 * Convert an RGB color into a html style string that represents it.
	 * 
	 * @param color the color to convert
	 * @return a String of the type "#RRBBGG"
	 */
	public static String parseRGBColor(final RGB color) {
		return "#" + Integer.toHexString(color.red)
				+ Integer.toHexString(color.green)
				+ Integer.toHexString(color.blue);
	}

	/**
	 * Parse a String of the form "#RRGGBB" into an RGB color, where RR is the
	 * red value represented by an hexadeximal number. This is the classical
	 * html color definition format.
	 * 
	 * @param color the color String
	 * @return an RGB color representing that String
	 */
	public static RGB parseColorString(final String color) {
		RGB rgb;
		try {
			if (color.length() == COLOR_STRING_LENGTH) {
				if (color.charAt(0) == '#') {
					rgb =
							new RGB(Integer
									.parseInt(color.substring(
										RED_POSITION, RED_POSITION + 2),
										BASE_16), Integer.parseInt(color
									.substring(GREEN_POSITION,
										GREEN_POSITION + 2), BASE_16),
									Integer.parseInt(color.substring(
										BLUE_POSITION, BLUE_POSITION + 2),
										BASE_16));
					return rgb;
				}
			}
			throw new InvalidColorException("Invalid color: \"" + color
					+ "\"");
		} catch (NumberFormatException e) {
			throw new InvalidColorException("Invalid color: \"" + color
					+ "\"");
		}
	}

}
