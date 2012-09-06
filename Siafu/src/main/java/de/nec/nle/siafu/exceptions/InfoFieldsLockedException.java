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
 * Exception thrown when trying to add new keys to the info field of an agent.
 * New keys are only allowed during the createAgents method.
 * 
 * @author Miquel Martin
 * 
 */
public class InfoFieldsLockedException extends RuntimeException {
	/** Default serial version UID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Create an exception and include a message on the field that is not
	 * allowed anymore.
	 * 
	 * @param field the offending field
	 */
	public InfoFieldsLockedException(final String field) {
		super("Can't add new field: \"" + field + "\".");
	}
}
