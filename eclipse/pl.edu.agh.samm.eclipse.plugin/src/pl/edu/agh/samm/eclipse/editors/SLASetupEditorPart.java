/**
 * 
 */
package pl.edu.agh.samm.eclipse.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import pl.edu.agh.samm.common.core.SLAException;
import pl.edu.agh.samm.common.decision.IServiceLevelAgreement;
import pl.edu.agh.samm.eclipse.SAMM;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class SLASetupEditorPart extends EditorPart {

	private ISLAManager slaManager = null;

	public SLASetupEditorPart(ISLAManager slaManager) {
		this.slaManager = slaManager;
	}

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
		setInput(input);
		setSite(site);
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
		parent.setLayout(new GridLayout(1, false));

		Button configureButton = new Button(parent, SWT.PUSH);
		configureButton.setText("Configure SLA on Core");
		configureButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!SAMM.isConnected()) {
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							"Not connected", "Connect to Core first");
				} else {
					IServiceLevelAgreement serviceLevelAgreement = slaManager.getServiceLevelAgreement();
					try {
						SAMM.getCoreManagement().startSLAValidation(serviceLevelAgreement);
					} catch (SLAException e1) {
						SAMM.handleException(e1);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		Button unconfigureButton = new Button(parent, SWT.PUSH);
		unconfigureButton.setText("Unconfigure SLA on Core");
		unconfigureButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (!SAMM.isConnected()) {
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							"Not connected", "Connect to Core first");
				} else {
					try {
						SAMM.getCoreManagement().stopSLAValidation();
					} catch (SLAException e) {
						SAMM.handleException(e);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				widgetSelected(arg0);
			}
		});

		Button updateButton = new Button(parent, SWT.PUSH);
		updateButton.setText("Update SLA on Core");
		updateButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (!SAMM.isConnected()) {
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							"Not connected", "Connect to Core first");
				} else {
					IServiceLevelAgreement serviceLevelAgreement = slaManager.getServiceLevelAgreement();
					SAMM.getCoreManagement().updateSLA(serviceLevelAgreement);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				widgetSelected(arg0);
			}
		});

		Button retrieveButton = new Button(parent, SWT.PUSH);
		retrieveButton.setText("Retrieve SLA from Core");
		retrieveButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (!SAMM.isConnected()) {
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							"Not connected", "Connect to Core first");
				} else {
					IServiceLevelAgreement serviceLevelAgreement;
					try {
						serviceLevelAgreement = SAMM.getCoreManagement().retrieveCurrentSLA();
						slaManager.setServiceLevelAgreement(serviceLevelAgreement);
					} catch (SLAException e) {
						SAMM.handleException(e);
					}

				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				widgetSelected(arg0);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// do nothing
	}

}
