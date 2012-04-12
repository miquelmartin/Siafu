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
 * This <code>Publishable</code> encapsulates a <code>boolean</code>
 * primitive and makes it serializable (it can be flattened and blown up again)
 * for use with Siafu.
 * 
 * @author Miquel Martin
 * 
 */
public class BooleanType implements Publishable {

	/**
	 * The boolean value contained in this object.
	 */
	private boolean b;

	/**
	 * Build a <code>BooleanType</code> object that contains a boolean
	 * primitive.
	 * 
	 * @param val
	 *            the boolean value
	 */
	public BooleanType(final boolean val) {
		this.b = val;
	}

	/**
	 * Build a <code>BooleanType</code> object that contains a boolean
	 * primitive, as given by <code>Boolean(String)</code>.
	 * 
	 * @param val
	 *            the boolean value
	 */
	public BooleanType(final String val) {
		this.b = new Boolean(val);
	}

	/**
	 * Build a <code>BooleanType</code> object by taking the string that
	 * follows the ":" delimiter of the flattened data object and making a
	 * Boolean out of it.
	 * 
	 * @param flatData
	 *            the object to base this <code>Text</code> on
	 */
	public BooleanType(final FlatData flatData) {
		String data = flatData.getData();
		TypeUtils.check(this, data);
		this.b = new Boolean(data.substring(data.indexOf(':') + 1));
	}

	/**
	 * Returns the a text string "true" or "false" depending on the value in the
	 * boolean encapsulated in this instance.
	 * 
	 * @return the string value of the boolean object
	 */
	public String toString() {
		return "" + b;
	}

	/**
	 * Returns the boolean primitive encapsulated in this instance.
	 * 
	 * @return the boolean value in the <code>BooleanType</code> object
	 */
	public Boolean getValue() {
		return b;
	}

	/**
	 * Generates a flattened version of the data, of the type
	 * <code>BooleanType:trueorfalse</code> where content is the boolean
	 * encapsulated in the object.
	 * 
	 * @return a <code>FlatData</code> object representing this boolean value
	 */
	public FlatData flatten() {
		String data;
		// We use simple name, because our package is de.nec.nle.siafu.data
		data = this.getClass().getSimpleName() + ":";
		data += b;
		return new FlatData(data);
	}

	public boolean equals(Object o) {
		if (!(o instanceof BooleanType)) {
			return false;
		} else {
			BooleanType bt = (BooleanType) o;
			return (bt.getValue().equals(b));
		}
	}

	public int hashCode() {
		return (this.getClass().getName() + b).hashCode();
	}
}
