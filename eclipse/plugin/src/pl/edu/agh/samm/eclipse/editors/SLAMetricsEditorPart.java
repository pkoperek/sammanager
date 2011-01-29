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
package pl.edu.agh.samm.eclipse.editors;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import pl.edu.agh.samm.common.knowledge.ICriterion;
import pl.edu.agh.samm.eclipse.SAMM;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class SLAMetricsEditorPart extends EditorPart implements ISLAChangesListener {

	private static final String NO_CRITERION_FOUND_FOR_SELECTED_METRIC = "No criterion found for selected metric!";
	private static final String NO_METRIC_SELECTED = "No metric selected";
	private static final String NO_COST_SET_FOR_SELECTED_METRIC = "No cost set for selected metric!";
	private ISLAManager slaManager;
	private List slaResourcesList;
	private List slaMetricsList;
	private Text slaCriterion;
	private Text slaMetricCost;

	public SLAMetricsEditorPart(ISLAManager slaManager) {
		this.slaManager = slaManager;
		slaManager.addSLAChangesListener(this);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// does nothing
	}

	@Override
	public void doSaveAs() {
		// do nothing
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setInput(input);
		setSite(site);
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(3, false));
		createSLAResourcesPanel(parent);
		createSLAMetrics(parent);
	}

	private void createSLAMetrics(Composite parent) {
		Composite slaMetricsComposite = new Composite(parent, SWT.NONE);
		slaMetricsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		GridLayout slaMetricsLayout = new GridLayout(1, true);
		slaMetricsComposite.setLayout(slaMetricsLayout);

		Label slaMetricsListLabel = new Label(slaMetricsComposite, SWT.NONE);
		slaMetricsListLabel.setText("List of metrics available for resource: ");
		slaMetricsListLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		slaMetricsList = new List(slaMetricsComposite, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		slaMetricsList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		slaMetricsList.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String[] selectedResources = slaResourcesList.getSelection();
				String[] selectedMetrics = slaMetricsList.getSelection();
				if (selectedResources != null && selectedResources.length > 0 && selectedMetrics != null
						&& selectedMetrics.length > 0) {
					String selectedResource = selectedResources[0];
					String selectedMetric = selectedMetrics[0];

					// show criterion
					ICriterion criterion = slaManager.getCriterionForResourceMetric(selectedResource,
							selectedMetric);
					if (criterion == null) {
						slaCriterion.setText(NO_CRITERION_FOUND_FOR_SELECTED_METRIC);
					} else {
						slaCriterion.setText(criterion.toString());
					}

					// show cost
					Number cost = slaManager.getMetricCost(selectedResource, selectedMetric);

					if (cost != null) {
						slaMetricCost.setText(cost.toString());
					} else {
						slaMetricCost.setText(NO_COST_SET_FOR_SELECTED_METRIC);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		createButtons(slaMetricsComposite);

		createSLAMetricCost(slaMetricsComposite);

		// criteria description
		Label slaCriterionLabel = new Label(slaMetricsComposite, SWT.NONE);
		slaCriterionLabel.setText("Criterion:");
		slaCriterionLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		slaCriterion = new Text(slaMetricsComposite, SWT.BORDER | SWT.MULTI);
		slaCriterion.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		slaCriterion.setText(NO_METRIC_SELECTED);
		slaCriterion.setEditable(false);
	}

	private void createSLAMetricCost(Composite slaMetricsComposite) {
		Composite metricCostComposite = new Composite(slaMetricsComposite, SWT.NONE);
		metricCostComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		metricCostComposite.setLayout(new GridLayout(3, false));

		// metric cost field
		Label slaMetricCostLabel = new Label(metricCostComposite, SWT.NONE);
		slaMetricCostLabel.setText("Metric cost:");
		slaMetricCostLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		slaMetricCost = new Text(metricCostComposite, SWT.BORDER | SWT.SINGLE);
		slaMetricCost.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		slaMetricCost.setText(NO_METRIC_SELECTED);

		// accept cost button
		Button acceptCost = new Button(metricCostComposite, SWT.PUSH);
		acceptCost.setText("Accept cost");
		acceptCost.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, false, false));
		acceptCost.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String[] selectedResources = slaResourcesList.getSelection();
				String[] selectedMetrics = slaMetricsList.getSelection();
				if (selectedResources != null && selectedResources.length > 0 && selectedMetrics != null
						&& selectedMetrics.length > 0) {
					String selectedResource = selectedResources[0];
					String selectedMetric = selectedMetrics[0];

					Number cost = null;
					try {
						cost = Double.parseDouble(slaMetricCost.getText());
						slaManager.setResourceMetricCost(selectedResource, selectedMetric, cost);
					} catch (NumberFormatException ee) {
						if (slaMetricCost.getText().equals("")) {
							slaManager.setResourceMetricCost(selectedResource, selectedMetric, null);
						} else {
							SAMM.handleException(ee);
						}
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
		});
	}

	private void createButtons(Composite slaMetricsComposite) {
		// buttons composite
		Composite buttonComposite = new Composite(slaMetricsComposite, SWT.NONE);
		buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		buttonComposite.setLayout(new FillLayout(SWT.HORIZONTAL));

		// add button
		Button addCriterion = new Button(buttonComposite, SWT.PUSH);
		addCriterion.setText("Set criterion");
		addCriterion.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell parentShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

				String[] selectedResources = slaResourcesList.getSelection();
				String[] selectedMetrics = slaMetricsList.getSelection();
				if (selectedResources != null && selectedResources.length > 0 && selectedMetrics != null
						&& selectedMetrics.length > 0) {
					String selectedResource = selectedResources[0];
					String selectedMetric = selectedMetrics[0];

					CriterionDefinitionDialog criterionDefinitionDialog = new CriterionDefinitionDialog(
							parentShell);
					if (criterionDefinitionDialog.open() == Window.OK) {

						ICriterion criterion = criterionDefinitionDialog.getCriterion();

						slaManager.setCriterionForResourceMetric(selectedResource, selectedMetric, criterion);
					}
				} else {
					SAMM.showMessage("Error", "Please select a resource and a metric first!",
							MessageDialog.ERROR);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		// remove button
		Button removeButton = new Button(buttonComposite, SWT.PUSH);
		removeButton.setText("Remove criterion");
		removeButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String[] selectedResources = slaResourcesList.getSelection();
				String[] selectedMetrics = slaMetricsList.getSelection();
				if (selectedResources != null && selectedResources.length > 0 && selectedMetrics != null
						&& selectedMetrics.length > 0) {
					String selectedResource = selectedResources[0];
					String selectedMetric = selectedMetrics[0];

					slaManager.removeCriterionForResourceMetric(selectedResource, selectedMetric);
				}
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

		Label slaResourcesListLabel = new Label(slaResourcesComposite, SWT.NONE);
		slaResourcesListLabel.setText("Resources involved in contract: ");
		slaResourcesListLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		slaResourcesList = new List(slaResourcesComposite, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		slaResourcesList.setLayoutData(gridData);
		slaResourcesList.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String[] selectedResources = slaResourcesList.getSelection();

				// there should be only one resource
				String selectedResource = selectedResources[0];
				String resourceType = slaManager.getResourceType(selectedResource);
				Set<String> metrics = SAMM.getKnowledge().getMetricsForResourceType(resourceType);
				slaMetricsList.setItems(metrics.toArray(new String[0]));
				slaCriterion.setText(NO_METRIC_SELECTED);
				slaMetricCost.setText(NO_METRIC_SELECTED);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
	}

	@Override
	public void setFocus() {
		slaResourcesList.setFocus();
	}

	@Override
	public void slaChanged(SLAChangeRange changeRange) {
		if (changeRange.equals(SLAChangeRange.RESOURCES)) {
			slaResourcesList.setItems(slaManager.getInvolvedPatterns().toArray(new String[0]));
		}

		if (changeRange.equals(SLAChangeRange.CRITERIA)) {
			String[] selectedResources = slaResourcesList.getSelection();
			String[] selectedMetrics = slaMetricsList.getSelection();
			if (selectedResources != null && selectedResources.length > 0 && selectedMetrics != null
					&& selectedMetrics.length > 0) {
				String selectedResource = selectedResources[0];
				String selectedMetric = selectedMetrics[0];

				ICriterion criterion = slaManager.getCriterionForResourceMetric(selectedResource,
						selectedMetric);
				if (criterion != null) {
					slaCriterion.setText(criterion.toString());
				} else {
					slaCriterion.setText(NO_CRITERION_FOUND_FOR_SELECTED_METRIC);
				}
			}
		}
	}

}
