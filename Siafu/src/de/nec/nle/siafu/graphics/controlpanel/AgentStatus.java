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

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import de.nec.nle.siafu.graphics.ColorTools;
import de.nec.nle.siafu.graphics.GUI;
import de.nec.nle.siafu.graphics.Markers;
import de.nec.nle.siafu.graphics.markers.BalloonMarker;
import de.nec.nle.siafu.graphics.markers.Marker;
import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.Overlay;
import de.nec.nle.siafu.types.Publishable;

/**
 * The status item that displays information on an Agent.
 * 
 * @author Miquel Martin
 * 
 */
public class AgentStatus extends BaseStatus {

	/** The labels with the overlay information. */
	private LabelPair[] ovLabels;

	/** The labels with the agent info field data. */
	private LabelPair[] infoLabels;

	/** The labels with the simulation intrinsec labels. */
	private LabelPair[] agentLabels;

	/** The button which indicates if the agent is controlled by the GUI. */
	private Button movesFreelyButton;

	/** The list of overlays. */
	private Collection<Overlay> overlays;

	/** The composite for the agent labels. */
	private Composite agentComposite;

	/** The composite for the overlay labels. */
	private Composite overlayComposite;

	/** The composite for the info field labels. */
	private Composite infoComposite;

	/** The marker associated to this agent. */
	protected Marker marker;

	/**
	 * Create a status item for an agent.
	 * 
	 * @param parent the parent composite
	 * @param style the composite style
	 * @param agent the agent whose status we want to display
	 * @param icon the icon representig the agent
	 * @param gui the gui that conains it all
	 * @param agentsPanel the panel that contains all the status items
	 */
	public AgentStatus(final Composite parent, final int style,
			final Agent agent, final Image icon, final GUI gui,
			final AgentsPanel agentsPanel) {
		super(parent, gui, agentsPanel, agent, agent.getName(), icon,
				ColorTools.parseColorString(BalloonMarker.DEFAULT_COLOR));
		marker = new BalloonMarker(agent);
		gui.getMarkers().addMarker(marker, Markers.Type.INTERNAL);
	}

	/**
	 * Add the labels for this status.
	 * 
	 * @param parent the parent composite
	 */
	protected void addBodyElements(final Composite parent) {
		agentComposite = new Composite(parent, SWT.NORMAL);
		GridLayout glPlaceComposite = new GridLayout();
		glPlaceComposite.marginWidth = 0;
		agentComposite.setLayout(glPlaceComposite);

		infoComposite = new Composite(parent, SWT.NORMAL);
		GridLayout glInfoComposite = new GridLayout();
		glInfoComposite.marginWidth = 0;
		infoComposite.setLayout(glInfoComposite);

		overlayComposite = new Composite(parent, SWT.NORMAL);
		GridLayout glOverlayComposite = new GridLayout();
		glOverlayComposite.marginWidth = 0;
		overlayComposite.setLayout(glOverlayComposite);

		createAgentLabels(agentComposite);
		createOverlayLabels(overlayComposite);
		createInfoLabels(infoComposite);
		addMovesFreelyButton(parent);

	}

	/**
	 * Draw a button that lets the user be controlled by the GUI or the
	 * simulation.
	 * 
	 * @param parent the parent composite
	 */
	private void addMovesFreelyButton(final Composite parent) {
		Agent a = (Agent) content;
		movesFreelyButton = new Button(parent, SWT.CHECK);
		movesFreelyButton.setText("Moves freely");
		movesFreelyButton.setLayoutData(new GridData(
				GridData.GRAB_HORIZONTAL));
		movesFreelyButton.setSelection(a.isOnAuto());
		movesFreelyButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if (movesFreelyButton.getSelection()) {
					((Agent) content).returnControl();
				} else {
					((Agent) content).getControl();
				}
			}
		});
	}

	/**
	 * Update the color of the agent's marker.
	 * 
	 * @param newColor the chosen color
	 */
	protected void onColorChanged(final RGB newColor) {
		marker.setRGB(newColor);
	}

	/**
	 * Create the labels with information specific to the simulation.
	 * 
	 * @param parent the parent composite
	 */
	private void createAgentLabels(final Composite parent) {
		agentLabels = new LabelPair[2];
		agentLabels[0] = new LabelPair(parent, SWT.NONE, "Position");
		agentLabels[1] = new LabelPair(parent, SWT.NONE, "Destination");
	}

	/**
	 * Create the labels showing the overlay values for the agent's position.
	 * 
	 * @param parent the parent composite
	 */
	private void createOverlayLabels(final Composite parent) {
		overlays = gui.getOverlays();
		ovLabels = new LabelPair[overlays.size()];

		Iterator<Overlay> ovIt = overlays.iterator();
		int i = 0;

		while (ovIt.hasNext()) {
			Overlay ov = ovIt.next();
			ovLabels[i++] = new LabelPair(parent, SWT.NONE, ov.getName());
		}
	}

	/**
	 * Create the labels with the agent's info field data.
	 * 
	 * @param parent the parent composite
	 */
	private void createInfoLabels(final Composite parent) {
		Set<String> infoSet = Agent.getInfoKeys();
		Iterator<String> it = infoSet.iterator();
		infoLabels = new LabelPair[infoSet.size()];

		for (int i = 0; i < infoSet.size(); i++) {
			infoLabels[i] = new LabelPair(parent, SWT.NONE, it.next());
		}
	}

	/**
	 * Select the moves freely button according to the being controlled or
	 * not.
	 */
	private void refreshMovesFreelyButton() {
		Agent agent = (Agent) content;
		movesFreelyButton.setSelection(agent.isOnAuto());

	}

	/** Update the labels with agent info. */
	private void refreshAgentLabels() {
		Agent agent = (Agent) content;
		agentLabels[0].setValue(agent.getPos().getPrettyCoordinates());
		agentLabels[1].setValue(agent.isAtDestination() ? "none" : agent
				.getDestination().toString());
	}

	/** Update the overlay value labels. */
	private void refreshOvLabels() {
		Agent agent = (Agent) content;
		Iterator<Overlay> ovIt = overlays.iterator();
		int i = 0;

		while (ovIt.hasNext()) {
			Overlay ov = ovIt.next();
			ovLabels[i++].setValue(ov.getValue(agent.getPos()).toString());
		}
	}

	/** Update the info labels. */
	private void refreshInfoLabels() {
		Agent agent = (Agent) content;
		Collection<Publishable> values = agent.getInfoValues();
		Iterator<Publishable> it = values.iterator();

		for (int i = 0; i < values.size(); i++) {
			Object o = it.next();
			String value = (o != null) ? o.toString() : "null";
			infoLabels[i].setValue(value);
		}
	}

	/** Refresh the status item. */
	public void refresh() {
		refreshAgentLabels();
		refreshInfoLabels();
		refreshOvLabels();
		refreshMovesFreelyButton();
	}

	/**
	 * Clean up those resources, specially those that won't be disposed when
	 * the conainer composite is disposed.
	 * 
	 */
	public void disposeResources() {
		super.disposeResources();
	}
}
