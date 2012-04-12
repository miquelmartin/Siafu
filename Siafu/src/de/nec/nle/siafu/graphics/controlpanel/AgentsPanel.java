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

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.nec.nle.siafu.graphics.GUI;
import de.nec.nle.siafu.graphics.Markers;
import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.Trackable;

/**
 * This class defines the panel that displays the agents in the GUI.
 * 
 * @author Miquel Martin
 * 
 */
public class AgentsPanel extends BasePanel {

	/**
	 * Instantiate the panel.
	 * 
	 * @param gui the gui in which the panel fits
	 * @param parent the parent composite
	 * @param style the style of the panel's composite
	 */
	public AgentsPanel(final GUI gui, final Composite parent,
			final int style) {
		super(gui, parent, style);
	}

	/**
	 * Add all the agents to the combo box.
	 */
	protected void addSelectableElements() {
		String[] items = new String[gui.getAgents().size()];

		int i = 0;
		for (Agent a : gui.getAgents()) {
			items[i] = a.getName();
			i++;
		}

		Arrays.sort(items);
		selectionCombo.setItems(items);
	}

	/**
	 * Create an AgentStatus showing the information for the agent in o.
	 * 
	 * @param parent the parent composite
	 * @param o the agent to display
	 * @return the newly created status for the agent
	 */
	protected BaseStatus createStatusItem(final Composite parent,
			final Object o) {
		Agent a = (Agent) o;
		return new AgentStatus(parent, SWT.NONE, a, gui.getAgentSprite(a)
				.getImage(a.getDir()), gui, this);
	}

	/**
	 * Destroy an agent status item. This involves removing the associated
	 * markers.
	 * 
	 * @param baseStatus the status item that needs destroying
	 */
	protected void destroyStatusItem(final BaseStatus baseStatus) {
		gui.getMarkers().removeMarker((Trackable) baseStatus.getContent(),
			Markers.Type.INTERNAL);
		if (gui.getActive() != null
				&& gui.getActive().equals(baseStatus.getContent())) {
			gui.setActiveTrackable(null);
		}
	}

	/**
	 * Display the chosen action, make him active and put a Marker on him.
	 * 
	 * @param selection the selected agent's name
	 */
	protected void onSelectionMade(final String selection) {
		Agent agent = null;
		String name = selection;
		for (Agent a : gui.getAgents()) {
			if (name.equals(a.getName())) {
				agent = a;
				break;
			}
		}
		if (agent == null) {
			throw new RuntimeException(
					"You selected a non existing element."
							+ "And how did you do it if I may ask?");
		}
		gui.getControlPanel().setSelected(agent);
		gui.setActiveTrackable(agent);
	}

	/**
	 * Clean up those resources, specially those that won't be disposed when
	 * the conainer composite is disposed.
	 * 
	 */
	protected void disposeResources() {
		super.disposeResources();
	}
}
