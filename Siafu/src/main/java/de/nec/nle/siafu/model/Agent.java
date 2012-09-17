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

package de.nec.nle.siafu.model;

import java.util.Collection;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import de.nec.nle.siafu.exceptions.InfoFieldsLockedException;
import de.nec.nle.siafu.exceptions.InitializationRequiredException;
import de.nec.nle.siafu.exceptions.InfoUndefinedException;
import de.nec.nle.siafu.exceptions.PositionUnreachableException;
import de.nec.nle.siafu.exceptions.UnexistingSpriteException;
import de.nec.nle.siafu.exceptions.UnknownContextException;
import de.nec.nle.siafu.types.FlatData;
import de.nec.nle.siafu.types.Publishable;
import de.nec.nle.siafu.types.Text;
import de.nec.nle.siafu.utils.SequentialNamer;

/**
 * Instances of the Agent class represent the simulations Agents. Agents on auto
 * are handled by the behaviour model, and go about their pre-programmed
 * routine. Information intrinsec to the agent can be added by using the
 * {@link #set(String, Publishable)} method.
 * 
 * Each agent can be queried for its context using the
 * {@link #getContext(String)} method, which will return any of three types of
 * inromation, all as instances of the Publishable class:
 * <ul>
 * <li>Information that's intrinsec to the simulation, such as the agent's
 * position or destination
 * <li>Publishable objects that have been stored in the agent's
 * <code>info</code> field
 * <li>The value of an overlay at the agent's position
 * </ul>
 * 
 * @see de.nec.nle.siafu.behaviormodels.BaseAgentModel
 * @author Miquel Martin
 * 
 */
public class Agent implements Trackable, Comparable<Agent> {
	/** A destination, just to make sure it's never null.*/
	private static Place DEFAULT_DESTINATION = null;
	
	private static Place getDefaultPlace(Position pos, World world){
		if (DEFAULT_DESTINATION == null){
			DEFAULT_DESTINATION = new Place("StartingPosition", pos, world);
		}
		return DEFAULT_DESTINATION;
	}
	
	/**
	 * When the agent wanders, he may turn or not. If it does, this is how much.
	 */
	private static final int WANDER_TURN = 3;

	/**
	 * The default soberness for agents. This determines how often they change
	 * direction when wandering around.
	 */
	private static final int DEFAULT_SOBERNESS = 5;

	/** The amount of possible directions an agent can face. */
	private static final int POSSIBLE_DIRECTIONS = 8;

	/** The world this agent belongs to. */
	private static World world;

	/** States whether it's still possible to add fields to info. */
	private static boolean infoFieldsLocked = false;

	/** The name of thefields in the info object of each Agent. */
	private static final SortedSet<String> INFO_FIELDS = new TreeSet<String>();

	/** A random object used to add noise to the behaviour. */
	private static final Random RAND = new Random();

	/**
	 * Whether the auto is being controlled by the simulation (auto) or by the
	 * user, through the GUI or the command interface (not on auto).
	 */
	private Boolean onAuto = true;

	/** The agent's name. */
	private String name;

	// FIXME: this should be an enum
	/** The current direction that the agent is facing. */
	private int dir;

	/**
	 * The speed at which the agent moves. A speed of n means he will move n
	 * positions of the map matrix at every iteration.
	 */
	private int speed = 1;

	/** The current position of the agent. */
	private Position pos;

	/** True if the agent has already reached its destination. */
	private boolean atDestination;

	/**
	 * The place where the agent is headed for. The simulation will
	 * automatically move it in that direction at ecah iteration.
	 */
	private Place destination;

	/**
	 * The name of the image that represents the simulator. The names are the
	 * portion of the filename before the "-" in the files provided in the
	 * simulation data as sprites.
	 */
	private String image;

	/**
	 * The image that the agent had before the current one (<code>image</code>).
	 */
	private String previousImage;

	/**
	 * This Map contains context variables that are particular to the agent, and
	 * don't necessarily depend on the environment. Examples are:
	 * <ul>
	 * <li>Agent preferences
	 * <li>Transported data
	 * <li>Information kept in store by the behavior model
	 * </ul>
	 */
	private SortedMap<String, Publishable> info;

	/**
	 * Whether the agent is visible. Invisible agents will not be drawn on the
	 * simulation.
	 */
	private boolean visible = true;

	/**
	 * A high zPriority means the agent is drawn later, and therefore appears on
	 * top
	 */
	private int zPriority;

	/** 
	 * Resets the info fields that are statically frozen into the agent. This is
	 * required when a simulation is loaded without restarting the virtual machine.
	 */
	public static void resetAgents(){
		infoFieldsLocked=false;
		INFO_FIELDS.clear();
	}
	
	/**
	 * Create an Agent using a place where he will first appear on day 0, and
	 * his appearance. The name is automatically generated using the
	 * SequentialNamer class. The zPriority is set to 0;
	 * 
	 * @param start
	 *            the place where the agent starts its life
	 * @param image
	 *            the image name that represents the agent. This is the portion
	 *            of the image name before the hyphen provided in the sprites
	 *            directory of the simulation data.
	 * @param world
	 *            the world that the agent belongs to. It has to be the same for
	 *            all the agents in the simulation.
	 */
	public Agent(final Position start, final String image, final World world) {
		this(SequentialNamer.getNextName(), start, image, world, 0);
	}

	/**
	 * Create an Agent using a place where he will first appear on day 0, and
	 * his appearance. The name is automatically generated using the
	 * SequentialNamer class. The zPriority is set to 0;
	 * 
	 * @param name
	 *            the agent's name
	 * @param start
	 *            the place where the agent starts its life
	 * @param image
	 *            the image name that represents the agent. This is the portion
	 *            of the image name before the hyphen provided in the sprites
	 *            directory of the simulation data.
	 * @param world
	 *            the world that the agent belongs to. It has to be the same for
	 *            all the agents in the simulation.
	 */
	public Agent(final String name, final Position start, final String image,
			final World world) {
		this(name, start, image, world, 0);
	}

	/**
	 * Create an Agent using a name, a place where he will first appear on day 0
	 * and his appearance.
	 * 
	 * Note that the initial destination of the agent is set to his starting
	 * place.
	 * 
	 * @param name
	 *            the agent's name
	 * @param start
	 *            the initial position of the agent
	 * @param image
	 *            the image name that represents the agent. This is the portion
	 *            of the image name before the hyphen provided in the sprites
	 *            directory of the simulation data.
	 * @param world
	 *            the world that the agent belongs to. It has to be the same for
	 *            all the agents in the simulation.
	 * @param zPriority
	 *            an integer that determines the layer where the agent is drawn,
	 *            should there be more than one agent on the same spot. A high
	 *            priority means that the agent will be on top, a lower one
	 *            means he'll be at the bottom. The values are arbitrary and
	 *            depend only on the relative value between your different
	 *            agents.
	 */
	public Agent(final String name, final Position start, final String image,
			final World world, final int zPriority) {
		basicChecks(world);
		this.name = name;
		this.info = new TreeMap<String, Publishable>();
		this.dir = 0;
		this.image = image;
		this.previousImage = image;
		this.pos = start;
		this.destination = getDefaultPlace(start, world);
		this.atDestination = true;
		this.zPriority = zPriority;
	}

	/**
	 * Check that agents have been initialized and belong to the same world.
	 * 
	 * @param thisAgentsWorld
	 *            the world of this agent.
	 */
	private void basicChecks(final World thisAgentsWorld) {
		if (Agent.world == null) {
			throw new InitializationRequiredException(
					"You need to initialize the agents.");
		}
		if (Agent.world != thisAgentsWorld) {
			throw new RuntimeException(
					"All your users must belong to the same world");
		}
	}

	/**
	 * Prevent new keys from being added to the info field of the agent.
	 * 
	 * @see #set(String, Publishable)
	 * 
	 */
	public static void lockInfoFields() {
		infoFieldsLocked = true;
	}

	/**
	 * Determine if the agent has reached its destination.
	 * 
	 * @return true if it has, false otherwise
	 */
	public boolean isAtDestination() {
		return atDestination;
	}

	/**
	 * Print the name of the agent as its string representation.
	 * 
	 * @return the agent's name
	 */
	public String toString() {
		return getName();
	}

	/**
	 * Set the agent's name.
	 * 
	 * @param name
	 *            the agent's name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Get the agent's name.
	 * 
	 * @return the agent's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the current image of the agent.
	 * 
	 * @return the name of the agent's image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * Get the Z Priority of this agent. A High value means the agent will be
	 * drawn on top. A low value means he might be drawn on by other agents in
	 * the same position.
	 */
	public int getZPriority() {
		return zPriority;
	}


	/**
	 * Get the Z Priority of this agent. A High value means the agent will be
	 * drawn on top. A low value means he might be drawn on by other agents in
	 * the same position.
	 */
	public void setZPriority(final int zPriority) {
		this.zPriority = zPriority;
	}
	
	/**
	 * Change the appearance of the agent to that given by <code>image</code>.
	 * 
	 * @param image
	 *            take the filename of the sprite image you want to use out of
	 *            the ones given in the simulation. The image name is the part
	 *            of the filename before the "-"
	 * @throws UnexistingSpriteException
	 *             if the image is not available in this simulation
	 */
	public void setImage(final String image) {
		if (!world.getAvailableSprites().contains(image)) {
			throw new UnexistingSpriteException(image);
		}
		this.previousImage = this.image;
		this.image = image;
	}

	/**
	 * Return the agent to its previous appearance.
	 */
	public void setPreviousImage() {
		this.image = this.previousImage;
	}

	/**
	 * Get the destination of the agent. The returned place might be a temporary
	 * one, not defined in the world model. This occurs when the GUI or the
	 * external command interface have been used to move the agent somewhere.
	 * 
	 * @return a place for which the agent is headed, or null if the agent is
	 *         already at his destination.
	 */
	public Place getDestination() {
		return destination;
	}

	/**
	 * Get the speed at which the agent's moving.
	 * 
	 * @return the agent's speed
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * Set the speed at which the agent will move. A speed of n means that the
	 * agent will move n grid positions in an iteration.
	 * 
	 * @param speed
	 *            an Integer number with the number of grid steps to walk at
	 *            every iteration
	 */
	public void setSpeed(final int speed) {
		this.speed = speed;
	}

	/**
	 * Set the destination of the agent.
	 * 
	 * @param destination
	 *            the place where the agent will walk towards
	 */
	public void setDestination(final Place destination) {
		if (this.destination != null && this.destination.equals(destination)) {
			return;
		} else {
			atDestination = false;
			this.destination = destination;
		}
	}

	/**
	 * Get the direction in which the agent is currently facing.
	 * 
	 * @return a number between 0 and 7 (both included) representing the
	 *         direction, where 0 is north, and the rest follow clockwise (NE is
	 *         1)
	 */
	public int getDir() {
		return dir;
	}

	/**
	 * Set the direction in which the agent should face.
	 * 
	 * @param newDir
	 *            a number between 0 and 7 (both included) representing the
	 *            direction, where 0 is north, and the rest follow clockwise (NE
	 *            is 1). Numbers outside this range will be modulo-ed into
	 *            correctness.
	 */
	public void setDir(final int newDir) {
		dir = newDir % POSSIBLE_DIRECTIONS;
		if (dir < 0) {
			dir += POSSIBLE_DIRECTIONS;
		}
	}

	/**
	 * Get the agent's current position.
	 * 
	 * @return the agent's position
	 */
	public Position getPos() {
		return pos;
	}

	/**
	 * Set the position of the user. This method provides instant teleportation!
	 * 
	 * @param pos
	 *            the agent's position
	 */
	public void setPos(final Position pos) {
		this.pos = pos;
	}

	/**
	 * Sets a key-value pair in the info field of an agent. Note that while you
	 * can change the value of a key at any time, adding keys is not allowed
	 * after the createAgents method has been called; this ensures that the keys
	 * remain constant once the simulation starts.
	 * <p>
	 * Also note that when a new key is added to the info field, you must
	 * provide a value for every agent. This guarantees that each agent contains
	 * has the same information.
	 * 
	 * @param key
	 *            the key being added to the info field
	 * @param value
	 *            the value associated to the key
	 * @return the old value associated to the key, or null if there was no
	 *         value
	 * 
	 * @throws InfoFieldsLockedException
	 *             if the create agents method has already returned, and it's
	 *             therefore not possible to add new fields
	 */
	public Publishable set(final String key, final Publishable value) {
		if (infoFieldsLocked && !info.containsKey(key)) {
			throw new InfoFieldsLockedException(key);
		}
		INFO_FIELDS.add(key);
		return info.put(key, value);
	}

	/**
	 * Get a set containing the keys in the info field of the agents. Note that
	 * the keys in the info field of all agents must be the same, and have a non
	 * null value, for the simulation to run.
	 * 
	 * @return a set with the keys in the agent info field. This keyset is not
	 *         backed by the keys in the info fields themselves. Changes on this
	 *         set have no effect in the agent's info field.
	 */
	public static Set<String> getInfoKeys() {
		return new TreeSet<String>(INFO_FIELDS);
	}

	/**
	 * Check that this agent instance has all the fields specified in the static
	 * INFO_FIELDS. This ensures that all agents have exactly the same fields
	 * set.
	 * 
	 * @return true if the agent has all the fields from INFO_FIELDS set, false
	 *         otherwise
	 */
	public boolean checkAllInfoFieldsPresent() {
		return (info.keySet().equals(INFO_FIELDS));
	}

	/**
	 * Get a collection containing all the values in the info field. Unlike
	 * <code>getInfoKeys</code>, this collection is backed by the info field
	 * itself, so that changes to it will affect the agent info field.
	 * 
	 * @return a Collection with the info values in the agent
	 */
	public Collection<Publishable> getInfoValues() {
		return info.values();
	}

	/**
	 * Get the value of the given key from the info field of the agent.
	 * 
	 * @param key
	 *            the key whose value we want to retrieve from the info field
	 * @return the value of the given key
	 * @throws InfoUndefinedException
	 *             if the key doesn't exist in the agent's info field
	 */
	public Publishable get(final String key) {
		if (!info.containsKey(key)) {
			throw new InfoUndefinedException(key);
		}

		return info.get(key);
	}

	/**
	 * Turn the agent <code>turn</code> times 45 degrees. A turn parameter value
	 * of 4 will make the agent turn around 180 degrees.
	 * 
	 * @param turn
	 *            a number representing the number of times to turn 45 degrees
	 */
	public void turn(final int turn) {
		setDir(dir + turn);
	}

	/**
	 * Move n steps towards the agent's current destination, where n is the
	 * speed. The direction of the movemement will be that which gets the agent
	 * closest to the destination. For more information on the routing
	 * mechanism, see {@link de.nec.nle.siafu.model.Gradient}.
	 */
	public void moveTowardsDestination() {
		if (isAtDestination()) {
			return;
		} else {
			for (int i = 0; i < speed; i++) {
				moveTowardsPlace(destination);

				if (pos.equals(destination.getPos())) {
					destination = null;
					atDestination = true;

					break;
				}
			}
		}
	}

	/**
	 * Move the agent one step in the direction provided by the <code>dir</code>
	 * parameter.
	 * 
	 * @param moveDir
	 *            the direction in which to move
	 * @throws PositionUnreachableException
	 *             if the position where the agent would end is a wall.
	 */
	public void moveInDirection(final int moveDir)
			throws PositionUnreachableException {
		if (moveDir == -1) {
			return; // Place reached
		}

		pos = pos.calculateMove(moveDir);
		this.dir = moveDir;
	}

	/**
	 * Move n steps towards the provided place, where n is the speed. The
	 * direction of the movemement will be that which gets the agent closest to
	 * the Place. For more information on the routing mechanism, see
	 * {@link de.nec.nle.siafu.model.Gradient}.
	 * 
	 * Callinc this method does not affect the agent's current destination.
	 * 
	 * @param place
	 *            the destination place.
	 */
	public void moveTowardsPlace(final Place place) {
		try {
			moveInDirection(place.pointFrom(pos, dir));
		} catch (PositionUnreachableException e) {
			System.err.println("Agent '" + this + "' can't reach '" + place
					+ "' at '" + pos + "'");
		}
	}

	// TODO maybe this distance should be given in real distance, not pixels.
	/**
	 * <p>
	 * Makes the agent wander around the given place. In detail, the agent will
	 * wander around as in {@link #wander(int)}, until he gets farther away than
	 * <code>radius</code> grid positions from <code>place</code> in which case
	 * it will move towards place as in {@link #moveTowards to true. This is to
	 * avoid the default behaviour about heading for the Destination overriding
	 * the AgentModel command to wander somewhere.
	 * </p>
	 * Place(Place)} </p>
	 * <p>
	 * Note that making an Agent wander also sets the atDestination flag to
	 * true. This is to avoid the default behaviour about heading for the
	 * Destination overriding the AgentModel command to wander somewhere.
	 * </p>
	 * 
	 * @param place
	 *            the place to keep close to
	 * @param radius
	 *            the distance in grid points (Background pixels if you will) to
	 *            keep as dog-leash length
	 * @param soberness
	 *            how straight the person is going to walk
	 * @see #wander()
	 */
	public void wanderAround(final Place place, final int radius,
			final int soberness) {
		atDestination = true; // Keep the developer from forgetting this (comm
		// by Kostas)
		if (place.distanceFrom(pos) > radius) {
			moveTowardsPlace(place);
		} else {
			wander(soberness);
		}
	}

	/**
	 * <p>
	 * Makes the agent wander around a given place with a soberness of 5. This
	 * method is equivalent to calling wanderArdound(place, radius, 5).
	 * </p>
	 * <p>
	 * Note that making an Agent wander also sets the atDestination flag to
	 * true. This is to avoid the default behaviour about heading for the
	 * Destination overriding the AgentModel command to wander somewhere.
	 * </p>
	 * 
	 * @param place
	 *            the place to keep close to
	 * @param radius
	 *            the dog-leash length
	 * @see #wanderAround(Place, int, int)
	 */
	public void wanderAround(final Place place, final int radius) {
		wanderAround(place, radius, DEFAULT_SOBERNESS);
	}

	/**
	 * <p>
	 * Makes the agent wander with a predefined soberness of 5.
	 * </p>
	 * <p>
	 * Note that making an Agent wander also sets the atDestination flag to
	 * true. This is to avoid the default behaviour about heading for the
	 * Destination overriding the AgentModel command to wander somewhere.
	 * </p>
	 * 
	 * @see #wander(int)
	 * 
	 */
	public void wander() {
		wander(DEFAULT_SOBERNESS);
	}

	/**
	 * <p>
	 * Makes the agent walk randomly. In this random pattern, the agent moves
	 * one step per iteration, and then turns with a probability of one out of
	 * <code>soberness</code> times. The more sobber, the less times it will
	 * turn. If it does turn, there's an equal probability that it will turn
	 * left or right.
	 * </p>
	 * 
	 * <p>
	 * If the current direction of the agent takes him towards a wall, the agent
	 * will turn, much like a toy robot, 135 degrees at a time, until a free
	 * direction is found. If the agent is in a one pixel wide room, it will
	 * loudly complain about it.
	 * </p>
	 * <p>
	 * Note that making an Agent wander also sets the atDestination flag to
	 * true. This is to avoid the default behaviour about heading for the
	 * Destination overriding the AgentModel command to wander somewhere.
	 * </p>
	 * 
	 * @param soberness
	 *            the likelyhood that the agent turns in its random wandering.
	 *            One out of <code>soberness</code> times, it will turn.
	 */
	public void wander(final int soberness) {
		atDestination = true; // Keep the developer from forgetting this (comm
		// by Kostas)
		boolean stuck = true;
		int searchDir = (RAND.nextInt(2) == 1) ? (-WANDER_TURN) : WANDER_TURN;
		int tries = 0;

		while (stuck && (tries < POSSIBLE_DIRECTIONS)) {
			Position target = null;

			try {
				target = pos.calculateMove(dir);
				pos = target;
				stuck = false;
			} catch (PositionUnreachableException e) {
				turn(searchDir);
				tries++;
			}
		}

		if (tries == POSSIBLE_DIRECTIONS) {
			System.err.println("My name's " + name
					+ " and you've got me stuck in a "
					+ "one pixel wide room! I have rights!");
		}

		if (RAND.nextInt(soberness) == 0) {
			if (RAND.nextInt(2) == 1) {
				turn(1);
			} else {
				turn(-1);
			}
		}
	}

	/**
	 * Returns a context variable in FlatData format. Notice that this does not
	 * only apply to the agent's <code>info</code> data, but to anything on the
	 * simulation. With this method you can retrieve the following variables:
	 * The context variable can be one of three types:
	 * <ul>
	 * <li>The name of an overlay as defined in the simulation data
	 * <li>The name of one of the variables in the agent's info field, defined
	 * at the agent model upon creation
	 * <li>A simulation intrinsec variable. These are called: <i>Name, Time,
	 * Position Destination and atDestination</i>
	 * </ul>
	 * 
	 * @param ctxName
	 *            the name of the variable
	 * @return a FlatData object representing the requested data
	 * @throws UnknownContextException
	 *             if the context variable isn't known
	 */
	public FlatData getContext(final String ctxName)
			throws UnknownContextException {
		if (info.containsKey(ctxName)) {
			return info.get(ctxName).flatten();
		} else if (world.getOverlays().containsKey(ctxName)) {
			return world.getOverlays().get(ctxName).getValue(pos).flatten();
		} else if (ctxName.equals("Time")) {
			return new Text("" + world.getTime().getTimeInMillis()).flatten();
		} else if (ctxName.equals("Name")) {
			return new Text(name).flatten();
		} else if (ctxName.equals("Position")) {
			return pos.flatten();
		} else if (ctxName.equals("atDestination")) {
			return new Text(new Boolean(atDestination).toString()).flatten();
		} else if (ctxName.equals("Destination")) {
			return destination.flatten();
		} else {
			throw new UnknownContextException(ctxName);
		}
	}

	/**
	 * Return the control of the agent to the simulation. The agent will obey
	 * the next order coming from the agent model.
	 */
	public synchronized void returnControl() {
		onAuto = true;
	}

	/**
	 * Get the control of the agent, that is, keep it from obeying the will of
	 * the agent model.
	 */
	public synchronized void getControl() {
		setVisible(true);
		atDestination=true;
		onAuto = false;
	}

	/**
	 * Find out if the agent is being controlled, or is working automatically
	 * according to the agent model. An agent is being controlled when its
	 * getControl() method is called. This is at least done implicitly by the
	 * GUI upon clicking on the agent.
	 * 
	 * @return true if the agent is moving according to the agent model, false
	 *         otherwise
	 */
	public synchronized boolean isOnAuto() {
		// System.out.println("Returning "+onAuto);
		return onAuto;
	}

	/**
	 * Find out wether the agent is currently being drawn by the simulation GUI.
	 * 
	 * @return true if it is visible, false otherwise
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Set whether the GUI should draw this agent, or rather make it invisible.
	 * Note that an invisible agent can not be selected in the GUI, and that any
	 * track mark on it will also be ignored.
	 * 
	 * @param visible
	 *            true if the agent should be visible, false otherwise
	 */
	public void setVisible(final boolean visible) {
		this.visible = visible;
	}

	/**
	 * Initialize the Agent class.
	 * 
	 * @param agentsWorld
	 *            the world agents should belong to
	 */
	public static void initialize(final World agentsWorld) {
		world = agentsWorld;
		infoFieldsLocked = false;
	}

	public int compareTo(Agent a) {
		// Sorted sets are traversed in ascending order. We want a high Z
		// priority to be drawn later, so this fits

		int zPrioritydiff = zPriority - a.getZPriority();
		if (zPrioritydiff != 0) {
			return zPrioritydiff;
		} else if(this.equals(a)){
			return 0;
		} else {
			return this.hashCode()-a.hashCode();
		}
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (atDestination ? 1231 : 1237);
		result = prime * result
				+ ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + dir;
		result = prime * result + ((image == null) ? 0 : image.hashCode());
		result = prime * result + ((info == null) ? 0 : info.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((onAuto == null) ? 0 : onAuto.hashCode());
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		result = prime * result
				+ ((previousImage == null) ? 0 : previousImage.hashCode());
		result = prime * result + speed;
		result = prime * result + (visible ? 1231 : 1237);
		result = prime * result + zPriority;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Agent other = (Agent) obj;
		if (atDestination != other.atDestination)
			return false;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (dir != other.dir)
			return false;
		if (image == null) {
			if (other.image != null)
				return false;
		} else if (!image.equals(other.image))
			return false;
		if (info == null) {
			if (other.info != null)
				return false;
		} else if (!info.equals(other.info))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (onAuto == null) {
			if (other.onAuto != null)
				return false;
		} else if (!onAuto.equals(other.onAuto))
			return false;
		if (pos == null) {
			if (other.pos != null)
				return false;
		} else if (!pos.equals(other.pos))
			return false;
		if (previousImage == null) {
			if (other.previousImage != null)
				return false;
		} else if (!previousImage.equals(other.previousImage))
			return false;
		if (speed != other.speed)
			return false;
		if (visible != other.visible)
			return false;
		if (zPriority != other.zPriority)
			return false;
		return true;
	}
}
