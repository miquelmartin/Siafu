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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

/**
 * A pair of labels, the first, intended for the caption or key, in bold, and
 * the second, for the value, in normal font.
 * 
 * @author Miquel Martin
 * 
 */
public class LabelPair extends Composite {
	/** Margin for the label pair. */
	private static final int LABEL_MARGIN_HEIGHT = -3;

	/** The size of the label fonts. */
	private static final int FONT_SIZE = 10;

	/** The bold font. */
	private static Font boldFont =
			new Font(Display.getDefault(), "Arial", FONT_SIZE, SWT.BOLD);

	/** The label for the caption. */
	private Label captionLabel;

	/** The label for the label. */
	private Label valueLabel;

	/**
	 * Crate a new label pair.
	 * 
	 * @param parent the parent coposite
	 * @param style the parent composite's style
	 * @param caption the caption or key text
	 */
	public LabelPair(final Composite parent, final int style,
			final String caption) {
		this(parent, style, caption, "");
	}

	/**
	 * Crate a new label pair.
	 * 
	 * @param parent the parent coposite
	 * @param style the parent composite's style
	 * @param caption the caption or key text
	 * @param value the value to display
	 */
	public LabelPair(final Composite parent, final int style,
			final String caption, final String value) {
		super(parent, style);

		GridLayout layout = new GridLayout(2, false);
		GridData gdLP = new GridData(SWT.FILL, SWT.NORMAL, true, false);
		this.setLayoutData(gdLP);
		layout.marginHeight = LABEL_MARGIN_HEIGHT;
		setLayout(layout);
		captionLabel = new Label(this, SWT.WRAP);
		captionLabel.setText(caption + ": ");
		valueLabel = new Label(this, SWT.WRAP);
		valueLabel.setText(value);
		valueLabel.setFont(boldFont);
		valueLabel.setLayoutData(new GridData(SWT.FILL, SWT.NORMAL, true,
				false));
		this.layout();
	}

	/**
	 * Set the value for the label pair.
	 * @param value a String with the value
	 */
	public void setValue(final String value) {
		valueLabel.setText(value);
	}

	/**
	 * Set the caption for this label pair.
	 * 
	 * @param caption a String with the caption
	 */
	public void setCaption(final String caption) {
		captionLabel.setText(caption);
	}
}
