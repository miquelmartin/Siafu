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
 * @see de.nec.nle.siafu.types.Text
 * 
 * @author Miquel Martin
 * 
 */
public class Text implements Publishable {
	/**
	 * The text encapsulated in the <code>Text</code> object.
	 */
	private String text;

	/**
	 * Build a Text object by taking the string that follows the ":" delimiter
	 * of the flattened data object.
	 * 
	 * @param flatData
	 *            the object to base this <code>Text</code> on
	 * @throws InvalidFlatDataException
	 *             if the provided flatdata is not properly formatted, or is not
	 *             a flattened form of this class.
	 */
	public Text(final String flatData) throws InvalidFlatDataException {
		TypeUtils.check(this, flatData);
		this.text = flatData.substring(flatData.indexOf(':') + 1);
	}

	/**
	 * Returns the text string encapsulated in this instance.
	 * 
	 * @return the text string in the <code>Text</code> object
	 */
	public String toString() {
		return text;
	}

	/**
	 * Returns the text string encapsulated in this instance.
	 * 
	 * @return the text string in the <code>Text</code> object
	 */
	public String getText() {
		return text;
	}

	/**
	 * Generates a flattened version of the data, of the type
	 * <code>Text:string</code> where content is the string encapsulated in
	 * the <code>Text</code> object.
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
		data += text;
		return data;
	}

	public boolean equals(Object o) {
		if (!(o instanceof Text)) {
			return false;
		} else {
			Text t = (Text) o;
			return (t.getText().equals(text));
		}
	}

	public int hashCode() {
		return (this.getClass().getName() + text).hashCode();
	}

}
