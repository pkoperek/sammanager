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

package pl.edu.agh.samm.knowledge.impl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import pl.edu.agh.samm.common.core.IResourceListener;
import pl.edu.agh.samm.common.knowledge.ICriterion;
import pl.edu.agh.samm.common.knowledge.IKnowledge;
import pl.edu.agh.samm.knowledge.IOntModelProvider;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class KnowledgeImpl implements IKnowledge {

	private IOntModelProvider ontModelProvider;

	@Override
	public OntModel getOntologyModel() {
		return ontModelProvider.getOntModel();
	}

	public IOntModelProvider getOntModelProvider() {
		return ontModelProvider;
	}

	public void setOntModelProvider(IOntModelProvider ontModelProvider) {
		this.ontModelProvider = ontModelProvider;
	}

	@Override
	public void addOntologyChangeListener(IResourceListener resourceListener) {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public void removeOntologyChangeListener(IResourceListener resourceListener) {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public String getOntologyURI() {
		return "http://www.icsr.agh.edu.pl/samm_1.owl";
	}

	@Override
	public List<String> getCapabilitiesOfResourceType(String type) {
		return selectObjectsForPropertyOfSubject(type, getOntologyURI() + "#hasResourceTypeCapability");
	}

	private List<String> selectObjectsForPropertyOfSubject(String subject, String property) {
		List<String> retVal = new LinkedList<String>();
		OntModel model = ontModelProvider.getOntModel();

		Property propertyInstance = model.getProperty(property);

		Individual individual = model.getIndividual(subject);

		StmtIterator statementIterator = model.listStatements(new SimpleSelector(individual,
				propertyInstance, (Object) null));

		while (statementIterator.hasNext()) {
			String resourceTypeURI = statementIterator.nextStatement().getObject().asNode().getURI();
			retVal.add(resourceTypeURI);
		}

		return retVal;
	}

	private List<String> selectObjectsForStringPropertyOfSubject(String subject, String property) {
		List<String> retVal = new LinkedList<String>();
		OntModel model = ontModelProvider.getOntModel();

		Property propertyInstance = model.getProperty(property);

		Individual individual = model.getIndividual(subject);

		StmtIterator statementIterator = model.listStatements(new SimpleSelector(individual,
				propertyInstance, (Object) null));

		while (statementIterator.hasNext()) {
			String resourceTypeURI = statementIterator.nextStatement().getObject().asNode()
					.getLiteralLexicalForm();
			retVal.add(resourceTypeURI);
		}

		return retVal;
	}

	@Override
	public List<String> getUsedCapabilities(String metricURI) {
		return selectObjectsForPropertyOfSubject(metricURI, getOntologyURI() + "#usesTypeCapability");
	}

	@Override
	public List<String> getChildrenResourceTypes(String type) {
		return selectObjectsForPropertyOfSubject(type, getOntologyURI() + "#hasChildResourceOfType");
	}

	@Override
	public String getClassNameForCustomMetric(String uri) {
		// there should be only one class ... in case there are more - we take
		// the first one ;)
		List<String> clazz = selectObjectsForStringPropertyOfSubject(uri, getOntologyURI()
				+ "#hasCustomClass");

		return clazz.get(0);
	}

	@Override
	public boolean isCustomMetric(String uri) {
		List<String> classes = selectObjectsForStringPropertyOfSubject(uri, getOntologyURI()
				+ "#hasCustomClass");

		return classes.size() > 0;
	}

	@Override
	public List<String> getAllAvailableMetrics() {
		return getInstancesOfType(getOntologyURI() + "#CMetric");
	}

	@Override
	public Set<String> getMetricsForResourceType(String type) {
		Set<String> capabilities = new HashSet<String>();

		capabilities.addAll(selectObjectsForPropertyOfSubject(type, getOntologyURI()
				+ "#hasResourceTypeCapability"));

		return getMetricsUsingCapabilitiesForResourceType(type, capabilities);
	}

	@Override
	public Set<String> getMetricsUsingCapabilitiesForResourceType(String resourceType,
			Set<String> capabilities) {
		String capabilityIsUsedBy = getOntologyURI() + "#typeCapabilityIsUsedBy";

		Set<String> allMetrics = new HashSet<String>();
		for (String capability : capabilities) {
			List<String> metrics = selectObjectsForPropertyOfSubject(capability, capabilityIsUsedBy);
			allMetrics.addAll(metrics);
		}

		Set<String> retVal = new HashSet<String>();

		List<String> resourceTypeCapabilities = getCapabilitiesOfResourceType(resourceType);

		for (String metric : allMetrics) {
			if (resourceTypeCapabilities.containsAll(capabilities)) {
				retVal.add(metric);
			}
		}
		return retVal;
	}

	@Override
	public ICriterion getMetricValueAcceptationCriterion(String metricURI) {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public List<String> getMetricsWithDefinedLimits() {
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public List<String> getAllAvailableActions() {
		return getInstancesOfType(getOntologyURI() + "#CAction");
	}

	@Override
	public String getParameterType(String parameterURI) {
		List<String> ret = selectObjectsForPropertyOfSubject(parameterURI, getOntologyURI()
				+ "#hasActionParameterType");
		return ret.get(0);
	}

	@Override
	public List<String> getParametersOfAction(String actionURI) {
		List<String> ret = selectObjectsForPropertyOfSubject(actionURI, getOntologyURI()
				+ "#hasActionParameter");
		return ret;
	}

	private List<String> getInstancesOfType(String typeURI) {
		List<String> instances = new LinkedList<String>();
		OntModel model = ontModelProvider.getOntModel();
		OntClass ontClass = model.getOntClass(typeURI);
		ExtendedIterator instancesIterator = ontClass.listInstances();
		while (instancesIterator.hasNext()) {
			Individual instance = (Individual) instancesIterator.next();
			instances.add(instance.getURI());
		}
		return instances;
	}

}
