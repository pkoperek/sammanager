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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import pl.edu.agh.samm.eclipse.views.providers.CapabilityContentProvider;
import pl.edu.agh.samm.eclipse.views.resources.ResourcesTreeNode;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class CapabilitiesView extends ViewPart implements ISelectionListener {

	public static String ID = "pl.edu.agh.samm.eclipse.views.CapabilitiesView";
	private ListViewer viewer;

	@Override
	public void createPartControl(Composite parent) {
		Composite root = new Composite(parent, SWT.NULL);
		root.setLayout(new FillLayout(SWT.VERTICAL));

		viewer = new ListViewer(root);
		viewer.setContentProvider(new CapabilityContentProvider());

		getSite().getPage().addSelectionListener(this);
		getSite().setSelectionProvider(viewer);
	}

	@Override
	public void setFocus() {
		viewer.getList().setFocus();
	}

	@Override
	public void selectionChanged(IWorkbenchPart workbenchPart, ISelection selection) {
		if (workbenchPart instanceof ResourcesView) {
			if (selection instanceof IStructuredSelection) {
				ITreeSelection structuredSelection = (ITreeSelection) selection;
				Object selected = structuredSelection.getFirstElement();
				if (selected != null) {
					ResourcesTreeNode resource = (ResourcesTreeNode) selected;
					viewer.setInput(resource.getURI());
				}
			}
		}
	}

}
