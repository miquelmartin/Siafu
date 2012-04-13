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

import java.io.Serializable;

import de.nec.nle.siafu.exceptions.InitializationRequiredException;
import de.nec.nle.siafu.exceptions.PositionOnAWallException;
import de.nec.nle.siafu.exceptions.PositionOutOfTheMapException;
import de.nec.nle.siafu.types.FlatData;
import de.nec.nle.siafu.types.Publishable;
import de.nec.nle.siafu.types.TypeUtils;

/**
 * <p>
 * This class represents a position in the simulator map. Note that the
 * simulator works on two coordinate systems:
 * <ul>
 * <li>A row-column grid (from top left to bottom right)
 * <li>A translation of those rows and columns into coordinates, using the map
 * calibration
 * </ul>
 * <p>
 * The pretty coordinates methods allow you to go from one to the other.
 * 
 * @author Miquel Martin
 * 
 */
public class Position implements Serializable, Comparable<Position>,
		Publishable {
	/**
	 * The default value for the maximum distance that's still considered as
	 * "near".
	 */
	private static final int NEAR_DISTANCE = 15;

	/** Default serial version UID. */
	private static final long serialVersionUID = 1L;

	/** Whether the positions have been initialized. */
	private static boolean initialized;

	/** The coordinate tools used to make coordinate conversions. */
	private static CoordinateTools coordinateTools;

	/** Map width in columns. */
	private static int width;

	/** Map width in height. */
	private static int height;

	/** The simulation's world. */
	private static World world;

	/**
	 * The amount of possible directions.
	 */
	private static final int DIRECTIONS = 8;

	/**
	 * The shifts in rows and columns needed to go to each of the directions.
	 */
	private static final int[][] COMPASS = new int[][] { { -1, 0 }, { -1, 1 },
			{ 0, 1 }, { 1, 1 }, { 1, 0 }, { 1, -1 }, { 0, -1 }, { -1, -1 } };

	/** The row for this position, starting at the top of the map. */
	private int i;

	/** The column for this position, starting at the left side of the map. */
	private int j;

	/**
	 * Initialize the position system to match the simulated world.
	 * 
	 * Invoquing this method initializes the static CoordinateTool which assists
	 * in calculating latitude and longitudes back and forth from the Siafu
	 * row/col position system.
	 * 
	 * You must run this method once before Positions can be created.
	 * 
	 * @param worldObj
	 *            the simulation world
	 * @param topRight
	 *            an array with the latitude and longitude of the top right
	 *            corner of the provided background map
	 * @param bottomRight
	 *            an array with the latitude and longitude of the bottom right
	 *            corner of the provided background map
	 * @param bottomLeft
	 *            an array with the latitude and longitude of the bottm left
	 *            corner of the provided background map
	 */
	public static void initialize(final World worldObj,
			final double[] topRight, final double[] bottomRight,
			final double[] bottomLeft) {
		if (worldObj == null) {
			throw new RuntimeException("Null world received!");
		}
		world = worldObj;
		width = world.getWidth();
		height = world.getHeight();
		coordinateTools = new CoordinateTools(height, width, topRight,
				bottomRight, bottomLeft);
		initialized = true;

	}

	/**
	 * Create a position.
	 * 
	 * @param i
	 *            the row of the map. Top is 0
	 * @param j
	 *            the column of the map. Left is 0
	 * @throws PositionOutOfTheMapException
	 *             if you define a position outside the map
	 */
	public Position(final int i, final int j)
			throws PositionOutOfTheMapException {
		if (!initialized) {
			throw new InitializationRequiredException(
					"Position not initialized");
		}

		if ((i >= height) || (i < 0) || (j >= width) || (j < 0)) {
			throw new PositionOutOfTheMapException();
		}

		this.i = i;
		this.j = j;
	}

	/**
	 * Create a position using coordinates. The coordinates are then translated
	 * to rows and columns using the calibration provided in the config file.
	 * 
	 * @param lat
	 *            the latitude
	 * @param lon
	 *            the longitude
	 * @throws PositionOutOfTheMapException
	 *             if those coordinates map to a position outside the map
	 */
	public Position(final double lat, final double lon)
			throws PositionOutOfTheMapException {
		this(coordinateTools.coordinatesToLocal(lat, lon));
	}

	/**
	 * Create a position by copying an existing position.
	 * 
	 * @param p
	 *            the position to copy
	 * @throws PositionOutOfTheMapException
	 *             if the original position is out of the map
	 */
	public Position(final Position p) throws PositionOutOfTheMapException {
		this(p.i, p.j);
	}

	/**
	 * Create a position from a string of the type "row.col".
	 * 
	 * @param str
	 *            the position string
	 * @throws NumberFormatException
	 *             if row or col are not integer numbers
	 * @throws PositionOutOfTheMapException
	 *             if the row and column map to a position outside the map.
	 */
	public Position(final String str) throws NumberFormatException,
			PositionOutOfTheMapException {
		this(new Integer(str.split(".")[0]).intValue(), new Integer(
				str.split(".")[1]).intValue());
	}

	/**
	 * Builds a Position as defined by a FlatData object. Internally, the
	 * latitude and longitude is converted to a row and column in the
	 * simulator's matrix.
	 * 
	 * @param flatData
	 *            the flat data string with the latitude and longitude
	 */
	public Position(final FlatData flatData) {
		String data = flatData.getData();
		TypeUtils.check(this, data);
		String period = data.substring(data.indexOf(':') + 1);
		String[] part = period.split("#");

		double lat = new Double(part[0]);
		double lon = new Double(part[1]);

		Position pos = coordinateTools.coordinatesToLocal(lat, lon);
		this.i = pos.i;
		this.j = pos.j;
	}

	/**
	 * Print the position in the form "row.col".
	 * 
	 * @return the String representing the position
	 */
	public String toString() {
		return i + "." + j;
	}

	/**
	 * Calculate the position's hashcode, which is the toString() output's hash
	 * code.
	 * 
	 * @return the hashcode of the toString() output
	 */
	public int hashCode() {
		return toString().hashCode();
	}

	/**
	 * Calculate the resulting position if the you were to move in the direction
	 * given by dir.
	 * 
	 * @param rawDir
	 *            the direction in which to calculate the movement. it will be
	 *            normalized to modulo 8
	 * @return a new Position object with the resulting position, if the
	 *         resulting position is not a wall
	 * @throws PositionOnAWallException
	 *             if the resulting position is a wall.
	 */
	public Position calculateMove(final int rawDir)
			throws PositionOnAWallException {
		int dir = ((rawDir % DIRECTIONS) + DIRECTIONS) % DIRECTIONS;

		Position p = new Position(this.i + COMPASS[dir][0], this.j
				+ COMPASS[dir][1]);

		if (world.isAWall(p)) {
			throw new PositionOnAWallException();
		}

		return p;
	}

	/**
	 * Equals method which returns true if the positions have the same row and
	 * column.
	 * 
	 * @param o
	 *            the object to compare this position to
	 * @return true if the positions are equal
	 */
	public boolean equals(final Object o) {
		if (o instanceof Position) {
			Position p = (Position) o;

			if ((p.i == i) && (p.j == j)) {
				return true;
			} else {
				return false;
			}
		} else {
			throw new RuntimeException("Tried to compare a Position with a "
					+ o.getClass().getCanonicalName());
		}
	}

	/**
	 * The comparable interface for positions. A position compares larger than
	 * another one if the row is lower or, being in equal rows if the column is
	 * more to the left. In other words, bottom left, tiny, top right, big.
	 * 
	 * @param o
	 *            the object to compare to
	 * @return an integer according to the comparable interface
	 */
	public int compareTo(final Position p) {
		if (p.i > i) {
			return -1;
		} else if (p.i < i) {
			return 1;
		} else if (p.j > j) {
			return -1;
		} else if (p.j < j) {
			return 1;
		} else {
			return (0);
		}
	}

	/**
	 * Check if a position is near another, in terms grid points. The maximum
	 * distance is 15.
	 * 
	 * @param pos
	 *            the position to check
	 * @return true if the positions are near each other
	 */
	public boolean isNear(final Position pos) {
		return isNear(pos, NEAR_DISTANCE);
	}

	/**
	 * Check if a position is near another, in terms grid points. The maximum
	 * distance is given by radius.
	 * 
	 * @param pos
	 *            the position to check
	 * @param radius
	 *            the maximum distance to consider as near
	 * @return true if the positions are near each other
	 */
	public boolean isNear(final Position pos, final int radius) {
		if (Math.abs(pos.j - j) > radius) {
			return false;
		}

		if (Math.abs(pos.i - i) > radius) {
			return false;
		}

		double distanceI = Math.pow(pos.i - i, 2);
		double distanceJ = Math.pow(pos.j - j, 2);
		double distance = Math.sqrt(distanceI + distanceJ);
		if (distance > radius) {
			return false;
		}

		return true;
	}

	/**
	 * Get the latitude and longitude associated to this position.
	 * 
	 * @return a double array with the latitude as the first value, and the
	 *         longitude as the second
	 */
	public double[] getCoordinates() {
		return coordinateTools.localToCoordinates(this);
	}

	/**
	 * Get a String with the pretty printed coordinates represented by this
	 * Position object.
	 * 
	 * @see CoordinateTools#localToCoordinates(Position)
	 * @return the pretty formated coordinates string
	 */
	public String getPrettyCoordinates() {
		return coordinateTools.localToPrettyCoordinates(this);
	}

	/**
	 * Flattens the data in this Position object into the form:
	 * <code>de.nec.nle.siafu.model.Position:Lat#Lon</code> where Lat and Lon
	 * are the latitude and longitude in decimal format.
	 * 
	 * @return the flattened position
	 */
	public FlatData flatten() {
		String data;
		double[] coords = coordinateTools.localToCoordinates(this);
		data = this.getClass().getName() + ":";
		data += coords[0] + "#" + coords[1];
		return new FlatData(data);
	}

	/**
	 * Get the row for this position. The first row, 0, is on top of the map.
	 * 
	 * @return the row number
	 */
	public int getRow() {
		return i;
	}

	/**
	 * Set the row for this position. The first row, 0, is on top of the map.
	 * 
	 * @param newI
	 *            the row number
	 */
	public void setRow(final int newI) {
		this.i = newI;
	}

	/**
	 * Get the column for this position. The first column, 0, is on the left
	 * side of the map.
	 * 
	 * @return the row number
	 */
	public int getCol() {
		return j;
	}

	/**
	 * set the column for this position. The first column, 0, is on the left
	 * side of the map.
	 * 
	 * @param newJ
	 *            the row number
	 */

	public void setCol(final int newJ) {
		this.j = newJ;
	}
}
