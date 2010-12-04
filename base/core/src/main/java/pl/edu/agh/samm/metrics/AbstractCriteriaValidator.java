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
 * 
 */
package pl.edu.agh.samm.metrics;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.samm.common.core.IAlarm;
import pl.edu.agh.samm.common.core.IAlarmListener;
import pl.edu.agh.samm.common.knowledge.ICriterion;
import pl.edu.agh.samm.common.knowledge.IKnowledge;
import pl.edu.agh.samm.common.metrics.IConfiguredMetric;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public abstract class AbstractCriteriaValidator implements ICriteriaValidator {
	private List<IAlarmListener> alarmListeners = new CopyOnWriteArrayList<IAlarmListener>();
	private static final Logger logger = LoggerFactory.getLogger(AbstractCriteriaValidator.class);
	private IKnowledge knowledge = null;

	/**
	 * @return the knowledge
	 */
	public IKnowledge getKnowledge() {
		return knowledge;
	}

	/**
	 * @param knowledge
	 *            the knowledge to set
	 */
	public void setKnowledge(IKnowledge knowledge) {
		this.knowledge = knowledge;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.edu.agh.samm.metrics.ICriteriaValidator#addAlarmListener(pl.edu.
	 * agh.samm.common.core.IAlarmListener)
	 */
	@Override
	public void addAlarmListener(IAlarmListener listener) {
		alarmListeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pl.edu.agh.samm.metrics.ICriteriaValidator#removeAlarmListener(pl.edu
	 * .agh.samm.common.core.IAlarmListener)
	 */
	@Override
	public void removeAlarmListener(IAlarmListener listener) {
		alarmListeners.remove(listener);
	}

	protected void fireAlarm(IAlarm alarm) {
		for (IAlarmListener listener : alarmListeners) {
			try {
				listener.handleAlarm(alarm);
			} catch (Exception e) {
				logger.error("Alarm listener failed to handle alarm!", e);
			}
		}
	}

	private ISuggestedMetricsComputationEngine suggestedMetricsComputationEngine = null;

	/**
	 * @return the suggestedMetricsComputationEngine
	 */
	protected ISuggestedMetricsComputationEngine getSuggestedMetricsComputationEngine() {
		return suggestedMetricsComputationEngine;
	}

	/**
	 * @param suggestedMetricsComputationEngine
	 *            the suggestedMetricsComputationEngine to set
	 */
	public void setSuggestedMetricsComputationEngine(
			ISuggestedMetricsComputationEngine suggestedMetricsComputationEngine) {
		this.suggestedMetricsComputationEngine = suggestedMetricsComputationEngine;
	}

	protected Map<IConfiguredMetric, Number> getMetricsSuggestedToStart(IConfiguredMetric metric) {
		return this.suggestedMetricsComputationEngine.getMetricsSuggestedToStart(metric);
	}

	protected String getDescriptionForAlarm(IConfiguredMetric metric, ICriterion criterion) {
		return "The value of metric: " + metric.getMetricURI() + " observed on: " + metric.getResourceURI()
				+ " violates the criterion:\n" + criterion.toString();
	}
}
