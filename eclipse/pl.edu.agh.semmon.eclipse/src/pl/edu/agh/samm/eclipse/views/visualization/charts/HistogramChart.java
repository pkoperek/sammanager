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

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.IntervalXYDataset;

import pl.edu.agh.samm.eclipse.tools.MultiSet;
import pl.edu.agh.samm.eclipse.views.visualization.IChart;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 */

public class HistogramChart implements IChart {
	private static final long serialVersionUID = 1L;

	private ChartPanel chartPanel;

	private Text logArea;

	private JFreeChart chart;

	// private JSlider slider;

	private int binCount = 5;

	public HistogramChart() {

		final IntervalXYDataset dataset = createDataset();

		chart = ChartFactory.createXYBarChart(null, "Intervals", false, "Count", dataset,
				PlotOrientation.VERTICAL, true, false, false);

		final XYPlot plot = (XYPlot) chart.getPlot();
		final NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
		domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		// add the chart to a panel...
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 250));
		chartPanel.setMouseZoomable(true, false);
		// packPanel = new JPanel();
		// packPanel.setLayout(new BorderLayout());
		// packPanel.add(chartPanel, BorderLayout.CENTER);

		// slider panel
		// JPanel sliderPanel = new JPanel();
		// sliderPanel.add(new JLabel("Number of intervals:  "));
		//
		// // slider
		// slider = new JSlider();
		// slider.setMinimum(3);
		// slider.setMaximum(27);
		// slider.setMajorTickSpacing(6);
		// slider.setPaintTicks(true);
		// slider.setPaintLabels(true);
		// slider.addChangeListener(new SliderListener());
		// slider.setValue(binCount);
		//
		// sliderPanel.add(slider);

		// packPanel.add(sliderPanel, BorderLayout.SOUTH);
	}

	private SimpleBarDataset dataset = new SimpleBarDataset();

	private LinkedHashMap<String, HistogramSeries> allSeriesList = new LinkedHashMap<String, HistogramSeries>();

	private LinkedHashMap<String, HistogramSeries> actualList = new LinkedHashMap<String, HistogramSeries>();

	private IntervalXYDataset createDataset() {
		return dataset;
	}

	@Override
	public void addSeries(final String metricName) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (allSeriesList.containsKey(metricName)) {
					actualList.put(metricName, allSeriesList.get(metricName));
				} else {
					HistogramSeries histogramSeries = new HistogramSeries(metricName, allSeriesList.size());
					allSeriesList.put(metricName, histogramSeries);
					actualList.put(metricName, histogramSeries);
					dataset.addSeries(metricName);
				}
				if (logArea != null) {
					logArea.append("New series: [" + metricName + "]\n");
				}

			}
		});
	}

	@Override
	public void refreshSeries(final String metricName, final Number value) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				Double firstValueX = null;
				Double lastValueX = null;
				actualList.get(metricName).addValue(value.doubleValue());
				for (Entry<String, HistogramSeries> set : actualList.entrySet()) {
					MultiSet<Double> multiSet = set.getValue().getMultiSet();
					if (multiSet.uniqueSize() > 0) {
						if (firstValueX == null) {
							firstValueX = multiSet.getFirstValue();
						} else if (firstValueX.doubleValue() > multiSet.getFirstValue()) {
							firstValueX = multiSet.getFirstValue();
						}
						if (lastValueX == null) {
							lastValueX = multiSet.getLastValue();
						} else if (lastValueX.doubleValue() < multiSet.getLastValue()) {
							lastValueX = multiSet.getLastValue();
						}
					}
				}

				if (firstValueX.doubleValue() != lastValueX.doubleValue()) {
					for (Entry<String, HistogramSeries> set : actualList.entrySet()) {
						set.getValue().refreshSeries(firstValueX, lastValueX);
					}
				}

			}
		});

	}

	@Override
	public void removeSeries(final String metricName) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				actualList.remove(metricName);
				if (logArea != null) {
					logArea.append("Remove series: [" + metricName + "]\n");
				}
			}
		});

	}

	// private class SliderListener implements ChangeListener {
	// public void stateChanged(ChangeEvent e) {
	// binCount = slider.getValue();
	// }
	// }

	private class HistogramSeries implements Comparable {
		String seriesName;

		int seriesPosition;

		MultiSet<Double> valueSet = new MultiSet<Double>();

		public void refreshSeries(double firstValueX, double lastValueX) {
			double[] bins = valueSet.preapreBins(binCount, firstValueX, lastValueX);
			if (bins != null) {
				int[] valuesOfBins = valueSet.findValuesForBins(bins);
				int[] minMax = valueSet.getMinMax(valuesOfBins);

				XYPlot plot = (XYPlot) chart.getPlot();
				if (plot.getRangeAxis().getRange().getUpperBound() * 1.1 < minMax[1]) {
					plot.getRangeAxis(0).setRange(minMax[0], minMax[1] * 1.1);
				}

				plot.getDomainAxis(0).setRange(firstValueX, lastValueX);

				dataset.setBins(bins, seriesPosition);
				dataset.setValues(valuesOfBins, seriesPosition);
			}
		}

		public void addValue(double value) {
			valueSet.addValue(value);
		}

		public MultiSet<Double> getMultiSet() {
			return valueSet;
		}

		public HistogramSeries(String seriesName, int seriesPosition) {
			this.seriesName = seriesName;
			this.seriesPosition = seriesPosition;
		}

		@Override
		public int compareTo(Object o) {
			return seriesName.compareTo(((HistogramSeries) o).seriesName);
		}

		@Override
		public boolean equals(Object obj) {
			return seriesName.equals(((HistogramSeries) obj).seriesName);
		}

		@Override
		public int hashCode() {
			return seriesName.hashCode();
		}
	}

	// public void addLogPanel(JTextArea logPanel) {
	// logArea = logPanel;
	// }

	@Override
	public JFreeChart getJFreeChart() {
		return chart;
	}

	@Override
	public void addLogPanel(Text logPanel) {
		this.logArea = logPanel;
	}
}
