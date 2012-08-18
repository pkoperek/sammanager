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

package pl.edu.agh.samm.core;

import java.io.Serializable;

import pl.edu.agh.samm.api.core.IAlarm;
import pl.edu.agh.samm.api.metrics.IMetric;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class Alarm implements IAlarm, Serializable {
	private static final long serialVersionUID = 3815913955406226979L;

	private IMetric metric;
	private String ruleName;
	private Number value;

	public Alarm(IMetric metric, String ruleName, Number value) {
		this.metric = metric;
		this.ruleName = ruleName;
		this.value = value;
	}

	/**
	 * @return the metric
	 */
	@Override
	public IMetric getMetric() {
		return metric;
	}

	@Override
	public String getRuleName() {
		return ruleName;
	}

	@Override
	public Number getValue() {
		return value;
	}

}
