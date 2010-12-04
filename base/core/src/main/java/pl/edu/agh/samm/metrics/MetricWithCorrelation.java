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

package pl.edu.agh.samm.metrics;

import pl.edu.agh.samm.common.metrics.IConfiguredMetric;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class MetricWithCorrelation implements Comparable<MetricWithCorrelation> {

	private IConfiguredMetric metric;
	private IConfiguredMetric correlatingMetric;

	public MetricWithCorrelation(IConfiguredMetric metric, IConfiguredMetric correlatingMetric,
			double correlation) {
		this.metric = metric;
		this.correlatingMetric = correlatingMetric;
		this.correlation = correlation;
	}

	double correlation;

	public IConfiguredMetric getMetric() {
		return metric;
	}

	public IConfiguredMetric getCorrelatingMetric() {
		return correlatingMetric;
	}

	public double getCorrelation() {
		return correlation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((correlatingMetric == null) ? 0 : correlatingMetric.hashCode());
		long temp;
		temp = Double.doubleToLongBits(correlation);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((metric == null) ? 0 : metric.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MetricWithCorrelation other = (MetricWithCorrelation) obj;
		if (correlatingMetric == null) {
			if (other.correlatingMetric != null)
				return false;
		} else if (!correlatingMetric.equals(other.correlatingMetric))
			return false;
		if (Double.doubleToLongBits(correlation) != Double.doubleToLongBits(other.correlation))
			return false;
		if (metric == null) {
			if (other.metric != null)
				return false;
		} else if (!metric.equals(other.metric))
			return false;
		return true;
	}

	@Override
	public int compareTo(MetricWithCorrelation o) {
		return Double.compare(this.correlation, o.correlation);
	}

}
