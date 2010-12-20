/**
 * 
 */
package pl.edu.agh.samm.eclipse;

import java.util.Collection;

import pl.edu.agh.samm.common.metrics.IConfiguredMetric;
import pl.edu.agh.samm.common.metrics.IRMIMetricsManagerListener;
import pl.edu.agh.samm.eclipse.model.RunningMetricsList;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class MetricsManagerListener implements IRMIMetricsManagerListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.edu.agh.samm.common.metrics.IMetricsManagerListener#
	 * notifyNewMetricsStarted(java.util.Collection)
	 */
	@Override
	public void notifyNewMetricsStarted(Collection<IConfiguredMetric> startedMetrics) {
		for (IConfiguredMetric configuredMetric : startedMetrics) {
			RunningMetricsList.getInstance().addRunningMetric(configuredMetric);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.edu.agh.samm.common.metrics.IMetricsManagerListener#
	 * notifyMetricsHasStopped(java.util.Collection)
	 */
	@Override
	public void notifyMetricsHasStopped(Collection<IConfiguredMetric> stoppedMetrics) {
		for (IConfiguredMetric configuredMetric : stoppedMetrics) {
			RunningMetricsList.getInstance().removeRunningMetric(configuredMetric);
		}
	}

}
