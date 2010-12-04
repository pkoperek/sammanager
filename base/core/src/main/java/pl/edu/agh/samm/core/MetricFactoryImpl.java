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
package pl.edu.agh.samm.core;

import pl.edu.agh.samm.common.metrics.IConfiguredMetric;
import pl.edu.agh.samm.common.metrics.MetricImpl;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class MetricFactoryImpl implements IMetricFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pl.edu.agh.samm.core.impl.IMetricFactory#createMetric(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public IConfiguredMetric createMetric(String metricURI, String resourceURI) {
		return new MetricImpl(metricURI, resourceURI);
	}

}
