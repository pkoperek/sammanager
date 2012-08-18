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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.edu.agh.samm.api.tadapter.IMeasurementListener;
import pl.edu.agh.samm.api.tadapter.IResourceDiscoveryListener;
import pl.edu.agh.samm.api.tadapter.ITransportAdapter;

/**
 * Bean listening for the changes of {@link Set} containing references to
 * Registry OSGi services. Simply adds a specific listener for every new
 * Registry bound.
 * 
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class TransportAdapterListener {

	private List<ITransportAdapter> onRegistryBindCache = new ArrayList<ITransportAdapter>();
	private List<ITransportAdapter> onRegistryUnbindCache = new ArrayList<ITransportAdapter>();
	private IResourceDiscoveryListener registryListener = null;
	private IMeasurementListener measurementListener = null;

	/**
	 * @param measurementListener
	 *            the measurementListener to set
	 */
	public void setMeasurementListener(IMeasurementListener measurementListener) {
		this.measurementListener = measurementListener;
		tryToCleanCache();
	}

	public void setRegistryListener(IResourceDiscoveryListener registryListener) {
		this.registryListener = registryListener;
		tryToCleanCache();
	}

	private void tryToCleanCache() {
		if (registryListener != null) {
			for (ITransportAdapter registryFromCache : onRegistryBindCache) {
				if (registryListener != null) {
					registryFromCache.addTransportAdapterListener(registryListener);
				}
				if (measurementListener != null) {
					registryFromCache.addMeasurementListener(measurementListener);
				}
			}
			onRegistryBindCache.clear();

			for (ITransportAdapter registryFromCache : onRegistryUnbindCache) {
				if (registryListener != null) {
					registryFromCache.removeTransportAdapterListener(registryListener);
				}
				if (measurementListener != null) {
					registryFromCache.removeMeasurementListener(measurementListener);
				}
			}
			onRegistryUnbindCache.clear();
		}
	}

	public void onBind(ITransportAdapter adapter, Map properties) {
		onRegistryBindCache.add(adapter);
		tryToCleanCache();
	}

	public void onUnbind(ITransportAdapter adapter, Map properties) {
		onRegistryUnbindCache.add(adapter);
		tryToCleanCache();
	}

}
