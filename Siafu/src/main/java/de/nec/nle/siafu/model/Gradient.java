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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import de.nec.nle.siafu.exceptions.PositionUnreachableException;

/**
 * This class is used to generate and query the distance gradients that help the
 * Siafu Agents to navigate.
 * 
 * When an agent is ordered to head for a destination, it does so by following
 * something close to the shortest route. This deviation from optimal is due to
 * the discrete nature of the map Siafu uses. An agent can move only vertically,
 * horizontally or diagonally on top of a checker board. In other words, if you
 * were to walk on equal sizes steps, and always facing North, South, East,
 * West, NE, NW, SW or SE, you'd take the same route.
 * 
 * This is usually not a problem for more simulations, although all feedback is
 * welcome.
 * 
 * This quasi-shortest distance is determined by pre-calculating the distance
 * from every single point in the map to the desired destination. We call this a
 * distance gradient. The agent then knows what the new distance will be if he
 * moves to each of the positions adjacent to his current position, and chooses
 * the one that gets him closer to its target.
 * 
 * The gradient is calculated by growing distances from the destination point.
 * To do so, we calulate the distance from the target point to each of the
 * adjacent pixels. You can picture this as a 3d surface, where the height is
 * the distance to the destination point. If there were no wall,s this would be
 * the inside of an inverted cone. To walk the agent simply follows the
 * direction of biggest gradient (slope). When the distance grows from the
 * center, we consider the walls in the map, and so the optimal routes are
 * calculated.
 * 
 * @author Miquel Martin
 * 
 */
public class Gradient implements Serializable {
	/**
	 * The possible directions. They match N, NE, E.. NW for the values 0, 1, 2,
	 * 3.. 7.
	 */
	private static final int POSSIBLE_DIRS = 8;

	/** Serializable objects get an ID. */
	private static final long serialVersionUID = 9007616593340917939L;

	/**
	 * The distance to a place that is unreachable. It is inferior to any other
	 * distance, and equal to the maximum interger value possible. A wall is
	 * unreachable. So is an area completely surrounded by walls.
	 */
	public static final int UNREACHABLE = Integer.MAX_VALUE;

	/** Distance between two horizontallly or vertically adjacent points. */
	private static final int STRAIGHT_DISTANCE = 10;

	/** Distance between two diagonally adjacent points. */
	private static final int DIAGONAL_DISTANCE = 14;

	/**
	 * The distance from each row,col position in the map to the gradient
	 * center.
	 */
	private int[][] distance;

	/** The map height. */
	private final int h;

	/** The map width. */
	private final int w;

	/** The center of the gradient. */
	private Position center;

	/**
	 * Creates a full map gradient, which knows the distance from any point in
	 * the map to pos.
	 * 
	 * @param center
	 *            the gradient's center, that is, the position we want to reach
	 *            from anywhere on the map
	 * @param world
	 *            the world where we need to navigate
	 */
	public Gradient(final Position center, final World world) {
		this(center, world, null);
	}

	/**
	 * Creates a gradient (distance from points to pos).However, this is not a
	 * full map gradient, since the calculation ceases when we reach the desired
	 * point. If that point is not reachable, then the end result is a full map.
	 * There is now way to guarantee that a gradient created with this method
	 * will or will not be a full map gradient.
	 * 
	 * This is used, for instance, in the case of the user manually directing an
	 * Agent to an arbitrary point. Because the gradient will never be used
	 * again, there's no need to perform the full calculation.
	 * 
	 * @param center
	 *            the gradient's center, that is, the position we want to reach
	 *            from anywhere on the map
	 * @param relevantPos
	 *            the position where we start in order to go to the gradient's
	 *            center
	 * @param world
	 *            the world where we need to navigate
	 */
	public Gradient(final Position center, final World world,
			final Position relevantPos) {
		h = world.getHeight();
		w = world.getWidth();
		this.center = center;

		calculateGradient(world, relevantPos);
	}

	/**
	 * Returns the gradient's center position.
	 * 
	 * @return the string representing the center position
	 */
	public String toString() {
		return center.toString();
	}

	/**
	 * Returns the matrix with the distances from anywhere on the map to the
	 * center.
	 * 
	 * @return an int matrix with the distances in simulation grid points
	 */
	public int[][] getDistances() {
		return distance;
	}

	/**
	 * Get the height of the gradient (and of the map).
	 * 
	 * @return and int with the height
	 */
	public int getHeight() {
		return h;
	}

	/**
	 * Get the width of the gradient (and of the map).
	 * 
	 * @return an int with the width
	 */
	public int getWidth() {
		return w;
	}

	/**
	 * Calculate the distance from any point in the map to the center. If a
	 * relevant position is provided, the calculation stops when that position
	 * is reached.
	 * 
	 * @param world
	 *            the world, from which we take the walls
	 * @param relevantPos
	 *            the position we care about
	 */
	private void calculateGradient(final World world, final Position relevantPos) {
		distance = new int[h][w];
		boolean doneCalculating = false;
		boolean foundRelevantPos = false;

		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				distance[i][j] = UNREACHABLE;
			}
		}
		// The positions we still have to calculate in this round
		Set<Position> pending = new HashSet<Position>();

		// The newly discovered positions that will be pending on the next
		// round.
		Set<Position> next = new HashSet<Position>();

		// Make the center be at 0 distance from itself
		distance[center.getRow()][center.getCol()] = 0;
		pending.add(center);

		while (!doneCalculating) {
			// Calculate the distance to neighbouring points from each of the
			// pending ones
			for (Position pos : pending) {
				int distanceStraight = distance[pos.getRow()][pos.getCol()]
						+ STRAIGHT_DISTANCE;
				int distanceDiagonal = distance[pos.getRow()][pos.getCol()]
						+ DIAGONAL_DISTANCE;

				// Add the distance, if the neighbout is verticallly or
				// horizaontally reachable
				for (int dir = 0; dir < POSSIBLE_DIRS; dir += 2) {
					try {
						Position newPos = pos.calculateMove(dir);
						if (distanceStraight < distance[newPos.getRow()][newPos
								.getCol()]) {
							distance[newPos.getRow()][newPos.getCol()] = distanceStraight;
							next.add(newPos);
						}
					} catch (PositionUnreachableException e) {
						continue;
					}
				}

				// Add the distance, if the neighbouring position is
				// diagonally reachable
				for (int dir = 1; dir < POSSIBLE_DIRS; dir += 2) {
					try {
						Position newPos = pos.calculateMove(dir);
						if (distanceDiagonal < distance[newPos.getRow()][newPos
								.getCol()]) {
							distance[newPos.getRow()][newPos.getCol()] = distanceDiagonal;
							next.add(newPos);
						}
					} catch (PositionUnreachableException e) {
						continue;
					}
				}
				if (relevantPos != null && pending.contains(relevantPos)) {
					foundRelevantPos = true;
					doneCalculating = true;
				}
			}
			pending = next;
			next = new HashSet<Position>();

			if (pending.isEmpty()) {
				doneCalculating = true;
			}
		}
		if (relevantPos != null && !foundRelevantPos) {
			throw new PositionUnreachableException();
		}
	}

	/**
	 * Get the direction towards which an agent should walk in order to reach
	 * the gradient center fastest. If more than one direction yields the same
	 * distance, and one of them is the preferred dir, then the preferred dir is
	 * returned. Otherwise, the first optimal one is returned.
	 * 
	 * @param pos
	 *            the position from which we want to move to the center
	 * @param preferredDir
	 *            if more than one way is optimal, prefer this one. If
	 *            preferredDir is -1, there is no preferred dir
	 * @return the direction to follow
	 */
	public int pointFrom(final Position pos, final int preferredDir) {

		ArrayList<Integer> optimalDirs = new ArrayList<Integer>(POSSIBLE_DIRS);
		int min = distance[pos.getRow()][pos.getCol()];
		int grad;

		if (min == 0) {
			return -1;
		}

		for (int dir = 0; dir < POSSIBLE_DIRS; dir++) {
			Position aux;

			try {
				aux = pos.calculateMove(dir);
				grad = distance[aux.getRow()][aux.getCol()];
				if (grad == min) {
					optimalDirs.add(dir);
				} else if (grad < min) {
					min = grad;
					optimalDirs.clear();
					optimalDirs.add(dir);
				}
			} catch (PositionUnreachableException e) {
				continue;
			}
		}

		if (preferredDir != -1 && optimalDirs.contains(preferredDir)) {
			return preferredDir;
		} else {
			return optimalDirs.get(0);
		}

	}

	/**
	 * Returns the direction towards the center of the gradient or -1 if we are
	 * already there.
	 * 
	 * @param pos
	 *            the position for which we want to know the optimal direction
	 *            towards the gradient center
	 * @return the optimal direction or -1 if the destination has been reached
	 */
	public int pointFrom(final Position pos) {
		return pointFrom(pos, -1);
	}

	/**
	 * Return the distance in simulation grid points between pos and the
	 * gradient's center.
	 * 
	 * @param pos
	 *            the position to calculate the distance from
	 * @return the distance in simulation gridpoints
	 */
	public int distanceFrom(final Position pos) {
		return distance[pos.getRow()][pos.getCol()];
	}
}
