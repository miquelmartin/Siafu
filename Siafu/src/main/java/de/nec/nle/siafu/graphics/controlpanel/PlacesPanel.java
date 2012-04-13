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
import java.util.HashMap;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import de.nec.nle.siafu.graphics.GUI;
import de.nec.nle.siafu.graphics.Markers;
import de.nec.nle.siafu.model.Place;
import de.nec.nle.siafu.model.Trackable;

/**
 * This class defines the panel that displays the places in the GUI.
 * 
 * @author Miquel Martin
 * 
 */

public class PlacesPanel extends BasePanel {

	/** The maximum Hue value. */
	private static final float HUE_RANGE = 360f;

	/** The brigthness of the color for each place type. */
	private static final float TYPE_COLOR_BRIGHTNESS = 0.75f;

	/** The saturation of the color for each place type. */
	private static final float TYPE_COLOR_SATURATION = 0.65f;

	/** The colors associated to each type. */
	private HashMap<String, Color> placeTypeColors;

	/** The little icon for each place type. */
	private HashMap<String, Image> placeTypeImg;

	/**
	 * Create the places panel.
	 * 
	 * @param gui the container gui
	 * @param parent the parent coposite
	 * @param style the parent composite's style
	 */
	public PlacesPanel(final GUI gui, final Composite parent,
			final int style) {
		super(gui, parent, style);

	}

	/**
	 * Add all the places to the combo box. The types are put in as is, and
	 * each place is indented with two spaces.
	 */
	@Override
	protected void addSelectableElements() {
		placeTypeColors = createPlaceTypeColors();
		placeTypeImg = createPlaceTypeImages();

		for (String t : gui.getPlaceTypes()) {
			selectionCombo.add(t);

			String[] itemsOfType =
					new String[gui.getPlacesOfType(t).size()];
			int i = 0;
			for (Place p : gui.getPlacesOfType(t)) {
				itemsOfType[i] = p.getName();
				i++;
			}
			Arrays.sort(itemsOfType);

			for (String item : itemsOfType) {
				selectionCombo.add("  " + item);
			}

		}
	}

	/**
	 * Choose the colors for the types. We go for constant Saturation and
	 * Brightness, and cycle through the possible hues.
	 * 
	 * @return a HashMap with the color for each of the place types
	 */
	private HashMap<String, Color> createPlaceTypeColors() {
		Set<String> types = gui.getPlaceTypes();
		int size = types.size();
		HashMap<String, Color> colors = new HashMap<String, Color>(size);

		float hue = 0;
		for (String t : types) {
			colors.put(t, new Color(Display.getCurrent(), new RGB(hue,
					TYPE_COLOR_SATURATION, TYPE_COLOR_BRIGHTNESS)));
			hue += HUE_RANGE / (1 + size);
		}
		return colors;
	}

	/**
	 * Create a bullet mark of the appropriate color for each plac type.
	 * @return a HashMap with the place types and their images.
	 */
	private HashMap<String, Image> createPlaceTypeImages() {
		Set<String> types = gui.getPlaceTypes();
		HashMap<String, Image> imgMap =
				new HashMap<String, Image>(types.size());
		for (String t : types) {
			Image img =
					new Image(Display.getCurrent(), getClass()
							.getResourceAsStream(
								"/misc/placeTypeBaseBig.png"));
			GC gc = new GC(img);
			gc.setAntialias(SWT.ON);
			gc.setBackground(placeTypeColors.get(t));
			// The PNG provides the alpha mask
			gc.fillRectangle(img.getBounds());
			gc.dispose();
			imgMap.put(t, img);
		}
		return imgMap;
	}

	/**
	 * Create the status item and mark the place on the canvas.
	 * 
	 * @param parent the parent composite
	 * @param o the place to create the status item for
	 * @return the new status item
	 */
	@Override
	protected BaseStatus createStatusItem(final Composite parent,
			final Object o) {
		Place p = (Place) o;
		Image icon = placeTypeImg.get(p.getType());
		RGB rgb = placeTypeColors.get(p.getType()).getRGB();
		return new PlaceStatus(parent, p, icon, rgb, gui, this);
	}

	/**
	 * Destroy this status item, which involves removing the marks associated
	 * to it.
	 * 
	 * @param baseStatus the status item to destroy
	 */
	@Override
	protected void destroyStatusItem(final BaseStatus baseStatus) {
		gui.getMarkers().removeMarker((Trackable) baseStatus.getContent(),
			Markers.Type.INTERNAL);
	}

	/**
	 * Select the place.
	 * 
	 * @param selection the selected place
	 */
	@Override
	protected void onSelectionMade(final String selection) {
		Place place = null;
		// Place names have two space in the combo box
		String name = selection.substring(2);
		for (Place p : gui.getPlaces()) {
			if (name.equals(p.getName())) {
				place = p;
				break;
			}
		}
		if (place == null) {
			// You hit one of the place type titles
			return;
		}
		gui.getControlPanel().setSelected(place);

	}

	/**
	 * Clean up those resources, specially those that won't be disposed when
	 * the conainer composite is disposed.
	 * 
	 */
	protected void disposeResources() {
		super.disposeResources();
		for (Color c : placeTypeColors.values()) {
			c.dispose();
		}
		for (Image i : placeTypeImg.values()) {
			i.dispose();
		}
	}
}
