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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import de.nec.nle.siafu.model.Trackable;

/**
 * Draw a marker shaped like a balloon in comics, when a character talks.
 * 
 * @author Miquel Martin
 * 
 */
public class BalloonMarker extends Marker {
	/** Width of the balloon's comma. */
	private static final int COMMA_WIDTH = 8;

	/** Height of the balloon's comma. */
	private static final int COMMA_HEIGHT = 15;

	/** Horizontal text padding. */
	private static final int TEXT_PADDING = 10;

	/** Size of the balloon's rounded corners. */
	private static final int CORNER_SIZE = 5;

	/** Alpha value for the semi transparent balloon. */
	private static final int TRANSPARENT_ALPHA = 0x77;

	/** The vertical offset of the marker. */
	private static final int I_OFFSET = 60;

	/** The horizontal offset of the marker. */
	private static final int J_OFFSET = 10;

	/** The balloon's height. */
	private static final int HEIGHT = 20;

	/** The balloon's default color. */
	public static final String DEFAULT_COLOR = "#C00000";

	/** The white color. */
	private Color white;

	/** The foreground color before we change it to draw the marker. */
	private Color oldFgColor;

	/** The background color before we change it to draw the marker. */
	private Color oldBgColor;

	/** The font before we change it to draw the marker. */
	private Font oldFont;

	/** The alpha value before we change it to draw the marker. */
	private int oldAlpha;

	/** The marker's caption. */
	private String caption;

	/** The marker's color. */
	private Color color;

	/** The marker's font. */
	private Font font;

	/** The width of the balloon which fits the caption text. */
	private int width;

	/**
	 * True if the marker has been drawn before, and all calculations are
	 * already done.
	 */
	private boolean drawnBefore = false;

	/**
	 * Draws a cartoon like balloon on top of the Trackable, displaying the
	 * tackable's name. The BalloonMarker color is set to DEFAULT_COLOR.
	 * 
	 * @param t the trackable to put the marker on
	 * 
	 */
	public BalloonMarker(final Trackable t) {
		this(t, DEFAULT_COLOR);
	}

	/**
	 * Draws a cartoon like balloon on top of the Trackable, displaying the
	 * tackable's name.
	 * 
	 * @param t the trackable to put the marker on
	 * @param colorString the String representing the Balloon's color (e.g.
	 *            #CC0000);
	 * 
	 * @throws InvalidColorException if the color string is misformated
	 */
	public BalloonMarker(final Trackable t, final String colorString) {
		this(t, colorString, t.getName());
	}

	/**
	 * Draws a cartoon like balloon on top of the Trackable, displaying the
	 * caption text.
	 * 
	 * @param t the trackable to put the marker on
	 * @param colorString the String representing the Balloon's color (e.g.
	 *            #CC0000);
	 * @param caption the text to display inside the balloon
	 * @throws InvalidColorException if the color string is misformated
	 */
	public BalloonMarker(final Trackable t, final String colorString,
			final String caption) {
		this.t = t;
		this.caption = caption;
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
		oldFont = gc.getFont();
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
		gc.setFont(oldFont);
		gc.setAlpha(oldAlpha);
	}

	/**
	 * Draw the balloon using the given GC.
	 * 
	 * @param gc the GC used for the drawing
	 */
	public void draw(final GC gc) {

		if (!drawnBefore) {
			white = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
			this.font = gc.getFont();
			width = gc.textExtent(caption).x;
			color = new Color(Display.getCurrent(), rgbColor);
			drawnBefore = true;
		}

		backup(gc);

		gc.setBackground(color);
		gc.setForeground(white);
		gc.setFont(font);

		gc.setAlpha(TRANSPARENT_ALPHA);

		gc.fillRoundRectangle(t.getPos().getCol() - J_OFFSET, t.getPos()
				.getRow()
				- I_OFFSET, width + TEXT_PADDING, HEIGHT, CORNER_SIZE,
			CORNER_SIZE);

		gc.fillPolygon(new int[] {t.getPos().getCol() + 2,
				t.getPos().getRow() - I_OFFSET + HEIGHT,
				t.getPos().getCol() + 2,
				t.getPos().getRow() - I_OFFSET + HEIGHT + COMMA_HEIGHT,
				t.getPos().getCol() + COMMA_WIDTH,
				t.getPos().getRow() - I_OFFSET + HEIGHT - 1});

		gc.setAlpha(Integer.MAX_VALUE);

		gc.drawText(caption, t.getPos().getCol() - J_OFFSET + CORNER_SIZE,
			t.getPos().getRow() - I_OFFSET + 2, SWT.DRAW_TRANSPARENT);

		restore(gc);
	}

	/** Destroy any resources that won't be otherwise freed. */
	@Override
	public void disposeResources() {
		if (color != null) {
			//Removed according to bug 1866023
			//color.dispose();
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
	 * Get the RGB color for the marker. It corresponds to the Balloon color.
	 * 
	 * @return the RGB color
	 */
	@Override
	public RGB getRGB() {
		return rgbColor;
	}

	/**
	 * Set the color of the balloon.
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
