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
package pl.edu.agh.samm.eclipse.wizards;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.wizard.Wizard;

import pl.edu.agh.samm.eclipse.wizards.pages.EucalyptusPropertiesPage;
import pl.edu.agh.samm.eclipse.wizards.pages.ITransportProtocolPropertiesPage;
import pl.edu.agh.samm.eclipse.wizards.pages.JMXPropertiesPage;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public abstract class PropertiesPagesWizard extends Wizard {

	private List<ITransportProtocolPropertiesPage> propertiesPages = new LinkedList<ITransportProtocolPropertiesPage>();

	public PropertiesPagesWizard() {
		propertiesPages.add(new JMXPropertiesPage());
		propertiesPages.add(new EucalyptusPropertiesPage());
	}

	@Override
	public void addPages() {
		for (ITransportProtocolPropertiesPage page : propertiesPages) {
			addPage(page);
		}
	}

	public List<ITransportProtocolPropertiesPage> getPropertiesPages() {
		return propertiesPages;
	}

	protected void savePreferences() {
		for (ITransportProtocolPropertiesPage page : propertiesPages) {
			page.savePreferences();
		}
	}

	public Map<String, Object> getParameters() {
		// assuming that properties won't interfere
		Map<String, Object> params = new HashMap<String, Object>();
		for (ITransportProtocolPropertiesPage page : propertiesPages) {
			Map<String, Object> pageParams = page.getProperties();
			if (pageParams != null) {
				params.putAll(pageParams);
			}
		}
		return params;
	}
}
