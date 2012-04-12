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

package de.nec.nle.siafu.control;

/**
 * Siafu's main class. It simply parses the command line parameters and starts
 * the controller.
 * 
 * @author Miquel Martin
 * 
 */
public final class Siafu {
	/**
	 * Siafu's version.
	 */
	public static final String RELEASE = "1.0.4";

	/**
	 * The string with the command line syntax.
	 */
	private static final String SYNTAX =
			"Command line arguments: [--config=CONFIG_FILE] "
					+ "[--simulation=SIMULATION_PATH] [-h]\n"
					+ "where CONFIG_FILE is the configuration XML, "
					+ "and SIMULATION_PATH is either\n"
					+ "the simulation's root folder or it's "
					+ "packed form in a jar file.";

	/**
	 * Prevent this utility class from being instantiated.
	 */
	private Siafu() {
	}

	/**
	 * Start Siafu by parsing the command line arguments and starting the
	 * controller.
	 * 
	 * @param args the command line args
	 */
	public static void main(final String[] args) {
		String configPath = null;
		String simulationPath = null;

		for (int i = 0; i < args.length; i++) {
			try {
				if (args[i].startsWith("-c=")
						|| args[i].startsWith("--config=")) {
					configPath = args[i].split("=")[1];
				} else if (args[i].startsWith("-s=")
						|| args[i].startsWith("--simulation")) {
					simulationPath = args[i].split("=")[1];
				} else if (args[i].equals("-v")
						|| args[i].equals("--version")) {
					System.out.println("Siafu v" + RELEASE
							+ " , Miquel Martin, NEC Europe Ltd.");
					System.exit(0);
				} else {
					System.err.println("Unrecognized parameter '"
							+ args[i] + "'");
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				System.err.println(SYNTAX);
				System.exit(1);
			}

		}

		new Controller(configPath, simulationPath);
	}
}
