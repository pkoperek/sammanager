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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.samm.api.metrics.IMetric;
import pl.edu.agh.samm.api.metrics.IMetricEvent;
import pl.edu.agh.samm.api.sla.IServiceLevelAgreement;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class CostEvaluationMetricListener implements ICurrentCostEvaluator {

	private static Logger logger = LoggerFactory
			.getLogger(CostEvaluationMetricListener.class);

	private Map<IMetric, Double> actualValues = Collections
			.synchronizedMap(new HashMap<IMetric, Double>());
	private IServiceLevelAgreement serviceLevelAgreement = null;

	private ScheduledExecutorService executor = Executors
			.newSingleThreadScheduledExecutor();

	public void init() {
		executor.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				logger.info("Current cost of running system: "
						+ getCurrentCostEvaluation());

			}

		}, 0, 20, TimeUnit.SECONDS);
	}

	/**
	 * @return the serviceLevelAgreement
	 */
	public IServiceLevelAgreement getServiceLevelAgreement() {
		return serviceLevelAgreement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.edu.agh.samm.core.ICurrentCostEvaluator#setServiceLevelAgreement
	 * (pl.edu.agh.samm.api.sla.IServiceLevelAgreement)
	 */
	public void setServiceLevelAgreement(
			IServiceLevelAgreement serviceLevelAgreement) {
		this.serviceLevelAgreement = serviceLevelAgreement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pl.edu.agh.samm.core.ICurrentCostEvaluator#getCurrentCostEvaluation()
	 */
	@Override
	public synchronized Number getCurrentCostEvaluation() {
		Double sum = 0.0;
		for (Double value : actualValues.values()) {
			sum += value;
		}
		return sum;
	}

	@Override
	public synchronized void setupSLA(
			IServiceLevelAgreement serviceLevelAgreement) {
		actualValues.clear();
		this.serviceLevelAgreement = serviceLevelAgreement;
	}

	@Override
	public void processMetricEvent(IMetricEvent metricEvent) throws Exception {
		if (serviceLevelAgreement != null) {
			String metricURI = metricEvent.getMetric().getMetricURI();
			String resourceURI = metricEvent.getMetric().getResourceURI();

			for (String pattern : serviceLevelAgreement.getInvolvedPatterns()) {
				if (Pattern.matches(pattern, resourceURI)) {
					Number multiplier = serviceLevelAgreement.getMetricCost(
							pattern, metricURI);

					if (multiplier != null) {
						Double valueToStore = multiplier.doubleValue()
								* metricEvent.getValue().doubleValue();

						actualValues.put(metricEvent.getMetric(), valueToStore);
					}
					break;
				}
			}
		}
	}

}
