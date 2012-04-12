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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import de.nec.nle.siafu.graphics.GUI;

/**
 * The base status item implementation. All other status items must extend
 * this.
 * 
 * @author Miquel Martin
 * 
 */
public abstract class BaseStatus extends Composite {
	/** Left margin for the body composite. */
	private static final int BODY_LEFT_MARGIN = 10;

	/** Height of the color picker image. */
	private static final int PICKER_IMG_HEIGHT = 6;

	/** Width of the color picker image. */
	private static final int PICKER_IMG_WIDTH = 10;

	/** Number of columns in the header. */
	private static final int DEFAULT_HEADER_COLUMNS = 4;

	/** Size of the caption font on the header. */
	private static final int HEADER_FONT_SIZE = 12;

	/** The font for the name displayed as status item header. */
	private static Font nameFont;

	/** SWT's display. */
	private Display display = Display.getCurrent();

	/** The image for the clsoe button. */
	private static Image closeImg;

	/** A reference to the status item. */
	private BaseStatus thisStatusBox;

	/** The panel where the satus item is embeded. */
	private BasePanel panel;

	/** The label with the status item name. */
	private Label nameLabel;

	/** The label with the icon shown next to the name. */
	private Label iconLabel;

	/** The color of the color picker. */
	private Color colorPickerColor;

	/** The image showing the currently chosen color picker color. */
	private Image colorPickerImage;

	/** The label with the color picker image. */
	private Label colorPickerButton;

	/** The header of the status item. */
	private Composite header;

	/** The info part of the status item. */
	private Composite body;

	/** The color of the marker. */
	private RGB color;

	/** The container GUI. */
	protected GUI gui;

	/** The element being shown in this status item. */
	protected Object content;

	/**
	 * Crate a base panel. This will be called by the constructor of the
	 * inheriting class.
	 * 
	 * @param parent the parent composite
	 * @param gui the container GUI
	 * @param panel the panel where this status item belongs
	 * @param content the element being shown
	 * @param caption the caption on top of the status item.
	 * @param icon the icon shown next to the caption.
	 */
	protected BaseStatus(final Composite parent, final GUI gui,
			final BasePanel panel, final Object content,
			final String caption, final Image icon) {
		this(parent, gui, panel, content, caption, icon, null);
	}

	/**
	 * Create a base panel, but specify the color of the marker.
	 * 
	 * @param parent the parent composite
	 * @param gui the container GUI
	 * @param panel the panel where this status item belongs
	 * @param content the element being shown
	 * @param caption the caption on top of the status item.
	 * @param icon the icon shown next to the caption.
	 * @param color the color of the marker.
	 */
	protected BaseStatus(final Composite parent, final GUI gui,
			final BasePanel panel, final Object content,
			final String caption, final Image icon, final RGB color) {
		super(parent, SWT.NORMAL);
		this.panel = panel;
		this.color = color;
		this.gui = gui;
		this.content = content;

		this
				.setLayoutData(new GridData(SWT.FILL, SWT.NORMAL, true,
						false));

		if (nameFont == null) {
			nameFont =
					new Font(display, "default", HEADER_FONT_SIZE,
							SWT.BOLD);
		}
		thisStatusBox = this;

		this.setLayout(new GridLayout(1, false));

		createHeader(caption, icon);
		createBody();

		refresh();
		parent.pack();

	}

	/**
	 * Create the satus item header.
	 * 
	 * @param name the name to display as caption
	 * @param icon the icon to show next to the caption
	 */
	protected void createHeader(final String name, final Image icon) {
		header = new Composite(this, SWT.NONE);
		header.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
				false));

		int headerColumns = DEFAULT_HEADER_COLUMNS;
		if (color != null) {
			headerColumns = DEFAULT_HEADER_COLUMNS + 1;
		}
		header.setLayout(new GridLayout(headerColumns, false));
		iconLabel = new Label(header, SWT.NONE);
		iconLabel.setImage(icon);
		nameLabel = new Label(header, SWT.NONE);
		nameLabel.setText(name);
		nameLabel.setFont(nameFont);
		if (color != null) {
			createColorPicker(header);
		}

		createCloseButton(header);
	}

	/**
	 * Create the image that shows the chosen color for the Marker.
	 */
	private void createColorPickerImage() {
		if (colorPickerImage != null && colorPickerColor != null) {
			colorPickerImage.dispose();
			colorPickerColor.dispose();
		}

		colorPickerColor = new Color(Display.getCurrent(), color);
		colorPickerImage =
				new Image(Display.getCurrent(), PICKER_IMG_WIDTH,
						PICKER_IMG_HEIGHT);
		GC gcImg = new GC(colorPickerImage);
		gcImg.setBackground(colorPickerColor);
		gcImg.fillRectangle(colorPickerImage.getBounds());
		gcImg.dispose();
	}

	/**
	 * Create the color picker to choose the marker color.
	 * 
	 * @param parent the parent composite
	 */
	private void createColorPicker(final Composite parent) {
		createColorPickerImage();
		colorPickerButton = new Label(parent, SWT.NONE);
		colorPickerButton.setImage(colorPickerImage);
		colorPickerButton.addMouseListener(new MouseAdapter() {
			public void mouseUp(final MouseEvent e) {
				ColorDialog cd =
						new ColorDialog(colorPickerButton.getShell());
				cd.setText("Choose the marker color");
				cd.setRGB(color);
				color = cd.open();
				if (color != null) {
					onColorChanged(color);
					createColorPickerImage();
					colorPickerButton.setImage(colorPickerImage);
				}
			}
		});
	}

	/**
	 * Method to execute when a new color is chosen.
	 * 
	 * @param newColor the new chosen color
	 */
	protected abstract void onColorChanged(final RGB newColor);

	/**
	 * Create the status item body.
	 * 
	 */
	protected void createBody() {
		body = new Composite(this, SWT.NONE);
		GridLayout glBody = new GridLayout(1, false);
		glBody.marginLeft = BODY_LEFT_MARGIN;
		body.setLayout(glBody);
		addBodyElements(body);

	}

	/**
	 * Method to call in order to add the elements of the status item's body.
	 * 
	 * @param parent the parent composite
	 */
	protected abstract void addBodyElements(final Composite parent);

	/**
	 * The button to remove this status item.
	 * 
	 * @param parent the parent composite
	 */
	private void createCloseButton(final Composite parent) {
		if (closeImg == null) {
			closeImg =
					new Image(Display.getCurrent(), getClass()
							.getResourceAsStream("/res/misc/close.png"));
		}
		Label close = new Label(parent, SWT.NONE);
		close.setImage(closeImg);
		close.setToolTipText("Hide this agent");
		close.setLayoutData(new GridData(SWT.END, SWT.BEGINNING, true,
				false));
		close.addMouseListener(new MouseAdapter() {
			public void mouseUp(final MouseEvent e) {
				panel.removeStatusItem(thisStatusBox);
			}
		});

	}

	/**
	 * Get the element being displayed on this status item.
	 * @return the element being displayed
	 */
	public Object getContent() {
		return content;
	}

	/**
	 * Refresh the status item.
	 */
	public abstract void refresh();

	/**
	 * Clean up those resources, specially those that won't be disposed when
	 * the conainer composite is disposed.
	 * 
	 */
	public void disposeResources() {
		// Dispose orphan resources
		colorPickerColor.dispose();
		colorPickerImage.dispose();

		// Remove the parent
		this.dispose();
	}
}
