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
import pl.edu.agh.samm.common.core.IResourceListener;
import pl.edu.agh.samm.common.core.ResourceAlreadyRegisteredException;
import pl.edu.agh.samm.common.core.ResourceNotRegisteredException;
import pl.edu.agh.samm.common.core.Rule;
import pl.edu.agh.samm.common.core.SLAException;
import pl.edu.agh.samm.common.metrics.IMetric;
import pl.edu.agh.samm.common.metrics.IMetricListener;
import pl.edu.agh.samm.common.metrics.IMetricsManagerListener;
import pl.edu.agh.samm.common.metrics.MetricNotRunningException;
import pl.edu.agh.samm.common.sla.IServiceLevelAgreement;

/**
 * Simply delegates all calls to passed Core Management instance
 * 
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class SAMMCoreManagement implements SAMMCoreManagementMBean {
	private ICoreManagement coreManagement;

	public static final String OBJECT_NAME = "pl.edu.agh.samm:type=CoreManagement";
	private Logger logger = LoggerFactory.getLogger(SAMMCoreManagement.class
			.getName());

	private ObjectName objectName;

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

	/**
	 * @param coreManagement
	 *            the coreManagement to set
	 */
	public void setCoreManagement(ICoreManagement coreManagement) {
		this.coreManagement = coreManagement;
	}

	@Override
	public void test(String arg) {
		logger.info("Testing SAMMCoreManagement: " + arg);
	}

	public void updateSLA(IServiceLevelAgreement serviceLevelAgreement) {
		coreManagement.updateSLA(serviceLevelAgreement);
	}

	public void startSLAValidation(IServiceLevelAgreement serviceLevelAgreement)
			throws SLAException {
		coreManagement.startSLAValidation(serviceLevelAgreement);
	}

	public void stopSLAValidation() throws SLAException {
		coreManagement.stopSLAValidation();
	}

	public IServiceLevelAgreement retrieveCurrentSLA() throws SLAException {
		return coreManagement.retrieveCurrentSLA();
	}

	public boolean isSLAValidationRunning() {
		return coreManagement.isSLAValidationRunning();
	}

	public boolean isResourceRegistered(String uri) {
		return coreManagement.isResourceRegistered(uri);
	}

	public Collection<String> getAllRegisteredResources() {
		return coreManagement.getAllRegisteredResources();
	}

	public String getResourceType(String uri) {
		return coreManagement.getResourceType(uri);
	}

	public List<String> getResourceCapabilities(String uri)
			throws ResourceNotRegisteredException {
		return coreManagement.getResourceCapabilities(uri);
	}

	public void addResourceListener(IResourceListener listener) {
		coreManagement.addResourceListener(listener);
	}

	public void removeResourceListener(IResourceListener listener) {
		coreManagement.removeResourceListener(listener);
	}

	public void startMetric(IMetric metric) {
		coreManagement.startMetric(metric);
	}

	public void startMetricAndAddRunningMetricListener(IMetric runningMetric,
			Collection<IMetricListener> listeners) {
		coreManagement.startMetricAndAddRunningMetricListener(runningMetric,
				listeners);
	}

	public void startMetricAndAddRunningMetricListener(IMetric runningMetric,
			IMetricListener listener) {
		coreManagement.startMetricAndAddRunningMetricListener(runningMetric,
				listener);
	}

	public boolean isMetricRunning(IMetric metric) {
		return coreManagement.isMetricRunning(metric);
	}

	public void stopMetric(IMetric metric) {
		coreManagement.stopMetric(metric);
	}

	public void updateMetricPollTimeInterval(IMetric metric)
			throws MetricNotRunningException {
		coreManagement.updateMetricPollTimeInterval(metric);
	}

	public void addRunningMetricListener(IMetric metric,
			IMetricListener listener) throws MetricNotRunningException {
		coreManagement.addRunningMetricListener(metric, listener);
	}

	public void removeRunningMetricListener(IMetric metric,
			IMetricListener listener) {
		coreManagement.removeRunningMetricListener(metric, listener);
	}

	public void addRunningMetricsManagerListener(
			IMetricsManagerListener listener) {
		coreManagement.addRunningMetricsManagerListener(listener);
	}

	public void removeRunningMetricsManagerListener(
			IMetricsManagerListener listener) {
		coreManagement.removeRunningMetricsManagerListener(listener);
	}

	public IMetric createMetricInstance(String metricURI, String resourceURI) {
		return coreManagement.createMetricInstance(metricURI, resourceURI);
	}

	public void registerResource(String uri, String type,
			Map<String, Object> parameters)
			throws ResourceAlreadyRegisteredException {
		coreManagement.registerResource(uri, type, parameters);
	}

	public void unregisterResource(String uri) {
		coreManagement.unregisterResource(uri);
	}

	public void addAlarmListener(IAlarmListener listener) {
		coreManagement.addAlarmListener(listener);
	}

	public void removeAlarmListener(IAlarmListener listener) {
		coreManagement.removeAlarmListener(listener);
	}

	public void addResourceParameters(String uri, Map<String, Object> parameters)
			throws ResourceNotRegisteredException {
		coreManagement.addResourceParameters(uri, parameters);
	}

	public void addActionExecutorListener(IActionExecutionListener listener) {
		coreManagement.addActionExecutorListener(listener);
	}

	public void removeActionExecutorListener(IActionExecutionListener listener) {
		coreManagement.removeActionExecutorListener(listener);
	}

	public void addRule(Rule rule) {
		coreManagement.addRule(rule);
	}

	public void clearRules() {
		coreManagement.clearRules();
	}

	public void removeRule(String ruleName) {
		coreManagement.removeRule(ruleName);
	}

}
