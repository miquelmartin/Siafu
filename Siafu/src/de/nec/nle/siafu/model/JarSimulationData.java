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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * There is one instance of this class per run of Siafu. It encapsulates the
 * simulation information, including the agent, world and context models, the
 * configuration file, the image maps and the sprites. It provides an easy
 * interface to retreive all the information.
 * 
 * @author Miquel Martin
 * 
 */
public class JarSimulationData extends SimulationData {
	/** The JarFile that contains the simulation data. */
	private JarFile jar;

	/**
	 * Builds a SimulationData object taking the path to the simulation data
	 * as parameter.
	 * 
	 * @param path the File pointing to the simulation jar file
	 */
	public JarSimulationData(final File path) {
		super(path);
		try {
			jar = new JarFile(path);
		} catch (IOException e) {
			throw new RuntimeException("Error reading the simulation jar at "
					+ path);
		}
	}

	/**
	 * Performs the operations of getMapFilesOfType in the case that the
	 * simulation path is specified as a jar file.
	 * 
	 * @param path path to the type of files to be retrieved
	 * @return a map with pairs of overlay names and their data
	 */
	protected HashMap<String, InputStream> getFilesByPath(final String path) {
		HashMap<String, InputStream> foundFiles;
		foundFiles = new HashMap<String, InputStream>();

		Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			String name = entry.getName();
			if (name.startsWith(path) && name.endsWith(".png")) {
				int beginIndex = name.lastIndexOf("/") + 1;
				int endIndex = name.lastIndexOf(".");
				String niceName = name.substring(beginIndex, endIndex);
				InputStream stream;
				try {
					stream = jar.getInputStream(entry);
				} catch (IOException e) {
					throw new RuntimeException("Can't extract " + name
							+ " from " + jar.getName());
				}
				foundFiles.put(niceName, stream);
			}
		}
		return foundFiles;
		// FIXME: So, when do we close the jar file?
		// FIXME list what you've found
	}
	
	/**
	 * Performs the operations of getFileNamesByType in the case that the
	 * simulation path is specified as a jar file.
	 * 
	 * @param path path to the type of files to be retrieved
	 * @return a an ArrayList with the file names
	 */
	protected ArrayList<String> getFileNamesByPath(final String path) {
		ArrayList<String> foundFiles;
		foundFiles = new ArrayList<String>();

		Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			String name = entry.getName();
			if (name.startsWith(path) && name.endsWith(".png")) {
				int beginIndex = name.lastIndexOf("/") + 1;
				int endIndex = name.lastIndexOf(".");
				String niceName = name.substring(beginIndex, endIndex);
				foundFiles.add(niceName);
			}
		}
		return foundFiles;
		// FIXME: So, when do we close the jar file?
		// FIXME list what you've found
	}

	/**
	 * Get a file from the jar, as specified by path.
	 * 
	 * @param path the path of the file
	 * @return the InputStream for the requested file
	 */
	protected InputStream getFile(final String path) {
		JarEntry entry = null;
		Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements()) {
			entry = entries.nextElement();
			if (entry.getName().equals(path)) {
				try {
					return jar.getInputStream(entry);
				} catch (IOException e) {
					throw new RuntimeException("Can not extract " + path);
				}
			}
		}
		throw new RuntimeException("Your simulation data is missing " + path);
	}
}
