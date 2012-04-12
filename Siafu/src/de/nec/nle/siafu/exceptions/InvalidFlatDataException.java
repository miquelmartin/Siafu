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

package de.nec.nle.siafu.exceptions;

/**
 * Thrown when parsing a flat data that does not conform to the appropriate
 * format for the class it represents.
 * 
 * @author Miquel Martin
 * 
 */
public class InvalidFlatDataException extends RuntimeException {

	/** Default serial version UID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Create the exception with an explanatory message.
	 * 
	 * @param data the invalid flat data.
	 */
	public InvalidFlatDataException(final String data) {
		super("The string \"" + data
				+ "\' is not a flattened piece of data");
	}

	/**
	 * Create the exception with an explanatory message.
	 * 
	 * @param data the provided data
	 * @param className the class that this flat data should belong to
	 * @param e the exception that caused this.
	 */
	public InvalidFlatDataException(final String data,
			final String className, final Exception e) {
		super("Can't instantiate \"" + className
				+ "\" with the flat data \"" + data + "\'.", e);
	}

}
