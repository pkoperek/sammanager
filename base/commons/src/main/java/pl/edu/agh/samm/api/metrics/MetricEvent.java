package pl.edu.agh.samm.api.metrics;

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
