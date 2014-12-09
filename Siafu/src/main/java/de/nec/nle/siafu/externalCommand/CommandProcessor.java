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

package de.nec.nle.siafu.externalCommand;

import java.util.ArrayList;
import java.util.Collection;

import de.nec.nle.siafu.control.Controller;
import de.nec.nle.siafu.exceptions.NothingNearException;
import de.nec.nle.siafu.exceptions.AgentNotFoundException;
import de.nec.nle.siafu.exceptions.PlaceNotFoundException;
import de.nec.nle.siafu.exceptions.TrackableNotFoundException;
import de.nec.nle.siafu.exceptions.UnknownContextException;
import de.nec.nle.siafu.graphics.Markers;
import de.nec.nle.siafu.graphics.markers.Marker;
import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.Place;
import de.nec.nle.siafu.model.Position;
import de.nec.nle.siafu.model.Trackable;
import de.nec.nle.siafu.model.World;
import de.nec.nle.siafu.types.FlatData;

/**
 * This class implements the actual actions before the external commands.
 * Methods in this class are meant to be called from the command listener.
 * 
 * @author Miquel Martin
 * 
 */
public class CommandProcessor {
	/** Siafu's controller. */
	private Controller control;

	/**
	 * Create a command processor.
	 * 
	 * @param control
	 *            Siafu's controller
	 */
	public CommandProcessor(final Controller control) {
		this.control = control;
	}

	/**
	 * Put a marker on an trackable.
	 * 
	 * @param mark
	 *            the mark to put
	 */
	public void mark(final Marker mark) {
		// System.out.println("Marking "+p.getName()+" with style
		// "+style+".");
		control.getGUI().getMarkers().addMarker(mark, Markers.Type.EXTERNAL);
	}

	/**
	 * Remove the external markers from the trackable.
	 * 
	 * @param t
	 *            the trackable to unmark
	 */
	public void unMark(final Trackable t) {
		// System.out.println("Removing mark from "+p.getName()+".");
		control.getGUI().getMarkers().removeMarker(t, Markers.Type.EXTERNAL);
	}

	/**
	 * Remove all the markers from all the trackables.
	 */
	public void unMarkAll() {
		control.getGUI().getMarkers().removeAllMarkers(Markers.Type.EXTERNAL);
	}

	/**
	 * Move the provided agent to the temporary place tempPlace. You are
	 * encouraged to use temporary places to have faster calculation (the
	 * gradient is partial) and to avoid persisting too much to the hard drive.
	 * 
	 * @param a
	 *            the agent to move
	 * @param tempPlace
	 *            the place to move it to
	 */
	public void move(final Agent a, final Place tempPlace) {
		// System.out.println("Moving "+p.getName()+" to
		// "+tempPlace.getPos().getPrettyLatitude()+"
		// "+tempPlace.getPos().getPrettyLongitude()+".");
		a.setDestination(tempPlace);
	}

	/**
	 * Allow an agent to follor the AgentModel's commands or not.
	 * 
	 * @param a
	 *            the agent to control
	 * @param autoSetting
	 *            true if we want the agent to move according to the behavior
	 *            model, false for it to stand still and await GUI or
	 *            CommandListener commands
	 */
	public void auto(final Agent a, final boolean autoSetting) {
		// System.out.println("Putting "+p.getName()+" on auto
		// mode="+autoSetting+".");
		if (autoSetting) {
			a.returnControl();
		} else {
			a.getControl();
		}
	}

	/**
	 * Make all agents move on auto, that is, following the behavior model.
	 * 
	 * @param autoSetting
	 *            true to make all agents move on auto, false to make them all
	 *            stand still and be controlled by the gui only.
	 */
	public void autoAll(final boolean autoSetting) {
		// System.out.println("Putting "+p.getName()+" on auto
		// mode="+autoSetting+".");
		if (autoSetting) {
			for (Agent a : control.getWorld().getPeople()) {
				a.returnControl();
			}
		} else {
			for (Agent a : control.getWorld().getPeople()) {
				a.getControl();
			}
		}
	}

	/**
	 * Change the agent's image to that given by image.
	 * 
	 * @param a
	 *            the agent to modify
	 * @param image
	 *            the name of the sprite to assign to the agent
	 */
	public void image(final Agent a, final String image) {
		// System.out.println("Making "+p.getName()+" look like: "+image+".");
		a.setImage(image);
	}

	/**
	 * Revert the agent's image to the one it had before, effectively rolling
	 * back one image change.
	 * 
	 * @param a
	 *            the agent whose image we want to revert
	 */
	public void resetImage(final Agent a) {
		a.setPreviousImage();
	}

	/**
	 * Get an agent's context.
	 * 
	 * @param trackableName
	 *            an array with the trakables whose context we want to retrieve
	 * @param context
	 *            an array with the context variable names to retrieve
	 * @return a string of the type "agent1/ctxValue1 agent1/ctxValue2
	 *         agent2/ctxValue1..."
	 * @throws UnknownContextException
	 *             if the context variable is unknown
	 * @throws TrackableNotFoundException
	 *             if the agent or place is unknown
	 */
	public String getContext(final String[] trackableName,
			final String[] context) throws UnknownContextException,
			TrackableNotFoundException {
		String reply = new String();
		Collection<Trackable> trackables;

		trackables = new ArrayList<Trackable>();

		for (int i = 0; i < trackableName.length; i++) {
			try {
				trackables.add(control.getWorld().getPersonByName(
						trackableName[i]));
			} catch (AgentNotFoundException e) {
				try {
					trackables.add(control.getWorld().getPlaceByName(
							trackableName[i]));
				} catch (PlaceNotFoundException e2) {
					throw new TrackableNotFoundException("Trackable \""
							+ trackableName[i] + "\" not found");
				}
			}
		}

		for (Trackable t : trackables) {
			for (int j = 0; j < context.length; j++) {
				reply += (t.getName() + "/" + t.getContext(context[j]) + " ");
			}
		}

		return reply;
	}

	/**
	 * Set an agent's context.
	 * 
	 * @param trackableName
	 *            the agent whose context we want to set
	 * @param variable
	 *            the variable to set
	 * @param value
	 *            the new value for that variable
	 * @throws TrackableNotFoundException
	 *             if the trackable doesn't exist
	 */
	public void setContext(final String trackableName, final String variable,
			final String value) throws TrackableNotFoundException {
		Trackable t;
		try {
			t = control.getWorld().getPersonByName(trackableName);
		} catch (AgentNotFoundException e) {
			try {
				t = control.getWorld().getPlaceByName(trackableName);
			} catch (PlaceNotFoundException e2) {
				throw new TrackableNotFoundException("Uknown trackable: \""
						+ trackableName + "\".");
			}
		}
		FlatData fd = new FlatData(value);
		t.set(variable, fd.rebuild());
	}

	/**
	 * Find agents near the position pos, up to a distance dist.
	 * 
	 * @param pos
	 *            the base position
	 * @param dist
	 *            the distance in simulation grid points
	 * @return the list of space separated agents near that position
	 * @throws NothingNearException
	 *             if nothing is found
	 */
	public String findAgentsNear(final Position pos, final int dist)
			throws NothingNearException {
		World world = control.getWorld();
		String reply = new String();

		for (Trackable a : world.findAllAgentsNear(pos, dist, false)) {
			reply += a.getName() + " ";
		}
		return reply;
	}

	/**
	 * Find places near the position pos, up to a distance dist.
	 * 
	 * @param pos
	 *            the base position
	 * @param dist
	 *            the distance in simulation grid points
	 * @return the list of space separated places near that position
	 * @throws NothingNearException
	 *             if nothing is found
	 */
	public String findPlacesNear(final Position pos, final int dist)
			throws NothingNearException {
		World world = control.getWorld();
		String reply = new String();
		for (Trackable a : world.findAllPlacesNear(pos, dist, false)) {
			reply += a.getName() + " ";
		}
		return reply;
	}

	/**
	 * Make all agents visible.
	 */
	public void unhideAll() {
		for (Agent a : control.getWorld().getPeople()) {
			a.setVisible(true);
		}
	}

	/**
	 * Make the given agent visible.
	 * 
	 * @param a
	 *            the agent to unhide
	 */
	public void unhide(final Agent a) {
		a.setVisible(true);
	}

	/**
	 * Make all agents invisible.
	 */
	public void hideAll() {
		for (Agent a : control.getWorld().getPeople()) {
			a.setVisible(false);
		}
	}

	/**
	 * Hide a given agent.
	 * 
	 * @param a
	 *            the agent to make invisible
	 */
	public void hide(final Agent a) {
		a.setVisible(false);
	}

	/**
	 * Get the simulation time.
	 * 
	 * @return the simulation time in Unix time
	 */
	public String time() {
		return Long
				.toString(control.getWorld().getTime().getTimeInMillis() / 1000);
	}
}
