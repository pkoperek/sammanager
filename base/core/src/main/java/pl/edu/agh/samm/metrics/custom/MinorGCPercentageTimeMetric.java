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

package pl.edu.agh.samm.metrics.custom;

import java.util.Map;

import pl.edu.agh.samm.common.metrics.ICustomMetric;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class MinorGCPercentageTimeMetric implements ICustomMetric {

	public static final String totalMinorGCCountCapability = "http://www.icsr.agh.edu.pl/samm_1.owl#MinorGCTotalCountTypeCapability";
	public static final String jvmUptimeCapability = "http://www.icsr.agh.edu.pl/samm_1.owl#UptimeTypeCapability";

	private Double previousGCCount = 0.0;
	private Double previousJVMUptime = 0.0;

	@Override
	public Number computeValue(Map<String, Number> capabilitiesValues) {
		Double averageMinorGCCount = null;

		Number totalGCCountObj = capabilitiesValues.get(totalMinorGCCountCapability);
		Number jvmUptimeObj = capabilitiesValues.get(jvmUptimeCapability);

		if (totalGCCountObj != null && jvmUptimeObj != null) {
			Double totalGCCount = Double.valueOf(totalGCCountObj.toString());
			Double jvmUptime = Double.valueOf(jvmUptimeObj.toString());

			if (jvmUptime.equals(previousJVMUptime)) {
				// extra sanity check
				jvmUptime += 1000.0;
			}

			averageMinorGCCount = ((totalGCCount - previousGCCount) / ((jvmUptime - previousJVMUptime) / 1000.0));
			previousGCCount = totalGCCount;
			previousJVMUptime = jvmUptime;
			// averageMinorGCCount = totalGCCount / jvmUptime;
		}

		return averageMinorGCCount;
	}

}
