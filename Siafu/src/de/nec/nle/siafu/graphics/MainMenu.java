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

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 * The menu on the top bar of the simulator, which provides, among others,
 * simulation loading control and siafu configuration.
 * 
 * @author Miquel Martin
 * 
 */
public class MainMenu {

	/** How many recent items to show. */
	private static final int AMOUNT_OF_RECENT_ITEMS = 5;

	/** The maximum length to display of the recent item's path. */
	private static final int MAXIMUM_RECENT_ENTRY_LENGTH = 30;

	/**
	 * The folder of the latest loaded simulation.
	 */
	private String selected = System.getProperty("user.dir");

	/** The menu itself. */
	private Menu menuBar;

	/** The shell inw hich this menu fits. */
	private Shell shell;

	/** The container gui. */
	private GUI gui;

	/** Some of the menu items. */
	private MenuItem simulationCloseItem, simulationOpenJarItem,
			simulationOpenDirItem;

	/** The configuration of Siafu. */
	private XMLConfiguration siafuConfig;

	/** The drop down menus. */
	private Menu simulationMenu, optionsMenu, helpMenu;

	/** The headers for the drop down menus. */
	private MenuItem simulationMenuHeader, optionsMenuHeader,
			helpMenuHeader;

	/**
	 * Create the menu.
	 * 
	 * @param shell the container shell
	 * @param simGUI the simulation's GUI
	 * @param siafuConfig the config for Siafu
	 */
	public MainMenu(final Shell shell, final GUI simGUI,
			final XMLConfiguration siafuConfig) {
		this.shell = shell;
		this.gui = simGUI;
		this.siafuConfig = siafuConfig;
		menuBar = new Menu(shell, SWT.BAR);

		simulationMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
		simulationMenuHeader.setText("&Simulation");
		createSimulationMenu(simulationMenuHeader);

		optionsMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
		optionsMenuHeader.setText("Op&tions");
		createOptionsMenu(optionsMenuHeader);

		helpMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
		helpMenuHeader.setText("&Help");
		createHelpMenu(helpMenuHeader);

		shell.setMenuBar(menuBar);
	}

	/**
	 * Add an item to the recently opened list in the simulation menu.
	 * 
	 * @param parent the parent menu
	 */
	private void addRecent(final Menu parent) {
		String[] recent =
				siafuConfig.getStringArray("ui.recentsimulation");
		if (recent != null && recent.length != 0) {
			new MenuItem(parent, SWT.SEPARATOR);

			for (int i = 0; i < recent.length; i++) {
				MenuItem m = new MenuItem(parent, SWT.PUSH);
				String name =
						recent[i].substring(recent[i]
								.lastIndexOf(File.separator) + 1);
				int length = recent[i].length();
				if (length > MAXIMUM_RECENT_ENTRY_LENGTH) {
					length = MAXIMUM_RECENT_ENTRY_LENGTH;
				}
				String path = "[" + recent[i].substring(0, length) + "]";
				m.setText("&" + (i + 1) + " " + name + " " + path);
				m.setData(recent[i]);
				m.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(final SelectionEvent e) {
						openSimulation((String) e.widget.getData());
					}
				});
			}
		}

	}

	/**
	 * Create the options menu.
	 * 
	 * @param header the header from which the menu hangs
	 */
	private void createOptionsMenu(final MenuItem header) {
		if (optionsMenu != null) {
			optionsMenu.dispose();
		}

		optionsMenu = new Menu(shell, SWT.DROP_DOWN);

		MenuItem viewShowPathItem = new MenuItem(optionsMenu, SWT.CHECK);
		viewShowPathItem.setText("&Show agent's path\tCTRL+P");
		viewShowPathItem.setAccelerator(SWT.CTRL + 'P');
		viewShowPathItem.setSelection(true);
		viewShowPathItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				gui.setPathShown(!gui.isPathShown());
			}
		});

		MenuItem simulateNightItem = new MenuItem(optionsMenu, SWT.CHECK);
		simulateNightItem.setText("Simulate &night");
		simulateNightItem.setSelection(gui.isNightSimulated());
		simulateNightItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				gui.simulateNight(((MenuItem) e.getSource()).getSelection());
			}
		});

				
		new MenuItem(optionsMenu, SWT.SEPARATOR);

		MenuItem toolsOptionsItem = new MenuItem(optionsMenu, SWT.PUSH);
		toolsOptionsItem.setText("Con&figuration\tCtrl+T");
		toolsOptionsItem.setAccelerator(SWT.CTRL + 'T');
		toolsOptionsItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				new ConfigShell(siafuConfig);
			}

		});

		optionsMenuHeader.setMenu(optionsMenu);
	}

	/**
	 * Create the help menu.
	 * 
	 * @param header the header from which the menu hangs
	 */
	private void createHelpMenu(final MenuItem header) {
		if (helpMenu != null) {
			helpMenu.dispose();
		}
		helpMenu = new Menu(shell, SWT.DROP_DOWN);
		MenuItem helpAboutItem = new MenuItem(helpMenu, SWT.PUSH);
		helpAboutItem.setText("&About");

		helpAboutItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				new AboutShell();
			}
		});

		helpMenuHeader.setMenu(helpMenu);

	}

	/**
	 * Create the simulation menu.
	 * 
	 * @param header the header from which the menu hangs
	 */
	private void createSimulationMenu(final MenuItem header) {
		if (simulationMenu != null) {
			simulationMenu.dispose();
		}

		simulationMenu = new Menu(shell, SWT.DROP_DOWN);

		simulationOpenJarItem = new MenuItem(simulationMenu, SWT.PUSH);
		simulationOpenJarItem.setText("&Open packed simulation\tCtrl+O");
		simulationOpenJarItem.setAccelerator(SWT.CTRL + 'O');
		simulationOpenJarItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.setText("Open a simulation");
				fd.setFilterPath(selected);
				String[] filterExt = {"*.jar"};
				fd.setFilterExtensions(filterExt);
				openSimulation(fd.open());
			}

		});

		simulationOpenDirItem = new MenuItem(simulationMenu, SWT.PUSH);
		simulationOpenDirItem
				.setText("&Open unpacked simulation\tCtrl+Shift+O");
		simulationOpenDirItem.setAccelerator(SWT.SHIFT + SWT.CTRL + 'O');
		simulationOpenDirItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				DirectoryDialog fd = new DirectoryDialog(shell, SWT.OPEN);
				fd.setText("Choose the simulation's root folder");
				fd.setFilterPath(getLastFolder());
				openSimulation(fd.open());
			}
		});

		simulationCloseItem = new MenuItem(simulationMenu, SWT.PUSH);
		simulationCloseItem.setText("&Close\tCtrl+C");
		simulationCloseItem.setAccelerator(SWT.CTRL + 'C');
		simulationCloseItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				gui.reportSimulationDataChange(null);
			}
		});

		addRecent(simulationMenu);

		new MenuItem(simulationMenu, SWT.SEPARATOR);

		MenuItem simulationQuitItem =
				new MenuItem(simulationMenu, SWT.PUSH);
		simulationQuitItem.setText("&Quit\tCtrl+Q");
		simulationQuitItem.setAccelerator(SWT.CTRL + 'Q');
		simulationQuitItem.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(final SelectionEvent e) {
				gui.close();

			}

		});

		header.setMenu(simulationMenu);
	}

	/**
	 * Get the folder of the last open simulation. This is used to set the
	 * open dialog box on the last used folder.
	 * 
	 * @return the folder of the last open simulation
	 */
	private String getLastFolder() {
		if (new File(selected).isDirectory()) {
			return selected;
		} else {
			return selected.substring(0, selected
					.lastIndexOf(File.separator));
		}
	}

	/**
	 * Load a simulation, closing the running one if there is one.
	 * 
	 * @param choice the path to the simulation that we want to open
	 */
	private void openSimulation(final String choice) {
		if (choice != null) {
			selected = choice;

			saveToRecent(selected);

			createSimulationMenu(simulationMenuHeader);

			gui.reportSimulationDataChange(selected);
		}

	}

	/**
	 * Put the simulation that has just been opened into the Simulation menu's
	 * recently opened file list.
	 * 
	 * @param newSelected the newly opened simulation
	 */
	private void saveToRecent(final String newSelected) {
		String[] recent =
				siafuConfig.getStringArray("ui.recentsimulation");
		String[] newRecent = new String[AMOUNT_OF_RECENT_ITEMS];

		newRecent[0] = newSelected;
		for (int i = 0; i < AMOUNT_OF_RECENT_ITEMS - 1
				&& i < recent.length; i++) {
			if (newSelected.equals(recent[i])) {
				continue;
			} else {
				newRecent[i + 1] = recent[i];
			}
		}

		siafuConfig.setProperty("ui.recentsimulation", newRecent);
		try {
			siafuConfig.save();
		} catch (ConfigurationException e) {
			throw new RuntimeException(
					"Can not save the configuration file", e);
		}
	}

	/**
	 * Enable or disable the menus that allow the user to change the
	 * simulation. These menus should be disabled whily a simulation is being
	 * loaded.
	 * 
	 * @param enabled true if we want to allow the user to change the
	 *            simulation, false otherwise
	 */
	public void simulationChangeMenuesEnabled(final boolean enabled) {
		simulationCloseItem.setEnabled(enabled);
		simulationOpenJarItem.setEnabled(enabled);
		simulationOpenDirItem.setEnabled(enabled);
	}
}
