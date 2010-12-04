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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class ResourceUrlPage extends WizardPage {

	private static final String COMBO_TYPE_CLUSTER = "Cluster";
	private static final String COMBO_TYPE_NODE = "Node";

	private static final String NODE_TYPE = "http://www.icsr.agh.edu.pl/samm_1.owl#Node";
	private static final String CLUSTER_TYPE = "http://www.icsr.agh.edu.pl/samm_1.owl#Cluster";

	private Composite container;
	private Text urlText;
	private Combo typeCombo;

	public ResourceUrlPage() {
		super("Resource URL");
		setTitle("Resource URL");
		setDescription("Insert Resource's URL");
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;

		Label label = new Label(container, SWT.NULL);
		label.setText("Resources type:");

		typeCombo = new Combo(container, SWT.READ_ONLY | SWT.DROP_DOWN);
		String[] types = new String[] { COMBO_TYPE_CLUSTER, COMBO_TYPE_NODE };
		typeCombo.setItems(types);
		typeCombo.select(0);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		typeCombo.setLayoutData(gd);

		label = new Label(container, SWT.NULL);
		label.setText("Resources URL:");

		urlText = new Text(container, SWT.BORDER | SWT.SINGLE);
		urlText.setText("/cluster01");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		urlText.setLayoutData(gd);

		setControl(container);
	}

	public String getUrl() {
		return urlText.getText();
	}

	public String getType() {
		if (typeCombo.getText().equals(COMBO_TYPE_CLUSTER)) {
			return CLUSTER_TYPE;
		}
		if (typeCombo.getText().equals(COMBO_TYPE_NODE)) {
			return NODE_TYPE;
		}
		return null;
	}

	@Override
	public boolean canFlipToNextPage() {
		return true;
	}
}
