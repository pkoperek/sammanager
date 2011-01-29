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
package pl.edu.agh.samm.eclipse.views.visualization.charts;

import java.awt.Color;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleInsets;

import pl.edu.agh.samm.eclipse.views.visualization.IChart;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 */
public class LineChart implements IChart {

	private static final long serialVersionUID = 5255640494762585736L;

	private TimeSeriesCollection mainDataset = new TimeSeriesCollection();

	private Text logArea;

	public LineChart() {
		createChart();
	}

	/**
	 * we need this list for properly mapping
	 */
	private Map<String, TimeSeries> timeSeriesVector = Collections
			.synchronizedMap(new LinkedHashMap<String, TimeSeries>());

	private Map<String, TimeSeriesCollection> historicalDataSet = Collections
			.synchronizedMap(new LinkedHashMap<String, TimeSeriesCollection>());

	private XYPlot plot;

	private JFreeChart chart;

	private JFreeChart createChart() {
		chart = ChartFactory.createTimeSeriesChart(null, // title
				"Seconds", "Event", mainDataset, true, // create legend?
				true, false);
		plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);

		final NumberAxis axisVert = new NumberAxis("Main metric Axis");
		axisVert.setAutoRangeIncludesZero(false);
		axisVert.setTickMarksVisible(false);

		plot.setRangeAxis(0, axisVert);

		// set standard XYItem renderer to avoid ugly small filled squares
		// for each point on the chart which makes whole chart unreadable
		plot.setRenderer(new StandardXYItemRenderer());
		StandardXYItemRenderer r = (StandardXYItemRenderer) plot.getRenderer();
		StandardXYToolTipGenerator tooltipGenerator = new StandardXYToolTipGenerator(
				StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, new SimpleDateFormat("HH:mm:ss"),
				NumberFormat.getInstance());
		r.setToolTipGenerator(tooltipGenerator);

		DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));
		return chart;
	}

	private TimeSeriesCollection createDataset(final String name) {
		final TimeSeriesCollection newDataset = new TimeSeriesCollection();
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				final NumberAxis axis = new NumberAxis(name.length() > 40 ? (name.substring(0, 40) + "...")
						: name);
				int dataSetNumber = historicalDataSet.size();
				axis.setAutoRangeIncludesZero(false);
				plot.setRangeAxis(dataSetNumber, axis);
				plot.setDataset(dataSetNumber, newDataset);
				plot.mapDatasetToRangeAxis(dataSetNumber, dataSetNumber);
				XYItemRenderer renderer2 = new StandardXYItemRenderer();
				StandardXYToolTipGenerator tooltipGenerator = new StandardXYToolTipGenerator(
						StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT, new SimpleDateFormat("H:m:s"),
						NumberFormat.getInstance());
				renderer2.setToolTipGenerator(tooltipGenerator);
				plot.setRenderer(dataSetNumber, renderer2);

			}
		});

		return newDataset;
	}

	@Override
	public void addSeries(final String metricName) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				TimeSeriesCollection datasetAddTo;
				if (historicalDataSet.size() == 0) {
					historicalDataSet.put(metricName, mainDataset);
					datasetAddTo = mainDataset;
					System.out.println("PLOT: " + plot);
					System.out.println("RANGEAXIS: " + plot.getRangeAxis());
					plot.getRangeAxis().setLabel(
							metricName.length() > 35 ? (metricName.substring(0, 35) + "...") : metricName);
				} else {
					// this is not the first metric
					// now we need to check if we had used this metric in past
					if (historicalDataSet.containsKey(metricName)) {
						datasetAddTo = historicalDataSet.get(metricName);
					} else {
						datasetAddTo = createDataset(metricName);
						historicalDataSet.put(metricName, datasetAddTo);
					}
				}
				TimeSeries s1 = new TimeSeries(metricName);
				// mainDataset.addSeries(s1);
				datasetAddTo.addSeries(s1);
				timeSeriesVector.put(metricName, s1);
				if (logArea != null) {
					logArea.append("New series: [" + metricName + "]\n");
				}
			}
		});

	}

	@Override
	public void refreshSeries(final String metric, final Number value) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				TimeSeries serie = timeSeriesVector.get(metric);
				if (serie != null) {
					timeSeriesVector.get(metric).add(new Millisecond(), value);
				}
			}
		});

	}

	@Override
	public void removeSeries(final String metricName) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				timeSeriesVector.remove(metricName);
				if (logArea != null) {
					logArea.append("Remove series: [" + metricName + "]\n");
				}
			}
		});
	}

	// public void addLogPanel(JTextArea logPanel) {
	// logArea = logPanel;
	// }

	@Override
	public void addLogPanel(Text logPanel) {
		this.logArea = logPanel;

	}

	@Override
	public JFreeChart getJFreeChart() {
		return chart;
	}

}
