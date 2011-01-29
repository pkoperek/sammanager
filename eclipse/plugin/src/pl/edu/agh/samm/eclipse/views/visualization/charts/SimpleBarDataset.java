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

import java.util.ArrayList;
import java.util.LinkedList;

import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 */
public class SimpleBarDataset extends AbstractIntervalXYDataset implements IntervalXYDataset {

	private class SeriesValues {
		private Double[] xStart = new Double[maxPeriod];

		/** The end values. */
		private Double[] xEnd = new Double[maxPeriod];

		/** The y values. */
		private Double[] yValues = new Double[maxPeriod];
	}

	private ArrayList<SeriesValues> valueMap = new ArrayList<SeriesValues>();

	private LinkedList<String> nameSeries = new LinkedList<String>();

	private static final long serialVersionUID = 6615458956743853451L;

	private final int maxPeriod = 100;

	/** The start values. */

	private int binsCount = 0;

	public SimpleBarDataset() {
	}

	@Override
	public int getSeriesCount() {
		return valueMap.size();
	}

	@Override
	public int getItemCount(final int series) {
		return binsCount;
	}

	@Override
	public Number getX(final int series, final int item) {
		Number wartosc = this.valueMap.get(series).xStart[item];
		return wartosc;
	}

	@Override
	public Number getY(final int series, final int item) {
		return this.valueMap.get(series).yValues[item];
	}

	@Override
	public Number getStartX(final int series, final int item) {
		double przedzial = valueMap.get(0).xEnd[0] - valueMap.get(0).xStart[0];
		przedzial = przedzial * 0.1 * ((valueMap.size() - series - 1));
		Number wartosc = this.valueMap.get(series).xStart[item].doubleValue() + przedzial;
		return wartosc;
	}

	@Override
	public Number getEndX(final int series, final int item) {
		double przedzial = valueMap.get(0).xEnd[0] - valueMap.get(0).xStart[0];
		przedzial = przedzial * 0.1 * ((valueMap.size() - series - 1));
		Number wartosc = this.valueMap.get(series).xEnd[item].doubleValue() - przedzial;
		return wartosc;
	}

	@Override
	public Number getStartY(int series, int item) {
		return this.valueMap.get(series).yValues[item];
	}

	@Override
	public Number getEndY(final int series, final int item) {
		return this.valueMap.get(series).yValues[item];
	}

	@Override
	public Comparable getSeriesKey(int arg0) {
		return nameSeries.get(arg0);
	}

	// public

	public void setBins(double[] bins, int series) {
		binsCount = bins.length - 1;
		for (int licznik = 1; licznik < bins.length; licznik++) {
			valueMap.get(series).xStart[licznik - 1] = bins[licznik - 1];
			valueMap.get(series).xEnd[licznik - 1] = bins[licznik];
		}
	}

	public void setValues(int[] values, int series) {
		for (int licznik = 0; licznik < values.length; licznik++) {
			valueMap.get(series).yValues[licznik] = (double) values[licznik];
		}
	}

	public void addSeries(String name) {
		nameSeries.add(name);
		valueMap.add(new SeriesValues());
	}

}
