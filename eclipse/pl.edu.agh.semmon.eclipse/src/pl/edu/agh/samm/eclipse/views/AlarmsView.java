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

import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import pl.edu.agh.samm.eclipse.SAMM;
import pl.edu.agh.samm.eclipse.views.providers.AlarmsViewContentProvider;
import pl.edu.agh.samm.eclipse.views.providers.AlarmsViewLabelProvider;

/**
 * Alarms view
 * 
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class AlarmsView extends ViewPart {

	public static String ID = "pl.edu.agh.samm.eclipse.views.AlarmsView";

	private ListViewer viewer;

	@Override
	public void createPartControl(Composite parent) {
		Composite root = new Composite(parent, SWT.NULL);
		root.setLayout(new FillLayout(SWT.VERTICAL));

		viewer = new ListViewer(root);
		viewer.setContentProvider(new AlarmsViewContentProvider());
		viewer.setLabelProvider(new AlarmsViewLabelProvider());

		getSite().setSelectionProvider(viewer);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public ListViewer getViewer() {
		return viewer;
	}

	public void refresh() {
		viewer.setInput(SAMM.getAlarms());
	}
}
