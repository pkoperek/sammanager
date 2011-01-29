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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import pl.edu.agh.samm.eclipse.SAMM;
import pl.edu.agh.samm.eclipse.model.Metric;
import pl.edu.agh.samm.eclipse.model.ResourceCapability;
import pl.edu.agh.samm.eclipse.views.providers.MetricContentProvider;
import pl.edu.agh.samm.eclipse.views.resources.ResourcesTreeNode;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class MetricsView extends ViewPart implements ISelectionListener {

	public static String ID = "pl.edu.agh.samm.eclipse.views.MetricsView";

	private ListViewer viewer;

	private String resourceURI;

	@Override
	public void createPartControl(Composite parent) {
		Composite root = new Composite(parent, SWT.NULL);
		root.setLayout(new GridLayout(1, false));

		viewer = new ListViewer(root);
		viewer.getList().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new MetricContentProvider());

		getSite().getPage().addSelectionListener(this);
		getSite().setSelectionProvider(viewer);
	}

	@Override
	public void setFocus() {
		viewer.getList().setFocus();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (part instanceof ResourcesView) {
			if (!selection.isEmpty()) {
				if (selection instanceof IStructuredSelection) {
					IStructuredSelection structuredSelection = (IStructuredSelection) selection;
					if (structuredSelection.getFirstElement() instanceof ResourcesTreeNode) {
						ResourcesTreeNode treeNode = (ResourcesTreeNode) structuredSelection
								.getFirstElement();

						String type = SAMM.getCoreManagement().getResourceType(treeNode.getURI());
						List<Metric> metrics = createMetricsListFromURIs(SAMM.getKnowledge()
								.getMetricsForResourceType(type));

						viewer.setInput(metrics);
						resourceURI = treeNode.getURI();
					}
				}
			}
		} else if (part instanceof CapabilitiesView) {
			if (!selection.isEmpty()) {
				if (selection instanceof IStructuredSelection) {
					IStructuredSelection structuredSelection = (IStructuredSelection) selection;
					if (structuredSelection.getFirstElement() instanceof ResourceCapability) {
						List<ResourceCapability> resourcesCapabilities = structuredSelection.toList();
						Set<String> capabilitiesUris = new HashSet<String>();

						for (ResourceCapability c : resourcesCapabilities) {
							capabilitiesUris.add(c.getURI());
						}

						String type = SAMM.getCoreManagement().getResourceType(resourceURI);

						List<Metric> metrics = new LinkedList<Metric>();

						metrics.addAll(createMetricsListFromURIs(SAMM.getKnowledge()
								.getMetricsUsingCapabilitiesForResourceType(type, capabilitiesUris)));

						viewer.setInput(metrics);
					}
				}

			}
		}
	}

	private List<Metric> createMetricsListFromURIs(Set<String> uris) {
		List<Metric> metrics = new LinkedList<Metric>();
		for (String uri : uris) {
			metrics.add(new Metric(uri));
		}
		return metrics;
	}
}
