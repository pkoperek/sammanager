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

import java.util.Collection;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.samm.api.core.ICoreManagement;
import pl.edu.agh.samm.api.db.IStorageService;
import pl.edu.agh.samm.api.metrics.IMetric;
import pl.edu.agh.samm.api.metrics.IMetricEvent;
import pl.edu.agh.samm.api.metrics.IMetricListener;
import pl.edu.agh.samm.api.metrics.IMetricsManagerListener;
import pl.edu.agh.samm.api.metrics.MetricNotRunningException;
import pl.edu.agh.samm.api.tadapter.IMeasurementEvent;
import pl.edu.agh.samm.api.tadapter.IMeasurementListener;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class MeasurementMetricDBListener implements IMeasurementListener,
		IMetricListener, IMetricsManagerListener {

	private static final Logger logger = LoggerFactory
			.getLogger(MeasurementMetricDBListener.class);

	private ICoreManagement coreManagement = null;
	private IStorageService storageService = null;

	public void init() {
		coreManagement.addRunningMetricsManagerListener(this);
	}

	/**
	 * @param storageService
	 *            the storageService to set
	 */
	public void setStorageService(IStorageService storageService) {
		this.storageService = storageService;
	}

	@Override
	public void processMeasurementEvent(IMeasurementEvent event) {
		storageService.storeMeasurement(event.getInstanceUri(),
				event.getCapabilityUri(), new Date(), event.getValue());
	}

	@Override
	public void notifyMetricsHasStopped(Collection<IMetric> stoppedMetrics) {

		for (IMetric metric : stoppedMetrics) {
			coreManagement.removeRunningMetricListener(metric, this);
		}
	}

	@Override
	public void notifyNewMetricsStarted(Collection<IMetric> startedMetrics) {
		for (IMetric metric : startedMetrics) {
			try {
				coreManagement.addRunningMetricListener(metric, this);
			} catch (MetricNotRunningException e) {
				logger.error("Exception!", e);
			}
		}

	}

	public void setCoreManagement(ICoreManagement coreManagement) {
		this.coreManagement = coreManagement;
	}

	@Override
	public void processMetricEvent(IMetricEvent metricEvent) throws Exception {
		storageService.storeMetricValue(metricEvent.getMetric(),
				metricEvent.getValue());
	}

}
