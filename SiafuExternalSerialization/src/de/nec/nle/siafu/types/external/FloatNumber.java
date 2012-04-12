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

package de.nec.nle.siafu.types.external;

import de.nec.nle.siafu.exceptions.external.InvalidFlatDataException;

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
 * @see de.nec.nle.siafu.types.FloatNumber
 * 
 * @author Miquel Martin
 * 
 */
public class FloatNumber implements Publishable {

	/**
	 * The double number contained in this object.
	 */
	private double d;

	/**
	 * Build an <code>DoubleNumber</code> encapsulating <code>i</code>.
	 * 
	 * @param d
	 *            the number to use
	 */
	public FloatNumber(final double d) {
		this.d = d;
	}

	/**
	 * Build an <code>DoubleNumber</code> object by taking the number that
	 * follows the ":" delimiter of the flattened data object.
	 * 
	 * @param flatData
	 *            the String to base this <code>FloatNumber</code> on
	 * @throws InvalidFlatDataException
	 *             if the provided flatdata is not properly formatted, or is not
	 *             a flattened form of this class.
	 */
	public FloatNumber(final String flatData) throws InvalidFlatDataException {
		TypeUtils.check(this, flatData);
		Double number = new Double(flatData
				.substring(flatData.indexOf(':') + 1));
		d = number.doubleValue();
	}

	/**
	 * Generate a string with the number in the object.
	 * 
	 * @return the string representing the number
	 */
	public String toString() {
		return "" + d;
	}

	/**
	 * Get the <code>double</code> encapsulated by this object.
	 * 
	 * @return the <code>double</code> value
	 */
	public double getNumber() {
		return d;
	}

	/**
	 * Generates a flattened version of the data, of the type
	 * <code>FloatNumber:float</code> where content is the string encapsulated
	 * in the <code>FloatNumber</code> object.
	 * 
	 * @return a flatdata String representing this object
	 */
	public String flatten() {
		String data;
		// We use simple name, because the fleshed out version of this class is
		// in the default package for data types: de.nec.nle.siafu.data
		String fullClassName = this.getClass().getName();
		String className = fullClassName.substring(fullClassName
				.lastIndexOf(".")+1);
		data = className + ":";
		data += d;
		return data;
	}

	public boolean equals(Object o) {
		if (!(o instanceof FloatNumber)) {
			return false;
		} else {
			FloatNumber fn = (FloatNumber) o;
			return (fn.getNumber() == d);
		}
	}

	public int hashCode() {
		return (this.getClass().getName() + d).hashCode();
	}
}
