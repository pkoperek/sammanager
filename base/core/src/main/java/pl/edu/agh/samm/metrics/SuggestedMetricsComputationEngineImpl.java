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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.math.stat.correlation.PearsonsCorrelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.samm.common.db.IStorageService;
import pl.edu.agh.samm.common.impl.CombinationGenerator;
import pl.edu.agh.samm.common.metrics.IConfiguredMetric;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class SuggestedMetricsComputationEngineImpl implements ISuggestedMetricsComputationEngine, Runnable {

	private static final int SUGGESTED_METRICS_COUNT = 3;

	private static final Logger log = LoggerFactory.getLogger(SuggestedMetricsComputationEngineImpl.class);

	private Map<IConfiguredMetric, Map<IConfiguredMetric, Number>> suggestionCache = new HashMap<IConfiguredMetric, Map<IConfiguredMetric, Number>>();
	private Map<IConfiguredMetric, SortedSet<MetricWithCorrelation>> metricsWithCorrelation = new HashMap<IConfiguredMetric, SortedSet<MetricWithCorrelation>>();

	private ReadWriteLock cacheLock = new ReentrantReadWriteLock();

	private ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);

	private IStorageService storageService;

	/**
	 * in minutes
	 */
	private long cacheRefreshRate;

	private static double[] convertValues(List<Number> values, int count) {
		double ret[] = new double[count];
		int index = 0;
		for (Number value : values) {
			ret[index++] = value.doubleValue();
			if (index == count)
				break;
		}
		return ret;
	}

	public void init() {
		scheduledExecutorService.scheduleWithFixedDelay(this, 0, cacheRefreshRate, TimeUnit.MINUTES);
	}

	public void destroy() {
		scheduledExecutorService.shutdownNow();
	}

	@Override
	public Map<IConfiguredMetric, Number> getMetricsSuggestedToStart(IConfiguredMetric metric) {
		cacheLock.readLock().lock();
		try {
			Map<IConfiguredMetric, Number> suggestion = suggestionCache.get(metric);
			return suggestion;
		} finally {
			cacheLock.readLock().unlock();
		}
	}

	@Override
	public void run() {
		try {
			metricsWithCorrelation.clear();

			List<IConfiguredMetric> allMetrics = storageService.getAllKnownMetrics();

			IConfiguredMetric[] metrics = allMetrics.toArray(new IConfiguredMetric[0]);

			if (metrics.length < 2) {
				return;
			}
			if (metrics.length > 1) {
				// correlation relation is symmetrical
				// we will count correlation between all pairs
				CombinationGenerator combinationGenerator = new CombinationGenerator(metrics.length, 2);
				while (combinationGenerator.hasMore()) {
					int[] combination = combinationGenerator.getNext();
					IConfiguredMetric metricOne = metrics[combination[0]];
					IConfiguredMetric metricTwo = metrics[combination[1]];

					// get historical data
					List<Number> metricOneValues = storageService.getHistoricalMetricValues(
							metricOne.getMetricURI(), metricOne.getResourceURI());
					List<Number> metricTwoValues = storageService.getHistoricalMetricValues(
							metricTwo.getMetricURI(), metricTwo.getResourceURI());
					int min = metricOneValues.size() > metricTwoValues.size() ? metricTwoValues.size()
							: metricOneValues.size();

					PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation();
					double correlation = pearsonsCorrelation.correlation(convertValues(metricOneValues, min),
							convertValues(metricTwoValues, min));
					if (!Double.isNaN(correlation)) {
						addCorrelation(metricOne, metricTwo, correlation);
						addCorrelation(metricTwo, metricOne, correlation);
					}
				}
			}
			updateCache();
		} catch (Exception e) {
			log.error("Error during computing correlation", e);
		}

	}

	private void updateCache() {
		cacheLock.writeLock().lock();
		try {
			suggestionCache.clear();
			for (IConfiguredMetric metric : metricsWithCorrelation.keySet()) {
				SortedSet<MetricWithCorrelation> suggestedMetricsWithCorrelation = metricsWithCorrelation
						.get(metric);
				Map<IConfiguredMetric, Number> suggestion = new HashMap<IConfiguredMetric, Number>();
				for (MetricWithCorrelation suggestedMetric : suggestedMetricsWithCorrelation) {
					suggestion.put(suggestedMetric.getCorrelatingMetric(), suggestedMetric.getCorrelation());
				}
				suggestionCache.put(metric, suggestion);
			}
		} finally {
			cacheLock.writeLock().unlock();
		}

	}

	private void addCorrelation(IConfiguredMetric metricOne, IConfiguredMetric metricTwo, double correlation) {
		if (!metricsWithCorrelation.containsKey(metricOne)) {
			metricsWithCorrelation.put(metricOne, new TreeSet<MetricWithCorrelation>());
		}
		SortedSet<MetricWithCorrelation> set = metricsWithCorrelation.get(metricOne);
		set.add(new MetricWithCorrelation(metricOne, metricTwo, correlation));
		if (set.size() > SUGGESTED_METRICS_COUNT) {
			set.remove(set.first());
		}
	}

	/**
	 * @param storageService
	 *            the storageService to set
	 */
	public void setStorageService(IStorageService storageService) {
		this.storageService = storageService;
	}

	public void setCacheRefreshRate(long cacheRefreshRate) {
		this.cacheRefreshRate = cacheRefreshRate;
	}
}
