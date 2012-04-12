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

import java.util.Vector;

import de.nec.nle.siafu.exceptions.external.InvalidFlatDataException;

/**
 * Collection of utilities to assist in flattening, checking and perform other
 * operations with the Siafu data types.
 * 
 * @author Miquel Martin
 * 
 */
public final class TypeUtils {

	private static final String DEFAULT_PACKAGE = "de.nec.nle.siafu.types.";

	/**
	 * Forbid the instantiation of this utility class.
	 */
	private TypeUtils() {
		// Do nothing.
	};

	/**
	 * Check that the type in the data text can be built by this class.
	 * 
	 * @param pub
	 *            the <code>Publishable</code> that wants to instantiate the
	 *            data
	 * @param flatData
	 *            the flattened data to verify
	 * @throws InvalidFlatDataException
	 *             it the flatdata object does is not flatdata formatted, or the
	 *             pub object is not of the right class.
	 */
	public static void check(final Publishable pub, final String flatData)
			throws InvalidFlatDataException {

		String publishableClass = pub.getClass().getName();
		publishableClass = stripExternal(publishableClass);

		int typeDelimiter = flatData.indexOf(':');
		if (typeDelimiter == -1) {
			throw new InvalidFlatDataException(
					"The provided flatData String is not a flat data type.");
		}
		String flatDataClass = flatData.substring(0, flatData.indexOf(':'));
		// Add the default package if the type belongs to it (has no package)
		if (flatDataClass.indexOf('.') == -1) {
			flatDataClass = DEFAULT_PACKAGE + flatDataClass;
		}

		if (!publishableClass.equals(flatDataClass)) {
			throw new InvalidFlatDataException("Can not build "
					+ pub.getClass().getName() + " (which represents "
					+ publishableClass + ") using flatdata of type " + flatData);
		}
	}

	/**
	 * The skeleton classes in this library all conform to
	 * ORIGINALPACKAGE.external.CLASSNAME, where ORIGINALPACKAGE is the package
	 * to which this class would belong in the simulator, as opposed to the
	 * package in this external library. In order to compare the classes, we
	 * need to strip off "external" and this is precisally what this method
	 * does.
	 * 
	 * @param publishableClass
	 *            the name of the class, as external
	 * @return the name of the class if it were not external
	 */
	public static String stripExternal(String publishableClass) {
		int packageDelimiter = publishableClass.lastIndexOf('.');
		String packageName = publishableClass.substring(0, packageDelimiter);
		String className = publishableClass.substring(packageDelimiter + 1);

		// Strip "external"
		packageName = packageName.substring(0, packageName.lastIndexOf('.'));

		return packageName + "." + className;
	}

	/**
	 * This method is analogous to {@link #stripExternal(String)} but takes an
	 * Object as parameter. To do the conversion, the method takes the object's
	 * Class and calls getName on it.
	 * 
	 * @param obj the object whose class name we ant to get as if it weren't external 
	 * @return the name of the class if it were not external
	 */
	public static String stripExternal(Object obj) {
		return stripExternal(obj.getClass().getName());
	}

	/**
	 * Utility method to split Strings on a delimiter character. Empty strings
	 * are not returned. This is close to the String.split() method taht's
	 * available in J2SE. We replicate it here to avoid making this library
	 * non-CDC.
	 * 
	 * @param str
	 *            the string that has to be split
	 * @param delimiter
	 *            the delimiter character
	 * @return an array with the component Strings
	 */
	public static String[] split(String str, char delimiter) {
		if (str.indexOf(delimiter) == -1) {
			return new String[] { str };
		}

		Vector v = new Vector();
		String leftOver = str;
		int nextD = -2;

		while (nextD != -1) {
			nextD = leftOver.indexOf(delimiter);
			String item = leftOver;
			if (nextD != -1) {
				item = leftOver.substring(0, nextD);
			}
			if (!item.equals("")) {
				v.add(item);
			}
			leftOver = leftOver.substring(nextD + 1);

		}

		return (String[]) v.toArray(new String[0]);
	}
}
