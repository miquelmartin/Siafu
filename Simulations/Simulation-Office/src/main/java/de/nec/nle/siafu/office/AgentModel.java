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

package de.nec.nle.siafu.office;

import static de.nec.nle.siafu.office.Constants.AVERAGE_TOILET_INTERVAL;
import static de.nec.nle.siafu.office.Constants.AVERAGE_WORK_HOURS;
import static de.nec.nle.siafu.office.Constants.AVERAGE_WORK_START;
import static de.nec.nle.siafu.office.Constants.DEFAULT_SPEED;
import static de.nec.nle.siafu.office.Constants.INLINE_WANDER;
import static de.nec.nle.siafu.office.Constants.MAX_WAIT_TIME;
import static de.nec.nle.siafu.office.Constants.POPULATION;
import static de.nec.nle.siafu.office.Constants.TOILET_BLUR;
import static de.nec.nle.siafu.office.Constants.TOILET_RETRY_BLUR;
import static de.nec.nle.siafu.office.Constants.TOILET_VISIT_DURATION;
import static de.nec.nle.siafu.office.Constants.WORK_END_BLUR;
import static de.nec.nle.siafu.office.Constants.WORK_START_BLUR;
import static de.nec.nle.siafu.office.Constants.Fields.ACTIVITY;
import static de.nec.nle.siafu.office.Constants.Fields.DESIRED_TOILET;
import static de.nec.nle.siafu.office.Constants.Fields.DESK;
import static de.nec.nle.siafu.office.Constants.Fields.END_WORK;
import static de.nec.nle.siafu.office.Constants.Fields.EVENT;
import static de.nec.nle.siafu.office.Constants.Fields.NEXT_EVENT_TIME;
import static de.nec.nle.siafu.office.Constants.Fields.NEXT_TOILET_VISIT;
import static de.nec.nle.siafu.office.Constants.Fields.START_WORK;
import static de.nec.nle.siafu.office.Constants.Fields.TEMPORARY_DESTINATION;
import static de.nec.nle.siafu.office.Constants.Fields.TOILET_DURATION;
import static de.nec.nle.siafu.office.Constants.Fields.TOILET_INTERVAL;
import static de.nec.nle.siafu.office.Constants.Fields.TYPE;
import static de.nec.nle.siafu.office.Constants.Fields.WAITING_PLACE;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;

import de.nec.nle.siafu.behaviormodels.BaseAgentModel;
import de.nec.nle.siafu.exceptions.InfoUndefinedException;
import de.nec.nle.siafu.exceptions.PlaceNotFoundException;
import de.nec.nle.siafu.exceptions.PlaceTypeUndefinedException;
import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.Place;
import de.nec.nle.siafu.model.World;
import de.nec.nle.siafu.office.Constants.Activity;
import de.nec.nle.siafu.types.BooleanType;
import de.nec.nle.siafu.types.EasyTime;
import de.nec.nle.siafu.types.Publishable;
import de.nec.nle.siafu.types.Text;
import de.nec.nle.siafu.types.TimePeriod;

/**
 * This class extends the {@link BaseAgentModel} and implements the behaviour
 * of an agent in the office simulation.
 * 
 * @see de.nec.nle.siafu.office
 * @author miquel
 * 
 */
public class AgentModel extends BaseAgentModel {

	/** The door to the office. */
	private Place door;

	/**
	 * Instantiates this agent model.
	 * 
	 * @param world the simulation's world
	 */
	public AgentModel(final World world) {
		super(world);
		try {
			door = world.getPlacesOfType("Door").iterator().next();

		} catch (PlaceTypeUndefinedException e) {
			throw new RuntimeException("The door's undefined", e);
		}
	}

	/**
	 * Create the agents for the Office simulation. There's two types: staff
	 * and students. The main difference lies in the amount of meetings they
	 * have to attend.
	 * 
	 * @return the created agents.
	 */
	@Override
	public ArrayList<Agent> createAgents() {
		ArrayList<Agent> people = new ArrayList<Agent>(POPULATION);
		createWorker(people, "Staff", "StaffDesk");
		createWorker(people, "Student", "StudentDesk");
		return people;
	}

	/**
	 * This method creates all the workers for the office simulation.
	 * 
	 * @param people the array where you need to put your created people
	 * @param type the type of person (staff vs student)
	 * @param deskType the kind of desk (staff vs student)
	 */
	private void createWorker(final ArrayList<Agent> people,
			final String type, final String deskType) {
		Iterator<Place> deskIt;
		try {
			deskIt = world.getPlacesOfType(deskType).iterator();
		} catch (PlaceTypeUndefinedException e) {
			throw new RuntimeException("No staff desks defined", e);
		}

		int i = 0;
		while (deskIt.hasNext()) {
			Place desk = deskIt.next();

			Agent a =
					new Agent(type + "-" + i, door.getPos(), "HumanBlue",
							world);
			a.setVisible(false);
			EasyTime startWork =
					new EasyTime(AVERAGE_WORK_START).blur(WORK_START_BLUR);

			EasyTime endWork =
					new EasyTime(startWork).shift(AVERAGE_WORK_HOURS, 0)
							.blur(WORK_END_BLUR);

			EasyTime toiletInterval =
					new EasyTime(AVERAGE_TOILET_INTERVAL)
							.blur(TOILET_BLUR);

			a.set(TYPE, new Text(type));
			a.set(DESK, desk);
			a.set(START_WORK, startWork);
			a.set(END_WORK, endWork);
			a.set(ACTIVITY, Activity.RESTING);
			a.set(TOILET_INTERVAL, toiletInterval);
			a.set(NEXT_TOILET_VISIT, new EasyTime(startWork)
					.shift(toiletInterval));
			a.set(DESIRED_TOILET, new Text("None"));
			a.set(NEXT_EVENT_TIME, new Text("None"));
			a.set(TOILET_DURATION, new Text("None"));
			a.set(WAITING_PLACE, new Text("None"));
			a.set(EVENT, new Text("None"));
			a.set(TEMPORARY_DESTINATION, new Text("None"));
			a.setSpeed(DEFAULT_SPEED);
			people.add(a);
			i++;
		}
	}

	/**
	 * Handle the agents by checking if they need to respond to an event, go
	 * to the toilet or go/come home.
	 * 
	 * @param agents the people in the simulation
	 */
	@Override
	public void doIteration(final Collection<Agent> agents) {
		Calendar time = world.getTime();
		EasyTime now =
				new EasyTime(time.get(Calendar.HOUR_OF_DAY), time
						.get(Calendar.MINUTE));

		Iterator<Agent> peopleIt = agents.iterator();
		while (peopleIt.hasNext()) {
			handlePerson(peopleIt.next(), now);
		}
	}

	/**
	 * Handle the people in the simulation.
	 * 
	 * @param a the agent to handle
	 * @param now the current time
	 */
	private void handlePerson(final Agent a, final EasyTime now) {
		if (!a.isOnAuto()) {
			return; // This guy's being managed by the user interface
		}
		try {
			switch ((Activity) a.get(ACTIVITY)) {
			case RESTING:
				if (now.isAfter((EasyTime) a.get(START_WORK))
						&& now.isBefore((EasyTime) a.get(END_WORK))) {
					goToDesk(a);
					a.set(NEXT_TOILET_VISIT, new EasyTime(((EasyTime) a
							.get(START_WORK))).shift((EasyTime) a
							.get(TOILET_INTERVAL)));
				}
				break;

			case LEAVING_WORK:
				if (a.isAtDestination()) {
					goToSleep(a);
				}
				break;

			case GOING_2_DESK:
				if (a.isAtDestination()) {
					beAtDesk(a);
				}
				break;

			case GOING_2_TOILET:
				if (a.isAtDestination()) {
					lineInToilet(a, now);
				}
				break;

			case GOING_2_GLOBAL_MEETING:
				if (a.isAtDestination()) {
					beAtGlobalMeeting(a);
				}
				break;

			case AT_DESK:
				if (now.isAfter((EasyTime) a.get(END_WORK))
						|| now.isIn(new TimePeriod(new EasyTime(0, 0),
								(EasyTime) a.get(START_WORK)))) {
					goHome(a);
				}
				handleEvent(a);
				if (isTimeForToilet(a, now)) {
					goToToilet(a);
				}
				break;

			case ENTERING_TOILET:
				if (a.isAtDestination()) {
					arriveAtToilet(a, now);
				}
				break;

			case IN_TOILET:
				beAtToilet(a, now);
				break;
			default:
				throw new RuntimeException("Unknown Activity");
			}

		} catch (InfoUndefinedException e) {
			throw new RuntimeException("Unknown info requested for " + a,
					e);
		}
	}

	/**
	 * Make the agent stay in the room.
	 * 
	 * @param a the agent that's in the meeting
	 */
	private void beAtGlobalMeeting(final Agent a) {
		if (((Text) a.get(EVENT)).toString().equalsIgnoreCase(
			"GlobalMeetingEnded")) {
			a.set(EVENT, new Text("None"));
			goToDesk(a);
		}
	}

	/**
	 * Head for the meeting in the conference room.
	 * 
	 * @param a the agent that should go
	 */
	private void goToGlobalMeeting(final Agent a) {
		a.setImage("HumanYellow");
		a.setDestination((Place) a.get("TemporaryDestination"));
		a.set(ACTIVITY, Activity.GOING_2_GLOBAL_MEETING);
	}

	/**
	 * See if the agent has an event, and react to it.
	 * 
	 * @param a the agent we need to check
	 */
	private void handleEvent(final Agent a) {
		Object e = a.get(EVENT);
		if (e.equals(new Text("None"))) {
			return;
		}

		String event = e.toString();
		if (event.equalsIgnoreCase("ConferenceRoomMeeting")) {
			goToGlobalMeeting(a);
		}
	}

	/**
	 * Have the agent spend some time in the toilet.
	 * 
	 * @param a the agent in need
	 * @param now the current simulation time
	 */
	private void beAtToilet(final Agent a, final EasyTime now) {
		if (now.isAfter((EasyTime) (a.get(NEXT_EVENT_TIME)))) {
			goToDesk(a);
			((Place) a.get(DESIRED_TOILET)).set("Busy", new BooleanType(
					false));
		}
	}

	/**
	 * Mark the agent as having reached the toilet.
	 * 
	 * @param a the agent in need
	 * @param now the current simulation time
	 */
	private void arriveAtToilet(final Agent a, final EasyTime now) {
		a.set(ACTIVITY, Activity.IN_TOILET);
		a.set(NEXT_EVENT_TIME, new EasyTime(now)
				.blur(TOILET_VISIT_DURATION));
	}

	/**
	 * Make the agent queue to enter the toilet.
	 * 
	 * @param a the agent in need
	 * @param now the current time
	 */
	private void lineInToilet(final Agent a, final EasyTime now) {
		Publishable info = a.get(DESIRED_TOILET);
		Place toilet;
		if (!(info instanceof Place)) {
			try {
				toilet =
						world
								.getNearestPlaceOfType("Bathroom", a
										.getPos());
			} catch (PlaceNotFoundException e) {
				throw new RuntimeException(e);
			}
			a.set(DESIRED_TOILET, toilet);
			a.set(NEXT_EVENT_TIME, new EasyTime(now).shift(new EasyTime(0,
					MAX_WAIT_TIME)));
		} else {
			toilet = (Place) info;
		}

		if (now.isAfter((EasyTime) a.get(NEXT_EVENT_TIME))) {
			a.set(NEXT_TOILET_VISIT, new EasyTime(now).shift(0,
				2 * TOILET_RETRY_BLUR).blur(TOILET_RETRY_BLUR));
			goToDesk(a);
		}
		boolean busy = ((BooleanType) toilet.get("Busy")).getValue();
		if (busy) {
			a.wanderAround((Place) (a.get(WAITING_PLACE)), INLINE_WANDER);
		} else {
			toilet.set("Busy", new BooleanType(true));
			a.setDestination(toilet);
			a.set(ACTIVITY, Activity.ENTERING_TOILET);
		}

	}

	/**
	 * Send the agent to the toilet.
	 * 
	 * @param a the agent that just has to go
	 */
	private void goToToilet(final Agent a) {
		try {
			a.setDestination(world.getNearestPlaceOfType(
				"BathroomEntrance", a.getPos()));
		} catch (PlaceNotFoundException e) {
			throw new RuntimeException(e);
		}
		a.set(ACTIVITY, Activity.GOING_2_TOILET);
		a.set(WAITING_PLACE, a.getDestination());
	}

	/**
	 * Decide if it's time for this agent to pay a visit to the toilet.
	 * 
	 * @param a the agent to check
	 * @param now the current time
	 * @return true if the agent has to go
	 */
	private boolean isTimeForToilet(final Agent a, final EasyTime now) {
		EasyTime nextVisit = (EasyTime) a.get(NEXT_TOILET_VISIT);
		if (nextVisit.isBefore((EasyTime) a.get(START_WORK))) {
			nextVisit.shift((EasyTime) a.get(TOILET_INTERVAL));
			a.set(NEXT_TOILET_VISIT, nextVisit);
		}
		if (now.isAfter((EasyTime) a.get(NEXT_TOILET_VISIT))) {
			a.set(NEXT_TOILET_VISIT, new EasyTime(now).shift((EasyTime) a
					.get(TOILET_INTERVAL)));
			a.setImage("HumanGreen");
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Send the agent home.
	 * 
	 * @param a the lucky agent
	 */
	private void goHome(final Agent a) {
		a.setDestination(door);
		a.set(ACTIVITY, Activity.LEAVING_WORK);
	}

	/**
	 * Send the agent to sleep by making him invisible.
	 * 
	 * @param a the agent that will disappear
	 */
	private void goToSleep(final Agent a) {
		a.set(ACTIVITY, Activity.RESTING);
		a.setVisible(false);
	}

	/**
	 * Send the agent to his desk.
	 * 
	 * @param a the agent
	 */
	private void goToDesk(final Agent a) {
		a.set(ACTIVITY, Activity.GOING_2_DESK);
		a.setImage("HumanBlue");
		a.setVisible(true);
		a.setDestination((Place) a.get(DESK));
	}

	/**
	 * Make the agent stay by his desk.
	 * 
	 * @param a the hardworking agent
	 */
	private void beAtDesk(final Agent a) {
		a.set(ACTIVITY, Activity.AT_DESK);
	}

}
