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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public abstract class AbstractTransportAdapter implements ITransportAdapter {

	protected List<IResourceDiscoveryListener> registryListeners = new ArrayList<IResourceDiscoveryListener>();
	protected List<IMeasurementListener> capabilityListeners = new ArrayList<IMeasurementListener>();
	private static final Logger logger = LoggerFactory
			.getLogger(AbstractTransportAdapter.class);

	@Override
	public void addTransportAdapterListener(
			final IResourceDiscoveryListener listener) {
		if (!registryListeners.contains(listener)) {
			registryListeners.add(listener);
		}
	}

	@Override
	public void removeTransportAdapterListener(
			IResourceDiscoveryListener listener) {
		registryListeners.remove(listener);
	}

	@Override
	public void addMeasurementListener(IMeasurementListener capabilityListener) {
		if (!capabilityListeners.contains(capabilityListener)) {
			capabilityListeners.add(capabilityListener);
		}
	}

	@Override
	public void removeMeasurementListener(
			IMeasurementListener capabilityListener) {
		capabilityListeners.remove(capabilityListener);
	}

	protected void fireNewCapabilityValueEvent(String capabilityUri,
			String resourceInstanceUri, String resourceTypeUri, Object value) {
		if (value != null) {
			IMeasurementEvent event = new MeasurementEvent(capabilityUri,
					resourceInstanceUri, resourceTypeUri, value);
			fireEvent(event);
		}
	}

	protected void fireEvent(IMeasurementEvent event) {
		for (IMeasurementListener listener : capabilityListeners) {
			try {
				listener.processMeasurementEvent(event);
			} catch (Throwable t) {
				logger.error(
						"Listener thrown an exception during MeasurementEvent processing! Removing from listeners list!",
						t);
				capabilityListeners.remove(listener);
			}
		}
	}

	protected void fireEvent(IResourceDiscoveryEvent event) {
		for (IResourceDiscoveryListener listener : registryListeners) {
			try {
				listener.processEvent(event);
			} catch (Throwable t) {
				logger.error(
						"Listener thrown an exception during ResourceDiscoveryEvent processing!",
						t);
			}
		}
	}

	protected void fireNewResourcesEvent(String parentURI,
			Map<String, String> types,
			Map<String, Map<String, Object>> properties) {
		IResourceDiscoveryEvent event = new ResourceDiscoveryEvent(
				ResourceDiscoveryEventType.NEW_RESOURCES_DISCOVERED, types,
				properties, parentURI);
		fireEvent(event);
	}
}
