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

import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.commons.lang.NotImplementedException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import de.nec.nle.siafu.control.Controller;
import de.nec.nle.siafu.graphics.Markers.Type;
import de.nec.nle.siafu.graphics.markers.Marker;
import de.nec.nle.siafu.model.Agent;
import de.nec.nle.siafu.model.Overlay;
import de.nec.nle.siafu.model.Place;
import de.nec.nle.siafu.model.Position;
import de.nec.nle.siafu.model.Trackable;
import de.nec.nle.siafu.model.World;

/**
 * This class handles the actual drawing on the simulation's canvas for most of
 * the simulator's graphics. A notable exception are Markers, which draw
 * themselves.
 * 
 * @author Miquel Martin
 * 
 */
public class Painter {
	/** Mask for the places overlay palette. */
	private static final int PLACE_PALETTE_MASK = 0x3FC;

	/**
	 * How many minutes per hour. On earth, of course. Or any similar planet
	 * with equal rotation and translation period. Yes, checkstyle forced me to
	 * remove the magic number.
	 */
	private static final float MINUTES_PER_HOUR = 60f;

	/** How much to darken the background. */
	private static final int DARKNESS_STEP = 100;

	/** Blue mask. */
	private static final int BLUE_MASK = 0x0000FF;

	/** Gren mask. */
	private static final int GREEN_MASK = 0x00FF00;

	/** Red mask. */
	private static final int RED_MASK = 0xFF0000;

	/**
	 * When the blue sky is painted on the background, this value defines how
	 * transparent the blue should be.
	 */
	private static final int BG_ALPHA_STEP = 0xA0;

	/** The maximum direction value. */
	private static final int MAX_DIR = 8;

	/** Er... 0xFF. Checkstyle forced me. */
	private static final int FF = 0xff;

	/** Name says it all. Checkstyle forced me. */
	private static final int ONE_HUNDRED = 100;

	/** Color depth of the images. */
	private static final int COLOR_DEPTH = 24;

	/** Alpha value when drawing an overlay on the background. */
	private static final int OVERLAY_ALPHA = 170;

	/** The SWT display. */
	private Display display;

	/**
	 * Sunrise hour.
	 */
	private static final int SUNRISE = 6;

	/**
	 * Sunset hour.
	 */
	private static final int SUNSET = 20;

	/**
	 * How many steps does it take to go from full light to full darkness.
	 */
	private static final int SUNSET_STEPS = 5;

	/** The image of the background with an overlay on top. */
	private Image overlayImg;

	/** The sprites of the agents, sorted by sprite name. */
	private HashMap<String, Sprite> personImg = new HashMap<String, Sprite>();

	/** A reference to the Siafu controller. */
	private Controller control;

	/** A reference to the simulation's world. */
	private World world;

	/** The ImageData for the background image. */
	private ImageData backgroundData = null;

	/** The ImageData for a blue image (used for the sky). */
	private ImageData skyData = null;

	/**
	 * The set of images that make the background, where 0 is normal, and the
	 * rest show a progressive increase the sky darnkess.
	 */
	private Image[] background = null;

	/** The simulation time. */
	private Calendar time;

	/**
	 * Instantiate a painter.
	 * 
	 * @param control
	 *            the Controller object for Siafu
	 */
	public Painter(final Controller control) {
		this.control = control;
		this.world = control.getWorld();
		this.time = world.getTime();
		this.display = Display.getDefault();
		preloadPeopleImages();

		backgroundData = new ImageData(control.getSimulationData()
				.getBackgroundFile());

		prepareBackgrounds();
	}

	/**
	 * Create a set of darkened background for the night time of the simulation.
	 */
	private void prepareBackgrounds() {
		prepareSky();

		background = new Image[SUNSET_STEPS];
		Controller.getProgress().reportBackgroundCreationStart(SUNSET_STEPS);

		for (int i = 0; i < SUNSET_STEPS; i++) {
			Controller.getProgress().reportBackgroundCreated();
			background[i] = getDarkenedBackground(ONE_HUNDRED * i
					/ SUNSET_STEPS);
		}
		Controller.getProgress().reportBackgroundCreationEnd();
	}

	/**
	 * Create a blue image data that will be used as the sky color.
	 */
	private void prepareSky() {
		int width = backgroundData.width;
		int height = backgroundData.height;
		RGB[] colors = new RGB[] { new RGB(0, 0, FF), new RGB(0, 0, FF) };
		PaletteData palette = new PaletteData(colors);
		palette.isDirect = false;
		skyData = new ImageData(width, height, 1, palette);

		int[] blueRow = new int[width];
		for (int i = 0; i < blueRow.length; i++) {
			blueRow[i] = 1;
		}
		for (int i = 0; i < height; i++) {
			skyData.setPixels(0, i, width, blueRow, 0);
		}
	}

	/**
	 * Load the images for all the sprites that will be used in the simulation.
	 */
	private void preloadPeopleImages() {
		HashMap<String, InputStream> rawSprites = control.getSimulationData()
				.getSprites();
		for (String fileName : rawSprites.keySet()) {
			String spriteName = fileName.split("-")[0];

			// Have we processed another direction of this sprite?
			if (personImg.containsKey(spriteName)) {
				continue;
			}
			Sprite s;
			try {
				String[] offset = fileName.split("-")[1].split("\\.");
				s = new Sprite(spriteName, new Integer(offset[0]), new Integer(
						offset[1]));

				for (int dir = 0; dir < MAX_DIR; dir++) {
					s.setImage(dir, new Image(display, rawSprites
							.get(spriteName + "-" + s.getVOffset() + "."
									+ s.getHOffset() + "-" + dir)));
				}
			} catch (Exception e) {
				throw new RuntimeException("Can't read the sprites for "
						+ spriteName, e);
			}

			personImg.put(s.getName(), s);
		}
	}

	/**
	 * Generate an image of the original background, only darkened a certain
	 * percentage. 100 will render a completely dark blue background.
	 * 
	 * @param percent
	 *            the percentage of dark blue to add to the background
	 * @return the darkened Image
	 */
	private Image getDarkenedBackground(final int percent) {
		byte alfa;
		Image img;
		if (percent < 0) {
			alfa = 0;
		} else if (percent > ONE_HUNDRED) {
			alfa = ONE_HUNDRED;
		} else {
			alfa = (byte) (BG_ALPHA_STEP * (percent) / (double) ONE_HUNDRED);
		}

		if (!backgroundData.palette.isDirect) {
			backgroundData = makePaletteDirect(backgroundData);
		}
		img = new Image(Display.getCurrent(), backgroundData);

		skyData.alpha = alfa;
		GC imgGC = new GC(img);
		imgGC.drawImage(new Image(Display.getCurrent(), skyData), 0, 0);
		imgGC.dispose();

		return img;
	}

	/**
	 * Turn the background image into an image with a direct palette. By the
	 * way, PNG's can have indexed palettes, and Windows will keep that when you
	 * draw on it. Linux, on the other hand, will treat any PNG as direct
	 * palette (non=indexed). Since we are drawing overlays on top of the
	 * original image, we definitelly need more colors than the original image
	 * has.
	 * 
	 * @param originalData
	 *            the original picture, potentially with a direct palette
	 * @return a new ImageData where the pixel values correspond to RGB values,
	 *         instead of the original, indexed one. If the original palette was
	 *         already direct, the ImageData you get back is still a copy of the
	 *         original
	 */
	public ImageData makePaletteDirect(final ImageData originalData) {
		// Make a direct palette image
		PaletteData pd = new PaletteData(RED_MASK, GREEN_MASK, BLUE_MASK);
		ImageData baseData = new ImageData(originalData.width,
				originalData.height, COLOR_DEPTH, pd);
		Image base = new Image(Display.getCurrent(), baseData);

		// Draw our image on top to convert indexed pixel values into RGB
		// values
		GC baseGC = new GC(base);
		Image originalImg = new Image(Display.getCurrent(), originalData);
		baseGC.drawImage(originalImg, 0, 0);
		baseGC.dispose();

		// Get our fresh new direct palette imagedata
		ImageData converted = base.getImageData();

		// Clean up and return
		originalImg.dispose();
		base.dispose();

		return converted;
	}

	/**
	 * Choose how dark the background should be depending on the time of the
	 * day.
	 * 
	 * @return the index of the required background
	 */
	private int chooseDarknessLevel() {
		int hour = time.get(Calendar.HOUR_OF_DAY);
		int minute;

		if ((hour > SUNRISE) && (hour < SUNSET)) {
			return 0; // Day
		} else if ((hour < SUNRISE) || (hour > SUNSET)) {
			return ONE_HUNDRED; // Night
		} else {
			minute = time.get(Calendar.MINUTE);
			int darkness = (int) (DARKNESS_STEP * (minute / MINUTES_PER_HOUR));
			if (hour == SUNRISE) {
				return ONE_HUNDRED - darkness;
			} else {
				return darkness;
			}
		}
	}

	/**
	 * Draw the background image (darkened it it's late at night) or the
	 * background plus an overlay if the user is requesting it.
	 * 
	 * @param gc
	 *            the GC on which to draw
	 */
	public synchronized void paintBackground(final GC gc) {
		if (overlayImg == null) {
			int bgIndex = 0;
			if (control.getGUI().isNightSimulated()) {
				double darkness = chooseDarknessLevel() / (double) ONE_HUNDRED;
				bgIndex = (int) ((SUNSET_STEPS - 1) * (darkness));
			}
			gc.drawImage(background[bgIndex], 0, 0);
		} else {
			gc.drawImage(overlayImg, 0, 0);
		}
	}

	/**
	 * Get the sprite for a particular agent. This is derived from the agent's
	 * image.
	 * 
	 * @param a
	 *            the agent whose sprite we need
	 * @return the agent's sprite
	 */
	public Sprite getAgentSprite(final Agent a) {
		return personImg.get(a.getImage());
	}

	/**
	 * Paint the agents in the simulation.
	 * 
	 * @param gc
	 *            the GC on which to draw
	 */
	public void paintPeople(final GC gc) {
		// Put into a treeset to guarantee the order for the z layer
		Iterator<Agent> it = new TreeSet<Agent>(world.getPeople()).iterator();

		if (control.getGUI().isPathShown()) {
			paintPath(gc);
		}

		while (it.hasNext()) {
			Agent a = it.next();

			if (a.isVisible()) {

				Sprite s = personImg.get(a.getImage());

				if (s == null) {
					throw new RuntimeException("Unknown sprite " + a.getImage());
				}
				gc.drawImage(s.getImage(a.getDir()), a.getPos().getCol()
						- (s.getHOffset()), a.getPos().getRow()
						- s.getVOffset());
			}
		}
	}

	/**
	 * Draw the path that an agent is following in orer to reach its
	 * destination.
	 * 
	 * @param gc
	 *            the GC on which to draw
	 * 
	 */
	public void paintPath(final GC gc) {
		Trackable t = control.getGUI().getActive();
		if (t instanceof Agent) {
			Agent a = (Agent) t;
			if (a.getDestination() != null) {
				gc.setForeground(display.getSystemColor(SWT.COLOR_RED));
				// We use a temporary agent that "walks the walk"
				Agent path = new Agent(a.getPos(), "HumanBlue", world);
				path.setDir(a.getDir());
				path.setSpeed(2);
				path.setDestination(a.getDestination());
				while (!path.isAtDestination()) {
					Position pos = path.getPos();
					gc.drawPoint(pos.getCol(), pos.getRow());
					path.moveTowardsDestination();
				}
			}
		}
	}

	/**
	 * Get the bounds of the background.
	 * 
	 * @return a Rectangle with the bounds.
	 */
	public Rectangle getBounds() {
		return new Rectangle(0, 0, backgroundData.width, backgroundData.height);
	}

	/**
	 * Ask all the markers to draw themselves with the given GC.
	 * 
	 * @param gc
	 *            the GC on which to draw
	 * @param markers
	 *            the Markers object with all of the simulation's markers
	 */
	public void paintMarkers(final GC gc, final Markers markers) {
		for (Type t : Type.values()) {
			for (Marker m : t.getMarkers()) {
				m.draw(gc);
			}
		}
	}

	/**
	 * Called by the controller, this method draws the overlay on top of the
	 * background, and keep the image ready to be painted on the canvas instead
	 * of the background.
	 * 
	 * @param ov
	 *            the overlay that needs drawing
	 */
	public synchronized void requestOverlayDrawing(final Overlayable ov) {
		overlayImg = createOverlayImg(ov);
	}

	/**
	 * Destroy the overlay+background image. The painter will now resume drawing
	 * the background.
	 * 
	 */
	public synchronized void cancelOverlayDrawing() {
		overlayImg.dispose();
		overlayImg = null;

	}

	/**
	 * Create an overlay image by drawing the overlay itself on the background,
	 * with a semi transparent alpha value.
	 * 
	 * @param ov
	 *            the overlay to be drawn
	 * @return the overlay+background image
	 */
	private Image createOverlayImg(final Overlayable ov) {
		Image img;

		/*
		 * In the case of places, We choose a palette that only uses some of the
		 * lower bits (0x3FC) of the distance integer. This creates a cyclical
		 * gradient, which is easier to follow than a very smooth and large one.
		 * By setting the mask for R, G and B to the same value, we obtain gray
		 * overlays.
		 */
		PaletteData palette;
		int[][] values;

		if (ov instanceof Place) {
			palette = new PaletteData(PLACE_PALETTE_MASK, PLACE_PALETTE_MASK,
					PLACE_PALETTE_MASK);
			values = ((Place) ov).getGradient().getDistances();
		} else if (ov instanceof Overlay) {
			palette = new PaletteData(RED_MASK, GREEN_MASK, BLUE_MASK);
			values = ((Overlay) ov).getValueMatrix();
		} else {
			throw new NotImplementedException(
					"Can't draw overlayables of this type");
		}

		ImageData ovLayer = new ImageData(backgroundData.width,
				backgroundData.height, COLOR_DEPTH, palette);

		for (int i = 0; i < values.length; i++) {
			ovLayer.setPixels(0, i, values[0].length, values[i], 0);
		}
		Image ovLayerImg = new Image(display, ovLayer);

		img = new Image(display, backgroundData);
		GC imgGC = new GC(img);
		imgGC.setAlpha(OVERLAY_ALPHA);
		imgGC.drawImage(ovLayerImg, 0, 0);
		return img;

	}

	/**
	 * Destroy all SWT allocated resources.
	 * 
	 */
	public void disposeResources() {
		if (overlayImg != null) {
			overlayImg.dispose();
		}
		for (Sprite sprite : personImg.values()) {
			sprite.disposeResources();
		}
		for (Image img : background) {
			img.dispose();
		}
	}
}
