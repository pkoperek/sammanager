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
package pl.edu.agh.samm.eclipse.wizards.pages;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.prefs.BackingStoreException;

import pl.edu.agh.samm.eclipse.SAMM;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class JMXPropertiesPage extends WizardPage implements ITransportProtocolPropertiesPage {

	private static final String JMX_URL_KEY = "JMXURL";
	private static final String PREFERENCES_STORE_NODE = "pl.edu.agh.samm.eclipse.jmx";
	private Composite container;
	private Text urlText;
	private Button enabled;
	private String defaultServiceUrl = "service:jmx:rmi://localhost:9999/jndi/rmi://localhost:9999/jmxrmi";

	public JMXPropertiesPage() {
		super("JMX Properties");
		setTitle("JMX Properties");
		setDescription("Insert protocols properties");
	}

	@Override
	public void createControl(Composite parent) {
		ConfigurationScope scope = new ConfigurationScope();
		IEclipsePreferences node = scope.getNode(PREFERENCES_STORE_NODE);

		defaultServiceUrl = node.get(JMX_URL_KEY, defaultServiceUrl);

		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		Label label = new Label(container, SWT.NULL);
		label.setText("JMX Transport URL:");

		urlText = new Text(container, SWT.BORDER | SWT.SINGLE);
		urlText.setText(defaultServiceUrl);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		urlText.setLayoutData(gd);
		urlText.setEnabled(false);

		enabled = new Button(container, SWT.CHECK);
		enabled.setSelection(false);
		enabled.setText("Enable JMX transport");
		enabled.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				urlText.setEnabled(enabled.getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		setControl(container);
	}

	@Override
	public Map<String, Object> getProperties() {
		if (enabled.getSelection()) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(JMX_URL_KEY, urlText.getText());
			return params;
		}
		return null;
	}

	@Override
	public void savePreferences() {
		ConfigurationScope scope = new ConfigurationScope();
		IEclipsePreferences node = scope.getNode(PREFERENCES_STORE_NODE);

		node.put(JMX_URL_KEY, urlText.getText());

		try {
			node.flush();
		} catch (BackingStoreException e) {
			SAMM.handleException(e);
		}

	}

}
