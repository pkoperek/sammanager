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
package pl.edu.agh.samm.eclipse.views;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import pl.edu.agh.samm.common.metrics.IConfiguredMetric;
import pl.edu.agh.samm.eclipse.SAMM;
import pl.edu.agh.samm.eclipse.model.Metric;
import pl.edu.agh.samm.eclipse.views.resources.ResourcesTreeNode;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class AddMetricView extends ViewPart implements ISelectionListener {

	public static String ID = "pl.edu.agh.samm.eclipse.views.AddMetricView";

	private Text resourceText;
	// private Text capabilityText;
	private Text metricText;
	private Button addMetricButton;
	private Text pollTimeText;
	private ResourcesTreeNode resource;
	private Metric metric;

	// private ResourceCapability capability;

	public AddMetricView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite root = new Composite(parent, SWT.NULL);
		root.setLayout(new GridLayout(2, false));

		Label resourceLabel = new Label(root, SWT.NULL);
		resourceLabel.setText("Resource:");
		resourceLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

		resourceText = new Text(root, SWT.SINGLE | SWT.BORDER);
		resourceText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		resourceText.setEditable(false);

		// Label capabilityLabel = new Label(root, SWT.NULL);
		// capabilityLabel.setText("Capability:");
		// capabilityLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
		// false, false));
		//
		// capabilityText = new Text(root, SWT.SINGLE | SWT.BORDER);
		// capabilityText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
		// false));
		// capabilityText.setEditable(false);

		Label metricLabel = new Label(root, SWT.NULL);
		metricLabel.setText("Metric:");
		metricLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

		metricText = new Text(root, SWT.SINGLE | SWT.BORDER);
		metricText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		metricText.setEditable(false);

		Label pollTimeLabel = new Label(root, SWT.NULL);
		pollTimeLabel.setText("Poll time (ms):");
		pollTimeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

		pollTimeText = new Text(root, SWT.SINGLE | SWT.BORDER);
		pollTimeText.setText("1000");
		pollTimeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		addMetricButton = new Button(root, SWT.NULL);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gd.horizontalSpan = 2;
		addMetricButton.setLayoutData(gd);
		addMetricButton.setText("Start metric");
		addMetricButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				widgetSelected(arg0);
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				long pollTime = 1000L;
				try {
					pollTime = Long.parseLong(pollTimeText.getText());
				} catch (NumberFormatException e) {
					// nothing happens
					// just let user know it was started with 1000
					pollTimeText.setText(Long.toString(pollTime));
				}
				try {
					if (metric != null && resource != null) {
						IConfiguredMetric configuredMetric = SAMM.getCoreManagement()
								.createRunningMetricInstance(metric.getURI(), resource.getURI());

						SAMM.getCoreManagement().startMetric(configuredMetric);
					} else {
						MessageDialog messageDialog = new MessageDialog(PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow().getShell(), "Error!", null,
								"You have to select a resource and metric!", MessageDialog.ERROR,
								new String[] { "OK" }, 0);
						messageDialog.open();
					}
				} catch (Exception e) {
					SAMM.handleException(e);
				}
			}

		});

		getSite().getPage().addSelectionListener(this);
	}

	@Override
	public void setFocus() {
		resourceText.setFocus();
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		Object selectedObject = structuredSelection.getFirstElement();
		if (selectedObject != null) {
			if (part instanceof ResourcesView) {
				ResourcesTreeNode treeNode = (ResourcesTreeNode) selectedObject;
				resource = treeNode;
				resourceText.setText(treeNode.toString());
			} else if (part instanceof MetricsView) {
				Metric metricListObject = (Metric) selectedObject;
				metric = metricListObject;
				metricText.setText(metricListObject.toString());
			}
			// else if (part instanceof CapabilitiesView) {
			// ResourceCapability resourceCapability = (ResourceCapability)
			// selectedObject;
			// capability = resourceCapability;
			// capabilityText.setText(resourceCapability.toString());
			// }
		}

	}

}
