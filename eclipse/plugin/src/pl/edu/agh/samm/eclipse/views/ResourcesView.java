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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;

import pl.edu.agh.samm.common.core.IResourceEvent;
import pl.edu.agh.samm.common.core.IResourceListener;
import pl.edu.agh.samm.common.metrics.ResourceEventType;
import pl.edu.agh.samm.eclipse.SAMM;
import pl.edu.agh.samm.eclipse.model.ResourcesList;
import pl.edu.agh.samm.eclipse.views.providers.ResourcesLabelProvider;
import pl.edu.agh.samm.eclipse.views.providers.ResourcesTreeContentProvider;
import pl.edu.agh.samm.eclipse.views.resources.ResourcesTreeNode;
import pl.edu.agh.samm.eclipse.wizards.AddResourcePropertiesWizard;

/**
 * Views attached resources tree
 * 
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class ResourcesView extends ViewPart implements IResourceListener {

	public static String ID = "pl.edu.agh.samm.eclipse.views.ResourcesView";
	private TreeViewer viewer;

	public ResourcesView() {
		ResourcesList.getInstance().addResourceListner(this);
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite root = new Composite(parent, SWT.NULL);
		root.setLayout(new FillLayout(SWT.VERTICAL));
		viewer = new TreeViewer(root);
		viewer.setContentProvider(new ResourcesTreeContentProvider());
		viewer.setLabelProvider(new ResourcesLabelProvider());
		this.initContextMenu();

		getSite().setSelectionProvider(viewer);
	}

	private void initContextMenu() {
		// initalize the context menu
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				Action action = new Action() {
					@Override
					public void run() {
						ISelection selection = viewer.getSelection();
						if (selection instanceof IStructuredSelection) {
							ITreeSelection structuredSelection = (ITreeSelection) selection;
							Object selected = structuredSelection.getFirstElement();
							if (selected != null) {
								ResourcesTreeNode resource = (ResourcesTreeNode) selected;
								String uri = resource.getURI();

								AddResourcePropertiesWizard wizard = new AddResourcePropertiesWizard(uri);
								WizardDialog dialog = new WizardDialog(Display.getDefault().getActiveShell(),
										wizard);
								dialog.open();

							}
						}
					}
				};
				action.setText("Add transports");

				Action unregisterAction = new Action() {
					@Override
					public void run() {
						ISelection selection = viewer.getSelection();
						if (selection instanceof IStructuredSelection) {
							ITreeSelection structuredSelection = (ITreeSelection) selection;
							Object selected = structuredSelection.getFirstElement();
							if (selected != null) {
								ResourcesTreeNode resource = (ResourcesTreeNode) selected;
								String uri = resource.getURI();

								SAMM.getCoreManagement().unregisterResource(uri);

							}
						}
					}
				};
				unregisterAction.setText("Unregister");

				ISelection selection = viewer.getSelection();
				if (selection instanceof IStructuredSelection) {
					ITreeSelection structuredSelection = (ITreeSelection) selection;
					Object selected = structuredSelection.getFirstElement();
					if (selected != null) {
						manager.add(action);
						manager.add(unregisterAction);
					}
				}
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getTree());
		viewer.getTree().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	@Override
	public void setFocus() {
		viewer.getTree().setFocus();
	}

	public TreeViewer getViewer() {
		return viewer;
	}

	@Override
	public void processEvent(IResourceEvent event) throws Exception {
		if (event.getType().equals(ResourceEventType.RESOURCES_CHANGED)) {
			viewer.setInput(ResourcesList.getInstance().getResourcesList());
		}
	}
}
