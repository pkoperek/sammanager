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
package pl.edu.agh.samm.eclipse.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import pl.edu.agh.samm.common.metrics.IConfiguredMetric;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class RunningMetricsList {

	private static RunningMetricsList _instance;
	private List<IConfiguredMetric> runningMetrics = new CopyOnWriteArrayList<IConfiguredMetric>();
	private List<IRunningMetricListListener> listeners = new CopyOnWriteArrayList<IRunningMetricListListener>();

	private RunningMetricsList() {
	}

	static {
		_instance = new RunningMetricsList();
	}

	public static RunningMetricsList getInstance() {
		return _instance;
	}

	public synchronized void addRunningMetric(IConfiguredMetric runningMetric) {
		runningMetrics.add(runningMetric);
		fireAddRunningMetric(runningMetric);
	}

	protected void fireRemoveRunningMetric(IConfiguredMetric runningMetric) {
		for (IRunningMetricListListener listener : listeners) {
			listener.metricRemoved(runningMetric);
		}
	}

	protected void fireAddRunningMetric(IConfiguredMetric runningMetric) {
		for (IRunningMetricListListener listener : listeners) {
			listener.metricAdded(runningMetric);
		}
	}

	public synchronized void removeRunningMetric(IConfiguredMetric runningMetric) {
		runningMetrics.remove(runningMetric);
		fireRemoveRunningMetric(runningMetric);
	}

	public void addRunningMetricListListener(IRunningMetricListListener listener) {
		listeners.add(listener);
	}

	public void removeRunningMetricListListener(IRunningMetricListListener listener) {
		listeners.remove(listener);
	}

	public synchronized List<IConfiguredMetric> getList() {
		return runningMetrics;
	}
}
