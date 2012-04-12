/*
 * Copyright NEC Europe Ltd. 2006-2007
 * 
 * This file is part of the Remote Control for the context simulator Siafu.
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

package de.nec.nle.remote;

import java.io.IOException;
import java.net.UnknownHostException;

import org.xml.sax.SAXParseException;

import de.nec.nle.remote.communication.CommHandler;
import de.nec.nle.remote.view.GUI;
import de.nec.nle.remote.view.GUIBuilder;
import de.nec.nle.remote.view.StatusUpdater;

public class Remote {

	public static void main(String[] args) {
		if (args.length < 1) {
			System.err
					.println("Please provide a remote xml config file as a parameter.");
		} else {
			new Remote(args[0]);
		}
	}

	private boolean ended = false;
	private GUI gui;
	private ConfigParser cp;
	private StatusUpdater statusUpdater;
	private CommHandler comm;

	public void die() {
		ended = true;
	}

	public boolean isEnded() {
		return ended;
	}

	public Remote(String xmlPath) {
		gui = new GUI(this);
		new Thread(gui, "GUI").start();

		try {
			cp = new ConfigParser(xmlPath, this);
		} catch (SAXParseException e) {
			gui.getDisplay().syncExec(
					new ErrorMessage("Error validating your XML file at:\n"
							+ xmlPath + "\n" + e.getMessage(), true));
			return;
		} catch (IOException e) {
			gui.getDisplay().syncExec(
					new ErrorMessage("Error accessing your XML file at:\n"
							+ xmlPath + "\n" + e.getMessage(), true));
			return;
		}

		try {
			comm = new CommHandler(this, cp);
		} catch (UnknownHostException e) {
			gui.getDisplay().syncExec(
					new ErrorMessage("Can't resolve the provided hostname",
							true));
			return;
		}

		GUIBuilder guiBuilder = new GUIBuilder(cp, comm, gui);
		gui.getDisplay().syncExec(guiBuilder);

		this.statusUpdater = new StatusUpdater(this);
		new Thread(comm, "CommHandler").start();

	}

	class ErrorMessage implements Runnable {
		private String message;
		private boolean die;

		public ErrorMessage(String message, boolean die) {
			this.message = message;
			this.die = die;
		}

		public void run() {
			gui.showError(message);
			if (die) {
				gui.getDisplay().dispose();
			}
		}

	}

	public GUI getGUI() {
		return gui;
	}

	public CommHandler getCommHandler() {
		return comm;
	}

	public void updateConnectionStatus() {
		gui.getDisplay().asyncExec(statusUpdater);

	}

}
