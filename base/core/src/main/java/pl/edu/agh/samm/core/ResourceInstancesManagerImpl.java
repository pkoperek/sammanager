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
 * 
 */
package pl.edu.agh.samm.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.samm.common.core.DefaultResourceEventImpl;
import pl.edu.agh.samm.common.core.IResourceEvent;
import pl.edu.agh.samm.common.core.IResourceInstancesManager;
import pl.edu.agh.samm.common.core.IResourceListener;
import pl.edu.agh.samm.common.core.Resource;
import pl.edu.agh.samm.common.core.ResourceAlreadyRegisteredException;
import pl.edu.agh.samm.common.core.ResourceNotRegisteredException;
import pl.edu.agh.samm.common.metrics.ResourceEventType;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class ResourceInstancesManagerImpl implements IResourceInstancesManager {

	private static final Logger logger = LoggerFactory.getLogger(ResourceInstancesManagerImpl.class);
	private Map<String, List<String>> resourcesTree = new HashMap<String, List<String>>();
	private Map<String, Resource> resources = new HashMap<String, Resource>();
	private List<IResourceListener> resourceListeners = new LinkedList<IResourceListener>();
	private Map<String, List<String>> resourceCapabilities = new HashMap<String, List<String>>();
	private Map<String, List<String>> resourcesOfType = new HashMap<String, List<String>>();
	private IResourceDiscoveryAgent resourceDiscoveryAgent = null;

	public IResourceDiscoveryAgent getResourceDiscoveryAgent() {
		return resourceDiscoveryAgent;
	}

	public void setResourceDiscoveryAgent(IResourceDiscoveryAgent resourceDiscoveryAgent) {
		this.resourceDiscoveryAgent = resourceDiscoveryAgent;
	}

	@Override
	public void addChildResource(String parentUri, String uri, String type, Map<String, Object> parameters)
			throws ResourceNotRegisteredException {
		if (resourcesTree.containsKey(parentUri)) {
			try {
				addResource(uri, type, parameters);
			} catch (ResourceAlreadyRegisteredException e) {
				// nothing bad happened - we were just adding a child which was
				// added before
			}

			List<String> children = resourcesTree.get(parentUri);
			if (children == null) {
				children = new ArrayList<String>();
			}

			children.add(uri);
			resourcesTree.put(parentUri, children);
		} else {
			throw new ResourceNotRegisteredException(parentUri);
		}
	}

	protected void fireNewResourceEvent(Resource resource) {
		IResourceEvent resourceEvent = new DefaultResourceEventImpl(ResourceEventType.RESOURCES_ADDED,
				resource);
		fireResourceEvent(resourceEvent);
	}

	protected void fireResourcesPropertiesChangedEvent(Resource resource) {
		IResourceEvent resourceEvent = new DefaultResourceEventImpl(
				ResourceEventType.RESOURCES_PROPERTIES_CHANGED, resource);
		fireResourceEvent(resourceEvent);
	}

	@Override
	public void addResource(String uri, String type, Map<String, Object> properties)
			throws ResourceAlreadyRegisteredException {
		if (!resourcesTree.containsKey(uri)) {
			this.resourcesTree.put(uri, null);
			Resource resource = new Resource(uri, type, properties);
			resources.put(uri, resource);

			List<String> resourcesOfTypeList = null;
			if (resourcesOfType.containsKey(type)) {
				resourcesOfTypeList = resourcesOfType.get(type);
			} else {
				resourcesOfTypeList = new LinkedList<String>();
				resourcesOfType.put(type, resourcesOfTypeList);
			}

			resourcesOfTypeList.add(uri);

			fireNewResourceEvent(resource);
		} else {
			throw new ResourceAlreadyRegisteredException(uri);
		}
	}

	@Override
	public boolean isResourceRegistered(String uri) {
		return this.resourcesTree.containsKey(uri);
	}

	@Override
	public void removeResource(String uri) {
		Resource removedResource = this.resources.remove(uri);
		List<String> children = this.resourcesTree.remove(uri);
		if (children != null) {
			for (String child : children) {
				removeResource(child);
			}
		}
		this.fireRemovedResourceEvent(removedResource);
	}

	private void fireRemovedResourceEvent(Resource resource) {
		IResourceEvent resourceEvent = new DefaultResourceEventImpl(ResourceEventType.RESOURCES_REMOVED,
				resource);
		fireResourceEvent(resourceEvent);
	}

	@Override
	public void addResourceListener(IResourceListener resourceListener) {
		this.resourceListeners.add(resourceListener);
	}

	@Override
	public void removeResourceListener(IResourceListener resourceListener) {
		this.resourceListeners.remove(resourceListener);
	}

	protected void fireResourceEvent(IResourceEvent event) {
		for (IResourceListener listener : resourceListeners) {
			try {
				listener.processEvent(event);
			} catch (Exception e) {
				logger.error("Listener failed!", e);
			}
		}
	}

	@Override
	public String getResourceType(String uri) {
		if (resources.containsKey(uri)) {
			return resources.get(uri).getType();
		}
		return null;
	}

	@Override
	public Collection<String> getAllRegisteredResources() {
		return new HashSet<String>(resourcesTree.keySet());
	}

	@Override
	public List<String> getResourceCapabilities(String uri) throws ResourceNotRegisteredException {
		if (!resourceCapabilities.containsKey(uri)) {
			Resource resource = resources.get(uri);
			List<String> capabilities = resourceDiscoveryAgent.getResourceCapabilities(resource);
			resourceCapabilities.put(uri, capabilities);
		}

		return resourceCapabilities.get(uri);
	}

	@Override
	public Resource getResourceForURI(String uri) {
		return resources.get(uri);
	}

	@Override
	public List<String> getResourcesOfType(String type) {
		return resourcesOfType.get(type);
	}

	@Override
	public void addResourceParameters(String uri, Map<String, Object> parameters) {
		Resource resource = resources.get(uri);
		for (Map.Entry<String, Object> entry : parameters.entrySet()) {
			resource.setProperty(entry.getKey(), entry.getValue());
		}
		fireResourcesPropertiesChangedEvent(resource);
	}

}
