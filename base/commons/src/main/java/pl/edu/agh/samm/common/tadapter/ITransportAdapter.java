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

import java.util.List;

import pl.edu.agh.samm.common.action.Action;
import pl.edu.agh.samm.common.core.Resource;

/**
 * Engine holding information about resources attached with specific transport
 * adapter.
 * 
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public interface ITransportAdapter {

	Object getCapabilityValue(Resource resource, String capabilityType) throws Exception;

	boolean hasCapability(Resource resource, String capabilityType) throws Exception;

	void addMeasurementListener(IMeasurementListener capabilityListener);

	void removeMeasurementListener(IMeasurementListener capabilityListener);

	void addTransportAdapterListener(IResourceDiscoveryListener listener);

	void removeTransportAdapterListener(IResourceDiscoveryListener listener);

	void registerResource(Resource resource) throws Exception;

	void unregisterResource(Resource resource);

	boolean isURISupported(Resource resource);

	void discoverChildren(Resource resource, List<String> types) throws Exception;

	boolean isActionSupported(String actionUri);

	boolean isResourceRegistered(Resource resource);

	/**
	 * Executes action. The execution is invoked in a separate thread!
	 * 
	 * @param actionToExecute
	 * @throws ActionNotSupportedException
	 */
	void executeAction(Action actionToExecute) throws ActionNotSupportedException;

	// Collection<? extends String> discoverResourceCapabilities(
	// Resource resource, List<String> capabilitiesTypes)
	// throws ResourceNotRegisteredException;

}
