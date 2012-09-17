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
import org.eclipse.swt.graphics.ImageData;

import de.nec.nle.siafu.graphics.Overlayable;
import de.nec.nle.siafu.types.Publishable;

/**
 * An overlay is in essence a matrix which maps a value to each position of
 * the simulated world. Depending on the overlay type, the value can be a
 * boolean value, a discrete value, or others, wrapped in the Publishable
 * format.
 * 
 * An overlay can be created from an image provided in the simulation data, or
 * manually by setting the values of the matrix.
 * 
 * @author Miquel Martin
 * 
 */
public abstract class Overlay implements Overlayable {
	/**
	 * The value matrix. This values are integers, and are mapped to the real
	 * output by the specific implementation of getValue().
	 */
	protected int[][] value;

	/**
	 * The overlay name. Note that two overlays are the same if they have the
	 * same name.
	 */
	protected String name;

	/**
	 * Perform the common operations to create an overlay from an image: set
	 * the name and read the values from the InputStream.
	 * 
	 * @param name the overlay name
	 * @param is the InputStream with the image that represents the overlay
	 *            values
	 */
	protected Overlay(final String name, final InputStream is) {
		this.name = name;
		value = fillMatrix(is);
	}

	/**
	 * Perform the common operations to manually create an overlay: set the
	 * name and keep the values.
	 * 
	 * @param name the overlay name
	 * @param value the values for the overaly
	 */
	protected Overlay(final String name, final int[][] value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Get an instance of Overlay which fits the particular overlay defined in
	 * the simulation data. The values come from the InputStream obtained from
	 * simulation data, and the name, type and type details are obtained from
	 * the simulation configuration.
	 * 
	 * @param name the overlay name
	 * @param is the InputStream that represents the values
	 * @param simulationConfig the configuration details for the overlay
	 * @return an instance of Overlay whose getValue() method returns what you
	 *         would expect from the overlay subtype
	 */
	public static Overlay getOverlay(final String name, final InputStream is,
			final Configuration simulationConfig) {
		String type =
				simulationConfig.getString("overlays." + name + "[@type]");
		if (type == null) {
			throw new RuntimeException("Configuration missing for overlay "
					+ name);
		}
		if (type.equals("binary")) {
			return new BinaryOverlay(name, is, simulationConfig);
		} else if (type.equals("discrete")) {
			return new DiscreteOverlay(name, is, simulationConfig);
		} else if (type.equals("real")) {
			return new RealOverlay(name, is);
		} else {
			throw new RuntimeException("Unknown overlay type for " + name);
		}
	}

	/**
	 * Fill the value matrix from the data in an InputStream which is linked
	 * to an image.
	 * 
	 * @param is the InputStream with the image
	 * @return the filled value matrix
	 */
	protected int[][] fillMatrix(final InputStream is) {
		ImageData image = new ImageData(is);
		value = new int[image.height][image.width];

		for (int i = 0; i < value.length; i++) {
			image.getPixels(0, i, value[i].length, value[i], 0);
		}

		return value;
	}

	/**
	 * Get the name of the overlay.
	 * 
	 * @return the overlay name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the matrix with the integer values of the overlay. This values are
	 * independent on how the overlay type actually maps them to a Publishable
	 * value.
	 * 
	 * @return an integer matrix with the values
	 */
	public int[][] getValueMatrix() {
		return value;
	}

	/**
	 * Get a Publishable object that represents the value, as mapped by the
	 * overlay type. For instance, a binary overlay will return a BooleanType,
	 * and a discrete one will return a Text.
	 * 
	 * @param pos the position for which we want to read the value
	 * @return the Publishable with the value
	 */
	public abstract Publishable getValue(Position pos);

	/**
	 * Get a string representation of the overlay by printing out the
	 * overlay's name.
	 * 
	 * @return the overlay's name
	 */
	public String toString() {
		return name;
	}

	/**
	 * Compare two overlays. Two overlays are the same if they have the same
	 * name, regarding of their value matrix.
	 * 
	 * @param obj the Object to compare this overlay to
	 * @return true if the overlays are the same
	 */
	public boolean equals(final Object obj) {
		if (obj == null || !(obj instanceof Overlay)) {
			return false;
		} else {
			return name.equals(((Overlay) obj).getName());
		}
	}

	/**
	 * Get the hash code value for the overlay.
	 * 
	 * @return the hash code value for the overlay
	 */
	public int hashCode() {
		return name.hashCode();
	}
}
