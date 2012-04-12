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

import de.nec.nle.siafu.types.IntegerNumber;

/**
 * A real overlay does not perform any mapping between the matrix values and
 * the returned value. This means that the return value of getValue is an
 * IntegerNumber with the matrix value (or the pixel value for image generated
 * overlays).
 * 
 * The pixelvalue is the representation of the RGB values. In this sense,
 * white which FFFFFFx0 yields a maximal pixel value and black, 000000x0
 * yields the minimum
 * 
 * @author Miquel Martin
 * 
 */
public class RealOverlay extends Overlay {

	/**
	 * Create a real overlay.
	 * 
	 * @param name the name of the overlay
	 * @param is the InputStream with the image that represents the values
	 */
	public RealOverlay(final String name, final InputStream is) {
		super(name, is);
	}

	/**
	 * Manually create a real overlay by providing all of its parameters.
	 * 
	 * @param name the name of the overaly
	 * @param value an integer matrix with the values at each position of the
	 *            map
	 */
	public RealOverlay(final String name, final int[][] value) {
		super(name, value);
	}

	/**
	 * Return the value mapped to this position. That is, an IntegerNumber
	 * with the matrix value (or the pixel value for image generated
	 * overlays).
	 * 
	 * @param pos the position to evaluate
	 * @return the overlay value in the position
	 */
	public IntegerNumber getValue(final Position pos) {
		return new IntegerNumber(value[pos.getRow()][pos.getCol()]);
	}

}
