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

import static de.nec.nle.siafu.leimen.Constants.AVG_WORK_TIME;
import static de.nec.nle.siafu.leimen.Constants.BIG_WANDER;
import static de.nec.nle.siafu.leimen.Constants.ONE_HOUR_BLUR;
import static de.nec.nle.siafu.leimen.Constants.PARTY_ANIMAL_FACTOR;
import static de.nec.nle.siafu.leimen.Constants.PARTY_ANIMAL_TYPES;
import static de.nec.nle.siafu.leimen.Constants.POPULATION;
import static de.nec.nle.siafu.leimen.Constants.SMALL_WANDER;
import static de.nec.nle.siafu.leimen.Constants.TWO_HOUR_BLUR;
import static de.nec.nle.siafu.leimen.Constants.WORKAHOLIC_FACTOR;
import static de.nec.nle.siafu.leimen.Constants.WORKAHOLIC_TYPES;
import static de.nec.nle.siafu.leimen.Constants.WORK_PARTY_MIN_TIME;
import static de.nec.nle.siafu.leimen.Constants.Fields.ACTIVITY;
import static de.nec.nle.siafu.leimen.Constants.Fields.HOME;
import static de.nec.nle.siafu.leimen.Constants.Fields.OFFICE;
import static de.nec.nle.siafu.leimen.Constants.Fields.PARTY_ANIMAL;
import static de.nec.nle.siafu.leimen.Constants.Fields.PARTY_END;
import static de.nec.nle.siafu.leimen.Constants.Fields.PARTY_PLACE;
import static de.nec.nle.siafu.leimen.Constants.Fields.PARTY_START;
import static de.nec.nle.siafu.leimen.Constants.Fields.SLEEP_PERIOD;
import static de.nec.nle.siafu.leimen.Constants.Fields.WILL_GO_PARTY;
import static de.nec.nle.siafu.leimen.Constants.Fields.WORKAHOLIC;
import static de.nec.nle.siafu.leimen.Constants.Fields.WORK_END;
import static de.nec.nle.siafu.leimen.Constants.Fields.WORK_START;
import static de.nec.nle.siafu.leimen.Constants.Fields.HAS_CAR;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Random;

import de.nec.nle.siafu.behaviormodels.BaseAgentModel;
import de.nec.nle.siafu.exceptions.InfoUndefinedException;
import de.nec.nle.siafu.exceptions.PlaceNotFoundException;
import de.nec.nle.siafu.leimen.Constants.Activity;
import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.Place;
import de.nec.nle.siafu.model.World;
import de.nec.nle.siafu.types.BooleanType;
import de.nec.nle.siafu.types.EasyTime;
import de.nec.nle.siafu.types.Text;
import de.nec.nle.siafu.types.TimePeriod;

/**
 * Defines the behavior of the agent population. Essentially, they wake up in
 * the morning at home (red spots), and go to work (blue spots) and then
 * either go home and stay, or go to an entertainment place (yellow spots) or
 * one and then the other. Eventually (hopefully before they have to go work
 * again) they go to sleep home, and the cycle resumes.
 * 
 * @author Miquel Martin
 * 
 */
public class AgentModel extends BaseAgentModel {
	/**
	 * Cars move in between 1 and 1+SPEED_RANGE speed.
	 */
	private static final int SPEED_RANGE = 3;

	/**
	 * An agent that owns a car will only use it to cover distances of this
	 * amount.
	 */
	private static final int MIN_DIST_4_CAR = 100;

	/** A random number generator. */
	private static final Random RAND = new Random();

	/**
	 * Noon time.
	 */
	private static final EasyTime NOON = new EasyTime(12, 0);

	/**
	 * A constructor that simply calls the super constructor (BaseAgentModel).
	 * 
	 * @param world the simulated world
	 */
	public AgentModel(final World world) {
		super(world);
	}

	/**
	 * Create POPULATION agents randomly, using the PersonGenerator class.
	 * 
	 * @return the list of agents.
	 */
	public ArrayList<Agent> createAgents() {
		System.out.println("Creating " + POPULATION + " people");

		ArrayList<Agent> people =
				AgentGenerator.createRandomPopulation(POPULATION, world);

		// We need a value for the first party. It will, however, be ignored
		final int firstPartyStart = 22;
		final int firstPartyEnd = 23;

		for (Agent a : people) {
			a.set(WILL_GO_PARTY, new BooleanType(false));
			a.set(PARTY_START, new EasyTime(firstPartyStart, 0));
			a.set(PARTY_END, new EasyTime(firstPartyEnd, 0));
			try {
				a.set(PARTY_PLACE, world.getRandomPlaceOfType("Entertainment"));
			} catch (PlaceNotFoundException e) {
				throw new RuntimeException("You need to have places of type Entertainment for this simulation.");
			}
		}
		return people;
	}

	/**
	 * Perform an iteration by going through each of the agents. The exact
	 * behaviour is explained in this class' description. Note that agents who
	 * are being controlled by the GUI will not be affected.
	 * 
	 * @param agents the agents in the world (inluding those controlled
	 *            through the GUI
	 */
	public void doIteration(final Collection<Agent> agents) {
		for (Agent a : agents) {
			if (!a.isOnAuto()) {
				continue; // This guy's being managed by the user interface
			}

			Calendar time = world.getTime();
			EasyTime now =
					new EasyTime(time.get(Calendar.HOUR_OF_DAY), time
							.get(Calendar.MINUTE));

			TimePeriod sleepPeriod = (TimePeriod) a.get(SLEEP_PERIOD);
			EasyTime workStart = (EasyTime) a.get(WORK_START);
			EasyTime workEnd = (EasyTime) a.get(WORK_END);
			EasyTime partyEnd = (EasyTime) a.get(PARTY_END);
			EasyTime sleepEnd =
					((TimePeriod) a.get(SLEEP_PERIOD)).getEnd();
			switch ((Activity) a.get(ACTIVITY)) {
			case ASLEEP:
				if (!now.isIn(sleepPeriod)) {
					a.set(ACTIVITY, Activity.AT_HOME);
				}

				break;

			case AT_HOME:

				if (now.isAfter(workStart) && now.isBefore(workEnd)) {
					goWork(a);
				} else if (isTimeForParty(now, a)) {
					goParty(a);
				} else if (now.isIn(sleepPeriod)) {
					a.set(ACTIVITY, Activity.ASLEEP);
				} else {
					beIdleAtHome(a);
				}

				break;

			case GOING_TO_WORK:

				if (a.isAtDestination()) {
					a.set(WILL_GO_PARTY, new BooleanType(false));
					a.set(ACTIVITY, Activity.WORKING);
					carify(a, false, "HumanBlue", 1);
					setWorkEnd(a, workStart);
				}

				break;

			case WORKING:
				if (!now.isBefore((EasyTime) a.get(WORK_END))) {
					decideAboutGoingParty(a);
					goHome(a);
				} else {
					beAtWork(a);
				}

				break;

			case GOING_HOME:

				if (a.isAtDestination()) {
					a.set(ACTIVITY, Activity.AT_HOME);
				} else if (isTimeForParty(now, a)) {
					goParty(a);
				}

				break;

			case GOING_TO_PARTY:

				if (a.isAtDestination()) {
					a.set(ACTIVITY, Activity.AT_PARTY);
					a.set(WILL_GO_PARTY, new BooleanType(false));
					setPartyEnd(a, now);
				}

				break;

			case AT_PARTY:
				if (now.isAfter(partyEnd)) {
					goHome(a);
				} else if (now.isAfter(sleepEnd) && now.isBefore(NOON)) {
					goWork(a);
				} else {
					beAtParty(a);
				}

				break;
			default:
				throw new RuntimeException("Unable to handle activity "
						+ (Activity) a.get(ACTIVITY));
			}
		}
	}

	/**
	 * Modifies the agent to behave like a car by setting the right speed and
	 * appearance).
	 * 
	 * @param a the agent to turn into a car
	 * @param turnToCar true if the agent should become a car, false if it
	 *            should look back like a person when looking like a car.
	 * @param appearance the name of the sprite that represents the car we
	 *            need
	 * @param speed the speed of a car. The speed of a person is 1 by default.
	 */
	protected void carify(final Agent a, final boolean turnToCar,
			final String appearance, final int speed) {
		if (turnToCar) {
			a.setImage(appearance);
			a.setSpeed(speed);
		} else {
			a.setSpeed(1);
			a.setImage(appearance);
		}
	}

	/**
	 * Give a numeric value to a string from a sorted list of strings. In this
	 * case, the workaholic and party animal levels. For instance, a hermit or
	 * a slacker have a level of 0, while "Just say where" or terminal
	 * workaholics are whoping top level 4.
	 * 
	 * @param value the value to rate
	 * @param types the list of possible types to rate against
	 * @return the party animal index, between 0 and 4, both included
	 */
	protected int getIndex(final Text value, final ArrayList types) {
		for (int i = 0; i < types.size(); i++) {
			if (value.equals(types.get(i))) {
				return i;
			}
		}
		throw new RuntimeException("Uknown value: " + value);
	}

	/**
	 * Set the time at which the party ends, which is the given start time
	 * plus a number of hours depending on the party animal factor, and
	 * blurred over one hour.
	 * 
	 * @param a the agent whose party end we want to set
	 * @param start the time at which the party starts
	 */
	protected void setPartyEnd(final Agent a, final EasyTime start) {
		int paIndex =
				getIndex((Text) a.get(PARTY_ANIMAL), PARTY_ANIMAL_TYPES);
		EasyTime partyEnd =
				new EasyTime(start)
						.shift(paIndex * PARTY_ANIMAL_FACTOR, 1);
		partyEnd.blur(ONE_HOUR_BLUR);
		a.set(PARTY_END, partyEnd);
		return;
	}

	/**
	 * Set the time at which the agent leaves work, which is the given start
	 * time plus a AVG_WORK_TIME plus number of hours depending on the
	 * workaholic factor, and blurred over one hour.
	 * 
	 * @param a the agent whose party end we want to set
	 * @param start the time at which the party starts
	 */
	protected void setWorkEnd(final Agent a, final EasyTime start) {
		int paIndex = getIndex((Text) a.get(WORKAHOLIC), WORKAHOLIC_TYPES);
		EasyTime partyEnd =
				new EasyTime(start).shift(AVG_WORK_TIME + paIndex
						* WORKAHOLIC_FACTOR, 1);
		partyEnd.blur(ONE_HOUR_BLUR);
		a.set(WORK_END, partyEnd);
		return;
	}

	/**
	 * Upon leaving work a agent decides if he is gonig party. The more of a
	 * party animal the agent is, the more chances that he will decide to go
	 * party. Also, the more of a party animal, the later he will start the
	 * party
	 * 
	 * @param a the agent for whom we have to decide
	 * @throws InfoUndefinedException
	 */
	protected void decideAboutGoingParty(final Agent a) {
		int paIndex =
				getIndex((Text) a.get(PARTY_ANIMAL), PARTY_ANIMAL_TYPES);

		if (RAND.nextInt(PARTY_ANIMAL_TYPES.size()) < paIndex) {
			// Go party
			EasyTime workEnd = ((EasyTime) a.get(WORK_END));
			int shift = WORK_PARTY_MIN_TIME + paIndex;
			EasyTime partyStart = new EasyTime(workEnd).shift(shift, 0);
			partyStart.blur(TWO_HOUR_BLUR);
			a.set(WILL_GO_PARTY, new BooleanType(true));
			a.set(PARTY_START, partyStart);
			try {
				a.set(PARTY_PLACE, world
						.getRandomPlaceOfType("Entertainment"));
			} catch (PlaceNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Figure out if the agent should be heading for a party right now,
	 * namely, whether he has decided to go party today, and if the time to go
	 * has already past.
	 * 
	 * @param now the current time
	 * @param a the agent being considered
	 * @return true if it is time to go party, false otherwise
	 */
	protected boolean isTimeForParty(final EasyTime now, final Agent a) {
		return ((BooleanType) a.get(WILL_GO_PARTY)).getValue()
				&& now.isAfter((EasyTime) a.get(PARTY_START));
	}

	/**
	 * Go to the assigned office. If the place is far, and the agent has a car
	 * then he turns into a car to go there.
	 * 
	 * @param a the agent to sent to work
	 */
	protected void goWork(final Agent a) {
		a.set(ACTIVITY, Activity.GOING_TO_WORK);

		if ((((Place) a.get(OFFICE)).distanceFrom(a.getPos()) > MIN_DIST_4_CAR)
				&& ((BooleanType) a.get(HAS_CAR)).getValue()) {
			carify(a, true, "CarBlue", RAND.nextInt(SPEED_RANGE) + 1);
		} else {
			a.setImage("HumanBlue");
		}

		a.setDestination((Place) a.get(OFFICE));
	}

	/**
	 * Send the agent to an entertainment place.
	 * 
	 * @param a the agent to be sent
	 */
	protected void goParty(final Agent a) {
		a.set(ACTIVITY, Activity.GOING_TO_PARTY);
		a.setImage("HumanYellow");
		try {
			a.setDestination((Place) world
					.getRandomPlaceOfType("Entertainment"));
		} catch (PlaceNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Send the agent home.
	 * 
	 * @param a the agent to be sent home
	 */
	protected void goHome(final Agent a) {
		a.set(ACTIVITY, Activity.GOING_HOME);
		a.setImage("HumanMagenta");
		a.setDestination((Place) a.get(HOME));
	}

	/**
	 * Keeps the user wandering around home.
	 * 
	 * @param a the agent to keep at home
	 */
	protected void beIdleAtHome(final Agent a) {
		a.wanderAround((Place) a.get(HOME), SMALL_WANDER);
	}

	/**
	 * Do the activities related to being at work. In this case, wander around
	 * the work place with a radius of <code>SMALL_WANDER</code>.
	 * 
	 * @param p the agent that should be at work
	 * @throws InfoUndefinedException if the person doesn't exist
	 */
	protected void beAtWork(final Agent p) throws InfoUndefinedException {
		p.wanderAround((Place) p.get(OFFICE), SMALL_WANDER);
	}

	/**
	 * Keeps the agent int he entertainment place, wandering around, and
	 * potentially getting out of the entertainment area.
	 * 
	 * @param a the agent to keep at the party
	 */
	protected void beAtParty(final Agent a) {
		a.wanderAround((Place) a.get(PARTY_PLACE), BIG_WANDER);
	}
}
