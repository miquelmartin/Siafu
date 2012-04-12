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

package de.nec.nle.siafu.types;


/**
 * This <code>Publishable</code> encapsulates an <code>double</code>
 * primitive and makes it serializable (it can be flattened and blown up again)
 * for use with Siafu.
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
	 *            the object to base this <code>Text</code> on
	 */
	public FloatNumber(final FlatData flatData) {
		String data = flatData.getData();
		TypeUtils.check(this, data);
		Double number = new Double(data.substring(data.indexOf(':') + 1));
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
	 * @return a <code>FlatData</code> object representing this object
	 */
	public FlatData flatten() {
		String data;
		// We use simple name, because our package is de.nec.nle.siafu.data
		data = this.getClass().getSimpleName() + ":";
		data += d;
		return new FlatData(data);
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
