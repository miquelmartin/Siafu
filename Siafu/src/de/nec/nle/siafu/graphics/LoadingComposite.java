package de.nec.nle.siafu.graphics;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class LoadingComposite extends Composite {

	public LoadingComposite(Composite parent) {
		super(parent, SWT.BORDER);
		GridLayout glLoadingComposite = new GridLayout();
		GridData gdLoadingComposite = new GridData(SWT.CENTER, SWT.CENTER,
				true, true);
		this.setSize(parent.getSize());
		this.setLayout(glLoadingComposite);
		this.setLayoutData(gdLoadingComposite);
	}

	public void dispose() {
		super.dispose();
	}

}
