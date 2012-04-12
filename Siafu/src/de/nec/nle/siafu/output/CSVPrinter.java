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

package de.nec.nle.siafu.output;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.configuration.Configuration;

import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.Overlay;
import de.nec.nle.siafu.model.World;
import de.nec.nle.siafu.types.BooleanType;
import de.nec.nle.siafu.types.Publishable;
import de.nec.nle.siafu.types.Text;

/**
 * This implementation of <code>SimulatorOutputPrinter</code> prints all the
 * information of an agent, plus the value of the overlays (that is, the whole
 * context set) for each of the users, in a Comma Separated Value file format.
 * <p>
 * Each time the interval given in the config file has passed, a printout is
 * generated. If the history option is set to true, the printout will accumulate
 * the data for all the iterations. Otherwise only the latest iteration will be
 * kept.
 * <p>
 * Note that all the fields in the CSV are common to all the entities, and
 * derived from the agent returned by
 * <code>World.getPeople().iterator().next()</code>. Therefore, you need to make
 * sure this particular user has all the entries in his data fields.
 * 
 * @author Miquel Martin
 * 
 */
public class CSVPrinter implements SimulatorOutputPrinter {

	/**
	 * The size of the buffer used in the <code>BufferedOutputStream</code> when
	 * writing the output file.
	 */
	private static final int BUFFER_SIZE = 102400;

	/**
	 * Conversion factor between secons and millisecons. You gotta love
	 * CheckStyle for forcing you to define these...
	 */
	private static final int SECOND_TO_MS_FACTOR = 1000;

	/**
	 * The <code>World</code> we have to print about.
	 */
	private World world;

	/**
	 * The header to print at the beginning of each output file. This is
	 * generated from a model user, as explained in the class description.
	 */
	private String header;

	/**
	 * The file name where the printout is to be stored. Defined by the
	 * configuration file provided in the command line.
	 */
	private String outputPath;

	/**
	 * The <code>File</code> in which we store the printed information. This is
	 * a temporary file if the simulation doesn't keep the history (and later
	 * gets copied to the configured one) or directly the configured file if
	 * history is kept.
	 */
	private File outputFile;

	/**
	 * The output stream associated to the <code>outputFile</code>.
	 */
	private BufferedOutputStream out;

	/**
	 * If set to true, keep the data from the previous iteration in the outputed
	 * file. Otherwise, ensure that the output file contains only a snapshot of
	 * the latest iteration.
	 */
	private boolean keepHistory;

	/**
	 * How much simulation time in ms must pass between printouts. Note that the
	 * printout will occur for the next iteration which happens later than
	 * <code>interval</code> simulation seconds from the last printout.
	 */
	private int intervalInMillis;

	/**
	 * The simulation's calendar time (as given by
	 * <code>getTimeInMillis()</code> of the last printout.
	 */
	private long lastPrintoutTime;

	/**
	 * Builds a <code>CVSPrinter</code> object. If history is to be kept, this
	 * already initializes the output file.
	 * 
	 * @param world
	 *            the world to print about
	 * @param config
	 *            the simulation configuration file
	 */
	public CSVPrinter(final World world, final Configuration config) {
		this.world = world;
		this.outputPath = config.getString("output.csv.path");
		this.keepHistory = config.getBoolean("output.csv.keephistory");
		this.intervalInMillis = SECOND_TO_MS_FACTOR
				* config.getInt("output.csv.interval");

		this.header = createHeader();

		if (keepHistory) {
			initializeFile(outputPath);
		}
	}

	/**
	 * Create the header line of the CSV file.
	 * 
	 * @return the generated header
	 */
	private String createHeader() {
		header = new String();

		// Person class intrinsec info
		header += "time,";
		header += "entityID,";
		header += "position,";
		header += "atDestination,";
		// header += "destination,";

		// Person info fields
		String[] infoFields = Agent.getInfoKeys().toArray(new String[0]);

		for (String field : infoFields) {
			header += (field + ",");
		}

		// Overlays
		for (String overlay : world.getOverlays().keySet()) {
			header += (overlay + ",");
		}

		// Remove last comma
		return header.substring(0, header.lastIndexOf(","));
	}

	/**
	 * Prints the information of all the agents into the output file if
	 * <code>interval</code> time has passed since the last printout. This will
	 * wipe out the old information in the file, unless the
	 * <code>keepHistory</code> option has been enabled in the config file.
	 */
	public void notifyIterationConcluded() {
		long lastPrintoutAge = world.getTime().getTimeInMillis()
				- lastPrintoutTime;
		if (lastPrintoutAge > intervalInMillis) {
			lastPrintoutTime = world.getTime().getTimeInMillis();

			if (!keepHistory) {
				initializeFile(outputPath + ".tmp");
			}

			for (Agent agent : world.getPeople()) {
				add(agent);
			}

			if (!keepHistory) {
				cleanup();
				outputFile.renameTo(new File(outputPath));
			}
		}
	}

	/**
	 * Creates the file to print to, and adds the header. This is done everytime
	 * an iteration is printed if the history is not kept, or just once at the
	 * beginning of the simulation if we want a single file with all the data.
	 * 
	 * @param filePath
	 *            the path to the file where we store the printed data. Note
	 *            that, when not keeping the history, this is a temporary file,
	 *            which is moved onto the configured output path at each
	 *            iteration.
	 */
	private void initializeFile(final String filePath) {
		try {
			outputFile = new File(filePath);

			PrintStream outStream = new PrintStream(outputFile);
			out = new BufferedOutputStream(outStream, BUFFER_SIZE);
		} catch (Exception e) {
			throw new RuntimeException("Can't create the output file: "
					+ filePath, e);
		}

		try {
			out.write(header.getBytes(), 0, header.length());
			out.write("\n".getBytes(), 0, 1);
		} catch (IOException e) {
			throw new RuntimeException("Can't write the output file", e);
		}
	}

	/**
	 * Add an agent's info to the printed data.
	 * 
	 * @param agent
	 *            the agent to print
	 */
	private void add(final Agent agent) {
		String line = new String();
		// Person class intrinsec info
		line += addPersonIntrinsecInfo(agent);

		// Info
		line += addInfoFields(agent);

		// Overlays
		line += addOverlayInfo(agent);

		// Remove last comma
		writeLine(line);
	}

	/**
	 * Write a line in the output file.
	 * 
	 * @param line
	 *            the line to write
	 */
	private void writeLine(final String line) {
		String trimmedline = line.substring(0, line.lastIndexOf(","));

		try {
			out.write(trimmedline.getBytes(), 0, trimmedline.length());
			out.write("\n".getBytes(), 0, 1);
		} catch (IOException e) {
			throw new RuntimeException("Can't write the output file", e);
		}
	}

	/**
	 * Adds to the printout line those fields which are simulation intrinsec
	 * information, namely the time, the agent's name, his position and his
	 * destination.
	 * 
	 * @param agent
	 *            the agent whose intrinsec information we want to add
	 * @return the line we generate
	 */
	private String addPersonIntrinsecInfo(final Agent agent) {
		String line = new String();
		line += (new Text("" + world.getTime().getTimeInMillis()).flatten() + ",");
		line += (new Text(agent.getName()).flatten() + ",");
		line += (agent.getPos().flatten() + ",");
		line += (new BooleanType(agent.isAtDestination()).flatten() + ",");
		// line += (agent.getDestination().flatten() + ",");

		return line;
	}

	/**
	 * Adds to the printout line those fields which are specific to an agent,
	 * that is, those contained in the info fields of the agent.
	 * 
	 * @param agent
	 *            the agent whose information we want to add
	 * @return the line we generate
	 */
	private String addInfoFields(final Agent agent) {
		String line = new String();

		for (Publishable info : agent.getInfoValues()) {
			if (info == null) {
				throw new RuntimeException(
						"You can't have null values in the Agent's info if you are using a CSVPrinter");
			}
			line += (info.flatten() + ",");
		}
		return line;
	}

	/**
	 * Adds to the printout line the value of each overlay, at the position of
	 * the agent.
	 * 
	 * @param agent
	 *            the agent whose information we want to add
	 * @return the line we generate
	 */
	private String addOverlayInfo(final Agent agent) {
		String line = new String();
		for (Overlay overlay : world.getOverlays().values()) {
			line += (new Text(overlay.getValue(agent.getPos()) + ","))
					.flatten();
		}
		return line;
	}

	/**
	 * Flushes the remaining data, and closes the output files.
	 */
	public void cleanup() {
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			throw new RuntimeException("Can't close the output file", e);
		}
	}

}
