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

import static de.nec.nle.siafu.office.Constants.MEETING_DURATION;
import static de.nec.nle.siafu.office.Constants.MEETING_DURATION_BLUR;
import static de.nec.nle.siafu.office.Constants.MEETING_START;
import static de.nec.nle.siafu.office.Constants.Fields.EVENT;
import static de.nec.nle.siafu.office.Constants.Fields.TYPE;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;

import de.nec.nle.siafu.behaviormodels.BaseWorldModel;
import de.nec.nle.siafu.exceptions.InfoUndefinedException;
import de.nec.nle.siafu.exceptions.PlaceNotFoundException;
import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.Place;
import de.nec.nle.siafu.model.World;
import de.nec.nle.siafu.types.BooleanType;
import de.nec.nle.siafu.types.EasyTime;
import de.nec.nle.siafu.types.Text;

/**
 * The world model for the simulation. In this case, the conference room calls
 * for a global meeting which some staff members attend.
 * 
 * @author Miquel Martin
 */
public class WorldModel extends BaseWorldModel {
	/** Noon time. */
	private static final EasyTime NOON = new EasyTime(12, 0);

	/** Beginning of the global meeting. */
	private EasyTime meetingStart;

	/** Time at which the meeting starts. */
	private EasyTime meetingEnd;

	/** Time at which the meeting ends. */
	private boolean dayEventsPlanned = false;

	/**
	 * Create the world model.
	 * 
	 * @param world the simulation's world.
	 */
	public WorldModel(final World world) {
		super(world);
	}

	/**
	 * Add the Busy variable to the info field of all the places.
	 * 
	 * @param places the places created so far by the images
	 */
	@Override
	public void createPlaces(final ArrayList<Place> places) {
		for (Place p : places) {
			p.set("Busy", new BooleanType(false));
		}
	}

	/**
	 * Schedule a daily meeting, and ensure all the necessary Agents are
	 * invited over to it when the time comes.
	 * 
	 * @param places the places in the simulation.
	 */
	public void doIteration(final Collection<Place> places) {
		Calendar time = world.getTime();
		EasyTime now =
				new EasyTime(time.get(Calendar.HOUR_OF_DAY), time
						.get(Calendar.MINUTE));

		if (now.isAfter(NOON) && dayEventsPlanned) {
			dayEventsPlanned = false;
		}
		if (now.isBefore(NOON) && !dayEventsPlanned) {
			planDayEvents();
		}

		try {
			if (meetingStart != null && now.isAfter(meetingStart)) {
				meetingStart = null;
				organizeGlobalMeeting();
			}

			if (now.isAfter(meetingEnd)) {
				terminateGlobalMeeting();
			}

		} catch (InfoUndefinedException e) {
			throw new RuntimeException("Person's missing information", e);
		}

	}

	/**
	 * Finish a meeting by changing the event in the involved agents.
	 */
	private void terminateGlobalMeeting() {
		Iterator<Agent> peopleIt = world.getPeople().iterator();
		while (peopleIt.hasNext()) {
			Agent p = peopleIt.next();
			if (((Text) p.get(EVENT)).toString().equalsIgnoreCase(
				"ConferenceRoomMeeting")) {
				p.set(EVENT, new Text("GlobalMeetingEnded"));
			}
		}
	}

	/**
	 * Invite as many peolpe as there are seats to the meeting.
	 */
	private void organizeGlobalMeeting() {
		Collection<Place> seats;
		try {
			seats = world.getPlacesOfType("ConferenceRoomSeat");
		} catch (PlaceNotFoundException e) {
			throw new RuntimeException(e);
		}
		Iterator<Place> seatsIt = seats.iterator();
		Iterator<Agent> peopleIt = world.getPeople().iterator();
		int seatsLeft = seats.size();
		while (peopleIt.hasNext()) {
			Agent p = peopleIt.next();
			if (((Text) p.get(TYPE)).toString().equalsIgnoreCase("staff")
					&& seatsLeft > 0) {
				seatsLeft--;
				p.set(EVENT, new Text("ConferenceRoomMeeting"));
				p.set("TemporaryDestination", seatsIt.next());
			}
		}
	}

	/** Plan a meeting at 11h00. */
	private void planDayEvents() {
		meetingStart = new EasyTime(MEETING_START);
		meetingEnd =
				new EasyTime(meetingStart).shift(MEETING_DURATION).blur(
					MEETING_DURATION_BLUR);
		dayEventsPlanned = true;
	}
}
