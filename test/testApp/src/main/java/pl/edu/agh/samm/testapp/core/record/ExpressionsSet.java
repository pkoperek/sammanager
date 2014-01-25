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
package pl.edu.agh.samm.testapp.core.record;

import java.util.ArrayList;
import java.util.List;

/**
 * @author koperek
 * 
 */
public class ExpressionsSet {
	private List<String> expressions = new ArrayList<String>();

	public static final String SLEEP_AFTER = "sleepAfterDefault";
	private long sleepAfterInterval;

	{
		String sleepAfterString = System.getProperty(SLEEP_AFTER);
		if (sleepAfterString != null) {
			sleepAfterInterval = Long.parseLong(sleepAfterString);
		} else {
			sleepAfterInterval = 60000; // 1 min
		}
	}

	public void setSleepAfterInterval(long sleepAfterInterval) {
		this.sleepAfterInterval = sleepAfterInterval;
	}

	public long getSleepAfterInterval() {
		return sleepAfterInterval;
	}

	public List<String> getExpressions() {
		return expressions;
	}

	public void setExpressions(List<String> expressions) {
		this.expressions = expressions;
	}

	public void addExpression(String expr) {
		this.expressions.add(expr);
	}
}
