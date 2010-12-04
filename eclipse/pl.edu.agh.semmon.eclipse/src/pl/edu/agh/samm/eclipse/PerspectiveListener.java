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
package pl.edu.agh.samm.eclipse;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PerspectiveAdapter;

import pl.edu.agh.samm.eclipse.editors.SLAEditor;
import pl.edu.agh.samm.eclipse.editors.SLAEditorInput;
import pl.edu.agh.samm.eclipse.model.ResourcesList;
import pl.edu.agh.samm.eclipse.perspectives.ActionsPerspective;
import pl.edu.agh.samm.eclipse.perspectives.AlarmsPerspective;
import pl.edu.agh.samm.eclipse.perspectives.SLAPerspective;
import pl.edu.agh.samm.eclipse.views.ActionsListView;
import pl.edu.agh.samm.eclipse.views.AlarmsView;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class PerspectiveListener extends PerspectiveAdapter {

	@Override
	public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		if (perspective.getId().equals(AlarmsPerspective.ID)) {
			AlarmsView alarmsView = (AlarmsView) page.findView(AlarmsView.ID);
			alarmsView.getViewer().setInput(SAMM.getAlarms());
		} else if (perspective.getId().equals(SLAPerspective.ID)) {
			try {
				page.closeAllEditors(false);
				page.openEditor(new SLAEditorInput(ResourcesList.getInstance().getResourcesList()),
						SLAEditor.ID);
			} catch (PartInitException e) {
				SAMM.handleException(e);
			}
		} else if (perspective.getId().equals(ActionsPerspective.ID)) {
			ActionsListView actionListView = (ActionsListView) page.findView(ActionsListView.ID);
			actionListView.refresh();
		}
	}

}
