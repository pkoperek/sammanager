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
package pl.edu.agh.samm.eclipse.views.providers;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import pl.edu.agh.samm.common.metrics.IConfiguredMetric;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class RunningMetricsContentProvier implements IStructuredContentProvider {

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object source) {
		if (source instanceof List) {
			List<IConfiguredMetric> listOfMetrics = (List<IConfiguredMetric>) source;
			return listOfMetrics.toArray();
		}
		return null;
	}

	@Override
	public void dispose() {
		// nothing happens
	}

	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		// nothing happens
	}

}
