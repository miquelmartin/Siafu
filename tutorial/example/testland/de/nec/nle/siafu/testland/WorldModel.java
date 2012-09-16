package de.nec.nle.siafu.testland;

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
