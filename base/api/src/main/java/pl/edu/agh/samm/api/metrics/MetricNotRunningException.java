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
 * 
 */
package pl.edu.agh.samm.api.metrics;

/**
 * Exception thrown if a operation is reqested for a metric which is not running
 * 
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class MetricNotRunningException extends Exception {

	/**
	 * Name of metric which is not running
	 */
	private String metricName = null;

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 5738495649115487467L;

	/**
	 * Creates new instance of {@link MetricNotRunningException}
	 */
	public MetricNotRunningException() {
		super();
	}

	/**
	 * Returns metric name
	 * 
	 * @return Metric name
	 */
	public String getMetricName() {
		return metricName;
	}

	/**
	 * Sets metric name
	 * 
	 * @param metricName
	 *            Metric name to set
	 */
	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}

	/**
	 * Creates new instance of {@link MetricNotRunningException}
	 * 
	 * @param metricName
	 *            Name of metric which is not working
	 */
	public MetricNotRunningException(String metricName) {
		super("Metric: " + metricName + " is not running!");
	}

	/**
	 * Creates new instance of {@link MetricNotRunningException}
	 * 
	 * @param cause
	 *            Exception being cause of problems
	 */
	public MetricNotRunningException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates new instance of {@link MetricNotRunningException}
	 * 
	 * @param message
	 *            Message describing problem
	 * @param cause
	 *            Exception being cause of problems
	 */
	public MetricNotRunningException(String message, Throwable cause) {
		super(message, cause);
	}

}
