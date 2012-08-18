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

package pl.edu.agh.samm.core;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.samm.api.core.IResourceEvent;
import pl.edu.agh.samm.api.core.IResourceInstancesManager;
import pl.edu.agh.samm.api.core.Resource;
import pl.edu.agh.samm.api.knowledge.IKnowledge;
import pl.edu.agh.samm.api.metrics.ResourceEventType;
import pl.edu.agh.samm.api.tadapter.ITransportAdapter;

/**
 * Performs discovery on newly registered resources
 * 
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class ResourceDiscoveryAgent implements IResourceDiscoveryAgent {

	private static final Logger logger = LoggerFactory
			.getLogger(ResourceDiscoveryAgent.class);

	private IKnowledge knowledgeService = null;
	private IResourceInstancesManager resourceInstancesManager = null;
	private Set<ITransportAdapter> transportAdapters = null;

	public void setKnowledgeService(IKnowledge knowledgeService) {
		this.knowledgeService = knowledgeService;
	}

	public IKnowledge getKnowledgeService() {
		return knowledgeService;
	}

	public Set<ITransportAdapter> getTransportAdapters() {
		return transportAdapters;
	}

	public void setTransportAdapters(Set<ITransportAdapter> transportAdapters) {
		this.transportAdapters = transportAdapters;
	}

	public IResourceInstancesManager getResourceInstancesManager() {
		return resourceInstancesManager;
	}

	public void setResourceInstancesManager(
			IResourceInstancesManager resourceInstancesManager) {
		this.resourceInstancesManager = resourceInstancesManager;
	}

	// @Override
	// public List<String> discoverResourceCapabilities(Resource resource)
	// throws ResourceNotRegisteredException {
	// IKnowledge knowledge = knowledgeProvider.getDefaultKnowledgeSource();
	// List<String> capabilitiesTypes = knowledge
	// .getCapabilitiesOfResource(resource.getType());
	//
	// List<ITransportAdapter> transportAdapters =
	// resource.getTransportAdapters();
	// List<String> capabilities = new LinkedList<String>();
	// for (ITransportAdapter adapter : transportAdapters) {
	// capabilities.addAll(adapter.discoverResourceCapabilities(resource,
	// capabilitiesTypes));
	// }
	// return capabilities;
	// }

	@Override
	public List<String> getResourceCapabilities(Resource resource) {
		return knowledgeService.getCapabilitiesOfResourceType(resource
				.getType());
	}

	@Override
	public void processEvent(IResourceEvent event) {
		logger.info("Got event: " + event);

		// this should be executed from a separate thread / executor
		if (event.getType().equals(ResourceEventType.RESOURCES_ADDED)) {
			Resource resource = (Resource) event.getAttachment();

			List<String> types = knowledgeService
					.getChildrenResourceTypes(resource.getType());

			// for every transport check if it supports the url
			for (ITransportAdapter adapter : transportAdapters) {
				if (adapter.isURISupported(resource)) {
					try {
						adapter.registerResource(resource);
						adapter.discoverChildren(resource, types);
						resource.addTransportAdapter(adapter);
					} catch (Exception e) {
						logger.error("Exception for transport adapter: "
								+ adapter + " caught!", e);
					}
				}
			}
		} else if (event.getType().equals(ResourceEventType.RESOURCES_REMOVED)) {
			Resource resource = (Resource) event.getAttachment();

			for (ITransportAdapter adapter : resource.getTransportAdapters()) {
				adapter.unregisterResource(resource);
			}
		} else if (event.getType().equals(
				ResourceEventType.RESOURCES_PROPERTIES_CHANGED)) {
			Resource resource = (Resource) event.getAttachment();

			List<String> types = knowledgeService
					.getChildrenResourceTypes(resource.getType());

			// for every transport check if it supports the url
			for (ITransportAdapter adapter : transportAdapters) {
				if (adapter.isURISupported(resource)) {
					try {
						if (!adapter.isResourceRegistered(resource)) {
							adapter.registerResource(resource);
						}
						adapter.discoverChildren(resource, types);
						resource.addTransportAdapter(adapter);
					} catch (Exception e) {
						logger.error("Exception for transport adapter: "
								+ adapter + " caught!", e);
					}
				}
			}
		}
	}

}
