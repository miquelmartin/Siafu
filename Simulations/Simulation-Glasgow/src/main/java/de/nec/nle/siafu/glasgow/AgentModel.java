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

import static de.nec.nle.siafu.glasgow.Constants.CAR_SPEED;
import static de.nec.nle.siafu.glasgow.Constants.MIN_PER_HOUR;
import static de.nec.nle.siafu.glasgow.Constants.P_HESITATE;
import static de.nec.nle.siafu.glasgow.Constants.P_GO_BY_CAR;
import static de.nec.nle.siafu.glasgow.Constants.RALF_ANDY_DIST_I;
import static de.nec.nle.siafu.glasgow.Constants.RALF_ANDY_DIST_J;
import static de.nec.nle.siafu.glasgow.Constants.TRAFFIC_AMPLITUDE;
import static de.nec.nle.siafu.glasgow.Constants.TRAFFIC_MEAN;
import static de.nec.nle.siafu.glasgow.Constants.TRAFFIC_VARIANCE;
import static de.nec.nle.siafu.glasgow.Constants.Fields.ACTIVITY;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import de.nec.nle.siafu.behaviormodels.BaseAgentModel;
import de.nec.nle.siafu.exceptions.InfoUndefinedException;
import de.nec.nle.siafu.exceptions.PlaceNotFoundException;
import de.nec.nle.siafu.exceptions.PlaceTypeUndefinedException;
import de.nec.nle.siafu.exceptions.PositionOutOfTheMapException;
import de.nec.nle.siafu.glasgow.Constants.Activity;
import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.Position;
import de.nec.nle.siafu.model.World;
import de.nec.nle.siafu.types.EasyTime;

/**
 * Behavior of the agents in the Glasgow simulation. Two users, Andy and Ralf
 * stay put, and can be moved around. We connected context-aware applications
 * to their context. There's a number of other agents that move autonomously
 * from one building to another, with a peak traffic at 14h00 and a gaussian
 * distribution.
 * 
 * @author Miquel Martin
 * 
 */
public class AgentModel extends BaseAgentModel {

	/**
	 * Instantiate the agent model.
	 * 
	 * @param world the world the agents live in
	 */
	public AgentModel(final World world) {
		super(world);
	}

	/**
	 * A random number generator.
	 */
	private static final Random RAND = new Random();

	/** The amount of people in Glasgow :). */
	private final int population = 50;

	/**
	 * Create the agents, where most are zombies, except for Ralf and Andy,
	 * who won't move.
	 * 
	 * @return a list with the simulation's agents.
	 */
	public ArrayList<Agent> createAgents() {
		System.out.println("Creating " + population + " people");
		ArrayList<Agent> people =
				AgentGenerator.createRandomPopulation(population, world);

		Iterator<Agent> peopleIt = people.iterator();
		while (peopleIt.hasNext()) {
			Agent a = peopleIt.next();

			a.setSpeed(1 + RAND.nextInt(2));
			a.setVisible(false);
		}

		// Kidnap two guys and make them our special guys
		peopleIt = people.iterator();
		Agent andy = peopleIt.next();
		Agent ralf = peopleIt.next();

		Position start;
		try {
			start =
					world.getPlacesOfType("Start").iterator().next()
							.getPos();
		} catch (PlaceTypeUndefinedException e) {
			throw new RuntimeException("No Start place defined", e);
		}

		andy.setName("Andy");
		andy.setPos(start);
		andy.setImage("HumanYellow");
		andy.setVisible(true);
		andy.setSpeed(2);
		andy.getControl();
		start.setRow(start.getRow() - RALF_ANDY_DIST_I / 2);
		start.setCol(start.getCol() - RALF_ANDY_DIST_J / 2);
		ralf.setName("Ralf");
		try {
			ralf.setPos(new Position(start.getRow() + RALF_ANDY_DIST_I,
					start.getCol() + RALF_ANDY_DIST_J));
		} catch (PositionOutOfTheMapException e) {
			throw new RuntimeException(
					"Start poinf for Ralf is not walkable", e);
		}
		ralf.setImage("HumanBlue");
		ralf.setVisible(true);
		ralf.setSpeed(2);
		ralf.getControl();
		return people;
	}

	/**
	 * Move the user from the current place to another building, with a
	 * certain probability of doing so with a car.
	 * 
	 * @param a the agent that must change buildings
	 */
	protected void changeBuilding(final Agent a) {
		a.setVisible(true);

		if (RAND.nextFloat() < P_GO_BY_CAR) {
			a.setImage("CarGreen");
			a.setSpeed(CAR_SPEED);
		}
		try {
			a.setDestination(world.getRandomPlaceOfType("Building"));
		} catch (PlaceNotFoundException e) {
			throw new RuntimeException(
					"You didn't define Building place types", e);
		}

		a.set(ACTIVITY, Activity.WALKING);
	}

	/**
	 * Move the agents from a building on to the next, with 14h00 being the
	 * busiest hour.
	 * 
	 * @param agents the agents to work on
	 */
	public void doIteration(final Collection<Agent> agents) {
		Iterator<Agent> agentsIt = agents.iterator();
		while (agentsIt.hasNext()) {
			Agent a = agentsIt.next();

			if (!a.isOnAuto()) {
				continue; // This guy's being managed by the user interface
			}
			Calendar time = world.getTime();
			EasyTime now =
					new EasyTime(time.get(Calendar.HOUR_OF_DAY), time
							.get(Calendar.MINUTE));

			try {
				switch ((Activity) a.get(ACTIVITY)) {

				case WAITING:
					double t =
							now.getHour()
									+ ((double) now.getMinute() / MIN_PER_HOUR);
					double gaussianThreshold =
							TRAFFIC_AMPLITUDE
									* Math.exp(-Math.pow(
										(t - TRAFFIC_MEAN), 2)
											/ TRAFFIC_VARIANCE);

					if (RAND.nextDouble() < gaussianThreshold) {
						changeBuilding(a);
						// p.setImage("HumanMagenta");
					}
					break;

				case WALKING:
					if (a.isAtDestination()) {
						if (!(RAND.nextFloat() < P_HESITATE)) {
							// Go in the building
							a.setVisible(false);
							a.setPreviousImage();
							a.set(ACTIVITY, Activity.WAITING);
						}
						a.setSpeed(1 + RAND.nextInt(2));
					}
					break;
				default:
					throw new RuntimeException("Unknown Activity");
				}
			} catch (InfoUndefinedException e) {
				e.printStackTrace();
				System.err.println("Information missing to handle "
						+ a.getName() + ". Skipping.");
				return;
			}
		}
	}
}
