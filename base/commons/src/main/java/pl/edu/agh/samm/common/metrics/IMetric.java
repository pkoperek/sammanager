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

package pl.edu.agh.samm.common.metrics;

import java.io.Serializable;

/**
 * Interface for metric running on the Core system.
 * 
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 */
public interface IMetric extends Serializable {

	/**
	 * Returns URI of {@link MetricOntSchema#AbstractMetricClass} class of URI
	 * of individual of one of its subclasses.
	 * 
	 * @return
	 */
	String getMetricURI();

	/**
	 * Returns URI of {@link ResourceOntSchema#ResourceClass} being monitored.
	 * 
	 * @return
	 */
	String getResourceURI();

	long setMetricPollTimeInterval(long pollTimeInterval);

	long getMetricPollTimeInterval();

	/**
	 * Returns true if resource URI is a regular expression - the metric is a
	 * pattern for single resource metrics
	 * 
	 * @return True if resourceURI contains * or ?
	 */
	boolean isPatternMetric();
}
