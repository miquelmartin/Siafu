package de.nec.nle.siafu.testland;

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
 * Utility class to generate an agent with randomized parameters.
 * 
 * @author Miquel Martin
 * 
 */
final class AgentGenerator {

	/**
	 * A random number generator.
	 */
	private static Random rand = new Random();

	/** Prevent the class from being instantiated. */
	private AgentGenerator() {
	}

	/**
	 * Create a random agent.
	 * 
	 * @param world the world to create it in
	 * @return the new agent
	 */
	public static Agent createRandomAgent(final World world) {
		try {
			Agent a =
					new Agent(world.getRandomPlaceOfType("Nowhere")
							.getPos(), "HumanYellow", world);
			a.set("Language", new Text(rand.nextInt(2) == 0 ? "Spanish"
					: "English"));

			return a;
		} catch (PlaceNotFoundException e) {
			throw new RuntimeException(
					"You didn't define the \"Nowhere\" type of places", e);
		}
	}

	/**
	 * Create a number of random agents.
	 * 
	 * @param amount the amount of agents to create
	 * @param world the world where the agents will dwell
	 * @return an ArrayList with the created agents
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
	 * Create a random info field for the agents. In this case, the field's
	 * empty.
	 * 
	 * @param world the world the agent lives in
	 * @return the info field
	 */
	public static Map<String, Publishable> createRandomInfo(
			final World world) {
		Map<String, Publishable> info = new HashMap<String, Publishable>();
		return info;
	}

}
