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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.samm.common.action.Action;
import pl.edu.agh.samm.common.action.ActionExecution;
import pl.edu.agh.samm.common.core.ILearningStageListener;
import pl.edu.agh.samm.common.core.IResourceEvent;
import pl.edu.agh.samm.common.core.IResourceListener;
import pl.edu.agh.samm.common.core.Resource;
import pl.edu.agh.samm.common.db.IStorageService;
import pl.edu.agh.samm.common.knowledge.IKnowledge;
import pl.edu.agh.samm.common.metrics.IConfiguredMetric;
import pl.edu.agh.samm.common.metrics.ResourceEventType;
import pl.edu.agh.samm.metrics.IMetricsManager;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class ExperimentatiorImpl extends CommonActionOperations implements IExperimentator, Runnable,
		IResourceListener {

	/**
	 * Number of seconds after which consequences will be recomputed
	 */
	private static final long CONSEQUENCES_RECOMPUTATION_DELAY = 60 * 5;

	private static final Logger logger = LoggerFactory.getLogger(ExperimentatiorImpl.class);

	/**
	 * Number of seconds after which, after the action finishes, measurements
	 * are taken to be considered "consequence"
	 */
	static final long CONSEQUENCE_DELAY = 60 * 2;

	/**
	 * Number of seconds that are used to take an average measurement value
	 */
	private static final long WINDOW_WIDTH = 60 * 2;

	private IStorageService storageService;

	private IActionExecutor actionExecutor;

	private IMetricsManager runningMetricsManager;

	private IMetricFactory metricFactory;

	/**
	 * actionURi<->(ResourceURI<->(CapabilityURI<->consequence))
	 */
	private Map<String, Map<String, Map<String, Number>>> consequences = Collections.emptyMap();

	private ReadWriteLock consequencesLock = new ReentrantReadWriteLock();

	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	protected void init() {
		executor.scheduleWithFixedDelay(this, 0, CONSEQUENCES_RECOMPUTATION_DELAY, TimeUnit.SECONDS);
	}

	private Set<IConfiguredMetric> metrics = Collections.synchronizedSet(new HashSet<IConfiguredMetric>());

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pl.edu.agh.samm.core.IExperimentator#getActionConsequences(java.lang.
	 * String)
	 */
	@Override
	public Map<String, Map<String, Number>> getActionConsequences(String actionURI) {
		consequencesLock.readLock().lock();
		try {
			if (consequences.containsKey(actionURI)) {
				return Collections.unmodifiableMap(consequences.get(actionURI));
			} else {
				return Collections.emptyMap();
			}
		} finally {
			consequencesLock.readLock().unlock();
		}
	}

	@Override
	public void run() {
		try {
			Map<String, Map<String, Map<String, Number>>> localConsequences = new HashMap<String, Map<String, Map<String, Number>>>();
			Map<String, List<ActionExecution>> actionExecutions = storageService.getAllActionExecutions();

			// grupowanie akcji po actionUri
			for (String actionUri : actionExecutions.keySet()) {
				logger.debug("Calculating consequences for action " + actionUri);
				Map<String, Map<String, List<Number>>> singleActionConsequences = new HashMap<String, Map<String, List<Number>>>();
				List<ActionExecution> executions = actionExecutions.get(actionUri);

				// for each execution of action with given actionUri
				for (ActionExecution action : executions) {
					localConsequences.put(actionUri, new HashMap<String, Map<String, Number>>());
					Date actionStartTime = action.getStartTime();
					Date actionEndTime = action.getEndTime();
					Date consequenceStartTime = new Date(actionEndTime.getTime() + CONSEQUENCE_DELAY * 1000);
					Date consequenceEndTIme = new Date(consequenceStartTime.getTime() + WINDOW_WIDTH * 1000);
					Set<String> knownResources = storageService.getKnownResources(actionStartTime,
							consequenceStartTime, WINDOW_WIDTH * 1000);
					// for (String parameterUri : action.getAction()
					// .getParameterValues().keySet()) {
					// String resource = action.getAction()
					// .getParameterValues().get(parameterUri);
					// for each resources that was known during action
					// execution
					for (String resource : knownResources) {
						Date beforeActionStartTime = new Date(actionStartTime.getTime() - WINDOW_WIDTH * 1000);

						Map<String, Number> beforeActionMeasurementValues = storageService
								.getAverageMeasurementValue(resource, beforeActionStartTime, actionStartTime);
						Map<String, Number> afterActionMeasurementValues = storageService
								.getAverageMeasurementValue(resource, consequenceStartTime,
										consequenceEndTIme);

						for (String capabilityUri : beforeActionMeasurementValues.keySet()) {
							Number preActionAverage = beforeActionMeasurementValues.get(capabilityUri);
							Number postActionAverage = afterActionMeasurementValues.get(capabilityUri);
							if (postActionAverage == null) {
								logger.warn("Capability " + capabilityUri
										+ " does not have stored values after action execution " + action);
								continue;
							}

							if (!singleActionConsequences.containsKey(resource)) {
								singleActionConsequences.put(resource, new HashMap<String, List<Number>>());
							}

							Map<String, List<Number>> capToConsequences = singleActionConsequences
									.get(resource);

							if (!capToConsequences.containsKey(capabilityUri)) {
								capToConsequences.put(capabilityUri, new LinkedList<Number>());
							}

							List<Number> conseq = capToConsequences.get(capabilityUri);

							conseq.add(postActionAverage.doubleValue() - preActionAverage.doubleValue());
						}

					}

				}
				for (String resource : singleActionConsequences.keySet()) {
					Map<String, List<Number>> capToList = singleActionConsequences.get(resource);
					for (Map.Entry<String, List<Number>> cap : capToList.entrySet()) {
						List<Number> consList = capToList.get(cap.getKey());
						Double avg = average(consList);

						if (!localConsequences.get(actionUri).containsKey(resource)) {
							localConsequences.get(actionUri).put(resource, new HashMap<String, Number>());
						}
						Map<String, Number> map = localConsequences.get(actionUri).get(resource);
						map.put(cap.getKey(), avg);
					}
				}
			}

			consequencesLock.writeLock().lock();
			try {
				consequences = localConsequences;
				logger.debug("Finished calculating consequences=" + consequences);
			} finally {
				consequencesLock.writeLock().unlock();
			}

		} catch (Exception e) {
			logger.error("Error while recumputing consequences", e);
		}
	}

	private double average(List<Number> consList) {
		Double sum = 0.0;
		for (Number elem : consList) {
			sum += elem.doubleValue();
		}

		return sum / consList.size();
	}

	@Override
	public void executeInitialLearning(final ILearningStageListener finishListener) {
		resourceInstancesManager.addResourceListener(this);
		startAllMetrics();

		Thread th = new Thread(new AllActionsRunnable(finishListener));
		th.start();
	}

	private void stopAllMetrics() {
		Iterator<IConfiguredMetric> iterator = metrics.iterator();
		while (iterator.hasNext()) {
			IConfiguredMetric metric = iterator.next();
			runningMetricsManager.stopMetric(metric);
			iterator.remove();
		}
	}

	private void startAllMetrics() {
		for (String resource : resourceInstancesManager.getAllRegisteredResources()) {
			startAllMetricsForResource(resource);
		}
	}

	private void startAllMetricsForResource(String resource) {
		IKnowledge knowledge = knowledgeProvider.getDefaultKnowledgeSource();
		Set<String> metricsToStart = knowledge.getMetricsForResourceType(resourceInstancesManager
				.getResourceType(resource));
		for (String metricUri : metricsToStart) {
			IConfiguredMetric metric = metricFactory.createMetric(metricUri, resource);
			try {
				runningMetricsManager.startMetric(metric);
				metrics.add(metric);
			} catch (Exception e) {
				logger.warn("Faild to run metric " + metric, e);
			}
		}
	}

	private class AllActionsRunnable implements Runnable {

		private ILearningStageListener endListener;

		public AllActionsRunnable(ILearningStageListener finishListener) {
			this.endListener = finishListener;
		}

		@Override
		public void run() {
			visitAllPossibleActionsWithParameters(new IActionVisitor() {

				@Override
				public void visitAction(Action act) {
					try {
						endListener.preActionWaiting();
						Thread.sleep(WINDOW_WIDTH * 1000);
						endListener.taskDone();

						endListener.startedNewAction(act.getActionURI());
						actionExecutor.executeRequest(act, true);
						endListener.taskDone();
						// after the execution we have to wait for some seconds
						// to
						// make the actions consequences stable
						try {
							endListener.waiting();
							Thread.sleep(CONSEQUENCE_DELAY * 1000);
							endListener.taskDone();
						} catch (InterruptedException e) {
							logger.warn("Thread got interupted before sleeping for " + CONSEQUENCE_DELAY
									+ "s to make consequences reliable", e);
						}

						endListener.postActionWaiting();
						Thread.sleep(WINDOW_WIDTH * 1000);
						endListener.taskDone();

					} catch (Exception e) {
						logger.error("Error on learning listener", e);
					}
				}
			});

			stopAllMetrics();
			resourceInstancesManager.removeResourceListener(ExperimentatiorImpl.this);

			try {
				endListener.learninegStageFinished();
			} catch (Exception e) {
				logger.warn("EndListener failed", e);
			}
		}

	}

	/**
	 * @param actionExecutor
	 *            the actionExecutor to set
	 */
	public void setActionExecutor(IActionExecutor actionExecutor) {
		this.actionExecutor = actionExecutor;
	}

	/**
	 * @param storageService
	 *            the storageService to set
	 */
	public void setStorageService(IStorageService storageService) {
		this.storageService = storageService;
	}

	@Override
	public void processEvent(IResourceEvent event) throws Exception {
		if (event.getType().equals(ResourceEventType.RESOURCES_ADDED)) {
			Resource resource = (Resource) event.getAttachment();
			startAllMetricsForResource(resource.getUri());
		}

	}

	/**
	 * @param runningMetricsManager
	 *            the runningMetricsManager to set
	 */
	public void setRunningMetricsManager(IMetricsManager runningMetricsManager) {
		this.runningMetricsManager = runningMetricsManager;
	}

	/**
	 * @param metricFactory
	 *            the metricFactory to set
	 */
	public void setMetricFactory(IMetricFactory metricFactory) {
		this.metricFactory = metricFactory;
	}

}
