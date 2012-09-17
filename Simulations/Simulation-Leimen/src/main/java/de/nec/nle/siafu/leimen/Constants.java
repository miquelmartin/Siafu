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

import java.util.ArrayList;

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
	/**
	 * Population size, that is, how many agents should inhabit this
	 * simulation.
	 */
	public static final int POPULATION = 100;

	/** A small maximum distance to wander off a main point when wanderng. */
	public static final int SMALL_WANDER = 80;

	/** A big distance to wander off a main point when wanderng. */
	public static final int BIG_WANDER = 200;

	/** Probability that the agent has a car. */
	public static final float PROB_HAS_CAR = 0.3f;

	/** Minimum age of an agent. */
	public static final int MIN_AGE = 10;

	/** Maximum age of an agent. */
	public static final int MAX_AGE = 60;

	/** Average hour of the day for work start. */
	public static final int AVG_WORK_START = 7;

	/** Average number of hours an agent works. */
	public static final int AVG_WORK_TIME = 8;

	/** Average number of hours an agent sleeps. */
	public static final int AVG_SLEEP_TIME = 8;

	/** 120min time blur. */
	public static final int TWO_HOUR_BLUR = 120;

	/** 60min time blur. */
	public static final int ONE_HOUR_BLUR = 60;

	/** 30min time blur. */
	public static final int HALF_HOUR_BLUR = 30;

	/** Minimum hours between leaving work and going party. */
	public static final int WORK_PARTY_MIN_TIME = 2;

	/** Hours would someone overwork per point in the party animal index. */
	public static final int WORKAHOLIC_FACTOR = 1;

	/** Hours would someone go party per point in the party animal index. */
	public static final int PARTY_ANIMAL_FACTOR = 1;

	/**
	 * The names of the fields in each agent object.
	 */
	static class Fields {
		/** Agent's age. */
		public static final String AGE = "Age";

		/** Agent's preferred cuisine. */
		public static final String CUISINE = "Cuisine";

		/** Agent's language. */
		public static final String LANGUAGE = "Language";

		/** Agent's gender. */
		public static final String GENDER = "Gender";

		/** How much of a party animal the agent is. */
		public static final String PARTY_ANIMAL = "Party animal";

		/** How much of a workaholic the agent is. */
		public static final String WORKAHOLIC = "Workaholism";

		/** Start working time of the agent. */
		public static final String WORK_START = "Work start";

		/** Finish working time of the agent. */
		public static final String WORK_END = "Work end";

		/** Start and finish sleeping period of the agent. */
		public static final String SLEEP_PERIOD = "Sleep pariod";

		/** Whether the agent has a car. */
		public static final String HAS_CAR = "Owns a car";

		/** The agent's home. */
		public static final String HOME = "Home";

		/** The agent's office. */
		public static final String OFFICE = "Office";

		/** The agent's current activity. */
		public static final String ACTIVITY = "Activity";

		/** Time at which the agent will go party (if WILL_GO_PARTY is true). */
		public static final String PARTY_START = "Party start time";

		/** Time at which this agent will end the party and go home. */
		public static final String PARTY_END = "Party end time";

		/** Time at which this agent will end the party and go home. */
		public static final String PARTY_PLACE = "Party place";

		/** Whether this user will go party in this wake period or not. */
		public static final String WILL_GO_PARTY = "Will go party";
	}

	/* FIXME the activity doesn't show the actual description */
	/**
	 * List of possible activies. This is implemented as an enum because it
	 * helps us in switch statements. Like the rest of the constants in this
	 * class, they could also have been coded directly in the model
	 */
	enum Activity implements Publishable {
		/** The agent's asleep. */
		ASLEEP("Asleep"),
		/** The agent's at work, presumably working. */
		WORKING("Working"),
		/** The agent's walking wihtout a specific purpose. */
		WALKING_ON_STREET("Walking on the street"),

		/** The agen'ts on the way to work. */
		GOING_TO_WORK("Going to work"),
		/** The agent's at home, doing homey things. */
		AT_HOME("At home"),
		/** The agent's on the way to a party. */
		GOING_TO_PARTY("Going to a party"),
		/** The agent's at a party. */
		AT_PARTY("At a party"),
		/** The agent's on the way home. */
		GOING_HOME("Going home");
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

	/** List of cuisine preferences. */
	public static final ArrayList<Text> CUISINE_TYPES = new ArrayList<Text>();

	/** A list with the possible genders, to ensure uniform spelling/form. */
	public static final ArrayList<Text> GENDER_TYPES = new ArrayList<Text>();

	/** List of possible languages that an agent speaks. */
	public static final ArrayList<Text> LANGUAGE_TYPES =
			new ArrayList<Text>();

	/** How much of a workaholic the user is. */
	public static final ArrayList<Text> WORKAHOLIC_TYPES =
			new ArrayList<Text>();

	/** How much of a party animal the user is. */
	public static final ArrayList<Text> PARTY_ANIMAL_TYPES =
			new ArrayList<Text>();

	static {
		CUISINE_TYPES.add(new Text("Italian"));
		CUISINE_TYPES.add(new Text("Thai"));
		CUISINE_TYPES.add(new Text("Spanish"));
		CUISINE_TYPES.add(new Text("French"));
		CUISINE_TYPES.add(new Text("African"));
		CUISINE_TYPES.add(new Text("Indian"));
		CUISINE_TYPES.add(new Text("German"));

		GENDER_TYPES.add(new Text("Male"));
		GENDER_TYPES.add(new Text("Female"));

		LANGUAGE_TYPES.add(new Text("Catalan"));
		LANGUAGE_TYPES.add(new Text("Italian"));
		LANGUAGE_TYPES.add(new Text("Spanish"));
		LANGUAGE_TYPES.add(new Text("French"));
		LANGUAGE_TYPES.add(new Text("German"));
		LANGUAGE_TYPES.add(new Text("Suomi"));
		LANGUAGE_TYPES.add(new Text("Swedish"));
		LANGUAGE_TYPES.add(new Text("Czech"));
		LANGUAGE_TYPES.add(new Text("Dutch"));

		PARTY_ANIMAL_TYPES.add(new Text("Hermit"));
		PARTY_ANIMAL_TYPES.add(new Text("Seldom"));
		PARTY_ANIMAL_TYPES.add(new Text("Average"));
		PARTY_ANIMAL_TYPES.add(new Text("Often"));
		PARTY_ANIMAL_TYPES.add(new Text("JustSayWhere"));

		WORKAHOLIC_TYPES.add(new Text("Slacker"));
		WORKAHOLIC_TYPES.add(new Text("Easygoing"));
		WORKAHOLIC_TYPES.add(new Text("Average"));
		WORKAHOLIC_TYPES.add(new Text("Hardworker"));
		WORKAHOLIC_TYPES.add(new Text("Terminal"));
	}
}
