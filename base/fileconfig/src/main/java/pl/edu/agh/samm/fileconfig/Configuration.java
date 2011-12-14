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

/**
 * @author koperek
 * 
 */
public class Configuration {
	private ConfigurationResourceSet resourceSet = null;
	private RuleSet ruleSet = null;
	private ConfigurationMetricSet metricSet = null;
	private Integer gracePeriod = null;

	public Integer getGracePeriod() {
		return gracePeriod;
	}

	public void setGracePeriod(Integer gracePeriod) {
		this.gracePeriod = gracePeriod;
	}

	public ConfigurationMetricSet getMetricSet() {
		return metricSet;
	}

	public ConfigurationResourceSet getResourceSet() {
		return resourceSet;
	}

	public void setResourceSet(ConfigurationResourceSet resourceSet) {
		this.resourceSet = resourceSet;
	}

	public RuleSet getRuleSet() {
		return ruleSet;
	}

	public void setRuleSet(RuleSet ruleSet) {
		this.ruleSet = ruleSet;
	}

	public void setMetricSet(ConfigurationMetricSet metricSet) {
		this.metricSet = metricSet;
	}

}
