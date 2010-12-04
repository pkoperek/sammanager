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
package pl.edu.agh.samm.eclipse.views.visualization;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import pl.edu.agh.samm.common.impl.StringHelper;
import pl.edu.agh.samm.common.metrics.IConfiguredMetric;
import pl.edu.agh.samm.common.metrics.IMetricListener;
import pl.edu.agh.samm.common.metrics.IRMIMetricListener;
import pl.edu.agh.samm.eclipse.SAMM;
import pl.edu.agh.samm.eclipse.views.visualization.charts.ChartType;
import pl.edu.agh.samm.eclipse.views.visualization.charts.HistogramChart;
import pl.edu.agh.samm.eclipse.views.visualization.charts.LineChart;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class Visualisation implements IVisualisation {
	private String name;

	private Map<IConfiguredMetric, IMetricListener> metricListeners = new HashMap<IConfiguredMetric, IMetricListener>();
	private IChart chart;

	private Boolean running = true;

	public Visualisation(ChartType typeOfVis) {
		createChart(typeOfVis);
	}

	private void createChart(ChartType chartType) {
		if (chartType == ChartType.HISTOGRAM) {
			chart = new HistogramChart();
		} else {
			chart = new LineChart();
		}
		// FIXME co z logiem zrobic??
		// chart.addLogPanel(visualisationFrame.txtScrollLog);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Visualisation)) {
			return false;
		}
		return ((Visualisation) obj).getName().equals(name);
	}

	@Override
	public String toString() {
		return name;
	}

	public void updateMetricValue(IConfiguredMetric metricToUpdate, final Number newValue) {
		if (running) {
			String metricName = StringHelper.getMetricName(metricToUpdate);
			chart.refreshSeries(metricName, newValue);
		}
	}

	@Override
	public Boolean isRunning() {
		return running;
	}

	@Override
	public void setRunning(final Boolean running) {
		this.running = running;
	}

	@Override
	public IChart getChart() {
		return chart;
	}

	@Override
	public void attachToMetric(IConfiguredMetric metric) throws Exception {
		IRMIMetricListener metricListener = new RMIMetricListener(this);
		metricListener = (IRMIMetricListener) UnicastRemoteObject.exportObject(metricListener, 0);
		String metricName = StringHelper.getMetricName(metric);
		chart.addSeries(metricName);
		SAMM.getCoreManagement().addRunningMetricListener(metric, metricListener);
		metricListeners.put(metric, metricListener);
	}

	@Override
	public void detachFromMetric(IConfiguredMetric metric) throws Exception {
		IMetricListener metricListener = metricListeners.get(metric);
		if (metricListener != null) {
			SAMM.getCoreManagement().removeRunningMetricListener(metric, metricListener);
			String metricName = StringHelper.getMetricName(metric);
			chart.removeSeries(metricName);
			metricListeners.remove(metric);
		}
	}

	private static class RMIMetricListener implements IRMIMetricListener {

		private Visualisation visualization = null;

		public RMIMetricListener(Visualisation visualisation) {
			this.visualization = visualisation;
		}

		@Override
		public void notifyMetricValue(IConfiguredMetric metric, Number value) throws RemoteException {
			this.visualization.updateMetricValue(metric, value);
		}

	}

	@Override
	public Collection<IConfiguredMetric> getAttachedMetrics() {
		return new LinkedList<IConfiguredMetric>(metricListeners.keySet());
	}

}
