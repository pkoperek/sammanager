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
