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
package pl.edu.agh.samm.eclipse.wizards;

import org.eclipse.jface.dialogs.MessageDialog;

import pl.edu.agh.samm.common.core.ResourceAlreadyRegisteredException;
import pl.edu.agh.samm.eclipse.SAMM;
import pl.edu.agh.samm.eclipse.wizards.pages.ResourceUrlPage;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class RegisterResourceWizard extends PropertiesPagesWizard {

	private ResourceUrlPage urlWp;

	public RegisterResourceWizard() {
		urlWp = new ResourceUrlPage();
	}

	@Override
	public void addPages() {
		addPage(urlWp);
		super.addPages();
	}

	public String getUrl() {
		return urlWp.getUrl();
	}

	public String getType() {
		return urlWp.getType();
	}

	@Override
	public boolean performFinish() {
		if (!SAMM.isConnected()) {
			MessageDialog.openError(getShell(), "Not connected", "Connect to Core first");
			return false;
		}

		try {
			SAMM.getCoreManagement().registerResource(getUrl(), getType(), getParameters());
		} catch (ResourceAlreadyRegisteredException e) {
			SAMM.showMessage("Registration failed", e.getMessage(), MessageDialog.ERROR);
			return false;
		}

		savePreferences();

		return true;
	}

}
