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
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import pl.edu.agh.samm.common.action.Action;
import pl.edu.agh.samm.common.core.IAlarm;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class DescriptionView extends ViewPart implements ISelectionListener {

	public static String ID = "pl.edu.agh.samm.eclipse.views.DescriptionView";
	private Text description;

	@Override
	public void createPartControl(Composite parent) {
		Composite root = new Composite(parent, SWT.NULL);
		root.setLayout(new FillLayout(SWT.VERTICAL));
		description = new Text(root, SWT.MULTI);
		description.setEditable(false);

		getSite().getPage().addSelectionListener(this);
	}

	@Override
	public void setFocus() {
		description.setFocus();
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (part instanceof AlarmsView) {
			if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;
				IAlarm alarm = (IAlarm) structuredSelection.getFirstElement();
				String description = alarm.getDescription();
				this.description.setText(description);
			}
		}
		if (part instanceof ActionsListView) {
			if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;
				Action alarm = (Action) structuredSelection.getFirstElement();
				StringBuilder builder = new StringBuilder();
				builder.append(alarm.getActionURI());
				builder.append("\nParameters:");
				for (String key : alarm.getParameterValues().keySet()) {
					builder.append("\n");
					builder.append(key);
					String value = alarm.getParameterValues().get(key);
					builder.append(value);
				}
				this.description.setText(builder.toString());
			}
		}

	}

}
