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

package de.nec.nle.siafu.valencia;


import de.nec.nle.siafu.behaviormodels.BaseWorldModel;
import de.nec.nle.siafu.model.Place;
import de.nec.nle.siafu.model.World;

import java.util.ArrayList;
import java.util.Collection;


/**
 * The world model for the simulation. In this case, nothing is done.
 * @author Miquel Martin
 */
public class WorldModel extends BaseWorldModel {
	
	/**
	 * Instantiate the WorldModel with the world.
	 * @param world the simulation's world
	 */
	public WorldModel(final World world) {
		super(world);
	}

	/**
	 * The places created by the images are fine, no new places needed.
	 * 
	 * @param places an ArrayList with the places created with the images
	 */
	@Override
	public void createPlaces(final ArrayList<Place> places) {
		// Do nothing.
	}

	/**
	 * Nothing done here.
	 * 
	 * @param places the places in the simulation
	 */
	@Override
	public void doIteration(final Collection<Place> places) {
		// Do nothing
	}
}
