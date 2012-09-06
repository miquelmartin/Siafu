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

package de.nec.nle.siafu.control.progress;

import de.nec.nle.siafu.control.Controller;

/**
 * A Progress implementation for GUI-less runs of Siafu. The reports are given
 * by simply printing out on the text console's standard output.
 * 
 * @author Miquel Martin
 * 
 */
public class ConsoleProgress implements Progress {

	/**
	 * Report the event on the standard output.
	 * 
	 * @param amount the amount of backgrounds that will be created
	 */
	public void reportBackgroundCreationStart(final int amount) {
		System.out.print("Preparing backgrounds");
	}

	/** Report the event on the standard output. */
	public void reportBackgroundCreated() {
		System.out.print(".");
	}

	/** Report the event on the standard output. */
	public void reportBackgroundCreationEnd() {
		System.out.println();

	}

	/** Report the event on the standard output. */
	public void reportCacheElementLoaded() {
		System.out.print(".");

	}

	/**
	 * Report the event on the standard output.
	 * 
	 * @param amountOfElements the amount of elements being loaded
	 */
	public void reportCachePrefill(final int amountOfElements) {
		if (amountOfElements > 0) {
			System.out
					.print("Prefilling cache (" + amountOfElements + ")");
		} else {
			System.out.println("Important: I'm storing place "
					+ "gradients in\n" + Controller.DEFAULT_GRADIENT_PATH
					+ "\nIt might take some 10MB!");
		}

	}

	/** Report the event on the standard output. */
	public void reportCachePrefillEnded() {
		System.out.println();
	}

	/** Report the event on the standard output. */
	public void reportCreatingAgents() {
		System.out.println("Creating agents");
	}

	/**
	 * Report the event on the standard output.
	 * 
	 * @param type the place type
	 * @param amount the amount of places of that type
	 */
	public void reportPlacesFound(final String type, final int amount) {
		System.out.println("Creating Places: " + type + " (" + amount
				+ ")");

	}

	/**
	 * Do nothing on the creation of a place.
	 * 
	 * @param type the type of the place that has just been created
	 */
	public void reportPlaceCreated(final String type) {
		// Do nothing
	}

	/**
	 * Report the event on the standard output.
	 * 
	 * @param worldName the name of the world
	 */
	public void reportWorldCreation(final String worldName) {
		System.out.println("Creating the world: " + worldName);

	}

	/** Report the event on the standard output. */
	public void reportSimulationStarted() {
		System.out.println("Starting the simulation");

	}

	/** Report the event on the standard output. */
	public void reportSimulationEnded() {
		System.out.println("Simulation ended");
	}

}
