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

import pl.edu.agh.samm.api.metrics.ICustomMetric;

/**
 * 
 * @author koperek
 * 
 */
public class AvgWaitTimeMetric implements ICustomMetric {

	public static final String SERVED_COUNT_CAPABILITY = "http://www.icsr.agh.edu.pl/samm_1.owl#MasterServedCountCapability";
	public static final String SUM_WAIT_TIME_CAPABILITY = "http://www.icsr.agh.edu.pl/samm_1.owl#MasterSumWaitTimeCapability";

	private long prevServedCount = 0;
	private long prevSumWaitTime = 0;

	@Override
	public Number computeValue(Map<String, Number> capabilitiesValues) {
		long servedCount = capabilitiesValues.get(SERVED_COUNT_CAPABILITY)
				.longValue();
		long sumWaitTime = capabilitiesValues.get(SUM_WAIT_TIME_CAPABILITY)
				.longValue();

		double retVal = 0;

		double servedCountDiff = servedCount - prevServedCount;
		double sumWaitTimeDiff = sumWaitTime - prevSumWaitTime;

		if (sumWaitTimeDiff == 0 || servedCountDiff == 0) {
			return 0;
		}
		
		retVal = sumWaitTimeDiff / servedCountDiff;

		prevSumWaitTime = sumWaitTime;
		prevServedCount = servedCount;

		return retVal;
	}
}
