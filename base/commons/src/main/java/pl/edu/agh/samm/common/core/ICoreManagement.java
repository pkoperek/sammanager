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

package pl.edu.agh.samm.common.core;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import pl.edu.agh.samm.common.metrics.IMetric;
import pl.edu.agh.samm.common.metrics.IMetricListener;
import pl.edu.agh.samm.common.metrics.IMetricsManagerListener;
import pl.edu.agh.samm.common.metrics.MetricNotRunningException;
import pl.edu.agh.samm.common.sla.IServiceLevelAgreement;
import pl.edu.agh.samm.common.sla.ServiceLevelAgreement;

/**
 * Interface for managing the Core component
 * 
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public interface ICoreManagement {

	// SLA
	/**
	 * Updates {@link ServiceLevelAgreement}
	 */
	void updateSLA(IServiceLevelAgreement serviceLevelAgreement);

	/**
	 * Start Service Level Agreement validation with given
	 * {@link ServiceLevelAgreement}
	 * 
	 * @param serviceLevelAgreement
	 *            defined Service Level Agreement
	 * @throws SLAException
	 *             thrown when SLA validation is already running
	 */
	void startSLAValidation(IServiceLevelAgreement serviceLevelAgreement)
			throws SLAException;

	/**
	 * Stops Service Level Agreement validation
	 * 
	 * @throws SLAException
	 *             thrown if SLA validation isn't running
	 */
	void stopSLAValidation() throws SLAException;

	/**
	 * Retrieves current Service Level Agreement
	 * 
	 * @return Service Level Agreement
	 * @throws SLAException
	 *             thrown when no SLA is defined
	 */
	IServiceLevelAgreement retrieveCurrentSLA() throws SLAException;

	/**
	 * Check is SLA validation is active
	 * 
	 * @return true when SLA validation is running, false otherwise
	 */
	boolean isSLAValidationRunning();

	/**
	 * Checks if resource with given URI is known to Core
	 * 
	 * @param uri
	 *            URI of resource
	 * @return true when resource is registered to Core
	 */
	boolean isResourceRegistered(String uri);

	/**
	 * Retrieves URI's of all known resources
	 * 
	 * @return Collection of URI's of registered resources
	 */
	Collection<String> getAllRegisteredResources();

	/**
	 * Get's URI describing type of resource instance
	 * 
	 * @param uri
	 *            URI of known resource
	 * @return URI of the resource's type
	 */
	String getResourceType(String uri);

	/**
	 * Get's URI's of capabilities which given resource instance has
	 * 
	 * @param uri
	 *            URI of resource instance
	 * @return List of resource's capabilities
	 * @throws ResourceNotRegisteredException
	 *             when resource instance is not registered to Core
	 */
	List<String> getResourceCapabilities(String uri)
			throws ResourceNotRegisteredException;

	// resource changes - node attachment/ontology changes

	/**
	 * Add's listener for resource changes (eg. resources added or removed)
	 */
	void addResourceListener(IResourceListener listener);

	/**
	 * Removes resources listener
	 */
	void removeResourceListener(IResourceListener listener);

	/**
	 * Starts (starts polling data) monitoring given metric
	 */
	void startMetric(IMetric metric);

	/**
	 * Starts a metric (starts to poll data) and adds listeners for that metric
	 * value changes
	 * 
	 * @param runningMetric
	 *            metric to start
	 * @param listeners
	 *            Collection of listener which will be notified when new metric
	 *            value is computed
	 */
	void startMetricAndAddRunningMetricListener(IMetric runningMetric,
			Collection<IMetricListener> listeners);

	/**
	 * Starts a metric (starts to poll data) and adds listener for that metric
	 * value changes
	 * 
	 * @param runningMetric
	 *            metric to start
	 * @param listener
	 *            listener which will be notified when new metric value is
	 *            computed
	 */
	void startMetricAndAddRunningMetricListener(IMetric runningMetric,
			IMetricListener listener);

	/**
	 * Check's if given metric is started
	 * 
	 * @param metric
	 *            metric to check if is running
	 * @return true wehn metric is started, false otherwise
	 */
	boolean isMetricRunning(IMetric metric);

	/**
	 * Stops computing value of a metric
	 * 
	 * @param metric
	 *            metric to stop
	 */
	void stopMetric(IMetric metric);

	/**
	 * Updates metric's polling time. New polling time should be set in the
	 * metric parameter
	 * 
	 * @param metric
	 *            metric with new polling time
	 * @throws MetricNotRunningException
	 *             thrown when given metric is not running
	 */
	void updateMetricPollTimeInterval(IMetric metric)
			throws MetricNotRunningException;

	/**
	 * Adds listener when given metric value changes
	 * 
	 * @param metric
	 *            metric for which add the listener to
	 * @param listener
	 *            listener which will be notified
	 * @throws MetricNotRunningException
	 *             thrown when given metric is not running
	 */
	void addRunningMetricListener(IMetric metric, IMetricListener listener)
			throws MetricNotRunningException;

	/**
	 * Removes listener from metric
	 * 
	 * @param metric
	 *            metric from which remove the listener
	 * @param listener
	 *            listener to remove
	 */
	void removeRunningMetricListener(IMetric metric, IMetricListener listener);

	/**
	 * Adds a listener which will be notified when new metric is started or
	 * running metric is stopped
	 * 
	 * @param listener
	 */
	void addRunningMetricsManagerListener(IMetricsManagerListener listener);

	/**
	 * Removes listener which was added with addRunningMetricsManagerListener
	 * 
	 * @param listener
	 */
	void removeRunningMetricsManagerListener(IMetricsManagerListener listener);

	/**
	 * Create's metric with given URI for resource with given URI
	 * 
	 * @param metricURI
	 *            URI of metric
	 * @param resourceURI
	 *            resource instance URI
	 * @return metric
	 */
	IMetric createMetricInstance(String metricURI, String resourceURI);

	/**
	 * Registers new resource with given URI with given type and passes
	 * parameters to Transport Adapters
	 * 
	 * @param uri
	 *            URI with which resource is going to be registered with
	 * @param type
	 *            URI of resource's type
	 * @param parameters
	 *            parameters passed to Transport Adapters
	 * @throws ResourceAlreadyRegisteredException
	 *             thrown when resource with given URI already exists
	 */
	void registerResource(Resource resource)
			throws ResourceAlreadyRegisteredException;

	/**
	 * Unregisters resource of given URI
	 * 
	 * @param uri
	 */
	void unregisterResource(String uri);

	/**
	 * Adds a listener which will be notified when threshold value is exceeded
	 * 
	 * @param listener
	 *            listener to add
	 */
	void addAlarmListener(IAlarmListener listener);

	/**
	 * Removes alarm listener
	 * 
	 * @param listener
	 *            listener to remove
	 */
	void removeAlarmListener(IAlarmListener listener);

	/**
	 * Adds new/overrides existing resource parameters
	 * 
	 * @param uri
	 *            URI of resource instance
	 * @param parameters
	 *            parameters to add to existing parameters
	 * @throws ResourceNotRegisteredException
	 *             thrown when resource of given URI is not registered
	 */
	void addResourceParameters(String uri, Map<String, Object> parameters)
			throws ResourceNotRegisteredException;

	/**
	 * Adds a listener for action executions - listener will be notified each
	 * time an action is taken by SAMM
	 * 
	 * @param listener
	 *            Listener to be added
	 */
	void addActionExecutorListener(IActionExecutionListener listener);

	/**
	 * Removes a listener for action executions
	 * 
	 * @param listener
	 *            Listener to be removed
	 */
	void removeActionExecutorListener(IActionExecutionListener listener);

	void addRule(Rule rule);

	void clearRules();

	void removeRule(String ruleName);
}
