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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import pl.edu.agh.samm.eclipse.views.visualization.IVisualisation;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class VisualizationManagementView extends ViewPart implements ISelectionListener {

	public static String ID = "pl.edu.agh.samm.eclipse.views.VisualizationManagementView";
	private Text selectedVisualizationText;
	private Button startVisualization;
	private Button stopVisualization;

	private IVisualisation visualization;

	@Override
	public void createPartControl(Composite parent) {
		Composite root = new Composite(parent, SWT.NULL);
		root.setLayout(new GridLayout(2, false));

		Label selectedVisualizationLabel = new Label(root, SWT.NULL);
		selectedVisualizationLabel.setText("Selected Visualization:");
		selectedVisualizationLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

		selectedVisualizationText = new Text(root, SWT.SINGLE | SWT.BORDER);
		selectedVisualizationText.setEditable(false);
		selectedVisualizationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		startVisualization = new Button(root, SWT.PUSH);
		startVisualization.setText("Start Visualization");
		startVisualization.addMouseListener(new org.eclipse.swt.events.MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {
				visualization.setRunning(true);
				refreshView();
			}

		});

		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan = 2;
		startVisualization.setLayoutData(gd);

		stopVisualization = new Button(root, SWT.PUSH);
		stopVisualization.setText("Stop Visualization");
		stopVisualization.addMouseListener(new org.eclipse.swt.events.MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {
				visualization.setRunning(false);
				refreshView();
			}

		});
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan = 2;
		stopVisualization.setLayoutData(gd);
		getSite().getPage().addSelectionListener(this);
	}

	@Override
	public void setFocus() {
		selectedVisualizationText.setFocus();

	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (part instanceof VisualizationsView) {
			if (!selection.isEmpty() && selection instanceof ITreeSelection) {
				ITreeSelection treeSelection = (ITreeSelection) selection;
				if (treeSelection.getFirstElement() instanceof IVisualisation) {
					visualization = (IVisualisation) treeSelection.getFirstElement();
					refreshView();
				}
			}
		}

	}

	private void refreshView() {
		selectedVisualizationText.setText(visualization.getName());
		startVisualization.setEnabled(!visualization.isRunning());
		stopVisualization.setEnabled(visualization.isRunning());
	}

}
