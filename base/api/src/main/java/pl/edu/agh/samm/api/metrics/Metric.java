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

import pl.edu.agh.samm.api.impl.StringHelper;

/**
 * Default implementation of {@link IMetric}
 * 
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 */
public class Metric implements IMetric {
	private static final long serialVersionUID = -5768117605198157976L;

	/**
	 * Default metric poll time interval, in miliseconds. Equals to 20000 ms,
	 * which is 20 second interval.
	 */
	public final static long DEFAULT_METRIC_POLL_TIME_INTERVAL = 20000;

	private final String resourceURI;
	private final String metricURI;
	private long metricPollTimeInterval;

	public Metric(String metricURI, String resourceURI) {
		this(metricURI, resourceURI, DEFAULT_METRIC_POLL_TIME_INTERVAL);
	}

	public Metric(String metricURI, String resourceURI,
			long metricPollTimeInterval) {

		if (metricPollTimeInterval <= 0) {
			metricPollTimeInterval = DEFAULT_METRIC_POLL_TIME_INTERVAL;
		}
		this.resourceURI = resourceURI;
		this.metricURI = metricURI;
		this.metricPollTimeInterval = metricPollTimeInterval;
	}

	@Override
	public long setMetricPollTimeInterval(long interval) {
		long previous = metricPollTimeInterval;
		if (interval > 0) {
			this.metricPollTimeInterval = interval;
		}
		return previous;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof IMetric) {
			IMetric metric = (IMetric) o;
			// ignore poll time interval
			return (metricURI.equals(metric.getMetricURI()))
					&& (resourceURI.equals(metric.getResourceURI()));
		}

		return super.equals(o);
	}

	@Override
	public String toString() {
		return StringHelper.getNameFromURI(metricURI) + " " + resourceURI;

	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String getMetricURI() {
		return metricURI;
	}

	@Override
	public String getResourceURI() {
		return resourceURI;
	}

	@Override
	public long getMetricPollTimeInterval() {
		return metricPollTimeInterval;
	}

	@Override
	public boolean isPatternMetric() {
		return resourceURI.contains("*") || resourceURI.contains("?") || resourceURI.contains("+");
	}
}
