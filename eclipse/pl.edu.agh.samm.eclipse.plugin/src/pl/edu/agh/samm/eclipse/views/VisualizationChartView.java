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
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.jfree.experimental.chart.swt.ChartComposite;

import pl.edu.agh.samm.eclipse.views.visualization.IVisualisation;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class VisualizationChartView extends ViewPart implements ISelectionListener {

	public static String ID = "pl.edu.agh.samm.eclipse.views.VisualizationChartView";
	private ChartComposite chartComposite;

	@Override
	public void createPartControl(Composite parent) {
		chartComposite = new ChartComposite(parent, SWT.NONE, null, true);
		getSite().getPage().addSelectionListener(this);
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void selectionChanged(IWorkbenchPart arg0, ISelection arg1) {
		if (arg0 instanceof VisualizationsView) {
			if (!arg1.isEmpty()) {
				TreeSelection sel = (TreeSelection) arg1;
				if (sel.getFirstElement() instanceof IVisualisation) {
					IVisualisation vis = (IVisualisation) sel.getFirstElement();
					chartComposite.setChart(vis.getChart().getJFreeChart());
					chartComposite.forceRedraw();
				}
			}
		}
	}

}
