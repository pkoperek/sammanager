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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.samm.api.metrics.IMetric;
import pl.edu.agh.samm.api.metrics.IMetricEvent;
import pl.edu.agh.samm.api.metrics.IMetricListener;
import pl.edu.agh.samm.api.metrics.IMetricsManagerListener;
import pl.edu.agh.samm.api.metrics.MetricNotRunningException;
import pl.edu.agh.samm.api.tadapter.IMeasurementEvent;
import pl.edu.agh.samm.api.tadapter.IMeasurementListener;
import pl.edu.agh.samm.metrics.IMetricsManager;

/**
 * @author koperek
 */
public class RuleProcessorInputListener implements IMetricListener,
        IMetricsManagerListener, IMeasurementListener {

    private static final Logger logger = LoggerFactory.getLogger(RuleProcessorInputListener.class);

    private IRuleProcessor ruleProcessor = null;
    private IMetricsManager metricsManager = null;
    private boolean enabled = false;

    public RuleProcessorInputListener(IRuleProcessor ruleProcessor, IMetricsManager metricsManager) {
        this.ruleProcessor = ruleProcessor;
        this.metricsManager = metricsManager;
    }

    /*
     * (non-Javadoc)
     *
     * @see pl.edu.agh.samm.api.metrics.IMetricsManagerListener#
     * notifyNewMetricsStarted(java.util.Collection)
     */
    @Override
    public void notifyNewMetricsStarted(Collection<IMetric> startedMetrics)
            throws Exception {
        for (IMetric metric : startedMetrics) {
            try {
                metricsManager.addMetricListener(metric, this);
            } catch (MetricNotRunningException e) {
                logger.warn("Metric: " + metric + " not running while it should be... Ignoring", e);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see pl.edu.agh.samm.api.metrics.IMetricsManagerListener#
     * notifyMetricsHasStopped(java.util.Collection)
     */
    @Override
    public void notifyMetricsHasStopped(Collection<IMetric> stoppedMetrics)
            throws Exception {
        for (IMetric metric : stoppedMetrics) {
            metricsManager.removeMetricListener(metric, this);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * pl.edu.agh.samm.api.metrics.IMetricListener#processMetricEvent(pl.
     * edu.agh.samm.api.metrics.IMetricEvent)
     */
    @Override
    public void processMetricEvent(IMetricEvent metricEvent) throws Exception {
        if (enabled) {
            ruleProcessor.processMetricEvent(metricEvent);
        }
    }

    @Override
    public void processMeasurementEvent(IMeasurementEvent event) {
        if (enabled) {
            ruleProcessor.processMeasurementEvent(event);
        }
    }

    public void enable() {
        enabled = true;
        metricsManager.addMetricsManagerListener(this);
        Collection<IMetric> runningMetrics = metricsManager.getRunningMetrics();
        for (IMetric runningMetric : runningMetrics) {
            try {
                metricsManager.addMetricListener(runningMetric, this);
            } catch (MetricNotRunningException e) {
                logger.warn("Metric: " + runningMetric
                        + " not running while it should be... Ignoring", e);
            }
        }
    }

    public void disable() {
        enabled = false;
        metricsManager.removeMetricsManagerListener(this);
        Collection<IMetric> runningMetrics = metricsManager.getRunningMetrics();
        for (IMetric runningMetric : runningMetrics) {
            try {
                metricsManager.addMetricListener(runningMetric, this);
            } catch (MetricNotRunningException e) {
                logger.warn("Metric: " + runningMetric
                        + " not running while it should be... Ignoring", e);
            }
        }
    }

}
