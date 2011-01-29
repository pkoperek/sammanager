/**
 * This file is part of SAMM.
 *
 * SAMM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SAMM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SAMM.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * 
 */
package pl.edu.agh.samm.eclipse.editors;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import pl.edu.agh.samm.eclipse.SAMM;
import pl.edu.agh.samm.eclipse.wizards.AddResourceToSLAWizard;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class SLAResourcesEditorPart extends EditorPart implements ISLAChangesListener {

	private ISLAManager slaManager = null;
	private List slaResourcesList = null;
	private List currentResourcesList = null;
	private Collection<String> currentResources = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite,
	 * org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		if (!(input instanceof SLAEditorInput)) {
			throw new IllegalArgumentException("Input has to be of SLAEditorInput type!");
		} else {
			SLAEditorInput slaEditor = (SLAEditorInput) input;
			currentResources = slaEditor.getResources();
		}
		setSite(site);
		setInput(input);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout(3, false);
		parent.setLayout(layout);

		// sla resources
		createSLAResourcesPanel(parent);

		// buttons
		createButtonsPanel(parent);

		// current resources list
		createCurrentResourcesPanel(parent);

	}

	public SLAResourcesEditorPart(ISLAManager slaManager) {
		this.slaManager = slaManager;
		slaManager.addSLAChangesListener(this);
	}

	private void createCurrentResourcesPanel(Composite parent) {
		Composite currentResourcesComposite = new Composite(parent, SWT.NONE);
		currentResourcesComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		currentResourcesComposite.setLayout(new GridLayout(1, false));

		Button refreshButton = new Button(currentResourcesComposite, SWT.PUSH);
		refreshButton.setText("Refresh currently available resources list");
		refreshButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		refreshButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				currentResourcesList.setItems(currentResources.toArray(new String[0]));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		Label currentResourcesListLabel = new Label(currentResourcesComposite, SWT.NONE);
		currentResourcesListLabel.setText("Resources available in system: ");
		currentResourcesListLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		currentResourcesList = new List(currentResourcesComposite, SWT.BORDER | SWT.V_SCROLL);
		currentResourcesList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		currentResourcesList.setItems(currentResources.toArray(new String[0]));
	}

	private void createButtonsPanel(Composite parent) {
		Composite buttonsComposite = new Composite(parent, SWT.NONE);
		RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
		buttonsComposite.setLayout(rowLayout);

		Button removeResourceButton = new Button(buttonsComposite, SWT.PUSH);
		removeResourceButton.setText("X");
		removeResourceButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				removeSelectedResourceFromSLA();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		Button addFromCurrentResourceButton = new Button(buttonsComposite, SWT.PUSH);
		addFromCurrentResourceButton.setText("<");
		addFromCurrentResourceButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				addResourceFromCurrentToSLA();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
	}

	private void createSLAResourcesPanel(Composite parent) {
		Composite slaResourcesComposite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		slaResourcesComposite.setLayoutData(gridData);

		GridLayout slaResourcesLayout = new GridLayout(1, true);
		slaResourcesComposite.setLayout(slaResourcesLayout);

		Button addResourceButton = new Button(slaResourcesComposite, SWT.PUSH);
		addResourceButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		addResourceButton.setText("Add new resource to contract");
		addResourceButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addResourceFromWizard();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		Label slaResourcesListLabel = new Label(slaResourcesComposite, SWT.NONE);
		slaResourcesListLabel.setText("Resources involved in contract: ");
		slaResourcesListLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		slaResourcesList = new List(slaResourcesComposite, SWT.BORDER | SWT.V_SCROLL);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		slaResourcesList.setLayoutData(gridData);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		slaResourcesList.setFocus();
	}

	private void addResourceFromCurrentToSLA() {
		String[] selectedResourcesToAdd = currentResourcesList.getSelection();
		for (String selectedResource : selectedResourcesToAdd) {
			ResourcePatternDialog dialog = new ResourcePatternDialog(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), selectedResource);
			dialog.create();
			if (dialog.open() == Window.OK) {
				String resourceType = SAMM.getCoreManagement().getResourceType(selectedResource);
				slaManager.addInvolvedResource(dialog.getUserInput(), resourceType);
			}

		}
	}

	private void addResourceFromWizard() {
		AddResourceToSLAWizard wizard = new AddResourceToSLAWizard(slaManager);
		WizardDialog wizardDialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell(), wizard);
		wizardDialog.open();
	}

	private void removeSelectedResourceFromSLA() {
		String[] selection = slaResourcesList.getSelection();
		for (String uri : selection) {
			slaManager.removeInvolvedResource(uri);
		}
	}

	@Override
	public void slaChanged(SLAChangeRange changeRange) {
		if (changeRange.equals(SLAChangeRange.RESOURCES)) {
			slaResourcesList.setItems(slaManager.getInvolvedPatterns().toArray(new String[0]));
			slaResourcesList.redraw();
		}
	}

}
