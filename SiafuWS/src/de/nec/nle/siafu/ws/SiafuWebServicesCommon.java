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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * This is an abstract superclass for the Web Services of Siafu. It contains
 * the utility methods used to communicate to Siafu on its TCP port.
 * 
 * @author Miquel Martin
 */
public abstract class SiafuWebServicesCommon {
	/**
	 * The URL of the config file. It contains the host and port where Siafu
	 * is listening in.
	 */
	protected static final String CONFIG_URL =
			"http://localhost:8080/SiafuWS/config.xml";

	/** The port where Siafu is listening for commands. */
	private static int siafuPort;

	/** The ip address where Siafu is listening for commands. */
	private static InetAddress siafuIP;

	{
		if (siafuIP == null || siafuPort == 0) {
			XMLConfiguration config = new XMLConfiguration();
			try {
				config.load(CONFIG_URL);
			} catch (ConfigurationException e) {
				throw new RuntimeException(
						"Can't find the configuration file at " + CONFIG_URL,
						e);
			}
			String hostName = config.getString("host");
			try {
				siafuIP = InetAddress.getByName(hostName);
			} catch (UnknownHostException e) {
				throw new RuntimeException("Can not resolve " + siafuIP);
			}
			siafuPort = config.getInt("tcpport");
		}
	}

	/**
	 * Execute a command by sending the given String to Siafu. Do not wait for
	 * a reply.
	 * 
	 * @param command the command to be sent, as it would if typed into the
	 *            port directly
	 * @throws RemoteException if the command fails
	 */
	protected void doCommand(final String command) throws RemoteException {
		doCommand(command, false);
	}

	/**
	 * Execute a command by sending the given String to Siafu. Do not wait for
	 * a reply. Note that all commands can potentially have an Error reply,
	 * and so, ignoring it might lead to future problems, since your command
	 * might not have been executed.
	 * 
	 * @param command the command to be sent, as it would if typed into the
	 *            port directly
	 * @param replyNeeded whether to wait for a reply or not.
	 * @return the command reply if replyNeeded, or "Done" otherwise
	 * @throws RemoteException if the command fails
	 */
	protected String doCommand(final String command, final boolean replyNeeded)
			throws RemoteException {
		try {
			Socket socket = new Socket(siafuIP, siafuPort);
			BufferedReader in =
					new BufferedReader(new InputStreamReader(socket
							.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

			out.println(command);

			String reply;
			if (replyNeeded) {
				reply = in.readLine();
			} else {
				reply = "Done";
			}
			in.close();
			out.close();
			socket.close();

			if (reply.startsWith("ERR -")) {
				throw new RemoteException(reply);
			}
			return reply;
		} catch (IOException e) {
			throw new RemoteException("Command failed", e);
		}
	}
}
