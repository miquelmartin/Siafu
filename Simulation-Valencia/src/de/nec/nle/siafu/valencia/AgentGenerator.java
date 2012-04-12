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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.nec.nle.siafu.exceptions.PlaceNotFoundException;
import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.World;
import de.nec.nle.siafu.types.Publishable;
import de.nec.nle.siafu.types.Text;

/**
 * Utility class to generate agents that fit the Valencia Simulation.
 * 
 * @author Miquel Martin
 */
public final class AgentGenerator {
	/** A random number generator. */
	private static Random rand = new Random();

	/**
	 * Prevent this class from being instantiated.
	 */
	private AgentGenerator() {
	}

	/**
	 * Create a randomized agent for Valencia.
	 * 
	 * @param world the world the agent will live in
	 * @return the new agent
	 */
	public static Agent createRandomAgent(final World world) {
		try {
			Agent a =
					new Agent(
							world.getRandomPlaceOfType("Other").getPos(),
							"HumanGreen", world);
			a.set("Language", new Text(rand.nextInt(2) == 0 ? "Spanish"
					: "English"));
			return a;
		} catch (PlaceNotFoundException e) {
			throw new RuntimeException(
					"You didn't define the \"Other\" type of places", e);
		}
	}

	/**
	 * Create a number of agents for the Valencia simulation.
	 * 
	 * @param amount the amount of agents to create
	 * @param world the world the agents will live in
	 * @return an ArrayList with the created population
	 */
	public static ArrayList<Agent> createRandomPopulation(
			final int amount, final World world) {
		ArrayList<Agent> population = new ArrayList<Agent>(amount);
		for (int i = 0; i < amount; i++) {
			population.add(createRandomAgent(world));
		}
		return population;
	}

	/**
	 * Create a randomized info field for an agent.
	 * 
	 * @param world the world the agent lives in
	 * @return the randomized info field
	 */
	public static Map<String, Publishable> createRandomInfo(
			final World world) {
		Map<String, Publishable> info = new HashMap<String, Publishable>();
		return info;
	}

}
