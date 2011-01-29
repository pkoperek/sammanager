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

import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IGraphContentProvider;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;

import pl.edu.agh.samm.common.core.IAlarm;
import pl.edu.agh.samm.common.metrics.IConfiguredMetric;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class GraphInfoView extends ViewPart implements ISelectionListener {

	public static String ID = "pl.edu.agh.samm.eclipse.views.GraphInfoView";
	private GraphViewer graphViewer;

	@Override
	public void createPartControl(Composite parent) {
		Composite root = new Composite(parent, SWT.NULL);
		root.setLayout(new FillLayout(SWT.VERTICAL));
		graphViewer = new GraphViewer(root, SWT.NONE);
		graphViewer.setContentProvider(new GraphContentProvider());
		graphViewer.setLabelProvider(new GraphLabelProvider());
		graphViewer.setLayoutAlgorithm(new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);

		getSite().getPage().addSelectionListener(this);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (part instanceof AlarmsView) {
			if (!selection.isEmpty()) {
				if (selection instanceof IStructuredSelection) {
					IStructuredSelection structuredSelection = (IStructuredSelection) selection;
					if (structuredSelection.getFirstElement() instanceof IAlarm) {
						IAlarm alarm = (IAlarm) structuredSelection.getFirstElement();
						graphViewer.setInput(alarm);
					}
				}
			}
		}

	}

	private class GraphContentProvider implements IGraphContentProvider {

		private IAlarm alarm = null;

		@Override
		public void dispose() {
			alarm = null;
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (viewer == graphViewer && newInput instanceof IAlarm) {
				alarm = (IAlarm) newInput;
			}
		}

		@Override
		public Object getSource(Object rel) {
			return alarm.getMetric();
		}

		@Override
		public Object getDestination(Object rel) {
			if (rel.getClass().isArray()) {
				Object[] pair = (Object[]) rel;
				return pair[0];
			}

			return null;
		}

		@Override
		public Object[] getElements(Object input) {
			List<IConfiguredMetric> metrics = alarm.getMetricsToStart();
			Object[][] retVal = new Object[metrics.size()][2];
			for (int i = 0; i < metrics.size(); i++) {
				IConfiguredMetric metric = metrics.get(i);
				retVal[i][0] = metric;
				retVal[i][1] = alarm.getSuggestedMetricRank(metric);
			}
			return retVal;
		}

	}

	private class GraphLabelProvider extends LabelProvider {
		@Override
		public String getText(Object element) {
			if (element.getClass().isArray()) {
				// means that this is a IConfiguredMetric, Rank pair
				Object[] array = (Object[]) element;
				return array[1].toString();
			}

			if (element instanceof IConfiguredMetric) {
				IConfiguredMetric configuredMetric = (IConfiguredMetric) element;
				return configuredMetric.getMetricURI() + " at " + configuredMetric.getResourceURI();
			}

			return null;
		}
	}

}
