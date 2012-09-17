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

import de.nec.nle.siafu.types.EasyTime;
import de.nec.nle.siafu.types.FlatData;
import de.nec.nle.siafu.types.Publishable;
import de.nec.nle.siafu.types.Text;
import de.nec.nle.siafu.types.TimePeriod;

/**
 * A list of the constants used by this simulation. None of this is strictly
 * needed, but it makes referring to certain values easier and less error
 * prone.
 * 
 * @author Miquel Martin
 */
public class Constants {
	/**
	 * In the mode, the simulation will pause at the demo events. If you are
	 * going to run this simulation without a GUI, remember to disable this,
	 * to keep the simulation from pausing.
	 */
	public static final boolean DEMO_MODE = true;
	
	/** Simulation steps. */
	enum SimSteps {
		/** Simulation start. */
		ZERO,
		/** Pietro goes to the cinema. */
		FIRST,
		/** Pietro reaches the cinema. */
		SECOND,
		/** Pietro awaits in the cinema and Teresa goes towards it. */
		THIRD,
		/** Teresa is walking to the cinema. */
		FOURTH,
		/** Teresa stands next to Pietro in the cinema. */
		FIFTH,
		/** The movie ends, the couple goes to a Cafe. */
		SIXTH,
		/**
		 * Restaurant recommendation arrives (outside the simulation) so they
		 * stop.
		 */
		SEVENTH,
		/** They head for a restaurant. */
		EIGHT,
		/** Night ends, simulation restarts. */
		NINTH;
	}

	/** The period in which boats leave port. */
	public static final TimePeriod ANCHORED_BOAT_LEAVE_TIME =
			new TimePeriod(new EasyTime(8, 0), new EasyTime(13, 0));

	/** The period in which boats arrive at port. */
	public static final TimePeriod ANCHORED_BOAT_ARRIVAL_PERIOD =
			new TimePeriod(new EasyTime(20, 0), new EasyTime(1, 0));

	/** The time of the race. Notice that it shifts from day to day! */
	public static final EasyTime RACE_TIME = new EasyTime(17, 0);

	/** The amount of boats in the race. */
	public static final int AMOUNT_RACING_BOAT = 5;

	/** The amonut of boats that anchor at night. */
	public static final int AMOUNT_ANCHORING_BOAT = 5;

	/**
	 * Separation Pietro-Teresa at cinema. Decrease according to cultural
	 * limits.
	 */
	public static final int TERESA_SEPARATION = 5;

	/** Car speed. */
	public static final int CAR_SPEED = 5;

	/**
	 * Probability that the agent decides to move by car.
	 */
	public static final float P_GO_BY_CAR = 0.3f;

	/**
	 * Probability that the agent leaves his place and goes to another
	 * building.
	 */
	public static final float P_CHANGE_BUILDING = 0.3f;

	/**
	 * Probability that the agent hesitates right before stepping into a
	 * building.
	 */
	public static final float P_HESITATE = 0.9f;

	/** Probability that a boat anchors at the docks, after nightfall. */
	public static final float P_BOAT_ANCHOR = 0.02f;

	/** Probability that a boat departs the docks, after sunrise. */
	public static final float P_BOAT_LEAVE = 0.02f;

	/** Maximum race start delay. */
	public static final int RACE_DELAY_MIN = 20;

	/** Speed of a racing boat. */
	public static final int BOAT_RACING_SPEED = 7;

	/** Number of minutes in an hour. Ahem. */
	public static final double MIN_PER_HOUR = 60d;

	/** Variance of the gaussian traffic distribution. */
	public static final int TRAFFIC_VARIANCE = 20;

	/** Mean of the gaussian traffic distribution. */
	public static final int TRAFFIC_MEAN = 14;

	/** Amplitude of the gaussian traffic distribution. */
	public static final double TRAFFIC_AMPLITUDE = 0.001d;

	/** The time at which Pietro goes to the cinema to meet Teresa. */
	public static final EasyTime TIME_PIETRO_TO_CINEMA =
			new EasyTime(15, 30);

	/** The time at which Teresa goes to the cinema to meet Pietro. */
	public static final EasyTime TIME_TERESA_TO_CINEMA =
			new EasyTime(17, 0);

	/** Time in which both Pietro and Teresa head for the cafe. */
	public static final EasyTime TIME_GO_TO_CAFE = new EasyTime(21, 0);

	/** Time in which both Pietro and Teresa go for dinner out. */
	public static final EasyTime TIME_STOP_DINNER = new EasyTime(21, 30);

	/**
	 * Population size, that is, how many agents should inhabit this
	 * simulation.
	 */
	public static final int POPULATION = 150;

	/** A small maximum distance to wander off a main point when wanderng. */
	public static final int SMALL_WANDER = 10;

	/** A big distance to wander off a main point when wanderng. */
	public static final int BIG_WANDER = 20;

	/** Probability that the agent has a car. */
	public static final float PROB_HAS_CAR = 0.3f;

	/** 120min time blur. */
	public static final int TWO_HOUR_BLUR = 120;

	/** 60min time blur. */
	public static final int ONE_HOUR_BLUR = 60;

	/** 30min time blur. */
	public static final int HALF_HOUR_BLUR = 30;

	/**
	 * The names of the fields in each agent object.
	 */
	static class Fields {
		/** The agent's current activity. */
		public static final String ACTIVITY = "Activity";
	}

	/* FIXME the activity doesn't show the actual description */
	/**
	 * List of possible activies. This is implemented as an enum because it
	 * helps us in switch statements. Like the rest of the constants in this
	 * class, they could also have been coded directly in the model
	 */
	enum Activity implements Publishable {
		/** The agent's (person or boat) is waiting. */
		WAITING("Waiting"),
		/** The agent's walking. */
		WALKING("Walking"),
		/** The boat is at the docks for the night. */
		ANCHORED("Anchored"),
		/** The boat is on the way to docking for the night. */
		ANCHORING("Anchoring"),
		/** The boat is leaving port. */
		LEAVING("Leaving"),
		/** The boat is out on the sea. */
		SAILING("Sailing"),
		/** The boat is in the middle of the race. */
		RACING("Racing"),
		/** The boat is decelarating after crossing the arrival buoys. */
		DECELERATING("Decelerating"),
		/** The boat has ended the race and has decelerated. */
		ARRIVED("Arrived");

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
