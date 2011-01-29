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
package pl.edu.agh.samm.eclipse.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import pl.edu.agh.samm.eclipse.views.AlarmsView;
import pl.edu.agh.samm.eclipse.views.DescriptionView;
import pl.edu.agh.samm.eclipse.views.GraphInfoView;
import pl.edu.agh.samm.eclipse.views.SuggestionsView;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class AlarmsPerspective implements IPerspectiveFactory {

	public static String ID = "pl.edu.agh.samm.eclipse.perspectives.AlarmsPerspective";

	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.addView(AlarmsView.ID, IPageLayout.LEFT, 0.25f, layout.getEditorArea());
		layout.getViewLayout(AlarmsView.ID).setCloseable(false);
		layout.addView(SuggestionsView.ID, IPageLayout.TOP | IPageLayout.RIGHT, 0.5f, layout.getEditorArea());
		layout.getViewLayout(SuggestionsView.ID).setCloseable(false);
		layout.addView(GraphInfoView.ID, IPageLayout.BOTTOM | IPageLayout.RIGHT, 0.2f, layout.getEditorArea());
		layout.getViewLayout(GraphInfoView.ID).setCloseable(false);
		layout.addView(DescriptionView.ID, IPageLayout.BOTTOM, 0.5f, layout.getEditorArea());
		layout.getViewLayout(DescriptionView.ID).setCloseable(false);
	}

}
