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

import de.nec.nle.siafu.types.FlatData;
import de.nec.nle.siafu.types.Publishable;
import de.nec.nle.siafu.types.Text;

import java.util.ArrayList;

/**
 * A list of the constants used by this simulation. None of this is strictly
 * needed, but it makes referring to certain values easier and less error
 * prone.
 * 
 * @author Miquel Martin
 */
public class Constants {
	/**
	 * Probability that the agent hesitates right before stepping into a
	 * building.
	 */
	public static final float P_HESITATE = 0.9f;

	/** Number of minutes in an hour. Ahem. */
	public static final double MIN_PER_HOUR = 60d;

	/** Variance of the gaussian traffic distribution. */
	public static final int TRAFFIC_VARIANCE = 20;

	/** Mean of the gaussian traffic distribution. */
	public static final int TRAFFIC_MEAN = 14;

	/** Amplitude of the gaussian traffic distribution. */
	public static final double TRAFFIC_AMPLITUDE = 0.001d;

	/** Columns between Ralf and Andy's start. */
	public static final int RALF_ANDY_DIST_J = 7;

	/** Rows between Ralf and Andy's start. */
	public static final int RALF_ANDY_DIST_I = 15;

	/** Car speed. */
	public static final int CAR_SPEED = 5;

	/**
	 * Probability that the agent decides to move by car.
	 */
	public static final float P_GO_BY_CAR = 0.7f;

	/**
	 * Probability that the agent leaves his place and goes to another
	 * building.
	 */
	public static final float P_CHANGE_BUILDING = 0.3f;

	/**
	 * The names of the fields in each agent object.
	 */
	static class Fields {

		/** The agent's current activity. */
		public static final String ACTIVITY = "Activity";

		/** Agent's age. */
		public static final String AGE = "Age";

		/** Agent's preferred cuisine. */
		public static final String CUISINE = "Cuisine";

		/** Agent's work area. */
		public static final String WORKAREA = "WorkArea";

		/** Agent's gender. */
		public static final String GENDER = "Gender";

		/** Agent's acquisitive level. */
		public static final String ACQUISITIVELEVEL = "AcquisitiveLevel";

		/** Agents prefered music gender. */
		public static final String MUSICGENDER = "MusicGender";

	}

	/**
	 * Enumeration of the possible activities agents engage in.
	 */
	enum Activity implements Publishable {
		/** Walking. */
		WALKING("Walking"),
		/** Working. */
		WAITING("Working");
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

	/** List of possible cuisine types. */
	public static final ArrayList<Text> CUISINE_TYPES =
			new ArrayList<Text>();

	/** A list with the possible genders, to ensure uniform spelling/form. */
	public static final ArrayList<Text> GENDER_TYPES =
			new ArrayList<Text>();

	/** List of possible languages that an agent speaks. */
	public static final ArrayList<Text> LANGUAGE_TYPES =
			new ArrayList<Text>();

	/** List of possible work areas. */
	public static final ArrayList<Text> WORKAREA_TYPES =
			new ArrayList<Text>();

	/** List of positive acquisitive levels. */
	public static final ArrayList<Text> ACQUISITIVELEVEL_TYPES =
			new ArrayList<Text>();

	/** List of possible music gender types. */
	public static final ArrayList<Text> MUSICGENDER_TYPES =
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

		WORKAREA_TYPES.add(new Text("engineer"));
		WORKAREA_TYPES.add(new Text("athlete"));
		WORKAREA_TYPES.add(new Text("housewife"));
		WORKAREA_TYPES.add(new Text("carpenter"));
		WORKAREA_TYPES.add(new Text("plumber"));
		WORKAREA_TYPES.add(new Text("shopkeeper"));
		WORKAREA_TYPES.add(new Text("researcher"));
		WORKAREA_TYPES.add(new Text("geek"));
		WORKAREA_TYPES.add(new Text("enologist"));

		ACQUISITIVELEVEL_TYPES.add(new Text("none"));
		ACQUISITIVELEVEL_TYPES.add(new Text("low"));
		ACQUISITIVELEVEL_TYPES.add(new Text("average"));
		ACQUISITIVELEVEL_TYPES.add(new Text("high"));
		ACQUISITIVELEVEL_TYPES.add(new Text("extravagant"));

		MUSICGENDER_TYPES.add(new Text("Rock"));
		MUSICGENDER_TYPES.add(new Text("Classic"));
		MUSICGENDER_TYPES.add(new Text("Dance"));
		MUSICGENDER_TYPES.add(new Text("Folk"));
		MUSICGENDER_TYPES.add(new Text("Electronic"));
	}

}
