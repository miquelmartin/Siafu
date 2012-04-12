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
 * @see de.nec.nle.siafu.model.Place
 * 
 * @author Miquel Martin
 * 
 */
public class Place implements Publishable {

	/** The name of the place. */
	private String name;

	/**
	 * The type of place. If the place was created using an image, this is the
	 * image name.
	 */
	private String type;

	/** The position of the place. */
	private Position pos;

	/**
	 * Create a place specifying all of the parameters.
	 * 
	 * @param type
	 *            the type of place (e.g. office, airport, restroom)
	 * @param pos
	 *            the position of the place
	 * @param name
	 *            the name of the place
	 * 
	 * @throws PositionUnreachableException
	 */
	// Can throw position unreachable
	public Place(final String type, final Position pos, final String name) {
		this.type = type;
		this.pos = pos;
		this.name = name;

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
	public Place(final String flatData) throws InvalidFlatDataException {
		final int nameField = 0;
		final int typeField = 1;
		final int latField = 2;
		final int lonField = 3;
		TypeUtils.check(this, flatData);
		String place = flatData.substring(flatData.indexOf(':') + 1);
		String[] part = TypeUtils.split(place, '#');
		this.name = part[nameField];
		this.type = part[typeField];
		double lat = new Double(part[latField]).doubleValue();
		double lon = new Double(part[lonField]).doubleValue();
		this.pos = new Position(lat, lon);

	}

	/**
	 * Turn the place to a String by returning it's name.
	 * 
	 * @return the place's name
	 */
	public String toString() {
		return getName();
	}

	/**
	 * Get the place's name.
	 * 
	 * @return the place's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the place.
	 * 
	 * @param name
	 *            the place name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Get the place's type.
	 * 
	 * @return the place's type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Get the position of the place.
	 * 
	 * @return the place's position
	 */
	public Position getPos() {
		return pos;
	}

	/**
	 * Flatten out the Place object with the format:
	 * <code>de.nec.nle.siafu.model.Place:name#type#latitude#longitude</code>.
	 * 
	 * @return a flatdata String representing this object
	 * 
	 */
	public String flatten() {
		String data;
		data = TypeUtils.stripExternal(this) + ":";
		data += name + "#";
		data += type + "#";
		data += pos.getLatitude() + "#" + pos.getLongitude();
		return data;
	}

	public boolean equals(Object o) {
		if (!(o instanceof Place)) {
			return false;
		} else {
			Place p = (Place) o;
			return (p.getName().equals(name) && p.getType().equals(type) && p
					.getPos().equals(pos));
		}
	}

	public int hashCode() {
		return (this.getClass().getName() + name + type + pos.toString())
				.hashCode();
	}

}
