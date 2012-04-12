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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
 * @see de.nec.nle.siafu.types.TextList
 * 
 * @author Miquel Martin
 * 
 */
public class TextList implements Publishable {
	/**
	 * The list of strings.
	 */
	private List l;

	/**
	 * Build a TextList object by taking the string that follows the ":"
	 * delimiter of the flattened data object and splitting it into substrings
	 * at the "#" character.
	 * 
	 * @param flatData
	 *            the object to base this <code>TextList</code> on
	 * @throws InvalidFlatDataException
	 *             if the provided flatdata is not properly formatted, or is not
	 *             a flattened form of this class.
	 */
	public TextList(final String flatData) throws InvalidFlatDataException {
		TypeUtils.check(this, flatData);
		String entries = flatData.substring(flatData.indexOf(':') + 1);
		l = Arrays.asList(TypeUtils.split(entries, '#'));
	}

	/**
	 * Builds a <code>TextList</code> object using the supplied array of
	 * Strings.
	 * 
	 * @param sArray
	 *            the array of Strings
	 */
	public TextList(final String[] sArray) {
		this.l = new ArrayList(Arrays.asList(sArray));
	}

	/**
	 * Builds a <code>TextList</code> object using the supplied list.
	 * 
	 * @param s
	 *            the list of Strings to build the object
	 */
	public TextList(final List s) {
		this.l = s;
	}

	/**
	 * Returns a comma separated list of the strings in the list.
	 * 
	 * @return a string of the type "element1,element2,...,elementN"
	 */
	public String toString() {
		String s = new String();
		Iterator lIt = l.iterator();
		while (lIt.hasNext()) {
			s += lIt.next() + ",";
		}
		s = s.substring(0, s.lastIndexOf(','));
		return s;
	}

	/**
	 * Get the list of Strings contained in this object.
	 * 
	 * @return the <code>List</code> object with the strings
	 */
	public List getList() {
		return l;
	}

	/**
	 * Generates a flattened version of the data, of the type
	 * <code>TextList:string1#string2#...</code> where each string is each of
	 * the strings contained in the list.
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

		Iterator lIt = l.iterator();
		while (lIt.hasNext()) {
			data += lIt.next() + "#";
		}
		data = data.substring(0, data.lastIndexOf('#'));
		return data;
	}

	public boolean equals(Object o) {
		if (!(o instanceof TextList)) {
			return false;
		} else {
			TextList tl = (TextList) o;
			return (tl.getList().equals(l));
		}
	}

	public int hashCode() {
		return (this.getClass().getName() + l.toString()).hashCode();
	}
}
