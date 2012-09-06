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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * There is one instance of this class per run of Siafu. It encapsulates the
 * simulation information, including the agent, world and context models, the
 * configuration file, the image maps and the sprites. It provides an easy
 * interface to retreive all the information.
 * 
 * @author Miquel Martin
 * 
 */
public class DirectorySimulationData extends SimulationData {

	/** The path to the simulation data, whic is a folder. */
	private File dir;

	/**
	 * Builds a SimulationData object taking the path to the simulation data
	 * as parameter.
	 * 
	 * @param dir the simulation directory
	 */
	public DirectorySimulationData(final File dir) {
		super(dir);
		this.dir = dir;
	}

	/**
	 * Performs the operations of getFilesByPath in the case that the
	 * simulation path is specified as a folder.
	 * 
	 * @param path path to the type of files to be retrieved
	 * @return a map with pairs of overlay names and their data
	 */
	protected HashMap<String, InputStream> getFilesByPath(final String path) {
		HashMap<String, InputStream> foundFiles;
		foundFiles = new HashMap<String, InputStream>();

		String overlayPathString =
				dir.getAbsolutePath() + File.separator + path;
		File absolutePath = new File(overlayPathString);

		String[] files = absolutePath.list(new FilenameFilter() {
			public boolean accept(final File directory, final String name) {
				return name.endsWith(".png");
			}
		});

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				int end = files[i].lastIndexOf(".");
				String name = files[i].substring(0, end);
				String fileName =
						overlayPathString + File.separator + files[i];
				try {
					foundFiles.put(name, new FileInputStream(fileName));
				} catch (FileNotFoundException e) {
					throw new RuntimeException("Can't find " + fileName
							+ ".");
				}
			}
		}
		return foundFiles;
	}

	/**
	 * Performs the operations of getFileNamesByPath in the case that the
	 * simulation path is specified as a folder.
	 * 
	 * @param path path to the type of files to be retrieved
	 * @return a map with pairs of overlay names and their data
	 */
	protected ArrayList<String> getFileNamesByPath(final String path) {
		ArrayList<String> foundFiles;
		foundFiles = new ArrayList<String>();

		String overlayPathString =
				dir.getAbsolutePath() + File.separator + path;
		File absolutePath = new File(overlayPathString);

		String[] files = absolutePath.list(new FilenameFilter() {
			public boolean accept(final File directory, final String name) {
				return name.endsWith(".png");
			}
		});

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				int end = files[i].lastIndexOf(".");
				String name = files[i].substring(0, end);
				foundFiles.add(name);
			}
		}
		return foundFiles;
	}

	/**
	 * Get a file from the simulation directory, as specified by path.
	 * 
	 * @param path the path of the file
	 * @return the InputStream for the requested file
	 */
	protected InputStream getFile(final String path) {
		InputStream file = classLoader.getResourceAsStream(path);
		if (file == null)
			throw new RuntimeException("Your simulation data is missing "
					+ path + ". Perhaps you opened the wrong directory?");
		return file;
	}
}
