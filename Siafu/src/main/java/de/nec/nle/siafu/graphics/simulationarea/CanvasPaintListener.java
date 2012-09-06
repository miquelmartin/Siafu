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

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import de.nec.nle.siafu.control.Controller;
import de.nec.nle.siafu.graphics.Markers;
import de.nec.nle.siafu.graphics.Painter;
import de.nec.nle.siafu.graphics.controlpanel.ControlPanel;

/**
 * This class implements the paint listener responsible for drawing the canvas
 * of the GUI, where all the action is displayed.
 * 
 * @author Miquel Martin
 * 
 */
public class CanvasPaintListener implements PaintListener {
	/** Siafu's controller. */
	private Controller control;

	/** The painter that does the actual drawing. */
	private Painter painter;

	/**
	 * The markers that need to be drawn.
	 */
	private Markers markers;

	/**
	 * Create the CanasPaintListener.
	 * 
	 * @param control Siafu's controller
	 * @param painter the painter that does the actual drawing
	 * @param controlPanel the control panel of the GUI
	 * @param markers the markers object which contains all the markers to be
	 *            drawn
	 */
	public CanvasPaintListener(final Controller control,
			final Painter painter, final ControlPanel controlPanel,
			final Markers markers) {
		this.control = control;
		this.painter = painter;
		this.markers = markers;
	}

	/**
	 * Draws the canvas that represents the simulation. Note that we don't
	 * draw directly on the event's graphical context (gc) but rather work on
	 * an image which we then draw on it. This prevents image flickering
	 * (detected on some windows PCs).
	 * 
	 * @param e the PaintEvent that triggered the redraw
	 */
	public void paintControl(final PaintEvent e) {
		Image baseImg =
				new Image(Display.getCurrent(), painter.getBounds());
		GC gcAux = new GC(baseImg);

		painter.paintBackground(gcAux);

		painter.paintPeople(gcAux);
		synchronized (markers) {
			painter.paintMarkers(gcAux, markers);
		}
		control.setDrawingCondluded();
		gcAux.dispose();
		e.gc.drawImage(baseImg, 0, 0);
		baseImg.dispose();
	}

}
