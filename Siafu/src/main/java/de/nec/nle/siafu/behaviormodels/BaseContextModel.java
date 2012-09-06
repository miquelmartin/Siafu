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
import java.util.Map;

import de.nec.nle.siafu.model.Overlay;
import de.nec.nle.siafu.model.World;

/**
 * Classes extending BaseContextModel define the context behaviour. The method
 * createOverlays is called upon creating the world and doIteration is called
 * at every iteration.<br>
 * 
 * You must provide one extension of this class for the simulator to work.
 * 
 * @author Miquel Martin
 * 
 */
public abstract class BaseContextModel {
	/** The simulated world. */
	protected World world;

	/**
	 * Instantiate a BaseContextModel.
	 * 
	 * @param world the simulation's world
	 */
	public BaseContextModel(final World world) {
		this.world = world;
	}

	/**
	 * This method recieves the overlays that have been created from the
	 * images in the simulation data. It can then add and remove overlays, as
	 * well as modify the existing ones.
	 * 
	 * @param olList an array with the overlays already created from the
	 *            images in the simulation data
	 */
	public abstract void createOverlays(final ArrayList<Overlay> olList);

	/**
	 * The parameter overlays contains the overlays defined in the image maps,
	 * the developer can add or modify overlay values at will. This is a
	 * callback function.
	 * 
	 * @param overlays the overlays on which the developer can work to perform
	 *            changes in the simulated overlays, by altering their values.
	 */
	public abstract void doIteration(final Map<String, Overlay> overlays);
}
