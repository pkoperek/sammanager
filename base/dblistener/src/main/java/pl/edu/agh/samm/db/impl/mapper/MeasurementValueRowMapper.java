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

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import pl.edu.agh.samm.api.metrics.MeasurementValue;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class MeasurementValueRowMapper implements ParameterizedRowMapper<MeasurementValue> {

	@Override
	public MeasurementValue mapRow(ResultSet rs, int arnrg1) throws SQLException {
		MeasurementValue mv = new MeasurementValue();
		mv.setCapabilityUri(rs.getString("capability_uri"));
		mv.setId(rs.getLong("id"));
		mv.setInstanceUri(rs.getString("instance_uri"));
		mv.setTimestamp(new Date(rs.getTimestamp("timestamp").getTime())); // FIXME
																			// why
																			// the
																			// heck
																			// is
																			// has
																			// to
																			// be
																			// like
																			// that?
																			// HSQL's
																			// fault?
		mv.setValue(rs.getDouble("value"));
		return mv;
	}

}
