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
package pl.edu.agh.samm.eclipse.tools;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author barca
 * 
 * @param <V>
 * 
 * 
 * @see taken&changed from <BR>
 *      http://www.koders.com/java/fidFDDB5C773EE92AF9A438C2C3235CC08AC66650E2.
 *      aspx
 */
public class MultiSet<V> {
	private TreeMap<V, Integer> map;

	public MultiSet() {
		map = new TreeMap<V, Integer>();
	}

	public void addValue(V key) {
		if (!contains(key)) {
			map.put(key, new Integer(1));
		} else {
			Integer val = map.get(key);
			int prev = val.intValue();
			int cur = prev + 1;
			map.put(key, Integer.valueOf(cur));
		}
	}

	public int uniqueSize() {
		return map.size();
	}

	/**
	 * Remove one of key from the set, if present. Return true iff it was
	 * present.
	 */
	public boolean remove(V key) {
		if (!contains(key)) {
			return false;
		}
		Integer val = map.get(key);
		int prev = val.intValue();
		if (prev > 1) {
			map.put(key, Integer.valueOf(prev - 1));
		} else {
			map.remove(key);
		}
		return true;
	}

	public boolean contains(V key) {
		return map.containsKey(key);
	}

	public int count(V key) {
		if (!contains(key)) {
			return 0;
		}
		Integer val = map.get(key);
		return val.intValue();
	}

	public Collection keySet() {
		return map.keySet();
	}

	public Collection values() {
		return map.values();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public double[] getCenterOfBins(double[] bins) {
		double[] center = new double[bins.length - 1];
		for (int licznik = 1; licznik < bins.length; licznik++) {
			center[licznik - 1] = bins[licznik - 1] + (bins[licznik] - bins[licznik - 1]) / 2;
		}
		return center;
	}

	public double[] preapreBins(int binNumber, double firstValue, double lastValue) {
		if (map.size() == 0)
			return null;
		double[] bins = new double[binNumber + 1];
		bins[0] = firstValue;
		bins[binNumber] = lastValue;
		double interval = (lastValue - firstValue) / binNumber;
		for (int licznik = 1; licznik < binNumber; licznik++) {
			bins[licznik] = bins[licznik - 1] + interval;
		}

		return bins;
	}

	public int[] findValuesForBins(double[] bins) {
		int[] binsCount = new int[bins.length - 1];
		int binNumber = 1;
		for (Map.Entry<V, Integer> entry : map.entrySet()) {
			Double key = (Double) entry.getKey();
			int value = entry.getValue();
			if (key > bins[binNumber]) {
				while (key > bins[binNumber])
					binNumber++;
			}
			binsCount[binNumber - 1] += value;
		}
		return binsCount;
	}

	public int[] getMinMax(int values[]) {
		int[] minMax = new int[2];
		for (int i : values) {
			if (minMax[0] > i) {
				minMax[0] = i;
			}
			if (minMax[1] < i) {
				minMax[1] = i;
			}
		}

		return minMax;
	}

	@Override
	public String toString() {
		return map.toString();
	}

	public V getLastValue() {
		return map.lastKey();
	}

	public V getFirstValue() {
		return map.firstKey();
	}

}
