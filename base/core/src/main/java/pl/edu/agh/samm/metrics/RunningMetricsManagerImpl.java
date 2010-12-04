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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.samm.common.core.IKnowledgeProvider;
import pl.edu.agh.samm.common.core.IResourceInstancesManager;
import pl.edu.agh.samm.common.core.Resource;
import pl.edu.agh.samm.common.knowledge.IKnowledge;
import pl.edu.agh.samm.common.metrics.IConfiguredMetric;
import pl.edu.agh.samm.common.metrics.IMetricListener;
import pl.edu.agh.samm.common.metrics.IMetricsManagerListener;
import pl.edu.agh.samm.common.metrics.MetricNotRunningException;

/**
 * Manages a set of running metrics (objects containing descriptor of resource
 * being monitored and with methods used to get monitored data out of monitoring
 * system).
 * 
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 */
public class RunningMetricsManagerImpl implements IMetricsManager, IMetricProblemObserver {

	private final Logger logger = LoggerFactory.getLogger(RunningMetricsManagerImpl.class);

	private IKnowledgeProvider knowledgeProvider = null;
	private IResourceInstancesManager resourceInstancesManager = null;
	private List<IMetricsManagerListener> metricManagerListeners = new CopyOnWriteArrayList<IMetricsManagerListener>();
	private Map<IConfiguredMetric, MetricTask> scheduledTasks = new HashMap<IConfiguredMetric, MetricTask>();
	private ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(10);

	private Map<IConfiguredMetric, ScheduledFuture<?>> scheduledFutures = new HashMap<IConfiguredMetric, ScheduledFuture<?>>();

	public IKnowledgeProvider getKnowledgeProvider() {
		return knowledgeProvider;
	}

	public void setKnowledgeProvider(IKnowledgeProvider knowledgeProvider) {
		this.knowledgeProvider = knowledgeProvider;
	}

	public IResourceInstancesManager getResourceInstancesManager() {
		return resourceInstancesManager;
	}

	public void setResourceInstancesManager(IResourceInstancesManager resourceInstancesManager) {
		this.resourceInstancesManager = resourceInstancesManager;
	}

	@Override
	public Collection<IConfiguredMetric> getRunningMetrics() {
		return scheduledTasks.keySet();
	}

	@Override
	public void addMetricsManagerListener(IMetricsManagerListener listener) {
		if (!metricManagerListeners.contains(listener)) {
			metricManagerListeners.add(listener);
			if (scheduledTasks.size() > 0) {
				try {
					listener.notifyNewMetricsStarted(new HashSet<IConfiguredMetric>(scheduledTasks.keySet()));
				} catch (Exception e) {
					logger.error("Metric Manager Listener thrown an exception!", e);
				}
			}
		}
	}

	@Override
	public void removeMetricsManagerListener(IMetricsManagerListener listener) {
		metricManagerListeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seepl.edu.agh.samm.common.metrics.IRunningMetricsManager#
	 * addRunningMetricListener(pl.edu.agh.samm.common.impl.metrics.IMetric,
	 * pl.edu.agh.samm.common.impl.metrics.IMetricListener)
	 */
	@Override
	public void addMetricListener(IConfiguredMetric metric, IMetricListener listener)
			throws MetricNotRunningException {
		if (scheduledTasks.containsKey(metric)) {
			MetricTask metricTask = scheduledTasks.get(metric);
			metricTask.addMetricListener(listener);
		} else {
			throw new MetricNotRunningException(metric.getMetricURI());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seepl.edu.agh.samm.common.metrics.IRunningMetricsManager#
	 * removeRunningMetricListener (pl.edu.agh.samm.common.impl.metrics.IMetric,
	 * pl.edu.agh.samm.common.impl.metrics.IMetricListener)
	 */
	@Override
	public void removeMetricListener(IConfiguredMetric metric, IMetricListener listener) {
		if (scheduledTasks.containsKey(metric)) {
			MetricTask metricTask = scheduledTasks.get(metric);
			metricTask.removeMetricListener(listener);
		}
	}

	protected void fireNewMetricHasStarted(IConfiguredMetric metric) {
		Collection<IConfiguredMetric> collection = new ArrayList<IConfiguredMetric>();
		collection.add(metric);
		for (IMetricsManagerListener listener : metricManagerListeners) {
			try {
				listener.notifyNewMetricsStarted(collection);
			} catch (Exception e) {
				logger.error("Metric Manager Listener thrown an exception!", e);
			}
		}
	}

	protected void fireMetricHasStopped(IConfiguredMetric metric) {
		Collection<IConfiguredMetric> collection = new ArrayList<IConfiguredMetric>();
		collection.add(metric);
		for (IMetricsManagerListener listener : metricManagerListeners) {
			try {
				listener.notifyMetricsHasStopped(collection);
			} catch (Exception e) {
				logger.error("Metric Manager Listener thrown an exception!", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.edu.agh.samm.common.impl.metrics.IMetricsManager#startMetric(pl
	 * .edu.agh.samm.common.metrics.IRunningMetric)
	 */
	@Override
	public void startMetric(IConfiguredMetric runningMetric) {
		startMetricAndAddRunningMetricListener(runningMetric, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.edu.agh.samm.common.impl.metrics.IMetricsManager#stopMetric(pl
	 * .edu.agh.samm.common.metrics.IRunningMetric)
	 */
	@Override
	public void stopMetric(IConfiguredMetric metric) {
		logger.info("Stopping metric: " + metric);
		if (scheduledTasks.containsKey(metric)) {
			scheduledTasks.remove(metric);
			ScheduledFuture<?> removedFuture = scheduledFutures.remove(metric);
			removedFuture.cancel(false);

			fireMetricHasStopped(metric);
		}
	}

	@Override
	public boolean isMetricRunning(IConfiguredMetric metric) {
		return scheduledTasks.containsKey(metric);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seepl.edu.agh.samm.common.metrics.IRunningMetricsManager#
	 * startMetricAndAddRunningMetricListener
	 * (pl.edu.agh.samm.common.impl.metrics.IMetric, java.util.Collection)
	 */
	@Override
	public void startMetricAndAddRunningMetricListener(IConfiguredMetric metric,
			Collection<IMetricListener> listeners) {
		logger.info("Starting metric: " + metric);
		MetricTask task = null;
		if (!scheduledTasks.containsKey(metric)) {
			IKnowledge knowledge = knowledgeProvider.getDefaultKnowledgeSource();
			List<String> usedCapabilities = knowledge.getUsedCapabilities(metric.getMetricURI());
			Resource resource = resourceInstancesManager.getResourceForURI(metric.getResourceURI());
			if (knowledge.isCustomMetric(metric.getMetricURI())) {
				String customClassName = knowledge.getClassNameForCustomMetric(metric.getMetricURI());
				task = new CustomMetricTask(metric, usedCapabilities, resource, customClassName);
			} else {
				task = new SingleCapabilityMetricTask(metric, usedCapabilities, resource);
			}
			task.init();

		} else {
			task = scheduledTasks.get(metric);
		}

		if (listeners != null) {
			for (IMetricListener listener : listeners) {
				task.addMetricListener(listener);
			}
		}

		if (!scheduledTasks.containsKey(metric)) {
			task.setProblemObserver(this);
			ScheduledFuture<?> future = scheduledExecutorService.scheduleAtFixedRate(task, 0L,
					metric.getMetricPollTimeInterval(), TimeUnit.MILLISECONDS);
			scheduledTasks.put(metric, task);
			scheduledFutures.put(metric, future);

			fireNewMetricHasStarted(metric);
		}
	}

	@Override
	public void updateMetricPollTime(IConfiguredMetric metric) throws MetricNotRunningException {
		stopMetric(metric);
		startMetric(metric);
	}

	@Override
	public void problemOcurred(IConfiguredMetric metric, Exception e) {
		// we don't care what kind of exception was thrown right now - just kill
		// the metric
		stopMetric(metric);
	}

}
