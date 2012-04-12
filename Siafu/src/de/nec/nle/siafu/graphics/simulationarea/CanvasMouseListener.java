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

package de.nec.nle.siafu.graphics.simulationarea;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;

import de.nec.nle.siafu.exceptions.NothingNearException;
import de.nec.nle.siafu.exceptions.PositionOutOfTheMapException;
import de.nec.nle.siafu.exceptions.PositionUnreachableException;
import de.nec.nle.siafu.graphics.GUI;
import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.Place;
import de.nec.nle.siafu.model.Position;
import de.nec.nle.siafu.model.Trackable;
import de.nec.nle.siafu.model.World;

/**
 * The listener that governs what happens when clicking around on the GUI's
 * canvas.
 * 
 * @author Miquel Martin
 * 
 */
public class CanvasMouseListener implements MouseListener {
	/** The number for the left mouse button. */
	private static final int LEFT_MOUSE_BUTTON = 1;

	/** The number for the right mouse button. */
	private static final int RIGHT_MOUSE_BUTTON = 3;

	/** The container GUI. */
	private GUI gui;

	/** The simulation's world. */
	private World world;

	/**
	 * Create the mouse listener for the canvas.
	 * 
	 * @param gui The container GUI
	 * @param world The simulation's world
	 */
	public CanvasMouseListener(final GUI gui, final World world) {
		this.gui = gui;
		this.world = world;
	}

	/**
	 * What to do when the mouse button is pressed. Nothing.
	 * 
	 * @param e the mouse event that triggered this method
	 */
	public void mouseDown(final MouseEvent e) {
	}

	/**
	 * What to do when a double click occurs. We make the selected agent move
	 * over to the clicked place.
	 * 
	 * @param e the mouse event that triggered this method
	 */
	public void mouseDoubleClick(final MouseEvent e) {
		// FIXME: A double click should not be also twice single click. Is
		// there a workaround for this that doesn't delay the single click
		// too much?

		Agent p;
		Object o = gui.getActive();

		if (o instanceof Agent) {
			p = (Agent) o;
		} else {
			return;
		}

		Place tempPlace;
		try {
			tempPlace =
					new Place("UserSelected", new Position(e.y, e.x),
							world, p.getPos());
		} catch (PositionUnreachableException ex) {
			System.out.println("Position unreachable");
			return;
		}
		p.getControl();
		p.setDestination(tempPlace);
	}

	/**
	 * What to do when the mouse button is released. This depends on the
	 * button:
	 * <ul>
	 * <li> Pressing the left mouse button selects an agent near the pointer,
	 * and has no effect if the agent is already selected
	 * <li> Pressing the right menu brings up the context menu to choose the
	 * agent or move the selected one to the clicked point
	 * </ul>
	 * 
	 * @param e the mouse event that triggered this method
	 */
	public void mouseUp(final MouseEvent e) {
		Trackable target;
		Position clickPos = new Position(e.y, e.x);
		switch (e.button) {
		case LEFT_MOUSE_BUTTON:
			try {
				target = world.findAgentNear(clickPos, true);
			} catch (PositionOutOfTheMapException ex) {
				System.err.println("Stop clikcing outside the map!");

				return;
			} catch (NothingNearException ex) {
				return;
			}

			gui.setActiveTrackable(target);
			gui.getControlPanel().setSelected(target);
			break;

		case RIGHT_MOUSE_BUTTON:
			gui.showContextMenu(clickPos);
			break;

		default:
			System.err.println("Unknown mouse button (" + e.button + ")");
			break;
		}
	}

}
