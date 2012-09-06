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

package de.nec.nle.siafu.glasgow;

import static de.nec.nle.siafu.glasgow.Constants.ACQUISITIVELEVEL_TYPES;
import static de.nec.nle.siafu.glasgow.Constants.CUISINE_TYPES;
import static de.nec.nle.siafu.glasgow.Constants.GENDER_TYPES;
import static de.nec.nle.siafu.glasgow.Constants.MUSICGENDER_TYPES;
import static de.nec.nle.siafu.glasgow.Constants.WORKAREA_TYPES;
import static de.nec.nle.siafu.glasgow.Constants.Fields.ACQUISITIVELEVEL;
import static de.nec.nle.siafu.glasgow.Constants.Fields.ACTIVITY;
import static de.nec.nle.siafu.glasgow.Constants.Fields.AGE;
import static de.nec.nle.siafu.glasgow.Constants.Fields.CUISINE;
import static de.nec.nle.siafu.glasgow.Constants.Fields.GENDER;
import static de.nec.nle.siafu.glasgow.Constants.Fields.MUSICGENDER;
import static de.nec.nle.siafu.glasgow.Constants.Fields.WORKAREA;

import java.util.ArrayList;
import java.util.Random;

import de.nec.nle.siafu.exceptions.PlaceNotFoundException;
import de.nec.nle.siafu.glasgow.Constants.Activity;
import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.World;
import de.nec.nle.siafu.types.IntegerNumber;
import de.nec.nle.siafu.types.Text;

/**
 * Utility class to generate agents fitting the Glasgow scenario.
 * 
 * @author Miquel Martin
 */
final class AgentGenerator {

	/** Maximum agent's age. */
	private static final int MAX_AGE = 60;
	
	/** Minimum agent's age. */
	private static final int MIN_AGE = 10;
	
	/** A random number generator. */
	private static Random rand = new Random();

	/** Prevent instantiation of this class. */
	private AgentGenerator() {
	}

	/**
	 * Create a population made up of <code>size</code> random agents.
	 * 
	 * @param size the population size
	 * @param world the world object of the whole simulation
	 * @return an ArrayList with the collection of agents
	 */
	public static ArrayList<Agent> createRandomPopulation(final int size,
		final World world) {
		ArrayList<Agent> population = new ArrayList<Agent>(size);

		for (int i = 0; i < size; i++) {
			population.add(createRandomAgent(world));
		}

		return population;
	}

	/**
	 * Create a random agent that fits the Glasgow simulation.
	 * @param world the world the agent will live in
	 * @return the new agent
	 */
	public static Agent createRandomAgent(final World world) {
		Agent a;
		try {
			a =
					new Agent(world.getRandomPlaceOfType("Building")
							.getPos(), "HumanGreen", world);
		} catch (PlaceNotFoundException e) {
			throw new RuntimeException(
					"You didn't define the \"Bulding\" type of places", e);
		}

		a.set(AGE, new IntegerNumber(MIN_AGE + rand.nextInt(MAX_AGE)));
		a.set(CUISINE, getRandomType(CUISINE_TYPES));
		a.set(WORKAREA, getRandomType(WORKAREA_TYPES));
		a.set(ACQUISITIVELEVEL, getRandomType(ACQUISITIVELEVEL_TYPES));
		a.set(GENDER, getRandomType(GENDER_TYPES));
		a.set(MUSICGENDER, getRandomType(MUSICGENDER_TYPES));
		a.set(ACTIVITY, Activity.WAITING);
		return a;
	}

	/**
	 * Return a random element from the given array list.
	 * 
	 * @param types the ArrayList containing the types
	 * @return the randomly chosen type
	 */
	private static Text getRandomType(final ArrayList types) {
		return (Text) types.get(rand.nextInt(types.size()));
	}
}
