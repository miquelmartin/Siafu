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

import de.nec.nle.siafu.types.EasyTime;
import de.nec.nle.siafu.types.FlatData;
import de.nec.nle.siafu.types.Publishable;
import de.nec.nle.siafu.types.Text;

/**
 * A list of the constants used by this simulation. None of this is strictly
 * needed, but it makes referring to certain values easier and less error
 * prone.
 * 
 * @author Miquel Martin
 */
public class Constants {
	/** Time for the daily meeting. */
	public static final EasyTime MEETING_START = new EasyTime(11, 0);
	
	/** Average duration of meetings. */
	public static final EasyTime MEETING_DURATION = new EasyTime(1, 30);

	/** Blur of the meeting duration. */
	public static final int MEETING_DURATION_BLUR = 30;

	/** Wander radius when lining for something. */
	public static final int INLINE_WANDER = 10;

	/** Average retry time when the toilet line is too long. */
	public static final int TOILET_RETRY_BLUR = 90;

	/** Maximum waiting time when in line. */
	public static final int MAX_WAIT_TIME = 30;

	/** Name says all. */
	public static final int TOILET_VISIT_DURATION = 20;

	/** Default agent speed. */
	public static final int DEFAULT_SPEED = 6;

	/** Amount of people. */
	public static final int POPULATION = 100;

	/** Agents go to the toilet every TOILET_INTERVAL plus minus some 200min. */
	public static final int TOILET_BLUR = 200;

	/** Blur in minutes from the average work end time. */
	public static final int WORK_END_BLUR = 200;

	/** Average amount of working hours. */
	public static final int AVERAGE_WORK_HOURS = 8;

	/** Agents start working 300min around the default time. */
	public static final int WORK_START_BLUR = 120;

	/** The average time at which people start working. */
	public static final EasyTime AVERAGE_WORK_START = new EasyTime(9, 0);

	/** The average interval between toilet visits. */
	public static final EasyTime AVERAGE_TOILET_INTERVAL =
			new EasyTime(3, 0);

	/**
	 * The names of the fields in each agent object.
	 */
	static class Fields {
		/** The agent's current activity. */
		public static final String ACTIVITY = "Activity";

		/** Whether the person's staff or student. */
		public static final String TYPE = "Type";

		/** The type of desk. */
		public static final String DESK = "Desk";

		/** The time at which the agent starts to work. */
		public static final String START_WORK = "StartWork";

		/** The time at whcih the agent ends working. */
		public static final String END_WORK = "EndWork";

		/** How often the agent goes to the toilet. */
		public static final String TOILET_INTERVAL = "ToiletInterval";

		/** The time for the next visit to the toilet. */
		public static final String NEXT_TOILET_VISIT = "NextToiletVisit";

		/** The toilet for which the agent is queuing. */
		public static final String DESIRED_TOILET = "DesiredToilet";

		/** The time at which the next event occurs. */
		public static final String NEXT_EVENT_TIME = "NextEventTime";

		/** The time spend in the toilet. */
		public static final String TOILET_DURATION = "ToiletDuration";

		/** The place where the agent is lining. */
		public static final String WAITING_PLACE = "WaitingPlace";

		/** The next upcoming event. */
		public static final String EVENT = "Event";

		/** A temporary dfestination. */
		public static final String TEMPORARY_DESTINATION =
				"TemporaryDestination";
	}

	/**
	 * List of possible activies. This is implemented as an enum because it
	 * helps us in switch statements. Like the rest of the constants in this
	 * class, they could also have been coded directly in the model
	 */
	enum Activity implements Publishable {
		/** The agent's resting. */
		RESTING("Resting"),
		/** The agent is going home. */
		LEAVING_WORK("LeavingWork"),
		/**
		 * The agent is in the toilet. You don't want more details, I'm sure.
		 */
		IN_TOILET("InTheToilet"),
		/** The agent is heading for the toilet. */
		GOING_2_TOILET("Going2Toilet"),
		/** The agent is headed for his desk. */
		GOING_2_DESK("Going2Desk"),
		/** The agent is going to a meeting. */
		GOING_2_GLOBAL_MEETING("Going2Meeting"),
		/** The agent is at his desk. */
		AT_DESK("AtDesk"),
		/** The agent is entering into the toilet after queuing. */
		ENTERING_TOILET("EnteringToilet"),
		/** The agent is at a meeting. */
		AT_MEETING("AtMeeting"),
		/** The agent is entering in a meeting. */
		ENTERING_MEETING("EnteringMeeting");

		/** Human readable desription of the activity. */
		private String description;

		/**
		 * Get the description of the activity.
		 * 
		 * @return a string describing the activity
		 */
		public String toString() {
			return description;
		}

		/**
		 * Build an instance of Activity which keeps a human readable
		 * description for when it's flattened.
		 * 
		 * @param description the humanreadable description of the activity
		 */
		private Activity(final String description) {
			this.description = description;
		}

		/**
		 * Flatten the description of the activity.
		 * 
		 * @return a flatenned text with the description of the activity
		 */
		public FlatData flatten() {
			return new Text(description).flatten();
		}
	}
}
