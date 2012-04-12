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

import org.apache.commons.lang.NotImplementedException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import de.nec.nle.siafu.graphics.GUI;
import de.nec.nle.siafu.model.BinaryOverlay;
import de.nec.nle.siafu.model.DiscreteOverlay;
import de.nec.nle.siafu.model.Overlay;
import de.nec.nle.siafu.model.RealOverlay;

/**
 * This is the panel that contains the overlays in the simulation.
 * 
 * @author Miquel Martin
 * 
 */
public class OverlaysPanel extends BasePanel {

	/**
	 * Whether the icons have already been loaded and are available.
	 */
	private boolean iconsLoaded;

	/** The icon for binary overlays. */
	private Image binaryOverlayIcon;

	/** The icon for discrete overlays. */
	private Image discreteOverlayIcon;

	/** The icon for real overlays. */
	private Image realOverlayIcon;

	/**
	 * Create the overlays panel.
	 * 
	 * @param gui the container gui
	 * @param parent the parent composite
	 * @param style the parent's style
	 */
	public OverlaysPanel(final GUI gui, final Composite parent,
			final int style) {
		super(gui, parent, style);
	}

	/**
	 * Get the right icon for this overlay type.
	 * 
	 * @param ov the overlay for which an icon is needed
	 * @return the right icon as an Image
	 */
	private Image getIcon(final Overlay ov) {
		if (!iconsLoaded) {
			binaryOverlayIcon =
					new Image(Display.getCurrent(), getClass()
							.getResourceAsStream(
								"/res/misc/binaryOverlayIcon.png"));
			discreteOverlayIcon =
					new Image(Display.getCurrent(), getClass()
							.getResourceAsStream(
								"/res/misc/discreteOverlayIcon.png"));
			realOverlayIcon =
					new Image(Display.getCurrent(), getClass()
							.getResourceAsStream(
								"/res/misc/realOverlayIcon.png"));
			iconsLoaded = true;

		}
		if (ov instanceof BinaryOverlay) {
			return binaryOverlayIcon;
		} else if (ov instanceof DiscreteOverlay) {
			return discreteOverlayIcon;

		} else if (ov instanceof RealOverlay) {
			return realOverlayIcon;
		}
		throw new NotImplementedException(
				"You tried to get the icon of an unknown overlay type");
	}

	/**
	 * Add the overlays to the combo box of the panel.
	 */
	@Override
	protected void addSelectableElements() {
		String[] items = new String[gui.getOverlays().size()];

		int i = 0;
		for (Overlay ov : gui.getOverlays()) {
			items[i] = ov.getName();
			i++;
		}

		Arrays.sort(items);
		selectionCombo.setItems(items);
	}

	/**
	 * Create a status item for this overlay, given by o.
	 * 
	 * @param parent the parent composite
	 * @param o the overlay we want to display
	 * @return the status item for the overlay
	 */
	@Override
	protected BaseStatus createStatusItem(final Composite parent,
			final Object o) {
		Overlay ov = (Overlay) o;
		return new OverlayStatus(parent, ov, getIcon(ov), gui, this);
	}

	/**
	 * Destroy the status item given by baseStatus. This is not needed for
	 * overlays.
	 * 
	 * @param baseStatus the status to display.
	 */
	@Override
	protected void destroyStatusItem(final BaseStatus baseStatus) {
	}

	/**
	 * Set the chosen overlay as the active one, so that the GUI can display
	 * it when we press the show overlay button.
	 * 
	 * @param selection the name of the selected overlay
	 */
	@Override
	protected void onSelectionMade(final String selection) {
		Overlay overlay = null;
		for (Overlay ov : gui.getOverlays()) {
			if (selection.equals(ov.getName())) {
				overlay = ov;
				break;
			}
		}
		if (overlay == null) {
			throw new RuntimeException(
					"You chose a non existing overlay. How did u mange??");
		}
		gui.getControlPanel().setSelected(overlay);

	}

	/**
	 * Clean up those resources, specially those that won't be disposed when
	 * the conainer composite is disposed.
	 * 
	 */
	protected void disposeResources() {
		super.disposeResources();
		binaryOverlayIcon.dispose();
		discreteOverlayIcon.dispose();
		realOverlayIcon.dispose();
	}
}
