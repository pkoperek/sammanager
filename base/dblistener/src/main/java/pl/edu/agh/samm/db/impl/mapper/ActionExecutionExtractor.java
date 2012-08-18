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

package pl.edu.agh.samm.db.impl.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import pl.edu.agh.samm.api.action.Action;
import pl.edu.agh.samm.api.action.ActionExecution;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class ActionExecutionExtractor implements ResultSetExtractor {

	@Override
	public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
		List<ActionExecution> ael = new LinkedList<ActionExecution>();
		ActionExecution currentActionExecution = null;
		HashMap<String, String> currentParameters = null;
		Action currentAction = null;
		Integer currentActionExecId = null;
		while (rs.next()) {
			Integer id = rs.getInt("id");

			if (!id.equals(currentActionExecId)) {
				if (currentActionExecution != null) {
					ael.add(currentActionExecution);
				}

				// this is new execution
				currentActionExecId = id;
				Date start = new Date(rs.getTimestamp("start_time").getTime());// FIXME
																				// why
																				// the
																				// heck
																				// it
																				// has
																				// to
																				// be
																				// like
																				// that
																				// to
																				// get
																				// correct
																				// date??
																				// HSQL?
				Date end = new Date(rs.getTimestamp("end_time").getTime());
				currentAction = new Action();
				currentAction.setActionURI(rs.getString("action_uri"));

				currentParameters = new HashMap<String, String>();

				currentAction.setParameterValues(currentParameters);

				currentActionExecution = new ActionExecution(currentAction, start, end);
			}

			String paramName = rs.getString("param_name");
			String paramValue = rs.getString("param_value");

			currentParameters.put(paramName, paramValue);

		}
		if (!ael.contains(currentActionExecution)) {
			ael.add(currentActionExecution);
		}
		return ael;
	}

}
