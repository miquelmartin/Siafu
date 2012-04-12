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

package de.nec.nle.siafu.graphics.markers;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;

import de.nec.nle.siafu.model.Trackable;

/**
 * A Marker is a graphical queue that is drawn on top of a Trackable in order
 * to highlight it in the simulator. The classes that extend Marker provide
 * different graphical styles.
 * 
 * @author Miquel Martin
 * 
 */
public abstract class Marker {

	/** The trackable on which the Marker is drawn. */
	protected Trackable t;

	/** The color of the marker. */
	protected RGB rgbColor;

	/**
	 * The draw method to draw the Marker instance.
	 * 
	 * @param gc the GC to use in order to draw the mark.
	 */
	public abstract void draw(GC gc);

	/**
	 * Get the trackable being marked.
	 * 
	 * @return the trackable being marked
	 */
	public Trackable getTrackable() {
		return t;
	}

	/**
	 * Get the RGB color of the marker.
	 * 
	 * @return the RGB for the marker color
	 */
	public abstract RGB getRGB();

	/**
	 * Set the RGB color for the marker.
	 * 
	 * @param rgb the chosen RGB
	 */
	public abstract void setRGB(RGB rgb);

	/**
	 * Dispose the swt OS resources allocated by the marker.
	 * 
	 */
	public abstract void disposeResources();

	/**
	 * Set the trackable on top of which this marker floats.
	 * 
	 * @param newT the trackable to mark
	 * @return the old trackable, or null if there wasn't one
	 */
	public abstract Trackable setTrackable(Trackable newT);

	/**
	 * Comparison of two markers.
	 * 
	 * @param o the Marker to compare to
	 * @return true if the marker is on top of the same Trackable, false
	 *         otherwise
	 */
	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof Marker)) {
			return false;
		}
		return ((Marker) o).getTrackable().equals(t);
	}

	/**
	 * Get the hashcode of the Marker, which corresponds to the HashCode of
	 * the trackable it marks.
	 * @return the hashcode
	 */
	@Override
	public int hashCode() {
		return t.hashCode();
	}
}
