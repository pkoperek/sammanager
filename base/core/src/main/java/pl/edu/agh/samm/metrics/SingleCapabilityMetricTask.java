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

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.samm.common.core.Resource;
import pl.edu.agh.samm.common.metrics.IMetric;
import pl.edu.agh.samm.common.tadapter.ITransportAdapter;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class SingleCapabilityMetricTask extends MetricTask {

	private static final Logger logger = LoggerFactory.getLogger(SingleCapabilityMetricTask.class);
	private ITransportAdapter adapterToUse;
	private String usedCapability;
	private Resource resource;

	public SingleCapabilityMetricTask(IMetric metric, List<String> usedCapabilities,
			Resource resource) {
		super(metric, usedCapabilities, resource);
	}

	@Override
	public void init() {
		super.init();

		if (!this.isOneCapabilityUsed()) {
			throw new RuntimeException(
					"Using single capability metric task for a metric with multiple capabilities used! Metric: "
							+ getMetric());
		}

		adapterToUse = getAdapterForSingleCapabilitySituation();
		usedCapability = getCapabilityForSingleCapabilitySituation();
		resource = getResource();
	}

	/*
	 * Not used at all
	 * 
	 * (non-Javadoc)
	 * 
	 * @see pl.edu.agh.samm.metrics.MetricTask#computeMetricValue(java.util.Map)
	 */
	@Override
	public Number computeMetricValue(Map<String, Number> values) {
		return values.values().toArray(new Number[1])[0];
	}

	@Override
	public void run() {
		Number value;
		try {
			value = (Number) adapterToUse.getCapabilityValue(resource, usedCapability);
			if (value != null) {
				fireMetricEvent(value);
			}
		} catch (Exception e) {
			logger.error("Couldn't retrieve metric value! Metric: " + getMetric() + " Resource: " + resource
					+ " Capability: " + usedCapability, e);
			reportProblem(e);
		}
	}

}
