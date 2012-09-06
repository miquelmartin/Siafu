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

package de.nec.nle.siafu.graphics;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.nec.nle.siafu.graphics.markers.Marker;
import de.nec.nle.siafu.model.Trackable;

/**
 * Instances of this class encapsulate the marks in the simulator.
 * <p>
 * A track mark is a visual cue that allows the GUI users to follow an agent,
 * or identify a place. To faciliate managing marks from external
 * applications, external tracks are kept separate from internal ones, so that
 * fiddling with the GUI won't alter what your application is trying to
 * display, and vice-versa.
 * 
 * @author Miquel Martin
 * 
 */
public class Markers {

	/**
	 * Check if a given <code>Trackable</code> is being tracked in the GUI
	 * at the moment.
	 * 
	 * @param trackable the Trackable we want to check
	 * @param type the type of Track, which can be any of {@link Type}
	 * @return true if the object is being tracked for that track type, false
	 *         otherwise
	 */
	public synchronized boolean isTracked(final Trackable trackable,
			final Type type) {
		return type.contains(trackable);
	}

	/**
	 * This method is equal to isTracked(Trackable,Type), but does not
	 * discriminate types. If the trackable has any type of mark, true is
	 * returned.
	 * 
	 * @param trackable the trackable to check
	 * @return true if the trackable has any type of mark
	 */
	public synchronized boolean isTracked(final Trackable trackable) {
		for (Type type : Type.values()) {
			if (type.contains(trackable)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Tracks a <code>Trackable</code> in the GUI using a Marker of the
	 * class provided in the marker parameter. The marker is stored in the
	 * Type list of markers. Only one marker per Type is allowed, which
	 * essentially means that each marking source can only keep one tag at a
	 * time on the Trackable.
	 * 
	 * @param mark the Marker object to draw
	 * @param type the source of the track
	 */
	public synchronized void addMarker(final Marker mark, final Type type) {
		type.addMarker(mark);
	}

	/**
	 * Clear all the tracks of a given type.
	 * 
	 * @param type the type of tracks to be removed
	 */
	public synchronized void removeAllMarkers(final Type type) {
		type.clear();
	}

	/**
	 * Clear all marks of all types.
	 * 
	 */
	public synchronized void removeAllMarkers() {
		for (Type type : Type.values()) {
			type.clear();
		}
	}

	/** Dispose of all the resources allocated by SWT. */
	public synchronized void disposeResources() {
		removeAllMarkers();
	}

	/**
	 * Remove a track mark for a specific track type and trackable.
	 * 
	 * @param trackable the Trackable we don't want to mark anymore
	 * @param type the type of track to remove
	 */
	public synchronized void removeMarker(final Trackable trackable,
			final Type type) {
		type.removeMarker(trackable);
	}

	/**
	 * Remove all marks from a trackable.
	 * 
	 * @param trackable the Trackable we don't want to mark anymore
	 */
	public synchronized void removeMarker(final Trackable trackable) {
		for (Type type : Type.values()) {
			type.removeMarker(trackable);
		}
	}

	/**
	 * Get the marker of a given type, for a given trackable.
	 * 
	 * @param t the trackable
	 * @param type the type
	 * @return the associated marker
	 */
	public Marker getMarker(final Trackable t, final Type type) {
		return type.getMarker(t);
	}

	/**
	 * Get the markers of a certain type. Note that there is no guarantee that
	 * the map won't change while you are reading. Synchronization is up to
	 * the developer.
	 * 
	 * By type, we refer to the category of markers, not the actual look of
	 * the marker.
	 * 
	 * @param type an instance of Type
	 * @return a map with the external tracks
	 * @see Type
	 */
	public Collection<Marker> geMarks(final Type type) {
		return type.getMarkers();
	}

	/**
	 * This class represents the types of tracks available in the simulator.
	 * The reason for difernetiation is their origin: INTERNAL_TRACK has been
	 * set using the GUI, while EXTERNAL_TRACK is set using the external
	 * command interface.
	 */
	public enum Type {
		/**
		 * Denotes tracks set through the GUI.
		 */
		INTERNAL,
		/**
		 * Denotes tracks set through the external command interface.
		 */
		EXTERNAL,
		/**
		 * Denotes the trackables which are currently active in the simulator.
		 */
		ACTIVE;

		/**
		 * A map with all the markers of this type, and the agent they belong
		 * to.
		 */
		private Map<Trackable, Marker> markers =
				new HashMap<Trackable, Marker>();

		/**
		 * Add a marker of this type.
		 * 
		 * @param m the marker to add
		 */
		protected void addMarker(final Marker m) {
			markers.put(m.getTrackable(), m);
		}

		/**
		 * Remove a marker of this type.
		 * 
		 * @param t the trackable whose marker we want removed
		 */
		protected void removeMarker(final Trackable t) {
			if (contains(t)) {
				markers.get(t).disposeResources();
				markers.remove(t);
			}
		}

		/**
		 * Dispose the SWT resources allocated by the container for the
		 * markers of this type.
		 */
		protected void disposeResources() {
			clear();
		}

		/** Remove all the markers of this type. */
		protected void clear() {
			for (Marker m : markers.values()) {
				m.disposeResources();
			}
			markers.clear();
		}

		/**
		 * Find out if this type of markers has one on the given trackable t.
		 * 
		 * @param t the trackable to check
		 * @return true if the trackable is marked in this type of markers.
		 */
		protected boolean contains(final Trackable t) {
			return markers.containsKey(t);
		}

		/**
		 * Get the marker of this type associated to the trackable t.
		 * 
		 * @param t the trackable to get the marker for
		 * @return the marker
		 */
		protected Marker getMarker(final Trackable t) {
			return markers.get(t);
		}

		/**
		 * Get all the markers of this type.
		 * 
		 * @return a Collection with the markers.
		 */
		protected Collection<Marker> getMarkers() {
			return markers.values();
		}
	}
}
