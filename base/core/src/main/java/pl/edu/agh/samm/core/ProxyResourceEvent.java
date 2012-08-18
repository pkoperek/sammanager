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

import java.io.Serializable;

import pl.edu.agh.samm.api.core.IResourceEvent;
import pl.edu.agh.samm.api.metrics.ResourceEventType;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class ProxyResourceEvent implements IResourceEvent, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5744949427059278435L;
	private String attachment = null;
	private ResourceEventType type;

	public ProxyResourceEvent(IResourceEvent resourceEvent) {
		this.attachment = (resourceEvent.getAttachment() != null) ? resourceEvent.getAttachment().toString()
				: "";
		this.type = resourceEvent.getType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.edu.agh.samm.api.core.IResourceEvent#getAttachment()
	 */
	@Override
	public Object getAttachment() {
		return attachment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.edu.agh.samm.api.core.IResourceEvent#getType()
	 */
	@Override
	public ResourceEventType getType() {
		return type;
	}

}
