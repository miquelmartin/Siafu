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

import java.util.Random;

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
 * @see de.nec.nle.siafu.types.EasyTime
 * 
 * @author Miquel Martin
 * 
 */
public class EasyTime implements Publishable {
	/** Minutes per hour. */
	private static final int MINS_PER_HOUR = 60;

	/** Hours per day. */
	private static final int HOURS_PER_DAY = 24;

	/** A reusable random object. */
	private Random rand = new Random();

	/** Hour of the day in 24h format. */
	private int hour;

	/**
	 * Minute of the hour.
	 */
	private int minute;

	/**
	 * Builds an EasyTime object by taking a FlatData object of the format
	 * <code>EasyTime:HH#MM</code>.
	 * 
	 * @param flatData
	 *            the flattened data with the time
	 * @throws InvalidFlatDataException
	 *             if the provided flatdata is not properly formatted, or is not
	 *             a flattened form of this class.
	 */
	public EasyTime(final String flatData) throws InvalidFlatDataException {
		TypeUtils.check(this, flatData);
		this.hour = new Integer(flatData.substring(flatData.indexOf(':') + 1,
				flatData.indexOf('#'))).intValue();
		this.minute = new Integer(flatData.substring(flatData.indexOf('#') + 1))
				.intValue();
		normalize();
	}

	/**
	 * Creates new EasyTime which has the same time as the one provided as
	 * parameter.
	 * 
	 * @param time
	 *            the EasyTime object to clone.
	 */
	public EasyTime(final EasyTime time) {
		this.hour = time.getHour();
		this.minute = time.getMinute();
		normalize();
	}

	/**
	 * Creates an EastTime object using the hour and minute as parameters.
	 * 
	 * @param hour
	 *            the hour of the day, in 24 hour format
	 * @param minute
	 *            the minute of the hour
	 */
	public EasyTime(final int hour, final int minute) {
		this.hour = hour;
		this.minute = minute;
		normalize();
	}

	/**
	 * Normalizes the time so that hours are in the range 0..24 and minutes in
	 * the 0..60 range.
	 */
	private void normalize() {
		hour += minute / MINS_PER_HOUR;
		if (minute < 0) {
			minute %= MINS_PER_HOUR;
			minute += MINS_PER_HOUR;
			hour--;
		}
		minute %= MINS_PER_HOUR;

		if (hour < 0) {
			hour %= HOURS_PER_DAY;
			hour += HOURS_PER_DAY;
		}
		hour %= HOURS_PER_DAY;
	}

	/**
	 * Determines if this instance of EasyTime represents a time after the one
	 * given in the parameter.
	 * 
	 * @param t
	 *            the time to compare to
	 * @return true if this instance is after <code>t</code>, false otherwise
	 */
	public boolean isAfter(final EasyTime t) {
		if (hour > t.hour) {
			return true;
		} else if ((hour == t.hour) && (minute > t.minute)) {
			return true;
		}

		return false;
	}

	/**
	 * Determines if this instance of EasyTime represents a time before, or
	 * equal to the one given in the parameter.
	 * 
	 * @param t
	 *            the time to compare to
	 * @return true if this instance represents a time before or at equal to
	 *         that of <code>t</code>, false otherwise
	 */
	public boolean isBefore(final EasyTime t) {
		return !isAfter(t);
	}

	/**
	 * Determines if this instance of EasyTime is in the time period given by
	 * the parameter tp. The comparison conisders hour roll over at midnight
	 * when the period spans midnight.
	 * 
	 * @param tp
	 *            the <code>TimePeriod</code> to compare to
	 * @return true if this instance is after the start of the period, and
	 *         before or right at the end of the period.
	 */
	public boolean isIn(final TimePeriod tp) {
		EasyTime start = tp.getStart();
		EasyTime end = tp.getEnd();

		if (end.isAfter(start)) { // We went over 00h00

			if (isAfter(tp.getStart()) && isBefore(tp.getEnd())) {
				return true;
			}
		} else if (isAfter(tp.getStart()) || isBefore(tp.getEnd())) {
			return true;
		}

		return false;
	}

	/**
	 * Change this EasyTime by adding the hours and minutes of the EasyTime in
	 * the parameter and this one. Note thatthe hour and minutes can be negative
	 * values. Also note that no day is increased or decreased when the time
	 * goes before or after midnight.
	 * 
	 * @param hours
	 *            a number (positive or not) of hours to shift
	 * @param minutes
	 *            a number (positive or not) of minutes to shift
	 * @return this EasyTime, after the addition
	 */
	public EasyTime shift(final int hours, final int minutes) {
		this.hour += hours;
		this.minute += minutes;
		normalize();
		return this;
	}

	/**
	 * Change this EasyTime by adding the hours and minutes of the EasyTime in
	 * the parameter and this one. Note that<code>et</code> can contain
	 * negative values. Also note that no day is increased or decreased when the
	 * time goes before or after midnight.
	 * 
	 * @param et
	 *            an EasyTime object representing the number of hours and
	 *            minutes to shift
	 * @return this EasyTime, after the addition
	 */
	public EasyTime shift(final EasyTime et) {
		return shift(et.getHour(), et.getMinute());
	}

	/**
	 * Blurs the given EasyTime <code>time</code> by adding or removing a
	 * random amount of minutes, between 0 and blurMinutes.
	 * 
	 * @param blurMinutes
	 *            the number of minutes to add (if positive) or remove (if
	 *            negative)
	 * @return this EasyTime, after the blurring
	 */
	public EasyTime blur(final int blurMinutes) {
		int minuteShift = rand.nextInt(blurMinutes) - (blurMinutes / 2);
		this.shift(0, minuteShift);
		return this;
	}

	/**
	 * Get the hour of the day, in 24h format, for this object.
	 * 
	 * @return the hour of the day
	 */
	public int getHour() {
		return hour;
	}

	/**
	 * Get the minute of the hour for this object.
	 * 
	 * @return the minute in the hour
	 */
	public int getMinute() {
		return minute;
	}

	/**
	 * Get the time of this EasyTime object in seconds since midnight.
	 * 
	 * @return the time in seconds since midnight
	 */
	public int getTimeInSeconds() {
		final int hourToSeconds = 3600;
		final int minuteToSeconds = 60;
		return (hour * hourToSeconds) + (minute * minuteToSeconds);
	}

	/**
	 * Returns a representation of the time with the format HH:MM.
	 * 
	 * @return the time string
	 */
	public String toString() {
		return hour + ":" + minute;
	}

	/**
	 * Generates a flattened version of the data, of the type
	 * <code>EasyTime:HH#MM</code> where content is the string encapsulated in
	 * the <code>EasyTime</code> object.
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
		data += hour + "#" + minute;
		return data;
	}

	public boolean equals(Object o) {
		if (!(o instanceof EasyTime)) {
			return false;
		} else {
			EasyTime et = (EasyTime) o;
			return (et.getHour() == hour && et.getMinute() == minute);
		}
	}

	public int hashCode() {
		return (this.getClass().getName() + hour + " " + minute).hashCode();
	}
}
