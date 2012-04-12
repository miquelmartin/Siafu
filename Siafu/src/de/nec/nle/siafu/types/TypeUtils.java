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
 * Collection of utilities to assist in flattening, checking and perform other
 * operations with the Siafu data types.
 * 
 * @author Miquel Martin
 * 
 */
public final class TypeUtils {

	/**
	 * Forbid the instantiation of this utility class.
	 */
	private TypeUtils() {
		// Do nothing.
	};

	/**
	 * Check that the type in the data text can be built by this class.
	 * 
	 * @param pub the <code>Publishable</code> that wants to instantiate the
	 *            data
	 * @param data the flattened data to verify
	 */
	public static void check(final Publishable pub, final String data) {
		String type = data.substring(0, data.indexOf(':'));
		String className;
		if (type.indexOf('.') == -1) {
			className = pub.getClass().getSimpleName();
		} else {
			className = pub.getClass().getName();
		}
		if (!type.equals(className)) {
			throw new RuntimeException("Can not build " + className
					+ " out of " + type);
		}
	}
}
