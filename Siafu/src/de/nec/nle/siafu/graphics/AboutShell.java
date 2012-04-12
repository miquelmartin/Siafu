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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/** A shell to showing the credits for Siafu. */
public class AboutShell {

	/** Number of columns in the header. */
	private static final int HEADER_COLUMNS = 3;

	/** The height of the About Shel. */
	private static final int SHELL_HEIGHT = 300;

	/** The width of the About Shel. */
	private static final int SHELL_WIDTH = 400;

	/** Defauklt font size. */
	private static final int DEFAULT_FONT_SIZE = 10;

	/** Font size for the Siafu title label. */
	private static final int SIAFU_FONT_SIZE = 14;

	/** SWT's display. */
	private Display display;

	/** Siafu's icon. */
	private Image icon;

	/** NEC's logo. */
	private Image nec;

	/** The font for the Siafu title. */
	private Font siafuFont;

	/** Bold font. */
	private Font boldFont;

	/** Create the About shell. */
	public AboutShell() {
		display = Display.getCurrent();

		siafuFont = new Font(display, "default", SIAFU_FONT_SIZE, SWT.BOLD);
		boldFont = new Font(display, "default", DEFAULT_FONT_SIZE, SWT.BOLD);

		icon =
				new Image(display, getClass().getResourceAsStream(
					"/res/misc/icon.png"));
		nec =
				new Image(display, getClass().getResourceAsStream(
					"/res/misc/nec.png"));

		GridLayout glShell = new GridLayout(1, true);
		Shell shell = new Shell();
		shell.setSize(SHELL_WIDTH, SHELL_HEIGHT);
		shell.setLayout(glShell);
		shell.setImage(icon);

		GridLayout glHeader = new GridLayout(HEADER_COLUMNS, false);
		GridData gdHeader =
				new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		Composite header = new Composite(shell, SWT.NONE);
		header.setLayoutData(gdHeader);
		header.setLayout(glHeader);

		GridData gdIconLabel =
				new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		Label iconLabel = new Label(header, SWT.NONE);
		iconLabel.setLayoutData(gdIconLabel);
		iconLabel.setImage(icon);

		GridLayout glProgramName = new GridLayout(1, true);
		Composite programName = new Composite(header, SWT.NONE);
		programName.setLayout(glProgramName);

		Label siafuLabel = new Label(programName, SWT.NONE);
		siafuLabel.setFont(siafuFont);
		siafuLabel.setText("Siafu");

		Label anOpenContextSimulatorLabel =
				new Label(programName, SWT.NONE);
		anOpenContextSimulatorLabel
				.setText("An Open Source Context Simulator");

		GridData gdNECLabel =
				new GridData(SWT.END, SWT.BEGINNING, true, false);
		Label necLabel = new Label(header, SWT.NONE);
		necLabel.setLayoutData(gdNECLabel);
		necLabel.setImage(nec);

		Label authorsCaption = new Label(shell, SWT.NONE);
		authorsCaption.setFont(boldFont);
		authorsCaption.setText("Authors:");

		GridData gdAuthors =
				new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		Label authors = new Label(shell, SWT.WRAP);
		authors.setLayoutData(gdAuthors);
		// author.setSize(350, 60);
		authors.setText("Miquel Martin\nDeveloped at NEC Europe "
				+ "Laboratories, Heidelberg. Partly founded by the"
				+ "European Comission within the MobiLife project.\n");

		// contributorsCaption = new Label(shell, SWT.NONE);
		// contributorsCaption.setFont(boldFont);
		// contributorsCaption.setText("Contributors:");
		//
		// GridData gdContributors =
		// new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		// contributors = new Label(shell, SWT.WRAP);
		// contributors.setLayoutData(gdContributors);
		// contributors
		// .setText("Name: contribution");

		shell.addShellListener(new ShellAdapter() {

			public void shellClosed(final ShellEvent e) {
				disposeResources();
			}
		});

		shell.pack();
		shell.open();

	}

	/** Dispose of the SWT allocated resources. */
	private void disposeResources() {
		boldFont.dispose();
		siafuFont.dispose();
		icon.dispose();
		nec.dispose();
	}
}
