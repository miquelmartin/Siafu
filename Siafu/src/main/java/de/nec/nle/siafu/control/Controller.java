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

package de.nec.nle.siafu.control;

import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import de.nec.nle.siafu.control.progress.ConsoleProgress;
import de.nec.nle.siafu.control.progress.GUIProgress;
import de.nec.nle.siafu.control.progress.Progress;
import de.nec.nle.siafu.exceptions.GUINotReadyException;
import de.nec.nle.siafu.externalCommand.CommandListener;
import de.nec.nle.siafu.graphics.GUI;
import de.nec.nle.siafu.graphics.Markers;
import de.nec.nle.siafu.graphics.markers.Marker;
import de.nec.nle.siafu.model.SimulationData;
import de.nec.nle.siafu.model.Trackable;
import de.nec.nle.siafu.model.World;

/**
 * This is the main class of the simulator. Upon running its main method, a
 * new <code>Controller</code> is created. This, in turn starts three
 * threads:
 * <li> Simulation: does the actual simulation and number crunching
 * <li>GUI: runs the user interface, if selected in the configuration file
 * <li>CommandListener: listens for external commands on the TCP port defined
 * in the config file
 * <p>
 * Controller also acts as the reference point between the three threads,
 * allowing them to share objects, such as the <code>World</code> or each
 * other.
 * 
 * @author Miquel Martin
 * 
 */
public class Controller {
	/** Default value for CSV interval. */
	private static final int DEFAULT_CSV_INTERVAL = 300;

	/** Default value for the cache size. */
	private static final int DEFAULT_CACHE_SIZE = 100;

	/** Default value for the UI speed. */
	private static final int DEFAULT_UI_SPEED = 50;

	/** Default value for the TCP listening port. */
	private static final int DEFAULT_PORT = 4444;

	/** Default value for the gradient path. */
	public static final String DEFAULT_GRADIENT_PATH =
			System.getProperty("user.home") + File.separator + ".Siafu"
					+ File.separator + "CalculatedGradients"
					+ File.separator;

	/** Default config file location. */
	public static final String DEFAULT_CONFIG_FILE =
			System.getProperty("user.home") + File.separator + ".Siafu"
					+ File.separator + "config.xml";

	/**
	 * The configuration file for the simulator.
	 */
	private XMLConfiguration config;

	// FIXME refactor ro siafuConfig

	/**
	 * The simulation <code>Runnable</code>.
	 */
	private Simulation simulation;

	/**
	 * The GUI (if enabled in the configuration) for the simulation.
	 */
	private GUI gui;

	/** Whether the GUI is being used or not. */
	private boolean guiUsed;

	/**
	 * The Runnable that becomes a listening thread for commands on the TCP
	 * port.
	 */
	private CommandListener commandListener;

	/**
	 * The Progress object that displays simulation load status.
	 */
	private static Progress progress;

	/**
	 * Get the Progress class that displays simulation laod status.
	 * 
	 * @return the progress instance to use
	 */
	public static Progress getProgress() {
		return progress;
	}

	/**
	 * Initialize the simulator itself, and run the simulation.
	 * 
	 * @param configPath the file that defines the parameters of the
	 *            simulation
	 * @param simulationPath the path to the simulation data
	 */
	public Controller(final String configPath, final String simulationPath) {
		String verifiedConfigPath = configPath;

		if (configPath == null) {
			verifiedConfigPath = Controller.DEFAULT_CONFIG_FILE;
		}

		try {
			config = new XMLConfiguration(verifiedConfigPath);
			System.out.println("Using configuration at "
					+ verifiedConfigPath);
		} catch (ConfigurationException e) {
			System.out.println("The config file doesn't exist or "
					+ "is malformed. Recreating.");
			config = createDefaultConfigFile();
		}

		// Command Listener thread (for external commands)
		if (config.getBoolean("commandlistener.enable")) {
			int tcpPort = config.getInt("commandlistener.tcpport");
			try {
				commandListener = new CommandListener(this, tcpPort);
				// Start threads
				new Thread(commandListener, "Command Listener thread")
						.start();
			} catch (IOException e) {
				System.err.println("The TCP port " + tcpPort
						+ " is already in use. Is there another copy of "
						+ "Siafu running? Consider changing the port "
						+ "number in the config file.");
				return;
			}
		}

		guiUsed = config.getBoolean("ui.usegui");

		if (guiUsed) {
			// Printout to the GUI
			progress = new GUIProgress();

			// If there's a GUI, let it load the
			// simulation, if it's avaialble
			gui = new GUI(this, simulationPath);
			Thread guiThread = new Thread(gui, "GUI thread"); 
			guiThread.setDaemon(false); 
			guiThread.start(); 
		} else if (simulationPath != null) {
			// Printout to the Console
			progress = new ConsoleProgress();

			// Start the simulation without a GUI
			simulation = new Simulation(simulationPath, this);
		} else {
			// No simulation and no GUI to load. This won't
			// work. Die.
			System.err
					.println("Please activate the GUI in the config file "
							+ "or provide a simulation at the command line.");
			System.exit(1);
		}

	}

	/**
	 * Create a config file with default values. This is used when the config
	 * file doesn't exist in the first place.
	 * 
	 * @return the newly created configuration file.
	 */
	private XMLConfiguration createDefaultConfigFile() {
		System.out.println("Creating a default configuration file at "
				+ DEFAULT_CONFIG_FILE);
		XMLConfiguration newConfig = new XMLConfiguration();
		newConfig.setRootElementName("configuration");
		newConfig.setProperty("commandlistener.enable", true);
		newConfig.setProperty("commandlistener.tcpport", DEFAULT_PORT);
		newConfig.setProperty("ui.usegui", true);
		newConfig.setProperty("ui.speed", DEFAULT_UI_SPEED);
		newConfig.setProperty("ui.gradientcache.prefill", true);
		newConfig.setProperty("ui.gradientcache.size", DEFAULT_CACHE_SIZE);
		newConfig.setProperty("output.type", "null");
		newConfig.setProperty("output.csv.path", System
				.getProperty("user.home")
				+ File.separator + "SiafuContext.csv");
		newConfig.setProperty("output.csv.interval", DEFAULT_CSV_INTERVAL);
		newConfig.setProperty("output.csv.keephistory", true);

		try {
			newConfig.setFileName(DEFAULT_CONFIG_FILE);
			newConfig.save();
		} catch (ConfigurationException e) {
			throw new RuntimeException(
					"Can not create a default config file at "
							+ DEFAULT_CONFIG_FILE, e);
		}

		return newConfig;
	}

	
	/**
	 * Request for the simulation to stop. note that this doesn't kill the GUI
	 * (if it is being used).
	 * 
	 */
	public synchronized void stopSimulation() {
		simulation.die();
		notifyAll();
		simulation = null;
	}

	/**
	 * Start the simulation pointed to by the simulationpath.
	 * 
	 * @param simulationPath the path to the folder or jar containing the
	 *            simulation.
	 */
	public void startSimulation(final String simulationPath) {
		simulation = new Simulation(simulationPath, this);
	}

	/**
	 * Get the configuration for the simulator.
	 * 
	 * @return the <code>Configuration</code> object
	 */
	public XMLConfiguration getSiafuConfig() {
		return config;
	}

	/**
	 * Notifies the controler that the GUI has finished drawing an iteration
	 * and the simulation can continue.
	 */
	public synchronized void setDrawingCondluded() {
		notifyAll();
	}

	/**
	 * Asks the gui whether it wants to draw this iteration, and pauses until
	 * it wants to.
	 * <p>
	 * If the gui is not enabled in the configuration file, or if it chooses
	 * to skip this iteration, the method only action is to notify the GUI
	 * that it just missed an iteration. Note that the GUI might choose to
	 * skip <code>iterationStep</code> iterations to increase the
	 * interface's speed.
	 * 
	 */
	public synchronized void scheduleDrawing() {
		if ((gui != null) && gui.requestPermissionToDraw()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Pauses the simulation. See <code>isPaused()</code> for details. If no
	 * gui is used, the simulation thread still pauses.
	 * 
	 * @param state a boolean with the value true will pause the simulation,
	 *            while false will resume it.
	 */
	public void setPaused(final boolean state) {
		simulation.setPaused(state);
	}

	
	/**
	 * Reports on the pause state of the simulation.
	 * <p>
	 * When paused, time stops, and all automatic behaviour is interrupted.
	 * However, using the GUI, the user can still manipulate agents in his
	 * control, or update variables through the external command interface.
	 * 
	 * @return true if the simulation is paused, false otherwise
	 */
	public boolean isPaused() {
		return simulation.isPaused();
	}

	/**
	 * Get the world being simulated.
	 * 
	 * @return a World object representing the world.
	 */
	public World getWorld() {
		return simulation.getWorld();
	}

	/**
	 * Get the folder or jar file that contains all the simulation data.
	 * 
	 * @return a File that represents the folder with the images
	 */
	public SimulationData getSimulationData() {
		return simulation.getSimulationData();
	}

	/**
	 * Get the command listener for this simulator's run.
	 * 
	 * @return the command listener
	 */
	public CommandListener getCommandListener() {
		return commandListener;
	}

	/**
	 * End the simulator, that is quit. Stop the simulation, but also the GUI
	 * and anything else that's running.
	 */
	public synchronized void endSimulator() {
		if (commandListener != null) {
			commandListener.die();
		}

		// End the simulation
		if (simulation != null) {
			simulation.die();
		}

		if (gui != null) {
			gui.die();
		}
	}

	/**
	 * Get the simulator's GUI object.
	 * 
	 * @return a GUI object for the simulator's Graphical User Interface
	 */
	public GUI getGUI() {
		return gui;
	}

	/**
	 * Find out if the gui is being used.
	 * 
	 * @return true if the gui is used
	 */
	public boolean isGuiUsed() {
		return guiUsed;
	}

	/**
	 * Add a Marker to the simulation GUI. If the GUI is not ready, this
	 * method performs no action a GUINotReadyException is thrown. If the GUI
	 * is not being used this method returns silently and does nothing.
	 * 
	 * @param m the marker to add
	 * @throws GUINotReadyException if the GUI can not draw the mark at the
	 *             moment.
	 */
	public void addMarker(final Marker m) throws GUINotReadyException {
		if (guiUsed) {
			if (gui.canReceiveCommands()) {
				gui.getMarkers().addMarker(m, Markers.Type.INTERNAL);
			} else {
				throw new GUINotReadyException(
						"Can't add tracks until the simulation has started.");
			}
		}
	}

	/**
	 * Remove all Markers from the simulation GUI. If the GUI is not ready,
	 * this method performs no action a GUINotReadyException is thrown. If the
	 * GUI is not being used this method returns silently and does nothing.
	 * 
	 * @throws GUINotReadyException if the GUI can not draw the mark at the
	 *             moment.
	 */
	public void unMarkAll() throws GUINotReadyException {
		if (guiUsed) {
			if (gui.canReceiveCommands()) {
				gui.getMarkers().removeAllMarkers(Markers.Type.INTERNAL);
			} else {
				throw new GUINotReadyException(
						"Can't remove tracks until the simulation "
								+ "has started.");
			}
		}
	}

	/**
	 * Remove the Marker for this Trackable from the simulation GUI F. If the
	 * GUI is not ready, this method performs no action a GUINotReadyException
	 * is thrown. If the GUI is not being used this method returns silently
	 * and does nothing.
	 * 
	 * @param t the Trackable to unmark
	 * @throws GUINotReadyException if the GUI can not draw the mark at the
	 *             moment.
	 */
	public void unMark(final Trackable t) throws GUINotReadyException {
		if (guiUsed) {
			if (gui.canReceiveCommands()) {
				gui.getMarkers().removeMarker(t, Markers.Type.INTERNAL);
			} else {
				throw new GUINotReadyException(
						"Can't remove tracks until the simulation "
								+ "has started.");
			}
		}
	}

	/**
	 * Find out if the given Trackable is marked in the GUI. If the GUI is not
	 * ready, this method throws a GUINotReadyException. If the GUI is not
	 * being used this method returns false.
	 * 
	 * @param t the Trackable about which we are asking
	 * @return true if the trackable has been marked, false otherwise
	 * @throws GUINotReadyException if the GUI can not draw the mark at the
	 *             moment.
	 */
	public boolean isMarked(final Trackable t) throws GUINotReadyException {
		if (guiUsed) {
			if (gui.canReceiveCommands()) {
				return gui.getMarkers()
						.isTracked(t, Markers.Type.INTERNAL);
			} else {
				throw new GUINotReadyException(
						"Can't check tracks until the simulation has started.");
			}
		}
		return false;
	}

	/**
	 * Find out if the simulation is finished loading and already running.
	 * 
	 * @return true if the simulation is running.
	 */
	public boolean isSimulationRunning() {
		return simulation != null && simulation.isSimulationRunning();
	}
}
