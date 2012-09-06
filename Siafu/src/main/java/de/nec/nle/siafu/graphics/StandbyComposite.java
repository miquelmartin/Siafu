package de.nec.nle.siafu.graphics;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class StandbyComposite extends Composite {

	private static Image siafuIcon = null;
	private static Image noteIcon = null;

	private Display display = Display.getCurrent();

	private Composite splashComposite;
	private Composite hintComposite;

	public StandbyComposite(Composite parent) {
		super(parent, SWT.NONE);
		GridLayout glStandByComposite = new GridLayout();
		GridData gdStandByComposite = new GridData(SWT.CENTER, SWT.CENTER,
				true, true);
		this.setLayout(glStandByComposite);
		this.setLayoutData(gdStandByComposite);

		createSplashComposite();
		createHintComposite();

	}

	private void createSplashComposite() {
		GridData gdSplashComposite = new GridData(SWT.CENTER, SWT.CENTER, true,
				true);
		GridLayout glSplashComposite = new GridLayout(1, false);
		splashComposite = new Composite(this, SWT.NONE);
		splashComposite.setLayout(glSplashComposite);
		splashComposite.setLayoutData(gdSplashComposite);
		
		if (siafuIcon == null) {
			siafuIcon = new Image(display, getClass().getResourceAsStream(
					"/misc/icon.png"));
		}
		
		GridData gdIconLabel = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		Label iconLabel = new Label(splashComposite, SWT.NONE);
		iconLabel.setImage(siafuIcon);
		iconLabel.setLayoutData(gdIconLabel);
		
		Label splashLabel = new Label(splashComposite, SWT.NONE);
		splashLabel.setText("Load a simulation to start");
	}
	
	private void createHintComposite() {
		GridData gdhintComposite = new GridData(SWT.FILL, SWT.CENTER, true,
				true);
		gdhintComposite.verticalIndent=40;
		
		GridLayout glhintComposite = new GridLayout(2, false);
		hintComposite = new Composite(this, SWT.NONE);
		hintComposite.setLayout(glhintComposite);
		hintComposite.setLayoutData(gdhintComposite);
		if (noteIcon == null) {
			noteIcon = new Image(display, getClass().getResourceAsStream(
					"/misc/note.png"));
		}
		
		GridData gdNoteLabel = new GridData(SWT.CENTER, SWT.TOP, false,true);
		Label noteLabel = new Label(hintComposite, SWT.NONE);
		noteLabel.setImage(noteIcon);
		noteLabel.setLayoutData(gdNoteLabel);
		
		Label splashLabel = new Label(hintComposite, SWT.TOP);
		splashLabel.setText("By the way:\n- Select agents with your mouse or right-click for a context menu\n- Move agents by selecting them and double-clicking on the destination");
	}

	public void dispose(){
		super.dispose();
		siafuIcon.dispose();
		siafuIcon=null;
		noteIcon.dispose();
		noteIcon=null;
	}
}
