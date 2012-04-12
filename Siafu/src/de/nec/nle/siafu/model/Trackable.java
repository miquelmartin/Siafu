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

package de.nec.nle.siafu.model;

import de.nec.nle.siafu.exceptions.UnknownContextException;
import de.nec.nle.siafu.types.FlatData;
import de.nec.nle.siafu.types.Publishable;

/**
 * An interface for objects that can be tracked on the simulation, that is,
 * objects that have a position and can be marked with a Marker.
 * 
 * @author Miquel Martin
 * 
 */
public interface Trackable {
	/**
	 * Get the position of the trackable.
	 * 
	 * @return the position
	 */
	Position getPos();

	/**
	 * Get the name of the trackable.
	 * 
	 * @return the name
	 */
	String getName();

	/**
	 * Check if the trackable is visible.
	 * 
	 * @return true if it is visible
	 */
	boolean isVisible();

	/**
	 * Set the visibility of the trackable.
	 * 
	 * @param visible true to make it visible, false to make it disappear.
	 */
	void setVisible(boolean visible);

	/**
	 * Retrieve context information from the trackable.
	 * 
	 * @param ctxName the context to be retrieved
	 * @return the flattened data value for the context variable
	 * @throws UnknownContextException if the context variable is unknown for
	 *             this trackable
	 */
	FlatData getContext(final String ctxName)
			throws UnknownContextException;
	
	/**
	 * Set an info field in the trackable to the given value. 
	 * @param key the key to set
	 * @param value the value for the key
	 * @return the old value, if the key already existed, or null otherwise
	 */
	Publishable set(final String key, final Publishable value);
}
