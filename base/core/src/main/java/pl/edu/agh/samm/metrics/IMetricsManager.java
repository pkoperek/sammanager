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

package pl.edu.agh.samm.metrics;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;

import pl.edu.agh.samm.api.metrics.IMetric;
import pl.edu.agh.samm.api.metrics.IMetricListener;
import pl.edu.agh.samm.api.metrics.IMetricsManagerListener;
import pl.edu.agh.samm.api.metrics.MetricNotRunningException;

/**
 * Interface for a manager of running metrics on the Core system. <br>
 * 
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 */
public interface IMetricsManager {

	/**
	 * Returns a collection of currently running metrics on the Core system.
	 * 
	 * @return Running metrics
	 */
	Collection<IMetric> getRunningMetrics();

	/**
	 * Subscribes to the events of changes in the collection of currently
	 * running metrics on the Core system. When a new listener is added, it is
	 * notified about currently running metrics with use of the
	 * {@link IMetricsManagerListener#notifyNewMetricsStarted(Collection)} . If
	 * the listener was previously added, the call is ignored.
	 * 
	 * @param listener
	 *            Listener to add
	 */
	void addMetricsManagerListener(IMetricsManagerListener listener);

	/**
	 * Unsubscribes to the events of changes in the collection of currently
	 * running metrics on the Core system.
	 * 
	 * @param listener
	 *            Listener to add
	 */
	void removeMetricsManagerListener(IMetricsManagerListener listener);

	/**
	 * Subscribes to the events containing metric values. <br>
	 * Each metric is polled for a new value. If new value is received from
	 * metric, all Subscribers are notified.
	 * 
	 * @param runningMetric
	 *            Metric to run
	 * @param listener
	 *            Listener to add
	 */
	void addMetricListener(IMetric metric, IMetricListener listener)
			throws MetricNotRunningException;

	/**
	 * Starts a metric and attaches a metric listener so no values are missed by
	 * that listener.
	 * 
	 * @param runningMetric
	 *            Metric to run
	 * @param listeners
	 *            Listeners to add. If null - no listener added
	 */
	void startMetricAndAddRunningMetricListener(IMetric metric,
			Collection<IMetricListener> listeners);

	/**
	 * Unsubscribe from events containing metric values.
	 * 
	 * @param runningMetric
	 *            Running metric
	 * @param listener
	 *            Listener to remove. If null - no listener removed.
	 * 
	 */
	void removeMetricListener(IMetric metric, IMetricListener listener);

	/**
	 * Starts a new thread to monitor a metric
	 * 
	 * @param metric
	 *            Metric to be observed
	 */
	void startMetric(IMetric metric);

	/**
	 * Stops observing a specific metric
	 * 
	 * @param metric
	 *            Metric to for which observation is canceled
	 * @throws MetricNotRunningException
	 *             Exception thrown when requested metric is not started
	 */
	void stopMetric(IMetric metric);

	/**
	 * Remote interface method for
	 * {@link IMetric#setMetricPollTimeInterval(long)}.
	 * 
	 * @param metric
	 * @param pollTimeInterval
	 * @throws RemoteException
	 */
	void updateMetricPollTime(IMetric metric) throws MetricNotRunningException;

	/**
	 * Indicates that a metric is running or not
	 * 
	 * @param metric
	 *            Metric to be checked
	 * @return True is metric is observed, false otherwise
	 */
	boolean isMetricRunning(IMetric metric);

	List<IMetric> getPatternMetrics();

}
