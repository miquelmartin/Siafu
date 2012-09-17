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
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;

import de.nec.nle.siafu.model.Trackable;

/**
 * A marker shaped a bit like a fat cricket pole. Or a vertical price sticker.
 * Or something such, anyway. It's a pointy stick, stuck on the ground, with a
 * vertical text area and a colred sticker on top.
 * 
 * @author Miquel Martin
 * 
 */
public class StickMarker extends Marker {
	/** The rotation angle of the text. */
	private static final float TEXT_ROTATION = -90f;

	/** A non transparent alpha, for the text. */
	private static final int SOLID_ALPHA = 255;

	/** The transparency value for the Marker. */
	private static final int TRANSPARENT_ALPHA = 128;

	/** The sticks' width. */
	private static final int WIDTH = 20;

	/** The margin around the text. */
	private static final int TEXT_MARGIN = 6;

	/** The height of the bottom part of the stick. */
	private static final int BOTTOM_HEIGHT = 17;

	/** The height of the label at the top of the stick. */
	private static final int TOP_HEIGHT = 21;

	/** The stick's default color. */
	public static final String DEFAULT_COLOR = "#0000CC";

	/** The default color of the label on top of the stick. */
	public static final String TOP_DEFAULT_COLOR = "#FFFFFF";

	/** The RGB color for the top label. */
	private RGB rgbTopColor;

	/** The foreground color before we change it to draw the marker. */
	private Color oldFgColor;

	/** The background color before we change it to draw the marker. */
	private Color oldBgColor;

	/** The font before we change it to draw the marker. */
	private Font oldFont;

	/** The alpha value before we change it to draw the marker. */
	private int oldAlpha;

	/** The stick's color. */
	private Color color;

	/** The stick's top label color. */
	private Color topColor;

	/** The marker's font. */
	private Font font;

	/** The foreground color before we change it to draw the marker. */
	private String caption;

	/** The width of the caption text. */
	private int textWidth;

	/** The height of the balloon which fits the caption text. */
	private int textHeight;

	/** How much to offset the text before drawing. */
	private int textOffset;

	/**
	 * True if the marker has been drawn before, and all calculations are
	 * already done.
	 */
	private boolean drawnBefore = false;

	/**
	 * 
	 * Create a Stick Marker taking the trackable name as caption, the default
	 * color DEFAULT_COLOR as stick color and TOP_DEFAULT_COLOR as the top
	 * color.
	 * 
	 * @param t the trackable on which to put the marker
	 */
	public StickMarker(final Trackable t) {
		this(t, DEFAULT_COLOR, TOP_DEFAULT_COLOR);
	}

	/**
	 * 
	 * Create a Stick Marker taking the trackable name as caption, and the
	 * default color DEFAULT_COLOR as stick color.
	 * 
	 * @param t the trackable on which to put the marker
	 * @param topColorString the string for the color on the top of the stick
	 *            (e.g. #00CC00)
	 * 
	 * @throws InvalidColorException if the color string is misformated
	 */
	public StickMarker(final Trackable t, final String topColorString) {
		this(t, DEFAULT_COLOR, topColorString);
	}

	/**
	 * Create a Stick Marker taking the trackable name as caption.
	 * 
	 * @param t the trackable on which to put the marker
	 * @param colorString a String representing the color of the stick (e.g.
	 *            #CC0000)
	 * @param topColorString the string for the color on the top of the stick
	 *            (e.g. #00CC00)
	 * 
	 * @throws InvalidColorException if the color string is misformated
	 */
	public StickMarker(final Trackable t, final String colorString,
			final String topColorString) {
		this(t, colorString, topColorString, t.getName());
	}

	/**
	 * Create a Stick Marker. Stick Markers look like like a point stick stuck
	 * on the ground. The top of the stick has a characteristic color, and the
	 * text is displayed vertically along it.
	 * 
	 * @param t the trackable on which to put the marker
	 * @param colorString a String representing the color of the stick (e.g.
	 *            #CC0000)
	 * @param topColorString the string for the color on the top of the stick
	 *            (e.g. #00CC00)
	 * @param caption the text to show on the stick
	 * 
	 * @throws InvalidColorException if the color string is misformated
	 */
	public StickMarker(final Trackable t, final String colorString,
			final String topColorString, final String caption) {
		this.t = t;
		this.caption = caption;
		this.rgbColor = parseColorString(colorString);
		this.rgbTopColor = parseColorString(topColorString);
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
	 * Draw the stick.
	 * 
	 * @param gc the GC to draw it with
	 */
	public void draw(final GC gc) {

		if (!drawnBefore) {
			this.font = gc.getFont();
			textWidth = gc.textExtent(t.getName()).x;
			textHeight = gc.textExtent(t.getName()).y;
			textOffset = (WIDTH - textHeight) / 2;
			color = new Color(Display.getCurrent(), rgbColor);
			topColor = new Color(Display.getCurrent(), rgbTopColor);

			drawnBefore = true;
		}

		backup(gc);

		gc.setAlpha(TRANSPARENT_ALPHA);

		Color white = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);

		gc.setFont(font);

		int x0, y0;
		gc.setBackground(topColor);
		// Draw the top
		x0 = t.getPos().getCol() - (WIDTH / 2);
		y0 = t.getPos().getRow() - BOTTOM_HEIGHT - textWidth - TEXT_MARGIN;
		gc.fillPolygon(new int[] {x0, y0, x0, y0 - TOP_HEIGHT + 2, x0 + 2,
				y0 - TOP_HEIGHT, x0 + WIDTH - 2, y0 - TOP_HEIGHT,
				x0 + WIDTH, y0 - TOP_HEIGHT + 2, x0 + WIDTH, y0

		});

		gc.setBackground(color);

		// Draw the center
		x0 = t.getPos().getCol() - (WIDTH / 2);
		y0 = t.getPos().getRow() - BOTTOM_HEIGHT - textWidth - TEXT_MARGIN;
		gc.fillRectangle(x0, y0, WIDTH, textWidth + TEXT_MARGIN);

		// Draw the bottom
		gc.setAntialias(SWT.OFF);
		x0 = t.getPos().getCol() - (WIDTH / 2);
		y0 = t.getPos().getRow();
		gc.fillPolygon(new int[] {x0, y0 - BOTTOM_HEIGHT,
				x0 + (WIDTH / 2), y0, x0 + (WIDTH), y0 - BOTTOM_HEIGHT

		});
		gc.setAntialias(SWT.ON);
		gc.setForeground(color);
		gc
				.drawPolygon(new int[] {x0, y0 - BOTTOM_HEIGHT,
						x0 + (WIDTH / 2), y0, x0 + (WIDTH) - 1,
						y0 - BOTTOM_HEIGHT});

		// Draw the text
		gc.setForeground(white);
		gc.setAlpha(SOLID_ALPHA);
		Transform trans = new Transform(gc.getDevice());
		trans.rotate(TEXT_ROTATION);
		gc.setTransform(trans);
		gc.drawText(caption, -(t.getPos().getRow() - BOTTOM_HEIGHT)
				+ (TEXT_MARGIN / 2), t.getPos().getCol() - (WIDTH / 2)
				+ textOffset, SWT.DRAW_TRANSPARENT);
		gc.setTransform(null);
		trans.dispose();

		restore(gc);
	}

	/** Destroy any resources that won't be otherwise freed. */
	@Override
	public void disposeResources() {
		if (color != null) {
			color.dispose();
		}
		if (topColor != null) {
			topColor.dispose();
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
	 * Get the RGB color for the marker, which corresponds to the color of the
	 * stick itself.
	 * 
	 * @return the RGB color
	 */
	@Override
	public RGB getRGB() {
		return rgbColor;
	}

	/**
	 * Get the top RGB color for the marker, which corresponds to the color of
	 * the label on top of the stick.
	 * 
	 * @return the RGB color
	 */

	public RGB getTopRGB() {
		return rgbTopColor;
	}

	/**
	 * Set the color of the stick.
	 * 
	 * @param rgb the RGB color
	 */
	@Override
	public void setRGB(final RGB rgb) {
		disposeResources();
		this.rgbColor = rgb;
		drawnBefore = false;
	}

	/**
	 * Set the color of the label on top of the stick.
	 * 
	 * @param rgbTop the RGB color
	 */
	public void setRGBTop(final RGB rgbTop) {
		disposeResources();
		this.rgbTopColor = rgbTop;
		drawnBefore = false;
	}
}
