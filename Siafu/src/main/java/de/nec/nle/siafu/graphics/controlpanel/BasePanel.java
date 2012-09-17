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

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import de.nec.nle.siafu.graphics.GUI;

/**
 * An abstract clas for the panels in the simulator's gui. Note that all the
 * extending panels have a combo box to choose elements, and status items to
 * display information on the chosen elements.
 * 
 * @author Miquel Martin
 * 
 */
public abstract class BasePanel extends Composite {

	/** The panel where status items are displayed. */
	private Composite infoPanel;

	/** The scroleld composite for the status items. */
	private ScrolledComposite infoPanelScroll;

	/** The button to choose an element. */
	private Button selectButton;

	/** The elements being shown at the moment. */
	private ArrayList<Object> elementsShown;

	/** The status items for the elements being shown. */
	private ArrayList<BaseStatus> statusItems;

	/** The container GUI. */
	protected GUI gui;

	/** The combo box to choose elements. */
	protected Combo selectionCombo;

	/**
	 * Create a panel. The type of panel depends on what the invoquing
	 * superclass does next.
	 * 
	 * @param gui the container gui
	 * @param parent the parent composite
	 * @param style the compostie's style
	 */
	protected BasePanel(final GUI gui, final Composite parent,
			final int style) {
		super(parent, style);
		this.setLayout(new GridLayout(1, false));
		this.gui = gui;

		statusItems = new ArrayList<BaseStatus>();

		elementsShown = new ArrayList<Object>();

		createSelectionBox();
		createInfoPanel();
	}

	/**
	 * Refresh the status items.
	 * 
	 */
	protected void refresh() {
		for (BaseStatus as : statusItems) {
			as.refresh();
		}
	}

	/**
	 * Create the box where elements are selected.
	 * 
	 */
	private void createSelectionBox() {
		Composite selectionComposite = new Composite(this, SWT.NONE);
		selectionComposite.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		GridLayout glSelectComposite = new GridLayout(2, false);
		selectionComposite.setLayout(glSelectComposite);

		selectionCombo = new Combo(selectionComposite, SWT.READ_ONLY);
		selectionCombo.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.FILL_HORIZONTAL));

		selectionCombo.addSelectionListener(new SelectionAdapter() {
			public synchronized void widgetDefaultSelected(
					final SelectionEvent e) {
				onSelectionMade(selectionCombo.getText());
			}
		});

		selectButton = new Button(selectionComposite, SWT.PUSH);
		selectButton.setImage(new Image(Display.getCurrent(), getClass()
				.getResourceAsStream("/misc/examine.png")));
		selectButton.addSelectionListener(new SelectionAdapter() {
			public synchronized void widgetSelected(final SelectionEvent e) {
				onSelectionMade(selectionCombo.getText());
			}
		});

		/*
		 * TODO: before drawing, one should check the size of all the images,
		 * and resize them to fit the smallest one. Otherwise the images
		 * arestrecthed to the first found size, and look ugly.
		 */
		addSelectableElements();

		selectionCombo.select(0);

		selectionComposite.layout(true);
	}

	/**
	 * What do to when an element is seleced at the info box.
	 * 
	 * @param selection the name of the selected element
	 */
	protected abstract void onSelectionMade(final String selection);

	/**
	 * Adds the elements into the combo box.
	 * 
	 */
	protected abstract void addSelectableElements();

	/**
	 * Creates the panel that contains all the status items.
	 */
	private void createInfoPanel() {
		infoPanelScroll =
				new ScrolledComposite(this, SWT.V_SCROLL | SWT.H_SCROLL);
		infoPanelScroll.setLayout(new GridLayout(1, false));
		infoPanelScroll.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, true));
		infoPanelScroll.setExpandHorizontal(true);
		infoPanel = new Composite(infoPanelScroll, SWT.NORMAL);
		infoPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		infoPanel.setLayout(new GridLayout(1, true));
		infoPanelScroll.setContent(infoPanel);
	}

	/**
	 * Add a status item for the element given in o.
	 * 
	 * @param o the element for which a status item is needed.
	 */
	protected final synchronized void add(final Object o) {
		if (elementsShown.contains(o)) {
			return;
		} else {
			elementsShown.add(o);
			statusItems.add(createStatusItem(infoPanel, o));
			infoPanelScroll.setMinWidth(infoPanel.computeSize(SWT.DEFAULT,
				SWT.DEFAULT).x);
			infoPanelScroll.layout(true);
		}
	}

	/**
	 * The specifics of the status item creation for a particular element o.
	 * 
	 * @param parent the parent composite
	 * @param o the element for which to draw the status item
	 * @return the new status item
	 */
	protected abstract BaseStatus createStatusItem(final Composite parent,
			final Object o);

	/**
	 * Remove a status item from the info panel.
	 * 
	 * @param baseStatus the status item to be removed
	 */
	protected final synchronized void removeStatusItem(
			final BaseStatus baseStatus) {
		for (BaseStatus bs : statusItems) {
			if (bs.equals(baseStatus)) {
				destroyStatusItem(baseStatus);
				elementsShown.remove(bs.getContent());
				statusItems.remove(bs);
				bs.dispose();
				infoPanel.pack();
				infoPanelScroll.layout(true);
				infoPanel.layout(true);
				break;
			}
		}
	}

	/**
	 * The specifics of removing a status item of a particular subtype.
	 * 
	 * @param baseStatus the status item to remove
	 */
	protected abstract void destroyStatusItem(final BaseStatus baseStatus);

	/**
	 * Clean up those resources, specially those that won't be disposed when
	 * the conainer composite is disposed.
	 * 
	 */
	protected void disposeResources() {
		this.dispose();
	}
}
