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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import pl.edu.agh.samm.common.action.ActionExecution;
import pl.edu.agh.samm.db.impl.mapper.ActionExecutionExtractor;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class ActionExecutionDAO extends AbstractDao implements IActionExecutionDAO {

	private static final String SQL_INSERT = "INSERT INTO action_execution(action_uri, start_time, end_time) VALUES (:actionUri, :start, :end)";

	private static final String SQL_INSERT_PARAM = "INSERT INTO action_parameter(action_id, param_name, param_value) VALUES (?, ? ,?)";

	private static final String SQL_QUERY_ACTION_URIS = "SELECT distinct action_uri FROM action_execution";

	private static final String SQL_QUERY_ACTION_EXECUTIONS = "SELECT action_execution.id as id, "
			+ "action_uri, start_time, end_time, param_name, " + "param_value "
			+ "FROM action_execution, action_parameter "
			+ "WHERE action_execution.id=action_parameter.action_id " + "AND action_uri=:actionUri "
			+ "ORDER BY 1";

	@Override
	public void store(ActionExecution execution) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("actionUri", execution.getAction().getActionURI());
		params.put("start", execution.getStartTime());
		params.put("end", execution.getEndTime());
		SqlParameterSource sps = new MapSqlParameterSource(params);

		getSimpleJdbcTemplate().update(SQL_INSERT, sps);

		Integer actionId = getLastId();

		List<Object[]> batchParams = new LinkedList<Object[]>();

		for (Map.Entry<String, String> entry : execution.getAction().getParameterValues().entrySet()) {
			batchParams.add(new Object[] { actionId, entry.getKey(), entry.getValue() });
		}

		getSimpleJdbcTemplate().batchUpdate(SQL_INSERT_PARAM, batchParams);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ActionExecution> getAllActionExecutions(String actionUri) {
		SqlParameterSource sps = new MapSqlParameterSource("actionUri", actionUri);
		return (List<ActionExecution>) getSimpleJdbcTemplate().getNamedParameterJdbcOperations().query(
				SQL_QUERY_ACTION_EXECUTIONS, sps, new ActionExecutionExtractor());
	}

	@Override
	public Set<String> loadAllActionsUris() {
		return new HashSet<String>(getSimpleJdbcTemplate().query(SQL_QUERY_ACTION_URIS,
				new ParameterizedRowMapper<String>() {

					@Override
					public String mapRow(ResultSet arg0, int arg1) throws SQLException {
						return arg0.getString(1);
					}
				}, Collections.emptyMap()));
	}

}
