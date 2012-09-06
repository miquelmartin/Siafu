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

import org.apache.commons.configuration.Configuration;

import de.nec.nle.siafu.types.BooleanType;

/**
 * A binary overlay returns a BooleanType object (a publishable boolean) set
 * to true or false depending on whether the value at a given postion is above
 * or below a unique threshold.
 * 
 * @author Miquel Martin
 * 
 */
public class BinaryOverlay extends Overlay {

	/**
	 * The value threshold. If the matrix value in a position is under it,
	 * getValue will return false.
	 */
	private int threshold;

	/**
	 * Create a binary overlay from an image.
	 * 
	 * @param name the overlay name
	 * @param is the InputStream with the image that represents the values
	 * @param simulationConfig the configuration with the threshold details
	 */
	public BinaryOverlay(final String name, final InputStream is,
			final Configuration simulationConfig) {
		super(name, is);

		threshold =
				simulationConfig.getInt("overlays." + name
						+ "[@thresholdvalue]");
	}

	/**
	 * Create an overalay from scratch by providing all the necessary
	 * parameters.
	 * 
	 * @param name the overlay name
	 * @param value the value matrix of the overlay
	 * @param threshold the threshold beyond which the overlay value becomes
	 *            true
	 */
	public BinaryOverlay(final String name, final int[][] value,
			final int threshold) {
		super(name, value);
		this.threshold = threshold;
	}

	/**
	 * Return the value of the overlay as a BooleanType object. The value of
	 * the return will be true if the matrix value at the given position is
	 * above the threshold, and false otherwise.
	 * 
	 * @param pos the position to evaluate
	 * @return a BooleanType with true or false depending on the overlay
	 *         value.
	 */
	public BooleanType getValue(final Position pos) {
		int val = this.value[pos.getRow()][pos.getCol()];

		if (val <= threshold) {
			return new BooleanType(true);
		} else {
			return new BooleanType(false);
		}
	}

	/**
	 * Get the threshold of this binary overlay.
	 * 
	 * @return an integer representing the pixel value of the threshold
	 */
	public int getThreshold() {
		return threshold;
	}

}
