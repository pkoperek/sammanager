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
package pl.edu.agh.samm.metrics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.samm.common.core.Resource;
import pl.edu.agh.samm.common.metrics.IMetric;
import pl.edu.agh.samm.common.metrics.IMetricListener;
import pl.edu.agh.samm.common.metrics.MetricEvent;
import pl.edu.agh.samm.common.tadapter.ITransportAdapter;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public abstract class MetricTask implements Runnable {

	protected static final Logger logger = LoggerFactory
			.getLogger(MetricTask.class);
	private List<IMetricListener> metricListeners = new CopyOnWriteArrayList<IMetricListener>();
	private List<ClassLoader> metricListenersClassLoaders = new CopyOnWriteArrayList<ClassLoader>();
	private Resource resource = null;
	private List<String> usedCapabilities = null;
	protected IMetric metric = null;
	private List<ITransportAdapter> adapters = null;
	private Map<String, ITransportAdapter> adaptersToUseForCapabilities = new HashMap<String, ITransportAdapter>();
	private Map<String, Number> values = new HashMap<String, Number>();
	private IMetricProblemObserver problemObserver;

	public MetricTask(IMetric metric, List<String> usedCapabilities,
			Resource resource) {
		this.usedCapabilities = usedCapabilities;
		this.resource = resource;
		this.metric = metric;
	}

	public void setProblemObserver(IMetricProblemObserver observer) {
		this.problemObserver = observer;
	}

	public void addMetricListener(IMetricListener metricListener) {
		metricListeners.add(metricListener);
		// save metricListenres ClassLoader sa we can use it when notifying of
		// value change
		metricListenersClassLoaders.add(Thread.currentThread()
				.getContextClassLoader());
	}

	public IMetric getMetric() {
		return metric;
	}

	protected Resource getResource() {
		return resource;
	}

	public void removeMetricListener(IMetricListener metricListener) {
		int index = metricListeners.indexOf(metricListener);
		metricListenersClassLoaders.remove(index);
		metricListeners.remove(metricListener);

	}

	protected void fireMetricEvent(Number value) {
		logger.debug("Metric: " + metric.getMetricURI() + " resource: "
				+ metric.getResourceURI() + " value: " + value);
		int i = 0;
		for (IMetricListener listener : metricListeners) {
			try {
				// set listeners classloader
				Thread.currentThread().setContextClassLoader(
						metricListenersClassLoaders.get(i));
				listener.processMetricEvent(new MetricEvent(metric, value));
			} catch (Throwable e) {
				logger.warn("Error while notifying listener", e);
			}
			i++;
		}
	}

	public void init() {
		adapters = resource.getTransportAdapters();

		for (String usedCapability : usedCapabilities) {
			ITransportAdapter adapterToUse = null;
			for (ITransportAdapter adapter : adapters) {
				try {
					if (adapter.hasCapability(resource, usedCapability)) {
						adapterToUse = adapter;
						break;
					}
				} catch (Exception e) {
					// if exception is thrown - we assume no
					logger.debug(
							"Transport Adapter threw exception on hasCapability (ignoring)",
							e);
				}
			}
			if (adapterToUse == null) {
				throw new RuntimeException("No adapter found for: resource: "
						+ resource + " capability: " + usedCapability);
			} else {
				adaptersToUseForCapabilities.put(usedCapability, adapterToUse);
			}
		}

	}

	public boolean isOneCapabilityUsed() {
		return adaptersToUseForCapabilities.size() == 1;
	}

	protected ITransportAdapter getAdapterForSingleCapabilitySituation() {
		return adaptersToUseForCapabilities.values().toArray(
				new ITransportAdapter[1])[0];
	}

	protected String getCapabilityForSingleCapabilitySituation() {
		return usedCapabilities.get(0);
	}

	public abstract Number computeMetricValue(Map<String, Number> values);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		for (String usedCapability : usedCapabilities) {
			try {
				Number capabilityValue = (Number) adaptersToUseForCapabilities
						.get(usedCapability).getCapabilityValue(resource,
								usedCapability);
				values.put(usedCapability, capabilityValue);
			} catch (Exception e) {
				logger.error("Couldn't retrieve metric value! Metric: "
						+ metric + " Resource: " + resource + " Capability: "
						+ usedCapability, e);
				reportProblem(e);
			}
		}
		try {
			Number value = computeMetricValue(values);
			if (value != null) {
				fireMetricEvent(value);
			}
		} catch (Exception e) {
			logger.error("Error while computing metric value: ", e);
		}
	}

	protected void reportProblem(Exception e) {
		problemObserver.problemOcurred(getMetric(), e);
	}

}
