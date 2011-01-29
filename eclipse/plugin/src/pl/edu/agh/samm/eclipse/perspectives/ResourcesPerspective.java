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

import pl.edu.agh.samm.eclipse.views.AddMetricView;
import pl.edu.agh.samm.eclipse.views.CapabilitiesView;
import pl.edu.agh.samm.eclipse.views.MetricsView;
import pl.edu.agh.samm.eclipse.views.ResourcesView;

/**
 * Resources perspective
 * 
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class ResourcesPerspective implements IPerspectiveFactory {
	public static String ID = "pl.edu.agh.samm.eclipse.perspectives.Resources";

	@Override
	public void createInitialLayout(IPageLayout layout) {
		// don't show editor area - we don't have an editor now
		layout.setEditorAreaVisible(false);

		// add resources view
		layout.addView(ResourcesView.ID, IPageLayout.LEFT, 0.3f, layout.getEditorArea());
		layout.getViewLayout(ResourcesView.ID).setCloseable(false);
		layout.addView(CapabilitiesView.ID, IPageLayout.RIGHT, 0.3f, layout.getEditorArea());
		layout.getViewLayout(CapabilitiesView.ID).setCloseable(false);
		layout.addView(MetricsView.ID, IPageLayout.TOP, 0.7f, layout.getEditorArea());
		layout.getViewLayout(MetricsView.ID).setCloseable(false);
		layout.addView(AddMetricView.ID, IPageLayout.BOTTOM, 0.3f, layout.getEditorArea());
		layout.getViewLayout(AddMetricView.ID).setCloseable(false);
	}
}
