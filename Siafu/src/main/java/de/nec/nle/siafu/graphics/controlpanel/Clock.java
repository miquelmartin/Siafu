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

import java.text.DateFormat;
import java.util.Calendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;

import de.nec.nle.siafu.control.Controller;
import de.nec.nle.siafu.graphics.GUI;

/**
 * A clock showing the simulation's time, pause controls and speed scale.
 * 
 * @author Miquel Martin
 * 
 */
public class Clock extends Composite {
	/** Font size for the time label. */
	private static final int TIME_FONT_SIZE = 16;

	/** Font size for the date label. */
	private static final int DATE_FONT_SIZE = 14;

	/** The maximum value of the spped scale. */
	private static final int SCALE_MAXIMUM = 100;

	/** The date font. */
	private static Font dateFont =
			new Font(Display.getDefault(), "Tahoma", DATE_FONT_SIZE, SWT.BOLD);

	/** The time font. */
	private static Font timeFont =
			new Font(Display.getDefault(), "Tahoma", TIME_FONT_SIZE, SWT.BOLD);

	/**
	 * A reference to the simulation GUI.
	 */
	private GUI gui;

	/**
	 * A reference to the Calendar object that keeps the time. The original is
	 * in the World.
	 */
	private Calendar time;

	/** Siafu's controller. */
	private Controller control;

	/** The label for the date. */
	private Label clockDate;

	/** The label for the time. */
	private Label clockTime;

	/** The label with the pause/play icon. */
	private Label playLabel;

	/** The speed regulation scale. */
	private Scale speedScale;

	/** The date formatter. */
	private final DateFormat dateFormat =
			DateFormat.getDateInstance(DateFormat.MEDIUM);

	/** The time formatter. */
	private final DateFormat timeFormat =
			DateFormat.getTimeInstance(DateFormat.SHORT);

	/** True if the simulation is paused, false otherwise. */
	private boolean paused;

	/** The icon to show when the simulator's running. */
	private Image playingImg;

	/** The icon to show when the simulator's paused. */
	private Image pausedImg;

	/**
	 * Create the clock.
	 * 
	 * @param parent the parent composite
	 * @param style the composite's style
	 * @param controlObj siafu's controller
	 */
	public Clock(final Composite parent, final int style,
			final Controller controlObj) {
		super(parent, style);
		this.control = controlObj;
		gui = control.getGUI();
		this.time = control.getWorld().getTime();

		GridData gdClock =
				new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		this.setLayoutData(gdClock);

		GridLayout mainLayout = new GridLayout(2, false);
		// mainLayout.marginWidth = 10;
		// mainLayout.marginHeight = 10;
		this.setLayout(mainLayout);

		GridData gdLeftComposite =
				new GridData(SWT.FILL, SWT.FILL, true, true);
		GridLayout leftLayout = new GridLayout(1, false);
		Composite leftComposite = new Composite(this, SWT.NONE);
		leftComposite.setLayout(leftLayout);
		leftComposite.setLayoutData(gdLeftComposite);

		GridData gdClockDate =
				new GridData(SWT.FILL, SWT.END, true, false);
		clockDate = new Label(leftComposite, SWT.WRAP);
		clockDate.setAlignment(SWT.END);
		clockDate.setFont(dateFont);
		clockDate.setLayoutData(gdClockDate);

		GridData gdClockTime =
				new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		clockTime = new Label(leftComposite, SWT.WRAP);
		clockTime.setAlignment(SWT.END);
		clockTime.setFont(timeFont);
		clockTime.setLayoutData(gdClockTime);

		GridLayout rightLayout = new GridLayout(2, false);
		Composite rightComposite = new Composite(this, SWT.NONE);
		rightComposite.setLayout(rightLayout);

		int initialSpeed = control.getSiafuConfig().getInt("ui.speed");
		GridData gdSpeedScale =
				new GridData(SWT.FILL, SWT.CENTER, true, false);
		speedScale = new Scale(leftComposite, SWT.HORIZONTAL);
		speedScale.setLayoutData(gdSpeedScale);
		speedScale.setMaximum(SCALE_MAXIMUM);
		speedScale.setMinimum(1);
		speedScale.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				gui.setSpeed(((Scale) e.widget).getSelection());
			}
		});
		leftComposite.pack();

		gui.setSpeed(initialSpeed);
		speedScale.setSelection(initialSpeed);

		GridData gdLogo = new GridData();
		gdLogo.verticalSpan = 2;
		gdLogo.grabExcessHorizontalSpace = true;
		gdLogo.horizontalAlignment = GridData.CENTER;

		pausedImg =
				new Image(Display.getCurrent(), getClass()
						.getResourceAsStream("/misc/play.png"));

		playingImg =
				new Image(Display.getCurrent(), getClass()
						.getResourceAsStream("/misc/pause.png"));

		playLabel = new Label(rightComposite, SWT.NONE);
		playLabel.setLayoutData(gdLogo);
		playLabel.setImage(playingImg);
		playLabel.setAlignment(SWT.CENTER);
		playLabel.addMouseListener(new MouseAdapter() {
			public void mouseDown(final MouseEvent e) {

				if (control.isPaused()) {
					control.setPaused(false);
				} else {
					control.setPaused(true);
				}
			}

			public void mouseUp(final MouseEvent e) {
				return;
			}
		});

		this.refresh();
	}

	/** Change the gui to show a paused status.
	 * 
	 * @param pauseStatus whether to show a paused status or not.
	 */
	public void setPaused(final boolean pauseStatus) {
		this.paused = pauseStatus;
		if (paused) {
			playLabel.setImage(pausedImg);
		} else {
			playLabel.setImage(playingImg);
		}
	}

	/** Refresh the clock. */
	public void refresh() {
		clockDate.setText(dateFormat.format(time.getTime()));
		clockTime.setText(timeFormat.format(time.getTime()));
	}

	/** Dispose of the SWT resources. */
	public void disposeResources() {
	}
}
