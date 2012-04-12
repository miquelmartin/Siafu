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

package de.nec.nle.siafu.model;

import java.text.NumberFormat;

/**
 * Siafu's concept of space is actually discrete. When you provide the map
 * images in the simulation data, these are broken down into an array of
 * pixels, and the rows and columns of those become the real positions used
 * internally during the simulation.
 * 
 * This is naturally not satisfactory for the purpose of location simulation,
 * and so, the simulation data config file allows you to provide the
 * coordinates that calibrate the map into a real world place. The Coordinate
 * class is used to map the internal position used by Siafu into
 * latitude/longitude pairs.
 * 
 * The map is a rectangular portion of the world, not necessarily north
 * oriented. To calibrate it, you provide the top right, bottom right and
 * bottom left coordinates.
 * 
 * Finding the right lat/long in our rectuangular map is a matter of
 * coordinate rotation from (x,y) to (lat,lon) plus offsetting where:
 * 
 * x = mapWitdhDegrees * j/w <br>
 * y = mapHeightDegrees * (h-i)/h <br>
 * 
 * where: <br>
 * mapWitdth = sqrt( (brLat - blLat)^2 + (brLon - blLon)^2 ) <br>
 * mapHeight = sqrt( (trLat - brLat)^2 + (trLon - brLon)^2 ) <br>
 * 
 * Then: <br>
 * lat = x*sin(A) + y*cos(A) + blLat <br>
 * long = x*cos(A) - y*sin(A) + blLon <br>
 * 
 * where A is the tilt angle of the map:
 * 
 * A = arcos((brLong - blLong) / mapWidthDegrees)<br>
 * 
 * To reverse the proces, and go from coordinates to a map row and column, you
 * need to rotate the other way around, so:
 * 
 * y = lon*sin(-A) + lat*cos(-A) - blLat = lon * (-sin(A)) +lat*cos(A) -
 * (blLat*cos(a)) + (blLon*sin(a)) <br>
 * x = lon*cos(-A) - lat*sin(-A) - blLon = lon * cos(A)+ lat*sin(A) -
 * (blLat*sin(a)) - (blLon * cos(a) <br>
 * 
 * And then:
 * 
 * j = w * (x / mapWitdhDegrees) <br>
 * i = h - ( h * (y / mapHeightDegrees))<br>
 * 
 * 
 * @author Miquel Martin
 * 
 */
public class CoordinateTools {
	/**
	 * Number of decimal places in the minutes to show in the pretty printed
	 * coordinates.
	 */
	private static final int PRETTY_COORDINATE_PRECISION = 3;

	/**
	 * Minutes per degree. You gotta love Checkstyle's magic number
	 * complaints.
	 */
	private static final int MINUTES_PER_DEGREE = 60;

	// /** The map's width in columns. */
	// private final int width;

	/** The map's height in rows. */
	private final int height;

	/** The latitude of the bottom right corner of the map. */
	private final double brLat;

	/** The longitude of the bottom right corner of the map. */
	private final double brLon;

	/** The latitude of the bottom left corner of the map. */
	private final double blLat;

	/** The longitude of the bottom left corner of the map. */
	private final double blLon;

	/** The latitude of the top right corner of the map. */
	private final double trLat;

	/** The longitude of the top right corner of the map. */
	private final double trLon;

	/** The map height in degrees. */
	private double mapHeightDegrees;

	/** The map width in degrees. */
	private double mapWidthDegrees;

	/**
	 * Optimization variable: <br>
	 * x = mapWidthDegrees * (j/w); Therefore, "jFactor = mapWidthDegrees /
	 * w".
	 */
	private double jFactor;

	/**
	 * Optimization variable: <br>
	 * y = mapHeightDegrees * ((h - i)/h); Therefore "iFactor =
	 * mapHeightDegrees / h".
	 */
	private double iFactor;

	/**
	 * Optimization variable: <br>
	 * j = w * (x / mapWitdhDegrees); Therefore "xFactor = w /
	 * mapWitdhDegrees".
	 */
	private double xFactor;

	/**
	 * Optimization variable: <br>
	 * "yOffset = - (blLat*cos(a)) + (blLon*sin(a))".
	 */
	private double yOffset;

	/**
	 * Optimization variable: <br>
	 * "xOffset = - (blLat*sin(a)) - (blLon * cos(a)".
	 */
	private double xOffset;

	/**
	 * The name of the hemisphere latitudewise (North or South being N and S).
	 */
	private final String latHemisphere;

	/**
	 * The name of the hemisphere, longitudewise (East and West being E and
	 * W).
	 */
	private final String lonHemisphere;

	/**
	 * True if the map spans the greenwich meridian or the equator, false
	 * otherwise.
	 */
	private final boolean crossesZeroes;

	/** The cosinus of A, used to calculate lat/long - row/col conversions. */
	private final double cosA;

	/** The sinus of A, used to calculate lat/long - row/col conversions. */
	private final double sinA;

	/** A number formatter, used to pretty print the coordinate values. */
	private static final NumberFormat FORMATER =
			NumberFormat.getInstance();

	/**
	 * Initialize the mapping between the Siafu simulation grid and the actual
	 * coordinates.
	 * 
	 * The simulation map corresponds to a rectangular area in the real world.
	 * There's no limitations in the aspect ratio of the retangle, and it does
	 * not need to be north oriented.
	 * 
	 * The inizialization calculates how the amount of latitude and longitude
	 * degrees associated to each grid point. Using the method, the
	 * transformation between grid points and coordinates costs only two
	 * multiplications and two additions.
	 * 
	 * @param height the number of rows in the Siafu map
	 * @param width the number of columns in the Siafu map
	 * @param topRight an double array conaining the latitude and longitude of
	 *            the top right corner of the map.
	 * @param bottomRight an double array conaining the latitude and longitude
	 *            of the bottom right corner of the map.
	 * @param bottomLeft an double array conaining the latitude and longitude
	 *            of the bottom left corner of the map.
	 */
	public CoordinateTools(final int height, final int width,
			final double[] topRight, final double[] bottomRight,
			final double[] bottomLeft) {
		// this.width = width;
		this.height = height;

		blLat = bottomLeft[0];
		blLon = bottomLeft[1];

		brLat = bottomRight[0];
		brLon = bottomRight[1];

		trLat = topRight[0];
		trLon = topRight[1];

		// Optimization: check if we need to figure out N/S and E/W everytime,
		// or we are always on the same hemispheres.
		if (Math.signum(blLon) != Math.signum(trLon)
				|| Math.signum(blLat) != Math.signum(trLat)
				|| Math.signum(blLat) != Math.signum(trLat - brLat)
				|| Math.signum(blLon) != Math.signum(trLon - brLon)) {
			crossesZeroes = true;
			latHemisphere = null;
			lonHemisphere = null;
		} else {
			crossesZeroes = false;
			latHemisphere = getHemisphereByLatitude(blLat);
			lonHemisphere = getHemisphereByLongitude(blLon);
		}

		mapWidthDegrees =
				Math.sqrt(Math.pow(brLat - blLat, 2)
						+ Math.pow((brLon - blLon), 2));
		mapHeightDegrees =
				Math.sqrt(Math.pow(trLat - brLat, 2)
						+ Math.pow((trLon - brLon), 2));

		jFactor = mapWidthDegrees / width;
		iFactor = mapHeightDegrees / height;
		xFactor = width / mapWidthDegrees;

		double a = Math.acos((brLon - blLon) / mapWidthDegrees);

		cosA = Math.cos(a);
		sinA = Math.sin(a);

		yOffset = -(blLat * cosA) + (blLon * sinA);
		xOffset = -(blLat * sinA) - (blLon * cosA);
	}

	/**
	 * Convert a Siafu map position (a row column pair) into a string of the
	 * form "N 49 34.233 E 23 22.322", according to the map calibration.
	 * 
	 * @param pos the position
	 * @return the prettified string
	 */
	public String localToPrettyCoordinates(final Position pos) {
		return coordinatesToPrettyCoordinates(localToCoordinates(pos
				.getRow(), pos.getCol()));
	}

	/**
	 * Convert a Siafu map position (a row column pair) into a string of the
	 * form "N 49 34.233 E 23 22.322", according to the map calibration.
	 * 
	 * @param i the row of the position
	 * @param j the column of the position
	 * @return the prettified string
	 */
	public String localToPrettyCoordinates(final int i, final int j) {
		return coordinatesToPrettyCoordinates(localToCoordinates(i, j));
	}

	/**
	 * Convert a coordinate pair into a string of the form "N 49 34.233 E 23
	 * 22.322".
	 * 
	 * @param coord the coordinates to transofrm
	 * @return the prettified string
	 */
	public String coordinatesToPrettyCoordinates(final double[] coord) {
		FORMATER.setMaximumFractionDigits(PRETTY_COORDINATE_PRECISION);

		double angle = Math.abs(coord[0]);
		String lat =
				(((int) Math.floor(angle)) + " " + FORMATER
						.format((angle % 1) * MINUTES_PER_DEGREE));

		angle = Math.abs(coord[1]);
		String lon =
				(((int) Math.floor(angle)) + " " + FORMATER
						.format((angle % 1) * MINUTES_PER_DEGREE));

		if (!crossesZeroes) {
			return latHemisphere + " " + lat + " " + lonHemisphere + " "
					+ lon;
		} else {
			return getHemisphereByLatitude(coord[0]) + " " + lat + " "
					+ getHemisphereByLongitude(coord[1]) + " " + lon;
		}
	}

	/**
	 * Returns the latitude and longitude corresponding to the map internal
	 * row i and column j.
	 * 
	 * @param i the row
	 * @param j the column
	 * @return a double[2] with the latitude in position 0, and the
	 *         longitudein position 1.
	 */
	public double[] localToCoordinates(final int i, final int j) {
		double x = jFactor * j;
		double y = iFactor * (height - i);

		double[] coordinate = new double[2];
		// latitude
		coordinate[0] = x * sinA + y * cosA + blLat;

		// longitude
		coordinate[1] = x * cosA - y * sinA + blLon;

		return coordinate;
	}

	/**
	 * Returns the latitude and longitude corresponding to the map internal
	 * row and column given by the Position parameter.
	 * 
	 * @param pos the position to convert
	 * @return a double[2] with the latitude in position 0, and the
	 *         longitudein position 1.
	 */
	public double[] localToCoordinates(final Position pos) {
		return localToCoordinates(pos.getRow(), pos.getCol());
	}

	/**
	 * Returns the map row and column corresponding to the given coordinates.
	 * 
	 * @param lat the latitude
	 * @param lon the longitude
	 * @return a Position with the Siafu position matching the given
	 *         coordinates
	 */
	public Position coordinatesToLocal(final double lat, final double lon) {
		double y = -(lon * sinA) + lat * cosA + yOffset;
		double x = lon * cosA + lat * sinA + xOffset;

		int i = (int) Math.floor(height * (1 - (y / mapHeightDegrees)));
		int j = (int) Math.floor(xFactor * x);

		return new Position(i, j);
	}

	/**
	 * Get the name of the hemisphere for this latitude.
	 * 
	 * @param latitude a double with the latitude
	 * @return "N" if the latitude is 0 or above degrees, "S" otherwise
	 */
	private String getHemisphereByLatitude(final double latitude) {
		if (latitude < 0) {
			return "S";
		} else {
			return "N";
		}
	}

	/**
	 * Get the name of the hemisphere for this longitude.
	 * 
	 * @param longitude a double with the longitude
	 * @return "E" if the latitude is 0 or above degrees, "W" otherwise
	 */
	private String getHemisphereByLongitude(final double longitude) {
		if (longitude < 0) {
			return "W";
		} else {
			return "E";
		}
	}
}
