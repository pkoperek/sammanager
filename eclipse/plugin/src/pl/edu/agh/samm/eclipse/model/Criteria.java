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
package pl.edu.agh.samm.eclipse.model;

import pl.edu.agh.samm.common.knowledge.ICriterion;
import pl.edu.agh.samm.common.knowledge.RangeCriteria;
import pl.edu.agh.samm.common.knowledge.ThresholdOverCriteria;
import pl.edu.agh.samm.common.knowledge.ThresholdUnderCriteria;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public enum Criteria {
	THRESHOLD_UNDER(true, false), THRESHOLD_OVER(false, true), RANGE(true, true);

	private Criteria(boolean lowerThresholdEnabled, boolean upperThresholdEnabled) {
		this.lowerThresholdEnabled = lowerThresholdEnabled;
		this.upperThresholdEnabled = upperThresholdEnabled;
	}

	private boolean lowerThresholdEnabled;
	private boolean upperThresholdEnabled;

	public boolean isLowerThresholdEnabled() {
		return lowerThresholdEnabled;
	}

	public boolean isUpperThresholdEnabled() {
		return upperThresholdEnabled;
	}

	public ICriterion createCriterion(Double lowerThreshold, Double upperThreshold) {
		ICriterion criterion = null;
		switch (this) {
		case THRESHOLD_OVER:
			criterion = new ThresholdOverCriteria(upperThreshold);
			break;
		case THRESHOLD_UNDER:
			criterion = new ThresholdUnderCriteria(lowerThreshold);
			break;
		case RANGE:
			criterion = new RangeCriteria(upperThreshold, lowerThreshold);
			break;
		}
		return criterion;
	}
}
