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
import java.util.TreeMap;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;

import de.nec.nle.siafu.types.Text;

/**
 * A discrete overlay returns it's value as a Text object according to the
 * provided thresholds. The value corresponds to the tag of the smallest
 * threshold which is bigger than the integer value.
 * 
 * @author Miquel Martin
 * 
 */
public class DiscreteOverlay extends Overlay {
	/**
	 * The thresholds used to determine the value at a point.
	 */
	private int[] thresholds;

	/**
	 * The value tags associated to each threshold.
	 */
	private String[] tags;

	/**
	 * Create a discrete overlay using the thresholds int he configuration
	 * object.
	 * 
	 * @param name
	 *            the name of the overlay
	 * @param is
	 *            the InputStream with the image that represents the values
	 * @param simulationConfig
	 *            the configuration file where the threshold details are given
	 */
	public DiscreteOverlay(final String name, final InputStream is,
			final Configuration simulationConfig) {
		super(name, is);

		// A tree to sort the thresholds
		TreeMap<Integer, String> intervals = new TreeMap<Integer, String>();

		// Find out how many thresholds we have
		String[] property;
		try {
			property = simulationConfig.getStringArray("overlays." + name
					+ ".threshold[@tag]");
			if (property.length == 0)
				throw new ConfigurationRuntimeException();
		} catch (ConfigurationRuntimeException e) {
			throw new RuntimeException("You forgot the description of " + name
					+ " in the config file");
		}

		thresholds = new int[property.length];
		tags = new String[property.length];

		// Read the thresholds
		for (int i = 0; i < property.length; i++) {
			String tag = simulationConfig.getString("overlays." + name
					+ ".threshold(" + i + ")[@tag]");
			int pixelValue = simulationConfig.getInt("overlays." + name
					+ ".threshold(" + i + ")[@pixelvalue]");
			intervals.put(pixelValue, tag);
		}

		// Store the sorted thresholds
		int i = 0;
		for (int key : intervals.keySet()) {
			thresholds[i] = key;
			tags[i] = intervals.get(key);
			i++;
		}
	}

	/**
	 * Manually create a discrete overlay by providing all of its parameters.
	 * 
	 * @param name
	 *            the name of the overaly
	 * @param value
	 *            an integer matrix with the values at each position of the map
	 * @param thresholds
	 *            the thresholds that map integer values to Text values
	 * @param tags
	 *            the content of the Text value for each threshold
	 */
	public DiscreteOverlay(final String name, final int[][] value,
			final int[] thresholds, final String[] tags) {
		super(name, value);

		if (tags.length != thresholds.length) {
			throw new RuntimeException("You need as many tags as thresholds");
		}

		this.thresholds = thresholds;
		this.tags = tags;
	}

	/**
	 * Get a Text object with the value of the overlay in the given position. *
	 * The returned value is set according to the thresholds. The value
	 * corresponds to the tag of the smallest threshold which is bigger than the
	 * matrix value at the given position.
	 * 
	 * @param pos
	 *            the position for which we require the value
	 * @return a Text object with the value at that position
	 */
	public Text getValue(final Position pos) {
		int val = this.value[pos.getRow()][pos.getCol()];

		for (int i = 0; i < thresholds.length; i++) {
			if (val <= thresholds[i]) {
				return new Text(tags[i]);
			}
		}

		// Nothing found, we return the highest
		return new Text(tags[thresholds.length - 1]);
	}

	/**
	 * Get the thresholds of this discrete overlay.
	 * 
	 * @return an integer array representing the pixel value of the thresholds
	 */
	public int[] getThresholds() {
		return thresholds;
	}

	/**
	 * Get the tags associated to each threshold.
	 * 
	 * @return a String array with the tags
	 */
	public String[] getTags() {
		return tags;
	}

}
