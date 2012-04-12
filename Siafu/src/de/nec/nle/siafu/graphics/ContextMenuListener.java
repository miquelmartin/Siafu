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

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import de.nec.nle.siafu.control.Controller;
import de.nec.nle.siafu.exceptions.NothingNearException;
import de.nec.nle.siafu.exceptions.PositionUnreachableException;
import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.Place;
import de.nec.nle.siafu.model.Position;
import de.nec.nle.siafu.model.Trackable;
import de.nec.nle.siafu.model.World;

/**
 * This menu pops up upon right clicking on the canvas, and allows the user to
 * operate with the entities near the clicked point. For example, one can move
 * the selected agent to that point, or select one of the nearby agents.<br>
 * 
 * Note that the implementation is a bit strange. Instead of adding the menu
 * to the canvas, it is left stand alone, and the mouse events in the canvas
 * trigger my own fucntion, show, which sets the visibility to true, causing
 * the menuShown event to be triggered. This is so because the OS adds window
 * decorations which are not accounted for when getting the shell and canvas
 * location, making it impossible to locate where precisely int he canvas the
 * user clicked.
 * 
 * @author Miquel Martin
 * 
 */
public class ContextMenuListener implements MenuListener {
	/**
	 * The distance in simulation grid points that's considered as "near" for
	 * the purposes of selection.
	 */
	private static final int NEARBY_DISTANCE = 30;

	/** Whether the simulation was paused before we right clicked the canvas. */
	private boolean wasPaused = false;

	/** Siafu's controller. */
	private Controller control;

	/** The container gui. */
	private GUI gui;

	/** The simulation's world. */
	private World world;

	/** The context menu that we listen for. */
	private Menu contextMenu;

	/** The container shell. */
	private Shell shell;

	/** The simulation canvas. */
	private Canvas canvas;

	/** The position that the user clicked on. */
	private Position clickPos;

	/**
	 * Create the context menu listener.
	 * 
	 * @param control Siafu's controller
	 * @param contextMenu the menu we listen for
	 * @param shell the container shell
	 * @param canvas the canvas where the simulation is being shown
	 */
	public ContextMenuListener(final Controller control,
			final Menu contextMenu, final Shell shell, final Canvas canvas) {
		this.control = control;
		this.gui = control.getGUI();
		this.world = control.getWorld();
		this.contextMenu = contextMenu;
		this.shell = shell;
		this.canvas = canvas;
	}

	/**
	 * Make the menu visible. This could have been done in a more regular way
	 * if it weren't because we need the clicked position out of the canvas.
	 * The standard SWT mechanisms only return a position relative to the
	 * Display, and we simply can not account for the window manager border
	 * width, so the position wasn't accurate.
	 * 
	 * @param clickedPosition the position the user clicked on
	 */
	public void show(final Position clickedPosition) {
		this.clickPos = clickedPosition;
		contextMenu.setLocation(clickPos.getCol() + shell.getLocation().x
				+ canvas.getLocation().x, clickPos.getRow()
				+ shell.getLocation().y + canvas.getLocation().y);
		contextMenu.setVisible(true);
	}

	/**
	 * Fill the menu contents when the user right clicks on the canvas.
	 * Essentially, we figure out what agents are near the cursor, and let the
	 * user select them, or move the already selected one to the cursor's
	 * position.
	 * 
	 * @param e the trigger for the show
	 */
	public void menuShown(final MenuEvent e) {
		wasPaused = control.isPaused();
		control.setPaused(true);

		// Clean up the old menu
		for (MenuItem mi : contextMenu.getItems()) {
			mi.dispose();
		}

		boolean destinationSet = addDestinationSetter();
		boolean agentsSet = addAgents();

		if (destinationSet && agentsSet) {
			new MenuItem(contextMenu, SWT.SEPARATOR, 1);
		}

	}

	/**
	 * Add the option to move the selected user to this position.
	 * 
	 * @return true if there was a chosen user and we have really added the
	 *         option. It's important to draw separators.
	 */
	private boolean addDestinationSetter() {
		Trackable t = gui.getActive();
		Agent a = null;
		if (!(t instanceof Agent)) {
			return false;
		}

		a = (Agent) t;

		Place tempPlace;
		try {
			tempPlace =
					new Place("UserSelected", clickPos, world, a.getPos());
		} catch (PositionUnreachableException ex) {
			MenuItem mi = new MenuItem(contextMenu, SWT.PUSH);
			mi.setEnabled(false);
			mi.setText(a.getName() + " can't get here");
			return true;
		}

		MenuItem mi = new MenuItem(contextMenu, SWT.PUSH);
		mi.setData(tempPlace);
		// mi.setImage(new Image(Display.getCurrent(), getClass()
		// .getResourceAsStream("/res/misc/movehere.png")));
		// // TODO Image missing for the item
		mi.setText("Move " + a.getName() + " here");
		mi.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(final SelectionEvent e) {
				// TODO Auto-generated method stub

			}

			public void widgetSelected(final SelectionEvent e) {
				Agent a;
				Object o = gui.getActive();

				if (o instanceof Agent) {
					a = (Agent) o;

					a.setDestination(((Place) ((MenuItem) e.getSource())
							.getData()));
					a.getControl();
				}
			}

		});
		return true;
	}

	/**
	 * Add the agents found near the cursor's position.
	 * 
	 * @return true if at least one agent is added.
	 */
	private boolean addAgents() {
		/*
		 * TODO: before drawing, one should check the size of all the images,
		 * and resize them to fit the smallest one. Otherwise the images
		 * arestrecthed to the first found size, and look ugly.
		 */
		try {
			Collection<Trackable> agents =
					world.findAllAgentsNear(clickPos, NEARBY_DISTANCE,
						true);
			for (Trackable t : agents) {
				Agent a = (Agent) t;
				MenuItem mi = new MenuItem(contextMenu, SWT.CASCADE);
				mi.setText(a.getName());
				mi.setData(a);
				mi.setImage(gui.getAgentSprite(a).getImage(a.getDir()));
				mi.addSelectionListener(new SelectionListener() {
					public void widgetDefaultSelected(
							final SelectionEvent e) {

					}

					public void widgetSelected(final SelectionEvent e) {
						// TODO Auto-generated method stub
						Agent agent =
								(Agent) ((MenuItem) e.getSource())
										.getData();
						gui.getControlPanel().setSelected(agent);
						gui.setActiveTrackable(agent);
					}
				});
			}
		} catch (NothingNearException e1) {
			return false;
		}
		return true;
	}

	/**
	 * When the menu is hidden, un-pause the simulator if we paused it on
	 * opening the menu.
	 * 
	 * @param e the event that triggers the closure
	 */
	public void menuHidden(final MenuEvent e) {
		if (!wasPaused) {
			control.setPaused(false);
		}

	}

}
