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

package de.nec.nle.siafu.model.external;

import de.nec.nle.siafu.exceptions.external.InvalidFlatDataException;
import de.nec.nle.siafu.types.external.Publishable;
import de.nec.nle.siafu.types.external.TypeUtils;

/**
 * This class represents a deserialized Place object. It is, however, <i>not</i>
 * a Place object in the Siafu sense of the class, but rather a skeleton
 * representation with the same data fields, but none of the logic.
 * <p>
 * The purpose of this class is to facilitate dealing with FlatData strings
 * returned by Siafu (using the String constructor), so they can be handled as
 * Java objects.
 * <p>
 * Notice that the package in this class contains "external" as the last sub
 * package, to avoid erratic behavior should you (mistakenly) add this library
 * to a simulation or a Siafu deployment.
 * 
 * @see de.nec.nle.siafu.model.Position
 * 
 * @author Miquel Martin
 * 
 */
public class Position implements Comparable, Publishable {

	/** Longitude as a double. */
	private double lon;

	/** Latitude as a double. */
	private double lat;

	/**
	 * Create a position using coordinates. The coordinates are then translated
	 * to rows and columns using the calibration provided in the config file.
	 * 
	 * @param lat
	 *            the latitude
	 * @param lon
	 *            the longitude
	 * @throws PositionOutOfTheMapException
	 *             if those coordinates map to a position outside the map
	 */
	public Position(final double lat, final double lon) {
		this.lat = lat;
		this.lon = lon;
	}

	/**
	 * Builds a Position as defined by a FlatData object. Internally, the
	 * latitude and longitude is converted to a row and column in the
	 * simulator's matrix.
	 * 
	 * @param flatData
	 *            the flat data string with the latitude and longitude
	 * @throws InvalidFlatDataException
	 *             if the provided flatdata is not properly formatted, or is not
	 *             a flattened form of this class.
	 */
	public Position(final String flatData) throws InvalidFlatDataException {
		TypeUtils.check(this, flatData);
		String period = flatData.substring(flatData.indexOf(':') + 1);
		String[] part = TypeUtils.split(period, '#');

		this.lat = new Double(part[0]).doubleValue();
		this.lon = new Double(part[1]).doubleValue();
	}

	/**
	 * Print the position in the form "row.col".
	 * 
	 * @return the String representing the position
	 */
	public String toString() {
		return lat + "." + lon;
	}

	/**
	 * Calculate the position's hashcode, which is the toString() output's hash
	 * code.
	 * 
	 * @return the hashcode of the toString() output
	 */
	public int hashCode() {
		return (this.getClass().getName() + lat + lon).hashCode();
	}

	/**
	 * Equals method which returns true if the positions have the same row and
	 * column.
	 * 
	 * @param o
	 *            the object to compare this position to
	 * @return true if the positions are equal
	 */
	public boolean equals(final Object o) {
		if (o instanceof Position) {
			Position p = (Position) o;

			if ((p.lat == lat) && (p.lon == lon)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * The comparable interface for positions. A position compares larger than
	 * another one if the row is lower or, being in equal rows if the column is
	 * more to the left. In other words, bottom left, tiny, top right, big.
	 * 
	 * @param o
	 *            the object to compare to
	 * @return an integer according to the comparable interface
	 */
	public int compareTo(final Object o) {
		Position p = (Position) o;
		if (p.lat > lat) {
			return -1;
		} else if (p.lat < lat) {
			return 1;
		} else if (p.lon > lon) {
			return -1;
		} else if (p.lon < lon) {
			return 1;
		} else {
			return (0);
		}
	}

	/**
	 * Flattens the data in this Position object into the form:
	 * <code>de.nec.nle.siafu.model.Position:Lat#Lon</code> where Lat and Lon
	 * are the latitude and longitude in decimal format.
	 * 
	 * @return a flatdata String representing this object
	 */
	public String flatten() {
		String data;
		data = TypeUtils.stripExternal(this) + ":";
		data += lat + "#" + lon;
		return data;
	}

	/**
	 * Get the row for this position. The first row, 0, is on top of the map.
	 * 
	 * @return the row number
	 */
	public double getLatitude() {
		return lat;
	}

	/**
	 * Get the column for this position. The first column, 0, is on the left
	 * side of the map.
	 * 
	 * @return the row number
	 */
	public double getLongitude() {
		return lon;
	}
}
