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

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import pl.edu.agh.samm.eclipse.views.VisualizationChartView;
import pl.edu.agh.samm.eclipse.views.VisualizationManagementView;
import pl.edu.agh.samm.eclipse.views.VisualizationsView;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class VisualizationsPerspective implements IPerspectiveFactory {

	public static String ID = "pl.edu.agh.samm.eclipse.perspectives.VisualizationsPerspective";
	public static String FOLDER_ID = "pl.edu.agh.samm.eclipse.perspectives.VisualizationsPerspective.folder";

	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.addView(VisualizationsView.ID, IPageLayout.LEFT, 0.2f, layout.getEditorArea());
		layout.getViewLayout(VisualizationsView.ID).setCloseable(false);
		layout.addView(VisualizationChartView.ID, IPageLayout.TOP, 0.75f, layout.getEditorArea());
		layout.getViewLayout(VisualizationChartView.ID).setCloseable(false);
		IFolderLayout folderLayout = layout.createFolder(FOLDER_ID, IPageLayout.BOTTOM, 0.25f,
				layout.getEditorArea());
		folderLayout.addView(VisualizationManagementView.ID);
		layout.getViewLayout(VisualizationManagementView.ID).setCloseable(false);
		// folderLayout.addView(LegendView.ID);
		// layout.getViewLayout(LegendView.ID).setCloseable(false);
		// folderLayout.addView(LogView.ID);
		// layout.getViewLayout(LogView.ID).setCloseable(false);
	}

}
