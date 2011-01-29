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
public class EucalyptusPropertiesPage extends WizardPage implements ITransportProtocolPropertiesPage {

	private static final String EUCA_ENDPOINT_URL_KEY = "EUCALYPTUSENDPOINT";
	private static final String EUCA_ACCESSKEY_KEY = "EUCALYPTUSACCESSKEY";
	private static final String EUCA_SECRETKEY_KEY = "EUCALYPTUSSECRETKEY";
	private static final String EUCA_KEYNAME_KEY = "EUCALYPTUSKEYNAME";

	private static final String PREFERENCES_STORE_NODE = "pl.edu.agh.samm.eclipse.euca";

	private Composite container;
	private Text endPointURLText;
	private Text accessKeyText;
	private Text secretKeyText;
	private Text keyNameText;
	private Button enabled;

	private String defaultEndPointUrl = "http://192.168.1.200:8773/services/Eucalyptus";
	private String defaultAccessKey = "";
	private String defaultSecretKey = "";
	private String defaultKeyName = "mykey";

	public EucalyptusPropertiesPage() {
		super("Eucalyptus Properties");
		setTitle("Eucalyptus Properties");
		setDescription("Insert protocols properties");

	}

	@Override
	public void createControl(Composite parent) {
		ConfigurationScope scope = new ConfigurationScope();
		IEclipsePreferences node = scope.getNode(PREFERENCES_STORE_NODE);

		defaultEndPointUrl = node.get(EUCA_ENDPOINT_URL_KEY, defaultEndPointUrl);
		defaultAccessKey = node.get(EUCA_ACCESSKEY_KEY, defaultAccessKey);
		defaultSecretKey = node.get(EUCA_SECRETKEY_KEY, defaultSecretKey);
		defaultKeyName = node.get(EUCA_KEYNAME_KEY, defaultKeyName);

		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		Label label = new Label(container, SWT.NULL);
		label.setText("Eucalyptus Endpoint URL:");

		endPointURLText = new Text(container, SWT.BORDER | SWT.SINGLE);
		endPointURLText.setText(defaultEndPointUrl);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		endPointURLText.setLayoutData(gd);
		endPointURLText.setEnabled(false);

		label = new Label(container, SWT.NULL);
		label.setText("Access key:");
		accessKeyText = new Text(container, SWT.BORDER | SWT.SINGLE);
		accessKeyText.setText(defaultAccessKey);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		accessKeyText.setLayoutData(gd);
		accessKeyText.setEnabled(false);

		label = new Label(container, SWT.NULL);
		label.setText("Secret key:");
		secretKeyText = new Text(container, SWT.BORDER | SWT.SINGLE);
		secretKeyText.setText(defaultSecretKey);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		secretKeyText.setLayoutData(gd);
		secretKeyText.setEnabled(false);

		label = new Label(container, SWT.NULL);
		label.setText("Private key name:");
		keyNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		keyNameText.setText(defaultKeyName);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		keyNameText.setLayoutData(gd);
		keyNameText.setEnabled(false);

		enabled = new Button(container, SWT.CHECK);
		enabled.setSelection(false);
		enabled.setText("Enable Eucalyptus transport");
		enabled.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				endPointURLText.setEnabled(enabled.getSelection());
				accessKeyText.setEnabled(enabled.getSelection());
				secretKeyText.setEnabled(enabled.getSelection());
				keyNameText.setEnabled(enabled.getSelection());
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
			params.put(EUCA_ENDPOINT_URL_KEY, endPointURLText.getText());
			params.put(EUCA_ACCESSKEY_KEY, accessKeyText.getText());
			params.put(EUCA_SECRETKEY_KEY, secretKeyText.getText());
			params.put(EUCA_KEYNAME_KEY, keyNameText.getText());
			return params;
		}
		return null;
	}

	@Override
	public void savePreferences() {
		ConfigurationScope scope = new ConfigurationScope();
		IEclipsePreferences node = scope.getNode(PREFERENCES_STORE_NODE);

		node.put(EUCA_ENDPOINT_URL_KEY, endPointURLText.getText());
		node.put(EUCA_ACCESSKEY_KEY, accessKeyText.getText());
		node.put(EUCA_SECRETKEY_KEY, secretKeyText.getText());
		node.put(EUCA_KEYNAME_KEY, keyNameText.getText());

		try {
			node.flush();
		} catch (BackingStoreException e) {
			SAMM.handleException(e);
		}

	}

}
