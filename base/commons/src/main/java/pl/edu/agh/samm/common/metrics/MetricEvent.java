package pl.edu.agh.samm.common.metrics;

import pl.edu.agh.samm.common.metrics.IMetricEvent;

public class MetricEvent implements IMetricEvent {

	Number value = null;
	IMetric metric = null;

	public MetricEvent(IMetric metric, Number value) {
		this.value = value;
		this.metric = metric;
	}

	public Number getValue() {
		return value;
	}

	public IMetric getMetric() {
		return metric;
	}

}
