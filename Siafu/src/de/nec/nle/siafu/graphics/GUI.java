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

package de.nec.nle.siafu.graphics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

import de.nec.nle.siafu.control.Controller;
import de.nec.nle.siafu.control.progress.GUIProgress;
import de.nec.nle.siafu.exceptions.PlaceTypeUndefinedException;
import de.nec.nle.siafu.graphics.controlpanel.ControlPanel;
import de.nec.nle.siafu.graphics.markers.SpotMarker;
import de.nec.nle.siafu.graphics.simulationarea.CanvasMouseListener;
import de.nec.nle.siafu.graphics.simulationarea.CanvasPaintListener;
import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.Overlay;
import de.nec.nle.siafu.model.Place;
import de.nec.nle.siafu.model.Position;
import de.nec.nle.siafu.model.Trackable;
import de.nec.nle.siafu.model.World;

/**
 * The GUI class implements the simulator Graphical User Interface. It is a
 * thread of its own that refreshes the simulation canvas, where "the action
 * happens". The GUI is only invoked it so configured in the configuration. If
 * running, the GUI will stop the whole simulation when killed.
 * 
 * @author Miquel Martin
 * 
 */
public class GUI implements Runnable {

	/** Checkstyle forced me. */
	private static final int ONE_HUNDRED = 100;

	/** Vertical margin on the shell. */
	private static final int SHELL_MARGIN_HEIGHT = 5;

	/** Height of the main shell. */
	private static final int SHELL_HEIGHT = 480;

	/** Width of the main shell. */
	private static final int SHELL_WIDTH = 640;

	/** Minimum width of the control panel. */
	private static final int CONTROL_PANEL_MIN_WIDTH = 350;

	/**
	 * Speed value beyond which we start skipping frames to make the GUI faster.
	 */
	static final int SPEED_THRESHOLD = 50;

	/**
	 * Maximum number of iterations that can be skipped when displaying the
	 * simulation GUI. More than that, and all becomes too jumpy.
	 */
	public static final int MAX_SKIP_ITERATIONS = 10;

	/**
	 * Minimum number of iterations that can be skipped. One is the minimum.
	 */
	public static final int MIN_SKIP_ITERATIONS = 0;

	/**
	 * Maximum refresh in ms allowed between screen redraws, as controled by the
	 * speed variable.
	 */
	public static final int MAX_REFRESH = 600;

	/**
	 * Minimum refresh time in ms. Less than that, and the screen won't be ready
	 * for redraw when the GUI tries to grab it.
	 */
	public static final int MIN_REFRESH = 40;

	/** The state number when no simulation is loaded. */
	private static final int STATE_STANDBY = 0;

	/** The state number when a simulation is being loaded. */
	private static final int STATE_AWAITING_SIMULATION = 1;

	/** The state number when a simulation is loaded and running. */
	private static final int STATE_SHOWING_SIMULATION = 2;

	/** The status of the GUI, initially set to standby. */
	private int status = STATE_STANDBY;

	/**
	 * Indicates that the simulation is paused.
	 */
	private boolean paused;

	/** If true, the sky will darken as night falls */
	private boolean simulateNight = true;

	/**
	 * The <code>Tracks</code> that holds all the track marks in the current
	 * simulation.
	 */
	private Markers markers;

	/**
	 * Number of iterations to pass by before redrawing the screen.
	 */
	private int iterationStep;

	/**
	 * The amount of iterations that we have not drawn (skipped).
	 */
	private int skippedIterations;

	/**
	 * Milliseconds between screen redraws. Note that usually many iterations
	 * can occur between redraws.
	 */
	private int refreshSpeed;

	/**
	 * If true, an iteration is ready to be drawn by the gui, and the simulation
	 * thread is waiting for a <code>setDrawingConcluded()</code> call.
	 */
	private boolean iterationReady;

	/**
	 * Display the dottet line that marks the path from the active agent to its
	 * destination.
	 */
	private boolean pathShown = true;

	/**
	 * The main (and only) shell of the gui.
	 */
	private Shell shell;

	/** Siafu's icon. */
	private Image siafuIcon;

	/**
	 * The Canvas where the simulation is displayed.
	 */
	private Canvas canvas;

	/**
	 * The listener for contextMenu.
	 */
	private ContextMenuListener contextMenuListener;

	/**
	 * The Context Menu that pops up when right-clicking on the canvas and
	 * allows you to choose an agent or a destination.
	 */
	private Menu contextMenu;

	/**
	 * The trackable that's currently active.
	 */
	private Trackable activeTrackable;

	/**
	 * The display associated to the SWT thread.
	 */
	private Display display;

	/** A reference to the Siafu controller. */
	private Controller control;

	/** A reference to the simulation's world. */
	private World world;

	/**
	 * The class responsible for all the actual swt commands to draw the
	 * simulation canvas.
	 */
	private Painter painter;

	/**
	 * The control panel is located on the right hand side of the GUI, and
	 * allows for the selection of entities, display of their properties,
	 * control the simulation speed, etc...
	 */
	private ControlPanel controlPanel;

	/**
	 * Whether the simulation data has changed, meaning we need to close the
	 * current simulation or load a new one. It starts as true, to load the
	 * initial gui.
	 */
	private boolean simulationDataChanged;

	/** The path to the simulation data. */
	private String simulationDataPath;

	/** Whether the GUI is ready to receive commands or not. */
	private boolean canReceiveCommands;

	/** True if the simulation has ended, and so should the GUI. */
	private boolean ended;

	/** The composite that replaces the canvas when no simulation is loaded. */
	private Composite standByComposite;

	/** The composite that replaces the canvas when the simulation is loading. */
	private Composite loadingComposite;

	/** The Menu on top of the GUI. */
	private MainMenu mainMenu;

	/**
	 * Find out if the simulation data has changed, and so the old simulation
	 * should be closed and the new one reopened.
	 * 
	 * @return true if it has changed.
	 */
	private synchronized boolean isSimulationDataChanged() {
		return simulationDataChanged;
	}

	/**
	 * Acknowledge the simulation change and reset it.
	 * 
	 */
	private synchronized void resetSimulationDataChanged() {
		simulationDataChanged = false;
	}

	/**
	 * Tell the gui that the simulation has changed and it should update.
	 * 
	 * @param newSimulationDataPath
	 *            the new path for the simulation
	 */
	public synchronized void reportSimulationDataChange(
			final String newSimulationDataPath) {
		// Note: if same simulation is loaded again, we restat it.
		if (simulationDataPath == null && newSimulationDataPath == null) {
			simulationDataChanged = false;
		} else {
			simulationDataChanged = true;
			simulationDataPath = newSimulationDataPath;
		}
	}

	/**
	 * The runnable object which is called everytime the timerexec of swt is
	 * called when a simulation is running. It simply refreshes the whole
	 * canvas.
	 */
	private Runnable refreshSimulationRunnable = new Runnable() {
		/**
		 * This method is run everytime the timerExec method expires after
		 * refreshSpeed. If the simulation is waiting for an iteration to be
		 * drawn, it does so, and then sets the timer again for the next period.
		 * 
		 */
		public synchronized void run() {
			if (shell.isDisposed()) {
				return;
			}
			
			switch (status) {
			case STATE_STANDBY:
				if (simulationDataPath != null) {
					control.startSimulation(simulationDataPath);
					mainMenu.simulationChangeMenuesEnabled(false);
					status = STATE_AWAITING_SIMULATION;
					resetSimulationDataChanged();
					if(standByComposite!=null){
						standByComposite.dispose();
					}
					loadingComposite = new LoadingComposite(shell);
				}
				break;
			case STATE_AWAITING_SIMULATION:
				if (!control.isSimulationRunning()) {
					((GUIProgress) Controller.getProgress())
							.update(loadingComposite);
				} else {
					switchToSimulationMode();
					((GUIProgress) Controller.getProgress()).reset();
					mainMenu.simulationChangeMenuesEnabled(true);
					status = STATE_SHOWING_SIMULATION;
				}
				break;
			case STATE_SHOWING_SIMULATION:
				if (isSimulationDataChanged()) {
					switchToStandByMode();
					status = STATE_STANDBY;
				} else {
					updateSimulationGUI();
				}
				break;
			default:
				throw new RuntimeException("Unknown state");
			}

			display.timerExec(refreshSpeed, refreshSimulationRunnable);
		}

	};

	/**
	 * Update all of the gui components according to the simulation status.
	 * 
	 */
	private void updateSimulationGUI() {
		if (this.paused != control.isPaused()) {
			this.paused = control.isPaused();
			controlPanel.getClock().setPaused(paused);
		}

		if (iterationReady) {
			controlPanel.refresh();
			canvas.redraw();
		}
	}

	/**
	 * Go from standby to simulation mode, whenever a simulation is loaded using
	 * the gui's menu.
	 * 
	 */
	private void switchToSimulationMode() {
		world = control.getWorld();

		loadingComposite.dispose();
		
		painter = new Painter(control);
		markers = new Markers();
		canvas = createCanvas();
		contextMenu = createContextMenu();
		controlPanel = createControlPanel();
		shell.setText("Simulating " + world.getWorldName() + " - Siafu");
		shell.pack();
		canReceiveCommands = true;

		status = STATE_SHOWING_SIMULATION;

	}

	/**
	 * Go from simulation mode to stand by, showing a label to indicate the user
	 * to load a simulation.
	 * 
	 */
	private void switchToStandByMode() {
		// End the simulation
		canReceiveCommands = false;
		if (control.isSimulationRunning()) {
			control.stopSimulation();
		}

		// Destroy the simulation gui
		if (painter != null) {
			painter.disposeResources();
			painter = null;
		}
		if (markers != null) {
			markers.disposeResources();
			markers = null;
		}
		if (canvas != null) {
			canvas.dispose();
			canvas = null;
		}
		if (contextMenu != null) {
			contextMenu.dispose();
			contextMenu = null;
		}
		if (controlPanel != null) {
			controlPanel.disposeResources();
			controlPanel = null;
		}

		// Create Stand By gui
		createStandByGUI();
		refreshSpeed = MIN_REFRESH;
		shell.pack();

	}

	/**
	 * Create a label in the place of the canvas, which prompts the user to load
	 * a simulation.
	 * 
	 */
	private void createStandByGUI() {
		shell.setText("Siafu");
		standByComposite= new StandbyComposite(shell);
	}

	/**
	 * Default constructor. It keeps the reference to the relevant data, which
	 * whill be used when the thread actually starts.
	 * 
	 * @param control
	 *            the simulation's controller
	 * @param simulationPath
	 *            the path to the simulation data
	 */
	public GUI(final Controller control, final String simulationPath) {
		this.control = control;
		reportSimulationDataChange(simulationPath);
		// Force the first redraw
		simulationDataChanged = true;
		display = new Display();
	}

	/**
	 * Get the Tracks object for this simulation.
	 * 
	 * @return the Tracks object
	 */
	public Markers getMarkers() {
		return markers;
	}

	/**
	 * Get the agents in the simulation.
	 * 
	 * @return a collection with the agents
	 */
	public Collection<Agent> getAgents() {
		return world.getPeople();
	}

	/**
	 * Get the types of places in Siafu.
	 * 
	 * @return a Set with the place types
	 */
	public Set<String> getPlaceTypes() {
		return world.getPlaceTypes();
	}

	/**
	 * Get all the palces in the simulation.
	 * 
	 * @return a collection with all the places
	 */
	public Collection<Place> getPlaces() {
		return world.getPlaces();
	}

	/**
	 * Get all the places of a certain type.
	 * 
	 * @param type
	 *            the required type
	 * @return a collection with the palces of that type
	 */
	public Collection<Place> getPlacesOfType(final String type) {

		try {
			return world.getPlacesOfType(type);
		} catch (PlaceTypeUndefinedException e) {
			e.printStackTrace();
			return new ArrayList<Place>();
		}
	}

	/**
	 * Get the overlays in the simulation.
	 * 
	 * @return a Collection with the overlays
	 */
	public Collection<Overlay> getOverlays() {
		return world.getOverlays().values();
	}

	/**
	 * Get the control panel on the right hand side of the GUI.
	 * 
	 * @return the control panel object
	 */
	public ControlPanel getControlPanel() {
		return controlPanel;
	}

	/**
	 * Inform the GUI that the simulation thread has finished an iteration, and
	 * the data is ready to be drawn. The simulation further asks the GUI if
	 * this iteration has to be drawn. If it is to be drawn, the simulation must
	 * wait until the GUI is finished redrawing, at which time it will be
	 * notified to continue simulating.
	 * 
	 * The objective of this synchronization is to hold off the simulation while
	 * the GUI is redrawn, or let it continue if it is to be skipped.
	 * 
	 * Which iterations are skipped depends on the simulation speed. Only one
	 * out of iterationStep iterations will be drawn.
	 * 
	 * @return true if the GUI is going to draw the iteration, meaning that the
	 *         simulation must wait. False if the iteration will be skipped and
	 *         the simulation can go on.
	 */
	public synchronized boolean requestPermissionToDraw() {
		if (skippedIterations >= iterationStep) {
			iterationReady = true;
			skippedIterations = 0;
			return true;
		} else {
			skippedIterations++;
			return false;
		}

	}

	/**
	 * Sets the refresh speed of the GUI, that is, how often will it collect the
	 * simulation data and redraw the canvas. If the parameter is beyond the
	 * thresholds MIN_REFRESH and MAX_REFRESH, the value is left at the
	 * offending threshold.
	 * 
	 * @param guiRefreshSpeed
	 *            the interval between canvas redrawals
	 */
	public synchronized void setGuiRefreshSpeed(final int guiRefreshSpeed) {

		if (guiRefreshSpeed < MIN_REFRESH) {
			this.refreshSpeed = MIN_REFRESH;
		} else if (guiRefreshSpeed > MAX_REFRESH) {
			this.refreshSpeed = MAX_REFRESH;
		} else {
			this.refreshSpeed = guiRefreshSpeed;
		}
	}

	/**
	 * Set the amount of iterations to skip before actually redrawing the canvas
	 * to display one.
	 * 
	 * @param guiSkipIterations
	 *            how many iterations to skip
	 */
	public synchronized void setGuiSkipIterations(final int guiSkipIterations) {
		if (guiSkipIterations > MAX_SKIP_ITERATIONS) {
			this.iterationStep = MAX_SKIP_ITERATIONS;
		} else if (guiSkipIterations < MIN_SKIP_ITERATIONS) {
			this.iterationStep = MIN_SKIP_ITERATIONS;
		} else {
			this.iterationStep = guiSkipIterations;
		}
	}

	/**
	 * Get the sprite for the given agent.
	 * 
	 * @param a
	 *            the agent
	 * @return the sprite associated to that agent
	 */
	public Sprite getAgentSprite(final Agent a) {
		return painter.getAgentSprite(a);
	}

	/**
	 * The thread initialization routine. It works by drawing the gui elements
	 * and starting of the periodic timer that will refresh the simulation
	 * canvas.
	 */
	public void run() {
		System.out.println("Starting Siafu...");
		display.syncExec(new Runnable() {
			public void run() {
				createShell();
				switchToStandByMode();

				display.timerExec(refreshSpeed, refreshSimulationRunnable);
				swtLoop();
			}
		});
		System.out.println("Siafu ended");
	}

	/**
	 * Get the trackable which is currently selected in the GUI. Operations such
	 * as double clicking to move will be executed on this trackable when
	 * possible (e.g. a place won't move)
	 * 
	 * @return the selected trackable
	 */
	public Trackable getActive() {
		return activeTrackable;
	}

	/**
	 * Set which trackable, usually an agent or a place, is selected at the
	 * moment.
	 * 
	 * @param t
	 *            the trackable to set as selected
	 * 
	 */
	public void setActiveTrackable(final Trackable t) {
		activeTrackable = t;

		if (activeTrackable != null) {
			// Add mark. In the future we might support multiple active marks.
			if (markers.geMarks(Markers.Type.ACTIVE).isEmpty()) {
				markers.addMarker(new SpotMarker(t), Markers.Type.ACTIVE);
			} else {
				markers.geMarks(Markers.Type.ACTIVE).iterator().next()
						.setTrackable(t);
			}
		} else {
			markers.removeAllMarkers(Markers.Type.ACTIVE);
		}

	}

	/**
	 * Creates the canvas that displays the running simulation. It also defines
	 * the event handlers for the GUI operations, plus the paintListener for the
	 * canvas itself.
	 * 
	 * The paintListener actually performs the operations required to show the
	 * simulation on the GUI, including drawing the backgrounds and agents on
	 * it.
	 * 
	 * @return the created Canvas object
	 */
	private Canvas createCanvas() {
		Canvas newCanvas = new Canvas(shell, SWT.BORDER | SWT.NO_REDRAW_RESIZE
				| SWT.NO_BACKGROUND);

		GridData gdCanvas = new GridData(world.getWidth(), world.getHeight());

		newCanvas.setLayoutData(gdCanvas);
		newCanvas.addMouseListener(new CanvasMouseListener(this, world));

		newCanvas.addPaintListener(new CanvasPaintListener(control, painter,
				controlPanel, markers));

		return newCanvas;
	}

	/**
	 * Create the context menu that appears when right clicking on the
	 * simulation canvas.
	 * 
	 * @return the contextual menu
	 */
	private Menu createContextMenu() {
		Menu newContextMenu = new Menu(shell);
		contextMenuListener = new ContextMenuListener(control, newContextMenu,
				shell, canvas);
		newContextMenu.addMenuListener(contextMenuListener);
		return newContextMenu;
	}

	/**
	 * Show the context menu.
	 * 
	 * @param clickPos
	 *            the position the user has clicked on the canvas
	 */
	public void showContextMenu(final Position clickPos) {
		contextMenuListener.show(clickPos);
	}

	/**
	 * Creates the control panel, that is, everything right of the canvas,
	 * including the clock, the item chooser and the status panels.
	 * 
	 * @return the created ControlPanel
	 */
	private ControlPanel createControlPanel() {
		GridLayout glCPanel = new GridLayout();
		glCPanel.marginHeight = 0;

		ControlPanel newControlPanel = new ControlPanel(shell, SWT.NORMAL,
				control);
		newControlPanel.setLayout(glCPanel);

		GridData gdCPanel = new GridData(GridData.FILL_BOTH);
		gdCPanel.minimumWidth = CONTROL_PANEL_MIN_WIDTH;
		newControlPanel.setLayoutData(gdCPanel);
		return newControlPanel;
	}

	/**
	 * Create the main shell where the GUI runs.
	 * 
	 */
	private void createShell() {
		shell = new Shell();
		shell.setMinimumSize(SHELL_WIDTH, SHELL_HEIGHT);
		siafuIcon = new Image(display, getClass().getResourceAsStream(
				"/res/misc/icon.png"));
		shell.setImage(siafuIcon);
		GridLayout glShell = new GridLayout(2, false);
		glShell.marginHeight = SHELL_MARGIN_HEIGHT;
		shell.setLayout(glShell);
		mainMenu = new MainMenu(shell, this, control.getSiafuConfig());
	}

	/**
	 * Run the loop that swt uses to handle the GUI widgets. This loop runs for
	 * as long as the simulation lasts, and can not be made to wait by any other
	 * thread that tries to take the monitor away from it; SWT rules.
	 * 
	 */
	private void swtLoop() {
		shell.pack();
		shell.open();
		while (!shell.isDisposed() && !isEnded()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.dispose();
		control.setDrawingCondluded();
		if (!isEnded()) {
			control.endSimulator();
			control.setDrawingCondluded();
		}
	}

	/**
	 * Sets the simulation speed according to the slider bar on the simulation
	 * GUI. The speed of the simulation affects only how fast it is displayed,
	 * and not the actual simulation time. In order to accelerate a simulation,
	 * two parameters can be adjusted:
	 * <ul>
	 * <li>Skipping frames: do not attempt to draw all of the iterations that
	 * the simulation performs, but only one out of so many. If too many frames
	 * are skipped, the simulation will look jumpy.
	 * <li>Adjusting the refresh time: the canvas is redrawn every time the swt
	 * thread ticks down a refresh timer (using timerExec()). Adjusting this
	 * refresh timer provides for a smoother simulation.
	 * </ul>
	 * 
	 * A note on the refresh time: If the refresh is set under a certain
	 * threshold, it will have no effect, because the simulator will have to
	 * wait for the simulation data to be ready anyway. For this reason, it will
	 * only be useful to slow down the simulation, since a very high refresh
	 * time pauses the whole simulation until the GUI finally does the
	 * redrawing.
	 * 
	 * All of the maximum and minimum values for these two parameters are set in
	 * the GUI static fields MIN/MAX_SKIP_ITERATIONS and MIN/MAX_REFRESH. The
	 * two parameters are calculated on the basis of a single number, the
	 * guiSpeed, provided as a parameter.
	 * 
	 * When the speed is under <code>SPEED_THRESHOLD</code>, the simulation
	 * slows down the simulation by taking down the refresh rate. When above the
	 * threshold, the simulator skips iterations to make the simulation look
	 * faster.
	 * 
	 * @param guiSpeed
	 *            the percentage of the speed. The values of 0% and 100% are
	 *            defined by the value ranges of refresh and iteration skipping.
	 */
	public void setSpeed(final int guiSpeed) {
		int guiSkipIterations;
		int guiRefreshSpeed;

		if (guiSpeed < SPEED_THRESHOLD) {
			guiSkipIterations = MIN_SKIP_ITERATIONS;

			float refreshRange = MAX_REFRESH - GUI.MIN_REFRESH;
			float percent = 1 - ((float) guiSpeed / SPEED_THRESHOLD);
			guiRefreshSpeed = (int) (percent * refreshRange);
		} else if (guiSpeed > SPEED_THRESHOLD) {
			int skipRange = (GUI.MAX_SKIP_ITERATIONS - MIN_SKIP_ITERATIONS);
			float percent = (float) (guiSpeed - SPEED_THRESHOLD)
					/ (ONE_HUNDRED - SPEED_THRESHOLD);
			guiSkipIterations = (int) (percent * skipRange);
			guiRefreshSpeed = GUI.MIN_REFRESH;
		} else {
			guiRefreshSpeed = GUI.MIN_REFRESH;
			guiSkipIterations = GUI.MIN_SKIP_ITERATIONS;
		}

		setGuiRefreshSpeed(guiRefreshSpeed);
		setGuiSkipIterations(guiSkipIterations);
	}

	/**
	 * Find out if the path to the active agent's destination should be shown on
	 * the canvas.
	 * 
	 * @return true if it should, false otherwise
	 */
	public boolean isPathShown() {
		return pathShown;
	}

	/**
	 * Set whether the selected agent path towards its destination should be
	 * shown on the canvas.
	 * 
	 * @param showPath
	 *            true if the path should be drawn on the canvas, false
	 *            otherwise
	 */
	public void setPathShown(final boolean showPath) {
		this.pathShown = showPath;
	}

	/**
	 * Choose whether to darken the sky as night falls.
	 * 
	 * @param simulateNight
	 *            true if night should be simulated, false otherwise
	 */
	public void simulateNight(final boolean simulateNight) {
		this.simulateNight = simulateNight;
	}

	public boolean isNightSimulated() {
		return simulateNight;
	}

	/**
	 * Request that the GUI draw an overlayable on top of the background.
	 * 
	 * @param ov
	 *            an overlayable such as a place or, of course, an overlay
	 */
	public synchronized void requestOverlayDrawing(final Overlayable ov) {
		painter.requestOverlayDrawing(ov);

	}

	/**
	 * Stop drawing the overlayable image and draw the simple background.
	 * 
	 */
	public synchronized void cancelOverlayDrawing() {
		painter.cancelOverlayDrawing();

	}

	/**
	 * Close the shell.
	 * 
	 */
	public void close() {
		shell.dispose();
	}

	/**
	 * Fidn out if the GUI is able to receive commands. This happens when the
	 * simulation is loaded and displayed in the GUI.
	 * 
	 * @return true if it can, false otherwise
	 */
	public synchronized boolean canReceiveCommands() {
		return canReceiveCommands;
	}

	/**
	 * Find out if the simulation has ended and the GUI should die or not.
	 * 
	 * @return true if it has
	 */
	private synchronized boolean isEnded() {
		return ended;
	}

	/**
	 * Ask the GUI to die.
	 * 
	 */
	public synchronized void die() {
		ended = true;
	}
}
