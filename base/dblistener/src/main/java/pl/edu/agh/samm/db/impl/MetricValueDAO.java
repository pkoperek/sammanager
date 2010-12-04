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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.ParameterizedSingleColumnRowMapper;

import pl.edu.agh.samm.common.metrics.IConfiguredMetric;
import pl.edu.agh.samm.common.metrics.MetricImpl;
import pl.edu.agh.samm.common.metrics.MetricValue;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class MetricValueDAO extends AbstractDao implements IMetricValueDAO {

	private static final Logger log = LoggerFactory.getLogger(MetricValueDAO.class);

	private static final String SQL_INSERT = "INSERT INTO metric_value(metric_uri,resource_uri, timestamp, value) values (:metricUri, :resourceUri, :timestamp, :value)";

	private static final String SQL_QUERY_VALUES = "SELECT value FROM metric_value WHERE metric_uri=:metricUri AND resource_uri=:resourceUri";

	@Override
	public void store(MetricValue metricValue) {
		SqlParameterSource sps = new BeanPropertySqlParameterSource(metricValue);
		getSimpleJdbcTemplate().update(SQL_INSERT, sps);

	}

	@Override
	public List<Number> loadValues(String metricURI, String resourceURI) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("metricUri", metricURI);
		params.put("resourceUri", resourceURI);
		try {
			List<Number> values = getSimpleJdbcTemplate().query(SQL_QUERY_VALUES,
					ParameterizedSingleColumnRowMapper.newInstance(Number.class), params);
			return values;
		} catch (EmptyResultDataAccessException e) {
			log.info("No metric values meet criteria: metricUri=" + metricURI);
			return Collections.emptyList();
		}
	}

	@Override
	public List<IConfiguredMetric> getKnownMetrics() {
		return getSimpleJdbcTemplate().query("SELECT DISTINCT metric_uri, resource_uri FROM metric_value",
				new ParameterizedRowMapper<IConfiguredMetric>() {

					@Override
					public IConfiguredMetric mapRow(ResultSet rs, int arg1) throws SQLException {
						final String metricUri = rs.getString("metric_uri");
						final String resourceUri = rs.getString("resource_uri");
						return new MetricImpl(metricUri, resourceUri);
					};
				}, Collections.emptyMap());
	}
}
