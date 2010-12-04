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
package pl.edu.agh.samm.common.core;

import pl.edu.agh.samm.common.metrics.ResourceEventType;

/**
 * Default implementation of {@link IResourceEvent}
 * 
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class DefaultResourceEventImpl implements IResourceEvent {

	private static final long serialVersionUID = -198899591057125398L;
	private Object attachment;
	private ResourceEventType eventType;

	public DefaultResourceEventImpl(ResourceEventType eventType, Object attachment) {
		this.eventType = eventType;
		this.attachment = attachment;
	}

	public DefaultResourceEventImpl(ResourceEventType eventType) {
		this(eventType, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.edu.agh.samm.metrics.IResourceEvent#getAttachment()
	 */
	@Override
	public Object getAttachment() {
		return attachment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.edu.agh.samm.metrics.IResourceEvent#getType()
	 */
	@Override
	public ResourceEventType getType() {
		return eventType;
	}

	@Override
	public String toString() {
		return "DefaultResourceEventImpl type: " + eventType + " attachement: " + attachment;
	}
}
