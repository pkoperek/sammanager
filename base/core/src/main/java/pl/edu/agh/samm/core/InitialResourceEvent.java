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

import pl.edu.agh.samm.common.core.IResourceEvent;
import pl.edu.agh.samm.common.metrics.ResourceEventType;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class InitialResourceEvent implements IResourceEvent, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4312235121141110742L;
	private String resourceURI = null;

	public InitialResourceEvent(String resourceURI) {
		this.resourceURI = resourceURI;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.edu.agh.samm.common.core.IResourceEvent#getType()
	 */
	@Override
	public ResourceEventType getType() {
		return ResourceEventType.RESOURCES_ADDED;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.edu.agh.samm.common.core.IResourceEvent#getAttachment()
	 */
	@Override
	public Object getAttachment() {
		return resourceURI;
	}

}
