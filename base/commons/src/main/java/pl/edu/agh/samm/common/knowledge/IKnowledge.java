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

package pl.edu.agh.samm.common.knowledge;

import java.util.List;
import java.util.Set;

import pl.edu.agh.samm.common.core.IResourceListener;

import com.hp.hpl.jena.ontology.OntModel;

/**
 * Knowledge service interface - facade for this component
 * 
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public interface IKnowledge {
	String getOntologyURI();

	OntModel getOntologyModel();

	void addOntologyChangeListener(IResourceListener resourceListener);

	void removeOntologyChangeListener(IResourceListener resourceListener);

	List<String> getChildrenResourceTypes(String type);

	List<String> getCapabilitiesOfResourceType(String type);

	List<String> getUsedCapabilities(String metricURI);

	boolean isCustomMetric(String uri);

	String getClassNameForCustomMetric(String uri);

	List<String> getAllAvailableMetrics();

	Set<String> getMetricsForResourceType(String type);

	Set<String> getMetricsUsingCapabilitiesForResourceType(String resourceType, Set<String> capabilities);

	List<String> getMetricsWithDefinedLimits();

	ICriterion getMetricValueAcceptationCriterion(String metricURI);

	List<String> getAllAvailableActions();

	List<String> getParametersOfAction(String actionURI);

	String getParameterType(String parameterURI);

}