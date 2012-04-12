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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.configuration.Configuration;
import org.eclipse.swt.graphics.ImageData;

import de.nec.nle.siafu.behaviormodels.BaseAgentModel;
import de.nec.nle.siafu.behaviormodels.BaseContextModel;
import de.nec.nle.siafu.behaviormodels.BaseWorldModel;
import de.nec.nle.siafu.control.Controller;
import de.nec.nle.siafu.control.Simulation;
import de.nec.nle.siafu.exceptions.GUINotReadyException;
import de.nec.nle.siafu.exceptions.NothingNearException;
import de.nec.nle.siafu.exceptions.AgentNotFoundException;
import de.nec.nle.siafu.exceptions.PlaceNotFoundException;
import de.nec.nle.siafu.exceptions.PlaceTypeUndefinedException;
import de.nec.nle.siafu.exceptions.PlacesTypeIsEmptyException;
import de.nec.nle.siafu.exceptions.PositionOnAWallException;
import de.nec.nle.siafu.exceptions.PositionUnreachableException;
import de.nec.nle.siafu.graphics.markers.Marker;

/**
 * This class represents the world being simulated. It is the main class in what
 * concerns the simulation model. It contains all the agents, places and context
 * overlays. This is the class you'll be coming to when writing your World,
 * Agent and Context models.
 * 
 * The methods in this class let you interact with the world, find agents,
 * places, etc...
 * 
 * @author Miquel Martin
 * 
 */
public class World {
	/** The white color. Used to identify walls. */
	private static final int COLOR_WHITE = 0xFFFFFF;

	/**
	 * The distance in simulation grid points within which a Trackable is
	 * considered to be "near".
	 */
	private static final int NEAR_DISTANCE = 15;

	/**
	 * Whether the cache should be prefilled or not.
	 */
	private static Boolean prefillCache;

	/**
	 * The size of the cache, if the GUI is used.
	 */
	private static int cacheSize;

	/**
	 * A random number generator.
	 */
	private final Random rand = new Random();

	/**
	 * The world's height.
	 */
	private int height;

	/**
	 * The worlds width.
	 */
	private int width;

	/**
	 * The configuration of the simulation being displayed.
	 */
	private Configuration simulationConfig;

	/**
	 * The name of the world being simulated.
	 */
	private String worldName;

	/**
	 * A map of the simulated overlays.
	 */
	private SortedMap<String, Overlay> overlays;

	/**
	 * A collection of the places in the simulation.
	 */
	private ArrayList<Place> places;

	/**
	 * The set of place types.
	 */
	private Set<String> placeTypes = new HashSet<String>();

	/**
	 * The simulation time.
	 */
	private Calendar time;

	/**
	 * The behavior model for the agents.
	 */
	private BaseAgentModel agentModel;

	/**
	 * The behavior model for the places in the world.
	 */
	private BaseWorldModel worldModel;

	/**
	 * The behavior model for the simulated context.
	 */
	private BaseContextModel contextModel;

	/**
	 * The agents being simulated.
	 */
	private HashMap<String, Agent> people;

	/**
	 * The matrix of points that defines where an agent can walk or not.
	 */
	private boolean[][] walls;

	/**
	 * The simulation object, which starts the simulation thread.
	 */
	private Simulation simulation;

	/**
	 * The data that defines this simulation, including maps, sprites and
	 * behavioral classes.
	 */
	private SimulationData simData;

	/**
	 * Whether the gradient cache should be prefilled to avoid hiccups at the
	 * GUI.
	 * 
	 * @param prefill
	 *            true to fill the cache with place gradients
	 */
	public static void setShouldPrefillCache(final boolean prefill) {
		prefillCache = prefill;
	}

	/**
	 * Find out if the cache has to be prefilled or not.
	 * 
	 * @return true if the cache has to be prefilled
	 */
	public static boolean shouldPrefillCache() {
		return prefillCache;
	}

	/**
	 * Get the amount of gradients to keep in memory.
	 * 
	 * @return the said amount
	 */
	public static int getCacheSize() {
		return cacheSize;
	}

	/**
	 * Set the amount of gradients that should be kept in memory.
	 * 
	 * @param cacheSize
	 *            the said amount
	 */
	public static void setCacheSize(final int cacheSize) {
		World.cacheSize = cacheSize;
	}

	/**
	 * Instantiate the world in which the simulation will run.
	 * 
	 * @param simulation
	 *            the simulation object which is running this world.
	 * @param simData
	 *            the simulation data (maps, sprites, classes) for this
	 *            simulation.
	 */
	public World(final Simulation simulation, final SimulationData simData) {
		this.simulation = simulation;
		this.simData = simData;
		this.simulationConfig = simData.getConfigFile();
		this.worldName = simulationConfig.getString("worldname");

		Agent.resetAgents();
		
		Controller.getProgress().reportWorldCreation(worldName);

		buildWalls();

		initializeCoordinates();

		createTime();

		createPlaces();

		createPeople();

		freezeInfoFields();

		createOverlays();
	}

	/**
	 * Creates a place for each black pixel in the images contained in the
	 * simulation data. The place type is the name of the image.
	 * 
	 * @return a list of places generated from the images in the simulation
	 *         data.
	 */
	protected ArrayList<Place> createPlacesFromImages() {
		Place.initialize(this);
		ArrayList<Place> placesFromImg = new ArrayList<Place>();
		Map<String, InputStream> fileList = simData.getPlaceFiles();
		Iterator<String> listIt = fileList.keySet().iterator();

		int total = 0;

		while (listIt.hasNext()) {
			String type = (String) listIt.next();
			ArrayList<Position> placePoints = readPlacePoints(fileList
					.get(type));
			Iterator<Position> it = placePoints.iterator();
			total += placePoints.size();
			Controller.getProgress()
					.reportPlacesFound(type, placePoints.size());

			while (it.hasNext()) {
				Position pos = (Position) it.next();
				Place place;
				try {
					place = new Place(type, pos, this);
				} catch (PositionOnAWallException e) {
					throw new RuntimeException("One of your \"" + type
							+ "\" places, at " + pos + " is on a wall");
				}
				Controller.getProgress().reportPlaceCreated(type);
				placesFromImg.add(place);
			}
		}

		return placesFromImg;
	}

	/**
	 * Find the black pixels in the provided images, and interpret them as
	 * coordinates for places.
	 * 
	 * @param is
	 *            the InputStream to look for the points
	 * @return an ArrayList with the discovered positions
	 */
	private ArrayList<Position> readPlacePoints(final InputStream is) {
		ImageData attractorsImgData = new ImageData(is);
		ArrayList<Position> placePoints = new ArrayList<Position>();

		for (int i = 0; i < height; i++) {
			int[] row = new int[width];
			attractorsImgData.getPixels(0, i, width, row, 0);

			for (int j = 0; j < width; j++) {
				if (row[j] == 0) {
					Position attractor;

					try {
						attractor = new Position(i, j);
						placePoints.add(attractor);
					} catch (PositionUnreachableException e) {
						throw new RuntimeException("Place \"" + i + "," + j
								+ "\" is unreachable. Is it out of "
								+ "the map or on a wall?", e);
					}
				}
			}
		}

		return placePoints;
	}

	/**
	 * Get the name of the world.
	 * 
	 * @return the world's name
	 */
	public String getWorldName() {
		return worldName;
	}

	/**
	 * Get the height of the world map in pixels.
	 * 
	 * @return the height of the world in pixels
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Get the width of the world map in pixels.
	 * 
	 * @return the width of the world map in pixels
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Find out if the given position corresponds to a wall, or is actually
	 * walkable by agents.
	 * 
	 * @param pos
	 *            the position to check
	 * @return true if the position is on a wall, false otherwise
	 */
	public boolean isAWall(final Position pos) {
		return walls[pos.getRow()][pos.getCol()];
	}

	/**
	 * Get the names of the sprites available in this simulation.
	 * 
	 * @return a Set with the sprite names
	 */
	public Set<String> getAvailableSprites() {
		Set<String> spriteNames = new TreeSet<String>();

		for (String fileName : simData.getSpriteNames()) {
			spriteNames.add(fileName.split("-")[0]);
		}

		return spriteNames;
	}

	/**
	 * Get all the people (Agents) in the world.
	 * 
	 * @return a collection with the world's agents
	 */
	public Collection<Agent> getPeople() {
		return people.values();
	}

	/**
	 * Get an Agent by its name.
	 * 
	 * @param name
	 *            the agent's name
	 * @return the Agent instance
	 * @throws AgentNotFoundException
	 *             if the person does not exist
	 */
	public Agent getPersonByName(final String name)
			throws AgentNotFoundException {
		Agent p = people.get(name);

		if (p == null) {
			throw new AgentNotFoundException(name);
		} else {
			return p;
		}
	}

	/**
	 * Get all the places in the simulated world.
	 * 
	 * @return an ArrayList with the places
	 */
	public ArrayList<Place> getPlaces() {
		return places;
	}

	/**
	 * Get the simulation time.
	 * 
	 * @return a Calendar with the simulation time
	 */
	public Calendar getTime() {
		return time;
	}

	/**
	 * Initialize the simulation time.
	 */
	private void createTime() {
		time = Calendar.getInstance();
		time.clear();
		time.set(simulationConfig.getInt("starttime.year"), simulationConfig
				.getInt("starttime.month"), simulationConfig
				.getInt("starttime.day"), simulationConfig
				.getInt("starttime.hour"), simulationConfig
				.getInt("starttime.minute"));
	}

	/**
	 * Find a Trackable (Agent, Place, etc..) near the given position. The
	 * distance for "nearness" is set to NEAR_DISTANCE grid positions.
	 * 
	 * @param pos
	 *            the position in which to search
	 * @param visibleOnly
	 *            set to true to filter out invisible Trackables near the
	 *            position
	 * @return the first Trackable found
	 * @throws NothingNearException
	 *             if nothing is found nearby
	 */
	public Trackable findAnythingNear(final Position pos,
			final boolean visibleOnly) throws NothingNearException {
		try {
			return findAgentNear(pos, visibleOnly);
		} catch (NothingNearException e) {
			return findPlaceNear(pos, visibleOnly);
		}
	}

	/**
	 * Find an Agent near the given position. The distance is set to
	 * NEAR_DISTANCE.
	 * 
	 * @param pos
	 *            the position in which to search
	 * @param visibleOnly
	 *            set to true to find only visible agents
	 * @return the first nearby Agent found
	 * @throws NothingNearException
	 *             if nothing is found nearby
	 */
	public Trackable findAgentNear(final Position pos, final boolean visibleOnly)
			throws NothingNearException {
		return findNearOutOf(pos, people.values(), NEAR_DISTANCE, visibleOnly);
	}

	/**
	 * Find a Place near the given position. The distance is set to
	 * NEAR_DISTANCE.
	 * 
	 * @param pos
	 *            the position in which to search
	 * @param visibleOnly
	 *            set to true to find only visible places
	 * @return the first nearby Place found
	 * @throws NothingNearException
	 *             if nothing is found nearby
	 */
	public Trackable findPlaceNear(final Position pos, final boolean visibleOnly)
			throws NothingNearException {
		return findNearOutOf(pos, places, NEAR_DISTANCE, visibleOnly);
	}

	/**
	 * Find a Trackable that's near the given positions, out of the provided
	 * candidates.
	 * 
	 * @param pos
	 *            the position in which to search
	 * @param candidates
	 *            the nearby trackables that we are willing to accept
	 * @param distance
	 *            the distance in grid positions to consider a Trackable as
	 *            being near
	 * @param visibleOnly
	 *            set to true to find only visible trackables
	 * @return the first nearby trackable out of the candidates
	 * @throws NothingNearException
	 *             if nothing
	 */
	public Trackable findNearOutOf(final Position pos,
			final Collection<? extends Trackable> candidates,
			final int distance, final boolean visibleOnly)
			throws NothingNearException {
		Trackable target = null;
		Trackable candidate = null;

		Iterator<? extends Trackable> candidateIt = candidates.iterator();

		while ((target == null) && candidateIt.hasNext()) {
			candidate = (Trackable) candidateIt.next();

			if ((!visibleOnly || candidate.isVisible())
					&& candidate.getPos().isNear(pos, distance)) {
				target = candidate;
				if (visibleOnly && !((Trackable) candidate).isVisible()) {
					target = null;
				}
			}
		}

		if (target == null) {
			throw new NothingNearException();
		} else {
			return target;
		}
	}

	/**
	 * Find all the agents within distance grid positions.
	 * 
	 * @param pos
	 *            the position in which to search
	 * @param distance
	 *            the maximum distance at which we consider the agent as being
	 *            nearby
	 * @param visibleOnly
	 *            set to true if only visible trackables should be returned
	 * @return a collection with all the agents near pos
	 * @throws NothingNearException
	 *             if there's no agents nearby
	 */
	public ArrayList<Trackable> findAllAgentsNear(final Position pos,
			final int distance, final boolean visibleOnly)
			throws NothingNearException {
		return findAllNearOutOf(pos, people.values(), distance, visibleOnly);
	}

	/**
	 * Find all the places within distance grid positions.
	 * 
	 * @param pos
	 *            the position in which to search
	 * @param distance
	 *            the maximum distance at which we consider the place as being
	 *            nearby
	 * @param visibleOnly
	 *            set to true if only visible trackables should be returned
	 * @return a collection with all the places near pos
	 * @throws NothingNearException
	 *             if there's no agents nearby
	 */
	public ArrayList<Trackable> findAllPlacesNear(final Position pos,
			final int distance, final boolean visibleOnly)
			throws NothingNearException {
		return findAllNearOutOf(pos, places, distance, visibleOnly);
	}

	/**
	 * Find all the Trackable near the given position, out of the provided
	 * candidates.
	 * 
	 * @param pos
	 *            the position in which to search
	 * @param candidates
	 *            the nearby trackables that we are willing to accept
	 * @param distance
	 *            the distance in grid positions to consider a Trackable as
	 *            being near
	 * @param visibleOnly
	 *            set to true to find only visible trackables
	 * @return a collection with all the narby candidates
	 * @throws NothingNearException
	 *             if nothing
	 */
	public ArrayList<Trackable> findAllNearOutOf(final Position pos,
			final Collection<? extends Trackable> candidates,
			final int distance, final boolean visibleOnly)
			throws NothingNearException {
		ArrayList<Trackable> targets = new ArrayList<Trackable>();
		Trackable candidate;

		Iterator<? extends Trackable> candidatesIt = candidates.iterator();

		while (candidatesIt.hasNext()) {
			candidate = (Trackable) candidatesIt.next();

			if ((!visibleOnly || candidate.isVisible())
					&& candidate.getPos().isNear(pos, distance)) {
				targets.add(candidate);
			}
		}

		if (targets.isEmpty()) {
			throw new NothingNearException();
		} else {
			return targets;
		}
	}

	/**
	 * Find a place given its name.
	 * 
	 * @param name
	 *            the place's name
	 * @return the place instance
	 * @throws PlaceNotFoundException
	 *             if the place is not found
	 */
	public Place getPlaceByName(final String name)
			throws PlaceNotFoundException {
		Iterator<Place> placesIt = places.iterator();

		while (placesIt.hasNext()) {
			Place p = placesIt.next();

			if (p.getName().equals(name)) {
				return p;
			}
		}

		throw new PlaceNotFoundException(name);
	}

	/**
	 * Get the place at the given position.
	 * 
	 * @param pos
	 *            the position in which to find a place
	 * @return the place at that position
	 * @throws PlaceNotFoundException
	 *             if there is no place at that position
	 */
	public Place getPlaceByPosition(final Position pos)
			throws PlaceNotFoundException {
		Iterator<Place> placesIt = places.iterator();

		while (placesIt.hasNext()) {
			Place p = placesIt.next();

			if (p.getPos().equals(pos)) {
				return p;
			}
		}

		throw new PlaceNotFoundException("at " + pos.toString());
	}

	/**
	 * Get all the places of a given type.
	 * 
	 * @param type
	 *            the chosen type
	 * @return a Collection with the places of that type
	 * @throws PlaceTypeUndefinedException
	 *             if the type is not defined
	 */
	public Collection<Place> getPlacesOfType(final String type)
			throws PlaceTypeUndefinedException {
		if (!placeTypes.contains(type)) {
			throw new PlaceTypeUndefinedException(type);
		}

		ArrayList<Place> selection = new ArrayList<Place>();
		Iterator<Place> it = places.iterator();

		while (it.hasNext()) {
			Place p = (Place) it.next();

			if (p.getType().equals(type)) {
				selection.add(p);
			}
		}

		return selection;
	}

	/**
	 * Get the place that's colsest to the given position, for a given type.
	 * 
	 * @param type
	 *            the type to consider
	 * @param pos
	 *            the position in which to search
	 * @return the nearest place
	 * @throws PlaceTypeUndefinedException
	 *             if no place is found
	 */
	public Place getNearestPlaceOfType(final String type, final Position pos)
			throws PlaceTypeUndefinedException {
		Place nearest = null;
		double minDistance = -1;
		Iterator<Place> pIt = getPlacesOfType(type).iterator();

		while (pIt.hasNext()) {
			Place p = pIt.next();
			double distance = p.distanceFrom(pos);

			if ((distance < minDistance) || (nearest == null)) {
				nearest = p;
				minDistance = distance;
			}
		}

		return nearest;
	}

	/**
	 * Get a random place out of all the places known for the given type.
	 * 
	 * @param type
	 *            the type of place we need
	 * @return a random place of type "type"
	 * @throws PlaceNotFoundException
	 *             if there are no places of that type
	 */
	public Place getRandomPlaceOfType(final String type)
			throws PlaceNotFoundException {
		ArrayList<Place> typedPlaces = new ArrayList<Place>();
		typedPlaces.addAll(getPlacesOfType(type));

		if (typedPlaces.isEmpty()) {
			throw new PlacesTypeIsEmptyException(type);
		}

		return (Place) typedPlaces.get(rand.nextInt(typedPlaces.size()));
	}

	/**
	 * Pause the simulation.
	 * 
	 * @param pause
	 *            true if the simulation should be paused
	 */
	public void pause(final boolean pause) {
		simulation.setPaused(pause);
	}

	/**
	 * Make the world stop spinning with a single method call! Ok, this is
	 * identical to the pause method. Only funnier.
	 * 
	 * @param stop
	 *            true if you need a break and want to stop the world, false
	 *            otherwise.
	 */
	public void stopSpinning(final boolean stop) {
		pause(stop);
	}

	/**
	 * Add a Marker to the simulation GUI. If the GUI is not ready, this method
	 * performs no action a GUINotReadyException is thrown. If the GUI is not
	 * being used this method returns silently and does nothing.
	 * 
	 * @param m
	 *            the marker to add
	 * @throws GUINotReadyException
	 *             if the GUI can not draw the mark at the moment.
	 */
	public void addMarker(final Marker m) throws GUINotReadyException {
		simulation.addMarker(m);
	}

	/**
	 * Remove all Markers from the simulation GUI. If the GUI is not ready, this
	 * method performs no action a GUINotReadyException is thrown. If the GUI is
	 * not being used this method returns silently and does nothing.
	 * 
	 * @throws GUINotReadyException
	 *             if the GUI can not draw the mark at the moment.
	 */
	public void unMarkAll() throws GUINotReadyException {
		simulation.unMarkAll();
	}

	/**
	 * Remove the Marker for this Trackable from the simulation GUI F. If the
	 * GUI is not ready, this method performs no action a GUINotReadyException
	 * is thrown. If the GUI is not being used this method returns silently and
	 * does nothing.
	 * 
	 * @param t
	 *            the Trackable to unmark
	 * @throws GUINotReadyException
	 *             if the GUI can not draw the mark at the moment.
	 */
	public void unMark(final Trackable t) throws GUINotReadyException {
		simulation.unMark(t);
	}

	/**
	 * Find out if the given Trackable is marked in the GUI. If the GUI is not
	 * ready, this method throws a GUINotReadyException. If the GUI is not being
	 * used this method returns false.
	 * 
	 * @param t
	 *            the Trackable about which we are asking
	 * @return true if the trackable has been marked, false otherwise
	 * @throws GUINotReadyException
	 *             if the GUI can not draw the mark at the moment.
	 */
	public boolean isMarked(final Trackable t) throws GUINotReadyException {
		return simulation.isMarked(t);
	}

	/**
	 * Use the calibration provided in the configuration file to figure out the
	 * coordinates of each map position.
	 * 
	 */
	private void initializeCoordinates() {
		double[] topRight = new double[] {
				simulationConfig.getDouble("calibration.topright[@latitude]"),
				simulationConfig.getDouble("calibration.topright[@longitude]") };

		double[] bottomLeft = new double[] {
				simulationConfig.getDouble("calibration."
						+ "bottomleft[@latitude]"),
				simulationConfig.getDouble("calibration."
						+ "bottomleft[@longitude]") };
		double[] bottomRight = new double[] {
				simulationConfig.getDouble("calibration."
						+ "bottomright[@latitude]"),
				simulationConfig.getDouble("calibration."
						+ "bottomright[@longitude]") };

		Position.initialize(this, topRight, bottomRight, bottomLeft);
	}

	/**
	 * Create the people to simulate by asking the AgentModel to do so.
	 * 
	 */
	private void createPeople() {
		people = new HashMap<String, Agent>();

		try {
			agentModel = (BaseAgentModel) simData.getAgentModelClass()
					.getConstructor(new Class[] { this.getClass() })
					.newInstance(new Object[] { this });
		} catch (Exception e) {
			throw new RuntimeException("Can't instantiate the agent model", e);
		}

		Agent.initialize(this);
		Controller.getProgress().reportCreatingAgents();
		ArrayList<Agent> peopleList = agentModel.createAgents();
		Iterator<Agent> peopleIt = peopleList.iterator();

		while (peopleIt.hasNext()) {
			Agent p = peopleIt.next();
			people.put(p.getName(), p);
		}
	}

	/**
	 * Keep the simulation from adding any new fields to the info field of
	 * Agents. The values can still be changed, but no new keys are allowed.
	 * 
	 */
	private void freezeInfoFields() {
		Iterator<Agent> peopleIt = people.values().iterator();

		while (peopleIt.hasNext()) {
			Agent p = peopleIt.next();

			if (!p.checkAllInfoFieldsPresent()) {
				throw new RuntimeException(
						"Agent "
								+ p.getName()
								+ " is missing at least one field that other agents have.");
			}
		}

		Agent.lockInfoFields();
	}

	/**
	 * Generate a matrix with the world's walls, out of the image file provided
	 * in the simulation data.
	 * 
	 */
	private void buildWalls() {
		InputStream wallsIS = simData.getWallsFile();
		ImageData img = new ImageData(wallsIS);
		height = img.height;
		width = img.width;

		walls = new boolean[height][width];

		for (int i = 0; i < height; i++) {
			int[] colors = new int[width];
			img.getPixels(0, i, width, colors, 0);

			for (int j = 0; j < width; j++) {
				walls[i][j] = (colors[j] == COLOR_WHITE);
			}
		}
	}

	/**
	 * Create the world's places by generating them from out of the images, and
	 * then asking the WorldModel to create extra ones if needed.
	 * 
	 */
	private void createPlaces() {
		try {
			worldModel = (BaseWorldModel) simData.getWorldModelClass()
					.getConstructor(new Class[] { this.getClass() })
					.newInstance(new Object[] { this });

		} catch (Exception e) {
			throw new RuntimeException("Can't instantiate the world model", e);
		}
		places = createPlacesFromImages();
		worldModel.createPlaces(places);

	}

	/**
	 * Create the simulation overlays out of the overlay images provided, and
	 * then asking the ContextModel to modify them as suitable.
	 * 
	 */
	private void createOverlays() {
		overlays = new TreeMap<String, Overlay>();

		try {
			contextModel = (BaseContextModel) simData.getContextModelClass()
					.getConstructor(new Class[] { this.getClass() })
					.newInstance(new Object[] { this });
		} catch (Exception e) {
			throw new RuntimeException("Can't instantiate the context model", e);
		}

		ArrayList<Overlay> olList = createOverlaysFromImages();
		contextModel.createOverlays(olList);

		Iterator<Overlay> olListIt = olList.iterator();

		while (olListIt.hasNext()) {
			Overlay ol = olListIt.next();
			overlays.put(ol.getName(), ol);
		}
	}

	/**
	 * Create the simulation overlays out of the overlay images provided.
	 * 
	 * @return the collection of overlays
	 * 
	 */
	private ArrayList<Overlay> createOverlaysFromImages() {
		ArrayList<Overlay> overlaysFromImages = new ArrayList<Overlay>();
		Map<String, InputStream> fileList = simData.getOverlayFiles();
		Iterator<String> listIt = fileList.keySet().iterator();

		while (listIt.hasNext()) {
			String name = listIt.next();
			InputStream overlayIS = fileList.get(name);
			overlaysFromImages.add(Overlay.getOverlay(name, overlayIS,
					simulationConfig));
		}

		return overlaysFromImages;
	}

	/**
	 * Get the overlays being simulated.
	 * 
	 * @return a SortedMap with the overlays in the simulation
	 */
	public SortedMap<String, Overlay> getOverlays() {
		return overlays;
	}

	/**
	 * Get a Set with all the place types in the simulation.
	 * 
	 * @return a Set with the place types
	 */
	public Set<String> getPlaceTypes() {
		return placeTypes;
	}

	/**
	 * Create a new place type.
	 * 
	 * @param placeType
	 *            the name of the place type
	 */
	public void addPlaceType(final String placeType) {
		placeTypes.add(placeType);
	}

	/**
	 * Get the Agent Model being used in the simulation.
	 * 
	 * @return the AgentModel
	 */
	public BaseAgentModel getAgentModel() {
		return agentModel;
	}

	/**
	 * Get the CotnextModel being used in the simulation.
	 * 
	 * @return the ContextModel
	 */
	public BaseContextModel getContextModel() {
		return contextModel;
	}

	/**
	 * Get the World Model being used in the simulation.
	 * 
	 * @return the WorldModel
	 */
	public BaseWorldModel getWorldModel() {
		return worldModel;
	}

}
