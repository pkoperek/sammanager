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
package pl.edu.agh.samm.api.tadapter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class ResourceDiscoveryEvent implements IResourceDiscoveryEvent {

	private ResourceDiscoveryEventType eventType;
	private String parentURI;
	private Map<String, Map<String, Object>> properties;
	private Map<String, String> types;

	public ResourceDiscoveryEvent(ResourceDiscoveryEventType eventType, Map<String, String> types,
			Map<String, Map<String, Object>> properties, String parentURI) {
		this.eventType = eventType;
		this.parentURI = parentURI;
		this.properties = properties;
		this.types = types;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pl.edu.agh.samm.api.tadapter.ITransportAdapterEvent#getEventType()
	 */
	@Override
	public ResourceDiscoveryEventType getEventType() {
		return eventType;
	}

	@Override
	public String getParentResourceURI() {
		return parentURI;
	}

	@Override
	public Map<String, Map<String, Object>> getResourcesProperties() {
		return properties;
	}

	@Override
	public Map<String, String> getResourcesTypes() {
		return types;
	}

	@Override
	public List<String> getResources() {
		return new LinkedList<String>(types.keySet());
	}

}
