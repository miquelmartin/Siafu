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

import static de.nec.nle.siafu.leimen.Constants.AVG_SLEEP_TIME;
import static de.nec.nle.siafu.leimen.Constants.AVG_WORK_START;
import static de.nec.nle.siafu.leimen.Constants.AVG_WORK_TIME;
import static de.nec.nle.siafu.leimen.Constants.CUISINE_TYPES;
import static de.nec.nle.siafu.leimen.Constants.GENDER_TYPES;
import static de.nec.nle.siafu.leimen.Constants.HALF_HOUR_BLUR;
import static de.nec.nle.siafu.leimen.Constants.LANGUAGE_TYPES;
import static de.nec.nle.siafu.leimen.Constants.MAX_AGE;
import static de.nec.nle.siafu.leimen.Constants.MIN_AGE;
import static de.nec.nle.siafu.leimen.Constants.PARTY_ANIMAL_TYPES;
import static de.nec.nle.siafu.leimen.Constants.PROB_HAS_CAR;
import static de.nec.nle.siafu.leimen.Constants.TWO_HOUR_BLUR;
import static de.nec.nle.siafu.leimen.Constants.WORKAHOLIC_TYPES;
import static de.nec.nle.siafu.leimen.Constants.Fields.ACTIVITY;
import static de.nec.nle.siafu.leimen.Constants.Fields.AGE;
import static de.nec.nle.siafu.leimen.Constants.Fields.CUISINE;
import static de.nec.nle.siafu.leimen.Constants.Fields.GENDER;
import static de.nec.nle.siafu.leimen.Constants.Fields.HAS_CAR;
import static de.nec.nle.siafu.leimen.Constants.Fields.HOME;
import static de.nec.nle.siafu.leimen.Constants.Fields.LANGUAGE;
import static de.nec.nle.siafu.leimen.Constants.Fields.OFFICE;
import static de.nec.nle.siafu.leimen.Constants.Fields.PARTY_ANIMAL;
import static de.nec.nle.siafu.leimen.Constants.Fields.SLEEP_PERIOD;
import static de.nec.nle.siafu.leimen.Constants.Fields.WORKAHOLIC;
import static de.nec.nle.siafu.leimen.Constants.Fields.WORK_END;
import static de.nec.nle.siafu.leimen.Constants.Fields.WORK_START;

import java.util.ArrayList;
import java.util.Random;

import de.nec.nle.siafu.exceptions.PlaceNotFoundException;
import de.nec.nle.siafu.leimen.Constants.Activity;
import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.Place;
import de.nec.nle.siafu.model.Position;
import de.nec.nle.siafu.model.World;
import de.nec.nle.siafu.types.BooleanType;
import de.nec.nle.siafu.types.EasyTime;
import de.nec.nle.siafu.types.IntegerNumber;
import de.nec.nle.siafu.types.Text;
import de.nec.nle.siafu.types.TimePeriod;

/**
 * This class creates an agent that's suitable for the Leimen simulation, by
 * setting randomized values for the whole population.
 * 
 * @author Miquel Martin
 * 
 */
final class AgentGenerator {

	/**
	 * A random number generator.
	 */
	private static Random rand = new Random();

	/**
	 * Keep this class from being instantiated.
	 */
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
	 * Create a random agent that fits the Leimen simulation.
	 * @param world the world of the simulation
	 * @return the new agent
	 */
	public static Agent createRandomAgent(final World world) {
		Position anywhere = world.getPlaces().get(0).getPos();

		Agent a = new Agent(anywhere, "HumanGreen", world);

		boolean hasCar = false;
		if (rand.nextFloat() < PROB_HAS_CAR) {
			hasCar = true;
		}
		int age = MIN_AGE + rand.nextInt(MAX_AGE - MIN_AGE);
		EasyTime workStart = new EasyTime(AVG_WORK_START, 0);
		EasyTime sleepEnd = new EasyTime(workStart).shift(-1, 0);
		EasyTime sleepStart =
				new EasyTime(sleepEnd).shift(-AVG_SLEEP_TIME, 0);

		workStart.blur(TWO_HOUR_BLUR);
		sleepEnd.blur(HALF_HOUR_BLUR);
		sleepStart.blur(TWO_HOUR_BLUR);

		a.set(AGE, new IntegerNumber(age));
		a.set(CUISINE, getRandomType(CUISINE_TYPES));
		a.set(LANGUAGE, getRandomType(LANGUAGE_TYPES));
		a.set(GENDER, getRandomType(GENDER_TYPES));
		a.set(PARTY_ANIMAL, getRandomType(PARTY_ANIMAL_TYPES));
		a.set(WORKAHOLIC, getRandomType(WORKAHOLIC_TYPES));
		a.set(HAS_CAR, new BooleanType(hasCar));
		a.set(ACTIVITY, Activity.ASLEEP);
		a.set(WORK_START, workStart);
		a.set(WORK_END, new EasyTime(workStart).shift(AVG_WORK_TIME, 0));
		a.set(SLEEP_PERIOD, new TimePeriod(sleepStart, sleepEnd));

		try {
			a.set(HOME, world.getRandomPlaceOfType("Homes"));
		} catch (PlaceNotFoundException e) {
			throw new RuntimeException(
					"Can't find any homes Places. Did u create them?");
		}

		try {
			a.set(OFFICE, world.getRandomPlaceOfType("Offices"));
		} catch (PlaceNotFoundException e) {
			throw new RuntimeException(
					"Can't find any offices. Did u create them?");
		}

		a.setPos(((Place) a.get(HOME)).getPos());

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
