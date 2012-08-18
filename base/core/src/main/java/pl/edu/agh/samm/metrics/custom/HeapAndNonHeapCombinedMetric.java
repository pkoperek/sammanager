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
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class HeapAndNonHeapCombinedMetric implements ICustomMetric {

	public static final String HEAP_USAGE_CAPABILITY = "http://www.icsr.agh.edu.pl/samm_1.owl#HeapUsageTypeCapability";
	public static final String NON_HEAP_USAGE = "http://www.icsr.agh.edu.pl/samm_1.owl#NonHeapUsageTypeCapability";

	@Override
	public Number computeValue(Map<String, Number> capabilitiesValues) {
		Number heapUsage = capabilitiesValues.get(HEAP_USAGE_CAPABILITY);
		Number nonHeapUsage = capabilitiesValues.get(NON_HEAP_USAGE);

		return heapUsage.longValue() + nonHeapUsage.longValue();
	}

}
