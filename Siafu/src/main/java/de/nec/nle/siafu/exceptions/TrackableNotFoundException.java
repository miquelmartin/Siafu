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
 * Exception thrown when an agent or place (i.e. any trackable) that is being
 * searched by name, can not be found.
 * 
 * @author Miquel Martin
 * 
 */
public class TrackableNotFoundException extends Exception {
	/** Generated serial version uid. */
	private static final long serialVersionUID = 1166520472902419380L;

	/**
	 * Throw the exception plus the name of the trackable that has not been
	 * found.
	 * 
	 * @param name the agent that was being searched for
	 */
	public TrackableNotFoundException(final String name) {
		super("Person or place not found: \"" + name + "\".");
	}
}
