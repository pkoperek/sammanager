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

package pl.edu.agh.samm.common.db;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.edu.agh.samm.common.action.Action;
import pl.edu.agh.samm.common.action.ActionExecution;
import pl.edu.agh.samm.common.metrics.IMetric;
import pl.edu.agh.samm.common.metrics.MeasurementValue;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public interface IStorageService {

	void storeActionExecution(Action executedAction, Date startDate, Date endDate);

	void storeMetricValue(IMetric metric, Number value);

	void storeMeasurement(String instanceURI, String capabilityURI, Date timestamp, Object value);

	List<MeasurementValue> getHistoricalMeasurementValues(String instanceUri, Date startTime, Date endTime);

	Map<String, List<ActionExecution>> getAllActionExecutions();

	Set<String> getKnownResources(Date actionStartTime, Date consequenceStartTime, long windowWidth);

	Set<String> getAllKnownResources();

	Map<String, Number> getAverageMeasurementValue(String resource, Date beforeActionStartTime,
			Date actionStartTime);

	Set<String> getResourceCapabilites(String resourceURI);

	List<MeasurementValue> getHistoricalMeasurementValues(String resourceURI, String capabilityURI);

	List<MeasurementValue> getHistoricalMeasurementValues(String resourceURI, String capabilityURI,
			Date startTime, Date endTime);

	List<IMetric> getAllKnownMetrics();

	List<Number> getHistoricalMetricValues(String metricURI, String resourceURI);
}
