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

package pl.edu.agh.samm.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.edu.agh.samm.common.knowledge.ICriterion;
import pl.edu.agh.samm.common.knowledge.IKnowledge;
import pl.edu.agh.samm.common.metrics.IConfiguredMetric;
import pl.edu.agh.samm.metrics.AbstractCriteriaValidator;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class KnowledgeAlarmsMetricListener extends AbstractCriteriaValidator {

	private IKnowledge knowledgeService= null;
	private Map<String, ICriterion> acceptationCriteria = new HashMap<String, ICriterion>();

	public void init() {
		List<String> metricsWithDefinedLimits = knowledgeService.getMetricsWithDefinedLimits();

		for (String metricURI : metricsWithDefinedLimits) {
			ICriterion criterion = knowledgeService.getMetricValueAcceptationCriterion(metricURI);

			acceptationCriteria.put(metricURI, criterion);
		}
	}

	/**
	 * @param knowledgeProvider
	 *            the knowledgeProvider to set
	 */
	public void setKnowledgeService(IKnowledge knowledgeService) {
		this.knowledgeService = knowledgeService;
	}

	@Override
	public void notifyMetricValue(IConfiguredMetric metric, Number value) throws Exception {
		if (acceptationCriteria.containsKey(metric.getMetricURI())) {
			ICriterion criterion = acceptationCriteria.get(metric.getMetricURI());

			if (!criterion.meetsCriterion(value)) {
				Map<IConfiguredMetric, Number> metricsToStart = getMetricsSuggestedToStart(metric);
				Alarm alarm = new Alarm(metric, metricsToStart, getDescriptionForAlarm(metric, criterion));
				fireAlarm(alarm);
			}
		}
	}

}
