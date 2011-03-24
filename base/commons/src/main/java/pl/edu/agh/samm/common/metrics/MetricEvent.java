package pl.edu.agh.samm.common.metrics;

import pl.edu.agh.samm.common.metrics.IMetricEvent;

public class MetricEvent implements IMetricEvent {

	Number value = null;
	IMetric metric = null;
	private String resourceType;

	public MetricEvent(IMetric metric, Number value, String resourceType) {
		this.value = value;
		this.metric = metric;
		this.resourceType = resourceType;
	}

	public String getResourceType() {
		return resourceType;
	}

	public Number getValue() {
		return value;
	}

	public IMetric getMetric() {
		return metric;
	}

}
