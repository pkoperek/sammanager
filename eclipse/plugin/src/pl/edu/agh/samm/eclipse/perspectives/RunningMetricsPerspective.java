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

import pl.edu.agh.samm.eclipse.views.RunningMetricSettingsView;
import pl.edu.agh.samm.eclipse.views.RunningMetricsView;
import pl.edu.agh.samm.eclipse.views.VisualizationsView;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class RunningMetricsPerspective implements IPerspectiveFactory {

	public static String ID = "pl.edu.agh.samm.eclipse.perspectives.RunningMetricsPerspective";

	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.addView(RunningMetricsView.ID, IPageLayout.LEFT, 0.4f, layout.getEditorArea());
		layout.getViewLayout(RunningMetricsView.ID).setCloseable(false);
		layout.addView(VisualizationsView.ID, IPageLayout.TOP, 0.5f, layout.getEditorArea());
		layout.getViewLayout(VisualizationsView.ID).setCloseable(false);
		layout.addView(RunningMetricSettingsView.ID, IPageLayout.BOTTOM, 0.5f, layout.getEditorArea());
		layout.getViewLayout(RunningMetricSettingsView.ID).setCloseable(false);
	}

}
