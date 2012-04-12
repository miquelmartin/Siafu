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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import de.nec.nle.siafu.graphics.GUI;
import de.nec.nle.siafu.model.BinaryOverlay;
import de.nec.nle.siafu.model.DiscreteOverlay;
import de.nec.nle.siafu.model.Overlay;
import de.nec.nle.siafu.model.RealOverlay;

/**
 * The status item for overlays. This displays a gradient from white to black,
 * and maps the values of the overlay to it. Note that this is a color
 * gradient, totally unrelated to the distance gradients used for agent
 * routing.
 * 
 * @author Miquel Martin
 * 
 */
public class OverlayStatus extends BaseStatus {

	/** The indent for the threshold tags. */
	private static final int TAG_INDENT = 10;

	/** The height of the black to white gradient. */
	private static final int RANGE_HEIGHT = 200;

	/** The width of the black to white gradient. */
	private static final int RANGE_WIDTH = 90;

	/** SWT's display. */
	private static final Display DISPLAY = Display.getCurrent();

	/** The gray color. */
	private static final Color GRAY =
			DISPLAY.getSystemColor(SWT.COLOR_GRAY);

	/** The black color. */
	private static final Color BLACK =
			DISPLAY.getSystemColor(SWT.COLOR_BLACK);

	/** The width of the gradient image. */
	private static final int GRADIENT_WIDTH = 30;

	/** The height of the gradient image. */
	private static final int GRADIENT_HEIGHT = 100;

	/** The width of the canvas on which the gradient and scale is drawn. */
	private static final int CANVAS_WIDTH = 200;

	/** The padding of the canvas on which the gradient and scale is drawn. */
	private static final int CANVAS_PADDING = 20;

	/** The height of the canvas on which the gradient and scale is drawn. */
	private static final int CANVAS_HEIGHT =
			2 * CANVAS_PADDING + GRADIENT_HEIGHT;

	/**
	 * Two to the power of 24. Also known as the maximum pixel value, or even
	 * white
	 */
	private static final int TWO_POWER_24 = 16777216;

	/** The button that lets the user show an overlay on the canvas. */
	private Button showOverlayButton;

	/** The canvas where we show the gradient for the overlay. */
	private Canvas overlayRangeCanvas;

	/**
	 * Create a status item for the given overlay.
	 * 
	 * @param parent the parent composite
	 * @param overlay the overlay to show
	 * @param icon the icon for the overlay, depending on its type
	 * @param gui the container gui
	 * @param overlaysPanel the panel in which this status item must be
	 *            placed.
	 */
	public OverlayStatus(final Composite parent, final Overlay overlay,
			final Image icon, final GUI gui,
			final OverlaysPanel overlaysPanel) {
		super(parent, gui, overlaysPanel, overlay, overlay.getName(), icon);
	}

	/**
	 * At the body elements for the status item, in this case, a ranged
	 * gradient and a display button.
	 * 
	 * @param parent the parent composite
	 */
	protected void addBodyElements(final Composite parent) {
		GridData gdOverlayRangeCanbas =
				new GridData(CANVAS_WIDTH, CANVAS_HEIGHT);
		overlayRangeCanvas =
				new Canvas(parent, SWT.NORMAL | SWT.NO_BACKGROUND);
		overlayRangeCanvas.setSize(RANGE_WIDTH, RANGE_HEIGHT);
		overlayRangeCanvas.setLayoutData(gdOverlayRangeCanbas);
		overlayRangeCanvas.addPaintListener(new PaintListener() {

			public void paintControl(final PaintEvent e) {
				drawOverlayPicture(e);
			}
		});

		addShowOnMapButton(parent);
	}

	/**
	 * Draw a black to white gradient and map the overlay values on it.
	 * 
	 * @param e the paint event that triggered the redraw
	 */
	private void drawOverlayPicture(final PaintEvent e) {
		Display display = Display.getCurrent();

		Color white = display.getSystemColor(SWT.COLOR_WHITE);
		Color black = display.getSystemColor(SWT.COLOR_BLACK);

		Overlay ov = (Overlay) content;

		Image imgAux = new Image(display, CANVAS_WIDTH, CANVAS_HEIGHT);
		GC gcAux = new GC(imgAux);
		gcAux.setBackground(overlayRangeCanvas.getBackground());
		gcAux.fillRectangle(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
		gcAux.setForeground(white);
		gcAux.setBackground(black);
		gcAux.fillGradientRectangle(0, CANVAS_PADDING, GRADIENT_WIDTH,
			GRADIENT_HEIGHT, true);

		gcAux.setForeground(black);
		gcAux.drawRectangle(0, CANVAS_PADDING, GRADIENT_WIDTH,
			GRADIENT_HEIGHT);

		if (ov instanceof BinaryOverlay) {
			int[] thresholds =
					new int[] {((BinaryOverlay) ov).getThreshold()};
			String[] tags = new String[] {"True", "False"};
			drawMarks(thresholds, tags, gcAux);
		} else if (ov instanceof DiscreteOverlay) {
			int[] thresholds = ((DiscreteOverlay) ov).getThresholds();
			String[] tags = ((DiscreteOverlay) ov).getTags();
			int[] thresholdsAux = new int[thresholds.length - 1];
			for (int i = 0; i < thresholdsAux.length; i++) {
				thresholdsAux[i] = thresholds[i];
			}
			drawMarks(thresholdsAux, tags, gcAux);
		} else if (ov instanceof RealOverlay) {
			int[] thresholds = new int[] {TWO_POWER_24, 0};
			String[] tags = new String[] {"Real Overlay"};
			drawMarks(thresholds, tags, gcAux);
		}

		gcAux.dispose();
		e.gc.drawImage(imgAux, 0, 0);
		imgAux.dispose();
	}

	/**
	 * Draw the threshold marks on the black to white gradient.
	 * 
	 * @param thresholds the overlay thresholds
	 * @param tags the tag assigned to each of the thresholds
	 * @param gcAux the GC to draw with
	 */
	private void drawMarks(final int[] thresholds, final String[] tags,
			final GC gcAux) {
		int[] separators = new int[thresholds.length + 2];

		separators[thresholds.length + 2 - 1] = 0;
		separators[0] = CANVAS_HEIGHT;

		gcAux.setLineWidth(2);
		gcAux.setForeground(GRAY);
		for (int i = 0; i < thresholds.length; i++) {
			int percent =
					(int) (1d * (GRADIENT_HEIGHT * thresholds[i]))
							/ TWO_POWER_24;
			separators[i + 1] = CANVAS_PADDING + GRADIENT_HEIGHT - percent;
			gcAux.drawLine(0, separators[i + 1], CANVAS_WIDTH,
				separators[i + 1]);
		}

		gcAux.setForeground(BLACK);

		for (int i = 0; i < tags.length; i++) {
			gcAux.drawText(tags[i], GRADIENT_WIDTH + TAG_INDENT,
				separators[i + 1] + (separators[i] - separators[i + 1])
						/ 2 - gcAux.stringExtent(tags[i]).y / 2, true);
		}
	}

	/**
	 * Add the button to show the overlay on the canvas.
	 * 
	 * @param parent the parent composite
	 */
	private void addShowOnMapButton(final Composite parent) {
		showOverlayButton = new Button(parent, SWT.PUSH);
		showOverlayButton.setText("Click'n hold to show overlay");
		showOverlayButton
				.setToolTipText("Hold to show the overlay\non the map");
		showOverlayButton.setLayoutData(new GridData(
				GridData.GRAB_HORIZONTAL));
		showOverlayButton.addMouseListener(new MouseAdapter() {

			public void mouseDown(final MouseEvent e) {
				gui.requestOverlayDrawing((Overlay) content);
			}

			public void mouseUp(final MouseEvent e) {
				gui.cancelOverlayDrawing();
			}
		});
	}

	/**
	 * What to do if the color of the color picker changes. In this case, do
	 * nothing. We don't even have a color picker.
	 * 
	 * @param newColor the impossibly chosen color
	 */
	protected void onColorChanged(final RGB newColor) {
	}

	/** Refresh the status item. */
	public void refresh() {
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
