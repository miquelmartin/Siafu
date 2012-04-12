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
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import de.nec.nle.siafu.graphics.ColorTools;
import de.nec.nle.siafu.graphics.GUI;
import de.nec.nle.siafu.graphics.Markers;
import de.nec.nle.siafu.graphics.markers.Marker;
import de.nec.nle.siafu.graphics.markers.StickMarker;
import de.nec.nle.siafu.model.Overlay;
import de.nec.nle.siafu.model.Place;
import de.nec.nle.siafu.types.Publishable;

/**
 * The status item that displays information on a Place.
 * 
 * @author Miquel Martin
 * 
 */
public class PlaceStatus extends BaseStatus {

	/** The labels for the overlay value at the place's position. */
	private LabelPair[] ovLabels;

	/** The labels for the place info field. */
	private LabelPair[] infoLabels;

	/** The labels for the place intrinsec information. */
	private LabelPair[] placeLabels;

	/** Button to show the overlay as a, well, an overlay on the map window. */
	private Button showOverlayButton;

	/** The list of overlays. */
	private Collection<Overlay> overlays;

	/** The composite for the place info. */
	private Composite placeComposite;

	/** The composite for the overlay info. */
	private Composite overlayComposite;

	/** The composite for the place info field. */
	private Composite infoComposite;

	/** The Marker associated to this panel. */
	protected Marker marker;

	/**
	 * Create a placestatus using a string to describe the color of the
	 * marker.
	 * 
	 * @param parent the parent composite
	 * @param place the place to display information about
	 * @param icon the icon to show next to the place
	 * @param colorString a string of the form "#RRBBGG"
	 * @param gui the container gui
	 * @param placesPanel the panel that contains all the status items
	 */
	public PlaceStatus(final Composite parent, final Place place,
			final Image icon, final String colorString, final GUI gui,
			final PlacesPanel placesPanel) {
		this(parent, place, icon, ColorTools
				.parseColorString(ColorTools.COLOR_PLACE), gui,
				placesPanel);
	}

	/**
	 * Create a place status item to display place information.
	 * 
	 * @param parent the parent composite
	 * @param place the place to display information about
	 * @param icon the icon to show next to the place
	 * @param rgbColor the color of the marker
	 * @param gui the container gui
	 * @param placesPanel the panel that contains all the status items
	 */
	public PlaceStatus(final Composite parent, final Place place,
			final Image icon, final RGB rgbColor, final GUI gui,
			final PlacesPanel placesPanel) {
		super(parent, gui, placesPanel, place, place.getName(), icon,
				ColorTools.parseColorString(ColorTools.COLOR_PLACE));
		marker =
				new StickMarker(place, ColorTools.parseRGBColor(rgbColor));
		gui.getMarkers().addMarker(marker, Markers.Type.INTERNAL);
	}

	/**
	 * Add the info parts for this status.
	 * 
	 * @param parent the parent composite
	 */
	protected void addBodyElements(final Composite parent) {
		placeComposite = new Composite(parent, SWT.NORMAL);
		GridLayout glPlaceComposite = new GridLayout();
		glPlaceComposite.marginWidth = 0;
		placeComposite.setLayout(glPlaceComposite);

		infoComposite = new Composite(parent, SWT.NORMAL);
		GridLayout glInfoComposite = new GridLayout();
		glInfoComposite.marginWidth = 0;
		infoComposite.setLayout(glInfoComposite);

		overlayComposite = new Composite(parent, SWT.NORMAL);
		GridLayout glOverlayComposite = new GridLayout();
		glOverlayComposite.marginWidth = 0;
		overlayComposite.setLayout(glOverlayComposite);

		createPlaceLabels(placeComposite);
		createOverlayLabels(overlayComposite);

		addShowOnMapButton(parent);

	}

	/**
	 * Add the button to show the place gradient overlay.
	 * 
	 * @param parent the parent composite
	 */
	private void addShowOnMapButton(final Composite parent) {
		showOverlayButton = new Button(parent, SWT.PUSH);
		showOverlayButton.setText("Click'n hold to show gradient");
		showOverlayButton
				.setToolTipText("Hold to show the place's\ndistance "
						+ "gradient\non the map");
		showOverlayButton.setLayoutData(new GridData(
				GridData.GRAB_HORIZONTAL));
		showOverlayButton.addMouseListener(new MouseAdapter() {

			public void mouseDown(final MouseEvent e) {
				gui.requestOverlayDrawing((Place) content);
			}

			public void mouseUp(final MouseEvent e) {
				gui.cancelOverlayDrawing();
			}

		});
	}

	/**
	 * Update the color of the marker.
	 * 
	 * @param newColor the new color for the marker
	 */
	protected void onColorChanged(final RGB newColor) {
		marker.setRGB(newColor);
	}

	/**
	 * Create the place intrinsec labels.
	 * 
	 * @param parent the parent composite
	 */
	private void createPlaceLabels(final Composite parent) {
		placeLabels = new LabelPair[2];
		placeLabels[0] = new LabelPair(parent, SWT.NONE, "Position");
		placeLabels[1] = new LabelPair(parent, SWT.NONE, "Type");

		Place place = (Place) content;
		placeLabels[0].setValue(place.getPos().getPrettyCoordinates());
		placeLabels[1].setValue(place.getType());
	}

	/**
	 * Create the labels that show the overlay values for the place's
	 * position.
	 * 
	 * @param parent the parent composite
	 */
	private void createOverlayLabels(final Composite parent) {
		overlays = gui.getOverlays();
		ovLabels = new LabelPair[overlays.size()];

		Iterator<Overlay> ovIt = overlays.iterator();
		int i = 0;

		while (ovIt.hasNext()) {
			Overlay ov = (Overlay) ovIt.next();
			ovLabels[i++] = new LabelPair(parent, SWT.NONE, ov.getName());
		}
	}

	/**
	 * Create the labels and values for the info field in the place.
	 * 
	 * @param parent the parent composite
	 */
	private void createInfoLabelsAndValues(final Composite parent) {
		Place place = (Place) content;

		if (infoLabels != null) {
			for (LabelPair lp : infoLabels) {
				lp.dispose();
			}
		}
		Set<String> infoSet = ((Place) content).getInfoKeys();

		if (infoSet.size() != 0) {
			Iterator<String> it = infoSet.iterator();
			infoLabels = new LabelPair[infoSet.size()];

			for (int i = 0; i < infoSet.size(); i++) {
				infoLabels[i] = new LabelPair(parent, SWT.NONE, it.next());
			}

			Collection<Publishable> values = place.getInfoValues();
			Iterator<Publishable> iterator = values.iterator();

			for (int i = 0; i < values.size(); i++) {
				Object o = iterator.next();
				String value = (o != null) ? o.toString() : "null";
				infoLabels[i].setValue(value);
			}
		} else {
			infoLabels = new LabelPair[1];

			infoLabels[0] =
					new LabelPair(parent, SWT.NONE, "Info", "None");
		}
	}

	/** Refresh the overlay labels. */
	private void refreshOvLabels() {
		Place place = (Place) content;
		Iterator<Overlay> ovIt = overlays.iterator();
		int i = 0;

		while (ovIt.hasNext()) {
			Overlay ov = (Overlay) ovIt.next();
			ovLabels[i++].setValue(ov.getValue(place.getPos()).toString());
		}
	}

	/** Refresh the status item. */
	public void refresh() {
		refreshOvLabels();
		createInfoLabelsAndValues(infoComposite);
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
