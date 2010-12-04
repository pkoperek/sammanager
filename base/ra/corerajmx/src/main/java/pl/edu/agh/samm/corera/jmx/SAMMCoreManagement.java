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

package pl.edu.agh.samm.corera.jmx;

import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.samm.common.core.IActionExecutionListener;
import pl.edu.agh.samm.common.core.IAlarmListener;
import pl.edu.agh.samm.common.core.ICoreManagement;
import pl.edu.agh.samm.common.core.ILearningStageListener;
import pl.edu.agh.samm.common.core.IResourceListener;
import pl.edu.agh.samm.common.core.ResourceAlreadyRegisteredException;
import pl.edu.agh.samm.common.core.ResourceNotRegisteredException;
import pl.edu.agh.samm.common.core.SLAException;
import pl.edu.agh.samm.common.decision.IServiceLevelAgreement;
import pl.edu.agh.samm.common.metrics.IConfiguredMetric;
import pl.edu.agh.samm.common.metrics.IMetricListener;
import pl.edu.agh.samm.common.metrics.IMetricsManagerListener;
import pl.edu.agh.samm.common.metrics.MetricNotRunningException;

/**
 * Simply delegates all calls to passed Core Management instance
 * 
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class SAMMCoreManagement implements SAMMCoreManagementMBean {
	private ICoreManagement coreManagement;

	public static final String OBJECT_NAME = "pl.edu.agh.samm:type=Management";
	private Logger logger = LoggerFactory.getLogger(SAMMCoreManagement.class.getName());

	/**
	 * @param listener
	 * @see pl.edu.agh.samm.common.core.ICoreManagement#addActionExecutorListener(pl.edu.agh.samm.common.core.IActionExecutionListener)
	 */
	@Override
	public void addActionExecutorListener(IActionExecutionListener listener) {
		coreManagement.addActionExecutorListener(listener);
	}

	/**
	 * @param listener
	 * @see pl.edu.agh.samm.common.core.ICoreManagement#removeActionExecutorListener(pl.edu.agh.samm.common.core.IActionExecutionListener)
	 */
	@Override
	public void removeActionExecutorListener(IActionExecutionListener listener) {
		coreManagement.removeActionExecutorListener(listener);
	}

	/**
	 * @return
	 * @throws SLAException
	 * @see pl.edu.agh.samm.common.core.ICoreManagement#retrieveCurrentSLA()
	 */
	@Override
	public IServiceLevelAgreement retrieveCurrentSLA() throws SLAException {
		return coreManagement.retrieveCurrentSLA();
	}

	/**
	 * @param uri
	 * @see pl.edu.agh.samm.common.core.ICoreManagement#unregisterResource(java.lang.String)
	 */
	@Override
	public void unregisterResource(String uri) {
		coreManagement.unregisterResource(uri);
	}

	/**
	 * @param serviceLevelAgreement
	 * @see pl.edu.agh.samm.common.core.ICoreManagement#updateSLA(pl.edu.agh.samm.common.decision.IServiceLevelAgreement)
	 */
	@Override
	public void updateSLA(IServiceLevelAgreement serviceLevelAgreement) {
		coreManagement.updateSLA(serviceLevelAgreement);
	}

	@Override
	public boolean isSLAValidationRunning() {
		return coreManagement.isSLAValidationRunning();
	}

	private ObjectName objectName;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pl.edu.agh.samm.common.core.ICoreManagement#updateMetricPollTimeInterval
	 * (pl.edu.agh.samm.common.metrics.IConfiguredMetric)
	 */
	@Override
	public void updateMetricPollTimeInterval(IConfiguredMetric metric) throws MetricNotRunningException {
		coreManagement.updateMetricPollTimeInterval(metric);
	}

	/**
	 * @param listener
	 * @see pl.edu.agh.samm.common.impl.core.ICoreManagement#addResourceListener(pl.edu.agh.samm.common.core.metrics.IResourceListener)
	 */
	@Override
	public void addResourceListener(IResourceListener listener) {
		coreManagement.addResourceListener(listener);
	}

	/**
	 * @param metric
	 * @param listener
	 * @throws MetricNotRunningException
	 * @see pl.edu.agh.samm.common.impl.core.ICoreManagement#addMetricListener(pl.edu.agh.samm.common.impl.IConfiguredMetric.IMetric,
	 *      pl.edu.agh.samm.common.impl.metrics.IMetricListener)
	 */
	@Override
	public void addRunningMetricListener(IConfiguredMetric metric, IMetricListener listener)
			throws MetricNotRunningException {
		coreManagement.addRunningMetricListener(metric, listener);
	}

	/**
	 * @param listener
	 * @see pl.edu.agh.samm.common.impl.core.ICoreManagement#addMetricsManagerListener(pl.edu.agh.samm.common.impl.metrics.IMetricsManagerListener)
	 */
	@Override
	public void addRunningMetricsManagerListener(IMetricsManagerListener listener) {
		coreManagement.addRunningMetricsManagerListener(listener);
	}

	@Override
	public IConfiguredMetric createRunningMetricInstance(String metricURI, String resourceURI) {
		return coreManagement.createRunningMetricInstance(metricURI, resourceURI);
	}

	@Override
	public Collection<String> getAllRegisteredResources() {
		return coreManagement.getAllRegisteredResources();
	}

	/**
	 * @return the coreManagement
	 */
	public ICoreManagement getCoreManagement() {
		return coreManagement;
	}

	@Override
	public List<String> getResourceCapabilities(String uri) throws ResourceNotRegisteredException {
		return coreManagement.getResourceCapabilities(uri);
	}

	@Override
	public String getResourceType(String uri) {
		return coreManagement.getResourceType(uri);
	}

	public void init() throws Exception {
		logger.info("Starting Core Remote Access JMX...");
		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		objectName = new ObjectName(OBJECT_NAME);
		mBeanServer.registerMBean(this, objectName);
		logger.info("Started Core Remote Access JMX");
	}

	public void destroy() throws Exception {
		logger.info("Stopping Core Remote Access JMX...");
		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		mBeanServer.registerMBean(this, objectName);
		logger.info("Stopped Core Remote Access JMX");
	}

	@Override
	public boolean isMetricRunning(IConfiguredMetric metric) {
		return coreManagement.isMetricRunning(metric);
	}

	@Override
	public boolean isResourceRegistered(String uri) {
		return coreManagement.isResourceRegistered(uri);
	}

	@Override
	public void registerResource(String uri, String type, Map<String, Object> parameters)
			throws ResourceAlreadyRegisteredException {
		coreManagement.registerResource(uri, type, parameters);
	}

	/**
	 * @param listener
	 * @see pl.edu.agh.samm.common.impl.core.ICoreManagement#removeResourceListener(pl.edu.agh.samm.common.core.metrics.IResourceListener)
	 */
	@Override
	public void removeResourceListener(IResourceListener listener) {
		coreManagement.removeResourceListener(listener);
	}

	/**
	 * @param metric
	 * @param listener
	 * @see pl.edu.agh.samm.common.impl.core.ICoreManagement#removeMetricListener(pl.edu.agh.samm.common.impl.IConfiguredMetric.IMetric,
	 *      pl.edu.agh.samm.common.impl.metrics.IMetricListener)
	 */
	@Override
	public void removeRunningMetricListener(IConfiguredMetric metric, IMetricListener listener) {
		coreManagement.removeRunningMetricListener(metric, listener);
	}

	/**
	 * @param listener
	 * @see pl.edu.agh.samm.common.impl.core.ICoreManagement#removeMetricsManagerListener(pl.edu.agh.samm.common.impl.metrics.IMetricsManagerListener)
	 */
	@Override
	public void removeRunningMetricsManagerListener(IMetricsManagerListener listener) {
		coreManagement.removeRunningMetricsManagerListener(listener);
	}

	/**
	 * @param coreManagement
	 *            the coreManagement to set
	 */
	public void setCoreManagement(ICoreManagement coreManagement) {
		this.coreManagement = coreManagement;
	}

	/**
	 * @param metric
	 * @see pl.edu.agh.samm.common.impl.core.ICoreManagement#startMetric(pl.edu.agh.samm.common.impl.IConfiguredMetric.IMetric)
	 */
	@Override
	public void startMetric(IConfiguredMetric metric) {
		coreManagement.startMetric(metric);
	}

	/**
	 * @param runningMetric
	 * @param listeners
	 * @see pl.edu.agh.samm.common.impl.core.ICoreManagement#startMetricAndAddRunningMetricListener(pl.edu.agh.samm.common.impl.IConfiguredMetric.IMetric,
	 *      java.util.Collection)
	 */
	@Override
	public void startMetricAndAddRunningMetricListener(IConfiguredMetric runningMetric,
			Collection<IMetricListener> listeners) {
		coreManagement.startMetricAndAddRunningMetricListener(runningMetric, listeners);
	}

	/**
	 * @param runningMetric
	 * @param listener
	 * @see pl.edu.agh.samm.common.impl.core.ICoreManagement#startMetricAndAddRunningMetricListener(pl.edu.agh.samm.common.impl.IConfiguredMetric.IMetric,
	 *      pl.edu.agh.samm.common.impl.metrics.IMetricListener)
	 */
	@Override
	public void startMetricAndAddRunningMetricListener(IConfiguredMetric runningMetric,
			IMetricListener listener) {
		coreManagement.startMetricAndAddRunningMetricListener(runningMetric, listener);
	}

	/**
	 * @param metric
	 * @throws MetricNotRunningException
	 * @see pl.edu.agh.samm.common.impl.core.ICoreManagement#stopMetric(pl.edu.agh.samm.common.impl.IConfiguredMetric.IMetric)
	 */
	@Override
	public void stopMetric(IConfiguredMetric metric) {
		coreManagement.stopMetric(metric);
	}

	@Override
	public void test(String arg) {
		logger.info("Testing SAMMCoreManagement: " + arg);
	}

	@Override
	public void addAlarmListener(IAlarmListener listener) {
		coreManagement.addAlarmListener(listener);
	}

	@Override
	public void removeAlarmListener(IAlarmListener listener) {
		coreManagement.removeAlarmListener(listener);
	}

	@Override
	public void startSLAValidation(IServiceLevelAgreement serviceLevelAgreement) throws SLAException {
		coreManagement.startSLAValidation(serviceLevelAgreement);
	}

	@Override
	public void stopSLAValidation() throws SLAException {
		coreManagement.stopSLAValidation();
	}

	@Override
	public void startLearning(ILearningStageListener finishListener) {
		coreManagement.startLearning(finishListener);
	}

	@Override
	public void addResourceParameters(String uri, Map<String, Object> parameters)
			throws ResourceNotRegisteredException {
		coreManagement.addResourceParameters(uri, parameters);
	}

}
