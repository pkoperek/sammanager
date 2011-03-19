package pl.edu.agh.samm.common.metrics;

public interface IMetricEvent {
	IMetric getMetric();

	Number getValue();
}
