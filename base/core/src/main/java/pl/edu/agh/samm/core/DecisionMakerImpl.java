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
package pl.edu.agh.samm.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.samm.common.action.Action;
import pl.edu.agh.samm.common.core.IKnowledgeProvider;
import pl.edu.agh.samm.common.decision.IServiceLevelAgreement;
import pl.edu.agh.samm.common.estimation.IEstimator;
import pl.edu.agh.samm.common.knowledge.IKnowledge;
import pl.edu.agh.samm.common.metrics.ICustomMetric;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class DecisionMakerImpl extends CommonActionOperations implements IDecisionMaker {

	/**
	 * Default value of delay between executions of consideration action (in
	 * seconds)
	 */
	public static final long DEFAULT_RECONSIDERATION_DELAY = 300;
	public static final int THREAD_POOL_SIZE = 1;
	private static final Logger logger = LoggerFactory.getLogger(DecisionMakerImpl.class);

	private IServiceLevelAgreement serviceLevelAgreement = null;
	private ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(
			THREAD_POOL_SIZE);
	private IEstimator estimator = null;
	// private IKnowledgeProvider knowledgeProvider = null;
	// private IResourceInstancesManager resourceInstancesManager = null;
	private IActionExecutor actionExecutor = null;
	private IExperimentator experimentator = null;

	/**
	 * Delay between executions of consideration action (in seconds)
	 */
	private long considerationDelay = DEFAULT_RECONSIDERATION_DELAY;

	/**
	 * The default minimal cost decrease which is worth consideration
	 */
	public static final long DEFAULT_DIFFERENCE_MINIMUM = 1;
	private double differenceMinimum = DEFAULT_DIFFERENCE_MINIMUM;

	public void init() {
		logger.debug("Decision Maker starting...");

		Runnable considerationAction = new Runnable() {
			@Override
			public void run() {
				logger.debug("Consideration action: started");
				Action actionToExecute = DecisionMakerImpl.this.findActionToExecute();
				logger.debug("Action to execute: " + actionToExecute);
				if (actionToExecute != null) {
					actionExecutor.executeRequest(actionToExecute);
				}
				logger.debug("Consideration action: finished");
			}
		};

		scheduledExecutorService.scheduleAtFixedRate(considerationAction, 0, considerationDelay,
				TimeUnit.SECONDS);

		logger.debug("Decision Maker started...");
	}

	/**
	 * @return the actionExecutor
	 */
	public IActionExecutor getActionExecutor() {
		return actionExecutor;
	}

	/**
	 * @param actionExecutor
	 *            the actionExecutor to set
	 */
	public void setActionExecutor(IActionExecutor actionExecutor) {
		this.actionExecutor = actionExecutor;
	}

	/**
	 * @return the knowledgeProvider
	 */
	public IKnowledgeProvider getKnowledgeProvider() {
		return knowledgeProvider;
	}

	/**
	 * @param knowledgeProvider
	 *            the knowledgeProvider to set
	 */
	@Override
	public void setKnowledgeProvider(IKnowledgeProvider knowledgeProvider) {
		this.knowledgeProvider = knowledgeProvider;
	}

	/**
	 * @return the experimentator
	 */
	public IExperimentator getExperimentator() {
		return experimentator;
	}

	/**
	 * @param experimentator
	 *            the experimentator to set
	 */
	public void setExperimentator(IExperimentator experimentator) {
		this.experimentator = experimentator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.edu.agh.samm.sla.impl.ICostEvaluator#setupSLA(pl.edu.agh.samm.
	 * common.decision.IServiceLevelAgreement)
	 */
	@Override
	public void setupSLA(IServiceLevelAgreement serviceLevelAgreement) {
		this.serviceLevelAgreement = serviceLevelAgreement;
	}

	/**
	 * @return the estimator
	 */
	public IEstimator getEstimator() {
		return estimator;
	}

	/**
	 * @param estimator
	 *            the estimator to set
	 */
	public void setEstimator(IEstimator estimator) {
		this.estimator = estimator;
	}

	private Action findActionToExecute() {

		final Action actionToExecute = new Action();

		// get an estimate of measurements after next
		// DEFAULT_RECONSIDERATION_DELAY
		final Map<String, Map<String, Number>> estimation = estimator
				.estimateAllMeasurements(DEFAULT_RECONSIDERATION_DELAY);

		// as the minimum cost set the evaluation of the bare estimation
		// (compute the cost when nothing is done ;) )
		final Number minCost = computeCost(estimation);
		logger.debug("Cost of estimated metric values: " + minCost);
		/**
		 * 1. for each action, for which we have parameters 2. get consequences
		 * of action from Experimentator in form of a
		 * Map<String,Map<String,Number>> - (ParameterName -> (capabilityURI ->
		 * correction value - absolute value!)) 3. generate all combinations of
		 * possible parameters (get them from
		 * ResourceInstancesManagerImpl.getResourcesOfType 4. for each
		 * combination - determine which resources are which parameters 5.
		 * substitute in map from point 2 ParameterName with resources from
		 * point 4 6. compute correction with correctEstimation 7. pass
		 * corrected estimations to computeCost - here we have the estimated
		 * cost of action execution
		 */

		visitAllPossibleActionsWithParameters(new IActionVisitor() {

			@Override
			public void visitAction(Action act) {
				// get consequences
				try {
					// logger.debug("Visiting action: " + act);
					Map<String, Map<String, Number>> consequences = experimentator.getActionConsequences(act
							.getActionURI());

					Map<String, Map<String, Number>> correctedEstimation = correctEstimation(estimation,
							consequences);
					// logger.debug("Corrected estimation: " +
					// correctedEstimation);
					Number computedCost = computeCost(correctedEstimation);
					logger.debug("Cost after correction for action " + act + ": " + computedCost);
					// compute the difference between current minimum and just
					// computed situation
					double diff = minCost.doubleValue() - computedCost.doubleValue();
					logger.debug("Cost difference: " + diff);
					// diff has to be > than 0 - so we know that computed cost
					// is
					// actually lower than the minCost
					if (diff > 0 && diff >= differenceMinimum) {
						actionToExecute.setActionURI(act.getActionURI());
						actionToExecute.setParameterValues(act.getParameterValues());
					}
				} catch (Exception e) {
					logger.error(e.toString(), e);
				}
			}
		});

		if (actionToExecute.getActionURI() == null) {
			return null;
		}

		return actionToExecute;
	}

	private Map<String, Map<String, Number>> correctEstimation(Map<String, Map<String, Number>> estimation,
			Map<String, Map<String, Number>> correction) {
		// logger.debug("correctEstimation, parameters: estimation=" +
		// estimation
		// + ", correction=" + correction);
		// returned corrected estimation
		Map<String, Map<String, Number>> correctedEstimation = new HashMap<String, Map<String, Number>>();

		for (String resource : estimation.keySet()) {
			// logger.debug("correcting est. for resource: " + resource);
			Map<String, Number> correctedResourceEstimations = new HashMap<String, Number>();
			Map<String, Number> resourceEstimations = estimation.get(resource);

			// logger.debug("resource estimations: " + resourceEstimations);

			if (correction.containsKey(resource)) {
				// if we have a correction for this resource - apply it
				Map<String, Number> resourceCorrection = correction.get(resource);
				// logger.debug("resourceCorrection: " + resourceCorrection);
				// for each measurement...
				for (String capability : resourceEstimations.keySet()) {
					// logger.debug("capability: " + capability);
					Number newVal = null;

					// ... check if it's corrected ...
					if (resourceCorrection.containsKey(capability)) {
						// logger.debug("resourceCorrection contains capability");
						// if yes - simply add correction value
						newVal = resourceEstimations.get(capability).doubleValue()
								+ resourceCorrection.get(capability).doubleValue();
					} else {
						// ... if no - just copy the old value
						// logger.debug("resourceCorrection doesn't contain capability, using old value");
						newVal = resourceEstimations.get(capability);
					}

					correctedResourceEstimations.put(capability, newVal);
				}
			} else {
				// logger.debug("Coping estimations");
				// if there are no corrections for this resource - simply copy
				// the estimations
				for (String capability : resourceEstimations.keySet()) {
					correctedResourceEstimations.put(capability, resourceEstimations.get(capability));
				}
			}

			correctedEstimation.put(resource, correctedResourceEstimations);
		}

		return correctedEstimation;
	}

	private Number computeCost(Map<String, Map<String, Number>> estimatedValues) {
		IKnowledge knowledge = knowledgeProvider.getDefaultKnowledgeSource();
		Number sum = 0.0;
		if (serviceLevelAgreement == null) {
			logger.warn("No Service Level Agreement, cost = 0");
			return 0;
		}
		List<String> involvedResources = serviceLevelAgreement.getInvolvedPatterns();
		for (String pattern : involvedResources) {
			for (String resourceURI : resourceInstancesManager.getAllRegisteredResources()) {
				if (Pattern.matches(pattern, resourceURI)) {
					List<String> metricsURIs = serviceLevelAgreement.getMetricsForResource(pattern);

					for (String metricURI : metricsURIs) {
						Number metricCost = serviceLevelAgreement.getMetricCost(pattern, metricURI);
						List<String> usedCapabilities = knowledge.getUsedCapabilities(metricURI);

						if (knowledge.isCustomMetric(metricURI)) {
							String customClassName = knowledge.getClassNameForCustomMetric(metricURI);
							Class<?> clazz;
							try {
								// instantiate the class
								clazz = Class.forName(customClassName);

								ICustomMetric customMetricInstance = (ICustomMetric) clazz.newInstance();
								// logger.debug("About to get resourceMeasurements for resourceUri: "
								// + resourceURI);
								// get capabilities -> values for particular
								// resource
								Map<String, Number> resourcesMeasurements = estimatedValues.get(resourceURI);
								// logger.debug("About to compute metric value for parameters: "
								// + resourcesMeasurements);
								// compute custom value
								Number customMetricValue = customMetricInstance
										.computeValue(resourcesMeasurements);
								logger.debug("resource: " + resourceURI + ", metric cost: "
										+ metricCost.doubleValue() + ", metric val: " + customMetricValue);
								// add to sum
								sum = sum.doubleValue() + customMetricValue.doubleValue()
										* metricCost.doubleValue();
							} catch (ClassNotFoundException e) {
								logger.error("No custom implementation found for metric: " + metricURI, e);
							} catch (InstantiationException e) {
								logger.error("Error creation custom metric object: " + metricURI, e);
							} catch (IllegalAccessException e) {
								logger.error(e.toString(), e);
							}
						} else {
							// assuming that non-custom metric has only one
							// capability
							String capabilityURI = usedCapabilities.get(0);
							Map<String, Number> resourceMeasurements = estimatedValues.get(resourceURI);
							Number estimatedMeasurement = resourceMeasurements.get(capabilityURI);

							// add metricCost * estimatedMeasurement - in simple
							// metrics, measurements == metrics
							sum = sum.doubleValue() + metricCost.doubleValue()
									* estimatedMeasurement.doubleValue();
						}

					}

				}
			}

		}

		return sum;
	}

}
