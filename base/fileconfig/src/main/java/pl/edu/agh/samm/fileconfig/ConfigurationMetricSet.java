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
package pl.edu.agh.samm.fileconfig;

import java.util.LinkedList;
import java.util.List;

/**
 * @author koperek
 * 
 */
public class ConfigurationMetricSet {
	private List<ConfigurationMetric> metrics = new LinkedList<ConfigurationMetric>();

	public void addMetric(ConfigurationMetric metric) {
		metrics.add(metric);
	}

	public List<ConfigurationMetric> getMetrics() {
		return metrics;
	}
}
