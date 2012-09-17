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

import static de.nec.nle.siafu.valencia.Constants.DEMO_MODE;
import static de.nec.nle.siafu.valencia.Constants.AMOUNT_ANCHORING_BOAT;
import static de.nec.nle.siafu.valencia.Constants.AMOUNT_RACING_BOAT;
import static de.nec.nle.siafu.valencia.Constants.ANCHORED_BOAT_ARRIVAL_PERIOD;
import static de.nec.nle.siafu.valencia.Constants.ANCHORED_BOAT_LEAVE_TIME;
import static de.nec.nle.siafu.valencia.Constants.BOAT_RACING_SPEED;
import static de.nec.nle.siafu.valencia.Constants.CAR_SPEED;
import static de.nec.nle.siafu.valencia.Constants.MIN_PER_HOUR;
import static de.nec.nle.siafu.valencia.Constants.POPULATION;
import static de.nec.nle.siafu.valencia.Constants.P_BOAT_ANCHOR;
import static de.nec.nle.siafu.valencia.Constants.P_BOAT_LEAVE;
import static de.nec.nle.siafu.valencia.Constants.P_GO_BY_CAR;
import static de.nec.nle.siafu.valencia.Constants.P_HESITATE;
import static de.nec.nle.siafu.valencia.Constants.RACE_DELAY_MIN;
import static de.nec.nle.siafu.valencia.Constants.RACE_TIME;
import static de.nec.nle.siafu.valencia.Constants.SMALL_WANDER;
import static de.nec.nle.siafu.valencia.Constants.TERESA_SEPARATION;
import static de.nec.nle.siafu.valencia.Constants.TIME_GO_TO_CAFE;
import static de.nec.nle.siafu.valencia.Constants.TIME_PIETRO_TO_CINEMA;
import static de.nec.nle.siafu.valencia.Constants.TIME_STOP_DINNER;
import static de.nec.nle.siafu.valencia.Constants.TIME_TERESA_TO_CINEMA;
import static de.nec.nle.siafu.valencia.Constants.TRAFFIC_AMPLITUDE;
import static de.nec.nle.siafu.valencia.Constants.TRAFFIC_MEAN;
import static de.nec.nle.siafu.valencia.Constants.TRAFFIC_VARIANCE;
import static de.nec.nle.siafu.valencia.Constants.Fields.ACTIVITY;
import static de.nec.nle.siafu.valencia.Constants.SimSteps.EIGHT;
import static de.nec.nle.siafu.valencia.Constants.SimSteps.FIFTH;
import static de.nec.nle.siafu.valencia.Constants.SimSteps.FIRST;
import static de.nec.nle.siafu.valencia.Constants.SimSteps.FOURTH;
import static de.nec.nle.siafu.valencia.Constants.SimSteps.NINTH;
import static de.nec.nle.siafu.valencia.Constants.SimSteps.SECOND;
import static de.nec.nle.siafu.valencia.Constants.SimSteps.SEVENTH;
import static de.nec.nle.siafu.valencia.Constants.SimSteps.SIXTH;
import static de.nec.nle.siafu.valencia.Constants.SimSteps.THIRD;
import static de.nec.nle.siafu.valencia.Constants.SimSteps.ZERO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import de.nec.nle.siafu.behaviormodels.BaseAgentModel;
import de.nec.nle.siafu.exceptions.GUINotReadyException;
import de.nec.nle.siafu.exceptions.PlaceNotFoundException;
import de.nec.nle.siafu.exceptions.PlaceTypeUndefinedException;
import de.nec.nle.siafu.exceptions.UnknownContextException;
import de.nec.nle.siafu.graphics.markers.BalloonMarker;
import de.nec.nle.siafu.graphics.markers.Marker;
import de.nec.nle.siafu.graphics.markers.SpotMarker;
import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.BinaryOverlay;
import de.nec.nle.siafu.model.Place;
import de.nec.nle.siafu.model.Position;
import de.nec.nle.siafu.model.World;
import de.nec.nle.siafu.types.BooleanType;
import de.nec.nle.siafu.types.EasyTime;
import de.nec.nle.siafu.types.Text;
import de.nec.nle.siafu.valencia.Constants.Activity;
import de.nec.nle.siafu.valencia.Constants.SimSteps;

/**
 * <p>
 * This class defined the behavior of agents in Valencia. There's three
 * distinct types:
 * </p>
 * <ul>
 * <li>Teresa and Pietro are the agents whose context we wanted to monitor.
 * If DEMO_MODE is set to false, they won't do anything. If it is set to on,
 * then the demo script unfolds. In the demo script, Pietro goes to the
 * cinema, and awaits for Teresa. Teresa then heads for the cinema, and the
 * Boats from the America's cup end the race at that time, generating an
 * event. Teresa and Pietro finally meet in the cinema, where they watch a
 * movie, and eventually walk away. Halfway through, a restaurant
 * recommendation arrives, and they head for one of the suggested places for
 * dinner. Eventually, they walk home. Separately. Sorry.</li>
 * <li>Boats race in the morning, full speed towards the arrival buoys, and
 * then slow down, disappearing on the edge of the map. Late at night, they go
 * to port, sailing away again in the morning.</li>
 * <li>The rest of the population goes from one place to another randomly.
 * </ul>
 * 
 * 
 * @author Miquel Martin
 * 
 */
public class AgentModel extends BaseAgentModel {

	/**
	 * Marks the steps in the simulation.
	 */
	private SimSteps simulationStep = ZERO;

	/** The agent playing Teresa. */
	private Agent teresa;

	/** The agent playing Pietro. */
	private Agent pietro;

	/** The boat that will loose. Yeap. The Siafu Sail cup is rigged. */
	private Agent looser;

	/** Pietro's initial position. */
	private Position startPietro;

	/** Teresa's initial position. */
	private Position startTeresa;

	/** Pietro and Teresa's cafe of choice. */
	private Place cafe;

	/** Pietro and Teresa's restaurant of choice. */
	private Place restaurant;

	/** Pietro and Teresa's cinema of choice. */
	private Place cinema;

	/**
	 * True if we have already acted on the race end for the purposes of news
	 * push (part of the demo, not of the simulation).
	 */
	private boolean raceEndNotified;

	/** Types of boat sprites. */
	private String[] boatTypes =
			new String[] {"BoatBlue", "BoatRed", "BoatYellow",
					"BoatGreen", "BoatPink"};

	/** The racing boats. */
	private ArrayList<Agent> racingBoats;

	/**
	 * The boats that anchor at night. Ok, confession, they are not the same.
	 */
	private ArrayList<Agent> anchoringBoats;

	/** Now. Refreshed every iteration. */
	private EasyTime now;

	/**
	 * Instantiate the agent model.
	 * 
	 * @param world the simulated world
	 */
	public AgentModel(final World world) {
		super(world);
	}

	/** A random number generator. */
	private static final Random RAND = new Random();

	/**
	 * Create the agents for the simulation, namely, Teresa, Pietro, the
	 * racing boats, the anchoring boats and the zombie population.
	 * 
	 * @return the created agents
	 */
	public ArrayList<Agent> createAgents() {
		System.out.println("Creating " + POPULATION + " people.");
		ArrayList<Agent> people =
				AgentGenerator.createRandomPopulation(POPULATION, world);

		for (Agent a : people) {
			a.set(ACTIVITY, Activity.WAITING);
			a.setSpeed(1 + RAND.nextInt(2));
			a.setVisible(false);
		}

		// Kidnap two guys and make them our special guys
		teresa = people.get(0);
		pietro = people.get(1);

		try {
			cinema = world.getRandomPlaceOfType("Cinema");
			restaurant = world.getPlaceByName("Restaurant-417.388");

			Iterator<Place> pIt =
					world.getPlacesOfType("Restaurant").iterator();

			pIt.next().setName("CasaSabrosa");
			pIt.next().setName("BurgerKing");
			pIt.next().setName("Eguzki");

			cafe = world.getPlaceByName("Cafe-255.412");

			pIt = world.getPlacesOfType("Cafe").iterator();

			pIt.next().setName("RioGrande");
			pIt.next().setName("Bolos");

			startTeresa =
					world.getPlacesOfType("StartTeresa").iterator().next()
							.getPos();
			startPietro =
					world.getPlacesOfType("StartPietro").iterator().next()
							.getPos();
		} catch (PlaceNotFoundException e) {
			throw new RuntimeException(e);
		}
		teresa.setName("Teresa");
		teresa.setPos(startTeresa);
		teresa.setImage("HumanYellow");
		teresa.setVisible(true);
		teresa.setSpeed(2);
		teresa.set("Language", new Text("German"));

		pietro.setName("Pietro");
		pietro.setPos(startPietro);
		pietro.setImage("HumanBlue");
		pietro.setVisible(true);
		pietro.setSpeed(2);
		pietro.set("Language", new Text("English"));

		// Kidnap 5 boats and make them racing boats
		racingBoats = new ArrayList<Agent>(AMOUNT_RACING_BOAT);

		for (int i = 2; i < 2 + AMOUNT_RACING_BOAT; i++) {
			Agent boat = people.get(i);
			boat.setName("Boat-" + i);
			Position start;
			try {
				start =
						world.getRandomPlaceOfType("EastBoatStart")
								.getPos();
			} catch (PlaceNotFoundException e) {
				throw new RuntimeException(e);
			}
			boat.setPos(start);
			boat.setVisible(false);
			boat.setSpeed(BOAT_RACING_SPEED);
			boat.setImage(boatTypes[RAND.nextInt(boatTypes.length)]);
			boat.set(ACTIVITY, Activity.WAITING);
			racingBoats.add(boat);
		}

		// Kidnap 5 boats and make them sunset anchoring boats
		anchoringBoats = new ArrayList<Agent>(AMOUNT_ANCHORING_BOAT);

		for (int i = 2 + AMOUNT_RACING_BOAT; i < 2 + AMOUNT_RACING_BOAT
				+ AMOUNT_ANCHORING_BOAT; i++) {
			Agent boat = people.get(i);
			boat.setName("Boat-" + i);
			resetAnchoringBoat(boat);
			boat.setImage(boatTypes[RAND.nextInt(boatTypes.length)]);
			anchoringBoats.add(boat);
		}

		looser = racingBoats.get(2);
		return people;
	}

	/**
	 * Get the anchor boats back at the start.
	 * 
	 * @param boat the boat to reset
	 */
	private void resetAnchoringBoat(final Agent boat) {
		Position start;
		try {
			start = world.getRandomPlaceOfType("WestBoatStart").getPos();
		} catch (PlaceNotFoundException e) {
			throw new RuntimeException(e);
		}
		boat.setPos(start);
		boat.setVisible(false);
		boat.setSpeed(1);
		boat.set(ACTIVITY, Activity.SAILING);
	}

	/**
	 * Move the agent from one building to another one. By car, if the agent's
	 * lucky.
	 * 
	 * @param a the agent to move
	 */
	protected void changeBuilding(final Agent a) {
		a.setVisible(true);

		if (RAND.nextFloat() < P_GO_BY_CAR) {
			a.setImage("CarGreen");
			a.setSpeed(CAR_SPEED);
		}
		try {
			a.setDestination(world.getRandomPlaceOfType("Other"));
		} catch (PlaceNotFoundException e) {
			throw new RuntimeException(
					"You didn't define Other place types", e);
		}

		a.set(ACTIVITY, Activity.WALKING);
	}

	/**
	 * Handle all the agents in the simulation. Normal users wander around
	 * buildings, boats race or anchor, and Pietro and Teresa move about the
	 * demo steps, if DEMO is set to true.
	 * 
	 * @param agents the collection of agents to handle
	 */
	public void doIteration(final Collection<Agent> agents) {
		Calendar time = world.getTime();
		now =
				new EasyTime(time.get(Calendar.HOUR_OF_DAY), time
						.get(Calendar.MINUTE));

		for (Agent a : agents) {
			if (!a.isOnAuto() || a.equals(teresa) || a.equals(pietro)) {
				continue; // This guy's being managed by the user interface
			}

			if (racingBoats.contains(a)) {
				handleRacingBoat(a);
			} else if (anchoringBoats.contains(a)) {
				handleAnchoringBoat(a);
			} else {
				handlePerson(a);
			}

			if (DEMO_MODE) {
				try {
					if (!raceEndNotified
							&& a.equals(looser)
							&& new BooleanType(a
									.getContext("AfterArrival"))
									.getValue()) {
						world.stopSpinning(true);
						raceEndNotified = true;
					}
				} catch (UnknownContextException e) {
					throw new RuntimeException(
							"AfterArrival is not an overlay", e);
				}
			}
		}

		if (DEMO_MODE) {
			try {
				Marker markTeresa = new SpotMarker(teresa, "#1111CC");
				Marker markPietro = new SpotMarker(pietro, "#CCCC11");
				switch (simulationStep) {
				case ZERO:
					try {
						world.addMarker(markPietro);
						world.addMarker(markTeresa);
						simulationStep = FIRST;
					} catch (GUINotReadyException e) {
						// Do nothing. We'll try again later.
						return;
					}
					break;
				case FIRST:
					if (now.isAfter(TIME_PIETRO_TO_CINEMA)) {
						world.stopSpinning(true);
						markPietro = new BalloonMarker(pietro, "#CCCC11");
						world.addMarker(markPietro);
						try {
							pietro.setDestination(world
									.getRandomPlaceOfType("Cinema"));
						} catch (PlaceNotFoundException e) {
							throw new RuntimeException(e);
						}
						simulationStep = SECOND;
					}
					break;
				case SECOND:
					if (pietro.isAtDestination()) {
						world.stopSpinning(true);
						simulationStep = THIRD;
					}
					break;
				case THIRD:
					pietro.wanderAround(cinema, SMALL_WANDER);
					if (now.isAfter(TIME_TERESA_TO_CINEMA)) {
						// world.stopSpinning(true);
						markTeresa = new BalloonMarker(teresa, "#1111CC");
						world.addMarker(markTeresa);
						teresa.setDestination(cinema);
						simulationStep = FOURTH;
					}
					break;
				case FOURTH:
					pietro.wanderAround(cinema, SMALL_WANDER);
					if (teresa.isAtDestination()) {
						world.stopSpinning(true);
						simulationStep = FIFTH;
					}
					break;
				case FIFTH:
					Position nextToTeresa = new Position(teresa.getPos());
					nextToTeresa.setCol(nextToTeresa.getCol()
							- TERESA_SEPARATION);
					nextToTeresa.setRow(nextToTeresa.getRow()
							- TERESA_SEPARATION);
					pietro.setDestination(new Place("CloseToTeresa",
							nextToTeresa, world, pietro.getPos()));
					simulationStep = SIXTH;
					break;
				case SIXTH:
					if (now.isAfter(TIME_GO_TO_CAFE)) {
						pietro.setDestination(cafe);
						teresa.setDestination(cafe);
						simulationStep = SEVENTH;
					}
					break;
				case SEVENTH:
					if (now.isAfter(TIME_STOP_DINNER)) {
						world.stopSpinning(true);
						simulationStep = EIGHT;
					}
					break;
				case EIGHT:
					try {
						for (Place rst : world
								.getPlacesOfType("Restaurant")) {
							Marker mark =
									new BalloonMarker(rst, "#11CCCC");
							world.addMarker(mark);
						}
					} catch (PlaceTypeUndefinedException e) {
						throw new RuntimeException(e);
					}
					teresa.setDestination(restaurant);
					pietro.setDestination(restaurant);
					simulationStep = NINTH;

					break;
				case NINTH:
					if (now.isBefore(new EasyTime(2, 0))) {
						simulationStep = ZERO;
						pietro.setDestination(new Place("startP",
								startPietro, world, pietro.getPos()));
						teresa.setDestination(new Place("startT",
								startTeresa, world, teresa.getPos()));
						raceEndNotified = false;
						world.unMarkAll();
					}
					break;
				default:
					throw new RuntimeException("Invalid simulation state");
				}
			} catch (GUINotReadyException e) {
				System.out.println("GUI not ready");
			}
		}
	}

	/**
	 * Handle racing boats. They speed towards the arrival buoys, and slowly
	 * decelarate after reching it.
	 * 
	 * @param boat the boat we want to move.
	 */
	private void handleRacingBoat(final Agent boat) {
		switch ((Activity) boat.get(ACTIVITY)) {
		case WAITING:
			if (!boat.equals(looser)) {
				if (now.isAfter(RACE_TIME)) {
					boat.setVisible(true);
					boat.set(ACTIVITY, Activity.RACING);
					boat.setSpeed(BOAT_RACING_SPEED);
					Place destination;
					try {
						destination =
								world.getRandomPlaceOfType("EastBoatEnd");
					} catch (PlaceNotFoundException e) {
						throw new RuntimeException(e);
					}
					boat.setDestination(destination);
					RACE_TIME.shift(0, RAND.nextInt(RACE_DELAY_MIN));
				}
			} else {
				boolean allOthersRacing = true;
				for (Agent b : racingBoats) {
					if (!b.equals(looser)
							&& b.get(ACTIVITY).equals(Activity.WAITING)) {
						allOthersRacing = false;
					}
				}
				if (allOthersRacing && now.isAfter(RACE_TIME)) {
					boat.setVisible(true);
					boat.set(ACTIVITY, Activity.RACING);
					boat.setSpeed(BOAT_RACING_SPEED);
					Place destination;
					try {
						destination =
								world.getRandomPlaceOfType("EastBoatEnd");
					} catch (PlaceNotFoundException e) {
						throw new RuntimeException(e);
					}
					boat.setDestination(destination);
				}
			}
			break;
		case RACING:
			BinaryOverlay arrival =
					(BinaryOverlay) world.getOverlays()
							.get("AfterArrival");
			boolean arrived = arrival.getValue(boat.getPos()).getValue();
			if (arrived) {
				boat.set(ACTIVITY, Activity.DECELERATING);
			}
			if (boat.isAtDestination()) {
				// FIXME: er.. this shouldn't happen. We fix it here
				Place destination;
				try {
					destination =
							world.getRandomPlaceOfType("EastBoatEnd");
				} catch (PlaceNotFoundException e) {
					throw new RuntimeException(e);
				}
				boat.setDestination(destination);
			}

			break;
		case DECELERATING:
			if (boat.getSpeed() > 1) {
				boat.setSpeed(boat.getSpeed() - 1);
			}
			if (boat.isAtDestination()) {
				boat.setVisible(false);
				boat.set(ACTIVITY, Activity.ARRIVED);
			}
			break;
		case ARRIVED:
			if (now.isBefore(new EasyTime(1, 0))) {
				boat.set(ACTIVITY, Activity.WAITING);
			}
			Position start;
			try {
				start =
						world.getRandomPlaceOfType("EastBoatStart")
								.getPos();
			} catch (PlaceNotFoundException e) {
				throw new RuntimeException(e);
			}
			boat.setPos(start);
			break;

		default:
			throw new RuntimeException("Unknwon Activity "
					+ (Activity) boat.get(ACTIVITY));
		}

	}

	/**
	 * Handle the boats which are anchoring for the night.
	 * 
	 * @param boat the boat we want to anchor
	 */
	private void handleAnchoringBoat(final Agent boat) {
		switch ((Activity) boat.get(ACTIVITY)) {
		case SAILING:
			if (now.isIn(ANCHORED_BOAT_ARRIVAL_PERIOD)
					&& RAND.nextFloat() < P_BOAT_ANCHOR) {
				boat.setVisible(true);

				Place destination = null;
				while (destination == null) {
					try {
						destination =
								world.getRandomPlaceOfType("WestBoatEnd");
					} catch (PlaceNotFoundException e) {
						throw new RuntimeException(e);
					}
					for (Agent otherBoat : anchoringBoats) {
						if (otherBoat.getDestination() == destination) {
							destination = null;
							continue;
						}
					}
				}
				boat.setDestination(destination);
				boat.set(ACTIVITY, Activity.ANCHORING);
			}
			break;
		case ANCHORING:
			if (boat.isAtDestination()) {
				boat.set(ACTIVITY, Activity.ANCHORED);
			}
			break;
		case ANCHORED:
			if (now.isIn(ANCHORED_BOAT_LEAVE_TIME)
					&& RAND.nextFloat() < P_BOAT_LEAVE) {
				try {
					boat.setDestination(world
							.getRandomPlaceOfType("WestBoatStart"));
				} catch (PlaceNotFoundException e) {
					throw new RuntimeException(e);
				}
				boat.set(ACTIVITY, Activity.LEAVING);
			}
			break;
		case LEAVING:
			if (boat.isAtDestination()) {
				resetAnchoringBoat(boat);
			}
			break;
		default:
			throw new RuntimeException("Unknwon Activity "
					+ (Activity) boat.get(ACTIVITY));

		}
	}

	/**
	 * Handle the "zombie" people in the simulation, by walking them from one
	 * building to the next. The traffic distribution is gaussian.
	 * 
	 * @param a the agent to handle
	 */
	private void handlePerson(final Agent a) {
		switch ((Activity) a.get(ACTIVITY)) {
		case WAITING:
			double t =
					now.getHour()
							+ ((double) now.getMinute() / MIN_PER_HOUR);
			double gaussianThreshold =
					TRAFFIC_AMPLITUDE
							* Math.exp(-Math.pow((t - TRAFFIC_MEAN), 2)
									/ TRAFFIC_VARIANCE);

			if (RAND.nextDouble() < gaussianThreshold) {
				changeBuilding(a);
				// p.setAppearance("HumanMagenta");
			}
			break;

		case WALKING:
			if (a.isAtDestination()) {
				if (!(RAND.nextFloat() < P_HESITATE)) {
					// Hesitate on coming in
					a.setVisible(false);
					a.set(ACTIVITY, Activity.WAITING);
				}
				a.setSpeed(1 + RAND.nextInt(2));
			}
			break;
		default:
			throw new RuntimeException("Unable to handle activity "
					+ (Activity) a.get(ACTIVITY));
		}

	}
}
