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

package pl.edu.agh.samm.common.tadapter;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class CapabilityEvent implements ICapabilityEvent {

	private Object value;
	private String resourceInstance;
	private String resourceType;
	private String capabilityUri;
	private CapabilityEventType eventType;

	public CapabilityEvent(CapabilityEventType eventType, String capabilityUri, String resourceInstanceUri,
			String resourceTypeUri, Object value) {
		this.eventType = eventType;
		this.capabilityUri = capabilityUri;
		this.resourceType = resourceTypeUri;
		this.resourceInstance = resourceInstanceUri;
		this.value = value;
	}

	@Override
	public CapabilityEventType getCapabilityEventType() {
		return eventType;
	}

	@Override
	public String getCapabilityUri() {
		return capabilityUri;
	}

	@Override
	public String getInstanceUri() {
		return resourceInstance;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public String getResourceType() {
		return resourceType;
	}

}
