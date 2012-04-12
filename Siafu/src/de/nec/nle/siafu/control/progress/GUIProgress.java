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

package de.nec.nle.siafu.control.progress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

import de.nec.nle.siafu.control.Controller;

/**
 * A progress implementation that displays the loading progress on the StandBy
 * composite of the GUI.
 * 
 * @author Miquel Martin
 * 
 */
public class GUIProgress implements Progress {
	/** The system font, in bold. */
	private Font bold;

	/** Whether the cache prefill has started already. */
	private boolean cachePrefillStarted;

	/** The size of the cache. */
	private int cacheSize;

	/** The amound of cache elements loaded so far. */
	private int cacheElementsLoaded;

	/** Whether the cache is full. */
	private boolean cachePrefillEnded;

	/** Whether the agents are being created. */
	private boolean creatingAgents;

	/** The place type names and the amount of each of them. */
	private HashMap<String, Integer> placesFound;

	/** Whether the simulation has already started. */
	private boolean simulationStarted;

	/** The name of the world. */
	private String worldName;

	/** Whether we hve started creating the world. */
	private boolean worldCreationStarted;

	/** The progress bar with for the cache loads. */
	private ProgressBar cacheBar;

	/** The label for the places found. */
	private Label placesLabel;

	/** The types of places and their associated creation progress bar. */
	private Hashtable<String, ProgressBar> placeCreationProgress;

	/**
	 * Contains the Strings with place types, once for each place of that type
	 * that has been created since the last iteration.
	 */
	private ArrayList<String> createdPlaces;

	/** The text in the places label. */
	private String placesText;

	/**
	 * Whether the simulation has been run before (and the place gradients
	 * have already been calculated).
	 */
	private boolean firstRun;

	/**
	 * Whether we've alerted the user of the disk space we'll take for the
	 * place gradients.
	 */
	private boolean warningIssued;

	/**
	 * Create an instance of this progress.
	 * 
	 */
	public GUIProgress() {
		reset();
	}

	/**
	 * Do nothing. The GUI is not available anymore at the time the
	 * backgrounds are created.
	 */
	public synchronized void reportBackgroundCreated() {
	}

	/**
	 * Do nothing. The GUI is not available anymore at the time the
	 * backgrounds are created.
	 */
	public synchronized void reportBackgroundCreationEnd() {
	}

	/**
	 * Do nothing. The GUI is not available anymore at the time the
	 * backgrounds are created.
	 * 
	 * @param amount the amount of backgrounds that will be created
	 */
	public synchronized void reportBackgroundCreationStart(final int amount) {
	}

	/** Increase the load count. */
	public synchronized void reportCacheElementLoaded() {
		cacheElementsLoaded++;
	}

	/**
	 * Keep the values so the GUI can draw them.
	 * 
	 * @param amountOfElements the amount of elements in the cache
	 */
	public synchronized void reportCachePrefill(final int amountOfElements) {
		if (amountOfElements > 0) {
			cachePrefillStarted = true;
			firstRun = false;
			cacheSize = amountOfElements;
		} else {
			firstRun = true;
		}
	}

	/** Store the change so the GUI can redraw it appropriately. */
	public synchronized void reportCachePrefillEnded() {
		cachePrefillEnded = true;
	}

	/** Store the change so the GUI can redraw it appropriately. */
	public synchronized void reportCreatingAgents() {
		creatingAgents = true;
	}

	/**
	 * Store the change so the GUI can redraw it appropriately.
	 * 
	 * @param type the type of place
	 * @param amount the amount of places of that type
	 */
	public synchronized void reportPlacesFound(final String type,
			final int amount) {
		synchronized (placesFound) {
			placesFound.put(type, amount);
		}

	}

	/**
	 * Report that a place of the given type has been creted, and store the
	 * type so that the update mechanism can reflect it on the gui.
	 * 
	 * @param type the type of the place that has just been created
	 */
	public void reportPlaceCreated(final String type) {
		if (firstRun) {
			synchronized (createdPlaces) {
				createdPlaces.add(type);
			}
		}

	}

	/** Store the change so the GUI can redraw it appropriately. */
	public synchronized void reportSimulationEnded() {
		// Do Nothing
	}

	/** Store the change so the GUI can redraw it appropriately. */
	public synchronized void reportSimulationStarted() {
		simulationStarted = true;
	}

	/**
	 * Store the change so the GUI can redraw it appropriately.
	 * 
	 * @param newWorldName the name of the world
	 */
	public synchronized void reportWorldCreation(final String newWorldName) {
		worldCreationStarted = true;
		this.worldName = newWorldName;
	}

	/**
	 * Update the composite with the information gathered since the last
	 * update.
	 * 
	 * @param parent the composite where the reports should be drawn
	 */
	public synchronized void update(final Composite parent) {
		if (bold == null) {
			FontData fd =
					Display.getCurrent().getSystemFont().getFontData()[0];
			bold =
					new Font(Display.getCurrent(), fd.getName(), fd.getHeight(),
							fd.getStyle() | SWT.BOLD);
		}

		if (worldCreationStarted) {
			Label start =
					addLabel("Creating world: " + worldName,
						parent);
			start.setFont(bold);
			worldCreationStarted = false;
		}
		if (simulationStarted) {
			addLabel("Simulation started", parent);
			simulationStarted = false;
		}
		if (cachePrefillStarted) {
			if (cacheBar == null) {
				addLabel("Prefilling the gradient cache", parent);
				GridData gdCacheBar =
						new GridData(SWT.CENTER, SWT.CENTER, true, false);
				cacheBar =
						new ProgressBar(parent, SWT.HORIZONTAL
								| SWT.SMOOTH);
				cacheBar.setMaximum(cacheSize);
				cacheBar.setLayoutData(gdCacheBar);
				parent.layout();
			}
			cacheBar.setSelection(cacheElementsLoaded);
		}
		if (cachePrefillEnded) {
			cachePrefillStarted = false;
			cachePrefillEnded = false;
			// cacheBar.dispose();
		}

		synchronized (placesFound) {
			if (placesFound.size() != 0) {
				if (!firstRun) {
					if (placesLabel == null) {
						placesLabel =
								addLabel(placesText, parent);
					}
					for (String type : placesFound.keySet()) {
						placesLabel.setText(placesLabel.getText() + "\n"
								+ type + " (" + placesFound.get(type)
								+ ") ");
					}
				} else {
					if (!warningIssued) {
						// First run, show progress bar for place creation

						Label warning =
								addLabel("Important: I'm storing place "
										+ "gradients in\n"
										+ Controller.DEFAULT_GRADIENT_PATH
										+ worldName
										+ "\nIt might take some 10MB!",
									parent);
						warning.setFont(bold);
						warningIssued = true;
					}

					for (String type : placesFound.keySet()) {
						int size = placesFound.get(type);
						addLabel("Creating " + size + " \"" + type
								+ "\" places", parent);
						GridData gdTypeProgress =
								new GridData(SWT.CENTER, SWT.CENTER, true,
										false);
						ProgressBar typeProgress =
								new ProgressBar(parent,
										SWT.HORIZONTAL | SWT.SMOOTH);
						typeProgress.setMaximum(size);
						typeProgress.setLayoutData(gdTypeProgress);
						parent.layout();
						placeCreationProgress.put(type, typeProgress);
					}
				}
				placesFound.clear();
			}
		}

		synchronized (createdPlaces) {
			if (createdPlaces.size() > 0) {
				for (String type : createdPlaces) {
					ProgressBar pb = placeCreationProgress.get(type);
					pb.setSelection(pb.getSelection() + 1);
				}
				createdPlaces.clear();
			}
		}
		if (creatingAgents) {
			addLabel("Creating Agents", parent);
			creatingAgents = false;
		}

		parent.layout();
	}

	/**
	 * Add a standard label to report on the event given by message.
	 * 
	 * @param message the message to show
	 * @param comp the composite where the label should be added
	 * @return the created label
	 */
	private Label addLabel(final String message, final Composite comp) {
		GridData gdLabel = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		Label l = new Label(comp, SWT.WRAP);
		l.setLayoutData(gdLabel);
		l.setAlignment(SWT.CENTER);
		l.setText(message);

		comp.layout();
		return l;
	}

	/**
	 * Initialize the progress to zero, taht is, the state when no simulation
	 * is laoded at all.
	 * 
	 */
	public synchronized void reset() {
		// All elements have been disposed when
		// the parent composite was disposed.
		cachePrefillStarted = false;
		cacheSize = 0;
		cacheElementsLoaded = 0;
		cachePrefillEnded = false;
		creatingAgents = false;
		placesFound = new HashMap<String, Integer>();
		simulationStarted = false;
		worldName = null;
		worldCreationStarted = false;
		if (cacheBar != null) {
			cacheBar.dispose();
			cacheBar = null;
		}
		placesLabel = null;
		placesText = "Places found:";
		createdPlaces = new ArrayList<String>();
		placeCreationProgress = new Hashtable<String, ProgressBar>();
		firstRun = false;
		warningIssued = false;
	}

}
