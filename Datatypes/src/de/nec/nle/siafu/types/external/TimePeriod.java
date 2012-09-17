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

package de.nec.nle.siafu.types.external;

import de.nec.nle.siafu.exceptions.external.InvalidFlatDataException;

/**
 * This class represents a deserialized Place object. It is, however, <i>not</i>
 * a Place object in the Siafu sense of the class, but rather a skeleton
 * representation with the same data fields, but none of the logic.
 * <p>
 * The purpose of this class is to facilitate dealing with FlatData strings
 * returned by Siafu (using the String constructor), so they can be handled as
 * Java objects.
 * <p>
 * Notice that the package in this class contains "external" as the last sub
 * package, to avoid erratic behavior should you (mistakenly) add this library
 * to a simulation or a Siafu deployment.
 * 
 * @see de.nec.nle.siafu.types.TimePeriod
 * 
 * @author Miquel Martin
 * 
 */
public class TimePeriod implements Publishable {
	/**
	 * The start of the period.
	 */
	private EasyTime start;

	/**
	 * The end of the period.
	 */
	private EasyTime end;

	/**
	 * Creates a TimePeriod object using the specified begin and end
	 * <code>EasyTime</code> objects.
	 * 
	 * @param start
	 *            the start of the period
	 * @param end
	 *            the end of the period
	 */
	public TimePeriod(final EasyTime start, final EasyTime end) {
		this.start = start;
		this.end = end;
	}

	/**
	 * Builds a TimePeriod object by taking a FlatData object of the format
	 * <code>EasyTime:startHH#startMM#endHH#endMM</code>.
	 * 
	 * @param flatData
	 *            the flattened data with the time period
	 * @throws InvalidFlatDataException
	 *             if the provided flatdata is not properly formatted, or is not
	 *             a flattened form of this class.
	 */
	public TimePeriod(final String flatData) throws InvalidFlatDataException {
		final int startHHField = 0;
		final int startMMField = 1;
		final int endHHField = 2;
		final int endMMField = 3;
		TypeUtils.check(this, flatData);
		String period = flatData.substring(flatData.indexOf(':') + 1);
		String[] part = TypeUtils.split(period, '#');
		this.start = new EasyTime(new Integer(part[startHHField]).intValue(),
				new Integer(part[startMMField]).intValue());
		this.end = new EasyTime(new Integer(part[endHHField]).intValue(),
				new Integer(part[endMMField]).intValue());
	}

	/**
	 * Get the <code>EasyTime</code> object that represents the start of this
	 * time period.
	 * 
	 * @return the start of the period in an EasyTime object
	 */
	public EasyTime getStart() {
		return start;
	}

	/**
	 * Get the <code>EasyTime</code> object that represents the end of this
	 * time period.
	 * 
	 * @return the end of the period in an EasyTime object
	 */
	public EasyTime getEnd() {
		return end;
	}

	/**
	 * Returns a representation of the time with the format HH:MM.
	 * 
	 * @return the time string
	 */
	public String toString() {
		return start.toString() + "-" + end.toString();
	}

	/**
	 * Generates a flattened version of the data, of the type
	 * <code>TimePeriod:startHour#startMinute#endHour#endMinute</code>.
	 * 
	 * @return a flatdata String representing this object
	 */
	public String flatten() {
		String data;
		// We use simple name, because the fleshed out version of this class is
		// in the default package for data types: de.nec.nle.siafu.data
		String fullClassName = this.getClass().getName();
		String className = fullClassName.substring(fullClassName
				.lastIndexOf(".")+1);
		data = className + ":";

		data += start.getHour() + "#" + start.getMinute();
		data += "#";
		data += end.getHour() + "#" + end.getMinute();
		return data;
	}

	public boolean equals(Object o) {
		if (!(o instanceof TimePeriod)) {
			return false;
		} else {
			TimePeriod tp = (TimePeriod) o;
			return (tp.getStart().equals(start) && tp.getEnd().equals(end));
		}
	}

	public int hashCode() {
		return (this.getClass().getName() + start + end).hashCode();
	}
}
