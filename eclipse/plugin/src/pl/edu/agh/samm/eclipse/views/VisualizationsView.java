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

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import pl.edu.agh.samm.common.metrics.IConfiguredMetric;
import pl.edu.agh.samm.eclipse.views.visualization.IVisualisation;
import pl.edu.agh.samm.eclipse.views.visualization.Visualisation;
import pl.edu.agh.samm.eclipse.views.visualization.VisualisationsContentProvider;
import pl.edu.agh.samm.eclipse.views.visualization.charts.ChartType;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class VisualizationsView extends ViewPart {

	public final static String ID = "pl.edu.agh.samm.eclipse.views.VisualizationsView";
	public final static String DEFAULT_VIS_NAME = "Default visualisation";

	private TreeViewer viewer;
	private List<IVisualisation> visualizations = new LinkedList<IVisualisation>();

	public VisualizationsView() {
		Visualisation vis = new Visualisation(ChartType.LINE);
		vis.setName(DEFAULT_VIS_NAME);
		visualizations.add(vis);
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite root = new Composite(parent, SWT.NULL);
		root.setLayout(new FillLayout(SWT.VERTICAL));
		viewer = new TreeViewer(root);
		viewer.setContentProvider(new VisualisationsContentProvider());
		viewer.setInput(visualizations);
		getSite().setSelectionProvider(viewer);
	}

	@Override
	public void setFocus() {
		viewer.getTree().setFocus();
	}

	public void addVisualization(IVisualisation vis) {
		// adds new visualization
		visualizations.add(vis);
		refresh();
	}

	public void removeVisualization(IVisualisation vis) {
		// container.removeVisualisation(vis);
		visualizations.remove(vis);
		refresh();
	}

	public void refresh() {
		viewer.setInput(visualizations);
		viewer.refresh();
	}

	@Override
	public void dispose() {
		super.dispose();
		for (IVisualisation vis : visualizations) {
			for (IConfiguredMetric metric : vis.getAttachedMetrics()) {
				try {
					vis.detachFromMetric(metric);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
