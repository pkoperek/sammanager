/**
 * 
 */
package pl.edu.agh.samm.eclipse.wizards;

import pl.edu.agh.samm.eclipse.editors.ISLAManager;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class AddResourceToSLAWizard extends RegisterResourceWizard {

	private ISLAManager slaManager;

	public AddResourceToSLAWizard(ISLAManager slaManager) {
		this.slaManager = slaManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		slaManager.addInvolvedResource(getUrl(), getType());
		slaManager.setResourceParameters(getUrl(), getParameters());
		return true;
	}

}
