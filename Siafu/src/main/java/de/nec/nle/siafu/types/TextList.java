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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A list of Strings. This class implements Publishable, which allows the list
 * to be serialized into a human readable format.
 * 
 * @author Miquel Martin
 * 
 */
public class TextList implements Publishable {
	/**
	 * The list of strings.
	 */
	private List<String> l;

	/**
	 * Builds a <code>TextList</code> object using the supplied array of
	 * Strings.
	 * 
	 * @param sArray the array of Strings
	 */
	public TextList(final String[] sArray) {
		this.l = new ArrayList<String>(Arrays.asList(sArray));
	}

	/**
	 * Builds a <code>TextList</code> object using the supplied list.
	 * 
	 * @param s the list of Strings to build the object
	 */
	public TextList(final List<String> s) {
		this.l = s;
	}

	/**
	 * Returns a comma separated list of the strings in the list.
	 * 
	 * @return a string of the type "element1,element2,...,elementN"
	 */
	public String toString() {
		if(l.isEmpty()){
			return "";
		}
		
		String s = new String();
		for (String li : l) {
			s += li + ",";
		}
		s = s.substring(0, s.lastIndexOf(','));
		return s;
	}

	/**
	 * Get the list of Strings contained in this object.
	 * 
	 * @return the <code>List</code> object with the strings
	 */
	public List<String> getList() {
		return l;
	}

	/**
	 * Generates a flattened version of the data, of the type
	 * <code>TextList:string1#string2#...</code> where each string is each of
	 * the strings contained in the list.
	 * 
	 * @return a <code>FlatData</code> object representing this TextList
	 *         object
	 */
	public FlatData flatten() {
		String data;
		// We use simple name, because our package is de.nec.nle.siafu.data
		data = this.getClass().getSimpleName() + ":";
		for (String s : l) {
			data += s + "#";
		}
		data = data.substring(0, data.lastIndexOf('#'));
		return new FlatData(data);
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
