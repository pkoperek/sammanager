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
package pl.edu.agh.samm.eclipse.editors;

import java.util.Map;

import pl.edu.agh.samm.common.decision.IServiceLevelAgreement;
import pl.edu.agh.samm.common.knowledge.ICriterion;

/**
 * Interface for management
 * 
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public interface ISLAManager extends IServiceLevelAgreement {
	/**
	 * Returns an instance of {@link IServiceLevelAgreement} being managed
	 * 
	 * @return Instance of {@link IServiceLevelAgreement} being managed
	 */
	IServiceLevelAgreement getServiceLevelAgreement();

	void addSLAChangesListener(ISLAChangesListener listener);

	void removeSLAChangesListener(ISLAChangesListener listener);

	void addInvolvedResource(String uri, String resourceType);

	void setResourceParameters(String uri, Map<String, Object> parameters);

	void removeInvolvedResource(String uri);

	void setCriterionForResourceMetric(String selectedResource, String selectedMetric, ICriterion criterion);

	void removeCriterionForResourceMetric(String selectedResource, String selectedMetric);

	void setResourceMetricCost(String selectedResource, String selectedMetric, Number cost);

	void setServiceLevelAgreement(IServiceLevelAgreement serviceLevelAgreement);
}
