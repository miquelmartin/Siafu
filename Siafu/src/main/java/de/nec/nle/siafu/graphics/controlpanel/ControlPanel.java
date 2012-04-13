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

package de.nec.nle.siafu.graphics.controlpanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import de.nec.nle.siafu.control.Controller;
import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.Overlay;
import de.nec.nle.siafu.model.Place;

/**
 * The control panel displayed on the right hand side of the simulation's
 * shell. It shows the simulation time, control speed, and information about
 * agents, places and overlays.
 * 
 * @author Miquel Martin
 * 
 */
public class ControlPanel extends Composite {
	/** The clock with the date, time, pause and speed controls. */
	private Clock clock;

	/**
	 * The tabs used to choose agents, places and overlays, and display their
	 * status.
	 */
	private CTabItem agentsTab, placesTab, overlaysTab;

	/** The panel to show and choose agent information. */
	private AgentsPanel agentsPanel;

	/** The panel to show and choose places information. */
	private PlacesPanel placesPanel;

	/** The panel to show and choose overlay information. */
	private OverlaysPanel overlaysPanel;

	/**
	 * Create the control panel.
	 * 
	 * @param parent the parent composite
	 * @param style the style of the parent composite
	 * @param control Siafu's controller
	 */
	public ControlPanel(final Composite parent, final int style,
			final Controller control) {
		super(parent, style);
		this.setLayout(new GridLayout(1, false));
		clock = new Clock(this, SWT.NORMAL, control);
		// FIXME: UNder construction
		// itemChooser = new ItemChooser(this, SWT.BORDER, control);
		// status = new EmptyStatus(this, SWT.BORDER);

		createPanelFolder(control);

		this.pack();
	}

	/**
	 * Create the folder that contains all the tabs.
	 * 
	 * @param control siafu's controller
	 * @return the CTabFolder for the tabs
	 */
	private CTabFolder createPanelFolder(final Controller control) {
		CTabFolder folder = new CTabFolder(this, SWT.BORDER);
		folder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		folder.setMinimizeVisible(false);
		folder.setMaximizeVisible(false);
		folder.setSimple(false);
		folder.setUnselectedImageVisible(true);

		agentsPanel = new AgentsPanel(control.getGUI(), folder, SWT.None);
		agentsTab = new CTabItem(folder, SWT.NONE);
		agentsTab.setText("Agents");
		agentsTab.setImage(new Image(Display.getCurrent(), getClass()
				.getResourceAsStream("/misc/agents.png")));
		agentsTab.setControl(agentsPanel);

		placesPanel = new PlacesPanel(control.getGUI(), folder, SWT.None);
		placesTab = new CTabItem(folder, SWT.NONE);
		placesTab.setText("Places");
		placesTab.setImage(new Image(Display.getCurrent(), getClass()
				.getResourceAsStream("/misc/places.png")));
		placesTab.setControl(placesPanel);

		overlaysPanel =
				new OverlaysPanel(control.getGUI(), folder, SWT.None);
		overlaysTab = new CTabItem(folder, SWT.NONE);
		overlaysTab.setText("Overlays");
		overlaysTab.setImage(new Image(Display.getCurrent(), getClass()
				.getResourceAsStream("/misc/overlays.png")));
		overlaysTab.setControl(overlaysPanel);

		folder.setSelection(0);

		return folder;
	}

	/**
	 * Set an element as selected, so taht its status is shown on the right
	 * panel.
	 * 
	 * @param o the selected element.
	 */
	public void setSelected(final Object o) {
		if (o instanceof Agent) {
			agentsPanel.add((Agent) o);
		} else if (o instanceof Place) {
			placesPanel.add((Place) o);
		} else if (o instanceof Overlay) {
			overlaysPanel.add((Overlay) o);
		}

	}

	/**
	 * Refresh the panel by refreshing each of its subcomponents.
	 * 
	 */
	public void refresh() {
		clock.refresh();
		if (agentsTab.isShowing()) {
			agentsPanel.refresh();
		} else if (placesTab.isShowing()) {
			placesPanel.refresh();
		} else if (overlaysTab.isShowing()) {
			overlaysPanel.refresh();
		}
	}

	/**
	 * Get the clock component.
	 * 
	 * @return the clock
	 */
	public Clock getClock() {
		return clock;
	}

	/**
	 * Dispose of SWT allocated resources.
	 * 
	 */
	public void disposeResources() {
		clock.disposeResources();
		this.dispose();
	}
}
