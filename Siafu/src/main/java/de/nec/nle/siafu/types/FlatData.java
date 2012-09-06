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

import java.lang.reflect.Constructor;

import de.nec.nle.siafu.exceptions.InvalidFlatDataException;

/**
 * An instance of <code>FlatData</code> represents a
 * <code>Publishable</code> object in flattenned form. In essence, flattened
 * data is a human readable equivalent of java standard serialized data.
 * <p>
 * In the typical case, a <code>FlatData</code> object is created from
 * flattened data coming, for instance, from persisted storage, a text file,
 * etc... You can then "blow" the data back into a full fledged
 * <code>Publishable</code> using the <code>rebuild()</code> method.
 * <p>
 * The standard way Siafu flattens data is by using the form
 * <code>ClassName:field1#field2#...#fieldN</code> but developers are free
 * to choose any form, provided that it becomes a String, and it begins with
 * <code>ClassName:</code>, so that the factory can identify the right type
 * through reflection.
 * <p>
 * Note that ClassName is either a fully qualified class name (including the
 * package) or simply a Class name, in which case the package is assumed to be
 * <code>de.nec.nle.siafu.types</code>.
 * 
 * @author Miquel Martin
 * 
 */
public class FlatData {
	/**
	 * The package to append to the class name in the flattened data, if none
	 * is provided.
	 */
	public static final String DEFAULT_PACKAGE = "de.nec.nle.siafu.types";

	/**
	 * The flattened data encapsulated by this object.
	 */
	private String data;

	/**
	 * Build an instance of <code>FlatData</code> from a <code>String</code>
	 * of flattened data.
	 * 
	 * @param data the <code>String</code> containing the serialized data
	 */
	public FlatData(final String data) {
		this.data = data;
	}

	/**
	 * Get the flattened data encapsulated by this object.
	 * 
	 * @return the <code>String</code> of flattened data.
	 */
	public String getData() {
		return data;
	}

	/**
	 * Blows up the data back into a <code>Publishable</code> object from
	 * its flattened state. If the class name does not specify a package,
	 * <code>de.nec.nle.siafu.types</code> is assumed.
	 * 
	 * @return the object represented by the flattened data.
	 * @throws InvalidFlatDataException if the flat data can not be parsed, or
	 *             the class it represents is not available, has no
	 *             appropriate constructor, or throws any other Exception
	 */
	public Publishable rebuild() {
		String packageName;
		String className;
		Class<? extends Publishable> classObj;
		Constructor<? extends Publishable> constructorObj;

		try {
			className = data.substring(0, data.indexOf(':'));
		} catch (IndexOutOfBoundsException e) {
			throw new InvalidFlatDataException(data);
		}

		if (className.indexOf('.') == -1) {
			// No package name specified
			packageName = DEFAULT_PACKAGE;
		} else {
			packageName = className.substring(0, className.lastIndexOf('.'));
			className = className.substring(className.lastIndexOf('.') + 1);
		}

		try {
			classObj = Class.forName(packageName + "." + className).asSubclass(Publishable.class);
		} catch (ClassNotFoundException e) {
			throw new InvalidFlatDataException(data, packageName + "."
					+ className, e);
		}

		try {
			constructorObj =
					classObj.getConstructor(new Class[] {FlatData.class});
		} catch (SecurityException e) {
			throw new RuntimeException("Error getting the constructor of "
					+ packageName + "." + className, e);
		} catch (NoSuchMethodException e) {
			throw new InvalidFlatDataException(data, packageName + "."
					+ className, e);
		}

		try {
			return (Publishable) constructorObj
					.newInstance(new Object[] {this});
		} catch (Exception e) {
			throw new InvalidFlatDataException(data, className, e);
		}
	}

	/**
	 * Get a human readable (and rebuildable) representation of this flattened
	 * data.
	 * 
	 * @return the data string
	 */
	public String toString() {
		return data;
	}
}
