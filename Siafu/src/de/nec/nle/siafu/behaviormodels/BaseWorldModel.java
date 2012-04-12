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

package de.nec.nle.siafu.behaviormodels;

import java.util.ArrayList;
import java.util.Collection;

import de.nec.nle.siafu.model.Place;
import de.nec.nle.siafu.model.World;

/**
 * Classes extending BaseWorldModel define the behaviour of the Places int he
 * world. The method createPlaces is called when the world is created. The
 * method doIteration, at each iteration of the simulation.<br>
 * 
 * You must provide one extension of this class for the simulator to work.
 * @author Miquel Martin
 * 
 */
public abstract class BaseWorldModel {
	/** The simulation's world. */
	protected World world;

	/**
	 * Instantiate a BaseWorldModel.
	 * 
	 * @param world the simulation's world
	 */
	public BaseWorldModel(final World world) {
		this.world = world;
	}

	/**
	 * This callback method receives the places that have been created from
	 * the images in the simulation data, and allows the model to add or
	 * remove further places, as well as modifying the existing ones.
	 * 
	 * @param places the places created from the simulation data images
	 */
	public abstract void createPlaces(ArrayList<Place> places);

	/**
	 * The parameter places contains the places defined in the image maps, the
	 * user can add or modify place settings at will. This is a callback
	 * function.
	 * 
	 * @param places the places on which the developer can work on to perform
	 *            changes in the simulated places
	 */
	public abstract void doIteration(final Collection<Place> places);

}
