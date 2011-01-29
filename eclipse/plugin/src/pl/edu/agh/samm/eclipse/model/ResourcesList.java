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
package pl.edu.agh.samm.eclipse.model;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import pl.edu.agh.samm.common.core.DefaultResourceEventImpl;
import pl.edu.agh.samm.common.core.IResourceEvent;
import pl.edu.agh.samm.common.core.IResourceListener;
import pl.edu.agh.samm.common.metrics.ResourceEventType;
import pl.edu.agh.samm.eclipse.SAMM;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class ResourcesList {
	private static ResourcesList _instance = null;
	private Collection<String> resources = Collections.emptyList();
	private List<IResourceListener> resourceListeners = new CopyOnWriteArrayList<IResourceListener>();

	static {
		_instance = new ResourcesList();
	}

	private ResourcesList() {
		// forbid manual instances creating
	}

	protected void fireResourcesChanged() {
		IResourceEvent resourceEvent = new DefaultResourceEventImpl(ResourceEventType.RESOURCES_CHANGED);
		for (IResourceListener resourceListener : resourceListeners) {
			try {
				resourceListener.processEvent(resourceEvent);
			} catch (Exception e) {
				SAMM.handleException(e);
			}
		}
	}

	public static ResourcesList getInstance() {
		return _instance;
	}

	public synchronized void setResourcesList(Collection<String> resources) {
		if (resources != null) {
			this.resources = resources;
		} else {
			this.resources = Collections.emptyList();
		}
		fireResourcesChanged();
	}

	public synchronized Collection<String> getResourcesList() {
		return resources;
	}

	public void addResourceListner(IResourceListener resourceListener) {
		this.resourceListeners.add(resourceListener);
	}

	public void removeResourceListener(IResourceListener resourceListener) {
		this.resourceListeners.remove(resourceListener);
	}
}
