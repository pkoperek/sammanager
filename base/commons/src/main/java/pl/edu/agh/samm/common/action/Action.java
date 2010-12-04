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

package pl.edu.agh.samm.common.action;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class Action implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5018657282713058699L;
	/**
	 * 
	 */
	private String actionURI = null;
	private Map<String, String> parameterValues = null;

	/**
	 * @return the actionURI
	 */
	public String getActionURI() {
		return actionURI;
	}

	/**
	 * @param actionURI
	 *            the actionURI to set
	 */
	public void setActionURI(String actionURI) {
		this.actionURI = actionURI;
	}

	/**
	 * @return the parameterValues
	 */
	public Map<String, String> getParameterValues() {
		return parameterValues;
	}

	/**
	 * @param parameterValues
	 *            the parameterValues to set
	 */
	public void setParameterValues(Map<String, String> parameterValues) {
		this.parameterValues = parameterValues;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Action [actionURI=").append(actionURI).append(", parameterValues=")
				.append(parameterValues).append("]");
		return builder.toString();
	}

}
