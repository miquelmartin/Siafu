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

package de.nec.nle.siafu.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Utility class to generate unique names. The first 1000 belong to several
 * international lists of hurricane names. After that, the returned names are
 * of the format "Person-#" where # is a number starting at 1.
 * 
 * The names are generated in the same sequence everytime the class is loaded.
 * 
 * @author Miquel Martin
 * 
 */
public final class SequentialNamer {

	/**
	 * Ensure the utility class is not instantiated.
	 * 
	 */
	private SequentialNamer() {
	}

	/**
	 * The reader linked to the file containing the name list. The file is
	 * /res/misc/NameList.txt
	 */
	private static BufferedReader nameList;

	/**
	 * The number to append to "Person-" next time getNameList is called, if
	 * we have run out of the first 1000 names.
	 */
	private static long index = 1;

	/**
	 * Create the BufferedReader where names are read from.
	 * 
	 */
	private static void getNameList() {
		InputStream is =
				new Object().getClass().getResourceAsStream(
					"/res/misc/NameList.txt");
		nameList = new BufferedReader(new InputStreamReader(is));
	}

	/**
	 * Get the next name. The names are unique. The sequence of names is
	 * constant throughout class loads.
	 * 
	 * @return a random name
	 * @throws RuntimeException if called more than 1000+Long.MAX_VALUE times
	 * 
	 */
	public static String getNextName() {
		if (nameList == null) {
			getNameList();
		}

		try {
			if (nameList.ready()) {
				return nameList.readLine();
			} else {
				if (index > Long.MAX_VALUE) {
					throw new RuntimeException(
							"Too many names asked out of the RandomNamer.");
				}
				return "Person" + (index++);
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Strange occurred when reading the default name list");
		}
	}
}
