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

package pl.edu.agh.samm.db.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import pl.edu.agh.samm.common.metrics.MeasurementValue;
import pl.edu.agh.samm.db.impl.mapper.MeasurementValueRowMapper;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class MeasurementValueDAO extends AbstractDao implements
		IMeasurementValueDAO {

	private static final String SQL_INSERT = "INSERT INTO measurement_value(capability_uri,instance_uri,timestamp,value) values (:capabilityUri,:instanceUri,:timestamp,:value)";

	private static final String SQL_QUERY_RESOURCE_URI_BETWEEN_DATE = "SELECT instance_uri FROM measurement_value WHERE timestamp BETWEEN :start AND :end";

	private static final String SQL_QUERY_RESOURCE_URI = "SELECT DISTINCT instance_uri FROM measurement_value";

	private static final String SQL_QUERY_RESOURCE_CAPABILITIES = "SELECT DISTINCT capability_uri FROM measurement_value WHERE instance_uri=:instance";

	private static final String SQL_QUERY_AVERAGE_BY_CAPABILITY = "SELECT capability_uri, AVG(value) FROM measurement_value WHERE instance_uri=:instance AND timestamp BETWEEN :start AND :end GROUP By capability_uri";

	private static final String SQL_QUERY_MEASUREMENT_BETWEEN_DATE = "SELECT id, capability_uri, instance_uri, timestamp, value FROM measurement_value WHERE instance_uri=:instanceUri AND timestamp BETWEEN :start AND :end";

	private static final String SQL_QUERY_MEASUREMENT_BY_CAPABILITY = "SELECT id, capability_uri, instance_uri, timestamp, value FROM measurement_value WHERE instance_uri=:instanceUri AND capability_uri=:capabilityUri";

	private static final String SQL_QUERY_MEASUREMENT_BY_CAPABILITY_BETWEEN_DATE = "SELECT id, capability_uri, instance_uri, timestamp, value FROM measurement_value WHERE instance_uri=:instanceUri AND capability_uri=:capabilityUri AND timestamp BETWEEN :start AND :end";

	private static final String SQL_QUERY_MEASUREMENT = "SELECT id, capability_uri, instance_uri, timestamp, value FROM measurement_value WHERE instance_uri=:instanceUri";

	@Override
	public void store(MeasurementValue information) {
		SqlParameterSource sps = new BeanPropertySqlParameterSource(information);
		getSimpleJdbcTemplate().update(SQL_INSERT, sps);
	}

	@Override
	public Map<String, Number> getAverageMeasurementValue(String resource,
			Date beforeActionStartTime, Date actionStartTime) {
		final Map<String, Number> ret = new HashMap<String, Number>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("instance", resource);
		params.put("start", beforeActionStartTime);
		params.put("end", actionStartTime);
		SqlParameterSource sps = new MapSqlParameterSource(params);
		getSimpleJdbcTemplate().getNamedParameterJdbcOperations().query(
				SQL_QUERY_AVERAGE_BY_CAPABILITY, sps, new RowCallbackHandler() {

					@Override
					public void processRow(ResultSet arg0) throws SQLException {
						ret.put(arg0.getString(1), arg0.getDouble(2));

					}
				});
		return ret;
	}

	@Override
	public List<MeasurementValue> getMeasurementValues(String instanceUri,
			Date startTime, Date endTime) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("instanceUri", instanceUri);

		String query = SQL_QUERY_MEASUREMENT;
		if (startTime != null && endTime != null) {
			query = SQL_QUERY_MEASUREMENT_BETWEEN_DATE;
			params.put("start", startTime);
			params.put("end", endTime);
		}

		SqlParameterSource sps = new MapSqlParameterSource(params);
		return getSimpleJdbcTemplate().query(query,
				new MeasurementValueRowMapper(), sps);
	}

	@Override
	public Set<String> getResourcesUris(Date preActionStartTime,
			Date actionStartTime) {
		Map<String, Object> sps = new HashMap<String, Object>();
		sps.put("start", preActionStartTime);
		sps.put("end", actionStartTime);
		return new HashSet<String>(getSimpleJdbcTemplate().query(
				SQL_QUERY_RESOURCE_URI_BETWEEN_DATE,
				new ParameterizedRowMapper<String>() {

					@Override
					public String mapRow(ResultSet arg0, int arg1)
							throws SQLException {
						return arg0.getString(1);
					}
				}, sps));

	}

	@Override
	public Set<String> getResourcesUris() {
		return new HashSet<String>(getSimpleJdbcTemplate().query(
				SQL_QUERY_RESOURCE_URI, new ParameterizedRowMapper<String>() {

					@Override
					public String mapRow(ResultSet arg0, int arg1)
							throws SQLException {
						return arg0.getString(1);
					}
				}));
	}

	@Override
	public Set<String> getResourceCapabilities(String resourceURI) {
		Map<String, Object> sps = new HashMap<String, Object>();
		sps.put("instance", resourceURI);
		return new HashSet<String>(getSimpleJdbcTemplate().query(
				SQL_QUERY_RESOURCE_CAPABILITIES,
				new ParameterizedRowMapper<String>() {
					@Override
					public String mapRow(ResultSet arg0, int arg1)
							throws SQLException {
						return arg0.getString(1);
					}
				}, sps));
	}

	@Override
	public List<MeasurementValue> getHistoricalMeasurementValues(
			String resourceURI, String capabilityURI) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("instanceUri", resourceURI);
		params.put("capabilityUri", capabilityURI);

		SqlParameterSource sps = new MapSqlParameterSource(params);
		return getSimpleJdbcTemplate().query(
				SQL_QUERY_MEASUREMENT_BY_CAPABILITY,
				new MeasurementValueRowMapper(), sps);
	}

	@Override
	public List<MeasurementValue> getHistoricalMeasurementValues(
			String resourceURI, String capabilityURI, Date startTime,
			Date endTime) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("instanceUri", resourceURI);
		params.put("capabilityUri", capabilityURI);
		params.put("start", startTime);
		params.put("end", endTime);
		SqlParameterSource sps = new MapSqlParameterSource(params);
		return getSimpleJdbcTemplate().query(
				SQL_QUERY_MEASUREMENT_BY_CAPABILITY_BETWEEN_DATE,
				new MeasurementValueRowMapper(), sps);
	}

}
