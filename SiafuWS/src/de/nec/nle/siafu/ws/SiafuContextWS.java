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

import java.rmi.RemoteException;
import static de.nec.nle.siafu.externalCommand.CommandNames.*;

/**
 * The Siafu Context Web Service enables the retrieval and setting of agent
 * context through a web service interface.
 * 
 * @author Miquel Martin
 * 
 */
public class SiafuContextWS extends SiafuWebServicesCommon {

	/**
	 * Set context information specific to an agent. Notice that this refers to
	 * the information in the info field of the agent. You can not use this
	 * method to change the value of an overlay at the position of the agent, or
	 * to change his position, appearance, etc..
	 * 
	 * Note that the value of the variable must be in flatdata format (e.g. text
	 * goes into "Text:Something" and a number goes into "IntegerNumber:3434".
	 * Check the FlatData api in Siafu for further information.
	 * 
	 * @param name
	 *            the agent to set the context for
	 * @param variable
	 *            the variable to set
	 * @param value
	 *            the flat data value for the given variable
	 * @throws RemoteException
	 *             if the command fails
	 */
	public void setAgentContext(final String name, final String variable,
			final String value) throws RemoteException {
		setAgentsMultipleContext(new String[] { name },
				new String[] { variable }, new String[] { value });
	}

	/**
	 * Set context information specific to a number of agents. Notice that this
	 * refers to the information in the info field of the agent. You can not use
	 * this method to change the value of an overlay at the position of the
	 * agent, or to change his position, appearance, etc..
	 * 
	 * @param name
	 *            the array with the agents to set the context for
	 * @param variable
	 *            an array with the info variables to set
	 * @param value
	 *            an array with the flat data values for the variables to be set
	 * @throws RemoteException
	 *             if the command fails
	 */
	public void setAgentsMultipleContext(final String[] name,
			final String[] variable, final String[] value)
			throws RemoteException {
		if (name.length != variable.length || name.length != value.length) {
			throw new RemoteException(
					"You need to provide as names as variables and values");
		}
		for (int i = 0; i < name.length; i++) {
			doCommand("setcontext " + name[i] + " " + variable[i] + " "
					+ value[i], true);
		}
	}

	/**
	 * Get the context information for the given agent. The Context information
	 * can be an agent field, an overlay value for the agent's position, or a
	 * key in the agent's info field).
	 * 
	 * The context variable can be one of three types:
	 * <ul>
	 * <li> The name of an overlay
	 * <li> The name of one of the variables in the agent's info field, defined
	 * at the agent model upon creation
	 * <li> A simulation intrinsec variable. These are called: <i>Name, Time,
	 * Position and atDestination</i>
	 * </ul>
	 * 
	 * @param name
	 *            the agent name
	 * @param context
	 *            the context to retrieve
	 * @return a String representing the contxt, whih can be parsed with the
	 *         FlatData datatypes
	 * @throws RemoteException
	 *             if the command fails
	 */
	public String getAgentContext(final String name, final String context)
			throws RemoteException {
		String[][] result = getAgentsMultipleContext(new String[] { name },
				new String[] { context });
		return result[0][0];
	}

	/**
	 * Get the given context informations for the given agent. The Context
	 * information can be an agent field, an overlay value for the agent's
	 * position, or a key in the agent's info field).
	 * 
	 * The return array a context variable per column, sorted as the context
	 * parameters.
	 * 
	 * If you provice context[]{Location, Language}, then the reply[1] will
	 * contain the agent's Language.
	 * 
	 * @see #getAgentContext(String, String)
	 * 
	 * @param name
	 *            the agent names
	 * @param context
	 *            the context to retrieve
	 * @return a String matrix, where the rows are the agents, and the
	 * @throws RemoteException
	 *             if the command fails
	 */
	public String[] getAgentMultipleContext(final String name,
			final String[] context) throws RemoteException {
		String[][] values = getAgentsMultipleContext(new String[] { name },
				context);
		return values[0];
	}

	// TODO: multidimensional arrays are not supported by axis :/
	/**
	 * Get the given context informations for the given agents. The Context
	 * information can be an agent field, an overlay value for the agent's
	 * position, or a key in the agent's info field).
	 * 
	 * The return matrix has an agent per row, and a context variable per
	 * column, sorted as the name and context parameters.
	 * 
	 * If you provice name[]{John, Peter} and contesxt[]{Location, Language},
	 * then the reply[1][0] will contain Peter's Location.
	 * 
	 * @see #getAgentContext(String, String)
	 * 
	 * @param name
	 *            the agent names
	 * @param context
	 *            the context to retrieve
	 * @return a String matrix, where the rows are the agents, and the
	 * @throws RemoteException
	 *             if the command fails
	 * 
	 */
	private String[][] getAgentsMultipleContext(final String[] name,
			final String[] context) throws RemoteException {
		String names = new String();
		String contexts = new String();
		String[][] values;

		for (int i = 0; i < name.length; i++) {
			names += name[i] + " ";
		}
		for (int i = 0; i < context.length; i++) {
			contexts += context[i] + " ";
		}

		names = names.trim();
		contexts = contexts.trim();

		String command = "getcontext " + names + " / " + contexts;
		String[] reply = doCommand(command, true).split(" ");

		values = new String[name.length][context.length];
		int n = 0;
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[0].length; j++) {
				values[i][j] = reply[n].substring(reply[n].indexOf("/") + 1,
						reply[n].length());
				n++;
			}
		}

		return values;
	}

	/**
	 * Find all of the agents which are within distance dist of the given agent.
	 * 
	 * @param agent
	 *            the agent around which we want to search
	 * @param dist
	 *            the maximum allowed distance from position
	 * @return a space separated list of the nearby agents
	 * @throws RemoteException
	 *             if the command fails
	 */
	public String findNearbyAgents(final String agent, final int dist)
			throws RemoteException {
		String command = FIND_NEARBY_AGENTS + " " + agent + " " + dist;
		String reply = doCommand(command, true);
		if (reply.endsWith("\n")) {
			reply = reply.substring(0, reply.indexOf("\n"));
		}
		return reply;
	}

	/**
	 * Find all of the places which are within distance dist of the given agent.
	 * 
	 * @param agent
	 *            the agent around which we want to search
	 * @param dist
	 *            the maximum allowed distance from position
	 * @return a space separated list of the nearby agents
	 * @throws RemoteException
	 *             if the command fails
	 */
	public String findNearbyPlaces(final String agent, final int dist)
			throws RemoteException {
		String command = FIND_NEARBY_PLACES + " " + agent + " " + dist;
		String reply = doCommand(command, true);
		if (reply.endsWith("\n")) {
			reply = reply.substring(0, reply.indexOf("\n"));
		}
		return reply;
	}
}
