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
package pl.edu.agh.samm.eclipse.views.visualization;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import pl.edu.agh.samm.common.metrics.IConfiguredMetric;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class VisualisationsContentProvider implements ITreeContentProvider {

	private List<IVisualisation> visualizationsList = new LinkedList<IVisualisation>();

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IVisualisation) {
			IVisualisation visualisation = (IVisualisation) parentElement;
			return visualisation.getAttachedMetrics().toArray();
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof IConfiguredMetric) {
			IConfiguredMetric metric = (IConfiguredMetric) element;
			for (IVisualisation vis : visualizationsList) {
				if (vis.getAttachedMetrics().contains(metric)) {
					return vis;
				}
			}
		}

		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof IVisualisation) {
			IVisualisation visualisation = (IVisualisation) element;
			return visualisation.getAttachedMetrics().size() > 0;
		}
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return visualizationsList.toArray();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.visualizationsList = (List<IVisualisation>) newInput;
	}
}
