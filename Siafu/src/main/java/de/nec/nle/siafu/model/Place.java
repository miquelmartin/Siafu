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
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import de.nec.nle.siafu.control.Controller;
import de.nec.nle.siafu.exceptions.InfoUndefinedException;
import de.nec.nle.siafu.exceptions.InitializationRequiredException;
import de.nec.nle.siafu.exceptions.PositionOnAWallException;
import de.nec.nle.siafu.exceptions.UnknownContextException;
import de.nec.nle.siafu.graphics.Overlayable;
import de.nec.nle.siafu.types.FlatData;
import de.nec.nle.siafu.types.Publishable;
import de.nec.nle.siafu.types.Text;
import de.nec.nle.siafu.types.TypeUtils;
import de.nec.nle.siafu.utils.PersistentCachedMap;
import de.nec.nle.siafu.utils.SiafuGradientCache;

/**
 * Places define a position in the simulated world. They can have their own
 * properties, be used to create events (using the WorldModel) and become
 * detinations for the agent's movements. Before you can use any Place
 * instance, you will have to initialize it with the world, by running the
 * initialize method. Ok, here's a catch: the gradients take a lot of space,
 * so they are persisted using the PersistentCachedMap. If you keep the
 * reference in a non temporary object, the Garbage Collector won't be able to
 * destroy it, so don't.
 * 
 * Never, ever, access temporary gradient (one with a relevant psition) or
 * gradients directly. Use getGradient instead.
 * 
 * @author Miquel Martin
 * 
 */
public class Place implements Trackable, Publishable, Overlayable {
	/** The gradients that lead agents to each pace. */
	private static PersistentCachedMap gradients;

	/** The simulation's world. */
	private static World world;

	/** The name of the place. */
	private String name;

	/**
	 * The type of place. If the place was created using an image, this is the
	 * image name.
	 */
	private String type;

	/** The position of the place. */
	private Position pos;

	/**
	 * If this is a temporary place (e.g. created by moving an agent using the
	 * GUI), the distance gradient to the place is not stored in the
	 * persistent cached maps, but rather kept in this non persisted variable.
	 */
	private Gradient temporaryGradient;

	/**
	 * Whether the place is visible. This is for compatibility with the
	 * Trackable interface, but doesn't mean much at this point.
	 */
	private boolean visible = true;

	/**
	 * A SortedMap like the one in agents, for places to store information.
	 */
	private SortedMap<String, Publishable> info;

	/**
	 * Create the SiafuGradientCache that will hold the simulation places
	 * gradients. This must be run before you instantiate any Place.
	 * 
	 * @param newWorld the world to which you initialize the places
	 */
	public static void initialize(final World newWorld) {
		Place.world = newWorld;
		// Allow the old value to be garbage
		// collected before the next is created
		gradients = null;

		gradients =
				new SiafuGradientCache(Controller.DEFAULT_GRADIENT_PATH,
						world.getWorldName(), World.getCacheSize(),
						World.shouldPrefillCache());
	}

	/**
	 * Create a Place. If relevantPosition is not null, we do not calculate a
	 * full distance gradient (i.e. the distance from anywhere in the map to
	 * this point), but a partial one, which stops calculating the moment it
	 * reaches relevantPosition. The place's name is created by concatenating
	 * the type and the position.
	 * 
	 * @param type the type of place (e.g. office, airport, restroom)
	 * @param pos the position of the place
	 * @param world the world to which this place belongs
	 * @param relevantPosition the position we care about for the purposes of
	 *            gradient calculation
	 */
	public Place(final String type, final Position pos, final World world,
			final Position relevantPosition) {
		this(type, pos, world, type + "-" + pos, relevantPosition);
	}

	/**
	 * Create a Place. The place's name is created by concatenating the type
	 * and the position.
	 * 
	 * @param type the type of place (e.g. office, airport, restroom)
	 * @param pos the position of the place
	 * @param world the world to which this place belongs
	 * 
	 */
	public Place(final String type, final Position pos, final World world) {
		this(type, pos, world, type + "-" + pos, null);
	}

	/**
	 * Create a Place. If relevantPosition is not null, we do not calculate a
	 * full distance gradient (i.e. the distance from anywhere in the map to
	 * this point), but a partial one, which stops calculating the moment it
	 * reaches relevantPosition.
	 * 
	 * @param type the type of place (e.g. office, airport, restroom)
	 * @param pos the position of the place
	 * @param world the world to which this place belongs
	 * @param name the name of the place
	 * 
	 */
	public Place(final String type, final Position pos, final World world,
			final String name) {
		this(type, pos, world, name, null);
	}

	/**
	 * Create a place specifying all of the parameters.
	 * 
	 * @param type the type of place (e.g. office, airport, restroom)
	 * @param pos the position of the place
	 * @param world the world to which this place belongs
	 * @param name the name of the place
	 * @param relevantPosition the position we care about for the purposes of
	 *            gradient calculation
	 * 
	 * @throws PositionUnreachableException
	 */
	// Can throw position unreachable
	public Place(final String type, final Position pos, final World world,
			final String name, final Position relevantPosition) {
		basicChecks(world);
		if(world.isAWall(pos)){
			throw new PositionOnAWallException();
		}
		this.info = new TreeMap<String, Publishable>();
		this.type = type;
		this.pos = pos;
		this.name = name;

		world.addPlaceType(type);

		if (relevantPosition != null) {
			temporaryGradient = new Gradient(pos, world, relevantPosition);
		} else if (!gradients.containsKey(pos.toString())) {
			gradients.put(pos, new Gradient(pos, world));
		}
	}

	/**
	 * Check that everyrthing's ok and we can really create this place.
	 * Essentially, check that we have initialized places and that all places
	 * belong to the same world.
	 * 
	 * @param thisPlacesWorld the world of this place
	 */
	private void basicChecks(final World thisPlacesWorld) {
		if (gradients == null) {
			throw new InitializationRequiredException(
					"You need to initialize the Place class");
		}
		if (Place.world != thisPlacesWorld) {
			throw new RuntimeException(
					"All your places must belong to the same World.");
		}
	}

	/**
	 * Builds a Position as defined by a FlatData object. Internally, the
	 * latitude and longitude is converted to a row and column in the
	 * simulator's matrix.
	 * 
	 * @param flatData the flat data string with the latitude and longitude
	 */
	public Place(final FlatData flatData) {
		final int nameField = 0;
		final int typeField = 1;
		final int latField = 2;
		final int lonField = 3;
		String data = flatData.getData();
		TypeUtils.check(this, data);
		String place = data.substring(data.indexOf(':') + 1);
		String[] part = place.split("#");
		this.name = part[nameField];
		this.type = part[typeField];
		double lat = new Double(part[latField]);
		double lon = new Double(part[lonField]);
		this.pos = new Position(lat, lon);
		
		this.info = new TreeMap<String, Publishable>(); 
		world.addPlaceType(type); 
		if (!gradients.containsKey(pos.toString())) { 
		gradients.put(pos, new Gradient(pos, world));
		}
	}

	/**
	 * Turn the place to a String by returning it's name.
	 * 
	 * @return the place's name
	 */
	public String toString() {
		return getName();
	}

	/**
	 * Get the place's name.
	 * 
	 * @return the place's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the place.
	 * 
	 * @param name the place name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Get the place's type.
	 * 
	 * @return the place's type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Get the position of the place.
	 * 
	 * @return the place's position
	 */
	public Position getPos() {
		return pos;
	}

	/**
	 * Put a value into the info object in this Place.
	 * 
	 * @param key the key to set
	 * @param value the value for that key
	 * @return the previous value, if this key was already set.
	 */
	public Publishable set(final String key, final Publishable value) {
		return info.put(key, value);
	}

	/**
	 * Get a value from the place info field, identified by key.
	 * 
	 * @param key the key to retrieve
	 * @return the Publishable value associated to that key
	 */
	public Publishable get(final String key) {
		// TODO: Should the get and set of info of places have the complexity
		// of the agents? No. The agents info fields can be printed in the
		// CVS, hence the need for locking. This isn't so for places.
		if (!info.containsKey(key)) {
			throw new InfoUndefinedException(key);
		}

		return info.get(key);
	}

	/**
	 * Return the keys of the info object. Note that this is not a static
	 * method as in Agent, because, unlike it, two Place instances can have
	 * different info fields.
	 * 
	 * @return a Set with the info keys
	 */
	public Set<String> getInfoKeys() {
		return info.keySet();
	}

	/**
	 * Get all the values for the objects mapped into the place's info field.
	 * 
	 * @return the Collection of values for the place
	 */
	public Collection<Publishable> getInfoValues() {
		return info.values();
	}

	/**
	 * Request that the PersistentCachedMap that holds the place gradients be
	 * filled with the first size entries. This keeps the GUI from hicupping
	 * when a place is first used in the simulation.
	 * 
	 * @param size the amount of elements to load from the persisted cache
	 */
	public static void fillCache(final int size) {
		if (gradients == null) {
			throw new RuntimeException(
					"Instanciate the PersistentCachedMap first.");
		} else {
			gradients.fillCache(size);
		}
	}

	/**
	 * Get the gradient to a place. The Gradient is a matrix with the distance
	 * from each map position to a central point. An agent can move towards
	 * that point by following the biggest descent all the way to the center.
	 * 
	 * @return the gradient for this place
	 */
	public Gradient getGradient() {
		if (temporaryGradient != null) {
			return temporaryGradient;
		} else {
			return (Gradient) gradients.get(pos.toString());
		}
	}

	/**
	 * Returns the optimum direction to reach this place, from the given
	 * position. The path is calculated using a propagated distance gradient
	 * 
	 * @param targetPos the position we intend to reach
	 * @return the optimal direction, represented by an int from 0 to 7, where
	 *         0 is North, and 7 is North-West, or -1 if we are there
	 */
	public int pointFrom(final Position targetPos) {
		return getGradient().pointFrom(targetPos);
	}

	/**
	 * This method is identical to <code>pointFrom(targetPos)</code> except
	 * if several directions turn out to be equally good, the one given as
	 * priority is returned. If the prioritized one is not one of the optimal
	 * ones, it is ignored. This makes the agent walk a bit less drunken,
	 * turning only when they really need to.
	 * 
	 * @param targetPos the position we intend to reach
	 * @param preferedDir the preferred direction, if more than one is
	 *            possible and this one is one of them
	 * @return the optimal direction, represented by an int from 0 to 7, where
	 *         0 is North, and 7 is North-West, or -1 if we are there
	 */
	public int pointFrom(final Position targetPos, final int preferedDir) {
		return getGradient().pointFrom(targetPos, preferedDir);
	}

	/**
	 * Return the distance from targetPos to this position. This distance is
	 * not calibrated to the map. May be it should...
	 * 
	 * @param targetPos the position we want to calculate the distance to
	 * @return the distance, considering that one pixel is "10" and the
	 *         diagonal step is 14.
	 */
	public int distanceFrom(final Position targetPos) {
		return getGradient().distanceFrom(targetPos);
	}

	/**
	 * Flatten out the Place object with the format:
	 * <code>de.nec.nle.siafu.model.Place:name#type#latitude#longitude</code>.
	 * 
	 * @return the <code>FlatData</code> object that represents this Place.
	 * 
	 */
	public FlatData flatten() {
		String data;
		double[] coords = pos.getCoordinates();
		data = this.getClass().getName() + ":";
		data += name + "#";
		data += type + "#";
		data += coords[0] + "#" + coords[1];
		return new FlatData(data);
	}

	/**
	 * Returns a context variable in FlatData format. Notice that this does
	 * not only apply to the place's <code>info</code> data, but to anything
	 * on the simulation. With this method you can retrieve the following
	 * variables: The context variable can be one of three types:
	 * <ul>
	 * <li> The name of an overlay as defined in the simulation data
	 * <li> The name of one of the variables in the place's info field,
	 * defined at the agent model upon creation
	 * <li> A simulation intrinsec variable. These are called: <i>Name, Time,
	 * and Position</i>
	 * </ul>
	 * 
	 * @param ctxName the name of the variable
	 * @return a FlatData object representing the requested data
	 * @throws UnknownContextException if the context variable isn't known
	 */
	public FlatData getContext(final String ctxName)
			throws UnknownContextException {
		if (info.containsKey(ctxName)) {
			return info.get(ctxName).flatten();
		} else if (world.getOverlays().containsKey(ctxName)) {
			return world.getOverlays().get(ctxName).getValue(pos)
					.flatten();
		} else if (ctxName.equals("Time")) {
			return new Text("" + world.getTime().getTimeInMillis())
					.flatten();
		} else if (ctxName.equals("Name")) {
			return new Text(name).flatten();
		} else if (ctxName.equals("Position")) {
			return pos.flatten();
		} else if (ctxName.equals("Type")) {
			return new Text(type).flatten();
		} else {
			throw new UnknownContextException(ctxName);
		}
	}
	
	/**
	 * Placed here for compliance with the Trackable interface.
	 * 
	 * @return if the place is visible.
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Placed here for compliance with the Trackable interface.
	 * 
	 * @param visible set to true to make the place visible
	 */
	public void setVisible(final boolean visible) {
		this.visible = visible;
	}
}
