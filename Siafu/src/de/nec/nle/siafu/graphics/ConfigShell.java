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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A shell to change the configuration of Siafu.
 * 
 * @author Miquel Martin
 * 
 */
public class ConfigShell {

	/** Maximum allowed speed. */
	private static final int MAX_SPEED = 100;

	/** Width of the speed scale widget. */
	private static final int SPEED_SCALE_WIDTH = 130;

	/** Default width for buttons. */
	private static final int DEFAULT_BUTTON_WIDTH = 80;

	/** Default width for text widgets. */
	private static final int DEFAULT_TEXT_WIDTH = 50;

	/** The configuration being changed. */
	private XMLConfiguration conf;

	/** The container shell. */
	private Shell shell;

	/** The text that's prefix to the selected speed. */
	private static final String SPEED_BASE_TEXT =
			"Initial simulation speed";

	/** The label for the speed choice. */
	private Label speedLabel;

	/** The Text to choose the cache size. */
	private Text cacheSizeText;

	/** The label on the size of the cache. */
	private Label cacheSizeLabel;

	/**
	 * The composite that shows the CSV options.
	 */
	private Composite csvOutputComposite;

	/** The radio buttons to choose the output method. */
	private Button csvOutputRadio, nullOutputRadio;

	/** The Text for the path for the CSV output. */
	private Text csvPath;

	/** The Text for the cosen TCP port. */
	private Text tcpPortText;

	/** The label showing the chosen port. */
	private Label tcpPortLabel;

	/** The shell's icon. */
	private Image icon;

	/** Whether to keep the history of the CSV button. */
	private Button keepHistoryButton;

	/** The scale to choose the sim's initial speed. */
	private Scale speedScale;

	/** The button to choose if the cache should be prefilled. */
	private Button fillCacheButton;

	/** The button to choose if Siafu should listen on a TCP port. */
	private Button listenButton;

	/** The text to choose the interval between CSV updates. */
	private Text intervalText;

	/**
	 * Create the configuration shell using the provided configuration.
	 * 
	 * @param conf the configuration
	 */
	public ConfigShell(final XMLConfiguration conf) {
		this.conf = conf;

		GridLayout glShell = new GridLayout(1, true);
		shell = new Shell();
		shell.setLayout(glShell);
		shell.setText("Configuration options");
		icon =
				new Image(Display.getCurrent(), getClass()
						.getResourceAsStream("/res/misc/icon.png"));
		shell.setImage(icon);
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(final ShellEvent e) {
				icon.dispose();
			}
		});
		createUIGroup();

		createOutputGroup();

		createListenerGroup();

		createButtons();

		shell.pack();
		shell.open();

	}

	/**
	 * Create the buttons at the bottom of the shell.
	 */
	private void createButtons() {
		GridData gdButtonsComposite =
				new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		GridLayout glButtonsComposite = new GridLayout(2, false);
		Composite buttonsComposite = new Composite(shell, SWT.NONE);
		buttonsComposite.setLayout(glButtonsComposite);
		buttonsComposite.setLayoutData(gdButtonsComposite);

		GridData gdButtonOk =
				new GridData(SWT.END, SWT.BEGINNING, true, false);
		gdButtonOk.minimumWidth = DEFAULT_BUTTON_WIDTH;
		Button buttonOK = new Button(buttonsComposite, SWT.PUSH);
		buttonOK.setLayoutData(gdButtonOk);
		buttonOK.setText("OK");
		buttonOK.setFocus();
		buttonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				saveConfig();
				shell.dispose();

			}
		});

		GridData gdButtonCancel =
				new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false);
		gdButtonCancel.minimumWidth = DEFAULT_BUTTON_WIDTH;
		Button buttonCancel = new Button(buttonsComposite, SWT.PUSH);
		buttonCancel.setLayoutData(gdButtonCancel);
		buttonCancel.setText("Cancel");
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				shell.dispose();

			}
		});
	}

	/** Save the configuration displayed in the shell into the XML file. */
	private void saveConfig() {
		conf.setProperty("commandlistener.enable", listenButton
				.getSelection());
		conf.setProperty("commandlistener.tcpport", tcpPortText.getText());
		// conf.setProperty("ui.usegui", true);
		conf.setProperty("ui.speed", speedScale.getSelection());
		conf.setProperty("ui.gradientcache.prefill", fillCacheButton
				.getSelection());
		conf.setProperty("ui.gradientcache.size", cacheSizeText.getText());
		if (nullOutputRadio.getSelection()) {
			conf.setProperty("output.type", "null");
		} else {
			conf.setProperty("output.type", "csv");
		}
		conf.setProperty("output.csv.path", csvPath.getText());
		conf.setProperty("output.csv.interval", intervalText.getText());
		conf.setProperty("output.csv.keephistory", keepHistoryButton
				.getSelection());

		try {
			conf.save();
		} catch (ConfigurationException e) {
			throw new RuntimeException(
					"Can not save the configuration file!", e);
		}

	}

	/** Create the group for the TCP listen options. */
	private void createListenerGroup() {
		GridData gdListenerGroup =
				new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		GridLayout glListenerGroup = new GridLayout(1, false);
		Group listenerGroup = new Group(shell, SWT.NONE);
		listenerGroup.setText("Command listener");
		listenerGroup.setLayout(glListenerGroup);
		listenerGroup.setLayoutData(gdListenerGroup);

		listenButton = new Button(listenerGroup, SWT.CHECK);
		listenButton.setSelection(conf
				.getBoolean("commandlistener.enable"));
		listenButton.setText("Listen for commands on a TCP port");
		listenButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				boolean enabled = ((Button) e.widget).getSelection();
				tcpPortLabel.setEnabled(enabled);
				tcpPortText.setEnabled(enabled);
			}
		});

		GridLayout glTCPPortComposite = new GridLayout(2, false);
		glTCPPortComposite.marginWidth = 0;
		Composite tcpPortComposite =
				new Composite(listenerGroup, SWT.NONE);
		tcpPortComposite.setLayout(glTCPPortComposite);

		GridData gdTCPPortText =
				new GridData(DEFAULT_TEXT_WIDTH, SWT.DEFAULT);
		tcpPortText = new Text(tcpPortComposite, SWT.BORDER);
		tcpPortText.setLayoutData(gdTCPPortText);
		tcpPortText.setText("" + conf.getInt("commandlistener.tcpport"));
		tcpPortText.addVerifyListener(new VerifyListener() {
			public void verifyText(final VerifyEvent e) {
				if (e.keyCode == SWT.DEL || e.keyCode == SWT.BS) {
					return;
				}
				String regexp = "[0-9]";
				char c = e.character;
				if (!("" + c).matches(regexp)) {
					e.doit = false;
				}
			}
		});
		tcpPortLabel = new Label(tcpPortComposite, SWT.NONE);
		tcpPortLabel.setText("Listening TCP port");
	}

	/** Create the group for simulation output choice. */
	private void createOutputGroup() {
		GridData gdOutputGroup =
				new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		GridLayout glOutputGroup = new GridLayout(1, false);
		Group outputGroup = new Group(shell, SWT.NORMAL);
		outputGroup.setText("Simulation Output:");
		outputGroup.setLayout(glOutputGroup);
		outputGroup.setLayoutData(gdOutputGroup);

		GridLayout glOutputTypeComposite = new GridLayout(1, false);
		glOutputTypeComposite.marginWidth = 0;
		Composite outputTypeComposite =
				new Composite(outputGroup, SWT.NONE);
		outputTypeComposite.setLayout(glOutputTypeComposite);

		nullOutputRadio = new Button(outputTypeComposite, SWT.RADIO);
		nullOutputRadio
				.setText("Output only on the Graphical User Interface");
		nullOutputRadio.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(final SelectionEvent e) {
				nullOutputRadio.setSelection(true);
				csvOutputRadio.setSelection(false);
				for (Control c1 : csvOutputComposite.getChildren()) {
					c1.setEnabled(false);
					if (c1 instanceof Composite) {
						for (Control c2 : ((Composite) c1).getChildren()) {
							c2.setEnabled(false);
						}
					}
				}
			}
		});
		nullOutputRadio.setSelection(conf.getString("output.type")
				.equalsIgnoreCase("null"));

		csvOutputRadio = new Button(outputTypeComposite, SWT.RADIO);
		csvOutputRadio
				.setText("Output to Comma Separated Value (CSV) file");
		csvOutputRadio.setSelection(conf.getString("output.type")
				.equalsIgnoreCase("csv"));
		csvOutputRadio.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(final SelectionEvent e) {
				csvOutputRadio.setSelection(true);
				nullOutputRadio.setSelection(false);
				for (Control c1 : csvOutputComposite.getChildren()) {
					c1.setEnabled(true);
					if (c1 instanceof Composite) {
						for (Control c2 : ((Composite) c1).getChildren()) {
							c2.setEnabled(true);
						}
					}
				}
			}
		});

		GridData gdCSVOutputComposite =
				new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		GridLayout glCSVOutputComposite = new GridLayout(1, false);
		glCSVOutputComposite.marginWidth = 0;
		csvOutputComposite = new Composite(outputTypeComposite, SWT.NONE);
		csvOutputComposite.setLayout(glCSVOutputComposite);
		csvOutputComposite.setLayoutData(gdCSVOutputComposite);

		GridData gdCSVPathComposite =
				new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		GridLayout glCSVPathComposite = new GridLayout(2, false);
		glCSVPathComposite.marginWidth = 0;
		Composite csvPathComposite =
				new Composite(csvOutputComposite, SWT.NONE);
		csvPathComposite.setLayoutData(gdCSVPathComposite);
		csvPathComposite.setLayout(glCSVPathComposite);

		GridData gdCSVPath =
				new GridData(SWT.FILL, SWT.BEGINNING, true, true);
		csvPath = new Text(csvPathComposite, SWT.BORDER);
		csvPath.setLayoutData(gdCSVPath);
		csvPath.setEnabled(csvOutputRadio.getSelection());
		csvPath.setText(conf.getString("output.csv.path"));
		Button csvChooseButton = new Button(csvPathComposite, SWT.PUSH);
		csvChooseButton.setText("Select");
		csvChooseButton.setEnabled(csvOutputRadio.getSelection());
		csvChooseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.setText("Open a simulation");
				fd.setFilterPath(csvPath.getText());
				csvPath.setText(fd.open());
			}
		});

		GridLayout glCSVIntervalComposite = new GridLayout(2, false);
		glCSVIntervalComposite.marginWidth = 0;
		Composite cacheSizeComposite =
				new Composite(csvOutputComposite, SWT.NONE);
		cacheSizeComposite.setLayout(glCSVIntervalComposite);

		GridData gdIntervalText =
				new GridData(DEFAULT_TEXT_WIDTH, SWT.DEFAULT);
		intervalText = new Text(cacheSizeComposite, SWT.BORDER);
		intervalText.setEnabled(csvOutputRadio.getSelection());
		intervalText.setLayoutData(gdIntervalText);
		intervalText.setText("" + conf.getInt("output.csv.interval"));
		intervalText.addVerifyListener(new VerifyListener() {
			public void verifyText(final VerifyEvent e) {
				if (e.keyCode == SWT.DEL || e.keyCode == SWT.BS) {
					return;
				}

				String regexp = "[0-9]";
				char c = e.character;
				if (!("" + c).matches(regexp)) {
					e.doit = false;
				}
			}
		});
		Label intervalLabel = new Label(cacheSizeComposite, SWT.NONE);
		intervalLabel.setEnabled(csvOutputRadio.getSelection());
		intervalLabel.setText("Simulation seconds between CSV printouts");
		intervalLabel
				.setToolTipText("Note that too high a number will cause an "
						+ "out of memory exception!");

		keepHistoryButton = new Button(csvOutputComposite, SWT.CHECK);
		keepHistoryButton
				.setText("Do not reset the CSV file after every simulated day");
		keepHistoryButton.setEnabled(csvOutputRadio.getSelection());
	}

	/** Create the group for the UI options. */
	private void createUIGroup() {
		GridData gdUiGroup =
				new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		GridLayout glUiGroup = new GridLayout(1, false);
		Group uiGroup = new Group(shell, SWT.NORMAL);
		uiGroup.setText("User Interface");
		uiGroup.setLayout(glUiGroup);
		uiGroup.setLayoutData(gdUiGroup);

		// Button useUI = new Button(uiGroup, SWT.CHECK);
		// useUI.setText("Use the Graphical user interface");
		// useUI
		// .setToolTipText("Note that you will need to edit\nthe config file
		// to reverse this setting!");

		GridData gdSpeedComposite =
				new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		GridLayout glSpeedComposite = new GridLayout(2, false);
		glSpeedComposite.marginWidth = 0;
		Composite speedComposite = new Composite(uiGroup, SWT.NONE);
		speedComposite.setLayout(glSpeedComposite);
		speedComposite.setLayoutData(gdSpeedComposite);

		GridData gdSpeedScale = new GridData(SPEED_SCALE_WIDTH, SWT.DEFAULT);
		GridData gdSpeedLabel =
				new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		speedScale = new Scale(speedComposite, SWT.HORIZONTAL);
		speedScale.setLayoutData(gdSpeedScale);
		speedScale.setMinimum(0);
		speedScale.setMaximum(MAX_SPEED);
		speedLabel = new Label(speedComposite, SWT.NONE);
		speedLabel.setLayoutData(gdSpeedLabel);
		speedLabel.setText("Initial simulation speed: "
				+ conf.getInt("ui.speed"));
		speedScale.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				speedLabel.setText(SPEED_BASE_TEXT + ": "
						+ ((Scale) e.widget).getSelection());
			}
		});
		speedScale.setSelection(conf.getInt("ui.speed"));

		fillCacheButton = new Button(uiGroup, SWT.CHECK);
		fillCacheButton.setText("Prefill the gradient cache");
		fillCacheButton.setSelection(conf
				.getBoolean("ui.gradientcache.prefill"));
		fillCacheButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				cacheSizeText.setEnabled(((Button) e.widget)
						.getSelection());
				cacheSizeLabel.setEnabled(((Button) e.widget)
						.getSelection());
			}
		});

		GridLayout glCacheSizeComposite = new GridLayout(2, false);
		glCacheSizeComposite.marginWidth = 0;
		Composite cacheSizeComposite = new Composite(uiGroup, SWT.NONE);
		cacheSizeComposite.setLayout(glCacheSizeComposite);

		GridData gdCacheSizeText =
				new GridData(DEFAULT_TEXT_WIDTH, SWT.DEFAULT);
		cacheSizeText = new Text(cacheSizeComposite, SWT.BORDER);
		cacheSizeText.setLayoutData(gdCacheSizeText);
		cacheSizeText.setText("" + conf.getInt("ui.gradientcache.size"));
		cacheSizeText.setEnabled(fillCacheButton.getSelection());
		cacheSizeText.addVerifyListener(new VerifyListener() {
			public void verifyText(final VerifyEvent e) {
				if (e.keyCode == SWT.DEL || e.keyCode == SWT.BS) {
					return;
				}

				String regexp = "[0-9]";
				char c = e.character;
				if (!("" + c).matches(regexp)) {
					e.doit = false;
				}
			}
		});
		cacheSizeLabel = new Label(cacheSizeComposite, SWT.NONE);
		cacheSizeLabel
				.setText("Number of gradients to store in the cache");
		cacheSizeLabel
				.setToolTipText("Note that too high a number will cause "
						+ "an out of memory exception!");
		cacheSizeLabel.setEnabled(fillCacheButton.getSelection());
	}
}
