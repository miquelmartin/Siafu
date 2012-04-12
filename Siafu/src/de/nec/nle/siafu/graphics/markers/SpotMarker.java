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

package de.nec.nle.siafu.graphics.markers;

import static de.nec.nle.siafu.graphics.ColorTools.parseColorString;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import de.nec.nle.siafu.model.Trackable;

/**
 * This Marker is meant to look like a spotlight. It simply draws a tapered
 * rectangle under the feet of the agent. The marker grows and changes it's
 * alpha value in a pulsating animation that will take your breat away. Or
 * not. Whatever. No one understands art.
 * 
 * @author Miquel Martin
 * 
 */
public class SpotMarker extends Marker {

	/** Initial width of the marker. */
	private static final int INITIAL_WIDTH = 12;

	/** Initial alpha vaule. */
	private static final int INITIAL_ALPHA = 255;

	/** How much the marker tapers at the top. */
	private static final int TAPER = 5;

	/** Value for opaque alpha. */
	private static final int NON_TRANSPARENT_ALPHA = 0xFF;

	/** How much to decrease the marker's alpha at each animation step. */
	private static final int ALPHA_MULTIPLIER = 8;

	/** Vertical offset of the marker. */
	private static final int HEIGHT_OFFSET = 10;

	/** Number of growth steps for the marker's animation. */
	private static final int ANIMATION_STEPS = 10;

	/**
	 * True if the marker has been drawn before, and all calculations are
	 * already done.
	 */
	private boolean drawnBefore = false;

	/** Marker's default color. */
	public static final String DEFAULT_COLOR = "#FFA02C";

	/** The background color before we change it to draw the marker. */
	private Color oldBgColor;

	/** The foreground color before we change it to draw the marker. */
	private Color oldFgColor;

	/** The alpha value before we change it to draw the marker. */
	private int oldAlpha;

	/** The marker's color. */
	private Color color;

	/** Marker's height. */
	private static final int HEIGHT = 6;

	/** Marker width. */
	private int width = INITIAL_WIDTH;

	/** Marker's alpha. */
	private int alfa = INITIAL_ALPHA;

	/** Animation's step. */
	private int count = 0;

	/** Direction of growth for the marker. */
	private int dir = 1;

	/**
	 * Draws a pulsating wedge (that's some description) under the given
	 * Trackable. The wedge pulsates in width and opacityangle to ratete. The
	 * color is set to DEFAULT_COLOR.
	 * 
	 * @param t the Trackable to put the marker on
	 */
	public SpotMarker(final Trackable t) {
		this(t, DEFAULT_COLOR);
	}

	/**
	 * Draws a pulsating wedge (that's some description) under the given
	 * Trackable. The wedge pulsates in width and opacity. The color is set to
	 * colorString.
	 * 
	 * @param t the Trackable to put the marker on
	 * @param colorString the String representing the color of the SpotMarker
	 *            (e.g. #CC0000)
	 * @throws InvalidColorException if the color string is misformated
	 */
	public SpotMarker(final Trackable t, final String colorString) {
		this.t = t;
		this.rgbColor = parseColorString(colorString);
	}

	/**
	 * Keep the old values for colors, fonts and alpha values of the GC.
	 * 
	 * @param gc the GC to backup
	 */
	private void backup(final GC gc) {
		oldFgColor = gc.getForeground();
		oldBgColor = gc.getBackground();
		oldAlpha = gc.getAlpha();
	}

	/**
	 * Restore the values of the GC (Fonts, alpha, colors, etc..) to the state
	 * before we did this run.
	 * 
	 * @param gc the GC to restore
	 */
	private void restore(final GC gc) {
		gc.setForeground(oldFgColor);
		gc.setBackground(oldBgColor);
		gc.setAlpha(oldAlpha);
	}

	/**
	 * Draw the spot marker. Note that it expands and fades at each redraw, to
	 * produce an animated effect.
	 * 
	 * @param gc the gc used to draw the spot marker
	 */
	public void draw(final GC gc) {
		backup(gc);

		if (!drawnBefore) {
			color = new Color(Display.getCurrent(), rgbColor);
			drawnBefore = true;
		}

		gc.setBackground(color);
		gc.setForeground(color);

		if (count > ANIMATION_STEPS) {
			count = 0;
			dir = -1 * dir;
		}
		count++;

		gc.setAlpha(alfa);
		gc.fillPolygon(new int[] {t.getPos().getCol() - width / 2 - TAPER,
				t.getPos().getRow() + HEIGHT + HEIGHT_OFFSET,
				t.getPos().getCol() - width / 2, t.getPos().getRow() + HEIGHT,
				t.getPos().getCol() + width / 2, t.getPos().getRow() + HEIGHT,
				t.getPos().getCol() + width / 2 + TAPER,
				t.getPos().getRow() + HEIGHT + HEIGHT_OFFSET});

		alfa -= dir * ALPHA_MULTIPLIER;
		width += dir;

		gc.setAlpha(NON_TRANSPARENT_ALPHA);

		restore(gc);
	}

	/** Destroy any resources that won't be otherwise freed. */
	@Override
	public void disposeResources() {
		if (color != null) {
			color.dispose();
		}
	}

	/**
	 * Set the trackable on which to draw the Marker.
	 * 
	 * @param newT the trackable
	 * @return the old Trackable being marked, or null if there was none
	 */
	@Override
	public Trackable setTrackable(final Trackable newT) {
		Trackable oldT = t;
		t = newT;
		drawnBefore = false;
		return oldT;
	}

	/**
	 * Get the RGB color for the marker. It corresponds to the spot color.
	 * 
	 * @return the RGB color
	 */
	@Override
	public RGB getRGB() {
		return rgbColor;
	}

	/**
	 * Set the color of the spot.
	 * 
	 * @param rgb the RGB color
	 */
	@Override
	public void setRGB(final RGB rgb) {
		color.dispose();
		this.rgbColor = rgb;
		drawnBefore = false;
	}

}
