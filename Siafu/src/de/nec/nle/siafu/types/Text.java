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
 * This <code>Publishable</code> encapsulates a <code>String</code> and
 * makes it serializable (it can be flattened and blown up again) for use with
 * Siafu.
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
	 * Build a <code>Text</code> object that contains <code>text</code>.
	 * 
	 * @param text the text content
	 */
	public Text(final String text) {
		this.text = text;
	}

	/**
	 * Build a Text object by taking the string that follows the ":" delimiter
	 * of the flattened data object.
	 * 
	 * @param flatData the object to base this <code>Text</code> on
	 */
	public Text(final FlatData flatData) {
		String data = flatData.getData();
		TypeUtils.check(this, data);
		this.text = data.substring(data.indexOf(':') + 1);
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
	 * @return a <code>FlatData</code> object representing this Text object
	 */
	public FlatData flatten() {
		String data;
		// We use simple name, because our package is de.nec.nle.siafu.data
		data = this.getClass().getSimpleName() + ":";
		data += text;
		return new FlatData(data);
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
