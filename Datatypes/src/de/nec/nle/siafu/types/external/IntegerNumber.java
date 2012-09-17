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
 * @see de.nec.nle.siafu.types.IntegerNumber
 * 
 * @author Miquel Martin
 * 
 */
public class IntegerNumber implements Publishable {

	/**
	 * The integer number contained in this object.
	 */
	private int i;

	/**
	 * Build an <code>IntegerNumber</code> encapsulating <code>i</code>.
	 * 
	 * @param i
	 *            the number to use
	 */
	public IntegerNumber(final int i) {
		this.i = i;
	}

	/**
	 * Build an <code>IntegerNumber</code> object by taking the number that
	 * follows the ":" delimiter * of the flattened data object.
	 * 
	 * @param flatData
	 *            the String to base this <code>IntegerNumber</code> on
	 * @throws InvalidFlatDataException
	 *             if the provided flatdata is not properly formatted, or is not
	 *             a flattened form of this class.
	 */
	public IntegerNumber(final String flatData) throws InvalidFlatDataException {
		TypeUtils.check(this, flatData);
		Integer number = new Integer(flatData
				.substring(flatData.indexOf(':') + 1));
		i = number.intValue();
	}

	/**
	 * Generate a string with the number in the object.
	 * 
	 * @return the string representing the number
	 */
	public String toString() {
		return "" + i;
	}

	/**
	 * Get the <code>int</code> encapsulated by this object.
	 * 
	 * @return the <code>int</code> value
	 */
	public int getNumber() {
		return i;
	}

	/**
	 * Generates a flattened version of the data, of the type
	 * <code>IntegerNumber:int</code> where content is the string encapsulated
	 * in the <code>IntegerNumber</code> object.
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
		data += i;
		return data;
	}

	public boolean equals(Object o) {
		if (!(o instanceof IntegerNumber)) {
			return false;
		} else {
			IntegerNumber in = (IntegerNumber) o;
			return (in.getNumber() == i);
		}
	}

	public int hashCode() {
		return (this.getClass().getName() + i).hashCode();
	}
}
