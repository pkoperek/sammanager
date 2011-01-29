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
package pl.edu.agh.samm.eclipse.views;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import pl.edu.agh.samm.common.metrics.IConfiguredMetric;
import pl.edu.agh.samm.common.metrics.MetricNotRunningException;
import pl.edu.agh.samm.eclipse.SAMM;
import pl.edu.agh.samm.eclipse.dialogs.NewVisualizationDialog;
import pl.edu.agh.samm.eclipse.model.RunningMetricsList;
import pl.edu.agh.samm.eclipse.views.visualization.IVisualisation;
import pl.edu.agh.samm.eclipse.views.visualization.Visualisation;
import pl.edu.agh.samm.eclipse.views.visualization.charts.ChartType;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class RunningMetricSettingsView extends ViewPart implements ISelectionListener {

	public static String ID = "pl.edu.agh.samm.eclipse.views.RunningMetricSettingsView";
	private Text selectedVisualizationText;
	private Text selectedMetricText;
	private Spinner pollTimeSpinner;
	private IConfiguredMetric selectedRunningMetric;
	private IVisualisation selectedVisualization;
	private Button stopMetricButton;

	@Override
	public void createPartControl(final Composite parent) {
		Composite root = new Composite(parent, SWT.NULL);
		root.setLayout(new GridLayout(1, false));

		// metric settings
		Group settingsGroup = new Group(root, SWT.SHADOW_IN);
		settingsGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		settingsGroup.setText("Metric Settings");
		settingsGroup.setLayout(new GridLayout(2, false));

		Label pollTimeLabel = new Label(settingsGroup, SWT.NULL);
		pollTimeLabel.setText("Poll time (ms):");
		pollTimeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

		pollTimeSpinner = new Spinner(settingsGroup, SWT.BORDER);
		pollTimeSpinner.setMinimum(1);
		pollTimeSpinner.setMaximum(Integer.MAX_VALUE);
		pollTimeSpinner.setSelection(1000);
		pollTimeSpinner.setIncrement(100);
		pollTimeSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

		pollTimeSpinner.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (selectedRunningMetric != null) {
					long currentPollTime = selectedRunningMetric.getMetricPollTimeInterval();
					long newPollTime = pollTimeSpinner.getSelection();
					if (newPollTime != currentPollTime) {
						selectedRunningMetric.setMetricPollTimeInterval(newPollTime);
						try {
							SAMM.getCoreManagement().updateMetricPollTimeInterval(selectedRunningMetric);
						} catch (MetricNotRunningException e1) {
							e1.printStackTrace();
							SAMM.handleException(e1);
						}
					}
				}

			}
		});

		stopMetricButton = new Button(settingsGroup, SWT.PUSH);
		stopMetricButton.setText("Stop metric");
		stopMetricButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		stopMetricButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (selectedRunningMetric != null) {
					SAMM.getCoreManagement().stopMetric(selectedRunningMetric);
					RunningMetricsList.getInstance().removeRunningMetric(selectedRunningMetric);
				}
			}
		});
		// visualization management
		Group visualizationManagement = new Group(root, SWT.SHADOW_IN);
		visualizationManagement.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
		visualizationManagement.setText("Visualization Management");
		visualizationManagement.setLayout(new GridLayout(2, false));

		Label selectedVisualizationLabel = new Label(visualizationManagement, SWT.NULL);
		selectedVisualizationLabel.setText("Selected Visualization:");
		selectedVisualizationLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

		selectedVisualizationText = new Text(visualizationManagement, SWT.SINGLE | SWT.BORDER);
		selectedVisualizationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		selectedVisualizationText.setEditable(false);

		Label selectedMetricLabel = new Label(visualizationManagement, SWT.NULL);
		selectedMetricLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		selectedMetricLabel.setText("Selected Metric:");

		selectedMetricText = new Text(visualizationManagement, SWT.SINGLE | SWT.BORDER);
		selectedMetricText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		selectedMetricText.setEditable(false);

		// buttons
		Button addMetricToVisualization = new Button(visualizationManagement, SWT.PUSH);
		addMetricToVisualization.setText("Add Metric to Visualiation");
		addMetricToVisualization.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		addMetricToVisualization.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {
				if (selectedRunningMetric != null && selectedVisualization != null) {
					try {
						selectedVisualization.attachToMetric(selectedRunningMetric);
						refreshVisualizationsView();
					} catch (Exception e1) {
						SAMM.handleException(e1);
					}

				} else {
					MessageDialog messageDialog = new MessageDialog(Display.getDefault().getActiveShell(),
							"Error!", null, "You have to select a metric and a visualization",
							MessageDialog.ERROR, new String[] { "OK" }, 0);
					messageDialog.open();
				}
			}

		});

		Button removeMetricFromVisualization = new Button(visualizationManagement, SWT.PUSH);
		removeMetricFromVisualization.setText("Remove Metric from Visualization");
		removeMetricFromVisualization.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		removeMetricFromVisualization.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (selectedRunningMetric != null && selectedVisualization != null) {
					try {
						selectedVisualization.detachFromMetric(selectedRunningMetric);
						refreshVisualizationsView();
					} catch (Exception e1) {
						SAMM.handleException(e1);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		Button addVisualization = new Button(visualizationManagement, SWT.PUSH);
		addVisualization.setText("Add Visualiation");
		addVisualization.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {
				NewVisualizationDialog dialog = new NewVisualizationDialog(parent.getShell());
				if (dialog.open() == Window.OK) {
					String visName = dialog.getVisualizationName();
					ChartType visType = dialog.getVisualizationType();
					IVisualisation vis = new Visualisation(visType);
					vis.setName(visName);
					vis.setRunning(false);
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage();
					VisualizationsView view = (VisualizationsView) page.findView(VisualizationsView.ID);
					view.addVisualization(vis);
					refreshVisualizationsView();
				}
			}

		});
		addVisualization.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

		Button removeVisualization = new Button(visualizationManagement, SWT.PUSH);
		removeVisualization.setText("Remove Visualiation");
		removeVisualization.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

		removeVisualization.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (selectedVisualization != null) {
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage();
					VisualizationsView view = (VisualizationsView) page.findView(VisualizationsView.ID);
					selectedVisualization.setRunning(false);
					view.removeVisualization(selectedVisualization);
					refreshVisualizationsView();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		getSite().getPage().addSelectionListener(this);
	}

	@Override
	public void setFocus() {
		this.selectedMetricText.setFocus();
	}

	private void refreshVisualizationsView() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		VisualizationsView view = (VisualizationsView) page.findView(VisualizationsView.ID);
		view.refresh();
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (part instanceof RunningMetricsView) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			if (!structuredSelection.isEmpty()) {
				IConfiguredMetric runningMetric = (IConfiguredMetric) structuredSelection.getFirstElement();
				selectedMetricText.setText(runningMetric.toString());
				selectedRunningMetric = runningMetric;
				pollTimeSpinner.setSelection((int) runningMetric.getMetricPollTimeInterval());
			}
		} else if (part instanceof VisualizationsView) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			if (!structuredSelection.isEmpty()
					&& structuredSelection.getFirstElement() instanceof IVisualisation) {
				IVisualisation visualization = (IVisualisation) structuredSelection.getFirstElement();
				selectedVisualization = visualization;
				this.selectedVisualizationText.setText(visualization.toString());
			}
		}
	}

}
