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

package pl.edu.agh.samm.knowledgera.jmx;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.samm.api.core.IResourceListener;
import pl.edu.agh.samm.api.knowledge.IKnowledge;

import com.hp.hpl.jena.ontology.OntModel;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class Knowledge implements KnowledgeMBean {

	private IKnowledge knowledge;

	private String OBJECT_NAME = "pl.edu.agh.samm:type=Knowledge";
	private Logger logger = LoggerFactory.getLogger(Knowledge.class.getName());
	private ObjectName objectName;

	/**
	 * @param actionURI
	 * @return
	 * @see pl.edu.agh.samm.api.knowledge.IKnowledge#getParametersOfAction(java.lang.String)
	 */
	@Override
	public List<String> getParametersOfAction(String actionURI) {
		return knowledge.getParametersOfAction(actionURI);
	}

	/**
	 * @param parameterURI
	 * @return
	 * @see pl.edu.agh.samm.api.knowledge.IKnowledge#getParameterType(java.lang.String)
	 */
	@Override
	public String getParameterType(String parameterURI) {
		return knowledge.getParameterType(parameterURI);
	}

	public void init() throws Exception {
		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		objectName = new ObjectName(OBJECT_NAME);
		mBeanServer.registerMBean(this, objectName);
		logger.info("Knowledge Remote Access JMX enabled");
	}

	public void destroy() throws Exception {
		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		mBeanServer.unregisterMBean(objectName);
		logger.info("Knowledge Remote Access JMX disabled");
	}

	/**
	 * @param resourceListener
	 * @see pl.edu.agh.samm.api.knowledge.IKnowledge#addOntologyChangeListener(pl.edu.agh.samm.api.core.IResourceListener)
	 */
	@Override
	public void addOntologyChangeListener(IResourceListener resourceListener) {
		knowledge.addOntologyChangeListener(resourceListener);
	}

	/**
	 * @return
	 * @see pl.edu.agh.samm.api.knowledge.IKnowledge#getAllAvailableMetrics()
	 */
	@Override
	public List<String> getAllAvailableMetrics() {
		return knowledge.getAllAvailableMetrics();
	}

	/**
	 * @param type
	 * @return
	 * @see pl.edu.agh.samm.api.knowledge.IKnowledge#getCapabilitiesOfResourceType(java.lang.String)
	 */
	@Override
	public List<String> getCapabilitiesOfResourceType(String type) {
		return knowledge.getCapabilitiesOfResourceType(type);
	}

	/**
	 * @param type
	 * @return
	 * @see pl.edu.agh.samm.api.knowledge.IKnowledge#getChildrenResourceTypes(java.lang.String)
	 */
	@Override
	public List<String> getChildrenResourceTypes(String type) {
		return knowledge.getChildrenResourceTypes(type);
	}

	/**
	 * @param uri
	 * @return
	 * @see pl.edu.agh.samm.api.knowledge.IKnowledge#getClassNameForCustomMetric(java.lang.String)
	 */
	@Override
	public String getClassNameForCustomMetric(String uri) {
		return knowledge.getClassNameForCustomMetric(uri);
	}

	/**
	 * @param type
	 * @return
	 * @see pl.edu.agh.samm.api.knowledge.IKnowledge#getMetricsForResourceType(java.lang.String)
	 */
	@Override
	public Set<String> getMetricsForResourceType(String type) {
		return knowledge.getMetricsForResourceType(type);
	}

	@Override
	public Set<String> getMetricsUsingCapabilitiesForResourceType(String resourceURI,
			Set<String> capabilityURIs) {
		return knowledge.getMetricsUsingCapabilitiesForResourceType(resourceURI, capabilityURIs);
	}

	/**
	 * @return
	 * @see pl.edu.agh.samm.api.knowledge.IKnowledge#getOntologyModel()
	 */
	@Override
	public OntModel getOntologyModel() {
		return knowledge.getOntologyModel();
	}

	/**
	 * @return
	 * @see pl.edu.agh.samm.api.knowledge.IKnowledge#getOntologyURI()
	 */
	@Override
	public String getOntologyURI() {
		return knowledge.getOntologyURI();
	}

	/**
	 * @param metricURI
	 * @return
	 * @see pl.edu.agh.samm.api.knowledge.IKnowledge#getUsedCapabilities(java.lang.String)
	 */
	@Override
	public List<String> getUsedCapabilities(String metricURI) {
		return knowledge.getUsedCapabilities(metricURI);
	}

	/**
	 * @param uri
	 * @return
	 * @see pl.edu.agh.samm.api.knowledge.IKnowledge#isCustomMetric(java.lang.String)
	 */
	@Override
	public boolean isCustomMetric(String uri) {
		return knowledge.isCustomMetric(uri);
	}

	/**
	 * @param resourceListener
	 * @see pl.edu.agh.samm.api.knowledge.IKnowledge#removeOntologyChangeListener(pl.edu.agh.samm.api.core.IResourceListener)
	 */
	@Override
	public void removeOntologyChangeListener(IResourceListener resourceListener) {
		knowledge.removeOntologyChangeListener(resourceListener);
	}

	public void setKnowledge(IKnowledge knowledge) {
		this.knowledge = knowledge;
	}

	public IKnowledge getKnowledge() {
		return knowledge;
	}

	/**
	 * @return
	 * @see pl.edu.agh.samm.api.knowledge.IKnowledge#getMetricsWithDefinedLimits()
	 */
	@Override
	public List<String> getMetricsWithDefinedLimits() {
		return knowledge.getMetricsWithDefinedLimits();
	}

	/**
	 * @return
	 * @see pl.edu.agh.samm.api.knowledge.IKnowledge#getAllAvailableActions()
	 */
	@Override
	public List<String> getAllAvailableActions() {
		return knowledge.getAllAvailableActions();
	}

}
