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
 * This exception is thrown when pointing the agent to a valid position which
 * the agent can not reach, because there is no path that leads to it.
 * 
 * @author Miquel Martin
 * 
 */
public class PositionUnreachableException extends RuntimeException {
	/** Default serialVersionUID. */
	private static final long serialVersionUID = 1L;
}
