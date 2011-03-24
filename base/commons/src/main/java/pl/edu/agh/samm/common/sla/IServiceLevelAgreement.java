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

package pl.edu.agh.samm.common.sla;

import java.util.List;
import java.util.Map;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public interface IServiceLevelAgreement {

	/**
	 * Lists all the RegExp patterns of resource instances URIs that are a part
	 * of Service Level Agreement
	 * 
	 * @return List of URI of resources involved in SLA
	 */
	List<String> getInvolvedPatterns();

	/**
	 * Returns URI defining type of given resource
	 * 
	 * @param pattern
	 *            RegExp pattern URI of resource instance
	 * @return URI of type of resource matching pattern
	 */
	String getResourceType(String pattern);

	/**
	 * Returns metrics URIs which are defined in SLA for given resource instance
	 * URI pattern
	 * 
	 * @param pattern
	 *            RegExp pattern of resource instance URI
	 * @return list of metrics for given resource
	 */
	List<String> getMetricsForResource(String pattern);

	/**
	 * Returns criteria (range and/or threshold values) defined for given metric
	 * for given resource URI RegExp pattern
	 * 
	 * @param pattern
	 *            resource instance URI RegExp pattern
	 * @param metricURI
	 *            metric URI
	 * @return criteria for given resource pattern and metric
	 */
	//ICriterion getCriterionForResourceMetric(String pattern, String metricURI);

	/**
	 * Gets metric cost for given resource instance URI RegExp pattern and
	 * metric
	 * 
	 * @param pattern
	 *            resource instance URI RegExp pattern
	 * @param metricURI
	 *            metric
	 * @return cost of given metric for given resource instance pattern
	 */
	Number getMetricCost(String pattern, String metricURI);

	/**
	 * Gets parameters (e.g. transport channel addresses, etc.) for given
	 * resource pattern
	 * 
	 * @param pattern
	 *            resource instance URI RegExp pattern
	 * @return map with additional parameters
	 */
	Map<String, Object> getParameters(String pattern);
}
