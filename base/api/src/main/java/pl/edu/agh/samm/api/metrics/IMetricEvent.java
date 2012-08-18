package pl.edu.agh.samm.api.metrics;

public interface IMetricEvent {
	IMetric getMetric();

	String getResourceType();

	Number getValue();
}
