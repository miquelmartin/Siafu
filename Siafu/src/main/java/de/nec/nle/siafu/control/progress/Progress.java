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

/**
 * An interface for the simulator to report on the loading of a simulation. By
 * telling the user what Siafu is doing, he will hopefully put up with it.
 * 
 * @author Miquel Martin
 * 
 */
public interface Progress {
	/**
	 * Report that we started creating the world.
	 * 
	 * @param worldName the name of the world
	 */
	void reportWorldCreation(String worldName);

	/**
	 * Report that the cache is now being filled up to amountOfElements.
	 * 
	 * @param amountOfElements the amount of elements loaded so far.
	 */
	void reportCachePrefill(int amountOfElements);

	/**
	 * Report that one element has been put into the cache.
	 * 
	 */
	void reportCacheElementLoaded();

	/**
	 * Report the end of the cache prefill.
	 * 
	 */
	void reportCachePrefillEnded();

	/**
	 * Report on a new place type being read from the images.
	 * 
	 * @param type the type of place
	 * @param amount how many places of that type have been found
	 */
	void reportPlacesFound(String type, int amount);

	/**
	 * Report that a new place has been created. This is quick if the place
	 * gradient is already persisted, but takes a while if it has to be
	 * calculated. Hence the need for the progress bar.
	 * 
	 * @param type the type of the place that has just been created
	 */
	void reportPlaceCreated(String type);

	/**
	 * Report on starting the creation of agents.
	 * 
	 */
	void reportCreatingAgents();

	/**
	 * Report on the simulation haveing started.
	 * 
	 */
	void reportSimulationStarted();

	/**
	 * Report on the simulation haveing ended.
	 * 
	 */
	void reportSimulationEnded();

	/**
	 * Report on the beginning of creation of amount darkened background.
	 * 
	 * @param amount the amount of backgrounds being created
	 */
	void reportBackgroundCreationStart(int amount);

	/**
	 * Report that one of the backgrounds has now been created.
	 * 
	 */
	void reportBackgroundCreated();

	/**
	 * Report that the creation of backgrounds has been finished.
	 * 
	 */
	void reportBackgroundCreationEnd();

}
