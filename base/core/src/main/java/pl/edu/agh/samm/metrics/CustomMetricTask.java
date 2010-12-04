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

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.samm.common.core.Resource;
import pl.edu.agh.samm.common.metrics.IConfiguredMetric;
import pl.edu.agh.samm.common.metrics.ICustomMetric;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class CustomMetricTask extends MetricTask {

	private static final Logger logger = LoggerFactory.getLogger(CustomMetricTask.class);
	private String customClassName = null;
	private ICustomMetric customMetricInstance = null;

	public CustomMetricTask(IConfiguredMetric metric, List<String> usedCapabilities, Resource resource,
			String customClassName) {
		super(metric, usedCapabilities, resource);
		this.customClassName = customClassName;
	}

	@Override
	public void init() {
		super.init();

		Class<?> clazz;
		try {
			clazz = Class.forName(customClassName);

			customMetricInstance = (ICustomMetric) clazz.newInstance();
		} catch (Exception e) {
			logger.error("Couldn't instantiate custom class! Class: " + customClassName, e);
			throw new RuntimeException("Couldn't instantiate custom class! Class: " + customClassName, e);
		}
	}

	@Override
	public Number computeMetricValue(Map<String, Number> values) {
		return customMetricInstance.computeValue(values);
	}

}
