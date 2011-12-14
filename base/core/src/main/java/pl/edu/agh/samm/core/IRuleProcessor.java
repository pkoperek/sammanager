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

import pl.edu.agh.samm.common.core.IAlarmListener;
import pl.edu.agh.samm.common.core.Rule;
import pl.edu.agh.samm.common.metrics.IMetricListener;
import pl.edu.agh.samm.common.sla.IServiceLevelAgreement;
import pl.edu.agh.samm.common.tadapter.IMeasurementListener;

/**
 * Rules processing engine
 * 
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public interface IRuleProcessor extends IMetricListener, IMeasurementListener {
	void setActionGracePeriod(int gracePeriod);
	
	void setupSLA(IServiceLevelAgreement serviceLevelAgreement);

	void addRule(Rule rule);

	void clearRules();

	void removeRule(String ruleName);

	void addAlarmListener(IAlarmListener alarmListener);

	void removeAlarmListener(IAlarmListener alarmListener);
}
