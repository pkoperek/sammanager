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

package pl.edu.agh.samm.sla;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import pl.edu.agh.samm.common.core.IAlarm;
import pl.edu.agh.samm.common.core.IResourceInstancesManager;
import pl.edu.agh.samm.common.knowledge.ICriterion;
import pl.edu.agh.samm.common.metrics.IConfiguredMetric;
import pl.edu.agh.samm.common.sla.IServiceLevelAgreement;
import pl.edu.agh.samm.core.Alarm;
import pl.edu.agh.samm.core.IMetricFactory;
import pl.edu.agh.samm.metrics.AbstractCriteriaValidator;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class SLAValidatingMetricListener extends AbstractCriteriaValidator implements ISLAValidator {

	private Map<IConfiguredMetric, ICriterion> criteria = new HashMap<IConfiguredMetric, ICriterion>();
	private IServiceLevelAgreement agreement = null;
	private IMetricFactory metricFactory = null;
	private IResourceInstancesManager resourceInstancesManager = null;

	public IResourceInstancesManager getResourceInstancesManager() {
		return resourceInstancesManager;
	}

	public void setResourceInstancesManager(IResourceInstancesManager resourceInstancesManager) {
		this.resourceInstancesManager = resourceInstancesManager;
	}

	public IMetricFactory getMetricFactory() {
		return metricFactory;
	}

	public void setMetricFactory(IMetricFactory metricFactory) {
		this.metricFactory = metricFactory;
	}

	@Override
	public void notifyMetricValue(IConfiguredMetric metric, Number value) {
		if (criteria.containsKey(metric)) {
			ICriterion criterion = criteria.get(metric);

			if (criterion == null) {
				throw new RuntimeException("No criterion defined for: " + metric);
			}

			if (!criterion.meetsCriterion(value)) {
				Map<IConfiguredMetric, Number> metricsWithRanks = getMetricsSuggestedToStart(metric);

				IAlarm alarm = new Alarm(metric, metricsWithRanks, getDescriptionForAlarm(metric, criterion));
				fireAlarm(alarm);
			}
		}
	}

	@Override
	public void setupSLA(IServiceLevelAgreement agreement) {
		this.agreement = agreement;
		this.criteria.clear();

		if (agreement != null) {
			List<String> involvedResources = agreement.getInvolvedPatterns();
			for (String pattern : involvedResources) {

				for (String resourceURI : resourceInstancesManager.getAllRegisteredResources()) {
					if (Pattern.matches(pattern, resourceURI)) {
						List<String> resourceMetrics = agreement.getMetricsForResource(pattern);
						if (resourceMetrics == null) {
							continue;
						}
						for (String metricURI : resourceMetrics) {
							// create configured metric instance
							IConfiguredMetric configuredMetric = metricFactory.createMetric(metricURI,
									resourceURI);

							// get threshold
							ICriterion criterion = agreement
									.getCriterionForResourceMetric(pattern, metricURI);

							// check the case when only the cost was defined for
							// a
							// metric...
							if (criterion != null) {
								// fill the map
								criteria.put(configuredMetric, criterion);
							}
						}
					}
				}

			}
		}
	}

	@Override
	public IServiceLevelAgreement getSLA() {
		return agreement;
	}

}
