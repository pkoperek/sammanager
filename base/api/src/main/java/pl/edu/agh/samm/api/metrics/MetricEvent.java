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

package pl.edu.agh.samm.api.metrics;

public class MetricEvent implements IMetricEvent {

	Number value = null;
	IMetric metric = null;
	private String resourceType;

	public MetricEvent(IMetric metric, Number value, String resourceType) {
		this.value = value;
		this.metric = metric;
		this.resourceType = resourceType;
	}

	public String getResourceType() {
		return resourceType;
	}

	public Number getValue() {
		return value;
	}

	public IMetric getMetric() {
		return metric;
	}

}
