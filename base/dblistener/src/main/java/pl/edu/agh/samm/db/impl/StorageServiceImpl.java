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
package pl.edu.agh.samm.db.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.samm.common.action.Action;
import pl.edu.agh.samm.common.action.ActionExecution;
import pl.edu.agh.samm.common.db.IStorageService;
import pl.edu.agh.samm.common.metrics.IMetric;
import pl.edu.agh.samm.common.metrics.MeasurementValue;
import pl.edu.agh.samm.common.metrics.MetricValue;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class StorageServiceImpl implements IStorageService {

	private static final Logger logger = LoggerFactory.getLogger(StorageServiceImpl.class);

	private IMeasurementValueDAO measurementValueDAO = null;
	private IMetricValueDAO metricValueDAO;
	private IActionExecutionDAO actionExecutionDAO;

	/**
	 * @param measurementValueDAO
	 *            the measurementValueDAO to set
	 */
	public void setMeasurementValueDAO(IMeasurementValueDAO measurementValueDAO) {
		this.measurementValueDAO = measurementValueDAO;
	}

	/**
	 * @param metricValueDAO
	 *            the metricValueDAO to set
	 */
	public void setMetricValueDAO(IMetricValueDAO metricValueDAO) {
		this.metricValueDAO = metricValueDAO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pl.edu.agh.samm.db.IStorageService#storeMeasurement(pl.edu.agh.samm.common
	 * .metrics.IConfiguredMetric, java.lang.Number)
	 */
	@Override
	public void storeMetricValue(IMetric metric, Number value) {
		MetricValue mv = new MetricValue();
		mv.setMetricUri(metric.getMetricURI());
		mv.setResourceUri(metric.getResourceURI());
		mv.setValue(value);
		mv.setTimestamp(new Date());
		metricValueDAO.store(mv);
	}

	@Override
	public void storeMeasurement(String instanceURI, String capabilityURI, Date timestamp, Object value) {
		MeasurementValue info = new MeasurementValue();
		info.setInstanceUri(instanceURI);
		info.setCapabilityUri(capabilityURI);
		info.setTimestamp(timestamp);
		info.setValue(value);

		if (logger.isDebugEnabled()) {
			logger.debug("Storing to database: " + info.toString());
		}
		measurementValueDAO.store(info);

	}

	@Override
	public void storeActionExecution(Action executedAction, Date startDate, Date endDate) {
		ActionExecution exe = new ActionExecution(executedAction, startDate, endDate);

		actionExecutionDAO.store(exe);
	}

	/**
	 * @param actionExecutionDAO
	 *            the actionExecutionDAO to set
	 */
	public void setActionExecutionDAO(IActionExecutionDAO actionExecutionDAO) {
		this.actionExecutionDAO = actionExecutionDAO;
	}

	@Override
	public Map<String, List<ActionExecution>> getAllActionExecutions() {
		Map<String, List<ActionExecution>> ret = new HashMap<String, List<ActionExecution>>();
		Set<String> allActions = actionExecutionDAO.loadAllActionsUris();

		for (String actionUri : allActions) {
			List<ActionExecution> executions = actionExecutionDAO.getAllActionExecutions(actionUri);
			ret.put(actionUri, executions);
		}

		return ret;
	}

	@Override
	public Map<String, Number> getAverageMeasurementValue(String resource, Date beforeActionStartTime,
			Date actionStartTime) {
		return measurementValueDAO.getAverageMeasurementValue(resource, beforeActionStartTime,
				actionStartTime);
	}

	@Override
	public List<MeasurementValue> getHistoricalMeasurementValues(String instanceUri, Date startTime,
			Date endTime) {
		return measurementValueDAO.getMeasurementValues(instanceUri, startTime, endTime);
	}

	@Override
	public Set<String> getKnownResources(Date actionStartTime, Date consequenceStartTime, long windowWidth) {

		Date preActionStartTime = new Date(actionStartTime.getTime() - windowWidth * 1000);
		Date consEndTime = new Date(consequenceStartTime.getTime() + windowWidth * 1000);
		Set<String> preActionResources = measurementValueDAO.getResourcesUris(preActionStartTime,
				actionStartTime);
		Set<String> postConsResources = measurementValueDAO.getResourcesUris(consequenceStartTime,
				consEndTime);
		preActionResources.retainAll(postConsResources);
		return preActionResources;
	}

	@Override
	public Set<String> getAllKnownResources() {
		return measurementValueDAO.getResourcesUris();
	}

	@Override
	public Set<String> getResourceCapabilites(String resourceURI) {
		return measurementValueDAO.getResourceCapabilities(resourceURI);
	}

	@Override
	public List<MeasurementValue> getHistoricalMeasurementValues(String resourceURI, String capabilityURI) {
		return measurementValueDAO.getHistoricalMeasurementValues(resourceURI, capabilityURI);
	}

	@Override
	public List<MeasurementValue> getHistoricalMeasurementValues(String resourceURI, String capabilityURI,
			Date startTime, Date endTime) {
		return measurementValueDAO.getHistoricalMeasurementValues(resourceURI, capabilityURI, startTime,
				endTime);
	}

	@Override
	public List<IMetric> getAllKnownMetrics() {
		return metricValueDAO.getKnownMetrics();
	}

	@Override
	public List<Number> getHistoricalMetricValues(String metricURI, String resourceURI) {
		return metricValueDAO.loadValues(metricURI, resourceURI);
	}
}
