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

package de.nec.nle.remote.view;

import de.nec.nle.remote.ConfigParser;
import de.nec.nle.remote.communication.CommHandler;

public class GUIBuilder implements Runnable {

	private GUI gui;
	private ConfigParser cp;
	private CommHandler comm;

	public GUIBuilder(ConfigParser cp, CommHandler comm, GUI gui) {
		this.gui = gui;
		this.cp = cp;
		this.comm = comm;
	}

	public void run() {
		cp.populateGUI(gui);
		gui.setCommHandler(comm);
		gui.openShell();
	}
}
