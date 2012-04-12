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

import java.lang.reflect.Constructor;

import de.nec.nle.siafu.exceptions.external.InvalidFlatDataException;

/**
 * This utility class provides the rebuild() method to create an external
 * Publishable object out of a FlatData. In other words: with this class you can
 * take Siafu output and turn it into one of the objects that represent it. The
 * method has the same function as the String (flatdata) constructor of the
 * other classes on this package but is useful if you don't know what type you
 * are receiving: instead of string parsing, you can use instanceof to determine
 * and later cast the precise Publishable implementation you have received.
 * 
 * @author Miquel Martin
 * 
 */
public class RebuildHelper {

	/**
	 * We don't want anyone instantiating a utility class
	 */
	private RebuildHelper() {
		// Do nothing; (Prevent instantiation)
	}

	/**
	 * The package to append to the class name in the flattened data, if none is
	 * provided.
	 */
	public static final String DEFAULT_PACKAGE = "de.nec.nle.siafu.types";

	/**
	 * Rebuilds a flatdata String into a Publishable object. The precise
	 * implementation of Publishable will depend on the flatdata.
	 * 
	 * @return the Publishable represented by the flattened data.
	 * @throws InvalidFlatDataException
	 *             if the flat data can not be parsed, or the class it
	 *             represents is not available, has no appropriate constructor,
	 *             or throws any other Exception
	 */
	public static Publishable rebuild(String flatData)
			throws InvalidFlatDataException {
		String packageName;
		String className;
		Class classObj;
		Constructor constructorObj;

		try {
			className = flatData.substring(0, flatData.indexOf(':'));
		} catch (IndexOutOfBoundsException e) {
			throw new InvalidFlatDataException(flatData);
		}

		if (className.indexOf('.') == -1) {
			// No package name specified
			packageName = DEFAULT_PACKAGE;
		} else {
			packageName = className.substring(0, className.lastIndexOf('.'));
			className = className.substring(className.lastIndexOf('.') + 1);
		}

		// Adding the external sufix to instantiate an skeleton class instead
		// of an original Siafu one
		packageName += ".external";

		try {
			classObj = Class.forName(packageName + "." + className);
		} catch (ClassNotFoundException e) {
			throw new InvalidFlatDataException(flatData, packageName + "."
					+ className, e);
		}

		try {
			constructorObj = classObj
					.getConstructor(new Class[] { String.class });
		} catch (SecurityException e) {
			throw new RuntimeException("Error getting the constructor of "
					+ packageName + "." + className, e);
		} catch (NoSuchMethodException e) {
			throw new InvalidFlatDataException(flatData, packageName + "."
					+ className, e);
		}

		try {
			return (Publishable) constructorObj
					.newInstance(new Object[] { flatData });
		} catch (Exception e) {
			throw new InvalidFlatDataException(flatData, className, e);
		}
	}

}
