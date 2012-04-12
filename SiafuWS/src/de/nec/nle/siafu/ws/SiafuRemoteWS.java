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

package de.nec.nle.siafu.ws;

import static de.nec.nle.siafu.externalCommand.CommandNames.*;
import java.rmi.RemoteException;

/**
 * The Siafu Remote Web Service enables the control of the simulation through
 * a web service interface. The user can highlight agents, change their image,
 * move thenm, etc...
 * 
 * @author Miquel Martin
 * 
 */
public class SiafuRemoteWS extends SiafuWebServicesCommon {

	/**
	 * Places a mark on top of an agent or place. Currently supported styles
	 * are: NamedBlue, NamedPink, NamedYellow, NamedRed, CircleBlue,
	 * CirclePink, CircleYellow, CircleRed. Named marks set a callout with the
	 * agent name on top; Circle marks place a glowing
	 * cross-hair-sort-of-circle on top of the agent.
	 * 
	 * @param name the name of the agent to mark
	 * @param style the mark style to use
	 * @throws RemoteException if the command fails
	 */
	public void setMark(final String name, final String style)
			throws RemoteException {
		setMarks(new String[] {name}, new String[] {style});
	}

	/**
	 * Places a mark on top of a number of agents or places. Currently
	 * supported styles are: NamedBlue, NamedPink, NamedYellow, NamedRed,
	 * CircleBlue, CirclePink, CircleYellow, CircleRed. Named marks set a
	 * callout with the agent name on top; Circle marks place a glowing
	 * cross-hair-sort-of-circle on top of the agent.
	 * 
	 * @param name an array containing the names of the agents to mark
	 * @param style an array with the mark style for each of the agents in the
	 *            name array
	 * @throws RemoteException if the command fails
	 */
	public void setMarks(final String[] name, final String[] style)
			throws RemoteException {
		if (name.length != style.length) {
			throw new RemoteException("Per name, I need a style");
		}
		for (int i = 0; i < name.length; i++) {
			doCommand(MARK + " " + name[i] + " " + style[i]);
		}
	}

	/**
	 * Remove the mark on an agent. If no mark is set, the command is ignored.
	 * The reserved word "all" stands for all of the agents in the simulation.
	 * 
	 * @param name the agent to un-mark
	 * @throws RemoteException if the command fails
	 */
	public void removeMark(final String name) throws RemoteException {
		removeMarks(new String[] {name});
	}

	/**
	 * Remove the marks on a number of agents. If no mark is set for one or
	 * any of the agents, the removal command for that agent is ignored. The
	 * reserved word "all" stands for all of the agents in the simulation.
	 * 
	 * @param name an array with the names of the agents whose mark must be
	 *            cleared
	 * @throws RemoteException if the command fails
	 */
	public void removeMarks(final String[] name) throws RemoteException {
		for (int i = 0; i < name.length; i++) {
			doCommand(UNMARK + " " + name[i]);
		}
	}

	/**
	 * Remove all of the marks in the simulator. Note that GUI set marks will
	 * not be affected.
	 * 
	 * @throws RemoteException if the command fails
	 */
	public void removeAllMarks() throws RemoteException {
		doCommand(UNMARK + " all");
	}

	/**
	 * Move an agent to a new position. If a position is not reachable, an
	 * error will be returned, and the agent will not move.
	 * 
	 * @param name the name of the agent to move
	 * @param latitude destination latitude
	 * @param longitude destination longitude
	 * @throws RemoteException if the command fails
	 */
	public void move(final String name, final double latitude,
			final double longitude) throws RemoteException {
		moveMultiple(new String[] {name}, new double[] {latitude},
			new double[] {longitude});
	}

	/**
	 * Move a number of agents to new positions. If a position is not
	 * reachable, an error will be returned, and the agent will not move.
	 * 
	 * @param name the name of the agents to move, packed in an array
	 * @param latitude an array of latitudes, matching the intended
	 *            destination for each name in the name parameter
	 * @param longitude an array of longitudes, matching the intended
	 *            destination for each name in the name parameter
	 * @throws RemoteException if the command fails
	 */
	public void moveMultiple(final String[] name, final double[] latitude,
			final double[] longitude) throws RemoteException {
		if (name.length != latitude.length || name.length != longitude.length) {
			throw new RemoteException(
					"Your batch must contain 3 elements: name, lat and long");
		}
		for (int i = 0; i < name.length; i++) {
			doCommand(MARK + " " + name[i] + " " + latitude[i] + " "
					+ longitude[i]);
		}
	}

	/**
	 * Sets the agent on auto mode (controlled by the agent model) or makes it
	 * ignore anything but direct commands through this interface or the GUI.
	 * The reserved word "all" stands for all of the agents in the simulation.
	 * 
	 * All agents are on auto mode by default.
	 * 
	 * @param name the agent to get in or out of auto mode
	 * @param setting true to set the agent on auto, false otherwise
	 * @throws RemoteException if the command fails
	 */
	public void autoMode(final String name, final boolean setting)
			throws RemoteException {
		autoModeMultiple(new String[] {name}, new boolean[] {setting});
	}

	/**
	 * Gets a number of agents into or out of auto mode. The reserved word
	 * "all" stands for all of the agents in the simulation.
	 * 
	 * @param name an array with the agent names
	 * @param setting an array with the auto setting of each of the agents
	 * @throws RemoteException if the command fails
	 */
	public void autoModeMultiple(final String[] name, final boolean[] setting)
			throws RemoteException {
		if (name.length != setting.length) {
			throw new RemoteException(
					"Your batch array must contain 2 elements: "
							+ "name, and true/false");
		}
		for (int i = 0; i < name.length; i++) {
			doCommand(AUTO + " " + name[i] + " " + setting[i]);
		}
	}

	/**
	 * Sets the image that represents an agent in the simulator GUI.
	 * 
	 * @param name the name of the agent to set
	 * @param image the name of the sprite, provided in the simulation data
	 *            (e.g. HumanBlue, CarGreen)
	 * @throws RemoteException if the command fails
	 */
	public void setAgentImage(final String name, final String image)
			throws RemoteException {
		setMultipleAgentImages(new String[] {name}, new String[] {image});
	}

	/**
	 * Sets the images that represent a number of agents in the simulator GUI.
	 * 
	 * @param name an array with the names of those agents whose image we want
	 *            to set
	 * @param image an array with the sprites which are to be used for each
	 *            agent in the name aray
	 * @throws RemoteException if the command fails.
	 */
	public void setMultipleAgentImages(final String[] name,
			final String[] image) throws RemoteException {
		if (name.length != image.length) {
			throw new RemoteException(
					"Per user, I need 2 elements: name and appearance");
		}
		for (int i = 0; i < name.length; i++) {
			doCommand(IMAGE + " " + name[i] + " " + image[i]);
		}
	}

	/**
	 * Reverts the image of an agent to that which he had before. This is
	 * particularly useful if to make a user temporary turn something else,
	 * and then get him back to "normal".
	 * 
	 * @param name the name of the user whose image is to be reverted
	 * @throws RemoteException if the command fails
	 */
	public void setPreviousImage(final String name) throws RemoteException {
		setMultiplePreviousImages(new String[] {name});
	}

	/**
	 * Reverts the image of a number of agents to the one they had before.
	 * This is particularly useful if to make a user temporary turn into
	 * something else, and then get him back to "normal".
	 * 
	 * @param name the array of names of the agents whose image is to be
	 *            reverted
	 * @throws RemoteException if the command fails
	 */
	public void setMultiplePreviousImages(final String[] name)
			throws RemoteException {
		for (int i = 0; i < name.length; i++) {
			String command = SET_PREVIOUS_IMAGE + " " + name;
			doCommand(command);
		}
	}

	/**
	 * Hide an agent from the GUI, that is, do not draw the agent at all.
	 * 
	 * @param name the agent name to be hidden
	 * @throws RemoteException if the command fails
	 */
	public void hideAgent(final String name) throws RemoteException {
		hideAgents(new String[] {name});
	}

	/**
	 * Hide a number of agents from the GUI, that is, simply do not draw the
	 * agents.
	 * 
	 * @param name the array of agent names that have to be hidden
	 * @throws RemoteException if the command fails
	 */
	public void hideAgents(final String[] name) throws RemoteException {
		for (int i = 0; i < name.length; i++) {
			doCommand(IMAGE + " " + name[i] + " invisible");
		}
	}

	/**
	 * Unhide an agent. This is synonimous to resetImage.
	 * 
	 * @param name the agent to unhide.
	 * @throws RemoteException if the command fails
	 */
	public void unhideAgent(final String name) throws RemoteException {
		unhideAgents(new String[] {name});
	}

	/**
	 * Unhide a number of agents. This is synonimous to resetImage.
	 * 
	 * @param name the array with the list of agents to unhide
	 * @throws RemoteException if the command fails
	 */
	public void unhideAgents(final String[] name) throws RemoteException {
		for (int i = 0; i < name.length; i++) {
			doCommand(SET_PREVIOUS_IMAGE + " " + name[i]);
		}
	}

}
