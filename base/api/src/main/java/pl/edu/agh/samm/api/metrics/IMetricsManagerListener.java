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

package pl.edu.agh.samm.api.metrics;

import java.util.Collection;

/**
 * Subscriber interface for events describing newly started and stopped metrics
 * on the Core system.
 * 
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 */
public interface IMetricsManagerListener {

	/**
	 * Notifies Subscriber about collection of already started metrics on the
	 * Core system. <br>
	 * In most cases collection <code>startedMetrics</code> has only single
	 * element.
	 * 
	 * @param startedMetrics
	 *            {@link Collection} of {@link IMetric} already
	 *            started metrics
	 */
	void notifyNewMetricsStarted(Collection<IMetric> startedMetrics) throws Exception;

	/**
	 * Notifies Subscriber about collection of already stopped metrics on the
	 * Core system. <br>
	 * In most cases collection <code>stoppedMetrics</code> has only single
	 * element.
	 * 
	 * @param stoppedMetrics
	 */
	void notifyMetricsHasStopped(Collection<IMetric> stoppedMetrics) throws Exception;
}
