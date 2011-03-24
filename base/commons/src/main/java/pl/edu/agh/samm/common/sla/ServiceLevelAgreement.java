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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class ServiceLevelAgreement implements IServiceLevelAgreement, Serializable {

	private static final long serialVersionUID = -3264499157016715402L;

	private List<String> involvedResources = new LinkedList<String>();

	// resource uri -> {parameter -> object}
	private Map<String, Map<String, Object>> parameters = new HashMap<String, Map<String, Object>>();

	// resource uri -> list of metrics
	private Map<String, List<String>> metricsForResources = new HashMap<String, List<String>>();

	// resource uri -> {metric uri -> criterion}
//	private Map<String, Map<String, ICriterion>> metricsCriteria = new HashMap<String, Map<String, ICriterion>>();

	// resource uri -> {metric uri -> cost}
	private Map<String, Map<String, Number>> resourcesMetricsCosts = new HashMap<String, Map<String, Number>>();

	// resource uri -> resource type
	private Map<String, String> resourceTypes = new HashMap<String, String>();

//	@Override
//	public ICriterion getCriterionForResourceMetric(String resourceURI, String metricURI) {
//		Map<String, ICriterion> metricCriteria = metricsCriteria.get(resourceURI);
//		if (metricCriteria != null) {
//			return metricCriteria.get(metricURI);
//		}
//
//		return null;
//	}

	public void addInvolvedResource(String uri, String resourceType) {
		involvedResources.add(uri);
		resourceTypes.put(uri, resourceType);
	}

	public void clear() {
		clearInvolvedResources();
		this.parameters.clear();
//		this.metricsCriteria.clear();
		this.metricsForResources.clear();
		this.resourcesMetricsCosts.clear();
	}

	public void clearInvolvedResources() {
		involvedResources.clear();
		resourceTypes.clear();
	}

	@Override
	public List<String> getInvolvedPatterns() {
		return involvedResources;
	}

	@Override
	public Number getMetricCost(String resourceURI, String metricURI) {
		Map<String, Number> resourceMetricsCost = resourcesMetricsCosts.get(resourceURI);

		if (resourceMetricsCost != null) {
			return resourceMetricsCost.get(metricURI);
		}

		return null;
	}

	@Override
	public List<String> getMetricsForResource(String resourceURI) {
		return metricsForResources.get(resourceURI);
	}

	@Override
	public Map<String, Object> getParameters(String resourceURI) {
		return parameters.get(resourceURI);
	}

	@Override
	public String getResourceType(String resource) {
		return resourceTypes.get(resource);
	}

	public void addResourceParameters(String uri, Map<String, Object> parameters) {
		this.parameters.put(uri, parameters);
	}

	public void removeInvolvedResource(String uri) {
		involvedResources.remove(uri);
		parameters.remove(uri);
		resourceTypes.remove(uri);
		metricsForResources.remove(uri);
//		metricsCriteria.remove(uri);
		resourcesMetricsCosts.remove(uri);
	}

//	public void addCriterionForResourceMetric(String selectedResource, String selectedMetric,
//			ICriterion criterion) {
//
//		// add actual criterion
//		Map<String, ICriterion> criteria = metricsCriteria.get(selectedResource);
//		if (criteria == null) {
//			criteria = new HashMap<String, ICriterion>();
//			metricsCriteria.put(selectedResource, criteria);
//		}
//		criteria.put(selectedMetric, criterion);
//
//		// add metric for resource
//		addMetricForResource(selectedResource, selectedMetric);
//	}

	private void addMetricForResource(String selectedResource, String selectedMetric) {
		List<String> metricsForResource = metricsForResources.get(selectedResource);

		if (metricsForResource == null) {
			metricsForResource = new ArrayList<String>();
			metricsForResources.put(selectedResource, metricsForResource);
		}

		if (!metricsForResource.contains(selectedMetric)) {
			metricsForResource.add(selectedMetric);
		}
	}

	//
	// public void removeCriterionForResourceMetric(String selectedResource,
	// String selectedMetric) {
	// Map<String, ICriterion> criteria = metricsCriteria.get(selectedResource);
	// if (criteria != null) {
	// criteria.remove(selectedMetric);
	// if (criteria.size() == 0) {
	// metricsCriteria.remove(selectedResource);
	// List<String> metricsForResource =
	// metricsForResources.get(selectedResource);
	// metricsForResource.remove(selectedMetric);
	// }
	// }
	// }

	public void setResourceMetricCost(String selectedResource, String selectedMetric, Number cost) {
		Map<String, Number> resourceMetricsCost = resourcesMetricsCosts.get(selectedResource);

		if (resourceMetricsCost == null) {
			resourceMetricsCost = new HashMap<String, Number>();
			resourcesMetricsCosts.put(selectedResource, resourceMetricsCost);
		}

		if (cost != null) {
			resourceMetricsCost.put(selectedMetric, cost);
		} else {
			resourceMetricsCost.remove(selectedMetric);
		}

		addMetricForResource(selectedResource, selectedMetric);
	}

}
