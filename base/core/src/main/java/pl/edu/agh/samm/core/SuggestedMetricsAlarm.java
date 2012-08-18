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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pl.edu.agh.samm.api.core.ISuggestedMetricsAlarm;
import pl.edu.agh.samm.api.metrics.IMetric;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class SuggestedMetricsAlarm implements ISuggestedMetricsAlarm, Serializable {
	private static final long serialVersionUID = 3815913955406226979L;
	private IMetric metric;
	private String description;
	private Map<IMetric, Number> ranks;

	public SuggestedMetricsAlarm(IMetric metric, Map<IMetric, Number> ranks, String description) {
		this.metric = metric;
		this.description = description;
		this.ranks = ranks;
	}

	/**
	 * @return the metric
	 */
	@Override
	public IMetric getMetric() {
		return metric;
	}

	/**
	 * @return the metricsToStart
	 */
	@Override
	public List<IMetric> getMetricsToStart() {
		return new LinkedList<IMetric>(ranks.keySet());
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public Number getSuggestedMetricRank(IMetric suggestedMetric) {
		return ranks.get(suggestedMetric);
	}

}
