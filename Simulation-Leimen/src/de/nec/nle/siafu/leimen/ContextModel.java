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

package de.nec.nle.siafu.leimen;

import java.util.ArrayList;
import java.util.Map;

import de.nec.nle.siafu.behaviormodels.BaseContextModel;
import de.nec.nle.siafu.model.Overlay;
import de.nec.nle.siafu.model.World;

/**
 * The model that controls the evolution of context in the simulation. Not
 * used in this case.
 * 
 * @author Miquel Martin
 * 
 */
public class ContextModel extends BaseContextModel {

	/**
	 * Constructor for the ContextModel.
	 * 
	 * @param world the simulated world
	 */
	public ContextModel(final World world) {
		super(world);
	}

	/**
	 * A hook to create new overlays. We use only the ones provided by the
	 * images.
	 * 
	 * @param oList the list of already existing overlays
	 */
	@Override
	public void createOverlays(final ArrayList<Overlay> oList) {
	}

	/**
	 * Hook to modify the overlays at simulation time. Nothing is done in this
	 * case.
	 * 
	 * @param overlays the available overlays
	 */
	@Override
	public void doIteration(final Map<String, Overlay> overlays) {
	}
}
