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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.samm.api.metrics.ICustomMetric;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class TomcatAvgProcessingTimePerRequestMetric implements ICustomMetric {

	private static final Logger logger = LoggerFactory
			.getLogger(TomcatAvgProcessingTimePerRequestMetric.class);

	private static final String REQUESTS_COUNT_CAPABILITY_URI = "http://www.icsr.agh.edu.pl/samm_1.owl#RequestCountTypeCapability";
	private static final String PROCESSING_TIME_CAPABILITY_URI = "http://www.icsr.agh.edu.pl/samm_1.owl#ProcessingTimeTypeCapability";

	private long lastValueRequests = -1;
	private long lastValueProcessingTime = -1;
	private double THRESHOLD = 1;

	@Override
	public Number computeValue(Map<String, Number> capabilitiesValues) {
		long requests = capabilitiesValues.get(REQUESTS_COUNT_CAPABILITY_URI).longValue();
		long processingTime = capabilitiesValues.get(PROCESSING_TIME_CAPABILITY_URI).longValue();

		if (requests == 0) {
			return 0;
		}

		if (lastValueRequests != -1) {
			if (requests - lastValueRequests == 0) {
				return 0;
			}

			double ret = (processingTime - lastValueProcessingTime) / (requests - lastValueRequests);

			ret /= 1000.0;

			lastValueProcessingTime = processingTime;
			lastValueRequests = requests;

			return checkThreshold(ret);
		} else {
			lastValueProcessingTime = processingTime;
			lastValueRequests = requests;
			return checkThreshold((processingTime / requests) / 1000.0);
		}

	}

	private Number checkThreshold(Number val) {
		logger.debug(val.toString());
		if (val.doubleValue() > THRESHOLD)
			return val.doubleValue() - THRESHOLD + 1.0;
		return 0;
	}
}
