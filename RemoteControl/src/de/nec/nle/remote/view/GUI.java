/*
 * Copyright NEC Europe Ltd. 2006-2007
 * 
 * This file is part of the Remote Control for the context simulator Siafu.
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

package de.nec.nle.remote.view;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import de.nec.nle.remote.Remote;
import de.nec.nle.remote.communication.CommHandler;

public class GUI implements Runnable {

	Logger logger = Logger.getLogger(this.getClass());

	private Shell shell;
	private Display display;
	private Font buttonFont;

	private CommHandler comm;
	private Remote controller;

	private boolean lastConnectionStatus = false;
	private ArrayList<Control> connectionDependentControls = new ArrayList<Control>();

	private ConnectionChecker connectionChecker;

	private Image connectedImg;
	private Image notConnectedImg;

	private Label connectionImgLabel;

	public GUI(Remote controller) {
		this.controller = controller;
	}

	public Display getDisplay() {
		return display;
	}

	public void run() {
		this.display = new Display();
		this.connectedImg = new Image(display, this.getClass()
				.getResourceAsStream("/res/connected.png"));
		this.notConnectedImg = new Image(display, this.getClass()
				.getResourceAsStream("/res/notConnected.png"));
		this.shell = new Shell();
		Image appIcon = new Image(display, this.getClass().getResourceAsStream(
				"/res/appIcon.png"));
		shell.setImage(appIcon);
		shell.setLayout(new GridLayout(2, false));
		buttonFont = new Font(Display.getDefault(), "Arial", 15, SWT.NORMAL);

		createConnectionStatusPanel();

		swtLoop();
		controller.die();
	}

	private void createConnectionStatusPanel() {
		Composite status = new Composite(shell, SWT.NONE);
		status.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false,
				2, 0));
		GridLayout glStatus = new GridLayout(2, false);
		glStatus.horizontalSpacing = 0;
		status.setLayout(glStatus);
		connectionImgLabel = new Label(status, SWT.NONE);
		connectionImgLabel.setImage(notConnectedImg);
		connectionImgLabel.setLayoutData(new GridData(SWT.END, SWT.BEGINNING,
				true, false, 2, 1));
	}

	public void createFooter(final Image logo, final String[] resetCommands) {
		if (logo == null && resetCommands == null) {
			return;
		}
		Composite footer = new Composite(shell, SWT.NONE);
		footer.setLayout(new GridLayout(2, false));

		footer
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2,
						1));

		Label logoLabel = new Label(footer, SWT.NONE);
		logoLabel.setLayoutData(new GridData(SWT.BEGINNING));
		if (logo != null) {
			logoLabel.setImage(logo);
		}
		if (resetCommands != null) {
			Image reset = new Image(display, this.getClass()
					.getResourceAsStream("/res/reset.png"));
			Button resetButton = new Button(footer, SWT.FLAT);
			resetButton.setLayoutData(new GridData(SWT.END, SWT.FILL, true,
					true));
			resetButton.setImage(reset);
			resetButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					logger.info("Resetting");
					try {
						for (String command : resetCommands) {
							comm.doCommand(command);
						}
					} catch (IOException e) {
						logger.error("Error resetting", e);
					}
				}
			});
		}
	}

	public synchronized Group createGroup(String caption) {
		Group group = new Group(shell, SWT.BORDER);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		group.setLayout(new GridLayout(2, false));
		group.setText(caption);
		group.setEnabled(false);

		connectionDependentControls.add(group);
		return group;
	}

	public synchronized Button addButton(final String[] commands,
			final String caption, final Composite parent) {
		return addButton(commands, null, caption, parent);
	}

	public synchronized Button addButton(final String[] commands, Image img,
			final String caption, final Composite parent) {

		if (img != null) {
			Label l = new Label(parent, SWT.NONE);
			l.setImage(img);
			connectionDependentControls.add(l);
		}

		Button b = new Button(parent, SWT.PUSH);
		GridData buttonGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		if (img == null) {
			buttonGridData.horizontalSpan = 2;
		}
		b.setLayoutData(buttonGridData);
		b.setText(caption);
		b.setFont(buttonFont);
		b.setEnabled(false);
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				try {
					for (String c : commands) {
						comm.doCommand(c);
					}

				} catch (IOException e) {
					logger.error("Error issuing command " + caption, e);
				}
			}
		});
		connectionDependentControls.add(b);
		return b;
	}

	private void swtLoop() {
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.dispose();
	}

	public Composite getMainComposite() {
		return shell;
	}

	public void showError(String message) {
		MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
		mb.setMessage(message);
		mb.open();
	}

	public void openShell() {
		shell.layout();
		shell.pack();
		shell.open();
	}

	class ConnectionChecker implements Runnable {

		public void run() {
			updateStatus();
		}
	}

	public void updateStatus() {
		boolean connectedNow = comm.isConnected();
		if (connectedNow != lastConnectionStatus) {
			logger.debug("Was connected=" + lastConnectionStatus + " but now="
					+ connectedNow);

			setButtonsEnabled(comm.isConnected());
			setConnectionStatus(connectedNow);
			lastConnectionStatus = connectedNow;
		}
		display.timerExec(1000, connectionChecker);
	}

	public void setCommHandler(CommHandler comm) {
		this.comm = comm;
		this.connectionChecker = new ConnectionChecker();
		display.timerExec(1000, connectionChecker);
	}

	private void setConnectionStatus(boolean connected) {
		if (connected) {
			connectionImgLabel.setImage(connectedImg);
		} else {
			connectionImgLabel.setImage(notConnectedImg);
		}
	}

	private void setButtonsEnabled(boolean enabled) {
		for (Control c : connectionDependentControls) {
			c.setEnabled(enabled);
		}
	}

	public void setButtonFontSize(int size) {
		buttonFont = new Font(Display.getDefault(), "Arial", size, SWT.NORMAL);
	}

	public void setShellTitle(String title) {
		shell.setText(title);
	}

}
